package com.multipolar.uiteller.backend.service.email;

import java.util.Map;

import lombok.Data;

@Data
public class MailModel {
	private String mailTo;
	private String subject;
	private String body;
	private String templateName;
	private Map<String, Object> templateModel;
}
