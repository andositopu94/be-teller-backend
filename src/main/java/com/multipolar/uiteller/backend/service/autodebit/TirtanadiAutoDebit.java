package com.multipolar.uiteller.backend.service.autodebit;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class TirtanadiAutoDebit extends AbstractAutodebitProcessor {

	private static final String TIRTANADI = "tirtanadi";

	@Override
	public boolean supports(String feature) {
		return StringUtils.equals(feature, TIRTANADI);
	}

	@Override
	public String getMessageRouting() {
		return "03008";
	}

	@Override
	public String getMessageRouting(String s) {
		return null;
	}

	@Override
	protected void setErrorMappingKey(Map<String, Object> map) {
		map.put("errorKey", "tirtanadi");
		map.put("errorInquiryKey", "inquiryTirtanadi");		
	}

}
