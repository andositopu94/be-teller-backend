package com.multipolar.uiteller.backend.service.autodebit;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class TirtakualoDtiAutoDebit extends AbstractAutodebitProcessor {

	private static final String TIRTAKUALO_DTI = "tirtakualoDti";

	@Override
	public boolean supports(String feature) {
		return StringUtils.equals(feature, TIRTAKUALO_DTI);
	}

	@Override
	public String getMessageRouting() {
		return "07000";
	}

	@Override
	public String getMessageRouting(String s) {
		return null;
	}

	@Override
	protected void setErrorMappingKey(Map<String, Object> map) {
		map.put("errorKey", "tirtakualoDti");
		map.put("errorInquiryKey", "inquiryTirtakualoDti");		
	}

}
