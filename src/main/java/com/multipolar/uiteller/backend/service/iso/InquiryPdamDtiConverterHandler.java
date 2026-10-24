package com.multipolar.uiteller.backend.service.iso;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.multipolar.uiteller.backend.model.converter.ConverterRule;
import com.multipolar.uiteller.backend.model.converter.RequestRule;
import com.multipolar.uiteller.backend.model.converter.Rule;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@Service
public class InquiryPdamDtiConverterHandler extends DefaultConverterHandler {

	private static Logger logger = LoggerFactory.getLogger(InquiryPdamDtiConverterHandler.class);
	
	private static final String SUPPORTED_URL_PDAM_DTI = "pdamDtiInquiry";
	private static final String INFO_ONE = "billInfoOne";
	private static final String PERIOD_ONE = "billPeriodOne";
	private static final String ACCOUNT_ONE = "billAccountOne";
	private static final String AMOUNT_ONE = "billAmountOne";
	private static final String PENALTY_ONE = "penaltyOne";
	private static final String ADDITIONAL_ONE = "additionalAmountOne";
	private static final String ADMIN_ONE = "adminFeeOne";
	private static final String START_ONE = "startMeterOne";
	private static final String END_ONE = "endMeterOne";
	private static final String CUBICATION_ONE = "cubicationOne";
	private static final String INFO_TWO = "billInfoTwo";
	private static final String PERIOD_TWO = "billPeriodTwo";
	private static final String ACCOUNT_TWO = "billAccountTwo";
	private static final String AMOUNT_TWO = "billAmountTwo";
	private static final String PENALTY_TWO = "penaltyTwo";
	private static final String ADDITIONAL_TWO = "additionalAmountTwo";
	private static final String ADMIN_TWO = "adminFeeTwo";
	private static final String START_TWO = "startMeterTwo";
	private static final String END_TWO = "endMeterTwo";
	private static final String CUBICATION_TWO = "cubicationTwo";
	private static final String INFO_THREE = "billInfoThree";
	private static final String PERIOD_THREE = "billPeriodThree";
	private static final String ACCOUNT_THREE = "billAccountThree";
	private static final String AMOUNT_THREE = "billAmountThree";
	private static final String PENALTY_THREE = "penaltyThree";
	private static final String ADDITIONAL_THREE = "additionalAmountThree";
	private static final String ADMIN_THREE = "adminFeeThree";
	private static final String START_THREE = "startMeterThree";
	private static final String END_THREE = "endMeterThree";
	private static final String CUBICATION_THREE = "cubicationThree";
	private static final String INFO_FOUR = "billInfoFour";
	private static final String PERIOD_FOUR = "billPeriodFour";
	private static final String ACCOUNT_FOUR = "billAccountFour";
	private static final String AMOUNT_FOUR = "billAmountFour";
	private static final String PENALTY_FOUR = "penaltyFour";
	private static final String ADDITIONAL_FOUR = "additionalAmountFour";
	private static final String ADMIN_FOUR = "adminFeeFour";
	private static final String START_FOUR = "startMeterFour";
	private static final String END_FOUR = "endMeterFour";
	private static final String CUBICATION_FOUR = "cubicationFour";
	private static final String INFO_FIVE = "billInfoFive";
	private static final String PERIOD_FIVE = "billPeriodFive";
	private static final String ACCOUNT_FIVE = "billAccountFive";
	private static final String AMOUNT_FIVE = "billAmountFive";
	private static final String PENALTY_FIVE = "penaltyFive";
	private static final String ADDITIONAL_FIVE = "additionalAmountFive";
	private static final String ADMIN_FIVE = "adminFeeFive";
	private static final String START_FIVE = "startMeterFive";
	private static final String END_FIVE = "endMeterFive";
	private static final String CUBICATION_FIVE = "cubicationFive";
	private static final String INFO_SIX = "billInfoSix";
	private static final String PERIOD_SIX = "billPeriodSix";
	private static final String ACCOUNT_SIX = "billAccountSix";
	private static final String AMOUNT_SIX = "billAmountSix";
	private static final String PENALTY_SIX = "penaltySix";
	private static final String ADDITIONAL_SIX = "additionalAmountSix";
	private static final String ADMIN_SIX = "adminFeeSix";
	private static final String START_SIX = "startMeterSix";
	private static final String END_SIX = "endMeterSix";
	private static final String CUBICATION_SIX = "cubicationSix";
	private static final String INFO_SEVEN = "billInfoSeven";
	private static final String PERIOD_SEVEN = "billPeriodSeven";
	private static final String ACCOUNT_SEVEN = "billAccountSeven";
	private static final String AMOUNT_SEVEN = "billAmountSeven";
	private static final String PENALTY_SEVEN = "penaltySeven";
	private static final String ADDITIONAL_SEVEN = "additionalAmountSeven";
	private static final String ADMIN_SEVEN = "adminFeeSeven";
	private static final String START_SEVEN = "startMeterSeven";
	private static final String END_SEVEN = "endMeterSeven";
	private static final String CUBICATION_SEVEN = "cubicationSeven";
	private static final String INFO_EIGHT = "billInfoEight";
	private static final String PERIOD_EIGHT = "billPeriodEight";
	private static final String ACCOUNT_EIGHT = "billAccountEight";
	private static final String AMOUNT_EIGHT = "billAmountEight";
	private static final String PENALTY_EIGHT = "penaltyEight";
	private static final String ADDITIONAL_EIGHT = "additionalAmountEight";
	private static final String ADMIN_EIGHT = "adminFeeEight";
	private static final String START_EIGHT = "startMeterEight";
	private static final String END_EIGHT = "endMeterEight";
	private static final String CUBICATION_EIGHT = "cubicationEight";
	private static final String KEY_BIT = "billReport";
	
	@Override
	public boolean supports(String s) {
		return StringUtils.equals(SUPPORTED_URL_PDAM_DTI, s);
	}

	@Override
	protected void setPrivateMessage(ISOMsg msg, Map<String, Object> map) throws ISOException {
		try {
			Rule rule = getRule();

			msg.set(3, rule.getPcode());
			if (StringUtils.equals(rule.getPcode(), Constants.CODE_INQUIRY) || StringUtils.equals(rule.getPcode(), Constants.CHECK_CODE_INQUIRY))
				msg.set(37, StringUtils.leftPad(getReferenceNumber(), 12, "0"));

			String routing = FeatureContextHolder.getContext().getInstansiParameter();
			if (StringUtils.isEmpty(routing))
				throw new ISOException("routing is empty");
			msg.set(63, routing);

			RequestRule[] requestRules = rule.getRequest();
			for (RequestRule requestRule : requestRules) {
				int bit = requestRule.getBit();
				ConverterRule[] values = requestRule.getValue();
				String valueForBit = getValueForBit(map, bit, values);
				msg.set(bit, valueForBit);
			}
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

	protected void setValueForMap(Map<String, Object> map, String bitValue, ConverterRule[] rules) {
		int index = 0;
		String billReport = "1";
		for (ConverterRule rule : rules) {
			String key = rule.getKey();
			int length = rule.getLength();
			String leftpad = rule.getLeftpad();
			String rightpad = rule.getRightpad();
			
			if (length == 0)
				length = bitValue.length();

			if (StringUtils.equals(billReport, "0") && 
					(StringUtils.equals(key, INFO_ONE) || StringUtils.equals(key, PERIOD_ONE) || StringUtils.equals(key, ACCOUNT_ONE) || StringUtils.equals(key, AMOUNT_ONE) || StringUtils.equals(key, PENALTY_ONE) || StringUtils.equals(key, ADDITIONAL_ONE) || StringUtils.equals(key, ADMIN_ONE) || StringUtils.equals(key,  START_ONE) || StringUtils.equals(key, END_ONE) || StringUtils.equals(key, CUBICATION_ONE) || 
							StringUtils.equals(key, INFO_TWO) || StringUtils.equals(key, PERIOD_TWO) || StringUtils.equals(key, ACCOUNT_TWO) || StringUtils.equals(key, AMOUNT_TWO) || StringUtils.equals(key, PENALTY_TWO) || StringUtils.equals(key, ADDITIONAL_TWO) || StringUtils.equals(key, ADMIN_TWO) || StringUtils.equals(key,  START_TWO) || StringUtils.equals(key, END_TWO) || StringUtils.equals(key, CUBICATION_TWO) ||
							StringUtils.equals(key, INFO_THREE) || StringUtils.equals(key, PERIOD_THREE) || StringUtils.equals(key, ACCOUNT_THREE) || StringUtils.equals(key, AMOUNT_THREE) || StringUtils.equals(key, PENALTY_THREE) || StringUtils.equals(key, ADDITIONAL_THREE) || StringUtils.equals(key, ADMIN_THREE) || StringUtils.equals(key,  START_THREE) || StringUtils.equals(key, END_THREE) || StringUtils.equals(key, CUBICATION_THREE) ||
							StringUtils.equals(key, INFO_FOUR) || StringUtils.equals(key, PERIOD_FOUR) || StringUtils.equals(key, ACCOUNT_FOUR) || StringUtils.equals(key, AMOUNT_FOUR) || StringUtils.equals(key, PENALTY_FOUR) || StringUtils.equals(key, ADDITIONAL_FOUR) || StringUtils.equals(key, ADMIN_FOUR) || StringUtils.equals(key,  START_FOUR) || StringUtils.equals(key, END_FOUR) || StringUtils.equals(key, CUBICATION_FOUR) ||
							StringUtils.equals(key, INFO_FIVE) || StringUtils.equals(key, PERIOD_FIVE) || StringUtils.equals(key, ACCOUNT_FIVE) || StringUtils.equals(key, AMOUNT_FIVE) || StringUtils.equals(key, PENALTY_FIVE) || StringUtils.equals(key, ADDITIONAL_FIVE) || StringUtils.equals(key, ADMIN_FIVE) || StringUtils.equals(key,  START_FIVE) || StringUtils.equals(key, END_FIVE) || StringUtils.equals(key, CUBICATION_FIVE) ||
							StringUtils.equals(key, INFO_SIX) || StringUtils.equals(key, PERIOD_SIX) || StringUtils.equals(key, ACCOUNT_SIX) || StringUtils.equals(key, AMOUNT_SIX) || StringUtils.equals(key, PENALTY_SIX) || StringUtils.equals(key, ADDITIONAL_SIX) || StringUtils.equals(key, ADMIN_SIX) || StringUtils.equals(key,  START_SIX) || StringUtils.equals(key, END_SIX) || StringUtils.equals(key, CUBICATION_SIX) ||
							StringUtils.equals(key, INFO_SEVEN) || StringUtils.equals(key, PERIOD_SEVEN) || StringUtils.equals(key, ACCOUNT_SEVEN) || StringUtils.equals(key, AMOUNT_SEVEN) || StringUtils.equals(key, PENALTY_SEVEN) || StringUtils.equals(key, ADDITIONAL_SEVEN) || StringUtils.equals(key, ADMIN_SEVEN) || StringUtils.equals(key,  START_SEVEN) || StringUtils.equals(key, END_SEVEN) || StringUtils.equals(key, CUBICATION_SEVEN) ||
							StringUtils.equals(key, INFO_EIGHT) || StringUtils.equals(key, PERIOD_EIGHT) || StringUtils.equals(key, ACCOUNT_EIGHT) || StringUtils.equals(key, AMOUNT_EIGHT) || StringUtils.equals(key, PENALTY_EIGHT) || StringUtils.equals(key, ADDITIONAL_EIGHT) || StringUtils.equals(key, ADMIN_EIGHT) || StringUtils.equals(key,  START_EIGHT) || StringUtils.equals(key, END_EIGHT) || StringUtils.equals(key, CUBICATION_EIGHT)))
				continue;
			if (StringUtils.equals(billReport, "1") && 
					(StringUtils.equals(key, INFO_TWO) || StringUtils.equals(key, PERIOD_TWO) || StringUtils.equals(key, ACCOUNT_TWO) || StringUtils.equals(key, AMOUNT_TWO) || StringUtils.equals(key, PENALTY_TWO) || StringUtils.equals(key, ADDITIONAL_TWO) || StringUtils.equals(key, ADMIN_TWO) || StringUtils.equals(key,  START_TWO) || StringUtils.equals(key, END_TWO) || StringUtils.equals(key, CUBICATION_TWO) ||
							StringUtils.equals(key, INFO_THREE) || StringUtils.equals(key, PERIOD_THREE) || StringUtils.equals(key, ACCOUNT_THREE) || StringUtils.equals(key, AMOUNT_THREE) || StringUtils.equals(key, PENALTY_THREE) || StringUtils.equals(key, ADDITIONAL_THREE) || StringUtils.equals(key, ADMIN_THREE) || StringUtils.equals(key,  START_THREE) || StringUtils.equals(key, END_THREE) || StringUtils.equals(key, CUBICATION_THREE) ||
							StringUtils.equals(key, INFO_FOUR) || StringUtils.equals(key, PERIOD_FOUR) || StringUtils.equals(key, ACCOUNT_FOUR) || StringUtils.equals(key, AMOUNT_FOUR) || StringUtils.equals(key, PENALTY_FOUR) || StringUtils.equals(key, ADDITIONAL_FOUR) || StringUtils.equals(key, ADMIN_FOUR) || StringUtils.equals(key,  START_FOUR) || StringUtils.equals(key, END_FOUR) || StringUtils.equals(key, CUBICATION_FOUR) ||
							StringUtils.equals(key, INFO_FIVE) || StringUtils.equals(key, PERIOD_FIVE) || StringUtils.equals(key, ACCOUNT_FIVE) || StringUtils.equals(key, AMOUNT_FIVE) || StringUtils.equals(key, PENALTY_FIVE) || StringUtils.equals(key, ADDITIONAL_FIVE) || StringUtils.equals(key, ADMIN_FIVE) || StringUtils.equals(key,  START_FIVE) || StringUtils.equals(key, END_FIVE) || StringUtils.equals(key, CUBICATION_FIVE) ||
							StringUtils.equals(key, INFO_SIX) || StringUtils.equals(key, PERIOD_SIX) || StringUtils.equals(key, ACCOUNT_SIX) || StringUtils.equals(key, AMOUNT_SIX) || StringUtils.equals(key, PENALTY_SIX) || StringUtils.equals(key, ADDITIONAL_SIX) || StringUtils.equals(key, ADMIN_SIX) || StringUtils.equals(key,  START_SIX) || StringUtils.equals(key, END_SIX) || StringUtils.equals(key, CUBICATION_SIX) ||
							StringUtils.equals(key, INFO_SEVEN) || StringUtils.equals(key, PERIOD_SEVEN) || StringUtils.equals(key, ACCOUNT_SEVEN) || StringUtils.equals(key, AMOUNT_SEVEN) || StringUtils.equals(key, PENALTY_SEVEN) || StringUtils.equals(key, ADDITIONAL_SEVEN) || StringUtils.equals(key, ADMIN_SEVEN) || StringUtils.equals(key,  START_SEVEN) || StringUtils.equals(key, END_SEVEN) || StringUtils.equals(key, CUBICATION_SEVEN) ||
							StringUtils.equals(key, INFO_EIGHT) || StringUtils.equals(key, PERIOD_EIGHT) || StringUtils.equals(key, ACCOUNT_EIGHT) || StringUtils.equals(key, AMOUNT_EIGHT) || StringUtils.equals(key, PENALTY_EIGHT) || StringUtils.equals(key, ADDITIONAL_EIGHT) || StringUtils.equals(key, ADMIN_EIGHT) || StringUtils.equals(key,  START_EIGHT) || StringUtils.equals(key, END_EIGHT) || StringUtils.equals(key, CUBICATION_EIGHT)))
				continue;
			if (StringUtils.equals(billReport, "2") && 
					(StringUtils.equals(key, INFO_THREE) || StringUtils.equals(key, PERIOD_THREE) || StringUtils.equals(key, ACCOUNT_THREE) || StringUtils.equals(key, AMOUNT_THREE) || StringUtils.equals(key, PENALTY_THREE) || StringUtils.equals(key, ADDITIONAL_THREE) || StringUtils.equals(key, ADMIN_THREE) || StringUtils.equals(key,  START_THREE) || StringUtils.equals(key, END_THREE) || StringUtils.equals(key, CUBICATION_THREE) ||
							StringUtils.equals(key, INFO_FOUR) || StringUtils.equals(key, PERIOD_FOUR) || StringUtils.equals(key, ACCOUNT_FOUR) || StringUtils.equals(key, AMOUNT_FOUR) || StringUtils.equals(key, PENALTY_FOUR) || StringUtils.equals(key, ADDITIONAL_FOUR) || StringUtils.equals(key, ADMIN_FOUR) || StringUtils.equals(key,  START_FOUR) || StringUtils.equals(key, END_FOUR) || StringUtils.equals(key, CUBICATION_FOUR) ||
							StringUtils.equals(key, INFO_FIVE) || StringUtils.equals(key, PERIOD_FIVE) || StringUtils.equals(key, ACCOUNT_FIVE) || StringUtils.equals(key, AMOUNT_FIVE) || StringUtils.equals(key, PENALTY_FIVE) || StringUtils.equals(key, ADDITIONAL_FIVE) || StringUtils.equals(key, ADMIN_FIVE) || StringUtils.equals(key,  START_FIVE) || StringUtils.equals(key, END_FIVE) || StringUtils.equals(key, CUBICATION_FIVE) ||
							StringUtils.equals(key, INFO_SIX) || StringUtils.equals(key, PERIOD_SIX) || StringUtils.equals(key, ACCOUNT_SIX) || StringUtils.equals(key, AMOUNT_SIX) || StringUtils.equals(key, PENALTY_SIX) || StringUtils.equals(key, ADDITIONAL_SIX) || StringUtils.equals(key, ADMIN_SIX) || StringUtils.equals(key,  START_SIX) || StringUtils.equals(key, END_SIX) || StringUtils.equals(key, CUBICATION_SIX) ||
							StringUtils.equals(key, INFO_SEVEN) || StringUtils.equals(key, PERIOD_SEVEN) || StringUtils.equals(key, ACCOUNT_SEVEN) || StringUtils.equals(key, AMOUNT_SEVEN) || StringUtils.equals(key, PENALTY_SEVEN) || StringUtils.equals(key, ADDITIONAL_SEVEN) || StringUtils.equals(key, ADMIN_SEVEN) || StringUtils.equals(key,  START_SEVEN) || StringUtils.equals(key, END_SEVEN) || StringUtils.equals(key, CUBICATION_SEVEN) ||
							StringUtils.equals(key, INFO_EIGHT) || StringUtils.equals(key, PERIOD_EIGHT) || StringUtils.equals(key, ACCOUNT_EIGHT) || StringUtils.equals(key, AMOUNT_EIGHT) || StringUtils.equals(key, PENALTY_EIGHT) || StringUtils.equals(key, ADDITIONAL_EIGHT) || StringUtils.equals(key, ADMIN_EIGHT) || StringUtils.equals(key,  START_EIGHT) || StringUtils.equals(key, END_EIGHT) || StringUtils.equals(key, CUBICATION_EIGHT)))
				continue;
			if (StringUtils.equals(billReport, "3") && 
					(StringUtils.equals(key, INFO_FOUR) || StringUtils.equals(key, PERIOD_FOUR) || StringUtils.equals(key, ACCOUNT_FOUR) || StringUtils.equals(key, AMOUNT_FOUR) || StringUtils.equals(key, PENALTY_FOUR) || StringUtils.equals(key, ADDITIONAL_FOUR) || StringUtils.equals(key, ADMIN_FOUR) || StringUtils.equals(key,  START_FOUR) || StringUtils.equals(key, END_FOUR) || StringUtils.equals(key, CUBICATION_FOUR) ||
							StringUtils.equals(key, INFO_FIVE) || StringUtils.equals(key, PERIOD_FIVE) || StringUtils.equals(key, ACCOUNT_FIVE) || StringUtils.equals(key, AMOUNT_FIVE) || StringUtils.equals(key, PENALTY_FIVE) || StringUtils.equals(key, ADDITIONAL_FIVE) || StringUtils.equals(key, ADMIN_FIVE) || StringUtils.equals(key,  START_FIVE) || StringUtils.equals(key, END_FIVE) || StringUtils.equals(key, CUBICATION_FIVE) ||
							StringUtils.equals(key, INFO_SIX) || StringUtils.equals(key, PERIOD_SIX) || StringUtils.equals(key, ACCOUNT_SIX) || StringUtils.equals(key, AMOUNT_SIX) || StringUtils.equals(key, PENALTY_SIX) || StringUtils.equals(key, ADDITIONAL_SIX) || StringUtils.equals(key, ADMIN_SIX) || StringUtils.equals(key,  START_SIX) || StringUtils.equals(key, END_SIX) || StringUtils.equals(key, CUBICATION_SIX) ||
							StringUtils.equals(key, INFO_SEVEN) || StringUtils.equals(key, PERIOD_SEVEN) || StringUtils.equals(key, ACCOUNT_SEVEN) || StringUtils.equals(key, AMOUNT_SEVEN) || StringUtils.equals(key, PENALTY_SEVEN) || StringUtils.equals(key, ADDITIONAL_SEVEN) || StringUtils.equals(key, ADMIN_SEVEN) || StringUtils.equals(key,  START_SEVEN) || StringUtils.equals(key, END_SEVEN) || StringUtils.equals(key, CUBICATION_SEVEN) ||
							StringUtils.equals(key, INFO_EIGHT) || StringUtils.equals(key, PERIOD_EIGHT) || StringUtils.equals(key, ACCOUNT_EIGHT) || StringUtils.equals(key, AMOUNT_EIGHT) || StringUtils.equals(key, PENALTY_EIGHT) || StringUtils.equals(key, ADDITIONAL_EIGHT) || StringUtils.equals(key, ADMIN_EIGHT) || StringUtils.equals(key,  START_EIGHT) || StringUtils.equals(key, END_EIGHT) || StringUtils.equals(key, CUBICATION_EIGHT)))
				continue;
			if (StringUtils.equals(billReport, "4") && 
					(StringUtils.equals(key, INFO_FIVE) || StringUtils.equals(key, PERIOD_FIVE) || StringUtils.equals(key, ACCOUNT_FIVE) || StringUtils.equals(key, AMOUNT_FIVE) || StringUtils.equals(key, PENALTY_FIVE) || StringUtils.equals(key, ADDITIONAL_FIVE) || StringUtils.equals(key, ADMIN_FIVE) || StringUtils.equals(key,  START_FIVE) || StringUtils.equals(key, END_FIVE) || StringUtils.equals(key, CUBICATION_FIVE) ||
							StringUtils.equals(key, INFO_SIX) || StringUtils.equals(key, PERIOD_SIX) || StringUtils.equals(key, ACCOUNT_SIX) || StringUtils.equals(key, AMOUNT_SIX) || StringUtils.equals(key, PENALTY_SIX) || StringUtils.equals(key, ADDITIONAL_SIX) || StringUtils.equals(key, ADMIN_SIX) || StringUtils.equals(key,  START_SIX) || StringUtils.equals(key, END_SIX) || StringUtils.equals(key, CUBICATION_SIX) ||
							StringUtils.equals(key, INFO_SEVEN) || StringUtils.equals(key, PERIOD_SEVEN) || StringUtils.equals(key, ACCOUNT_SEVEN) || StringUtils.equals(key, AMOUNT_SEVEN) || StringUtils.equals(key, PENALTY_SEVEN) || StringUtils.equals(key, ADDITIONAL_SEVEN) || StringUtils.equals(key, ADMIN_SEVEN) || StringUtils.equals(key,  START_SEVEN) || StringUtils.equals(key, END_SEVEN) || StringUtils.equals(key, CUBICATION_SEVEN) ||
							StringUtils.equals(key, INFO_EIGHT) || StringUtils.equals(key, PERIOD_EIGHT) || StringUtils.equals(key, ACCOUNT_EIGHT) || StringUtils.equals(key, AMOUNT_EIGHT) || StringUtils.equals(key, PENALTY_EIGHT) || StringUtils.equals(key, ADDITIONAL_EIGHT) || StringUtils.equals(key, ADMIN_EIGHT) || StringUtils.equals(key,  START_EIGHT) || StringUtils.equals(key, END_EIGHT) || StringUtils.equals(key, CUBICATION_EIGHT)))
				continue;
			if (StringUtils.equals(billReport, "5") && 
					(StringUtils.equals(key, INFO_SIX) || StringUtils.equals(key, PERIOD_SIX) || StringUtils.equals(key, ACCOUNT_SIX) || StringUtils.equals(key, AMOUNT_SIX) || StringUtils.equals(key, PENALTY_SIX) || StringUtils.equals(key, ADDITIONAL_SIX) || StringUtils.equals(key, ADMIN_SIX) || StringUtils.equals(key,  START_SIX) || StringUtils.equals(key, END_SIX) || StringUtils.equals(key, CUBICATION_SIX) ||
							StringUtils.equals(key, INFO_SEVEN) || StringUtils.equals(key, PERIOD_SEVEN) || StringUtils.equals(key, ACCOUNT_SEVEN) || StringUtils.equals(key, AMOUNT_SEVEN) || StringUtils.equals(key, PENALTY_SEVEN) || StringUtils.equals(key, ADDITIONAL_SEVEN) || StringUtils.equals(key, ADMIN_SEVEN) || StringUtils.equals(key,  START_SEVEN) || StringUtils.equals(key, END_SEVEN) || StringUtils.equals(key, CUBICATION_SEVEN) ||
							StringUtils.equals(key, INFO_EIGHT) || StringUtils.equals(key, PERIOD_EIGHT) || StringUtils.equals(key, ACCOUNT_EIGHT) || StringUtils.equals(key, AMOUNT_EIGHT) || StringUtils.equals(key, PENALTY_EIGHT) || StringUtils.equals(key, ADDITIONAL_EIGHT) || StringUtils.equals(key, ADMIN_EIGHT) || StringUtils.equals(key,  START_EIGHT) || StringUtils.equals(key, END_EIGHT) || StringUtils.equals(key, CUBICATION_EIGHT)))
				continue;
			if (StringUtils.equals(billReport, "6") && 
					(StringUtils.equals(key, INFO_SEVEN) || StringUtils.equals(key, PERIOD_SEVEN) || StringUtils.equals(key, ACCOUNT_SEVEN) || StringUtils.equals(key, AMOUNT_SEVEN) || StringUtils.equals(key, PENALTY_SEVEN) || StringUtils.equals(key, ADDITIONAL_SEVEN) || StringUtils.equals(key, ADMIN_SEVEN) || StringUtils.equals(key,  START_SEVEN) || StringUtils.equals(key, END_SEVEN) || StringUtils.equals(key, CUBICATION_SEVEN) ||
							StringUtils.equals(key, INFO_EIGHT) || StringUtils.equals(key, PERIOD_EIGHT) || StringUtils.equals(key, ACCOUNT_EIGHT) || StringUtils.equals(key, AMOUNT_EIGHT) || StringUtils.equals(key, PENALTY_EIGHT) || StringUtils.equals(key, ADDITIONAL_EIGHT) || StringUtils.equals(key, ADMIN_EIGHT) || StringUtils.equals(key,  START_EIGHT) || StringUtils.equals(key, END_EIGHT) || StringUtils.equals(key, CUBICATION_EIGHT)))
				continue;
			if (StringUtils.equals(billReport, "7") && 
					(StringUtils.equals(key, INFO_EIGHT) || StringUtils.equals(key, PERIOD_EIGHT) || StringUtils.equals(key, ACCOUNT_EIGHT) || StringUtils.equals(key, AMOUNT_EIGHT) || StringUtils.equals(key, PENALTY_EIGHT) || StringUtils.equals(key, ADDITIONAL_EIGHT) || StringUtils.equals(key, ADMIN_EIGHT) || StringUtils.equals(key,  START_EIGHT) || StringUtils.equals(key, END_EIGHT) || StringUtils.equals(key, CUBICATION_EIGHT)))
				continue;
			
			String value = StringUtils.substring(bitValue, index, index + length);
			if (leftpad != null)
				value = StringUtils.stripStart(value, leftpad);
			else if (rightpad != null)
				value = StringUtils.stripEnd(value, rightpad);
			
			if (StringUtils.equals(KEY_BIT, key)) {
				billReport = value;
				if (StringUtils.equals(billReport, "0")) {
					map.put("FORTUNA_EXCEPTION", "Jumlah Tagihan 0");
					return;
				}
			}
			
			map.put(key, value);
			index = index + length;
		}
	}
	
	

}
