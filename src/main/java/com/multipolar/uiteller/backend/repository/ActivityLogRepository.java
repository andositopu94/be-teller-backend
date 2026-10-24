package com.multipolar.uiteller.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multipolar.uiteller.backend.model.ActivityLog;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}
