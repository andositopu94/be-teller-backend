package com.multipolar.uiteller.backend.service.iso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.model.converter.ConverterRule;
import com.multipolar.uiteller.backend.repository.TransactionLogRepository;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;

@Service
public class PaymentFinnetConverterHandler extends DefaultConverterHandler {

//	private static Logger logger = LoggerFactory.getLogger(InquiryPstnConverterHandler.class);
    @Autowired
    private TransactionLogRepository transactionLogRepository;
    
	private static final String SUPPORTED_URL_PSTN = "pstn";
	private static final String SUPPORTED_URL_SPEEDY = "speedy";
	private static final String KEY_BIT = "billNumber";
	private static final String REFF_THREE = "billReferenceThree";
	private static final String VALUE_THREE = "billValueThree";
	private static final String REFF_TWO = "billReferenceTwo";
	private static final String VALUE_TWO = "billValueTwo";
	private static final String REFF_ONE = "billReferenceOne";
	private static final String VALUE_ONE = "billValueOne";

	private static Logger logger = LoggerFactory.getLogger(PaymentFinnetConverterHandler.class);

	@Override
	public boolean supports(String s) {
		return StringUtils.equals(SUPPORTED_URL_PSTN, s) || StringUtils.equals(SUPPORTED_URL_SPEEDY, s);
	}
	
	protected String getValueForBit(Map<String, Object> map, int bit, ConverterRule[] rules)
			throws IllegalArgumentException {
		Map<String, String> values = new HashMap<>();
		
		String billNumber = "1";
		
		StringBuilder message = new StringBuilder();
		for (ConverterRule rule : rules) {
			String other = rule.getOther();			
			String key = rule.getKey();
			int length = rule.getLength();
			String leftpad = rule.getLeftpad();
			String rightpad = rule.getRightpad();
			
			if (StringUtils.equals(billNumber, "1") && 
					(StringUtils.equals(key, REFF_THREE) || StringUtils.equals(key, VALUE_THREE) || 
							StringUtils.equals(key, REFF_TWO) || StringUtils.equals(key, VALUE_TWO)))
				continue;
			if (StringUtils.equals(billNumber, "2") && 
					(StringUtils.equals(key, REFF_THREE) || StringUtils.equals(key, VALUE_THREE)))
				continue;
			
			String requestValue = null;
			if (other != null)
				requestValue = other;
			else if (key != null) {
				Object value = map.get(key);
				if (value == null) {
					logger.warn(
							"key value is null, please check schema validation or rule for this request. Value is set to empty string. Key is " + key);
					requestValue = "";
				} else {
					requestValue = value.toString();
				}
			}
			
			if (StringUtils.equals(KEY_BIT, key)) {
				billNumber = requestValue;
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
		}

		return message.toString();
	}

	protected void setValueForMap(Map<String, Object> map, String bitValue, ConverterRule[] rules) {
		int index = 0;
		String billNumber = "1";
		for (ConverterRule rule : rules) {
			String key = rule.getKey();
			int length = rule.getLength();
			String leftpad = rule.getLeftpad();
			String rightpad = rule.getRightpad();
			
			if (length == 0)
				length = bitValue.length();

			if (StringUtils.equals(billNumber, "0") &&
					(StringUtils.equals(key, REFF_THREE) || StringUtils.equals(key, VALUE_THREE) || StringUtils.equals(key, REFF_TWO) || StringUtils.equals(key, VALUE_TWO) || StringUtils.equals(key, REFF_ONE) || StringUtils.equals(key, VALUE_ONE)))
				continue;
			if (StringUtils.equals(billNumber, "1") &&
					(StringUtils.equals(key, REFF_THREE) || StringUtils.equals(key, VALUE_THREE) || 
							StringUtils.equals(key, REFF_TWO) || StringUtils.equals(key, VALUE_TWO)))
				continue;
			if (StringUtils.equals(billNumber, "2") && 
					(StringUtils.equals(key, REFF_THREE) || StringUtils.equals(key, VALUE_THREE)))
				continue;
			
			String value = StringUtils.substring(bitValue, index, index + length);
			if (leftpad != null)
				value = StringUtils.stripStart(value, leftpad);
			else if (rightpad != null)
				value = StringUtils.stripEnd(value, rightpad);
			
			if (StringUtils.equals(KEY_BIT, key)) {
				billNumber = value;
			}
			
			map.put(key, value);
			index = index + length;
		}
	}
	
    @Override
    protected Map<String, Object> translatePrivateMessage(ISOMsg msg, boolean fl) throws ISOException {
        Map<String, Object> stringObjectMap = super.translatePrivateMessage(msg, fl);
        if (msg.getString(76) != null) {
            List<TransactionLog> transactionLogs = transactionLogRepository.findByCommonTransactionId(transactionLogRepository.findFirstByFieldNameAndFieldValueAndTrxTypeOrderByIdDesc(Constants.REFFERENCE_FIELD_NAME, StringUtils.stripStart(msg.getString(37), Constants.ZERO), FeatureContextHolder.getContext().getFeatureName()).getCommonTransaction().getId());
            Map<String, String> objectMap = new HashMap<>();
            for (TransactionLog log : transactionLogs)
                objectMap.put(log.getFieldName(), log.getFieldValue());
            stringObjectMap.put("customerName", objectMap.get("customerName") == null ? "" : objectMap.get("customerName"));
            stringObjectMap.put("taxNumber", objectMap.get("taxNumber") == null ? "" : objectMap.get("taxNumber"));
            stringObjectMap.put("archiveNumber", objectMap.get("archiveNumber") == null ? "" : objectMap.get("archiveNumber"));
        }
        return stringObjectMap;
    }
	
	

}
