package com.multipolar.uiteller.backend.service.sms;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SMSResponse {
	@JsonProperty("rc")
	private String rc;
	@JsonProperty("message")
	private String message;
}
