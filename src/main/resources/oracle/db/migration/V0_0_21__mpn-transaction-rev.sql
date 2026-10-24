CREATE OR REPLACE VIEW mpn_transaction_rev AS (
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
  WHERE trx.TRX_TYPE = 'dja' OR trx.TRX_TYPE = 'djp' OR trx.TRX_TYPE = 'djbc' OR (trx.TRX_TYPE = 'djaReinquiry' AND RC = '00') OR
        (trx.TRX_TYPE = 'djpReinquiry' AND RC = '00') OR (trx.TRX_TYPE = 'djbcReinquiry' AND RC = '00')
  GROUP BY log.ID, TRACE_NUMBER, BRANCH_CODE, STATUS, TRX_DATE, USERID);

CREATE OR REPLACE VIEW mpn_transaction_detail_rev AS (
  SELECT *
  FROM (
    SELECT
      TRACE                    AS trace_number,
      BRANCH_CODE              AS branch_code,
      rev.MESSAGE              AS message,
      STATUS                   AS status_log,
      FIELD_NAME,
      FIELD_VALUE,
      CREATED_AT               AS transaction_date,
      TELLER                   AS user_id,
      RCODE                    AS rc,
      'GAGAL ERROR CORRECTION' AS sts,
      TRX_TYPE
    FROM TRANSACTION_LOG trx
      JOIN REVERSE_HISTORY rev ON trx.COMMON_TRANSACTION_ID = rev.COMMON_ID
      JOIN COMMON_TRANSACTION_LOG log ON trx.COMMON_TRANSACTION_ID = log.ID
    WHERE trx.TRX_TYPE = 'dja' OR trx.TRX_TYPE = 'djp' OR trx.TRX_TYPE = 'djbc' OR (trx.TRX_TYPE = 'djaReinquiry' AND RC = '00') OR
          (trx.TRX_TYPE = 'djpReinquiry' AND RC = '00') OR (trx.TRX_TYPE = 'djbcReinquiry' AND RC = '00')
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
        'taxNumber' AS npwp
      ))
);

CREATE OR REPLACE VIEW mpn_transaction_detail AS (
  SELECT *
  FROM MPN_TRANSACTION_DETAILS
  UNION
  SELECT *
  FROM MPN_TRANSACTION_DETAIL_REV
);
