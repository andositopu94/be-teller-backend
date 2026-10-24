package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.CommonTransactionLog;
import com.multipolar.uiteller.backend.model.CommonTransactionRequestLog;
import com.multipolar.uiteller.backend.model.ManualAdvice;
import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.repository.ManualAdviceRepository;
import com.multipolar.uiteller.backend.resource.BusinessException;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.AccountResponse;
import com.multipolar.uiteller.backend.security.model.AccountResult;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
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
import org.jpos.iso.packager.ISO87APackager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/apiv2")
public class PrepaidController extends DefaultController {

    @Autowired
    private MessageGateway gateway;

    @Autowired
    private TransactionLogService logService;

    @Autowired
    private CoreGatewayServiceImpl coreGatewayService;

    @Autowired
    private TransactionLoggerFactory loggerFactory;

    @Autowired
    private ManualAdviceRepository adviceRepository;

    @Autowired
    private RequestLogService requestLogService;

    @Autowired
    private NotificationService notificationService;

    private static final long DEFAULT_REVERSAL_TIMEOUT = 50000L;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMddkkmmss");
    private static final SimpleDateFormat LOCAL_DATE = new SimpleDateFormat("MMdd");
    private static final SimpleDateFormat LOCAL_TIME = new SimpleDateFormat("kkmmss");

    @Autowired
    private TraceNumberGenerator traceGenerator;
    @Autowired
    private MonitoringLogService monitoringLogService;

    @PostMapping("/token")
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

        // reversal here if response code = 08
        if (response != null && StringUtils.equals(response.getString(39), "08")) {
            ISOMsg reverseResponse = reverse(msg);
            if (reverseResponse != null && StringUtils.equals(reverseResponse.getString(39), "00")) {
                response = (ISOMsg) reverseResponse.clone();
                response.set(39, "00");
            } else if (reverseResponse != null) {
                response = (ISOMsg) reverseResponse.clone();
                response.set(39, "999");
            } else if (reverseResponse == null) {
                Map<String, Object> reportData = FeatureContextHolder.getContext().getReportData();

                ManualAdvice advice = new ManualAdvice();
                ISOMsg savedRequest = (ISOMsg) msg.clone();
                String bit90 = msg.getMTI() + msg.getString(11) + msg.getString(7) + StringUtils.leftPad(msg.getString(32), 11, " ") + "           ";
                savedRequest.set(90, bit90);
                savedRequest.setMTI("0220");
                advice.setRefNum(msg.getString(37));
                byte[] result = savedRequest.pack();
                advice.setRequestContent(new String(result));
                advice.setAccountName(accountName);
                advice.setPayType((String) reportData.get("payType"));
                adviceRepository.save(advice);
                response.set(39, "777");
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

    @PostMapping("/advice")
    public ISOMsg manualAdvice(@RequestParam String refNum) throws BusinessException, ISOException, IOException {
        ManualAdvice advice = adviceRepository.findByRefNum(refNum);
        if (advice == null)
            throw new BusinessException("Transaksi dengan reference number: " + refNum + " tidak bisa ditemukan");

        String requestContent = advice.getRequestContent();
        ISO87APackager packager = new ISO87APackager();
        ISOMsg msg = new ISOMsg();
        msg.setPackager(packager);
        msg.unpack(requestContent.getBytes());

        // waktu
        Date date = new Date();
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZONE_GMT));
        //msg.setMTI("0200");
        //msg.set(3, "380000");
        msg.set(7, DATE_FORMAT.format(date));
        msg.set(12, LOCAL_TIME.format(date));
        msg.set(13, LOCAL_DATE.format(date));
        msg.set(15, LOCAL_DATE.format(date));
        //msg.set(61, "0000000000000              " + refNum);
        //msg.set(63, "04012");

        //msg.unset(90);
        msg.set(3,"500001");

        // user detail
        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String branchId = userDetail.getBranchCode();
        String userid = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        msg.set(41, StringUtils.leftPad(branchId + userid, 8));
        String location = userDetail.getBranchName();
        msg.set(43, StringUtils.rightPad(location, 40));

        String traceNumber = traceGenerator.getTraceNumber();
        msg.set(11, traceNumber);

        int index = 0;
        ISOMsg response = null;
        while (index < 3 && response == null) {
            try {
                //req ist untuk repeat advice mti 0221, advice pertama 0220
                if(index!=0){msg.setMTI("0221");}
                response = gateway.sendToHost(msg, DEFAULT_REVERSAL_TIMEOUT);
            } catch (ConnectException e) {
                e.printStackTrace();
            }
            index++;
        }

        if (response == null)
            throw new BusinessException("Host tidak mengirimkan response dalam batas waktu time-out");

        byte[] result = response.pack();
        advice.setResponseContent(new String(result));
        adviceRepository.save(advice);

        FeatureContextHolder.getContext().setSourceName(advice.getAccountName());
        FeatureContextHolder.getContext().setFeatureTransactionDate(date);
        FeatureContextHolder.getContext().setTraceNumber(traceNumber);

        FeatureContextHolder.getContext().setFeatureName("token");
        CommonTransactionLog commonLog = logService.logTransaction(msg, response);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("payType", advice.getPayType());
        TransactionLogger transactionLogger = loggerFactory.get("token");
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

        logService.saveAdditionalLog(logs, commonLog, msg.getString(11), "token");

        return response;
    }

    private ISOMsg reverse(ISOMsg payRequest) throws ISOException {
        // set bit MTI
        ISOMsg msg = (ISOMsg) payRequest.clone();
        String bit90 = msg.getMTI() + msg.getString(11) + msg.getString(7) + StringUtils.leftPad(payRequest.getString(32), 11, " ") + "           ";
        //req ist trace sama dengan pay
        // msg.set(11, traceGenerator.getTraceNumber());
        msg.set(90, bit90);
        msg.setMTI("0220");
        //req IST untuk advice pakai pcode 500001
        msg.set(3,"500001");

        int index = 0;
        ISOMsg response = null;
        while (index < 3 && response == null) {
            try {
                response = gateway.sendToHost(msg, DEFAULT_REVERSAL_TIMEOUT);
            } catch (ConnectException e) {
                e.printStackTrace();
            }
            index++;
        }

        return response;
    }
}
