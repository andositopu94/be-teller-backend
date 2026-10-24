package com.multipolar.uiteller.backend.service.logger;

import com.multipolar.uiteller.backend.model.*;
import com.multipolar.uiteller.backend.repository.*;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.resource.Troubleshoot;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ReprintCorrectionService {
    private static final String SUPPORTED_URL_DJP = "djp";
    private static final String SUPPORTED_URL_DJBC = "djbc";
    private static final String SUPPORTED_URL_DJA = "dja";
    private static final String SUPPORTED_URL_DJP_REINQUIRY = "djpReinquiry";
    private static final String SUPPORTED_URL_DJBC_REINQUIRY = "djbcReinquiry";
    private static final String SUPPORTED_URL_DJA_REINQUIRY = "djaReinquiry";
    private static final String SUPPORTED_URL_DJPG3 = "djpg3";
    private static final String SUPPORTED_URL_DJBCG3 = "djbcg3";
    private static final String SUPPORTED_URL_DJAG3 = "djag3";
    private static final String SUPPORTED_URL_DJPG3_REINQUIRY = "djpg3Reinquiry";
    private static final String SUPPORTED_URL_DJBCG3_REINQUIRY = "djbcg3Reinquiry";
    private static final String SUPPORTED_URL_DJAG3_REINQUIRY = "djag3Reinquiry";

    @Autowired
    private TransactionLogRepository transactionLogRepository;
    @Autowired
    private CommonTransactionLogRepository commonTransactionLogRepository;
    @Autowired
    private FailedLogRepository failedLogRepository;
    @Autowired
    private UniversitasLogRepository universitasLogRepository;
    @Autowired
    private UniversitasInitLogRepository universitasInitLogRepository;

    private Properties rule(String rule) throws IOException {
        rule=getMappedTrxType(rule);
        Resource resource = new ClassPathResource("correction/" + rule + ".properties");
        Properties properties = new Properties();
        properties.load(resource.getInputStream());
        return properties;
    }

    public List<TransactionLog> transactionRequestLogs(String ref, String archive) {
        String trx, trace;
        CommonTransactionLog id;
        TransactionLog transactionLog = transactionLogRepository.findFirstByFieldNameAndFieldValueAndTraceNumber(Constants.REFFERENCE_FIELD_NAME, ref, StringUtils.substring(archive, 5));
        if(transactionLog == null){
            throw new IllegalArgumentException("Data Not Found");
        }
        id = transactionLog.getCommonTransaction();
        trx = transactionLog.getTrxType();
        trace = transactionLog.getTraceNumber();
        return update(transactionLogRepository.findByCommonTransactionId(id.getId()), trx, transactionLogRepository.findFieldName(id.getId()), trace, id);
    }

    public List<TransactionLog> findTransactionByTraceAndDate(String trace, String trxDate) {
        // trx date format, DDMMYYYY
        List<TransactionLog> logs = transactionLogRepository.findTransactionByTraceAndDate(trace, trxDate);
        if (null == logs || logs.size() == 0)
            return null;

        String trxType = logs.get(0).getTrxType();
        CommonTransactionLog commonLog = logs.get(0).getCommonTransaction();
        Object[] fieldNames = transactionLogRepository.findFieldName(commonLog.getId());

        return update(logs, trxType, fieldNames, trace, commonLog);
    }

    private List<TransactionLog> update(List<TransactionLog> transactionLogs, String trx, Object[] objects, String trace, CommonTransactionLog id) {
        try {
            Properties properties = rule(trx);
            Set<String> set = properties.stringPropertyNames();
            transactionLogs.addAll(notExist(objects, set, trx, trace, id));
            Iterator<TransactionLog> transactionLogIterator = transactionLogs.iterator();
            while (transactionLogIterator.hasNext()) {
                TransactionLog log = transactionLogIterator.next();
                if (properties.getProperty(log.getFieldName()) == null)
                    continue;
                log.setAlias(properties.getProperty(log.getFieldName()));
            }
            return transactionLogs;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<TransactionLog> notExist(Object[] objects, Set<String> strings, String trx, String trace, CommonTransactionLog id) {
        List<TransactionLog> transactionLogs = new ArrayList<>();
        for (String s : strings)
            if (!ArrayUtils.contains(objects, s)) {
                TransactionLog transactionLog = new TransactionLog(s, null, trx, trace, id);
                transactionLogRepository.save(transactionLog);
                transactionLogs.add(transactionLog);
            }
        return transactionLogs;
    }

    public ResponseEntity updateFlag(ReprintUpdate reprintUpdate) {
        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String[] supported = new String[]{SUPPORTED_URL_DJP, SUPPORTED_URL_DJBC, SUPPORTED_URL_DJA,
                SUPPORTED_URL_DJPG3, SUPPORTED_URL_DJBCG3, SUPPORTED_URL_DJAG3,
                SUPPORTED_URL_DJP_REINQUIRY, SUPPORTED_URL_DJBC_REINQUIRY, SUPPORTED_URL_DJA_REINQUIRY,
                SUPPORTED_URL_DJPG3_REINQUIRY, SUPPORTED_URL_DJBCG3_REINQUIRY, SUPPORTED_URL_DJAG3_REINQUIRY};
        String trxType = null;
        FailedLogMonitoring failedLogMonitoring;
        try {
            CommonTransactionLog log = commonTransactionLogRepository.findOne(reprintUpdate.getId());
            failedLogMonitoring = failedLogRepository.findByCommonTransactionLog_Id(log.getId());
            String namaMahasiswa = "";
            for (TransactionLog transactionLog : reprintUpdate.getResult()) {
                if(transactionLog.getFieldName().equalsIgnoreCase("namaMahasiswa"))
                namaMahasiswa = transactionLog.getFieldValue();
            }
            for (TransactionLog transactionLog : reprintUpdate.getResult()) {
                trxType = transactionLog.getTrxType();
                if(transactionLog.getFieldName().equalsIgnoreCase("archiveNumber")) {
                    hackForEdupayAndEduinit(trxType, transactionLog, namaMahasiswa);
                }
                transactionLog.setCommonTransaction(log);
                updateWithConditionFieldNameNotEqualNamaMahasiswa(transactionLog);
            }
            log.setStatus(0);

            if (StringUtils.equalsAny(trxType, supported)) {
                log.setRc("00");
            }
            commonTransactionLogRepository.save(log);
            if (failedLogMonitoring != null) {
                failedLogMonitoring.setTroubleshoot(Troubleshoot.CORRECTION);
                failedLogMonitoring.setTroubleshootUserId(userDetail.getUsername());
                failedLogMonitoring.setTroubleshootDate(new Date());
                failedLogRepository.save(failedLogMonitoring);
            }
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
    }

    public ResponseEntity updateFlagAdmin(ReprintUpdate reprintUpdate) {
        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String[] supported = new String[]{SUPPORTED_URL_DJP, SUPPORTED_URL_DJBC, SUPPORTED_URL_DJA,
                SUPPORTED_URL_DJPG3, SUPPORTED_URL_DJBCG3, SUPPORTED_URL_DJAG3,
                SUPPORTED_URL_DJP_REINQUIRY, SUPPORTED_URL_DJBC_REINQUIRY, SUPPORTED_URL_DJA_REINQUIRY,
                SUPPORTED_URL_DJPG3_REINQUIRY, SUPPORTED_URL_DJBCG3_REINQUIRY, SUPPORTED_URL_DJAG3_REINQUIRY};
        String trxType = null;
        FailedLogMonitoring failedLogMonitoring;
        try {
            CommonTransactionLog log = commonTransactionLogRepository.findOne(reprintUpdate.getId());
            failedLogMonitoring = failedLogRepository.findByCommonTransactionLog_Id(log.getId());
            String namaMahasiswa = "";
            for (TransactionLog transactionLog : reprintUpdate.getResult()) {
                if(transactionLog.getFieldName().equalsIgnoreCase("namaMahasiswa"))
                    namaMahasiswa = transactionLog.getFieldValue();
            }
            for (TransactionLog transactionLog : reprintUpdate.getResult()) {
                trxType = transactionLog.getTrxType();
                hackForEdupayAndEduinit(trxType, transactionLog, namaMahasiswa);
                transactionLog.setCommonTransaction(log);
                updateWithConditionFieldNameNotEqualNamaMahasiswa(transactionLog);
            }
            log.setStatus(0);

            if (StringUtils.equalsAny(trxType, supported)) {
                log.setRc("00");
            }
            commonTransactionLogRepository.save(log);
            if (failedLogMonitoring != null) {
                failedLogMonitoring.setTroubleshootFlagAdmin(Troubleshoot.CORRECTION);
                failedLogMonitoring.setTroubleshootUserIdFlagAdmin(userDetail.getUsername());
                failedLogMonitoring.setTroubleshootDateFlagAdmin(new Date());
                failedLogRepository.save(failedLogMonitoring);
            }
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
    }

    private void hackForEdupayAndEduinit(String trxType, TransactionLog transactionLog, String namaMahasiswa) {
        if(trxType.equalsIgnoreCase("edupay")){
            List<UniversitasLog> universitasLogs = universitasLogRepository.findByArchiveNumber(transactionLog.getFieldValue());
            for(UniversitasLog universitasLog : universitasLogs){
                if(universitasLog.getFieldName().equalsIgnoreCase("Nama")){
                    universitasLog.setFieldValue1(namaMahasiswa);
                    universitasLogRepository.save(universitasLog);
                }
            }
        } else if (trxType.equalsIgnoreCase("eduinit")){
            List<UniversitasInitLog> universitasInitLogs = universitasInitLogRepository.findByArchiveNumber(transactionLog.getFieldValue());
            for(UniversitasInitLog universitasInitLog : universitasInitLogs){
                if(universitasInitLog.getFieldName().equalsIgnoreCase("Nama")){
                    universitasInitLog.setFieldValue1(namaMahasiswa);
                    universitasInitLogRepository.save(universitasInitLog);
                }
            }
        }
    }

    private void updateWithConditionFieldNameNotEqualNamaMahasiswa(TransactionLog transactionLog) {
        if(!transactionLog.getFieldName().equals("namaMahasiswa")) {
            transactionLogRepository.save(transactionLog);
        }
    }

    private String getMappedTrxType(String trxType) {
        if (StringUtils.startsWith(trxType, "pdamDtiInstallation")){
            return "pdamDtiInstallation";
        } else if (StringUtils.startsWith(trxType, "pdamDti")) {
            return "pdamDti";
        }
        return trxType;
    }
}
