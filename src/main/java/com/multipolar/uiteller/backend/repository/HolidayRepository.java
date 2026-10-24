package com.multipolar.uiteller.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multipolar.uiteller.backend.model.Holiday;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
	
}
