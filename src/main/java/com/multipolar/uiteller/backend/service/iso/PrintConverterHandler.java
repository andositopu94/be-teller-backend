	package com.multipolar.uiteller.backend.service.iso;

	import com.fasterxml.jackson.databind.JsonMappingException;
	import com.fasterxml.jackson.databind.ObjectMapper;
	import com.multipolar.uiteller.backend.model.converter.ConverterRule;
	import com.multipolar.uiteller.backend.model.converter.RequestRule;
	import com.multipolar.uiteller.backend.model.converter.Rule;
	import com.multipolar.uiteller.backend.model.converter.SiskoRule;
	import com.multipolar.uiteller.backend.resource.Constants;
	import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
	import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
	import org.apache.commons.lang3.StringUtils;
	import org.jpos.iso.ISOException;
	import org.jpos.iso.ISOMsg;
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.core.io.ClassPathResource;
	import org.springframework.core.io.Resource;
	import org.springframework.security.core.context.SecurityContextHolder;
	import org.springframework.stereotype.Service;

	import java.io.FileNotFoundException;
	import java.io.IOException;
	import java.text.SimpleDateFormat;
	import java.util.Calendar;
	import java.util.Date;
	import java.util.HashMap;
	import java.util.Map;

	@Service
	public class PrintConverterHandler extends SiskoConverterHandler {

		private static final String RULE_EXTENSION = ".rule.json";
		private static final SimpleDateFormat LOCAL_DATE = new SimpleDateFormat("yyMMdd");
		private static final SimpleDateFormat DATE_TIME = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
		private static final SimpleDateFormat LOCAL_TIME = new SimpleDateFormat("kk:mm:ss");
		private static final SimpleDateFormat SDATE = new SimpleDateFormat("dd/MM/yyyy");
		private static final SimpleDateFormat MONTH = new SimpleDateFormat("MM");
		private static final SimpleDateFormat DATE = new SimpleDateFormat("dd");
		private static Logger logger = LoggerFactory.getLogger(PrintConverterHandler.class);
		private static final String SUPPORTED_URL_INITIAL_PRINT = "bpihinitialPrint";
		private static final String SUPPORTED_URL_Payment_PRINT = "bpihPaymentPrint";
		private static final String defaultMTI = "0200";
		private static final String defaultAcquirer = "117";
		private static final Map<String, String> MAP = new HashMap<String, String>(){
			{ put("01", "1"); put("02", "2"); put("03", "3"); put("04", "4"); put("05", "5"); put("06", "6"); put("07", "7"); put("08", "8"); put("09", "9"); put("10", "A"); put("11", "B"); put("12", "C"); }
		};

		@Override
		public boolean supports(String s) {
			return StringUtils.equals(SUPPORTED_URL_INITIAL_PRINT, s) || StringUtils.equals(SUPPORTED_URL_Payment_PRINT, s);
		}

		@Override
		public ISOMsg doConvertToISO(Map<String, Object> map) throws InvalidMessageException {
			Date date = new Date();
			FeatureContextHolder.getContext().setFeatureTransactionDate(date);
			try {
				ISOMsg msg = new ISOMsg();
				setMti(msg, defaultMTI);
				setTransmissionDateTime(msg, date);
				setTransmissionTime(msg, date);
				setTransmissionDate(msg, date);
				setMerchantType(msg, Constants.MERCHANT_TYPE);
				setAcquirer(msg, defaultAcquirer);
				setTraceAuditNumber(msg);
				setTerminal(msg);
				setPrivateMessage(msg, map);
				return msg;
			} catch (ISOException ex) {
				throw new InvalidMessageException(ex.getMessage());
			}
		}

		protected SiskoRule getSiskoRule() throws IOException {
			String feature = FeatureContextHolder.getContext().getFeatureName();
			String filename = feature + RULE_EXTENSION;

			ObjectMapper objectMapper = new ObjectMapper();

			Resource resource = new ClassPathResource("rules/" + filename);

			return objectMapper.readValue(resource.getInputStream(), SiskoRule.class);
		}

		public Date date(){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.set(Calendar.HOUR_OF_DAY, Constants.MPN_SETTLEMENT_DATE);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime();
		}

		@Override
		protected void setTransmissionDate(ISOMsg msg, Date date) throws ISOException {
			msg.set(13, LOCAL_DATE.format(date));
		}

		@Override
		protected Map<String, Object> translatePrivateMessage(ISOMsg msg, boolean fl) throws ISOException {
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
					setValueForMap(map, valueFromBit, values);
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

			Map<String, Object> requestData = FeatureContextHolder.getContext().getReportData();
			UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
			map.put("sourceName", requestData.get("sourceName"));
			map.put("userId", userDetail.getUsername());
			map.put("branchCode", userDetail.getBranchCode());
			map.put("archiveNumber", userDetail.getUsername() + FeatureContextHolder.getContext().getTraceNumber());
			map.put("transactionDate", DATE_TIME.format(FeatureContextHolder.getContext().getFeatureTransactionDate()));
			map.put("transactionTime", LOCAL_TIME.format(FeatureContextHolder.getContext().getFeatureTransactionDate()));
			map.put("uploadFlag", FeatureContextHolder.getContext().getBpihReportUploadFlag());
			Map<String, String> statusMap = new HashMap<String, String>();
			statusMap.put("status", "00");
			statusMap.put("code", "00");
			statusMap.put("message", "Request Success");
			map.put("status", statusMap);

			return map;
		}

		@Override
		protected void setPrivateMessage(ISOMsg msg, Map<String, Object> map) throws ISOException {
			try {
				SiskoRule siskoRule = getSiskoRule();
				RequestRule[] requestRules = siskoRule.getRequest();
				for (RequestRule requestRule : requestRules) {
					int bit = requestRule.getBit();
					ConverterRule[] values = requestRule.getValue();
					String valueForBit = getValueForBit(map, bit, values);
					msg.set(bit, valueForBit);
				}
				msg.set(3, siskoRule.getPcode());
				msg.set(37, defaultAcquirer+MAP.get(MONTH.format(FeatureContextHolder.getContext().getFeatureTransactionDate()))+DATE.format(FeatureContextHolder.getContext().getFeatureTransactionDate())+FeatureContextHolder.getContext().getTraceNumber());
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			} catch (JsonMappingException jme) {
				logger.error("Can't parsing rule file, please validate the json format");
				jme.printStackTrace();
			} catch (IOException iox) {
				logger.error(iox.getMessage());
				iox.printStackTrace();
			}
		}

	}
