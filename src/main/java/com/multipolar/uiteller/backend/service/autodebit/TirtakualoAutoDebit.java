package com.multipolar.uiteller.backend.service.autodebit;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class TirtakualoAutoDebit extends AbstractAutodebitProcessor {

	private static final String TIRTAKUALO = "tirtakualo";

	@Override
	public boolean supports(String feature) {
		return StringUtils.equals(feature, TIRTAKUALO);
	}

	@Override
	public String getMessageRouting() {
		return "03020";
	}

	@Override
	public String getMessageRouting(String s) {
		return null;
	}

	@Override
	protected void setErrorMappingKey(Map<String, Object> map) {
		map.put("errorKey", "tirtakualo");
		map.put("errorInquiryKey", "inquiryTirtakualo");		
	}

}
