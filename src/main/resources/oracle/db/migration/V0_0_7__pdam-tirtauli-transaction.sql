create or replace view pdam_tirtauli_transaction as (
  select
    log.ID       as log_id,
    TRACE_NUMBER as trace_number,
    BRANCH_CODE  as branch_code,
    STATUS       as status_log,
    TRX_DATE     as transaction_date,
    USERID       as user_id
  from TRANSACTION_LOG trx
    join COMMON_TRANSACTION_LOG log
      on trx.COMMON_TRANSACTION_ID = log.ID
  where trx.TRX_TYPE = 'tirtauli'
  group by log.ID, TRACE_NUMBER, BRANCH_CODE, STATUS, TRX_DATE, USERID);

create or replace view pdam_tirtauli_transaction_detail as (
  select *
  from (
         select
           TRACE_NUMBER as trace_number,
           BRANCH_CODE  as branch_code,
           STATUS       as status_log,
           FIELD_NAME,
           FIELD_VALUE,
           TRX_DATE     as transaction_date,
           USERID       as user_id
         from TRANSACTION_LOG trx
           join COMMON_TRANSACTION_LOG log
             on trx.COMMON_TRANSACTION_ID = log.ID
         where trx.TRX_TYPE = 'tirtauli'
       ) pivot (
       listagg(FIELD_VALUE, ', ')
       within group (
       order by FIELD_VALUE)
       for
         FIELD_NAME
       in (
         'billAccount' as bill_account,
         'billAmount' as bill_amount,
         'billInfo' as bill_info,
         'billPeriod' as bill_period,
         'billReport' as bill_report,
         'cubication' as cubication,
         'currency' as currency_code,
         'customerAddress' as customer_address,
         'customerId' as customer_id,
         'customerName' as customer_name,
         'groupRate' as group_rate,
         'nominal' as nominal,
         'penalties' as penalties,
         'ref' as reff,
         'referenceNumber' as reference_number,
         'sourceNumber' as source_number,
         'adminCharge' as admin_charge,
         'archiveNumber' as archive_number,
         'retribution' as retribution
       ) )
);
