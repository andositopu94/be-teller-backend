package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.CommonTransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommonTransactionLogRepository extends JpaRepository<CommonTransactionLog, Long> {
    @Query(value = "SELECT userid, branch_code FROM common_transaction_log GROUP BY userid, branch_code", nativeQuery = true)
    Object[] findUserid();
}
