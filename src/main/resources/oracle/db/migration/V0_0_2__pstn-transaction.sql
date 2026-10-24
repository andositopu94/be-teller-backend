create or replace view finnet_pstn_transaction as (
  select
    TRACE_NUMBER as trace_number,
    BRANCH_CODE  as branch_code,
    STATUS       as status_log,
    TRX_DATE     as transaction_date,
    USERID       as user_id
  from TRANSACTION_LOG trx
    join COMMON_TRANSACTION_LOG log
      on trx.COMMON_TRANSACTION_ID = log.ID
  where trx.TRX_TYPE = 'pstn'
  group by TRACE_NUMBER, BRANCH_CODE, STATUS, TRX_DATE, USERID);

create or replace view finnet_pstn_transaction_detail as (
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
         where trx.TRX_TYPE = 'pstn'
       ) pivot (
       listagg(FIELD_VALUE, ', ')
       within group (
       order by FIELD_VALUE)
       for
         FIELD_NAME
       in (
         'areaCode' as area_code,
         'phoneNumber' as phone_number,
         'divreCode' as divre_code,
         'datelCode' as datel_code,
         'billNumber' as bill_number,
         'billReferenceTwo' as bill_ref_two,
         'billValueTwo' as bill_value_two,
         'billReferenceOne' as bill_ref_one,
         'billValueOne' as bill_value_one,
         'billReferenceThree' as bill_ref_three,
         'billValueThree' as bill_value_three,
         'nominal' as nominal,
         'currency' as currency_code,
         'sourceNumber' as source_number,
         'customerName' as customer_name,
         'archiveNumber' as archive_number,
         'adminFee' as admin_charge
       ) )
);
