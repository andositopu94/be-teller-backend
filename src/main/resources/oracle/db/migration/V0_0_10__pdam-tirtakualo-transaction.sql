CREATE OR REPLACE VIEW pdam_tirtakualo_transaction AS (
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
  WHERE trx.TRX_TYPE = 'tirtakualo'
  GROUP BY log.ID, TRACE_NUMBER, BRANCH_CODE, STATUS, TRX_DATE, USERID);

CREATE OR REPLACE VIEW pdam_tirtakualo_transaction_detail AS (
  SELECT *
  FROM (
    SELECT
      TRACE_NUMBER AS trace_number,
      BRANCH_CODE  AS branch_code,
      STATUS       AS status_log,
      FIELD_NAME,
      FIELD_VALUE,
      TRX_DATE     AS transaction_date,
      USERID       AS user_id
    FROM TRANSACTION_LOG trx
      JOIN COMMON_TRANSACTION_LOG log
        ON trx.COMMON_TRANSACTION_ID = log.ID
    WHERE trx.TRX_TYPE = 'tirtakualo'
  )
    PIVOT (
      listagg(FIELD_VALUE, ', ')
      WITHIN GROUP (
        ORDER BY FIELD_VALUE)
      FOR
        FIELD_NAME
      IN (
        'adminCharge' AS admin_charge,
        'address' AS alamat,
        'connectionInstallment' AS angsuran_sambungan,
        'archiveNumber' AS archive_number,
        'channelCost' AS bea_saluran,
        'connectionMonth' AS bulan_sambungan,
        'currency' AS currency_code,
        'group' AS golongan,
        'price' AS harga,
        'stamp' AS materai,
        'meterStart' AS meter_akhir,
        'meterEnd' AS meter_awal,
        'sourceName' AS nama_pelanggan,
        'customerId' AS nomor_pelanggan,
        'nominal' AS nominal,
        'period' AS periode,
        'care' AS rawat,
        'ref' AS reff,
        'sourceNumber' AS source_number,
        'checkDate' AS tanggal_cek,
        'trash' AS total_sampah
      ))
);
