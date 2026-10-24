-- select *
-- from transaction_log
-- where trx_type = 'tirtanadi';
-- create or replace view pdam_tirtanadi_transaction as (
CREATE OR REPLACE VIEW PDAM_TIRTANADI_TRANSACTION AS (
  select
    log.id       as LOG_ID,
    log.branch_code BRANCH_CODE,
    log.status   as STATUS_LOG,
    log.trx_date as TRANSACTION_DATE,
    log.userid   as USER_ID
  from common_transaction_log log
    join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type = 'tirtanadi'
  group by log.id, log.branch_code, log.status, log.trx_date, userid);

-- create or replace view pdam_tirtanadi_transaction_detail as (
CREATE OR REPLACE VIEW PDAM_TIRTANADI_TRANSACTION_DETAIL AS (
  select
    trace_number                as TRACE_NUMBER,
    log.id                      as LOG_ID,
    log.branch_code             as BRANCH_CODE,
    log.status                  as STATUS_LOG,
    log.trx_date                as TRANSACTION_DATE,
    log.userid                  as USER_ID,
    group_concat(case when trx.field_name = "billAccount"
      then trx.field_value end) as "BILL_ACCOUNT",
    group_concat(case when trx.field_name = "billAmount"
      then trx.field_value end) as "BILL_AMOUNT",
    group_concat(case when trx.field_name = "billInfo"
      then trx.field_value end) as "BILL_INFO",
    group_concat(case when trx.field_name = "billPeriod"
      then trx.field_value end) as "BILL_PERIOD",
    group_concat(case when trx.field_name = "billReport"
      then trx.field_value end) as "BILL_REPORT",
    group_concat(case when trx.field_name = "cubication"
      then trx.field_value end) as "CUBICATION",
    group_concat(case when trx.field_name = "currency"
      then trx.field_value end) as "CURRENCY_CODE",
    group_concat(case when trx.field_name = "customerAddress"
      then trx.field_value end) as "CUSTOMER_ADDRESS",
    group_concat(case when trx.field_name = "customerId"
      then trx.field_value end) as "CUSTOMER_ID",
    group_concat(case when trx.field_name = "customerName"
      then trx.field_value end) as "CUSTOMER_NAME",
    group_concat(case when trx.field_name = "groupRate"
      then trx.field_value end) as "GROUP_RATE",
    group_concat(case when trx.field_name = "nominal"
      then trx.field_value end) as "NOMINAL",
    group_concat(case when trx.field_name = "penalties"
      then trx.field_value end) as "PENALTIES",
    group_concat(case when trx.field_name = "ref"
      then trx.field_value end) as "REFF",
    group_concat(case when trx.field_name = "referenceNumber"
      then trx.field_value end) as "REFERENCE_NUMBER",
    group_concat(case when trx.field_name = "sourceNumber"
      then trx.field_value end) as "SOURCE_NUMBER",
    group_concat(case when trx.field_name = "adminCharge"
      then trx.field_value end) as "ADMIN_CHARGE",
    group_concat(case when trx.field_name = "archiveNumber"
      then trx.field_value end) as "ARCHIVE_NUMBER"
  from common_transaction_log log
    join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type = 'tirtanadi'
  group by trace_number, log_id, branch_code, status_log, transaction_date, user_id);