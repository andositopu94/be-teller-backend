package com.multipolar.uiteller.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multipolar.uiteller.backend.model.ManualAdvice;

public interface ManualAdviceRepository extends JpaRepository<ManualAdvice, Long> {
	ManualAdvice findByRefNum(String refNum);
}
