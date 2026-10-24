package com.multipolar.uiteller.backend.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multipolar.uiteller.backend.model.CommonTransactionLog;
import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.repository.TransactionLogRepository;
import com.multipolar.uiteller.backend.repository.TransactionRequestLogRepository;

@RestController
@RequestMapping("/apiv2")
public class MpnErrorCorrectionController {

    @Autowired
    private TransactionLogRepository logRepository;
    @Autowired
    private TransactionRequestLogRepository requestLogRepository;
    //@Autowired
    //private ReportService service;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

    @GetMapping("/mpnErrorCorrection")
    public List<Map<String, String>> getTransaction() {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        List<Map<String, String>> results = new ArrayList<>();
        List<TransactionLog> logs = logRepository.findForErrorCorrection(username);
        for (TransactionLog log : logs) {
            Map<String, String> map = new HashMap<>();

            CommonTransactionLog commonLog = log.getCommonTransaction();
            Long commonId = commonLog.getId();
            List<TransactionLog> trxLogs = logRepository.findByCommonTransactionId(commonId);

            map.put("branchCode", commonLog.getBranchCode());
            map.put("transactionDate", format.format(commonLog.getTrxDate()));
            map.put("status", commonLog.getStatus() == 0 ? "Berhasil" : "Gagal");
            map.put("tellerId", commonLog.getUserid());
            map.put("commonId", String.valueOf(commonId));
            map.put("message", commonLog.getMessage());
            map.put("rc", commonLog.getRc());

            String ref = null;
            for (TransactionLog tl : trxLogs) {
                map.put(tl.getFieldName(), tl.getFieldValue());
                if (StringUtils.equals(tl.getFieldName(), "ref"))
                	ref = tl.getFieldValue();
            }
            if(map.containsKey("transactionTime")) {
                String transactionTime = map.get("transactionTime").toString();
                LocalTime localTime = DateTimeFormat.forPattern("HHmmss").parseLocalTime(transactionTime);
                map.put("transactionDate", format.format(commonLog.getTrxDate()).toString().substring(0,11)+localTime.toString().substring(0,8));
            }
            map.put("traceNumber", log.getTraceNumber());
            Long requestId = requestLogRepository.findCommonIdFromTraceNumber(log.getTraceNumber(), StringUtils.stripStart(ref, "0"));
            map.put("requestId", String.valueOf(requestId));

            results.add(map);
        }

        return results;
    }

    @GetMapping("/mpnErrorCorrectionTrans")
    public List<Map<String, String>> getTransactionTrans(
            @RequestParam(name = "start", required = false) String trs,
            @RequestParam(name = "branch", required = false) String brc
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        List<Map<String, String>> results = new ArrayList<>();
        List<TransactionLog> logs = logRepository.findForErrorCorrectionTrx(username, trs, brc);
        for (TransactionLog log : logs) {
            Map<String, String> map = new HashMap<>();

            CommonTransactionLog commonLog = log.getCommonTransaction();
            Long commonId = commonLog.getId();
            List<TransactionLog> trxLogs = logRepository.findByCommonTransactionId(commonId);

            map.put("branchCode", commonLog.getBranchCode());
            map.put("transactionDate", format.format(commonLog.getTrxDate()));
            map.put("status", commonLog.getStatus() == 0 ? "Berhasil" : "Gagal");
            map.put("tellerId", commonLog.getUserid());
            map.put("commonId", String.valueOf(commonId));
            map.put("message", commonLog.getMessage());
            map.put("rc", commonLog.getRc());

            String ref = null;
            for (TransactionLog tl : trxLogs) {
                map.put(tl.getFieldName(), tl.getFieldValue());
                if (StringUtils.equals(tl.getFieldName(), "ref"))
                	ref = tl.getFieldValue();
            }

            map.put("traceNumber", log.getTraceNumber());
            Long requestId = requestLogRepository.findCommonIdFromTraceNumber(log.getTraceNumber(), StringUtils.stripStart(ref, "0"));
            map.put("requestId", String.valueOf(requestId));

            results.add(map);
        }

        return results;
    }
}
