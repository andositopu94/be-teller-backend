package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.CommonTransactionLog;
import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.model.TransactionSiskoRequestLog;
import com.multipolar.uiteller.backend.resource.BusinessException;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.SiskoForwarding;
import com.multipolar.uiteller.backend.service.CoreGatewayServiceImpl;
import com.multipolar.uiteller.backend.service.iso.MessageGateway;
import com.multipolar.uiteller.backend.service.iso.SiskoGateway;
import com.multipolar.uiteller.backend.service.logger.SiskoForwardingLogService;
import com.multipolar.uiteller.backend.service.logger.TransactionLogService;
import com.multipolar.uiteller.backend.service.logger.TransactionLogger;
import com.multipolar.uiteller.backend.service.logger.TransactionLoggerFactory;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apiv5")
public class SiskoForwardingController {

    @Autowired
    private SiskoForwardingLogService forwardingLogService;
    @Autowired
    private MessageGateway messageGateway;
    @Autowired
    private SiskoGateway siskoGateway;
    @Autowired
    private CoreGatewayServiceImpl coreGatewayService;
    @Autowired
    private TransactionLogService logService;
    @Autowired
    private TransactionLoggerFactory loggerFactory;
    private static final String PAYMENT_PCODE = "500000";

    @PostMapping("/**")
    public ISOMsg getTransaction(@RequestBody SiskoForwarding id) throws ConnectException, ISOException, InterruptedException, BusinessException, IOException {
        TransactionSiskoRequestLog transactionSiskoRequestLog;
        ISOMsg response;
        String source, branch, archive;
        source = forwardingLogService.getRequestSiskoTransactionLog(Long.parseLong(id.getId()), Constants.SOURCE_NAME_FIELD_NAME).getFieldValue();
        FeatureContextHolder.getContext().setSourceName(source);
        FeatureContextHolder.getContext().setFeatureTransactionDate(new Date());
        if (StringUtils.equals(id.getRev(), Constants.ZERO)) {
            transactionSiskoRequestLog = forwardingLogService.getRequestSiskoTransactionLog(Long.parseLong(id.getId()), Constants.REQUEST_TIMEOUT_FIELD_NAME);
            response = siskoGateway.sentForwardingToHost(transactionSiskoRequestLog.getFieldValue());
        } else {
            transactionSiskoRequestLog = forwardingLogService.getRequestSiskoTransactionLog(Long.parseLong(id.getId()), Constants.DEBIT_REQUEST_TIMEOUT_FIELD_NAME);
            response = messageGateway.sendReversalToHost(transactionSiskoRequestLog.getFieldValue());
            FeatureContextHolder.getContext().setFeatureName(FeatureContextHolder.getContext().getFeatureName() + Constants.REVERSAL_FEAUTURE_NAME);
        }
        ISOMsg isoMsg = FeatureContextHolder.getContext().getRequest();
        if (response == null)
            throw new BusinessException("Time-out exception, host give no response");
        forwardingLogService.setFlag(transactionSiskoRequestLog, response, id.getRev());
        String context = FeatureContextHolder.getContext().getFeatureName();
        if (StringUtils.equals(response.getString(39), Constants.SUCCESS_CODE) && !StringUtils.equals(response.getMTI(), Constants.REVERSAL_CODE)) {
            CommonTransactionLog commonLog = logService.logTransaction(isoMsg, response);
            branch = forwardingLogService.getRequestSiskoTransactionLog(Long.parseLong(id.getId()), Constants.BRANCH_NAME_FIELD_NAME).getFieldValue();
            archive = forwardingLogService.getRequestSiskoTransactionLog(Long.parseLong(id.getId()), Constants.ARCHIVE_NAME_FIELD_NAME).getFieldValue();
            Map<String, Object> map = new HashMap<>();
            map.put(Constants.SOURCE_NAME_FIELD_NAME, source);
            map.put(Constants.BRANCH_NAME_FIELD_NAME, branch);
            map.put(Constants.ARCHIVE_NAME_FIELD_NAME, archive);
            TransactionLogger transactionLogger = loggerFactory.get(context);
            List<TransactionLog> transactionLogs = transactionLogger.addLog(map);

            String requestBit28 = isoMsg.getString("28");
            String responseBit28 = response != null ? response.getString("28") : "0";
            if (StringUtils.equals("000000000", requestBit28)) {
                String adminCharge = responseBit28 != null ? responseBit28 : "0";
                TransactionLog chargeLog = new TransactionLog("adminCharge", StringUtils.stripStart(adminCharge, "0"));
                transactionLogs.add(chargeLog);
            } else {
                TransactionLog chargeLog = new TransactionLog("adminCharge", "0");
                transactionLogs.add(chargeLog);
            }

            logService.saveAdditionalLog(transactionLogs, commonLog, isoMsg.getString(11), context);
        }
        return response;
    }
}
