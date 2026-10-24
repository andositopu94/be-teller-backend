package com.multipolar.uiteller.backend.service.autoreinquiry;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipolar.uiteller.backend.model.CommonTransactionLog;
import com.multipolar.uiteller.backend.model.ResourceMessage;
import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.model.converter.Rule;
import com.multipolar.uiteller.backend.repository.ResourceMessageRepository;
import com.multipolar.uiteller.backend.repository.TransactionLogRepository;
import com.multipolar.uiteller.backend.resource.BusinessException;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.service.iso.MessageConverterFactory;
import com.multipolar.uiteller.backend.service.iso.MessageConverterHandler;
import com.multipolar.uiteller.backend.service.iso.MessageGateway;
import com.multipolar.uiteller.backend.service.logger.TransactionLogService;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;

@Service
public class AutoreinquiryMessaging {
	 private static final String RULE_EXTENSION = ".rule.json";
	private Logger logScheduler = LogManager.getLogger("scheduler-log");
		
    @Autowired
    private TransactionLogRepository logRepository;
    
    @Autowired
    private TransactionLogService logService;
    
	@Autowired
	private ResourceMessageRepository messageRepository;
	
	@Autowired
	private MessageConverterFactory converterFactory;
    
	@Autowired
	private MessageGateway gateway;
    
	private String feature;
	
	private String errorKey;
	
	private ISOMsg msg;
	 
	protected ISOMsg deserialize(Map<String, Object> map, String feature)
			throws IOException, JsonProcessingException, ISOException {
		System.out.println("Deserializing");
		System.out.println("Map is:");
		System.out.println(map);
		System.out.println("Feature is");
		System.out.println(feature);
		MessageConverterHandler converter = converterFactory.get(feature);
		msg = converter.doConvertToISO(map);
		return msg;
	}
    
	protected String getMessageIfError(ISOMsg response, String errorKey, CommonTransactionLog log) throws BusinessException {
		if (response == null)
			throw new BusinessException("Host give no response");

		if (errorKey == null)
			throw new BusinessException(
					"Specification violation, errorKey/errorInquiryKey need to be defined for autodebit map creation");

		if (!StringUtils.equals(response.getString("39"), "00")) {
			String errorCode = response.getString("39");
			ResourceMessage message = messageRepository.findByCodeAndMessage(errorKey, errorCode);
			String messageDesc = message == null ? "Unmapping Error Number (" + errorCode + ")" : message.getValue();

			return messageDesc;
		}

		log.setRc("00");
		return null;
	}
	
	
	protected Rule getRule() throws IOException {
        // default implementasion is read the rule file base on feature -->
        // feature.rule.json
        String filename = feature + RULE_EXTENSION;

        System.out.println("rule name is: " + filename);

        ObjectMapper objectMapper = new ObjectMapper();

        Resource resource = new ClassPathResource("rules/" + filename);

        // convert json string to object
        return objectMapper.readValue(resource.getInputStream(), Rule.class);
    }
	
	@Scheduled(cron = "${autoreinquiry.scheduler.cron}")
	public void autoreinquiryScheduler() throws IOException, ISOException, BusinessException, ParseException {
	
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
		SimpleDateFormat settlementDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date currentDate = new Date();
		logScheduler.info("Scheduler ntpn autoreinquiry mulai pada : " + format.format(currentDate));

	     List<Map<String, Object>> results = new ArrayList<>();
	     
	     int failedCount = 0;
	     int successCount = 0;


	     	String settlementDate = settlementDateFormat.format(currentDate); 
	     	
	        List<TransactionLog> logs = logRepository.findTrxWithoutNtpnAndUser(settlementDate);
	        for (TransactionLog log : logs) {
	            Map<String, Object> map = new HashMap<>();

	            CommonTransactionLog commonLog = log.getCommonTransaction();
	            Long commonId = commonLog.getId();
	            List<TransactionLog> trxLogs = logRepository.findByCommonTransactionId(commonId);
	            String mpnType = log.getTrxType();
	            System.out.println("Transaction type is: "+mpnType);

	            map.put("branchCode", commonLog.getBranchCode());
	            map.put("transactionDate", format.format(commonLog.getTrxDate()));
	            map.put("status", commonLog.getStatus() == 0 ? "Berhasil" : "Gagal");
	            map.put("tellerId", commonLog.getUserid());
	            map.put("commonId", String.valueOf(commonId));

	            for (TransactionLog tl : trxLogs) {
	                map.put(tl.getFieldName(), tl.getFieldValue());
	            }

	            map.put("traceNumber", log.getTraceNumber());
	            map.put("stan", log.getTraceNumber());
	            
	    		String mpnUser = commonLog.getUserid();
	    		String branchName = String.valueOf(map.get("branchName"));
	    		String billingCode = String.valueOf(map.get("billingCode"));
	    		String stan = String.valueOf(map.get("stan"));
	    		String trxDate = String.valueOf(map.get("transactionDate"));
	    		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(mpnUser, null);
	    		System.out.println("User is : "+mpnUser);
	    		System.out.println("Transaction date is: "+trxDate);
	    		
	    		UserDetailImpl userDetail = new UserDetailImpl();
	    		userDetail.setBranchName(branchName);
	    		userDetail.setBranchCode(commonLog.getBranchCode());
	    		userDetail.setUsername(mpnUser);
	    		auth.setDetails(userDetail);
	    		
	    		results.add(map);

	    		SecurityContextHolder.getContext().setAuthentication(auth);
	            
            
	            if (mpnType.equals("djp")){
	            	feature = "djpReinquiry";
	            	errorKey = "reinquiryDjp";
	            }
	            else if (mpnType.equals("djbc")){
	            	feature = "djbcReinquiry";
	            	errorKey = "reinquiryDjbc";

	            }
	            else if (mpnType.equals("dja")){
	            	feature = "djaReinquiry";
	            	errorKey = "reinquiryDja";

	            }
            	FeatureContextHolder.getContext().setFeatureName(feature);
        	    logScheduler.info("Reinquiry / proses transaksi abnormal otomatis dilakukan untuk kode billing: " + billingCode + " dan STAN: " + stan);
            	ISOMsg request = deserialize(map, feature);
            	ISOMsg response = gateway.sendToHost(request);
                boolean gotResponse = true;
                if (response == null){
                    gotResponse = false;
                }
            	System.out.println("Response: ");
            	System.out.println(response);
        		String error = getMessageIfError(response, (String) errorKey, commonLog);
        		System.out.println("Error is: ");
        		System.out.println(error);
        		if (error != null) {
        			commonLog.setStatus(1);
            	    logScheduler.info("Reinquiry / proses transaksi abnormal otomatis gagal dengan pesan error: " + error);
            	    failedCount=failedCount+1;
        		}
        		else if (error == null) {
        			logService.updateSuccessMpnReinquiry(msg, response);
            	    logScheduler.info("Reinquiry / proses transaksi abnormal otomatis berhasil untuk kode billing: " + billingCode + " dan STAN: " + stan);
            	    successCount=successCount+1;
        		}
	        }
		
		//List<Map<String, String>> data = mpnReinquiryController.getTransaction(currentDate.toString(), "djp");
		//logScheduler.info("Banyaknya data yang akan diproses oleh scheduler: " + data.size());

		//while (data.size() > 0) {
			//data = autodebitService(data);
			//logScheduler.info("data success pembayaran: " + data.size() + " data");
		//}

		// logScheduler.info("Proses re-check untuk data pdam/pln/bpjs");
		// processPdamNon88();
	    logScheduler.info("Jumlah data yang dilakukan reinquiry / proses tranasksi abnormal automatis: " + results.size());
	    logScheduler.info("Jumlah reinquiry / proses tranasksi abnormal automatis yang berhasil: " + successCount);
	    logScheduler.info("Jumlah reinquiry / proses tranasksi abnormal automatis yang gagal: " + failedCount);
		logScheduler.info("Scheduler autoreinquiry selesai pada: " + format.format(new Date()));
	}

}
