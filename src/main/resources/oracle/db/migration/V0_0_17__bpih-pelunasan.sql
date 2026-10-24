create or replace view bpih_payment_transaction as (
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
  where trx.TRX_TYPE = 'bpihPayment'
  group by log.ID, TRACE_NUMBER, BRANCH_CODE, STATUS, TRX_DATE, USERID);

create or replace view bpih_payment_transaction_detail as (
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
         where trx.TRX_TYPE = 'bpihPayment'
       ) pivot (
       listagg(FIELD_VALUE, ', ')
       within group (
       order by FIELD_VALUE)
       for
         FIELD_NAME
       in (
		 'branchName' as branch_name,
         'sourceNumber' as source_number,
		 'jemaahName' as jemaah_name,
		 'archiveNumber' as archive_number,		 
		 'lunasFlag' as lunas_flag,
		 'porsiNumber' as porsi_number,
		 'nominal' as nominal
       ) )
);
