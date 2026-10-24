package com.multipolar.uiteller.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multipolar.uiteller.backend.model.SmsMessageTemplate;
import com.multipolar.uiteller.backend.repository.SmsMessageTemplateRepository;

@RestController
@RequestMapping("/admin")
public class SmsMessageTemplateController {
	
	@Autowired
	private SmsMessageTemplateRepository templateRepository;

	@GetMapping("/sms-template")
	public List<SmsMessageTemplate> getAll() {
		return templateRepository.findAll();
	}
	
	@PostMapping("/sms-template")
	public List<SmsMessageTemplate> create(@RequestBody SmsMessageTemplate template) {
		templateRepository.save(template);
		
		return templateRepository.findAll();
	}
	
	@PostMapping("/sms-template/{id}")
	public List<SmsMessageTemplate> update(@PathVariable Long id, @RequestBody SmsMessageTemplate template) {
		SmsMessageTemplate old = templateRepository.findOne(id);
		old.setTemplate(template.getTemplate());
		templateRepository.save(old);
		
		return templateRepository.findAll();
	}
	
	@DeleteMapping("sms-template/{id}")
	public List<SmsMessageTemplate> delete(@PathVariable Long id) {
		SmsMessageTemplate template = templateRepository.findOne(id);
		templateRepository.delete(template);
		
		return templateRepository.findAll();
	}
	
	@GetMapping("sms-template/{id}")
	public SmsMessageTemplate findById(@PathVariable Long id) {
		return templateRepository.findOne(id);
	}
	
}
