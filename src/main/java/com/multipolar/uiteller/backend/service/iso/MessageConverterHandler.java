package com.multipolar.uiteller.backend.service.iso;

import java.util.Map;

import org.jpos.iso.ISOMsg;

public interface MessageConverterHandler {
		
	Map<String, Object> doConvertToJSon(ISOMsg isoMsg, boolean fl) throws InvalidMessageException;

	ISOMsg doConvertToISO(Map<String, Object> map) throws InvalidMessageException;

    boolean supports(String s);
}
