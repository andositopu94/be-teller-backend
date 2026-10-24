create or replace view universitas_init_transaction as (
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
  where trx.TRX_TYPE = 'eduinit'
  group by log.ID, TRACE_NUMBER, BRANCH_CODE, STATUS, TRX_DATE, USERID);

create or replace view universitas_init_transaction_detail as (
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
    where trx.TRX_TYPE = 'eduinit'
  ) pivot (
      listagg(FIELD_VALUE, ', ')
      within group (
        order by FIELD_VALUE)
      for
        FIELD_NAME
      in (
        'branchName' as branch_name,
        'sourceNumber' as source_number,
        'sourceName' as source_name,
        'archiveNumber' as archive_number,
        'identitas' as identity,
        'instansiName' as instance_name,
        'adminCharge' as admin_charge,
        'name' as customer_name,
        'nominal' as nominal
      ) )
);

create or replace view universitas_init_transaction_detail_add as (
  select *
  from (

    select *
    from (
      select
        ARCHIVE_NUMBER,
        FIELD_NAME,
        FIELD_VALUE1
      from UNIVERSITAS_INIT_LOG
    )
      pivot (
        listagg(FIELD_VALUE1, ', ')
        within group (
          order by FIELD_VALUE1)
        for
          FIELD_NAME
        in (
          'Jurusan' as jurusan,
          'NIM/NIS' as customer_id
        ) )

  )
);

create or replace view universitas_init_transaction_details as (
  select utd.*, utda.CUSTOMER_ID, utda.JURUSAN
  from UNIVERSITAS_INIT_TRANSACTION_DETAIL utd
    INNER JOIN UNIVERSITAS_INIT_TRANSACTION_DETAIL_ADD utda on utd.ARCHIVE_NUMBER = utda.ARCHIVE_NUMBER
);


