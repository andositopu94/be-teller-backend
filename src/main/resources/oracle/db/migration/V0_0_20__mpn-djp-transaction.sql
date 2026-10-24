CREATE OR REPLACE VIEW mpn_djp_transaction AS (
  SELECT
    log.ID       AS log_id,
    TRACE_NUMBER AS trace_number,
    BRANCH_CODE  AS branch_code,
    STATUS       AS status_log,
    TRX_DATE     AS transaction_date,
    USERID       AS user_id
  FROM TRANSACTION_LOG trx
    JOIN COMMON_TRANSACTION_LOG log
      ON trx.COMMON_TRANSACTION_ID = log.ID
  WHERE trx.TRX_TYPE = 'djp' OR (trx.TRX_TYPE = 'djpReinquiry' AND RC = '00')
  GROUP BY log.ID, TRACE_NUMBER, BRANCH_CODE, STATUS, TRX_DATE, USERID);

CREATE OR REPLACE VIEW mpn_djp_transaction_detail AS (
  SELECT *
  FROM (
    SELECT
      TRACE_NUMBER                AS trace_number,
      BRANCH_CODE                 AS branch_code,
      MESSAGE                     AS message,
      STATUS                      AS status_log,
      FIELD_NAME,
      FIELD_VALUE,
      TRX_DATE                    AS transaction_date,
      USERID                      AS user_id,
      RC                          AS rc,
      CASE WHEN RC = '00'
        THEN 'SETTLEMENT'
      WHEN RC = '90'
        THEN 'UNSETTLEMENT'
      WHEN RC = '08'
        THEN 'ABNORMAL'
      WHEN (RC LIKE 'R%' AND (RC != 'R0' AND RC != 'R98'))
        THEN 'ERROR CORRECTION'
      WHEN (RC = 'R0' OR RC = 'R98')
        THEN 'SUCCESS ERROR CORRECTION'
      ELSE 'UNCATEGORIZED' END AS sts,
      TRX_TYPE
    FROM TRANSACTION_LOG trx
      JOIN COMMON_TRANSACTION_LOG log
        ON trx.COMMON_TRANSACTION_ID = log.ID
    WHERE trx.TRX_TYPE = 'djp' OR (trx.TRX_TYPE = 'djpReinquiry' AND RC = '00')
  )
    PIVOT (
      listagg(FIELD_VALUE, ', ')
      WITHIN GROUP (
        ORDER BY FIELD_VALUE)
      FOR
        FIELD_NAME
      IN (
        'settlementDate' AS settlement_date,
        'billingCode' AS billing_code,
        'taxName' AS tax_name,
        'payCustomerName' AS customer_name,
        'ntpn' AS ntpn,
        'archiveNumber' AS archive_number,
        'nominal' AS nominal,
        'ref' AS ntb,
        'taxNumber' AS npwp,
        'taxAddress' AS tax_address,
        'account' AS tax_type,
        'depositCode' AS deposit_type,
        'taxPeriod' AS tax_period,
        'skNumber' AS sk_number
      ))
);
