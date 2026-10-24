-- select * from transaction_log where trx_type = 'pln';
-- create or replace view pln_postpaid_transaction as (
  CREATE OR REPLACE VIEW PLN_POSTPAID_TRANSACTION AS (
  select
    log.id       as LOG_ID,
    log.branch_code AS BRANCH_CODE,
    log.status   as STATUS_LOG,
    log.trx_date as TRANSACTION_DATE,
    log.userid   as USER_ID
  from common_transaction_log log
    join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type = 'pln'
  group by log.id, log.branch_code, log.status, log.trx_date, userid);

-- create or replace view pln_postpaid_transaction_detail as (
    CREATE OR REPLACE VIEW PLN_POSTPAID_TRANSACTION_DETAIL AS (
  select
    trace_number                as TRACE_NUMBER,
    log.id                      as LOG_ID,
    log.branch_code             as BRANCH_CODE,
    log.status                  as STATUS_LOG,
    log.trx_date                as TRANSACTION_DATE,
    log.userid                  as USER_ID,
    group_concat(case when trx.field_name = "productCode"
      then trx.field_value end) as "PRODUCT_CODE",
    group_concat(case when trx.field_name = "customerId"
      then trx.field_value end) as "CUSTOMER_ID",
    group_concat(case when trx.field_name = "customerName"
      then trx.field_value end) as "CUSTOMER_NAME",
    group_concat(case when trx.field_name = "billNominal"
      then trx.field_value end) as "BILL_NOMINAL",
    group_concat(case when trx.field_name = "adminCharge"
      then trx.field_value end) as "ADMIN_CHARGE",
    group_concat(case when trx.field_name = "billRefferenceOne"
      then trx.field_value end) as "BILL_REF_ONE",
    group_concat(case when trx.field_name = "billRefferenceTwo"
      then trx.field_value end) as "BILL_REF_TWO",
    group_concat(case when trx.field_name = "footer"
      then trx.field_value end) as "FOOTER",
    group_concat(case when trx.field_name = "sourceNumber"
      then trx.field_value end) as "SOURCE_NUMBER",
    group_concat(case when trx.field_name = "archiveNumber"
      then trx.field_value end) as "ARCHIVE_NUMBER"
  from common_transaction_log log
    join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type = 'pln'
  group by trace_number, log_id, branch_code, status_log, transaction_date, user_id);