-- universitas_transaction
CREATE OR REPLACE VIEW UNIVERSITAS_TRANSACTION AS (
  select
    log.id          as LOG_ID,
    log.branch_code as BRANCH_CODE,
    log.status      as STATUS_LOG,
    log.trx_date    as TRANSACTION_DATE,
    log.userid      as USER_ID
  from common_transaction_log log
    join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type = 'university'
  group by log.id, log.branch_code, log.status, log.trx_date, userid);

-- universitas_transaction_detail
CREATE OR REPLACE VIEW UNIVERSITAS_TRANSACTION_DETAIL AS (
  select
    trace_number                as TRACE_NUMBER,
    log.id                      as LOG_ID,
    log.branch_code             as BRANCH_CODE,
    log.status                  as STATUS_LOG,
    log.trx_date                as TRANSACTION_DATE,
    log.userid                  as USER_ID,
    group_concat(case when trx.field_name = "adminCharge"
      then trx.field_value end) as "ADMIN_CHARGE",
    group_concat(case when trx.field_name = "archiveNumber"
      then trx.field_value end) as "ARCHIVE_NUMBER",
    group_concat(case when trx.field_name = "bill"
      then trx.field_value end) as "BILL",
    group_concat(case when trx.field_name = "currency"
      then trx.field_value end) as "CURRENCY_CODE",
    group_concat(case when trx.field_name = "major"
      then trx.field_value end) as "MAJOR",
    group_concat(case when trx.field_name = "name"
      then trx.field_value end) as "NAME",
    group_concat(case when trx.field_name = "nim"
      then trx.field_value end) as "NIM",
    group_concat(case when trx.field_name = "nominal"
      then trx.field_value end) as "NOMINAL",
    group_concat(case when trx.field_name = "penalties"
      then trx.field_value end) as "PENALTIES",
    group_concat(case when trx.field_name = "ref"
      then trx.field_value end) as "REFF",
    group_concat(case when trx.field_name = "semester"
      then trx.field_value end) as "SEMESTER",
    group_concat(case when trx.field_name = "sourceNumber"
      then trx.field_value end) as "SOURCE_NUMBER",
    group_concat(case when trx.field_name = "totalPayment"
      then trx.field_value end) as "TOTAL_PAYMENT"
  from common_transaction_log log
    join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type = 'university'
  group by trace_number, log_id, branch_code, status_log, transaction_date, user_id);