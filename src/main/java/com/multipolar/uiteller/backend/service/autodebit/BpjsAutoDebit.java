package com.multipolar.uiteller.backend.service.autodebit;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class BpjsAutoDebit extends AbstractAutodebitProcessor {

	private static final String BPJS = "bpjs";

	@Override
	public boolean supports(String feature) {
		return StringUtils.equals(feature, BPJS);
	}

	@Override
	public String getMessageRouting() {
		return "03018";
	}

	@Override
	public String getMessageRouting(String s) {
		return null;
	}

	@Override
	protected void setErrorMappingKey(Map<String, Object> map) {
		map.put("errorKey", "bpjsInquiry");
		map.put("errorInquiryKey", "bpjs");		
	}

}
