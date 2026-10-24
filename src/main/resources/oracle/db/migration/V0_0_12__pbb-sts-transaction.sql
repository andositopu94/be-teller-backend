create or replace view PBB_STS_TRANSACTION as (
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
  where trx.TRX_TYPE = 'pbb'
  group by log.ID, TRACE_NUMBER, BRANCH_CODE, STATUS, TRX_DATE, USERID);

create or replace view PBB_STS_TRANSACTION_DETAIL as (
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
         where trx.TRX_TYPE = 'pbb'
       ) pivot (
       listagg(FIELD_VALUE, ', ')
       within group (
       order by FIELD_VALUE)
       for
         FIELD_NAME
       in (
         'alamat' as alamat,
         'archiveNumber' as archive_number,
         'adminCharge' as biaya_admin,
         'denda' as denda,
         'jumlahPokok' as jumlah_pokok,
         'mataAnggaran' as mata_anggaran,
         'mataUang' as mata_uang,
         'namaWp' as nama_wp,
         'noArsip' as no_arsip,
         'noSKP' as no_skp,
         'noSts' as no_sts,
         'noWp' as no_wp,
         'nominal' as nominal,
         'ref' as reff,
         'rekeningSumber' as rekening_sumber,
         'rekeningTujuan' as rekening_tujuan,
         'sourceName' as source_name,
         'uraian' as uraian
       ) )
);
