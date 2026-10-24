-- select *
-- from transaction_log
-- where trx_type = 'nontaglist';
-- create or replace view pln_nontaglist_transaction as (
CREATE OR REPLACE VIEW PLN_NONTAGLIST_TRANSACTION AS (
  select
    log.id          as LOG_ID,
    log.branch_code as BRANCH_CODE,
    log.status      as STATUS_LOG,
    log.trx_date    as TRANSACTION_DATE,
    log.userid      as USER_ID
  from common_transaction_log log
    join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type = 'nontaglist'
  group by log.id, log.branch_code, log.status, log.trx_date, userid);

-- create or replace view pln_nontaglist_transaction_detail as (
CREATE OR REPLACE VIEW PLN_NONTAGLIST_TRANSACTION_DETAIL AS (
  select
    trace_number                as TRACE_NUMBER,
    log.id                      as LOG_ID,
    log.branch_code             as BRANCH_CODE,
    log.status                  as STATUS_LOG,
    log.trx_date                as TRANSACTION_DATE,
    log.userid                  as USER_ID,
    group_concat(case when trx.field_name = "productCode"
      then trx.field_value end) as "PRODUCT_CODE",
    group_concat(case when trx.field_name = "registrationNumber"
      then trx.field_value end) as "REGISTRATION_NUMBER",
    group_concat(case when trx.field_name = "customerName"
      then trx.field_value end) as "CUSTOMER_NAME",
    group_concat(case when trx.field_name = "nominal"
      then trx.field_value end) as "NOMINAL",
    group_concat(case when trx.field_name = "billRefferenceOne"
      then trx.field_value end) as "BILL_REF_ONE",
    group_concat(case when trx.field_name = "billRefferenceTwo"
      then trx.field_value end) as "BILL_REF_TWO",
    group_concat(case when trx.field_name = "ref"
      then trx.field_value end) as "REFF",
    group_concat(case when trx.field_name = "currency"
      then trx.field_value end) as "CURRENCY_CODE",
    group_concat(case when trx.field_name = "adminCharge"
      then trx.field_value end) as "ADMIN_CHARGE",
    group_concat(case when trx.field_name = "footer"
      then trx.field_value end) as "FOOTER",
    group_concat(case when trx.field_name = "sourceNumber"
      then trx.field_value end) as "SOURCE_NUMBER",
    group_concat(case when trx.field_name = "archiveNumber"
      then trx.field_value end) as "ARCHIVE_NUMBER"
  from common_transaction_log log
    join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type = 'nontaglist'
  group by trace_number, log_id, branch_code, status_log, transaction_date, user_id);