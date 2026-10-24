package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.TransactionRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRequestLogRepository extends JpaRepository<TransactionRequestLog, Long> {
    List<TransactionRequestLog> findByFieldNameAndFieldValueAndTrxType(String fieldName, String fieldValue, String trxType);

    List<TransactionRequestLog> findByFieldNameAndFieldValueAndTrxTypeAndTraceNumber(String fieldName, String fieldValue, String trxType, String traceNumber);

    List<TransactionRequestLog> findByCommonTransactionRequestId(Long id);

    TransactionRequestLog findFirstByFieldNameAndFieldValueAndTraceNumber(String name, String value, String trace);

    TransactionRequestLog findByFieldNameAndFieldValueAndCommonTransactionRequestStatus(String fieldName, String fieldValue, Integer status);

    TransactionRequestLog findByCommonTransactionRequestIdAndFieldName(Long id, String s);

    List<TransactionRequestLog> findByFieldNameAndFieldValueAndTrxTypeAndCommonTransactionRequestUseridOrderByCommonTransactionRequestIdDesc(String fieldName, String fieldValue, String trxType, String userId);

    List<TransactionRequestLog> findByFieldNameAndFieldValueAndTrxTypeAndTraceNumberAndCommonTransactionRequestUseridOrderByCommonTransactionRequestIdDesc(String fieldName, String fieldValue, String trxType, String traceNumber, String userId);

    @Query(value = "SELECT trl.*, ctrl.* FROM transaction_request_log trl INNER JOIN common_transaction_request_log ctrl ON trl.common_transaction_request_id = ctrl.id WHERE TRUNC(ctrl.trx_date) = TO_DATE (:date, 'yyyymmdd') AND trl.field_name = :fieldName AND trl.field_value = :fieldValue AND trl.trx_type = :trxType AND ctrl.userid = :userId ORDER BY ctrl.id DESC", nativeQuery = true)
    List<TransactionRequestLog> findDateWithoutTrace(@Param("fieldName") String fieldName, @Param("fieldValue") String fieldValue, @Param("trxType") String trxType, @Param("userId") String userId, @Param("date") String dt);

    @Query(value = "SELECT trl.*, ctrl.* FROM transaction_request_log trl INNER JOIN common_transaction_request_log ctrl ON trl.common_transaction_request_id = ctrl.id WHERE TRUNC(ctrl.trx_date) = TO_DATE (:date, 'yyyymmdd') AND trl.field_name = :fieldName AND trl.field_value = :fieldValue AND trl.trx_type = :trxType AND ctrl.userid = :userId AND trl.TRACE_NUMBER = :traceNumber ORDER BY ctrl.id DESC", nativeQuery = true)
    List<TransactionRequestLog> findDateWithTrace(@Param("fieldName") String fieldName, @Param("fieldValue") String fieldValue, @Param("trxType") String trxType, @Param("userId") String userId, @Param("date") String dt, @Param("traceNumber") String traceNumber);
    
    @Query(value = "SELECT DISTINCT common_transaction_request_id " +
    				"FROM transaction_request_log " +
    				"WHERE trace_number = :trace AND field_name = 'ref' AND field_value = :ref", nativeQuery = true)
    Long findCommonIdFromTraceNumber(@Param("trace") String trace, @Param("ref") String ref);

    @Modifying
    @Query("update TransactionRequestLog set fieldValue = :fieldValue where fieldName = :fieldName and traceNumber = :traceNumber")
    void updateByFieldNameAndTraceNumber(@Param("fieldValue") String fieldValue,
                                         @Param("fieldName") String fieldName,
                                         @Param("traceNumber") String traceNumber);
}
