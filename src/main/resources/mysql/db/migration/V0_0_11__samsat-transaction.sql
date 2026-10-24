select *
from transaction_log
where trx_type = 'samsat';
-- universitas_transaction
CREATE OR REPLACE VIEW SAMSAT_TRANSACTION AS (
  select
    log.id          as LOG_ID,
    log.branch_code as BRANCH_CODE,
    log.status      as STATUS_LOG,
    log.trx_date    as TRANSACTION_DATE,
    log.userid      as USER_ID
  from common_transaction_log log
    join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type = 'samsat'
  group by log.id, log.branch_code, log.status, log.trx_date, userid);

-- universitas_transaction_detail
CREATE OR REPLACE VIEW SAMSAT_TRANSACTION_DETAIL AS (
  select
    trace_number                as TRACE_NUMBER,
    log.id                      as LOG_ID,
    log.branch_code             as BRANCH_CODE,
    log.status                  as STATUS_LOG,
    log.trx_date                as TRANSACTION_DATE,
    log.userid                  as USER_ID,
    group_concat(case when trx.field_name = "address"
      then trx.field_value end) as "ADDRESS",
    group_concat(case when trx.field_name = "adminCharge"
      then trx.field_value end) as "ADMIN_CHARGE",
    group_concat(case when trx.field_name = "archiveNumber"
      then trx.field_value end) as "ARCHIVE_NUMBER",
    group_concat(case when trx.field_name = "beaStnk"
      then trx.field_value end) as "BEA_STNK",
    group_concat(case when trx.field_name = "color"
      then trx.field_value end) as "COLOR",
    group_concat(case when trx.field_name = "currency"
      then trx.field_value end) as "CURRENCY_CODE",
    group_concat(case when trx.field_name = "datiCode"
      then trx.field_value end) as "DATI_CODE",
    group_concat(case when trx.field_name = "machineNumber"
      then trx.field_value end) as "MACHINE_NUMBER",
    group_concat(case when trx.field_name = "merk"
      then trx.field_value end) as "MERK",
    group_concat(case when trx.field_name = "model"
      then trx.field_value end) as "MODEL",
    group_concat(case when trx.field_name = "name"
      then trx.field_value end) as "NAME",
    group_concat(case when trx.field_name = "nominal"
      then trx.field_value end) as "NOMINAL",
    group_concat(case when trx.field_name = "numberOfPrograms"
      then trx.field_value end) as "NUMBER_OF_PROGRAMS",
    group_concat(case when trx.field_name = "ownership"
      then trx.field_value end) as "OWNERSHIP",
    group_concat(case when trx.field_name = "payCode"
      then trx.field_value end) as "PAY_CODE",
    group_concat(case when trx.field_name = "pkbDen"
      then trx.field_value end) as "PKB_DENDA",
    group_concat(case when trx.field_name = "pkbPok"
      then trx.field_value end) as "PKB_POKOK",
    group_concat(case when trx.field_name = "policeNumber"
      then trx.field_value end) as "POLICE_NUMBER",
    group_concat(case when trx.field_name = "progDen"
      then trx.field_value end) as "PROGRESIF_DENDA",
    group_concat(case when trx.field_name = "progPok"
      then trx.field_value end) as "PROGRESIF_POKOK",
    group_concat(case when trx.field_name = "ref"
      then trx.field_value end) as "REFF",
    group_concat(case when trx.field_name = "refferenceNumber"
      then trx.field_value end) as "REFERENCE_NUMBER",
    group_concat(case when trx.field_name = "sourceNumber"
      then trx.field_value end) as "SOURCE_NUMBER",
    group_concat(case when trx.field_name = "swdDen"
      then trx.field_value end) as "SWDLJJ_DENDA",
    group_concat(case when trx.field_name = "swdPok"
      then trx.field_value end) as "SWDLJJ_POKOK",
    group_concat(case when trx.field_name = "taxPeriod"
      then trx.field_value end) as "TAX_PERIOD",
    group_concat(case when trx.field_name = "total"
      then trx.field_value end) as "TOTAL",
    group_concat(case when trx.field_name = "trxId"
      then trx.field_value end) as "TRANSACTION_ID",
    group_concat(case when trx.field_name = "type"
      then trx.field_value end) as "TRANSACTION_TYPE"
  from common_transaction_log log
    join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type = 'samsat'
  group by trace_number, log_id, branch_code, status_log, transaction_date, user_id);