# select *
# from transaction_log
# where trx_type = 'pstn' or trx_type is null;
-- create or replace view finnet_pstn_transaction as (
CREATE OR REPLACE VIEW FINNET_PSTN_TRANSACTION AS (
  select
    log.id          as LOG_ID,
    log.branch_code as BRANCH_CODE,
    log.status      as STATUS_LOG,
    log.trx_date    as TRANSACTION_DATE,
    log.userid      as USER_ID
  from common_transaction_log log
    join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type = 'pstn'
  group by log.id, log.branch_code, log.status, log.trx_date, userid);

-- create or replace view finnet_pstn_transaction_detail as (
CREATE OR REPLACE VIEW FINNET_PSTN_TRANSACTION_DETAIL AS (
  select
    trace_number                as TRACE_NUMBER,
    log.id                      as LOG_ID,
    log.branch_code             as BRANCH_CODE,
    log.status                  as STATUS_LOG,
    log.trx_date                as TRANSACTION_DATE,
    log.userid                  as USER_ID,
    group_concat(case when trx.field_name = "areaCode"
      then trx.field_value end) as "AREA_CODE",
    group_concat(case when trx.field_name = "phoneNumber"
      then trx.field_value end) as "PHONE_NUMBER",
    group_concat(case when trx.field_name = "divreCode"
      then trx.field_value end) as "DIVRE_CODE",
    group_concat(case when trx.field_name = "datelCode"
      then trx.field_value end) as "DATEL_CODE",
    group_concat(case when trx.field_name = "billNumber"
      then trx.field_value end) as "BILL_NUMBER",
    group_concat(case when trx.field_name = "billReferenceTwo"
      then trx.field_value end) as "BILL_REF_TWO",
    group_concat(case when trx.field_name = "billValueTwo"
      then trx.field_value end) as "BILL_VALUE_TWO",
    group_concat(case when trx.field_name = "billReferenceOne"
      then trx.field_value end) as "BILL_REF_ONE",
    group_concat(case when trx.field_name = "billValueOne"
      then trx.field_value end) as "BILL_VALUE_ONE",
    group_concat(case when trx.field_name = "billReferenceThree"
      then trx.field_value end) as "BILL_REF_THREE",
    group_concat(case when trx.field_name = "billValueThree"
      then trx.field_value end) as "BILL_VALUE_THREE",
    group_concat(case when trx.field_name = "nominal"
      then trx.field_value end) as "NOMINAL",
    group_concat(case when trx.field_name = "currency"
      then trx.field_value end) as "CURRENCY_CODE",
    group_concat(case when trx.field_name = "sourceNumber"
      then trx.field_value end) as "SOURCE_NUMBER",
    group_concat(case when trx.field_name = "customerName"
      then trx.field_value end) as "CUSTOMER_NAME",
    group_concat(case when trx.field_name = "archiveNumber"
      then trx.field_value end) as "ARCHIVE_NUMBER",
    group_concat(case when trx.field_name = "adminFee"
      then trx.field_value end) as "ADMIN_CHARGE"
  from common_transaction_log log
    join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type = 'pstn'
  group by trace_number, log_id, branch_code, status_log, transaction_date, user_id);