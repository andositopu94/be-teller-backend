package com.multipolar.uiteller.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multipolar.uiteller.backend.model.TimeoutResourceMessage;

public interface TimeoutResourceMessageRepository extends JpaRepository<TimeoutResourceMessage, Long> {
	TimeoutResourceMessage findByCodeAndMessage(String code, String message);
}
