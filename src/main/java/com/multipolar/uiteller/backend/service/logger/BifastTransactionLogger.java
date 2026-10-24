package com.multipolar.uiteller.backend.service.logger;

import com.multipolar.uiteller.backend.model.TransactionLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BifastTransactionLogger extends DefaultTransactionLogger {
    private static final String SUPPORTED_URL = "bifast";

    @Override
    public List<TransactionLog> addLog(Map<String, Object> map) {
        List<TransactionLog> logs = super.addLog(map);

        String amount = (String) map.get("transactionAmount");
        String nominal = "";
        if (StringUtils.length(amount) > 2) {
            // Remove the last two characters
            nominal = StringUtils.substring(amount, 0, amount.length() - 2);
        }
        TransactionLog nominalLog = new TransactionLog("nominal", nominal);
        logs.add(nominalLog);

        return logs;
    }

    @Override
    public boolean supports(String s) {
        return StringUtils.equals(s, SUPPORTED_URL);
    }
}
