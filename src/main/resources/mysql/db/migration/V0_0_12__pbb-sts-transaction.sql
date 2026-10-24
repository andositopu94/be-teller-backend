-- select *
-- from transaction_log
-- where trx_type = 'pbb';
-- universitas_transaction
CREATE OR REPLACE VIEW PBB_STS_TRANSACTION AS (
  select
    log.id          as LOG_ID,
    log.branch_code as BRANCH_CODE,
    log.status      as STATUS_LOG,
    log.trx_date    as TRANSACTION_DATE,
    log.userid      as USER_ID
  from common_transaction_log log
    join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type = 'pbb'
  group by log.id, log.branch_code, log.status, log.trx_date, userid);

-- universitas_transaction_detail
CREATE OR REPLACE VIEW PBB_STS_TRANSACTION_DETAIL AS (
  select
    trace_number                as TRACE_NUMBER,
    log.id                      as LOG_ID,
    log.branch_code             as BRANCH_CODE,
    log.status                  as STATUS_LOG,
    log.trx_date                as TRANSACTION_DATE,
    log.userid                  as USER_ID,
    group_concat(case when trx.field_name = "alamat"
      then trx.field_value end) as "ALAMAT",
    group_concat(case when trx.field_name = "archiveNumber"
      then trx.field_value end) as "ARCHIVE_NUMBER",
    group_concat(case when trx.field_name = "adminCharge"
      then trx.field_value end) as "BIAYA_ADMIN",
    group_concat(case when trx.field_name = "denda"
      then trx.field_value end) as "DENDA",
    group_concat(case when trx.field_name = "jumlahPokok"
      then trx.field_value end) as "JUMLAH_POKOK",
    group_concat(case when trx.field_name = "mataAnggaran"
      then trx.field_value end) as "MATA_ANGGARAN",
    group_concat(case when trx.field_name = "mataUang"
      then trx.field_value end) as "MATA_UANG",
    group_concat(case when trx.field_name = "namaWp"
      then trx.field_value end) as "NAMA_WP",
    group_concat(case when trx.field_name = "noArsip"
      then trx.field_value end) as "NO_ARSIP",
    group_concat(case when trx.field_name = "noSKP"
      then trx.field_value end) as "NO_SKP",
    group_concat(case when trx.field_name = "noSts"
      then trx.field_value end) as "NO_STS",
    group_concat(case when trx.field_name = "noWp"
      then trx.field_value end) as "NO_WP",
    group_concat(case when trx.field_name = "nominal"
      then trx.field_value end) as "NOMINAL",
    group_concat(case when trx.field_name = "ref"
      then trx.field_value end) as "REFF",
    group_concat(case when trx.field_name = "rekeningSumber"
      then trx.field_value end) as "REKENING_SUMBER",
    group_concat(case when trx.field_name = "rekeningTujuan"
      then trx.field_value end) as "REKENING_TUJUAN",
    group_concat(case when trx.field_name = "sourceName"
      then trx.field_value end) as "SOURCE_NAME",
    group_concat(case when trx.field_name = "uraian"
      then trx.field_value end) as "URAIAN"
  from common_transaction_log log
    join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type = 'pbb'
  group by trace_number, log_id, branch_code, status_log, transaction_date, user_id);