package com.multipolar.uiteller.backend.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multipolar.uiteller.backend.model.CommonTransactionLog;
import com.multipolar.uiteller.backend.model.SuspectLogAction;
import com.multipolar.uiteller.backend.model.SuspectLogMonitoring;
import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.model.dto.SuspectMonitoring;
import com.multipolar.uiteller.backend.repository.TransactionLogRepository;
import com.multipolar.uiteller.backend.service.SuspectLogMonitoringService;

@RestController
@RequestMapping("/apiv2")
public class SuspectLogMonitoringController {

    @Autowired
    private SuspectLogMonitoringService suspectLogMonitoringService;
    
    @Autowired
    private TransactionLogRepository transactionLogRepository;
    
    private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");

    @GetMapping("suspectLogMonitoring/{user}")
    public List<SuspectLogMonitoring> suspectLogMonitoringList(@PathVariable String user) {
        return suspectLogMonitoringService.userSuspectLogMonitoring(user);
    }
    
    @GetMapping("transactionLogBySuspectId/{id}")
    public List<Map<String, String>> getTransaction(@PathVariable Long id) {
        List<Map<String, String>> results = new ArrayList<>();

        List<TransactionLog> logs = transactionLogRepository.findTrxBySuspectId(id);
        for (TransactionLog log : logs) {
            Map<String, String> map = new HashMap<>();

            CommonTransactionLog commonLog = log.getCommonTransaction();
            Long commonId = commonLog.getId();
            List<TransactionLog> trxLogs = transactionLogRepository.findByCommonTransactionId(commonId);

            map.put("branchCode", commonLog.getBranchCode());
            map.put("transactionDate", format.format(commonLog.getTrxDate()));
            map.put("status", commonLog.getStatus() == 0 ? "Berhasil" : "Gagal");
            map.put("tellerId", commonLog.getUserid());
            map.put("commonId", String.valueOf(commonId));

            for (TransactionLog tl : trxLogs) {
                map.put(tl.getFieldName(), tl.getFieldValue());
            }

            map.put("traceNumber", log.getTraceNumber());
            map.put("stan", log.getTraceNumber());

            results.add(map);
        }

        if (results.size() == 0)
            return results;

        return results;
    }

    @GetMapping("suspectMonitoring/{user}")
    public List<SuspectMonitoring> getSuspectMonitoringByUser(@PathVariable String user) {
        return suspectLogMonitoringService.getSuspectMonitoringByUser(user);
    }

    @GetMapping("suspectLogMonitoringAdmin")
    public List<SuspectLogMonitoring> suspectLogMonitoringAdminList() {
        return suspectLogMonitoringService.adminSuspectLogMonitoring();
    }

    @GetMapping("suspectMonitoringAdmin")
    public List<SuspectMonitoring> getSuspectMonitoringForAdmin() {
        return suspectLogMonitoringService.getSuspectMonitoringForAdmin();
    }

    @GetMapping("suspectWaitingUpdate/{id}/{user}")
    public List<SuspectMonitoring> suspectWaitingUpdate(@PathVariable Long id, @PathVariable String user) {
    	suspectLogMonitoringService.waitingStatusSuspect(id);
        return suspectLogMonitoringService.getSuspectMonitoringByUser(user);
    }

    @PostMapping("suspectLogAction")
    public SuspectLogAction newSuspectLogAction(@RequestBody SuspectLogAction suspectLogAction) {
        return suspectLogMonitoringService.newSuspectLogAction(suspectLogAction);
    }

    @PostMapping("updateSuspect/{id}/{param}")
    public SuspectLogMonitoring updateSuspect(@PathVariable Long id, @PathVariable String param)  {
        return suspectLogMonitoringService.updateSuspect(id, param);
    }
}
