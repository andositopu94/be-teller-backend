package com.multipolar.uiteller.backend.service.autodebit;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class TirtabulianAutoDebit extends AbstractAutodebitProcessor {

	private static final String TIRTABULIAN = "tirtabulian";

	@Override
	public boolean supports(String feature) {
		return StringUtils.equals(feature, TIRTABULIAN);
	}

	@Override
	public String getMessageRouting() {
		return "03009";
	}

	@Override
	public String getMessageRouting(String s) {
		return null;
	}

	@Override
	protected void setErrorMappingKey(Map<String, Object> map) {
		map.put("errorKey", "tirtabulian");
		map.put("errorInquiryKey", "inquiryTirtabulian");		
	}

}
