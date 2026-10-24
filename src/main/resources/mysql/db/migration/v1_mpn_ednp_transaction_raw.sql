CREATE OR REPLACE VIEW V1_MPN_EDNP_TRANSACTION_RAW AS (SELECT `trx`.`trace_number` AS `TRACE_NUMBER`, 
        `log`.`id`           AS `LOG_ID`, 
        `log`.`branch_code`  AS `BRANCH_CODE`, 
        `log`.`status`       AS `STATUS_LOG`, 
        `log`.`trx_date`     AS `TRANSACTION_DATE`, 
        `log`.`userid`       AS `USER_ID`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'account') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `account`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'archiveNumber') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `archive_number`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'adminCharge') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `admin_charge`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'billingCode') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `billing_code`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'branchName') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `branch_name`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'currency') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `currency`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'depositCode') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `deposit_code`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'documentDate') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `document_date`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'documentNumber') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `document_number`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'documentType') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `document_type`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'echelon') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `echelon`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'kl') THEN 
        `trx`.`field_value` end 
        ) SEPARATOR 
        ',')                 AS `kl`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'kppbcCode') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `kppbc_code`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'nominal') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `nominal`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'nop') THEN 
        `trx`.`field_value` 
        end) SEPARATOR 
        ',')                 AS `nop`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'ntpn') THEN 
        `trx`.`field_value` 
        end) SEPARATOR 
        ',')                 AS `ntpn`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'payCustomerId') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `pay_customer_id`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'payCustomerName') THEN 
        `trx`.`field_value` end 
        ) SEPARATOR ',')     AS `pay_customer_name`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'phoneDummy') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `phone_dummy`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'ref') THEN 
        `trx`.`field_value` 
        end) SEPARATOR 
        ',')                 AS `ref`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'satkerCode') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `satker_code`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'settlementDate') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `settlement_date`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'skNumber') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `sk_number`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'sourceName') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `source_name`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'sourceNumber') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `source_number`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'taxAddress') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `tax_address`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'taxName') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `tax_name`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'taxNumber') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `tax_number`, 
        Group_concat((CASE WHEN (`trx`.`field_name` = 'taxPeriod') THEN 
        `trx`.`field_value` end) 
        SEPARATOR ',')       AS `tax_period` 
 FROM   (`common_transaction_log` `log` 
         JOIN `transaction_log` `trx` 
           ON(( `log`.`id` = `trx`.`common_transaction_id` ))) 
 WHERE  ( `trx`.`trx_type` IN ( 'djp', 'djbc', 'dja' ) ) 
 GROUP  BY `trx`.`trace_number`, 
           `log_id`, 
           `log`.`branch_code`, 
           `status_log`, 
           `transaction_date`, 
           `user_id`) 