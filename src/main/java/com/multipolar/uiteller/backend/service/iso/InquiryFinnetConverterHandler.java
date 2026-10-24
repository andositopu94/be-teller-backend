package com.multipolar.uiteller.backend.service.iso;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.multipolar.uiteller.backend.model.converter.ConverterRule;

@Service
public class InquiryFinnetConverterHandler extends DefaultConverterHandler {

//	private static Logger logger = LoggerFactory.getLogger(InquiryPstnConverterHandler.class);
	
	private static final String SUPPORTED_URL_PSTN = "pstnInquiry";
	private static final String SUPPORTED_URL_SPEEDY = "speedyInquiry";
	private static final String KEY_BIT = "billNumber";
	private static final String REFF_THREE = "billReferenceThree";
	private static final String VALUE_THREE = "billValueThree";
	private static final String REFF_TWO = "billReferenceTwo";
	private static final String VALUE_TWO = "billValueTwo";
	private static final String REFF_ONE = "billReferenceOne";
	private static final String VALUE_ONE = "billValueOne";

	@Override
	public boolean supports(String s) {
		return StringUtils.equals(SUPPORTED_URL_PSTN, s) || StringUtils.equals(SUPPORTED_URL_SPEEDY, s);
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
				if (StringUtils.equals(billNumber, "0")) {
					map.put("FINNET_EXCEPTION", "Nilai tagihan Rp. 0");
					return;
				}
			}
			
			map.put(key, value);
			index = index + length;
		}
	}
	
	

}
