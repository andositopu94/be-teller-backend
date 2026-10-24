package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.CommonTransactionRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommonTransactionRequestLogRepository extends JpaRepository<CommonTransactionRequestLog, Long> {
}
