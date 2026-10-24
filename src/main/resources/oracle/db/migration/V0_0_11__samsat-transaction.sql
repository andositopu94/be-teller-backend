create or replace view SAMSAT_TRANSACTION as (
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
  where trx.TRX_TYPE = 'samsat'
  group by log.ID, TRACE_NUMBER, BRANCH_CODE, STATUS, TRX_DATE, USERID);

create or replace view SAMSAT_TRANSACTION_DETAIL as (
  select *
  from (
         select
           log.ID       as log_id,
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
         where trx.TRX_TYPE = 'samsat'
       ) pivot (
       listagg(FIELD_VALUE, ', ')
       within group (
       order by FIELD_VALUE)
       for
         FIELD_NAME
       in (
         'address' as address,
         'adminCharge' as admin_charge,
         'archiveNumber' as archive_number,
         'beaStnk' as bea_stnk,
         'chasisNumber' as chasis_number,
         'color' as color,
         'createYear' as create_year,
         'currency' as currency_code,
         'datiCode' as dati_code,
         'machineNumber' as machine_number,
         'merk' as merk,
         'model' as model,
         'name' as name,
         'nominal' as nominal,
         'numberOfPrograms' as number_of_programs,
         'ownership' as ownership,
         'payCode' as pay_code,
         'pkbDen' as pkb_denda,
         'pkbPok' as pkb_pokok,
         'policeNumber' as police_number,
         'progDen' as progresif_denda,
         'progPok' as progresif_pokok,
         'ref' as reff,
         'refferenceNumber' as reference_number,
         'sourceNumber' as source_number,
         'swdDen' as swdljj_denda,
         'swdPok' as swdljj_pokok,
         'taxPeriod' as tax_period,
         'total' as total,
         'trxId' as transaction_id,
         'type' as transaction_type,
         'ktp' as ktp		 
       ) )
);
