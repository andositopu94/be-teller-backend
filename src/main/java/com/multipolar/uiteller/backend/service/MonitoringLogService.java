package com.multipolar.uiteller.backend.service;

import com.multipolar.uiteller.backend.model.CommonTransactionLog;
import com.multipolar.uiteller.backend.model.CommonTransactionRequestLog;
import com.multipolar.uiteller.backend.model.MonitoringLog;
import com.multipolar.uiteller.backend.resource.Troubleshoot;

import java.util.List;

import org.jpos.iso.ISOMsg;

public interface MonitoringLogService {
    void failedTransactionLog(CommonTransactionLog commonTransactionLog, CommonTransactionRequestLog commonTransactionRequestLog, String trace, String ref, String status);

    List<MonitoringLog> userMonitoringLog(String user);

    List<MonitoringLog> adminMonitoringLog();

    void waitingStatus(Long id);

    void updateByCommonTransactionRequestLog(CommonTransactionRequestLog commonTransactionRequestLog, Troubleshoot troubleshoot);
    
    void saveSuspectTransaction(CommonTransactionLog commonLog, ISOMsg request, ISOMsg response);
    
    void delayTransactionStatusCall(CommonTransactionLog commonLog, ISOMsg request, ISOMsg response);
}
