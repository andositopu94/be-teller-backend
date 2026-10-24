CREATE OR REPLACE view V1_MPN_EDNP_TRANSACTION 
AS 
  (SELECT `o`.`pay_customer_name`                                      AS 
          `NAMA PELANGGAN` 
             , 
          `o`.`billing_code` 
             AS `KODE PEMBAYARAN`, 
          Cast(`o`.`transaction_date` AS date)                         AS 
             `TGL BAYAR`, 
          Cast(`o`.`nominal` AS DECIMAL(10, 0))                        AS 
          `JUMLAH` 
             , 
          Cast(`o`.`admin_charge` AS DECIMAL(10, 0)) 
             AS `BIAYA`, 
          Cast((`o`.`nominal` + `o`.`admin_charge`) AS DECIMAL(10, 0)) AS 
          `TOTAL`, 
          `o`.`archive_number`                                         AS 
             `NO ARSIP`, 
          `o`.`user_id`                                                AS `USER` 
          , 
          `o`.`status_log` 
          AS 
             `STATUS_LOG`, 
          `o`.`branch_code`                                            AS 
             `BRANCH_CODE`, 
          'TELLER'                                                     AS 
             `CHANNEL` 
   FROM   `V1_MPN_EDNP_TRANSACTION_RAW` `o`) 