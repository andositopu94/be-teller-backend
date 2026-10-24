package com.multipolar.uiteller.backend.service.iso;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.multipolar.uiteller.backend.model.MpnCutOff;
import com.multipolar.uiteller.backend.model.ResourceMessage;
import com.multipolar.uiteller.backend.model.converter.ConverterRule;
import com.multipolar.uiteller.backend.model.converter.RequestRule;
import com.multipolar.uiteller.backend.model.converter.Rule;
import com.multipolar.uiteller.backend.repository.ResourceMessageRepository;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.service.MpnSettlementDateUtil;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalTime;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class MpnConverterHandler extends DefaultConverterHandler {

	private static final SimpleDateFormat LOCAL_DATE = new SimpleDateFormat("MMdd");
	private static final SimpleDateFormat DATE = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
	private static final SimpleDateFormat TIME = new SimpleDateFormat("kk:mm:ss");
	private static final SimpleDateFormat SDATE = new SimpleDateFormat("dd/MM/yyyy");
	private static Logger logger = LoggerFactory.getLogger(MpnConverterHandler.class);
	private static final String SUPPORTED_URL_DJP = "djpInquiry";
	private static final String SUPPORTED_URL_DJPG3 = "djpg3Inquiry";
	private static final String SUPPORTED_URL_DJBC = "djbcInquiry";
	private static final String SUPPORTED_URL_DJBCG3 = "djbcg3Inquiry";
	private static final String SUPPORTED_URL_DJA = "djaInquiry";
	private static final String SUPPORTED_URL_DJAG3 = "djag3Inquiry";
	private static final String SUPPORTED_URL_DJP_PAY = "djp";
	private static final String SUPPORTED_URL_DJPG3_PAY = "djpg3";
	private static final String SUPPORTED_URL_DJBC_PAY = "djbc";
	private static final String SUPPORTED_URL_DJBCG3_PAY = "djbcg3";
	private static final String SUPPORTED_URL_DJA_PAY = "dja";
	private static final String SUPPORTED_URL_DJAG3_PAY = "djag3";
	private static final SimpleDateFormat TIME_SPACE = new SimpleDateFormat("kkmmss");

	@Autowired
	private ResourceMessageRepository messageRepository;
	@Autowired
	private CustomerIdService customerIdService;

	@Autowired
	private MpnSettlementDateUtil mpnUtil;

	@Override
	public boolean supports(String s) {
		return StringUtils.equals(SUPPORTED_URL_DJP, s) || StringUtils.equals(SUPPORTED_URL_DJPG3, s)
				|| StringUtils.equals(SUPPORTED_URL_DJBC, s) || StringUtils.equals(SUPPORTED_URL_DJBCG3, s)
				|| StringUtils.equals(SUPPORTED_URL_DJA, s) || StringUtils.equals(SUPPORTED_URL_DJAG3, s)
				|| StringUtils.equals(SUPPORTED_URL_DJP_PAY, s) || StringUtils.equals(SUPPORTED_URL_DJPG3_PAY, s)
				|| StringUtils.equals(SUPPORTED_URL_DJBC_PAY, s) || StringUtils.equals(SUPPORTED_URL_DJBCG3_PAY, s)
				|| StringUtils.equals(SUPPORTED_URL_DJA_PAY, s) || StringUtils.equals(SUPPORTED_URL_DJAG3_PAY, s);
	}

	@Override
	public ISOMsg doConvertToISO(Map<String, Object> map) throws InvalidMessageException {
		Date date = new Date();
		FeatureContextHolder.getContext().setFeatureTransactionDate(date);
		try {
			ISOMsg msg = new ISOMsg();
			String defaultMTI = "0200";
			setMti(msg, defaultMTI);
			String defaultBIN = "627486";
			setPan(msg, defaultBIN);
			setTransmissionDateTime(msg, date);
			setTransmissionTime(msg, date);
			setTransmissionDate(msg, date);
			setSettlementDate(msg, date);
			if(map.get("payType").equals("CASH")) {
				setMerchantType(msg, Constants.MERCHANT_TYPE);
			} else {
				setMerchantType(msg, Constants.OVERBOOKING_MERCHANT_TYPE);
			}
			String defaultAcquirer = "117";
			setAcquirer(msg, defaultAcquirer);
			//setRefferenceNumber(msg, getStan());
			setTraceAuditNumber(msg);
			setTerminal(msg);
			setTerminalLocation(msg);
			setPrivateMessage(msg, map);
			setCheckNumber(msg);

			return msg;
		} catch (ISOException ex) {
			throw new InvalidMessageException(ex.getMessage());
		}
	}

	@Override
	protected void setSettlementDate(ISOMsg msg, Date date) throws ISOException {
		// cek trx hari biasa, hari libur atau tanggal cut off
		MpnCutOff cutOff = mpnUtil.isOnCutOffPeriod(date);
		
		if (cutOff != null) {
			FeatureContextHolder.getContext().setSettlementDate(cutOff.getServeDate());
			msg.set(15, LOCAL_DATE.format(cutOff.getServeDate()));
			return;
		}
		
		boolean isTrxOnWeekend = mpnUtil.isWeekend(date);
		boolean isTrxOnHoliday = mpnUtil.isHoliday(date);
		
		// jika transaction di hari libur atau hari kerja
		if (isTrxOnHoliday || isTrxOnWeekend) {
			Date nextWorkingDay = mpnUtil.getNextWorkingDay(date);
			FeatureContextHolder.getContext().setSettlementDate(nextWorkingDay);
			msg.set(15, LOCAL_DATE.format(nextWorkingDay));
			return;
		}
		
		// jika hari biasa, cek sudah lewat jam cut off
		boolean afterCutHour = mpnUtil.afterCutOffHour(date);
		Date settlementDate = null;
		if (!afterCutHour) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			settlementDate = calendar.getTime();
		} else {
			settlementDate = mpnUtil.getNextWorkingDay(date);
		}
		
		FeatureContextHolder.getContext().setSettlementDate(settlementDate);
		msg.set(15, LOCAL_DATE.format(settlementDate));
	}

	public Date date() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, Constants.MPN_SETTLEMENT_DATE);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
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

		String sourceName = FeatureContextHolder.getContext().getSourceName();
		UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication()
				.getDetails();
		if(fl){
			map.put("sourceName", sourceName);
			map.put("userId", userDetail.getUsername());
			map.put("branchCode", userDetail.getBranchCode());
			map.put("branchName", userDetail.getBranchName());
			map.put("archiveNumber", userDetail.getUsername() + FeatureContextHolder.getContext().getTraceNumber());
			String transactionDateReq = DATE.format(FeatureContextHolder.getContext().getFeatureTransactionDate());
			String transactionTime = map.get("transactionTime").toString();
			LocalTime localTime = DateTimeFormat.forPattern("HHmmss").parseLocalTime(transactionTime);
			map.put("transactionDate", transactionDateReq.substring(0,11)+localTime.toString().substring(0,8));
			String settlementDate = mpnUtil.getSettlementDateFromResponse(msg);
			if (StringUtils.isNotEmpty(settlementDate))
				map.put("settlementDate", settlementDate);
			else
				map.put("settlementDate", SDATE.format(FeatureContextHolder.getContext().getSettlementDate()));
		}
		else{
			map.put("cardIdAtm", msg.getString(2));
			map.put("transactionDateAtm", msg.getString(13));
			try {
				map.put("transactionTimeAtm", TIME.format(TIME_SPACE.parse(msg.getString(12))));
			} catch (Exception e) {
				e.printStackTrace();
			}
			map.put("traceNumberAtm", msg.getString(37));
			map.put("atmCodeAtm", msg.getString(41));
			map.put("atmNameAtm", msg.getString(43));
			map.put("customerIdAtm", map.get(customerIdService.customerId(FeatureContextHolder.getContext().getFeatureName())));
		}

		if (!map.containsKey("status")) {
			Map<String, String> statusMap = new HashMap<String, String>();
			statusMap.put("status", "00");
			statusMap.put("code", "00");
			statusMap.put("message", "Request Success");
			map.put("status", statusMap);
		} 

		return map;
	}

	protected boolean exception(ISOMsg msg, Map<String, Object> map, String errorKey) {
		String resultCode = msg.getString(39);
		if (StringUtils.equals(resultCode, "00"))
			return false;

		ResourceMessage message = messageRepository.findByCodeAndMessage(errorKey, resultCode);
		String messageDesc = message == null ? "Unmapping Error Number (" + resultCode + ")" : message.getValue();
		Map<String, String> statusMap = new HashMap<String, String>();
		statusMap.put("status", "01");
		statusMap.put("code", resultCode);
		statusMap.put("message", messageDesc);
		map.put("status", statusMap);

		String feature = FeatureContextHolder.getContext().getFeatureName();

//		if (StringUtils.equalsAny(resultCode, "08")
//				&& (StringUtils.equals(feature, SUPPORTED_URL_DJA_PAY)
//				|| StringUtils.equals(feature, SUPPORTED_URL_DJBC_PAY)
//				|| StringUtils.equals(feature, SUPPORTED_URL_DJP_PAY)
//				|| StringUtils.equals(feature, SUPPORTED_URL_DJPG3_PAY)
//				|| StringUtils.equals(feature, SUPPORTED_URL_DJBCG3_PAY)
//				|| StringUtils.equals(feature, SUPPORTED_URL_DJAG3_PAY)))
//			return false;
		if (StringUtils.equalsAny(resultCode, "08", "90")
				&& (StringUtils.equals(feature, SUPPORTED_URL_DJA_PAY)
				|| StringUtils.equals(feature, SUPPORTED_URL_DJBC_PAY)
				|| StringUtils.equals(feature, SUPPORTED_URL_DJP_PAY)))
			return false;

		if (StringUtils.equalsAny(resultCode, "08")
				&& (StringUtils.equals(feature, SUPPORTED_URL_DJPG3_PAY)
				|| StringUtils.equals(feature, SUPPORTED_URL_DJBCG3_PAY)
				|| StringUtils.equals(feature, SUPPORTED_URL_DJAG3_PAY)))
			return false;

		return true;
	}
}
