package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.UniversitasInitLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UniversitasInitLogRepository extends JpaRepository<UniversitasInitLog, Long> {
	List<UniversitasInitLog> findByArchiveNumber(String archiveNumber);
}
