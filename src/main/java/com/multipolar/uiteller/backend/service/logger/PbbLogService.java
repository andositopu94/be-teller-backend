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
public class PbbLogService {
    private static Logger logger = LoggerFactory.getLogger(PbbLogService.class);
    private static final String PBB = "pbb";
    @Autowired
    private TransactionLogRepository transactionLogRepository;

    public void setReportData(ISOMsg response, String trace) {
        Map<String, Object> stringObjectMap = FeatureContextHolder.getContext().getReportData();
        if (stringObjectMap == null)
            stringObjectMap = new HashMap<>();
        TransactionLog transactionLog = transactionLogRepository.findFirstByFieldNameAndFieldValueAndTrxTypeAndTraceNumber(Constants.REFFERENCE_FIELD_NAME, StringUtils.stripStart(response.getString(37), Constants.ZERO), PBB, trace);
        if (transactionLog == null)
            throw new IllegalArgumentException("Data Not Found");
        List<TransactionLog> transactionLogs = transactionLogRepository.findByCommonTransactionId(transactionLog.getCommonTransaction().getId());
        Map<String, String> objectMap = new HashMap<>();
        for (TransactionLog log : transactionLogs)
            objectMap.put(log.getFieldName(), log.getFieldValue());
        stringObjectMap.put("noWp", objectMap.get("noWp") == null ? "" : objectMap.get("noWp"));
        stringObjectMap.put("namaWp", objectMap.get("namaWp") == null ? "" : objectMap.get("namaWp"));
        stringObjectMap.put("alamat", objectMap.get("alamat") == null ? "" : objectMap.get("alamat"));
        stringObjectMap.put("mataAnggaran", objectMap.get("mataAnggaran") == null ? "" : objectMap.get("mataAnggaran"));
        stringObjectMap.put("uraian", objectMap.get("uraian") == null ? "" : objectMap.get("uraian"));
        stringObjectMap.put("masaPajak", objectMap.get("masaPajak") == null ? "" : objectMap.get("masaPajak"));
        stringObjectMap.put("noSKP", objectMap.get("noSKP") == null ? "" : objectMap.get("noSKP"));
        stringObjectMap.put("jumlahPokok", objectMap.get("jumlahPokok") == null ? "" : objectMap.get("jumlahPokok"));
        stringObjectMap.put("denda", objectMap.get("denda") == null ? "" : objectMap.get("denda"));
        FeatureContextHolder.getContext().setReportData(stringObjectMap);
    }
}
