package com.multipolar.uiteller.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multipolar.uiteller.backend.model.SmsMessageTemplate;

public interface SmsMessageTemplateRepository extends JpaRepository<SmsMessageTemplate, Long> {
	
	SmsMessageTemplate findByTransaction(String trx);
}
