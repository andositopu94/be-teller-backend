package com.multipolar.uiteller.backend.service.autodebit;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;

import com.multipolar.uiteller.backend.model.Autodebit;
import com.multipolar.uiteller.backend.security.model.AccountResponse;
import com.multipolar.uiteller.backend.security.model.AccountResult;
import com.multipolar.uiteller.backend.service.CoreGatewayServiceImpl;

public abstract class AbstractAutodebitProcessor implements AutoDebitAware {
	
	@Autowired
    private CoreGatewayServiceImpl coreGatewayService;

	@Override
	public Map<String, Object> construcRequestMap(Autodebit data) {
		Map<String, Object> map = new HashMap<>();
		map.put("sourceNumber", data.getSourceNumber());
		map.put("customerId", data.getCustomerId());
		map.put("currency", "360");
		map.put("branchName", "SYSTEM");
		
		
		map.put("adminCharge", data.isCharged() ? "0" : "1");
		
		AccountResponse accountResponse = coreGatewayService.getAccountResponse(data.getSourceNumber());
        if (accountResponse == null)
        	map.put("sourceName", "-");
        else {
        	AccountResult accountResult = accountResponse.getResult();
        	String accountName = accountResult.getNICKNM();
        	map.put("sourceName", accountName);
        }
		
		setErrorMappingKey(map);
		
		return map;
	}

	@Override
	public ISOMsg createPaymentRequest(ISOMsg msg, Autodebit data) throws ISOException {
		ISOMsg request = (ISOMsg)msg.clone();
		request.set(3, "500000");
		request.setMTI("0200");
		
		// charge
		String charged = data.isCharged() ? "0" : "1";
		String chargedBit = StringUtils.leftPad(charged, 9, "0");
		request.set(28, chargedBit);
		request.unset(39);
		return request;
	}
	
	@Override
	public void addReportDataFromInquiry(Map<String, Object> map, ISOMsg inquiryResponse) {
		// do nothing
	}

	protected abstract void setErrorMappingKey(Map<String, Object> map);
}
