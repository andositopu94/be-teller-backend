package com.multipolar.uiteller.backend.service.autodebit;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class TirtauliAutoDebit extends AbstractAutodebitProcessor {

	private static final String TIRTAULI = "tirtauli";

	@Override
	public boolean supports(String feature) {
		return StringUtils.equals(feature, TIRTAULI);
	}

	@Override
	public String getMessageRouting() {
		return "03012";
	}

	@Override
	public String getMessageRouting(String s) {
		return null;
	}

	@Override
	protected void setErrorMappingKey(Map<String, Object> map) {
		map.put("errorKey", "tirtauli");
		map.put("errorInquiryKey", "inquiryTirtauli");		
	}

}
