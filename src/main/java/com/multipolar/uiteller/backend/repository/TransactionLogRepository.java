package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {

    List<TransactionLog> findByFieldNameAndFieldValueAndTrxType(String fieldName, String fieldValue, String trxType);

    TransactionLog findFirstByFieldNameAndFieldValueAndTrxType(String fieldName, String fieldValue, String trxType);

    TransactionLog findFirstByFieldNameAndFieldValueAndTrxTypeAndTraceNumber(String fieldName, String fieldValue, String trxType, String trace);

    List<TransactionLog> findByFieldNameAndFieldValueAndTrxTypeAndTraceNumber(String fieldName, String fieldValue, String trxType, String traceNumber);

    List<TransactionLog> findByCommonTransactionId(Long id);

    TransactionLog findByCommonTransactionIdAndFieldNameAndFieldValue(Long id, String fieldName, String fieldValue);

    List<TransactionLog> findByFieldNameAndFieldValueAndCommonTransactionTrxDateBetween(String fieldName, String fieldValue, Date startDate, Date endDate);

    @Query(value = "select * from transaction_log tl " +
            "where field_name = 'settlementDate' and field_value = :settlementDate " +
            "and common_transaction_id in " +
            "( " +
            "select tl.common_transaction_id " +
            "from transaction_log tl " +
            "left outer join common_transaction_log ctl on tl.common_transaction_id=ctl.id " +
            "where tl.field_name='ntpn' " +
            "and tl.trx_type NOT LIKE '%Reinquiry' " +
            "and (TRIM(tl.field_value)='' or TRIM(tl.field_value) IS NULL) " +
            "and ctl.rc = :rc " +
            "and ctl.userid = :user " +
            ") ",
            nativeQuery = true)
    List<TransactionLog> findTrxWithoutNtpn(@Param("settlementDate") String settlementDate,
                                            @Param("user") String user, @Param("rc") String rc);

    @Query(value = "select * from transaction_log tl " +
            "where field_name = 'settlementDate' and field_value = :settlementDate " +
            "and common_transaction_id in " +
            "( " +
            "select tl.common_transaction_id " +
            "from transaction_log tl " +
            "left outer join common_transaction_log ctl on tl.common_transaction_id=ctl.id " +
            "where tl.field_name='ntpn' " +
            "and tl.trx_type NOT LIKE '%Reinquiry' " +
            "and (TRIM(tl.field_value)='' or TRIM(tl.field_value) IS NULL) " +
            "and ctl.rc='08' " +
            "and ctl.userid = :user " +
            ") ",
            nativeQuery = true)
    List<TransactionLog> findTrxMpnG3WithoutNtpn(@Param("settlementDate") String settlementDate,
                                                 @Param("user") String user);

    @Query(value = "select * from transaction_log tl " +
            "where field_name = 'settlementDate' and field_value in (:settlementDate, :strMinusOne, :strMinusTwo) " +
            "and common_transaction_id in " +
            "( " +
            "select tl.common_transaction_id " +
            "from transaction_log tl " +
            "left outer join common_transaction_log ctl on tl.common_transaction_id=ctl.id " +
            "where tl.field_name='ntpn' " +
            "and tl.trx_type NOT LIKE '%Reinquiry' " +
            "and (TRIM(tl.field_value)='' or TRIM(tl.field_value) IS NULL) " +
            "and ctl.rc='08' " +
            "and ctl.userid = :user " +
            ")",
            nativeQuery = true)
    List<TransactionLog> findAllTrxMpnG3WithoutNtpn(@Param("settlementDate") String settlementDate,
                                                    @Param("strMinusOne") String strMinusOne,
                                                    @Param("strMinusTwo") String strMinusTwo,
                                                    @Param("user") String user);
    @Query(value = "select * from transaction_log tl " +
            "where field_name = 'settlementDate' and field_value = :settlementDate " +
            "and common_transaction_id in " +
            "( " +
            "select tl.common_transaction_id " +
            "from transaction_log tl " +
            "left outer join common_transaction_log ctl on tl.common_transaction_id=ctl.id " +
            "where tl.field_name in ('ntpn', 'billingCode') " +
            "and tl.trx_type NOT LIKE '%Reinquiry' " +
            "and (TRIM(tl.field_value)='' or TRIM(tl.field_value) IS NULL) " +
            "and ctl.rc='08' " +
            "and ctl.userid = :user " +
            ")",
            nativeQuery = true)
    List<TransactionLog> findAllTrxMpnG3WithoutNtpnByDate(@Param("user") String user,
                                                               @Param("settlementDate") String settlementDate);

    @Query(value = "select tl.id, tl.common_transaction_id, tl.field_name, tl.field_value, tl.trace_number, tl.trx_type " +
            "from transaction_log tl inner join common_transaction_log ctl on tl.common_transaction_id = ctl.id " +
            "where trx_type like '%Reinquiry' and ctl.status = 0 and tl.field_name = 'billingCode' " +
            "and tl.field_value in (:billingCodes)",
            nativeQuery = true)
    List<TransactionLog> findSuccessfulMpnReinquiry(@Param("billingCodes") List<String> billingCodes);

    TransactionLog findByFieldNameAndFieldValueAndCommonTransactionStatus(String fieldName, String fieldValue, Integer status);

    TransactionLog findByCommonTransactionIdAndAndFieldName(Long id, String s);

    TransactionLog findByTraceNumberAndFieldNameAndFieldValue(String s, String n, String v);

    TransactionLog findFirstByTraceNumberAndFieldNameAndFieldValueOrderByIdDesc(String s, String n, String v);

    TransactionLog findFirstByFieldNameAndFieldValueAndTrxTypeOrderByIdDesc(String n, String v, String t);

    @Query(value = "select tl.id, tl.common_transaction_id, tl.field_name, tl.field_value, tl.trace_number, tl.trx_type " +
            "from transaction_log tl inner join common_transaction_log ctl on tl.common_transaction_id = ctl.id " +
            "where trx_type in ('dja', 'djp', 'djbc') and ctl.rc in ('02', '27', '88') and tl.field_name = 'billingCode' AND ctl.userid = :userId "
            + "AND TRUNC(ctl.trx_date) = TRUNC(sysdate)"
            , nativeQuery = true)
    List<TransactionLog> findForErrorCorrection(@Param("userId") String userId);

    @Query(value = "select tl.id, tl.common_transaction_id, tl.field_name, tl.field_value, tl.trace_number, tl.trx_type " +
            "from transaction_log tl inner join common_transaction_log ctl on tl.common_transaction_id = ctl.id " +
            "where trx_type in ('dja', 'djp', 'djbc') and ctl.rc in ('02', '27', '88') and tl.field_name = 'billingCode' AND ctl.userid = :userId "
            + "AND TRUNC(ctl.trx_date) = TRUNC(TO_DATE(:start, 'DD/MM/YYYY')) AND ctl.BRANCH_CODE = :brc"
            , nativeQuery = true)
    List<TransactionLog> findForErrorCorrectionTrx(@Param("userId") String userId, @Param("start") String start, @Param("brc") String brc);

    @Query(value = "SELECT DISTINCT commonTransaction.id " +
            "FROM TransactionLog " +
            "WHERE trxType in ('dja', 'djp', 'djbc', 'djag3', 'djpg3', 'djbcg3') and traceNumber = :trace "
            + "AND fieldName = 'ref' AND fieldValue = :ref")
    List<Long> findCommonIdFromTraceNumber(@Param("trace") String trace, @Param("ref") String ref);

    @Query(value = "SELECT DISTINCT commonTransaction.id " +
                   "FROM TransactionLog " +
                   "WHERE trxType = 'bifast' and traceNumber = :trace "
                   + "AND fieldName = 'ref' AND fieldValue = :ref")
    List<Long> findCommonIdBifast(@Param("trace") String trace, @Param("ref") String ref);

    @Query(value = "select * from transaction_log tl " +
            "inner join common_transaction_log ctl on tl.common_transaction_id = ctl.id " +
            "where tl.trx_type in ('dja', 'djp', 'djbc', 'djag3', 'djpg3', 'djbcg3') " +
            "and ctl.rc = '08' " +
            "and tl.field_name = 'billingCode' " +
            "and tl.field_value = :billingCode " +
            //"and ctl.trx_date <= to_timestamp(:datetime, 'YYYY-MM-DD HH24:MI:SS') " +
            "and (TRUNC(ctl.trx_date) = TRUNC(CURRENT_DATE) or TRUNC(ctl.trx_date) = TRUNC(CURRENT_DATE- 1 ))", nativeQuery = true)
    List<TransactionLog>  findInAbnormalOrUnsettle(@Param("billingCode") String billingCode);

    TransactionLog findFirstByFieldNameAndFieldValueAndTraceNumberAndCommonTransactionUserid(String name, String value, String trace, String user);

    TransactionLog findFirstByFieldNameAndFieldValueAndTraceNumber(String name, String value, String traceNumber);

    @Query(value = "select t.field_name from transaction_log t where t.common_transaction_id= :id", nativeQuery = true)
    Object[] findFieldName(@Param("id") Long id);

    @Query(value = "select tl.* from transaction_log tl " +
            "inner join COMMON_TRANSACTION_LOG ctl on tl.COMMON_TRANSACTION_ID = ctl.id " +
            "where tl.trace_number = :trace " +
            "and to_char(ctl.TRX_DATE, 'DDMMYYYY') = :trxDate", nativeQuery = true)
    List<TransactionLog> findTransactionByTraceAndDate(@Param("trace") String trace, @Param("trxDate") String trxDate);
    
    @Query(value = "select * from transaction_log tl " +
            "where field_name = 'settlementDate' and field_value = :settlementDate " +
            "and common_transaction_id in " +
            "( " +
            "select tl.common_transaction_id " +
            "from transaction_log tl " +
            "left outer join common_transaction_log ctl on tl.common_transaction_id=ctl.id " +
            "where tl.field_name='ntpn' " +
            "and tl.trx_type NOT LIKE '%Reinquiry' " +
            "and tl.trx_type NOT LIKE '%g3%' " +
            "and (TRIM(tl.field_value)='' or TRIM(tl.field_value) IS NULL) " +
            "and ctl.rc in ('08', '90') " +
            ") ",
            nativeQuery = true)
    List<TransactionLog> findTrxWithoutNtpnAndUser(@Param("settlementDate") String settlementDate);
    
    @Query(value="select * from transaction_log tl where field_name='settlementDate' and common_transaction_id in (select tl.common_transaction_id from transaction_log tl left outer join suspect_log_monitoring slm on tl.common_transaction_id=slm.common_transaction_log_id where slm.id=:id)", nativeQuery=true)
    List<TransactionLog> findTrxBySuspectId(@Param("id") Long id);

    List<TransactionLog> findByTraceNumberAndTrxType(String traceNumber, String trxType);

    @Modifying
    @Query("update TransactionLog set fieldValue = :fieldValue where fieldName = :fieldName and traceNumber = :traceNumber")
    void updateByFieldNameAndTraceNumber(@Param("fieldValue") String fieldValue,
                                                   @Param("fieldName") String fieldName,
                                                   @Param("traceNumber") String traceNumber);

    @Query(value="select tl.* from common_transaction_log ctl\n" +
            "inner join transaction_log tl\n" +
            "on ctl.id = tl.common_transaction_id\n" +
            "where tl.trx_type in ('bpihinitial' )\n" +
            "and trunc(sysdate) = trunc(ctl.trx_date)\n" +
            "and tl.field_name = 'validationNumber'\n" +
            "and not exists (\n" +
            "    select tl_upload.common_transaction_id from transaction_log tl_upload\n" +
            "    where tl_upload.field_name = 'pdfUpload'\n" +
            "    and tl_upload.common_transaction_id in (\n" +
            "        select distinct ctl_today_trx.id from common_transaction_log ctl_today_trx\n" +
            "            inner join transaction_log tl_today_trx\n" +
            "            on ctl_today_trx.id = tl_today_trx.common_transaction_id\n" +
            "            where tl_today_trx.trx_type = 'bpihinitial'\n" +
            "            and trunc(sysdate) = trunc (ctl_today_trx.trx_date)\n" +
            "            and ctl_today_trx.id = ctl.id))", nativeQuery=true)
    List<TransactionLog> findTodayValidationNumberNotUploaded();

    @Query(value="select tl.* from common_transaction_log ctl" +
            "    inner join transaction_log tl" +
            "    on ctl.id = tl.common_transaction_id" +
            "    where tl.trx_type in ('bpihinitial' )" +
            "    and trunc(sysdate) = trunc(ctl.trx_date)" +
            "    and tl.field_name = 'sourceNumber'", nativeQuery=true)
    List<TransactionLog> findAllTodayBpihSourceNumber();

    @Query(value="select tl.* from common_transaction_log ctl" +
            "    inner join transaction_log tl" +
            "    on ctl.id = tl.common_transaction_id" +
            "    where tl.trx_type in ('bpihinitial' )" +
            "    and trunc(?1) = trunc(ctl.trx_date)" +
            "    and tl.field_name = 'sourceNumber'", nativeQuery=true)
    List<TransactionLog> findByDate(Date date);
    @Query(value ="select count (*) from "+
            "(select * from transaction_log where (field_name = 'ref'))  refAja, "+
            "(select * from transaction_log where (field_name = 'billingCode')) billingAja "+
            "where billingAja.common_transaction_id=refAja.common_transaction_id "+
            "and refAja.field_value =:ref "+
            "and billingAja.field_value = :idBilling",
            nativeQuery = true)
    Integer findByRefAndIdBil(@Param("ref") String ref,
                              @Param("idBilling") String idBilling);
    @Query(value= "select trx_type from transaction_log where field_value = :ref AND field_name = 'ref'" , nativeQuery = true)
    String[] findByRef(@Param("ref") String ref);

    @Query(value = "select * from transaction_log tl " +
            "where field_name = :fieldName and field_value = :fieldValue " +
            "and trx_type like :trxType ",
            nativeQuery = true)
    List<TransactionLog> findFirstByFieldNameAndFieldValueAndTrxTypeLike(@Param("fieldName") String fieldName,
                                                                         @Param("fieldValue") String fieldValue, @Param("trxType") String trxType);

    @Query(value = "select * from transaction_log tl where common_transaction_id" +
            " in ( select tl.common_transaction_id from transaction_log tl" +
            " left outer join common_transaction_log ctl on tl.common_transaction_id=ctl.id" +
            " where tl.trx_type in ('bifast' ) and ctl.rc='08' and ctl.userid=:user) ",
            nativeQuery = true)
    List<TransactionLog> findAllTrxBifast(@Param("user") String username);
}
