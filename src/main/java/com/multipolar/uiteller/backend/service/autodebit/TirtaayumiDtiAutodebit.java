package com.multipolar.uiteller.backend.service.autodebit;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class TirtaayumiDtiAutodebit extends AbstractAutodebitProcessor {

	private static final String TIRTAAYUMI_DTI = "tirtaayumiDtii";

	@Override
	public boolean supports(String feature) {
		return StringUtils.equals(feature, TIRTAAYUMI_DTI);
	}

	@Override
	public String getMessageRouting() {
		return "06009";
	}

	@Override
	public String getMessageRouting(String s) {
		return null;
	}

	@Override
	protected void setErrorMappingKey(Map<String, Object> map) {
		map.put("errorKey", "tirtaayumiDti");
		map.put("errorInquiryKey", "inquiryTirtaayumiDti");		
	}

}
