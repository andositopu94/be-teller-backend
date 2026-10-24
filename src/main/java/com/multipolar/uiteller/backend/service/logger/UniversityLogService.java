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
public class UniversityLogService {
    private static Logger logger = LoggerFactory.getLogger(UniversityLogService.class);
    private static final String EDUINIT = "edupay";
    @Autowired
    private TransactionLogRepository transactionLogRepository;

    public void setReportData(ISOMsg response, String trace) {
        Map<String, Object> stringObjectMap = FeatureContextHolder.getContext().getReportData();
        if (stringObjectMap == null)
            stringObjectMap = new HashMap<>();
        TransactionLog transactionLog = transactionLogRepository.findFirstByFieldNameAndFieldValueAndTrxTypeAndTraceNumber(Constants.REFFERENCE_FIELD_NAME, StringUtils.stripStart(response.getString(37), Constants.ZERO), EDUINIT, trace);
        if (transactionLog == null)
            throw new IllegalArgumentException("Data Not Found");
        List<TransactionLog> transactionLogs = transactionLogRepository.findByCommonTransactionId(transactionLog.getCommonTransaction().getId());
        Map<String, String> objectMap = new HashMap<>();
        for (TransactionLog log : transactionLogs)
            objectMap.put(log.getFieldName(), log.getFieldValue());
        stringObjectMap.put("archiveNumber", objectMap.get("archiveNumber") == null ? "" : objectMap.get("archiveNumber"));
        stringObjectMap.put("branchName", objectMap.get("branchName") == null ? "" : objectMap.get("branchName"));
        stringObjectMap.put("sourceName", objectMap.get("sourceName") == null ? "" : objectMap.get("sourceName"));
        stringObjectMap.put("nominal", objectMap.get("nominal") == null ? "" : objectMap.get("nominal"));
        stringObjectMap.put("kodeBilling", objectMap.get("kodeBilling") == null ? "" : objectMap.get("kodeBilling"));
        stringObjectMap.put("universityName", objectMap.get("universityName") == null ? "" : objectMap.get("universityName"));
        stringObjectMap.put("qrString", objectMap.get("qrString") == null ? "" : objectMap.get("qrString"));
        stringObjectMap.put("payType", objectMap.get("payType") == null ? "" : objectMap.get("payType"));
        stringObjectMap.put("sourceNumber", objectMap.get("sourceNumber") == null ? "" : objectMap.get("sourceNumber"));
        stringObjectMap.put("userId", objectMap.get("userId") == null ? "" : objectMap.get("userId"));
        stringObjectMap.put("ref", objectMap.get("ref") == null ? "" : objectMap.get("ref"));
        FeatureContextHolder.getContext().setReportData(stringObjectMap);
    }
}
