package com.multipolar.uiteller.backend.service;

import com.multipolar.uiteller.backend.model.CommonTransactionLog;
import com.multipolar.uiteller.backend.model.CommonTransactionRequestLog;
import com.multipolar.uiteller.backend.model.SuspectLogAction;
import com.multipolar.uiteller.backend.model.SuspectLogMonitoring;
import com.multipolar.uiteller.backend.model.dto.SuspectMonitoring;

import java.util.List;

public interface SuspectLogMonitoringService {
    void suspectTransactionLog(CommonTransactionLog commonTransactionLog, CommonTransactionRequestLog commonTransactionRequestLog, String trace, String ref, String status);

    List<SuspectLogMonitoring> userSuspectLogMonitoring(String user);

    List<SuspectMonitoring> getSuspectMonitoringByUser(String user);

    List<SuspectLogMonitoring> adminSuspectLogMonitoring();

    List<SuspectMonitoring> getSuspectMonitoringForAdmin();

    void waitingStatus(Long id);

    void waitingStatusSuspect(Long id);

    SuspectLogAction newSuspectLogAction(SuspectLogAction suspectLogAction);

    SuspectLogMonitoring updateSuspect(Long id, String param);
}
