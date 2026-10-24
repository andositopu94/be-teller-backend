package com.multipolar.uiteller.backend.service.autodebit;

import java.util.Map;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.multipolar.uiteller.backend.model.Autodebit;

public interface AutoDebitAware {
	
	Map<String, Object> construcRequestMap(Autodebit data);
	
	ISOMsg createPaymentRequest(ISOMsg msg, Autodebit data) throws ISOException;
	
	String getMessageRouting();
	String getMessageRouting(String s);
	
	boolean supports(String feature);
	
	void addReportDataFromInquiry(Map<String, Object> map, ISOMsg inquiryResponse);
}
