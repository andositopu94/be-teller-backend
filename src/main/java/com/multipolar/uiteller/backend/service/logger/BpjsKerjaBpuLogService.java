package com.multipolar.uiteller.backend.service.logger;

import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.repository.TransactionLogRepository;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BpjsKerjaBpuLogService {
    private static Logger logger = LoggerFactory.getLogger(BpjsKerjaBpuLogService.class);

    private static final String BPJS_KERJA_BPU = "bpjsKerjaBpu";

    @Autowired
    private TransactionLogRepository transactionLogRepository;

    public void setReportData(ISOMsg response, String trace) {
        Map<String, Object> stringObjectMap = FeatureContextHolder.getContext().getReportData();
        if (stringObjectMap == null)
            stringObjectMap = new HashMap<>();
        TransactionLog transactionLog = transactionLogRepository.findFirstByFieldNameAndFieldValueAndTrxTypeAndTraceNumber(Constants.REFFERENCE_FIELD_NAME, StringUtils.stripStart(response.getString(37), Constants.ZERO), BPJS_KERJA_BPU, trace);
        if (transactionLog == null)
            throw new IllegalArgumentException("Data Not Found");
        List<TransactionLog> transactionLogs = transactionLogRepository.findByCommonTransactionId(transactionLog.getCommonTransaction().getId());
        Map<String, String> objectMap = new HashMap<>();
        for (TransactionLog log : transactionLogs)
            objectMap.put(log.getFieldName(), log.getFieldValue());
        stringObjectMap.put("customerName", objectMap.get("customerName") == null ? "" : objectMap.get("customerName"));
        stringObjectMap.put("taxNumber", objectMap.get("taxNumber") == null ? "" : objectMap.get("taxNumber"));
        FeatureContextHolder.getContext().setReportData(stringObjectMap);
    }
}
