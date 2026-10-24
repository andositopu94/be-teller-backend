package com.multipolar.uiteller.backend.service.sms;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SMSModel {
	@JsonProperty("phoneNo")
	private String phoneNo;
	@JsonProperty("message")
	private String message;
	@JsonProperty("trxCode")
	private String trxCode = "NOTIF_TELLER";
	
	public SMSModel() {}
	public SMSModel(String phoneNo, String message) {
		this.phoneNo = phoneNo;
		this.message = message;
	}
}
