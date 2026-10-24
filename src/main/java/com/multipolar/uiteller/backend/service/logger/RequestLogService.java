package com.multipolar.uiteller.backend.service.logger;

import com.multipolar.uiteller.backend.model.CommonTransactionRequestLog;
import com.multipolar.uiteller.backend.model.CommonTransactionSiskoRequestLog;
import com.multipolar.uiteller.backend.model.TransactionRequestLog;
import com.multipolar.uiteller.backend.model.TransactionSiskoRequestLog;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class RequestLogService {
    private static Logger logger = LoggerFactory.getLogger(RequestLogService.class);
    private static final String PAYMENT_PCODE = "500000";
    private static final String CHECK_PAYMENT_PCODE = "502000";
    private static final String[] MPN_REINQUIRY = {"djpReinquiry", "djpg3Reinquiry", "djbcReinquiry", "djbcg3Reinquiry", "djaReinquiry", "djag3Reinquiry"};
    @Autowired
    private TransactionRequestLogService transactionRequestLogService;
    @Autowired
    private TransactionLoggerFactory loggerFactory;
    @Autowired
    private TransactionSiskoRequestLogService transactionSiskoRequestLogService;

    public CommonTransactionRequestLog request(String context, ISOMsg req) throws IOException {
        CommonTransactionRequestLog commonTransactionRequestLog = null;
        if (StringUtils.equals(req.getString(3), PAYMENT_PCODE) 
        		|| StringUtils.equals(req.getString(3), CHECK_PAYMENT_PCODE) 
        		|| (StringUtils.equalsAny(context, MPN_REINQUIRY))) {
            TransactionLogger transactionRequestLogger = loggerFactory.get(context);
            commonTransactionRequestLog = transactionRequestLogService.logTransaction(req);
            List<TransactionRequestLog> requestLogs = transactionRequestLogger.addRequestLog(FeatureContextHolder.getContext().getReportData());
            transactionRequestLogService.saveAdditionalLog(requestLogs, commonTransactionRequestLog, req.getString(11), context);
        }
        return commonTransactionRequestLog;
    }

    public CommonTransactionSiskoRequestLog siskoRequest(String context, ISOMsg req, ISOMsg debitReq) throws IOException, ISOException {
        CommonTransactionSiskoRequestLog commonTransactionSiskoRequestLog = null;
        TransactionLogger transactionRequestSiskoLogger = loggerFactory.get(context);
        commonTransactionSiskoRequestLog = transactionSiskoRequestLogService.logTransaction(req, debitReq);
        List<TransactionSiskoRequestLog> requestLogs = transactionRequestSiskoLogger.addSiskoRequestLog(FeatureContextHolder.getContext().getReportData());
        transactionSiskoRequestLogService.saveAdditionalLog(requestLogs, commonTransactionSiskoRequestLog, req.getString(11), context);
        return commonTransactionSiskoRequestLog;
    }

    public void requestState(CommonTransactionRequestLog commonTransactionRequestLog, ISOMsg res) {
        if (res != null)
            transactionRequestLogService.saveState(commonTransactionRequestLog, res);
    }

    public void requestSiskoState(CommonTransactionSiskoRequestLog commonTransactionSiskoRequestLog, ISOMsg res) {
        if (res != null)
            transactionSiskoRequestLogService.saveState(commonTransactionSiskoRequestLog, res);
    }
}
