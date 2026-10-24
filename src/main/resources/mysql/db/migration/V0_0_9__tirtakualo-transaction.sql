-- select *
-- from transaction_log
-- where trx_type = 'tirtakualo';
-- create or replace view pdam_tirtakualo_transaction as (
CREATE OR REPLACE VIEW PDAM_TIRTAKUALO_TRANSACTION AS (
  select
    log.id          as LOG_ID,
    log.branch_code as BRANCH_CODE,
    log.status      as STATUS_LOG,
    log.trx_date    as TRANSACTION_DATE,
    log.userid      as USER_ID
  from common_transaction_log log
    join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type = 'tirtakualo'
  group by log.id, log.branch_code, log.status, log.trx_date, userid);

-- create or replace view pdam_tirtakualo_transaction_detail as (
CREATE OR REPLACE VIEW PDAM_TIRTAKUALO_TRANSACTION_DETAIL AS (
  select
    trace_number                as TRACE_NUMBER,
    log.id                      as LOG_ID,
    log.branch_code             as BRANCH_CODE,
    log.status                  as STATUS_LOG,
    log.trx_date                as TRANSACTION_DATE,
    log.userid                  as USER_ID,
    group_concat(case when trx.field_name = "adminCharge"
      then trx.field_value end) as "ADMIN_CHARGE",
    group_concat(case when trx.field_name = "address"
      then trx.field_value end) as "ALAMAT",
    group_concat(case when trx.field_name = "connectionRest"
      then trx.field_value end) as "ANGSURAN_SAMBUNGAN",
    group_concat(case when trx.field_name = "archiveNumber"
      then trx.field_value end) as "ARCHIVE_NUMBER",
    group_concat(case when trx.field_name = "channelCost"
      then trx.field_value end) as "BEA_SALURAN",
    group_concat(case when trx.field_name = "connectionMonth"
      then trx.field_value end) as "BULAN_SAMBUNGAN",
    group_concat(case when trx.field_name = "currency"
      then trx.field_value end) as "CURRENCY_CODE",
    group_concat(case when trx.field_name = "group"
      then trx.field_value end) as "GOLONGAN",
    group_concat(case when trx.field_name = "price"
      then trx.field_value end) as "HARGA",
    group_concat(case when trx.field_name = "stamp"
      then trx.field_value end) as "MATERAI",
    group_concat(case when trx.field_name = "meterEnd"
      then trx.field_value end) as "METER_AKHIR",
    group_concat(case when trx.field_name = "meterStart"
      then trx.field_value end) as "METER_AWAL",
    group_concat(case when trx.field_name = "customerName"
      then trx.field_value end) as "NAMA_PELANGGAN",
    group_concat(case when trx.field_name = "customerId"
      then trx.field_value end) as "NOMOR_PELANGGAN",
    group_concat(case when trx.field_name = "nominal"
      then trx.field_value end) as "NOMINAL",
    group_concat(case when trx.field_name = "period"
      then trx.field_value end) as "PERIODE",
    group_concat(case when trx.field_name = "care"
      then trx.field_value end) as "RAWAT",
    group_concat(case when trx.field_name = "ref"
      then trx.field_value end) as "REFF",
    group_concat(case when trx.field_name = "sourceNumber"
      then trx.field_value end) as "SOURCE_NUMBER",
    group_concat(case when trx.field_name = "checkDate"
      then trx.field_value end) as "TANGGAL_CEK",
    group_concat(case when trx.field_name = "trash"
      then trx.field_value end) as "TOTAL_SAMPAH"
  from common_transaction_log log
    join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type = 'tirtakualo'
  group by trace_number, log_id, branch_code, status_log, transaction_date, user_id);