package com.multipolar.uiteller.backend.model.converter;

import lombok.Data;

@Data
public class SiskoRule {
	private String pcode;

	private String errorKey;

	private String transaction;
	
	
	private RequestRule[] request;
	private RequestRule[] response;
	
	private RequestRule[] report;
}
