package com.multipolar.uiteller.backend.service.logger;

import com.multipolar.uiteller.backend.model.TransactionLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

// FIXME belum selesai
@Service
public class SamsatTransactionLogger extends DefaultTransactionLogger {
    private static final String SUPPORTED_URL = "samsat";

    @Override
    public List<TransactionLog> addLog(Map<String, Object> map) {
        List<TransactionLog> logs = super.addLog(map);
        if (map.get("checkNumber") != null) {
            TransactionLog checkNumberLog = new TransactionLog("checkNumber", map.get("checkNumber").toString());
            logs.add(checkNumberLog);
        }
        if (map.get("checkPageNumber") != null) {
            TransactionLog checkPageNumberLog = new TransactionLog("checkPageNumber", map.get("checkPageNumber").toString());
            logs.add(checkPageNumberLog);
        }
        return logs;
    }

    @Override
    public boolean supports(String s) {
        return StringUtils.equals(s, SUPPORTED_URL);
    }
}
