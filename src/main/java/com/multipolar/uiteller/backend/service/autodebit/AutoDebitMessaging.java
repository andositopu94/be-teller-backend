package com.multipolar.uiteller.backend.service.autodebit;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.multipolar.uiteller.backend.model.Autodebit;
import com.multipolar.uiteller.backend.model.AutodebitLog;
import com.multipolar.uiteller.backend.model.CommonTransactionLog;
import com.multipolar.uiteller.backend.model.ResourceMessage;
import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.repository.AutodebitLogRepository;
import com.multipolar.uiteller.backend.repository.AutodebitRepository;
import com.multipolar.uiteller.backend.repository.ResourceMessageRepository;
import com.multipolar.uiteller.backend.resource.BusinessException;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.service.TraceNumberGenerator;
import com.multipolar.uiteller.backend.service.iso.MessageConverterFactory;
import com.multipolar.uiteller.backend.service.iso.MessageConverterHandler;
import com.multipolar.uiteller.backend.service.iso.MessageGateway;
import com.multipolar.uiteller.backend.service.logger.TransactionLogService;
import com.multipolar.uiteller.backend.service.logger.TransactionLogger;
import com.multipolar.uiteller.backend.service.logger.TransactionLoggerFactory;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;

@Service
public class AutoDebitMessaging {
	private Logger logScheduler = LogManager.getLogger("scheduler-log");

	@Autowired
	private MessageConverterFactory converterFactory;

	@Autowired
	private AutodebitProcessorFactory processorFactory;

	@Autowired
	private MessageGateway gateway;

	@Autowired
	private ResourceMessageRepository messageRepository;

	// @Autowired
	// private AdminChargeRepository chargeRepository;

	@Autowired
	private AutodebitRepository autodebitRepository;

	@Autowired
	private TraceNumberGenerator traceNumberGenerator;

	@Autowired
	private TransactionLogService logService;

	@Autowired
	private TransactionLoggerFactory loggerFactory;

	@Autowired
	private AutodebitLogRepository autodebitLogRepository;

	@Value("${app.autodebit.enable:false}")
	private boolean enableAutodebit;

	private static final String AUTODEBIT_BIT_18 = "6013";
	private static final String AUTODEBIT_USER = "systm";

	protected ISOMsg deserialize(Map<String, Object> map, String feature)
			throws IOException, JsonProcessingException, ISOException {
		MessageConverterHandler converter = converterFactory.get(feature);
		ISOMsg msg = converter.doConvertToISO(map);

		// khusus autodebit
		// Bit 18 (merchant type) diset menjadi 6013 (merchant type untuk
		// autodebit)
		msg.set(18, AUTODEBIT_BIT_18);

		return msg;
	}

	protected String getMessageIfError(ISOMsg response, String errorKey, AutodebitLog log) throws BusinessException {
		if (response == null)
			throw new BusinessException("Host give no response");

		if (errorKey == null)
			throw new BusinessException(
					"Specification violation, errorKey/errorInquiryKey need to be defined for autodebit map creation");

		if (!StringUtils.equals(response.getString("39"), "00")) {
			String errorCode = response.getString("39");
			ResourceMessage message = messageRepository.findByCodeAndMessage(errorKey, errorCode);
			String messageDesc = message == null ? "Unmapping Error Number (" + errorCode + ")" : message.getValue();
			log.setRc(response.getString(39));

			return messageDesc;
		}

		log.setRc("00");
		return null;
	}

	protected void logTransaction(ISOMsg request, ISOMsg response, String transaction) throws IOException {
		CommonTransactionLog commonLog = logService.logTransaction(request, response);

		// add feature if missing from map
		Map<String, Object> map = FeatureContextHolder.getContext().getReportData();
		TransactionLogger transactionLogger = loggerFactory.get(transaction);
		List<TransactionLog> logs = transactionLogger.addLog(map);

		String requestBit28 = request.getString("28");
		String responseBit28 = response != null ? response.getString("28") : "0";
		if (StringUtils.equals("000000000", requestBit28)) {
			String adminCharge = responseBit28 != null ? responseBit28 : "0";
			String stripAdmin = StringUtils.stripStart(adminCharge, "0");
			TransactionLog chargeLog = new TransactionLog("adminCharge", StringUtils.isEmpty(stripAdmin) ? "0" : stripAdmin);
			logs.add(chargeLog);
		} else {
			TransactionLog chargeLog = new TransactionLog("adminCharge", "0");
			logs.add(chargeLog);
		}

		logService.saveAdditionalLog(logs, commonLog, request.getString(11), transaction);
	}

	private void addFillers(Autodebit data, Map<String, Object> map) {
		if (!StringUtils.isEmpty(data.getFillerText1())) {
			map.put(data.getFillerText1(), data.getFillerValue1());
		}
		if (!StringUtils.isEmpty(data.getFillerText2())) {
			map.put(data.getFillerText2(), data.getFillerValue2());
		}
		if (!StringUtils.isEmpty(data.getFillerText3())) {
			map.put(data.getFillerText3(), data.getFillerValue3());
		}
	}

	public synchronized void process(Autodebit data, AutodebitLog log)
			throws IOException, JsonProcessingException, ISOException, BusinessException {

		log.setAutodebit(data);
		log.setProcessStart(new Date());

		// set security context
		// Bit 41 diset menjadi 3 digit cabang + auto (misal: 117auto) . Kode
		// cabang berdasarkan user teller yang login.
		String user = AUTODEBIT_USER;
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null);

		UserDetailImpl userDetail = new UserDetailImpl();
		userDetail.setBranchName(data.getBranchCode());
		userDetail.setBranchCode(data.getBranchCode());
		userDetail.setUsername(AUTODEBIT_USER);
		auth.setDetails(userDetail);

		SecurityContextHolder.getContext().setAuthentication(auth);

		String feature = data.getTransactionName();
		if (StringUtils.equals(data.getFillerText1(), "aliasTransactionName")) {
			feature = data.getFillerValue1();
		}
		// set feature context
		String featureInquiry = feature + "Inquiry";
		FeatureContextHolder.getContext().setFeatureName(featureInquiry);
		FeatureContextHolder.getContext().setInstansiParameter(data.getFillerValue2());

		AutoDebitAware processor = processorFactory.getAutoDebitProcessor(feature);

		// ambil data map dari database
		Map<String, Object> map = processor.construcRequestMap(data);

		// add fillers if any
		addFillers(data, map);

		FeatureContextHolder.getContext().setReportData(map);

		// ubah ke iso message request
		ISOMsg request = deserialize(map, featureInquiry);

		String ref = StringUtils.leftPad(String.valueOf(traceNumberGenerator.getReferenceNumber()), 12, "0");
		map.put("archiveNumber", ref);

		// set ref
		request.set(37, ref);
		log.setRef(ref);
		log.setInquiryTraceNumber(request.getString(11));

		// set routing bit 63
		if(feature.equals("pdamDti")){
			request.set(63, processor.getMessageRouting(data.getTransactionName()));
		}else{
			request.set(63, processor.getMessageRouting());
		}

		request.set(57, "OVERBOOKING");

		// kirim ke host
		ISOMsg response = gateway.sendToHost(request);

		String error = getMessageIfError(response, (String) map.get("errorInquiryKey"), log);
		if (error != null) {
			log.setStatus(1);
			throw new BusinessException(error);
		}

		// beberapa informasi hanya terdapat dr hasil inquiry dan tidak
		// terdapat di payment
		// set disini
		Map<String, Object> reportData = FeatureContextHolder.getContext().getReportData();
		processor.addReportDataFromInquiry(reportData, response);

		ISOMsg paymentRequest = processor.createPaymentRequest(response, data);
		paymentRequest.set(11, traceNumberGenerator.getTraceNumber());
		paymentRequest.set(57, "OVERBOOKING");
		log.setPaymentTraceNumber(paymentRequest.getString(11));

		// kirim ke host
		ISOMsg paymentResponse = gateway.sendToHost(paymentRequest);

		// cek response
		String errorPayment = getMessageIfError(paymentResponse, (String) map.get("errorKey"), log);
		if (paymentResponse == null) {
			log.setStatus(2);
			throw new BusinessException(errorPayment);
		}

		// insert log ke db (status dan transaksi)
		// we need to reset feature name in feature context
		FeatureContextHolder.getContext().setFeatureName(feature);
		
		// set payment trace to save as archiveNumber
		FeatureContextHolder.getContext().setTraceNumber(paymentRequest.getString(11));
		logTransaction(paymentRequest, paymentResponse, feature);

		if (errorPayment != null)
			throw new BusinessException(errorPayment);
	}

	private List<Autodebit> 	autodebitService(List<Autodebit> data) {
		List<Autodebit> successData = new ArrayList<>();
		for (Autodebit datum : data) {
			AutodebitLog log = new AutodebitLog();
			try {
				logScheduler.info("========================================================================");
				String message = String.format(
						"\tMulai memproses data dengan id: %d, tipe transaksi: %s, custumerId %s, nomor rekening %s",
						datum.getId(), datum.getTransactionName(), datum.getCustomerId(), datum.getSourceNumber());
				logScheduler.info(message);
				process(datum, log);
				logScheduler.info(
						"\tTransaksi sukses dengan trace number untuk pembayaran: " + log.getPaymentTraceNumber());
				log.setStatus(0);
				log.setMessage("SUCCESS");
				successData.add(datum);
			} catch (BusinessException e) {
				e.printStackTrace();
				log.setStatus(1);
				log.setMessage(e.getMessage());
				logScheduler.info("\tTransaksi gagal dengan pesan: " + e.getMessage());
			} catch (IOException | ISOException e) {
				log.setStatus(4);
				log.setMessage(e.getMessage());
				logScheduler.info("\tTransaksi gagal dengan pesan: " + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				logScheduler.info("\tTransaksi gagal dengan pesan: " + e.getMessage());
				log.setStatus(5);
				log.setMessage(e.getMessage());
				e.printStackTrace();
			} finally {
				log.setProcessEnd(new Date());
				autodebitLogRepository.save(log);
				logScheduler.info("\tProcess selesai dan log disimpan dengan id: " + log.getId());
			}
		}
		return successData;
	}

	@Scheduled(cron = "${app.autodebit.cron}")
	public void autodebitScheduler() {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
		Date currentDate = new Date();
		System.out.println("Masuk Scheduler");
		logScheduler.info("Scheduler mulai pada : " + format.format(currentDate));
		if(enableAutodebit) {
			List<Autodebit> data = autodebitRepository.fetchAutodebitData();
			logScheduler.info("Banyaknya data yang akan diproses oleh scheduler: " + data.size());

			do {
				data = autodebitService(data);
				logScheduler.info("data success pembayaran: " + data.size() + " data");
			} while (data.size() > 0);

			// logScheduler.info("Proses re-check untuk data pdam/pln/bpjs");
			// processPdamNon88();
		}else{
			logScheduler.info("autodebit disable");
		}

		logScheduler.info("Scheduler selesai pada: " + format.format(new Date()));
	}
}
