package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.CommonTransactionLog;
import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.model.TransactionRequestLog;
import com.multipolar.uiteller.backend.repository.TransactionRequestLogRepository;
import com.multipolar.uiteller.backend.resource.BusinessException;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.resource.Troubleshoot;
import com.multipolar.uiteller.backend.security.model.AccountResponse;
import com.multipolar.uiteller.backend.security.model.AccountResult;
import com.multipolar.uiteller.backend.security.model.Forwarding;
import com.multipolar.uiteller.backend.service.CoreGatewayServiceImpl;
import com.multipolar.uiteller.backend.service.MonitoringLogService;
import com.multipolar.uiteller.backend.service.iso.MessageGateway;
import com.multipolar.uiteller.backend.service.logger.*;
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
import java.util.*;

@RestController
@RequestMapping("/apiv4")
public class ForwardingController {

    @Autowired
    private ForwardingLogService forwardingLogService;
    @Autowired
    private MessageGateway messageGateway;
    @Autowired
    private CoreGatewayServiceImpl coreGatewayService;
    @Autowired
    private TransactionLogService logService;
    @Autowired
    private TransactionLoggerFactory loggerFactory;
    @Autowired
    private PbbLogService pbbLogService;
    @Autowired
    private UniversityinitLogService universityinitLogService;
    @Autowired
    private UniversityLogService universityLogService;
    @Autowired
    private PSTNLogService pstnLogService;
    @Autowired
    private SpeedyLogService speedyLogService;
    @Autowired
    private BpjsKerjaBpuLogService bpjsKerjaBpuLogService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private MonitoringLogService monitoringLogService;
    @Autowired
    private TransactionRequestLogRepository transactionRequestLogRepository;
    private static final String PAYMENT_PCODE = "500000";
    private static final String CHECK_PAYMENT_PCODE = "502000";
    private static final String PBB_PAYMENT = "pbb";
    private static final String EDUPAY_PAYMENT = "edupay";
    private static final String EDUINIT_PAYMENT = "eduinit";
    private static final String PSTN_PAYMENT = "pstn";
    private static final String SPEEDY_PAYMENT = "speedy";
    private static final String BPJS_KERJA_BPU = "bpjsKerjaBpu";
    private static final String CUSTOMER_NAME = "customerName";
    private static final String FULL_NAME = "fullName";
    private static final String TAX_NUMBER = "taxNumber";

    @PostMapping("/**")
    public ISOMsg getTransaction(@RequestBody Forwarding id) throws ISOException, InterruptedException, BusinessException, IOException {
        TransactionRequestLog logs;
        ISOMsg msg;
        List<TransactionLog> transactionLogs = new ArrayList<>();
        String source, branch, archive, trace, customerName = null, taxNumber = null;
        String context = FeatureContextHolder.getContext().getFeatureName();
        Boolean pbbsts = StringUtils.equals(context, PBB_PAYMENT);
        Boolean edusts = StringUtils.equals(context, EDUPAY_PAYMENT);
        Boolean eduinitsts = StringUtils.equals(context, EDUINIT_PAYMENT);
        Boolean pstn = StringUtils.equals(context, PSTN_PAYMENT);
        Boolean speedy = StringUtils.equals(context, SPEEDY_PAYMENT);
        Boolean bpjsKerjaBpu = StringUtils.equals(context, BPJS_KERJA_BPU);
        msg = notificationService.validationException(id.getId());
        if (msg != null)
            return msg;
        source = forwardingLogService.getRequestTransactionLog(Long.parseLong(id.getId()), Constants.SOURCE_NAME_FIELD_NAME).getFieldValue();
        FeatureContextHolder.getContext().setSourceName(source);
        FeatureContextHolder.getContext().setFeatureTransactionDate(new Date());
        logs = forwardingLogService.getRequestTransactionLog(Long.parseLong(id.getId()), Constants.REQUEST_TIMEOUT_FIELD_NAME);
        trace = logs.getTraceNumber();
        ISOMsg response = messageGateway.sendRorwardingToHost(logs.getFieldValue());
        ISOMsg isoMsg = FeatureContextHolder.getContext().getRequest();
        if (response == null)
            throw new BusinessException("Time-out exception, host give no response");
        forwardingLogService.setFlag(logs, response);
        if(bpjsKerjaBpu){
            List<TransactionRequestLog> dummy = transactionRequestLogRepository.findByCommonTransactionRequestId(Long.valueOf(id.getId()));
            Set<TransactionRequestLog> listBranch = new HashSet<>();
            Map<String, List<TransactionRequestLog>> backupnow = new HashMap<>();
            for( TransactionRequestLog dummyReps : dummy){
                if(StringUtils.equals(dummyReps.getFieldName(), "branchName")){
                    String traceNew = dummyReps.getTraceNumber();
                    if(backupnow.containsKey(traceNew)) {
                        List<TransactionRequestLog> backup2 = backupnow.get(traceNew);
                        backup2.add(dummyReps);
                        backupnow.put(traceNew, backup2);
                    } else {
                        List<TransactionRequestLog> backup3 = new ArrayList<>();
                        backup3.add(dummyReps);
                        backupnow.put(traceNew, backup3);
                    }
                }
            }
            for(Map.Entry<String, List<TransactionRequestLog>> newEntry : backupnow.entrySet()){
                String traceNumber = newEntry.getKey();
                List<TransactionRequestLog> backup4 = newEntry.getValue();
                List<TransactionRequestLog> toDelete;
                if(backup4.size() > 1){
                    toDelete = backup4.subList(1, backup4.size());
                } else {
                    toDelete = Collections.emptyList();
                }
                listBranch.addAll(toDelete);
            }
            transactionRequestLogRepository.delete(listBranch);
        }

        TransactionLogger transactionLogger = loggerFactory.get(context);
        if (StringUtils.equals(response.getString(39), Constants.SUCCESS_CODE)) {
            branch = forwardingLogService.getRequestTransactionLog(Long.parseLong(id.getId()), Constants.BRANCH_NAME_FIELD_NAME).getFieldValue();
            archive = forwardingLogService.getRequestTransactionLog(Long.parseLong(id.getId()), Constants.ARCHIVE_NAME_FIELD_NAME).getFieldValue();
            if (pbbsts) {
                final String destAccountNumber = response.getString(103);
                AccountResponse destAccountResponse = coreGatewayService.getAccountResponse(destAccountNumber);
                if (destAccountResponse == null)
                    throw new BusinessException("Time-out exception, host give no response");
                AccountResult destAccountResult = destAccountResponse.getResult();
                if (destAccountResult != null) {
                    String destAccountName = destAccountResult.getNICKNM();
                    FeatureContextHolder.getContext().setDestinationName(destAccountName);
                } else {
                    String destAccountName = Constants.STRIP_CODE;
                    FeatureContextHolder.getContext().setDestinationName(Constants.STRIP_CODE);
                }
                pbbLogService.setReportData(response, trace);
            }
            if (eduinitsts)
                universityinitLogService.setReportData(response, trace);
            if (edusts)
                universityLogService.setReportData(response, trace);
            if (pstn){
                pstnLogService.setReportData(response, trace);
                customerName = forwardingLogService.getRequestTransactionLog(Long.parseLong(id.getId()), CUSTOMER_NAME).getFieldValue();
                taxNumber = forwardingLogService.getRequestTransactionLog(Long.parseLong(id.getId()), TAX_NUMBER).getFieldValue();
            }
            if (speedy){
                speedyLogService.setReportData(response, trace);
                customerName = forwardingLogService.getRequestTransactionLog(Long.parseLong(id.getId()), CUSTOMER_NAME).getFieldValue();
                taxNumber = forwardingLogService.getRequestTransactionLog(Long.parseLong(id.getId()), TAX_NUMBER).getFieldValue();
            }
            if (bpjsKerjaBpu){
                bpjsKerjaBpuLogService.setReportData(response, trace);
                customerName = forwardingLogService.getRequestTransactionLog(Long.parseLong(id.getId()), FULL_NAME).getFieldValue();
//                taxNumber = forwardingLogService.getRequestTransactionLog(Long.parseLong(id.getId()), TAX_NUMBER).getFieldValue();
            }
            if (StringUtils.equals(isoMsg.getString(3), PAYMENT_PCODE) || StringUtils.equals(isoMsg.getString(3), CHECK_PAYMENT_PCODE)) {
                CommonTransactionLog commonLog = logService.logTransaction(isoMsg, response);
                Map<String, Object> map = new HashMap<>();
                if (pbbsts || eduinitsts || edusts || pstn || speedy || bpjsKerjaBpu)
                    map.putAll(FeatureContextHolder.getContext().getReportData());
                map.put(Constants.SOURCE_NAME_FIELD_NAME, source);
                map.put(Constants.BRANCH_NAME_FIELD_NAME, branch);
                map.put(Constants.ARCHIVE_NAME_FIELD_NAME, archive);
                FeatureContextHolder.getContext().setTraceNumber(trace);
                transactionLogs.addAll(transactionLogger.addLog(map));
                String requestBit28 = isoMsg.getString("28");
                String responseBit28 = response != null ? response.getString("28") : "0";
                System.out.println("ADMIN CHARGE     " + requestBit28 + "/"+ responseBit28);
                if (StringUtils.equals("000000000", requestBit28)) {
                    String adminCharge = responseBit28 != null ? responseBit28 : "0";
                    TransactionLog chargeLog = new TransactionLog("adminCharge", StringUtils.stripStart(adminCharge, "0"));
                    transactionLogs.add(chargeLog);
                } else {
                    TransactionLog chargeLog = new TransactionLog("adminCharge", "0");
                    transactionLogs.add(chargeLog);
                }
                logService.saveAdditionalLog(transactionLogs, commonLog, isoMsg.getString(11), context);
                monitoringLogService.updateByCommonTransactionRequestLog(logs.getCommonTransactionRequest(), Troubleshoot.FORWARDING);
            }
        }
        return response;
    }
}
