package com.multipolar.uiteller.backend.service.logger;

import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.model.TransactionRequestLog;
import com.multipolar.uiteller.backend.model.TransactionSiskoRequestLog;

import java.util.List;
import java.util.Map;

public interface TransactionLogger {
    List<TransactionLog> addLog(Map<String, Object> map);

    List<TransactionRequestLog> addRequestLog(Map<String, Object> map);

    List<TransactionSiskoRequestLog> addSiskoRequestLog(Map<String, Object> map);

    boolean supports(String s);
}
