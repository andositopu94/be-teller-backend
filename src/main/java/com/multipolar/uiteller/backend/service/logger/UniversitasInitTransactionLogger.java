
package com.multipolar.uiteller.backend.service.logger;

import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.model.TransactionRequestLog;
import com.multipolar.uiteller.backend.model.TransactionSiskoRequestLog;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UniversitasInitTransactionLogger implements TransactionLogger {
    private static final String SUPPORTED_URL = "eduinit";

    @Override
    public List<TransactionLog> addLog(Map<String, Object> map) {
        List<TransactionLog> logs = new ArrayList<>();

        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String archiveNumber = userDetail.getUsername() + FeatureContextHolder.getContext().getTraceNumber();
        TransactionLog archiveLog = new TransactionLog("archiveNumber", archiveNumber);
        logs.add(archiveLog);

        String branchName = map.get("branchName") == null ? "MISSING_BRANCH_NAME" : map.get("branchName").toString();
        TransactionLog branchLog = new TransactionLog("branchName", branchName);
        logs.add(branchLog);

        String sourceName = map.get("sourceName") == null ? "MISSING_SOURCE_NAME" : map.get("sourceName").toString();
        TransactionLog sourceLog = new TransactionLog("sourceName", sourceName);
        logs.add(sourceLog);

        String nominal = map.get("nominal") == null ? "MISSING_NOMINAL" : map.get("nominal").toString();
        TransactionLog nominalLog = new TransactionLog("nominal", nominal);
        logs.add(nominalLog);

        String kodeBilling = map.get("identitas") == null ? "MISSING_IDENTITAS" : map.get("identitas").toString();
        TransactionLog kodeBillingLog = new TransactionLog("identitas", kodeBilling);
        logs.add(kodeBillingLog);

        String instansiName = map.get("instansiName") == null ? "MISSING_INSTANSI_NAME" : map.get("instansiName").toString();
        TransactionLog instansiNameLog = new TransactionLog("instansiName", instansiName);
        logs.add(instansiNameLog);

        String instansi = map.get("instansi") == null ? "MISSING_INSTANSI_CODE" : map.get("instansi").toString();
        TransactionLog instansiLog = new TransactionLog("instansi", instansi);
        logs.add(instansiLog);

        String jurusan = map.get("jurusan") == null ? "MISSING_JURUSAN_CODE" : map.get("jurusan").toString();
        TransactionLog jurusanLog = new TransactionLog("jurusan", instansi);
        logs.add(jurusanLog);

        String qrString = map.get("qrString") == null ? "MISSING_QR_STRING" : map.get("qrString").toString();
        TransactionLog qrStringLog = new TransactionLog("qrString", qrString);
        logs.add(qrStringLog);

        String sourceNumber = map.get("sourceNumber") == null ? "MISSING_SOURCE_NUMBER" : map.get("sourceNumber").toString();
        TransactionLog sourceNumberLog = new TransactionLog("sourceNumber", sourceNumber);
        logs.add(sourceNumberLog);

        String payType = map.get("payType") == null ? "MISSING_PAY_TYPE" : map.get("payType").toString();
        TransactionLog payTypeLog = new TransactionLog("payType", payType);
        logs.add(payTypeLog);

        String user = map.get("userId") == null ? "MISSING_USER_ID" : map.get("userId").toString();
        TransactionLog userLog = new TransactionLog("userId", user);
        logs.add(userLog);

        String ref = map.get("ref") == null ? "MISSING_REFFERENCE" : map.get("ref").toString();
        TransactionLog refLog = new TransactionLog("ref", ref);
        logs.add(refLog);

        String name = map.get("name") == null ? "MISSING_NAME" : map.get("name").toString();
        TransactionLog nameLog = new TransactionLog("name", name);
        logs.add(nameLog);

        return logs;
    }

    @Override
    public List<TransactionRequestLog> addRequestLog(Map<String, Object> map) {
        List<TransactionRequestLog> logs = new ArrayList<>();
        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication()
                .getDetails();
        String archive = userDetail.getUsername() + FeatureContextHolder.getContext().getTraceNumber();

        String archiveName = archive == null ? "MISSING_ARCHIVE_NAME" : archive;
        TransactionRequestLog archiveLog = new TransactionRequestLog("archiveNumber", archiveName);
        logs.add(archiveLog);

        String branchName = map.get("branchName") == null ? "MISSING_BRANCH_NAME" : map.get("branchName").toString();
        TransactionRequestLog branchLog = new TransactionRequestLog("branchName", branchName);
        logs.add(branchLog);

        String sourceName = map.get("sourceName") == null ? "MISSING_SOURCE_NAME" : map.get("sourceName").toString();
        TransactionRequestLog sourceLog = new TransactionRequestLog("sourceName", sourceName);
        logs.add(sourceLog);

        String kodeBilling = map.get("identitas") == null ? "MISSING_IDENTITAS" : map.get("identitas").toString();
        TransactionRequestLog kodeBillingLog = new TransactionRequestLog("identitas", kodeBilling);
        logs.add(kodeBillingLog);

        String instansiName = map.get("instansiName") == null ? "MISSING_INSTANSI_NAME" : map.get("instansiName").toString();
        TransactionRequestLog instansiNameLog = new TransactionRequestLog("instansiName", instansiName);
        logs.add(instansiNameLog);

        String instansi = map.get("instansi") == null ? "MISSING_INSTANSI_CODE" : map.get("instansi").toString();
        TransactionRequestLog instansiLog = new TransactionRequestLog("instansi", instansi);
        logs.add(instansiLog);

        String jurusan = map.get("jurusan") == null ? "MISSING_JURUSAN_CODE" : map.get("jurusan").toString();
        TransactionRequestLog jurusanLog = new TransactionRequestLog("jurusan", instansi);
        logs.add(jurusanLog);

        String qrString = map.get("qrString") == null ? "MISSING_QR_STRING" : map.get("qrString").toString();
        TransactionRequestLog qrStringLog = new TransactionRequestLog("qrString", qrString);
        logs.add(qrStringLog);

        String payType = map.get("payType") == null ? "MISSING_PAY_TYPE" : map.get("payType").toString();
        TransactionRequestLog payTypeLog = new TransactionRequestLog("payType", payType);
        logs.add(payTypeLog);

        String user = map.get("userId") == null ? "MISSING_USER_ID" : map.get("userId").toString();
        TransactionRequestLog userLog = new TransactionRequestLog("userId", user);
        logs.add(userLog);

        String name = map.get("name") == null ? "MISSING_NAME" : map.get("name").toString();
        TransactionRequestLog nameLog = new TransactionRequestLog("name", name);
        logs.add(nameLog);

        return logs;
    }

    @Override
    public List<TransactionSiskoRequestLog> addSiskoRequestLog(Map<String, Object> map) {
        List<TransactionSiskoRequestLog> logs = new ArrayList<>();
        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication()
                .getDetails();
        String archive = userDetail.getUsername() + FeatureContextHolder.getContext().getTraceNumber();

        String archiveName = archive == null ? "MISSING_ARCHIVE_NAME" : archive;
        TransactionSiskoRequestLog archiveLog = new TransactionSiskoRequestLog("archiveNumber", archiveName);
        logs.add(archiveLog);
        return logs;
    }

    @Override
    public boolean supports(String s) {
        return StringUtils.equals(s, SUPPORTED_URL);
    }
}
