create or replace view finnet_speedy_transaction as (
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
  where trx.TRX_TYPE = 'speedy'
  group by log.ID, TRACE_NUMBER, BRANCH_CODE, STATUS, TRX_DATE, USERID);

create or replace view finnet_speedy_transaction_detail as (
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
         where trx.TRX_TYPE = 'speedy'
       ) pivot (
       listagg(FIELD_VALUE, ', ')
       within group (
       order by FIELD_VALUE)
       for
         FIELD_NAME
       in (
         'customerId' as customer_id,
         'divreCode' as divre_code,
         'datelCode' as datel_code,
         'billReferenceThree' as bill_ref_three,
         'billValueThree' as bill_value_three,
         'billReferenceTwo' as bill_ref_two,
         'billValueTwo' as bill_value_two,
         'billReferenceOne' as bill_ref_one,
         'billValueOne' as bill_value_one,
         'adminCharge' as admin_charge,
         'nominal' as nominal,
         'ref' as reff,
         'sourceNumber' as source_number,
         'currency' as currency_code,
         'customerName' as customer_name,
         'archiveNumber' as archive_number
       ) )
);
