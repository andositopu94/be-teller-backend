package com.multipolar.uiteller.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multipolar.uiteller.backend.model.HttpRequestLog;

public interface HttpRequestLogRepository extends JpaRepository<HttpRequestLog, Long> {
}
