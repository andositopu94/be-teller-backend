create or replace view SAMSAT_NASIONAL_TRANSACTION as (
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
  where trx.TRX_TYPE = 'samsatNasional'
  group by log.ID, TRACE_NUMBER, BRANCH_CODE, STATUS, TRX_DATE, USERID);

create or replace view SAMSAT_NASIONAL_TRANSACTION_DETAIL as (
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
         where trx.TRX_TYPE = 'samsatNasional'
       ) pivot (
       listagg(FIELD_VALUE, ', ')
       within group (
       order by FIELD_VALUE)
       for
         FIELD_NAME
       in (
'additionalInfo1' as additional_info_1,
'additionalInfo2' as additional_info_2,
'additionalInfo3' as additional_info_3,
'adminCharge' as admin_charge,
'adminFee' as admin_fee,
'archiveNumber' as archive_number,
'branchName' as branch_name,
'cc' as cc,
'chasisNumber' as chasis_number,
'checkNumber' as check_number,
'checkPageNumber' as check_page_number,
'color' as color,
'createYear' as create_year,
'currency' as currency,
'jenis' as jenis,
'merk' as merk,
'model' as model,
'name' as name,
'newTBPKBDate' as new_tbpkb_date,
'nik' as nik,
'nominal' as nominal,
'oldTBPKBDate' as old_tbpkb_date,
'ownership' as ownership,
'parkir' as parkir,
'payCode' as pay_code,
'payType' as pay_type,
'pkbDen' as pkb_den,
'pkbPok' as pkb_pok,
'pnbpDen' as pnbp_den,
'pnbpPok' as pnbp_pok,
'policeNumber' as police_number,
'ref' as ref,
'remark' as remark,
'sourceName' as source_name,
'sourceNumber' as source_number,
'sourceNumberDummy' as source_number_dummy,
'STNKEndDate' as stnk_end_date,
'swdDen' as swd_den,
'swdPok' as swd_pok,
'total' as total,
'transactionID' as transaction_id,
'type' as type,
'wheel' as wheel
		 
       ) )
);
