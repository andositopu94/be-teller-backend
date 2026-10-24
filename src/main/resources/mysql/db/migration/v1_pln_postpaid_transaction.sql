CREATE OR REPLACE view V1_PLN_POSTPAID_TRANSACTION 
AS 
  (SELECT o.customer_name                                               AS 
       'NAMA PELANGGAN', 
       o.customer_id                                                 AS 'NOPEL', 
       ''                                                            AS 
       'PERIODE', 
       o.transaction_date                                            AS 
       'TGL BAYAR', 
       o.bill_nominal                                                AS 'JUMLAH' 
       , 
       10                                                            AS 
       'DENDA', 
       o.admin_charge                                                AS 'BIAYA', 
       Cast((o.bill_nominal + 0 + o.admin_charge) AS DECIMAL(10, 0)) AS 'TOTAL', 
       o.archive_number                                              AS 
       'NO ARSIP', 
       o.user_id                                                     AS 'USER', 
       'TELLER'                                                      AS 
       'CHANNEL', 
       o.status_log, 
       o.transaction_date 
FROM   PLN_POSTPAID_TRANSACTION_DETAIL o) 