package com.multipolar.uiteller.backend.service.iso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.multipolar.uiteller.backend.model.*;
import com.multipolar.uiteller.backend.repository.ReprintTimeoutResourceRepository;
import com.multipolar.uiteller.backend.repository.ResourceMessageRepository;
import com.multipolar.uiteller.backend.repository.TransactionLogRepository;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.multipolar.uiteller.backend.model.converter.ConverterRule;
import com.multipolar.uiteller.backend.model.converter.RequestRule;
import com.multipolar.uiteller.backend.model.converter.Rule;
import com.multipolar.uiteller.backend.repository.UniversitasLogRepository;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.service.KonversiMataUangKeTerbilang;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;

@Service
public class PendidikanConverterHandler extends DefaultConverterHandler {

	private static final String EDUCATION_PAYMENT_INQ = "edupayInquiry";
	private static final String EDUCATION_PAYMENT = "edupay";

	private static Logger logger = LoggerFactory.getLogger(PendidikanConverterHandler.class);
	private static final String DELIMITER = "|";
	private static final String DOUBLE_DELIMITER = "||";
	private static final String STRIP = "|-|";

	private static final SimpleDateFormat DATE = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");

	@Autowired
	private UniversitasLogRepository logRepository;

	@Autowired
	private TransactionLogRepository trxLogRepository;

	@Autowired
	private ReprintTimeoutResourceRepository reprintTimeoutResourceRepository;

	@Autowired
	private ResourceMessageRepository messageRepository;

	@Override
	public boolean supports(String s) {
		return StringUtils.equals(EDUCATION_PAYMENT_INQ, s) || StringUtils.equals(EDUCATION_PAYMENT, s);
	}

	protected String getValueForBit(Map<String, Object> map, int bit, ConverterRule[] rules)
			throws IllegalArgumentException {
		Map<String, String> values = new HashMap<>();

		StringBuilder message = new StringBuilder();
		for (ConverterRule rule : rules) {
			String other = rule.getOther();
			String key = rule.getKey();
			int length = rule.getLength();
			String leftpad = rule.getLeftpad();
			String rightpad = rule.getRightpad();

			String requestValue = null;
			if (other != null)
				requestValue = other;
			else if (key != null) {
				Object value = map.get(key);
				if (value == null) {
					logger.warn(
							"key value is null, please check schema validation or rule for this request. Value is set to empty string. Key is "
									+ key);
					requestValue = "";
				} else {
					requestValue = value.toString();
				}
			}

			values.put(key, requestValue);

			if (requestValue == null)
				throw new IllegalArgumentException("One of other or key need to be defined in the rule");

			if (length != 0) {
				if (leftpad != null && rightpad == null)
					message.append(StringUtils.leftPad(requestValue, length, leftpad));
				else if (rightpad != null && leftpad == null)
					message.append(StringUtils.rightPad(requestValue, length, rightpad));
				else if (leftpad == null && rightpad == null && requestValue.length() == length)
					message.append(requestValue);
				else {
					String exception = "Can't parse rule, please recheck the rule file for bit " + bit;
					logger.error(exception);
					throw new IllegalArgumentException(exception);
				}
			} else {
				if (leftpad != null || rightpad != null) {
					String exception = "Cant have leftpad or rightpad when length is zero";
					logger.error(exception);
					throw new IllegalArgumentException(exception);
				} else {
					message.append(requestValue);
				}
			}

			if (bit == 48 || bit == 61)
				message.append(DELIMITER);
		}

		return message.toString();
	}

	@Override
	protected Map<String, Object> translatePrivateMessage(ISOMsg msg, boolean fl) throws ISOException {
		String feature = FeatureContextHolder.getContext().getFeatureName();
		UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
		Map<String, Object> requestData = FeatureContextHolder.getContext().getReportData();
		String traceNumber = FeatureContextHolder.getContext().getTraceNumber();
		String archiveNumber = userDetail.getUsername() + traceNumber;
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Rule rule = getRule();

			String errorKey = rule.getErrorKey();
			if (exception(msg, map, errorKey))
				return map;

			RequestRule[] responseRules = rule.getResponse();
			for (RequestRule responseRule : responseRules) {
				int bit = responseRule.getBit();
				String valueFromBit = msg.getString(bit);
				if (valueFromBit == null) {
					logger.error("Value get from bit is null, bit = " + bit);
					throw new ISOException("The rule said get value from bit but empty bit is found. bit " + bit);
				}

				ConverterRule[] values = responseRule.getValue();
				if (bit == 48 || bit == 61) {
					setValueForMap(map, valueFromBit, values);
					if (StringUtils.equals(feature, EDUCATION_PAYMENT_INQ))
						this.setUniversitasLog(map, archiveNumber, bit);
				} else
					super.setValueForMap(map, valueFromBit, values);
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			return generalFail();
		} catch (JsonMappingException jme) {
			logger.error("Can't parsing rule file, please validate the json format");
			jme.printStackTrace();
			return generalFail();
		} catch (IOException iox) {
			logger.error(iox.getMessage());
			iox.printStackTrace();
			return generalFail();
		}

		// we need put source account number in map also
		String sourceName = FeatureContextHolder.getContext().getSourceName();
		map.put("sourceName", sourceName);
		map.put("userId", userDetail.getUsername());
		map.put("branchCode", userDetail.getBranchCode());
		map.put("branchName", userDetail.getBranchName());
		map.put("archiveNumber", archiveNumber);
		map.put("transactionDate", DATE.format(FeatureContextHolder.getContext().getFeatureTransactionDate()));
		String msg41 = msg.getString(41);
		map.put("terminalId", StringUtils.substring(msg41, msg41.length() - 5));

		map.put("payType", requestData.get("payType"));
		map.put("universityName", requestData.get("universityName"));
		String kodeBilling = requestData.get("kodeBilling") != null ? requestData.get("kodeBilling").toString() : "UNKNOWN";
 		map.put("kodeBilling", kodeBilling);
		
		Object chargeObject = map.get("adminCharge");
		String chargeString = chargeObject != null && !StringUtils.isEmpty(chargeObject.toString()) ? chargeObject.toString() : "0";
		BigDecimal charge = new BigDecimal(chargeString);
//		BigDecimal nominal = new BigDecimal(map.get("nominal").toString());
		BigDecimal nominal = new BigDecimal(0);
		String nominalString = map.get("nominal").toString();
		if(!nominalString.equals("")){
			nominal = new BigDecimal(nominalString);
		}
		BigDecimal total = charge.add(nominal);
		String terbilang = new KonversiMataUangKeTerbilang(total).toString();
		map.put("terbilang", terbilang);
		String qrString = kodeBilling + "+" + archiveNumber + "+" + total.toString();
		map.put("qrString", qrString);

		if (StringUtils.equals(feature, EDUCATION_PAYMENT)) {
			List<TransactionLog> transactionLogs = trxLogRepository.findByTraceNumberAndTrxType(traceNumber, EDUCATION_PAYMENT);
			String archiveNumberUniv = "";
			for(TransactionLog transactionLog : transactionLogs){
				if(transactionLog.getFieldName().equalsIgnoreCase("qrString")) {
					String[] qrStrings = transactionLog.getFieldValue().split("\\+");
					archiveNumberUniv = qrStrings[1];
				}
			}
			List<UniversitasLog> logs = logRepository.findByArchiveNumber(archiveNumberUniv);
			List<DataSetoran> loopbit61 = new ArrayList<>();
			List<DataSetoran> loopbit48 = new ArrayList<>();
			for (UniversitasLog log : logs) {
				int bit = log.getBit();
				DataSetoran data = new DataSetoran();
				data.setParam(log.getFieldName());
				data.setValue(log.getFieldValue1());
				data.setStatusTampil(log.getStatusTampil());
				UniversitasLog universitasLog = new UniversitasLog();
				universitasLog.setFieldName(data.getParam());
				universitasLog.setFieldValue1(data.getValue());
				universitasLog.setStatusTampil(data.getStatusTampil());
				universitasLog.setBit(bit);
				universitasLog.setArchiveNumber(archiveNumber);
				logRepository.save(universitasLog);
				if (bit == 48) {
					data.setAdditionalValue(log.getFieldValue2());
					loopbit48.add(data);
				} else {
					loopbit61.add(data);
				}
			}
			map.put("loopbit48", loopbit48);
			map.put("loopbit61", loopbit61);
		}

		Map<String, String> statusMap = new HashMap<String, String>();
		statusMap.put("status", "00");
		statusMap.put("code", "00");
		statusMap.put("message", "Request Success");
		map.put("status", statusMap);
		return map;
	}

	protected void setValueForMap(Map<String, Object> map, String bitValue, ConverterRule[] rules) {
		int index = 0;
		bitValue = StringUtils.replace(bitValue, DOUBLE_DELIMITER, STRIP);
		String[] data = StringUtils.split(bitValue, DELIMITER);
		for (ConverterRule rule : rules) {
			String key = rule.getKey();

			if (StringUtils.equals("loopbit61", key)) {
				// loop status tampil, param dan isi param
				List<DataSetoran> list = new ArrayList<>();
				while (index < data.length) {
					DataSetoran setoran = new DataSetoran();
					setoran.setStatusTampil(data[index++]);
					setoran.setParam(data[index++]);
					setoran.setValue(data[index++]);
					list.add(setoran);
				}

				map.put(key, list);
			} else if (StringUtils.equals("loopbit48", key)) {
				List<DataSetoran> list = new ArrayList<>();
				while (index < data.length) {
					DataSetoran setoran = new DataSetoran();
					setoran.setStatusTampil(data[index++]);
					setoran.setParam(data[index++]);
					setoran.setValue(data[index++]);
					setoran.setAdditionalValue(data[index++]);
					list.add(setoran);
				}

				map.put(key, list);
			} else {
				String leftpad = rule.getLeftpad();
				String rightpad = rule.getRightpad();

				String value = data[index];
				if (leftpad != null)
					value = StringUtils.stripStart(value, leftpad);
				else if (rightpad != null)
					value = StringUtils.stripEnd(value, rightpad);

				map.put(key, value);
				index++;
			}
		}

	}

	@SuppressWarnings("unchecked")
	private void setUniversitasLog(Map<String, Object> map, String archiveNumber, int bit) {
		String ref = map.get("ref").toString();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (StringUtils.equals("loopbit61", entry.getKey()) || StringUtils.equals("loopbit48", entry.getKey())) {
				List<DataSetoran> list = (List<DataSetoran>) entry.getValue();
				for (DataSetoran data : list) {
					UniversitasLog log = new UniversitasLog();
					log.setFieldName(data.getParam());
					log.setFieldValue1(data.getValue());
					log.setStatusTampil(data.getStatusTampil());
					log.setBit(bit);
					if (StringUtils.equals("loopbit48", entry.getKey()))
						log.setFieldValue2(data.getAdditionalValue());
					log.setArchiveNumber(ref);
					logRepository.save(log);
				}
			}
		}
	}

	protected boolean exception(ISOMsg msg, Map<String, Object> map, String errorKey) {
		String resultCode = msg.getString(39);
		String trxType = FeatureContextHolder.getContext().getFeatureName();
		ReprintTimeoutResource print = reprintTimeoutResourceRepository.findFirstByTrxTypeAndCode(trxType,resultCode);
		if (StringUtils.equals(resultCode, "00") || print != null)
			return false;

		ResourceMessage message = messageRepository.findByCodeAndMessage(errorKey, resultCode);
		String messageDesc = message == null ? "Unmapping Error Number (" + resultCode + ")" : message.getValue();
		Map<String, String> statusMap = new HashMap<>();
		statusMap.put("status", "01");
		statusMap.put("code", resultCode);
		statusMap.put("message", messageDesc);
		map.put("status", statusMap);

		return true;
	}
}
