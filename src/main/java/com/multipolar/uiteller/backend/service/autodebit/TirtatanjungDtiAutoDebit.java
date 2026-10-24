package com.multipolar.uiteller.backend.service.autodebit;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class TirtatanjungDtiAutoDebit extends AbstractAutodebitProcessor {

	private static final String TIRTATANJUNG_DTI = "tirtatanjungDti";

	@Override
	public boolean supports(String feature) {
		return StringUtils.equals(feature, TIRTATANJUNG_DTI);
	}

	@Override
	public String getMessageRouting() {
		return "07001";
	}

	@Override
	public String getMessageRouting(String s) {
		return null;
	}

	@Override
	protected void setErrorMappingKey(Map<String, Object> map) {
		map.put("errorKey", "tirtatanjungDti");
		map.put("errorInquiryKey", "inquiryTirtatanjungDti");		
	}

}
