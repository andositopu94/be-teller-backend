create or replace view universitas_transaction as (
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
  where trx.TRX_TYPE = 'university'
  group by log.ID, TRACE_NUMBER, BRANCH_CODE, STATUS, TRX_DATE, USERID);

create or replace view universitas_transaction_detail as (
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
    where trx.TRX_TYPE = 'university'
  ) pivot (
    listagg(FIELD_VALUE, ', ')
    within group (
      order by FIELD_VALUE)
  for
    FIELD_NAME
  in (
    'adminCharge' as admin_charge,
    'archiveNumber' as archive_number,
    'bill' as bill,
    'currency' as currency_code,
    'major' as major,
    'name' as name,
    'nim' as nim,
    'nominal' as nominal,
    'penalties' as penalties,
    'ref' as reff,
    'semester' as semester,
    'sourceNumber' as source_number,
    'totalPayment' as total_payment
  ) )
);
