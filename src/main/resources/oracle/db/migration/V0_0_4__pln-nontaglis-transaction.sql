create or replace view pln_nontaglist_transaction as (
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
  where trx.TRX_TYPE = 'nontaglist'
  group by log.ID, TRACE_NUMBER, BRANCH_CODE, STATUS, TRX_DATE, USERID);

create or replace view pln_nontaglist_transaction_detail as (
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
         where trx.TRX_TYPE = 'nontaglist'
       ) pivot (
       listagg(FIELD_VALUE, ', ')
       within group (
       order by FIELD_VALUE)
       for
         FIELD_NAME
       in (
         'productCode' as customer_id,
         'registrationNumber' as registration_number,
         'customerName' as customer_name,
         'nominal' as nominal,
         'billRefferenceOne' as bill_ref_one,
         'billRefferenceTwo' as bill_ref_two,
         'ref' as reff,
         'currency' as currency_code,
         'adminCharge' as admin_charge,
         'footer' as footer,
         'sourceNumber' as source_name,
         'archiveNumber' as archive_number
       ) )
);
