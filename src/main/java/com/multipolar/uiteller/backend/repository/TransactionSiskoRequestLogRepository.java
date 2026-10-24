package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.TransactionSiskoRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionSiskoRequestLogRepository extends JpaRepository<TransactionSiskoRequestLog, Long> {
    List<TransactionSiskoRequestLog> findByFieldNameAndFieldValueAndTrxTypeOrderByCommonTransactionSiskoRequestLogIdDesc(String fieldName, String fieldValue, String trxType);

    List<TransactionSiskoRequestLog> findByFieldNameAndFieldValueAndTrxTypeAndTraceNumberOrderByCommonTransactionSiskoRequestLogIdDesc(String fieldName, String fieldValue, String trxType, String traceNumber);

    List<TransactionSiskoRequestLog> findByCommonTransactionSiskoRequestLogId(Long id);

    TransactionSiskoRequestLog findByFieldNameAndFieldValueAndCommonTransactionSiskoRequestLogStatus(String fieldName, String fieldValue, Integer status);

    TransactionSiskoRequestLog findByCommonTransactionSiskoRequestLogIdAndFieldName(Long id, String s);
}
