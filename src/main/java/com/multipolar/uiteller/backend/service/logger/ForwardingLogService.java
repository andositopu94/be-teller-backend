package com.multipolar.uiteller.backend.service.logger;

import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.model.TransactionRequestLog;
import com.multipolar.uiteller.backend.repository.CommonTransactionRequestLogRepository;
import com.multipolar.uiteller.backend.repository.ResourceMessageRepository;
import com.multipolar.uiteller.backend.repository.TransactionLogRepository;
import com.multipolar.uiteller.backend.repository.TransactionRequestLogRepository;
import com.multipolar.uiteller.backend.resource.BusinessException;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.ReversalResponse;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ForwardingLogService {
    private static Logger logger = LoggerFactory.getLogger(ForwardingLogService.class);

    @Autowired
    private CommonTransactionRequestLogRepository commonLogRepository;

    @Autowired
    private TransactionRequestLogRepository transactionRequestLogRepository;

    @Autowired
    private ResourceMessageRepository messageRepository;

    @Autowired
    private TransactionLogRepository transactionLogRepository;

    public TransactionRequestLog getRequestTransactionLog(Long id, String s) throws BusinessException {
        TransactionRequestLog transactionRequestLog = transactionRequestLogRepository.findByCommonTransactionRequestIdAndFieldName(id, s);
        if (transactionRequestLog == null)
            throw new BusinessException("Data Not Found");
        return transactionRequestLog;
    }

    public TransactionLog getTransactionLog(Long id, String s) throws BusinessException {
        TransactionLog transactionLog = transactionLogRepository.findByCommonTransactionIdAndAndFieldName(id, s);
        if (transactionLog == null)
            throw new BusinessException("Data Not Found");
        return transactionLog;
    }

    @Transactional
    public TransactionRequestLog setFlag(TransactionRequestLog transactionLog, ISOMsg isoMsg) {
        if (!StringUtils.equals(isoMsg.getString(39), Constants.SUCCESS_CODE))
            return null;
        TransactionRequestLog log = new TransactionRequestLog();
        log.setFieldName(Constants.FORWARDING_FIELD_NAME);
        log.setFieldValue(Constants.FORWARDING_FIELD_VALUE);
        log.setTraceNumber(transactionLog.getTraceNumber());
        log.setTrxType(transactionLog.getTrxType());
        log.setCommonTransactionRequest(transactionLog.getCommonTransactionRequest());
        return transactionRequestLogRepository.save(log);
    }

    @Transactional
    public TransactionRequestLog setRevFlag(TransactionRequestLog transactionLog, ReversalResponse reversalResponse) {
        if (!StringUtils.equals(reversalResponse.getRCode(), Constants.SUCCESS_CODE_MIDDLEWARE))
            return null;
        TransactionRequestLog log = new TransactionRequestLog();
        log.setFieldName(Constants.REVERSAL_FIELD_NAME);
        log.setFieldValue(Constants.REVERSAL_FIELD_VALUE);
        log.setTraceNumber(transactionLog.getTraceNumber());
        log.setTrxType(transactionLog.getTrxType());
        log.setCommonTransactionRequest(transactionLog.getCommonTransactionRequest());
        return transactionRequestLogRepository.save(log);
    }
}
