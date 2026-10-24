package com.multipolar.uiteller.backend.service.logger;

import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.model.TransactionRequestLog;
import com.multipolar.uiteller.backend.model.TransactionSiskoRequestLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SpeedyTransactionLogger extends DefaultTransactionLogger implements TransactionLogger {
    private static final String SUPPORTED_URL_SPEEDY = "speedy";


    @Override
    public List<TransactionLog> addLog(Map<String, Object> map) {
        List<TransactionLog> logs = super.addLog(map);
        String customerName = map.get("customerName") == null ? "MISING_CUSTOMER_NAME" : map.get("customerName").toString();
        TransactionLog customerLog = new TransactionLog("customerName", customerName);
        logs.add(customerLog);

        String taxNumber = map.get("taxNumber") == null ? "MISSING_TAX_NUMBER" : map.get("taxNumber").toString();
		TransactionLog taxLog = new TransactionLog("taxNumber", taxNumber);
		logs.add(taxLog);

        return logs;
    }

    @Override
    public List<TransactionRequestLog> addRequestLog(Map<String, Object> map) {
        List<TransactionRequestLog> logs = super.addRequestLog(map);
        String customerName = map.get("customerName") == null ? "MISING_CUSTOMER_NAME" : map.get("customerName").toString();
        TransactionRequestLog customerLog = new TransactionRequestLog("customerName", customerName);
        logs.add(customerLog);

        String taxNumber = map.get("taxNumber") == null ? "MISSING_TAX_NUMBER" : map.get("taxNumber").toString();
        TransactionRequestLog taxLog = new TransactionRequestLog("taxNumber", taxNumber);
        logs.add(taxLog);

        String payType = map.get("payType") == null ? "MISSING_PAY_TYPE" : map.get("payType").toString();
        TransactionRequestLog payTypeLog = new TransactionRequestLog("payType", payType);
        logs.add(payTypeLog);

        return logs;
    }

    @Override
    public List<TransactionSiskoRequestLog> addSiskoRequestLog(Map<String, Object> map) {
        List<TransactionSiskoRequestLog> logs = super.addSiskoRequestLog(map);
        String customerName = map.get("customerName") == null ? "MISING_CUSTOMER_NAME" : map.get("customerName").toString();
        TransactionSiskoRequestLog customerLog = new TransactionSiskoRequestLog("customerName", customerName);
        logs.add(customerLog);

        String taxNumber = map.get("taxNumber") == null ? "MISSING_TAX_NUMBER" : map.get("taxNumber").toString();
        TransactionSiskoRequestLog taxLog = new TransactionSiskoRequestLog("taxNumber", taxNumber);
        logs.add(taxLog);

        String payType = map.get("payType") == null ? "MISSING_PAY_TYPE" : map.get("payType").toString();
        TransactionSiskoRequestLog payTypeLog = new TransactionSiskoRequestLog("payType", payType);
        logs.add(payTypeLog);

        return logs;
    }

    @Override
    public boolean supports(String s) {
        return StringUtils.equals(s, SUPPORTED_URL_SPEEDY);
    }
}
