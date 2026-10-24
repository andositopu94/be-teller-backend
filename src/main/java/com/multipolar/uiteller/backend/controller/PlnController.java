package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.CommonTransactionLog;
import com.multipolar.uiteller.backend.model.CommonTransactionRequestLog;
import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.resource.BusinessException;
import com.multipolar.uiteller.backend.security.model.AccountResponse;
import com.multipolar.uiteller.backend.security.model.AccountResult;
import com.multipolar.uiteller.backend.service.CoreGatewayServiceImpl;
import com.multipolar.uiteller.backend.service.MonitoringLogService;
import com.multipolar.uiteller.backend.service.TraceNumberGenerator;
import com.multipolar.uiteller.backend.service.iso.MessageGateway;
import com.multipolar.uiteller.backend.service.logger.RequestLogService;
import com.multipolar.uiteller.backend.service.logger.TransactionLogService;
import com.multipolar.uiteller.backend.service.logger.TransactionLogger;
import com.multipolar.uiteller.backend.service.logger.TransactionLoggerFactory;
import com.multipolar.uiteller.backend.service.report.NotificationService;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apiv2")
public class PlnController extends DefaultController {

    @Autowired
    private MessageGateway gateway;

    @Autowired
    private TransactionLogService logService;

    @Autowired
    private CoreGatewayServiceImpl coreGatewayService;

    @Autowired
    private TransactionLoggerFactory loggerFactory;

    @Autowired
    private RequestLogService requestLogService;

    @Autowired
    private NotificationService notificationService;

    private static final long DEFAULT_REVERSAL_TIMEOUT = 50000L;

    @Autowired
    private TraceNumberGenerator traceGenerator;

    @Autowired
    private MonitoringLogService monitoringLogService;

    @PostMapping(value = {"/pln", "/nontaglist"})
    public ISOMsg request(@RequestBody ISOMsg msg)
            throws ConnectException, ISOException, InterruptedException, BusinessException, IOException {
        String feature = FeatureContextHolder.getContext().getFeatureName();
        final String accountNumber = msg.getString(102);
        AccountResponse accountResponse = coreGatewayService.getAccountResponse(accountNumber);
        if (accountResponse == null)
            throw new BusinessException("Time-out exception, host give no response");
        AccountResult accountResult = accountResponse.getResult();
        if (notificationService.transactionException(accountResult, msg) != null)
            return notificationService.transactionException(accountResult, msg);
        String accountName = accountResult.getNICKNM();
        FeatureContextHolder.getContext().setSourceName(accountName);
        CommonTransactionRequestLog commonTransactionRequestLog = requestLogService.request(feature, msg);
        ISOMsg response = gateway.sendToHost(msg);
        String bit48 = response.getString(48);

        // reversal here if response code = 08
        if (response != null && StringUtils.equals(response.getString(39), "08")) {
            ISOMsg reverseResponse = reverse(msg);
            if (reverseResponse != null && StringUtils.equals(reverseResponse.getString(39), "50")) {
                response = (ISOMsg) reverseResponse.clone();
                response.set(39, "00");
                //response reversal ist ga kirim bit48
                if(response.getString(48).isEmpty()){
                    response.set(48,bit48);
                }
            } else if (reverseResponse != null && StringUtils.equals(reverseResponse.getString(39), "00")) {
                response = (ISOMsg) reverseResponse.clone();
                response.set(39, "999");
            } else if (reverseResponse == null) {
                response.set(39, "888");
            }
        }

        if (response != null) {
            CommonTransactionLog commonLog = logService.logTransaction(msg, response);

            Map<String, Object> map = FeatureContextHolder.getContext().getReportData();
            TransactionLogger transactionLogger = loggerFactory.get(feature);
            List<TransactionLog> logs = transactionLogger.addLog(map);

            // hack for admin charge
            String requestBit28 = msg.getString("28");
            String responseBit28 = response != null ? response.getString("28") : "0";
            if (StringUtils.equals("000000000", requestBit28)) {
                String adminCharge = responseBit28 != null ? responseBit28 : "0";
                TransactionLog chargeLog = new TransactionLog("adminCharge", StringUtils.stripStart(adminCharge, "0"));
                logs.add(chargeLog);
            } else {
                TransactionLog chargeLog = new TransactionLog("adminCharge", "0");
                logs.add(chargeLog);
            }

            logService.saveAdditionalLog(logs, commonLog, response.getString(11), feature);
            requestLogService.requestState(commonTransactionRequestLog, response);
            monitoringLogService.delayTransactionStatusCall(commonLog, msg, response);
        } else {
            throw new BusinessException("Time-out exception, host give no response");
        }

        return response;
    }

    private ISOMsg reverse(ISOMsg payRequest) throws ISOException {
        // set bit MTI
        ISOMsg msg = (ISOMsg) payRequest.clone();
        String bit90 = msg.getMTI() + msg.getString(11) + msg.getString(7) + StringUtils.leftPad(payRequest.getString(32), 11, " ") + "           ";
        //req ist tracenya sama
        //msg.set(11, traceGenerator.getTraceNumber());
        msg.set(90, bit90);
        msg.setMTI("0420");

        int index = 0;
        ISOMsg response = null;
        while (index < 4 && response == null) {
            try {
                response = gateway.sendToHost(msg, DEFAULT_REVERSAL_TIMEOUT);
                //msg.set(11, traceGenerator.getTraceNumber());
            } catch (ConnectException e) {
                e.printStackTrace();
            }
            index++;
        }

        return response;
    }
}
