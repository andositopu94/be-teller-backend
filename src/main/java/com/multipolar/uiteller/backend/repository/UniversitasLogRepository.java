package com.multipolar.uiteller.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multipolar.uiteller.backend.model.UniversitasLog;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UniversitasLogRepository extends JpaRepository<UniversitasLog, Long> {
	List<UniversitasLog> findByArchiveNumber(String archiveNumber);
}
