package com.multipolar.uiteller.backend.controller;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multipolar.uiteller.backend.model.SmsMessageTemplate;
import com.multipolar.uiteller.backend.repository.SmsMessageTemplateRepository;
import com.multipolar.uiteller.backend.service.sms.SMSSender;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@RestController
@RequestMapping("/sms")
public class SMSController {
	
	private static final String PHONE_NO_KEY = "phoneNo";
	
	@Autowired
	private SmsMessageTemplateRepository templateRepository;
	
	@Autowired
	private SMSSender sender;
	
	@Autowired
	private Configuration freemarkerConfig;
	
	@GetMapping("/send/{trx}")
	public ResponseEntity<?> search(@PathVariable String trx, @RequestParam Map<String,String> params) {
		SmsMessageTemplate template = templateRepository.findByTransaction(trx);
		Map<String, String> result = new HashMap<>();
		if (template == null) {
			result.put("message", "Template Message tidak ditemukan, hubungi administrator system");
			return ResponseEntity.ok(result);
		}
		
		if (!params.containsKey(PHONE_NO_KEY)) {
			result.put("message", "Harap mengisi nomor handphone tujuan SMS");
			return ResponseEntity.ok(result);
		}
		
		try {			
			Template t = new Template("sms-template", new StringReader(template.getTemplate()), freemarkerConfig);
			String message = FreeMarkerTemplateUtils.processTemplateIntoString(t, params);
			
			System.out.println(message);
			sender.sendSms(params.get(PHONE_NO_KEY), message);
			result.put("message", "OK");
			return ResponseEntity.ok(result);
		} catch (IOException | TemplateException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
