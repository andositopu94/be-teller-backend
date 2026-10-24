package com.multipolar.uiteller.backend.service.autodebit;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.stereotype.Service;

import com.multipolar.uiteller.backend.model.Autodebit;

@Service
public class PlnAutodebit extends AbstractAutodebitProcessor {
	private static final String PLN_POSTPAID = "pln";
	
	@Override
	public String getMessageRouting() {
		return "03015";
	}

	@Override
	public String getMessageRouting(String s) {
		return null;
	}

	@Override
	public boolean supports(String feature) {
		return StringUtils.equals(PLN_POSTPAID, feature);
	}

	@Override
	protected void setErrorMappingKey(Map<String, Object> map) {
		map.put("errorKey", "pln");
		map.put("errorInquiryKey", "inquiryPln");		
	}

	@Override
	public Map<String, Object> construcRequestMap(Autodebit data) {
		Map<String, Object> map = super.construcRequestMap(data);		

		String customerId = data.getCustomerId();
		map.put("meterNumber", customerId);

		return map;
	}
	
	@Override
	public ISOMsg createPaymentRequest(ISOMsg msg, Autodebit data) throws ISOException {
		ISOMsg request = super.createPaymentRequest(msg, data);
		
		// off bit 48
		request.unset(48);		
		return request;
	}
}
