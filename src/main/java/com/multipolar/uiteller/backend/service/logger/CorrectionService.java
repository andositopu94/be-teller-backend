package com.multipolar.uiteller.backend.service.logger;

import com.multipolar.uiteller.backend.model.CommonTransactionRequestLog;
import com.multipolar.uiteller.backend.model.FailedLogMonitoring;
import com.multipolar.uiteller.backend.model.TransactionRequestLog;
import com.multipolar.uiteller.backend.repository.CommonTransactionRequestLogRepository;
import com.multipolar.uiteller.backend.repository.FailedLogRepository;
import com.multipolar.uiteller.backend.repository.TransactionRequestLogRepository;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.resource.Troubleshoot;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

@Service
public class CorrectionService {
    @Autowired
    private TransactionRequestLogRepository transactionRequestLogRepository;
    @Autowired
    private CommonTransactionRequestLogRepository commonTransactionRequestLogRepository;
    @Autowired
    private FailedLogRepository failedLogRepository;

    private Properties rule(String rule) throws IOException {
        rule=getMappedTrxType(rule);
        Resource resource = new ClassPathResource("correction/" + rule + ".properties");
        Properties properties = new Properties();
        properties.load(resource.getInputStream());
        return properties;
    }

    public List<TransactionRequestLog> transactionRequestLogs(String ref, String archive) {
        String trx;
        TransactionRequestLog transactionRequestLogs = transactionRequestLogRepository.findFirstByFieldNameAndFieldValueAndTraceNumber(Constants.REFFERENCE_FIELD_NAME, ref, StringUtils.substring(archive, 5, 11));
        if (transactionRequestLogs == null)
            return null;
        trx = transactionRequestLogs.getTrxType();
        return update(transactionRequestLogRepository.findByCommonTransactionRequestId(transactionRequestLogs.getCommonTransactionRequest().getId()), trx);
    }

    private List<TransactionRequestLog> update(List<TransactionRequestLog> transactionRequestLogs, String trx) {
        try {
            Properties properties = rule(trx);
            Iterator<TransactionRequestLog> transactionRequestLogIterator = transactionRequestLogs.iterator();
            while (transactionRequestLogIterator.hasNext()) {
                TransactionRequestLog log = transactionRequestLogIterator.next();
                if (properties.getProperty(log.getFieldName()) == null) {
                    transactionRequestLogIterator.remove();
                    continue;
                }
                if (log.getFieldValue() == null)
                    log.setFieldValue(Constants.STRIP_CODE);
                log.setFieldName(properties.getProperty(log.getFieldName()));
            }
            return transactionRequestLogs;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity updateFlag(String id) {
        try {
            UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
            CommonTransactionRequestLog log = commonTransactionRequestLogRepository.findOne(Long.parseLong(id));
            FailedLogMonitoring failedLogMonitoring = failedLogRepository.findByCommonTransactionRequestLog(log);
            log.setRc(Constants.TIMEOUT_CODE);
            commonTransactionRequestLogRepository.save(log);
            if (failedLogMonitoring != null) {
                failedLogMonitoring.setTroubleshootFlagAdmin(Troubleshoot.CORRECTION);
                failedLogMonitoring.setTroubleshootDateFlagAdmin(new Date());
                failedLogMonitoring.setTroubleshootUserIdFlagAdmin(userDetail.getUsername());
                failedLogRepository.save(failedLogMonitoring);
            }
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
    }

    public ResponseEntity updateForwardingFlag(String id) {
        try {
            UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
            CommonTransactionRequestLog log = commonTransactionRequestLogRepository.findOne(Long.parseLong(id));
            FailedLogMonitoring failedLogMonitoring = failedLogRepository.findByCommonTransactionRequestLog(log);
            log.setRc(Constants.TIMEOUT_CODE);
            commonTransactionRequestLogRepository.save(log);
            if (failedLogMonitoring != null) {
                failedLogMonitoring.setTroubleshootFlagAdmin(Troubleshoot.FORWARDING);
                failedLogMonitoring.setTroubleshootDateFlagAdmin(new Date());
                failedLogMonitoring.setTroubleshootUserIdFlagAdmin(userDetail.getUsername());
                failedLogRepository.save(failedLogMonitoring);
            }
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.CONFLICT);
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
