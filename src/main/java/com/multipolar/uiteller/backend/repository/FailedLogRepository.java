package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.CommonTransactionRequestLog;
import com.multipolar.uiteller.backend.model.FailedLogMonitoring;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedLogRepository extends JpaRepository<FailedLogMonitoring, Long> {
    FailedLogMonitoring findByCommonTransactionLog_Id(Long id);

    FailedLogMonitoring findByCommonTransactionRequestLog(CommonTransactionRequestLog commonTransactionRequestLog);
}
