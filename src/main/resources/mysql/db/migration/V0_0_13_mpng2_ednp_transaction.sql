CREATE OR REPLACE VIEW MPN_EDNP_TRANSACTION AS (
  select
    trace_number                as TRACE_NUMBER,
    log.id                      as LOG_ID,
    log.branch_code             as BRANCH_CODE,
    log.status                  as STATUS_LOG,
    log.trx_date                as TRANSACTION_DATE,
    log.userid                  as USER_ID,
    
	 group_concat(case when trx.field_name = "account" then trx.field_value end) as  account,
	 group_concat(case when trx.field_name = "archiveNumber" then trx.field_value end) as  archive_number,
	 group_concat(case when trx.field_name = "adminCharge" then trx.field_value end) as  admin_charge,
	 group_concat(case when trx.field_name = "billingCode" then trx.field_value end) as  billing_code,
	 group_concat(case when trx.field_name = "branchName" then trx.field_value end) as  branch_name,
	 group_concat(case when trx.field_name = "currency" then trx.field_value end) as  currency,
	 group_concat(case when trx.field_name = "depositCode" then trx.field_value end) as  deposit_code,
	 group_concat(case when trx.field_name = "documentDate" then trx.field_value end) as  document_date,
	 group_concat(case when trx.field_name = "documentNumber" then trx.field_value end) as  document_number,
	 group_concat(case when trx.field_name = "documentType" then trx.field_value end) as  document_type,
	 group_concat(case when trx.field_name = "echelon" then trx.field_value end) as  echelon,
	 group_concat(case when trx.field_name = "kl" then trx.field_value end) as  kl,
	 group_concat(case when trx.field_name = "kppbcCode" then trx.field_value end) as  kppbc_code,
	 group_concat(case when trx.field_name = "nominal" then trx.field_value end) as  nominal,
	 group_concat(case when trx.field_name = "nop" then trx.field_value end) as  nop,
	 group_concat(case when trx.field_name = "ntpn" then trx.field_value end) as  ntpn,
	 group_concat(case when trx.field_name = "payCustomerId" then trx.field_value end) as  pay_customer_id,
	 group_concat(case when trx.field_name = "payCustomerName" then trx.field_value end) as  pay_customer_name,
	 group_concat(case when trx.field_name = "phoneDummy" then trx.field_value end) as  phone_dummy,
	 group_concat(case when trx.field_name = "ref" then trx.field_value end) as  ref,
	 group_concat(case when trx.field_name = "satkerCode" then trx.field_value end) as  satker_code,
	 group_concat(case when trx.field_name = "settlementDate" then trx.field_value end) as  settlement_date,
	 group_concat(case when trx.field_name = "skNumber" then trx.field_value end) as  sk_number,
	 group_concat(case when trx.field_name = "sourceName" then trx.field_value end) as  source_name,
	 group_concat(case when trx.field_name = "sourceNumber" then trx.field_value end) as  source_number,
	 group_concat(case when trx.field_name = "taxAddress" then trx.field_value end) as  tax_address,
	 group_concat(case when trx.field_name = "taxName" then trx.field_value end) as  tax_name,
	 group_concat(case when trx.field_name = "taxNumber" then trx.field_value end) as  tax_number,
	 group_concat(case when trx.field_name = "taxPeriod" then trx.field_value end) as  tax_period
  from common_transaction_log log
  join transaction_log trx on log.id = trx.common_transaction_id
  where trx.trx_type  in ('djp','djbc','dja')
  group by trace_number, log_id, branch_code, status_log, transaction_date, user_id
);
