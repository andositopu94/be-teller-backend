package com.multipolar.uiteller.backend.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jfree.util.Log;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.multipolar.uiteller.backend.model.CommonTransactionLog;
import com.multipolar.uiteller.backend.model.CommonTransactionRequestLog;
import com.multipolar.uiteller.backend.model.MonitoringLog;
import com.multipolar.uiteller.backend.model.SuspectLogAction;
import com.multipolar.uiteller.backend.model.SuspectLogMonitoring;
import com.multipolar.uiteller.backend.model.SysParam;
import com.multipolar.uiteller.backend.model.TimeoutResource;
import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.model.dto.SuspectMonitoring;
import com.multipolar.uiteller.backend.repository.CommonTransactionLogRepository;
import com.multipolar.uiteller.backend.repository.MonitoringLogRepository;
import com.multipolar.uiteller.backend.repository.SuspectLogActionRepository;
import com.multipolar.uiteller.backend.repository.SuspectLogMonitoringRepository;
import com.multipolar.uiteller.backend.repository.SysParamRepository;
import com.multipolar.uiteller.backend.repository.TimeoutResourceRepository;
import com.multipolar.uiteller.backend.repository.TransactionLogRepository;
import com.multipolar.uiteller.backend.repository.TransactionRequestLogRepository;
import com.multipolar.uiteller.backend.resource.BusinessException;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.ValidationResponse;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;

@Service
public class SuspectLogMonitoringServiceImpl implements SuspectLogMonitoringService {
    @Autowired
    private MiddlewareGatewayServiceImpl middlewareGatewayService;
    @Autowired
    private MonitoringLogRepository monitoringLogRepository;
    @Autowired
    private CommonTransactionLogRepository commonTransactionLogRepository;
    @Autowired
    private TransactionLogRepository transactionLogRepository;
    @Autowired
    private TransactionRequestLogRepository transactionRequestLogRepository;
    @Autowired
    private TimeoutResourceRepository timeoutResourceRepository;
    @Autowired
    private SuspectLogMonitoringRepository suspectLogMonitoringRepository;
    @Autowired
    private SuspectLogActionRepository suspectLogActionRepository;
    @Autowired
    private SysParamRepository sysParamRepository;

    private static final Map<String, String> MAP_ID;
    private static final Map<String, String> MAP_SOURCE_NUM;
    
    private static final String CALL_STATUS_DELAY_PARAM = "webteller.teller.cms.statusCall.delay";

    static {
        MAP_ID = new HashMap<>();
        MAP_ID.put("tirtanadi", "customerId");
        MAP_ID.put("tirtaayumiDti", "customerId");
        MAP_ID.put("tirtakualoDti", "customerId");
        MAP_ID.put("tirtatanjungDti", "customerId");
        MAP_ID.put("tirtakualo", "customerId");
        MAP_ID.put("tirtabulian", "customerId");
        MAP_ID.put("tirtauli", "customerId");
        MAP_ID.put("installation", "customerId");
        MAP_ID.put("tirtaayumiDtiInstallation", "customerId");
        MAP_ID.put("tirtakualoDtiInstallation", "customerId");
        MAP_ID.put("tirtatanjungDtiInstallation", "customerId");
        MAP_ID.put("speedy", "customerId");
        MAP_ID.put("pbb", "noSts");
        MAP_ID.put("djp", "billingCode");
        MAP_ID.put("djbc", "billingCode");
        MAP_ID.put("dja", "billingCode");
        MAP_ID.put("djpg3", "billingCode");
        MAP_ID.put("djbcg3", "billingCode");
        MAP_ID.put("djag3", "billingCode");
        MAP_ID.put("pstn", "phoneNumber");
        MAP_ID.put("bpjs", "customerId");
        MAP_ID.put("pln", "meterNumber");
        MAP_ID.put("nontaglist", "registrationNumber");
        MAP_ID.put("samsat", "payCode");
        MAP_ID.put("samsatNasional", "payCode");
        MAP_ID.put("token", "meterNumber");
        MAP_ID.put("bpihinitial", "sourceNumber");
        MAP_ID.put("bpihinitialPrint", "validationNumber");
        MAP_ID.put("bpihPayment", "porsiNumber");
        MAP_ID.put("bpihPaymentPrint", "porsiNumber");
        MAP_ID.put("edupay", "kodeBilling");
        MAP_ID.put("eduinit", "identitas");
        MAP_ID.put("bpihinitialPrintDb", "validationNumber");
        MAP_ID.put("bpihPaymentPrintDb", "porsiNumber");
        MAP_ID.put("djpReinquiry", "billingCode");
        MAP_ID.put("djbcReinquiry", "billingCode");
        MAP_ID.put("djaReinquiry", "billingCode");
        MAP_ID.put("djpg3Reinquiry", "billingCode");
        MAP_ID.put("djbcg3Reinquiry", "billingCode");
        MAP_ID.put("djag3Reinquiry", "billingCode");
        MAP_ID.put("bifast", "destinationNumber");
        MAP_ID.put("pdamDti", "customerId");
        MAP_ID.put("pdamDtiInstallation", "customerId");
    }
    
    static {
        MAP_SOURCE_NUM = new HashMap<>();
        MAP_SOURCE_NUM.put("tirtanadi", "sourceNumber");
        MAP_SOURCE_NUM.put("tirtaayumiDti", "sourceNumber");
        MAP_SOURCE_NUM.put("tirtakualoDti", "sourceNumber");
        MAP_SOURCE_NUM.put("tirtatanjungDti", "sourceNumber");
        MAP_SOURCE_NUM.put("tirtakualo", "sourceNumber");
        MAP_SOURCE_NUM.put("tirtabulian", "sourceNumber");
        MAP_SOURCE_NUM.put("tirtauli", "sourceNumber");
        MAP_SOURCE_NUM.put("installation", "sourceNumber");
        MAP_SOURCE_NUM.put("tirtaayumiDtiInstallation", "sourceNumber");
        MAP_SOURCE_NUM.put("tirtakualoDtiInstallation", "sourceNumber");
        MAP_SOURCE_NUM.put("tirtatanjungDtiInstallation", "sourceNumber");
        MAP_SOURCE_NUM.put("speedy", "sourceNumber");
        MAP_SOURCE_NUM.put("pbb", "rekeningSumber");
        MAP_SOURCE_NUM.put("djp", "sourceNumber");
        MAP_SOURCE_NUM.put("djbc", "sourceNumber");
        MAP_SOURCE_NUM.put("dja", "sourceNumber");
        MAP_SOURCE_NUM.put("djpg3", "sourceNumber");
        MAP_SOURCE_NUM.put("djbcg3", "sourceNumber");
        MAP_SOURCE_NUM.put("djag3", "sourceNumber");
        MAP_SOURCE_NUM.put("pstn", "sourceNumber");
        MAP_SOURCE_NUM.put("bpjs", "sourceNumber");
        MAP_SOURCE_NUM.put("pln", "sourceNumber");
        MAP_SOURCE_NUM.put("nontaglist", "sourceNumber");
        MAP_SOURCE_NUM.put("samsat", "sourceNumber");
        MAP_SOURCE_NUM.put("samsatNasional", "sourceNumber");
        MAP_SOURCE_NUM.put("token", "sourceNumber");
        MAP_SOURCE_NUM.put("bpihinitial", "sourceNumber");
        MAP_SOURCE_NUM.put("bpihinitialPrint", "sourceNumber");
        MAP_SOURCE_NUM.put("bpihPayment", "sourceNumber");
        MAP_SOURCE_NUM.put("bpihPaymentPrint", "sourceNumber");
        MAP_SOURCE_NUM.put("edupay", "sourceNumber");
        MAP_SOURCE_NUM.put("eduinit", "sourceNumber");
        MAP_SOURCE_NUM.put("bpihinitialPrintDb", "sourceNumber");
        MAP_SOURCE_NUM.put("bpihPaymentPrintDb", "sourceNumber");
        MAP_SOURCE_NUM.put("djpReinquiry", "sourceNumber");
        MAP_SOURCE_NUM.put("djbcReinquiry", "sourceNumber");
        MAP_SOURCE_NUM.put("djaReinquiry", "sourceNumber");
        MAP_SOURCE_NUM.put("djpg3Reinquiry", "sourceNumber");
        MAP_SOURCE_NUM.put("djbcg3Reinquiry", "sourceNumber");
        MAP_SOURCE_NUM.put("djag3Reinquiry", "sourceNumber");
        MAP_SOURCE_NUM.put("bifast", "sourceNumber");
        MAP_SOURCE_NUM.put("pdamDti", "customerId");
        MAP_SOURCE_NUM.put("pdamDtiInstallation", "customerId");
    }

    @Override
    public void suspectTransactionLog(CommonTransactionLog commonTransactionLog, CommonTransactionRequestLog commonTransactionRequestLog, String trace, String ref, String status) {
        if (StringUtils.equals(status, Constants.SUCCESS_CODE))
            return;
        ValidationResponse validationResponse;
        TimeoutResource timeoutResource = timeoutResourceRepository.findFirstByTrxTypeAndCode(FeatureContextHolder.getContext().getFeatureName(), status);
        SuspectLogMonitoring suspectLogMonitoring = new SuspectLogMonitoring();
        try {
            if (timeoutResource == null) {
                suspectLogMonitoring.setStatus(Constants.OTHER_ERROR_RCODE);
                suspectLogMonitoring.setDescription(Constants.OTHER_ERROR_DESCRIPTION);
            } else {
                validationResponse = middlewareGatewayService.getStatusResponse(commonTransactionLog, trace, ref);
                suspectLogMonitoring.setStatus(validationResponse.getRCode());
                suspectLogMonitoring.setDescription(validationResponse.getMessage());
            }
        } catch (BusinessException e) {
            e.printStackTrace();
            suspectLogMonitoring.setStatus(Constants.STRIP_CODE);
            suspectLogMonitoring.setDescription(Constants.STRIP_CODE);
        } finally {
            suspectLogMonitoringRepository.save(suspectLogMonitoring);
        }
    }

    @Override
    public List<SuspectLogMonitoring> userSuspectLogMonitoring(String user) {
    	//SuspectLogMonitoring suspectLogMonitoring = new SuspectLogMonitoring();
        return suspectLogMonitoringRepository.findByUserId(user);
    }

    @Override
    public List<SuspectMonitoring> getSuspectMonitoringByUser(String user) {
        List<SuspectMonitoring> suspectMonitoringList = new ArrayList<>();
//        List<SuspectLogMonitoring> suspectLogMonitoringList = suspectLogMonitoringRepository.findByUserId(user);
        List<SuspectLogMonitoring> suspectLogMonitoringList = suspectLogMonitoringRepository.findByUserIdToday(user);
        for(SuspectLogMonitoring suspectLogMonitoring : suspectLogMonitoringList){
            SuspectMonitoring suspectMonitoring = new SuspectMonitoring();
            Long commonTransactionId = suspectLogMonitoring.getCommonTransactionLog().getId();

            Map<String, String> txMap = getTransaction(commonTransactionId);
            String trxType = txMap.get(Constants.TRANSACTION_TYPE_COLUMN_NAME);
            trxType=getMappedTrxType(trxType);

            suspectMonitoring.setMiddlewareStatus(suspectLogMonitoring.getStatus());
            suspectMonitoring.setDescription(suspectLogMonitoring.getDescription());
            suspectMonitoring.setId(suspectLogMonitoring.getId());

            suspectMonitoring.setBranchCode(suspectLogMonitoring.getCommonTransactionLog().getBranchCode());
            suspectMonitoring.setUserid(suspectLogMonitoring.getCommonTransactionLog().getUserid());
            suspectMonitoring.setTransactionDate(suspectLogMonitoring.getCommonTransactionLog().getTrxDate());
            suspectMonitoring.setStatus(suspectLogMonitoring.getCommonTransactionLog().getRc());

            suspectMonitoring.setArchiveNumber(txMap.get(Constants.ARCHIVE_NAME_FIELD_NAME));
            suspectMonitoring.setCustomerName(txMap.get(Constants.SOURCE_NAME_FIELD_NAME));
            suspectMonitoring.setNominal(txMap.get(Constants.NOMINAL_FIELD_NAME));
            suspectMonitoring.setReference(txMap.get(Constants.REFFERENCE_FIELD_NAME));
            suspectMonitoring.setSourceNumber(txMap.get(MAP_SOURCE_NUM.get(trxType)));
            suspectMonitoring.setTraceNumber(txMap.get(Constants.TRACE_NUMBER_COLUMN_NAME));
            suspectMonitoring.setTransactionType(txMap.get(Constants.TRANSACTION_TYPE_COLUMN_NAME));
            suspectMonitoring.setCustomerId(txMap.get(MAP_ID.get(trxType)));

            suspectMonitoring.setCommonRequestId(transactionRequestLogRepository.findCommonIdFromTraceNumber(txMap.get(Constants.TRACE_NUMBER_COLUMN_NAME), txMap.get(Constants.REFFERENCE_FIELD_NAME)));

            suspectMonitoringList.add(suspectMonitoring);
        }
        return suspectMonitoringList;
    }

    public Map<String, String> getTransaction(Long commonTransactionId){
        Map<String, String> txMap = new HashMap<>();
        String traceNumber = null;
        String trxType = null;
        for(TransactionLog transactionLog : transactionLogRepository.findByCommonTransactionId(commonTransactionId)){
            txMap.put(transactionLog.getFieldName(), transactionLog.getFieldValue());
            traceNumber = transactionLog.getTraceNumber();
            trxType = transactionLog.getTrxType();
        }
        txMap.put(Constants.TRACE_NUMBER_COLUMN_NAME, traceNumber);
        txMap.put(Constants.TRANSACTION_TYPE_COLUMN_NAME, trxType);
        return txMap;
    }

    @Override
    public List<SuspectLogMonitoring> adminSuspectLogMonitoring() {
        return suspectLogMonitoringRepository.findAll();
    }

    @Override
    public List<SuspectMonitoring> getSuspectMonitoringForAdmin() {
        List<SuspectMonitoring> suspectMonitoringList = new ArrayList<>();
        List<SuspectLogMonitoring> suspectLogMonitoringList = suspectLogMonitoringRepository.findByAll();
        for(SuspectLogMonitoring suspectLogMonitoring : suspectLogMonitoringList){
            SuspectMonitoring suspectMonitoring = new SuspectMonitoring();
            Long commonTransactionId = suspectLogMonitoring.getCommonTransactionLog().getId();

            Map<String, String> txMap = getTransaction(commonTransactionId);
            String trxType = txMap.get(Constants.TRANSACTION_TYPE_COLUMN_NAME);
            trxType=getMappedTrxType(trxType);

            suspectMonitoring.setMiddlewareStatus(suspectLogMonitoring.getStatus());
            suspectMonitoring.setDescription(suspectLogMonitoring.getDescription());
            suspectMonitoring.setId(suspectLogMonitoring.getId());

            suspectMonitoring.setBranchCode(suspectLogMonitoring.getCommonTransactionLog().getBranchCode());
            suspectMonitoring.setUserid(suspectLogMonitoring.getCommonTransactionLog().getUserid());
            suspectMonitoring.setTransactionDate(suspectLogMonitoring.getCommonTransactionLog().getTrxDate());
            suspectMonitoring.setStatus(suspectLogMonitoring.getCommonTransactionLog().getRc());

            suspectMonitoring.setArchiveNumber(txMap.get(Constants.ARCHIVE_NAME_FIELD_NAME));
            suspectMonitoring.setCustomerName(txMap.get(Constants.SOURCE_NAME_FIELD_NAME));
            suspectMonitoring.setNominal(txMap.get(Constants.NOMINAL_FIELD_NAME));
            suspectMonitoring.setReference(txMap.get(Constants.REFFERENCE_FIELD_NAME));
            suspectMonitoring.setSourceNumber(txMap.get(MAP_SOURCE_NUM.get(trxType)));
            suspectMonitoring.setTraceNumber(txMap.get(Constants.TRACE_NUMBER_COLUMN_NAME));
            suspectMonitoring.setTransactionType(txMap.get(Constants.TRANSACTION_TYPE_COLUMN_NAME));
            suspectMonitoring.setCustomerId(txMap.get(MAP_ID.get(trxType)));

            suspectMonitoring.setCommonRequestId(transactionRequestLogRepository.findCommonIdFromTraceNumber(txMap.get(Constants.TRACE_NUMBER_COLUMN_NAME), txMap.get(Constants.REFFERENCE_FIELD_NAME)));
            suspectMonitoring.setAction(suspectLogActionRepository.findLastActionBySuspectId(suspectLogMonitoring.getId()));

            suspectMonitoringList.add(suspectMonitoring);
        }
        return suspectMonitoringList;
    }

    @Override
    public void waitingStatus(Long id) {
        MonitoringLog monitoringLog = monitoringLogRepository.findOne(id);
        CommonTransactionLog commonTransactionLog = commonTransactionLogRepository.findOne(monitoringLog.getCommonId());
        ValidationResponse validationResponse;
        try {
            validationResponse = middlewareGatewayService.getStatusResponse(commonTransactionLog, monitoringLog.getTraceNumber(), monitoringLog.getReference());
            if (StringUtils.equals(monitoringLog.getMiddlewareStatus(), validationResponse.getRCode()))
                return;
            SuspectLogMonitoring suspectLogMonitoring = suspectLogMonitoringRepository.findOne(monitoringLog.getId());
            suspectLogMonitoring.setStatus(validationResponse.getRCode());
            suspectLogMonitoring.setDescription(validationResponse.getMessage());
            suspectLogMonitoringRepository.save(suspectLogMonitoring);
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void waitingStatusSuspect(Long id) {
    	SuspectLogMonitoring suspectLogMonitoring = suspectLogMonitoringRepository.findOne(id);
        CommonTransactionLog commonTransactionLog = commonTransactionLogRepository.findOne(suspectLogMonitoring.getCommonTransactionLog().getId());
        
        if (!this.shouldCallCmsStatus(commonTransactionLog))
        	return;
        
        ValidationResponse validationResponse;
        Map<String, String> txMap = getTransaction(suspectLogMonitoring.getCommonTransactionLog().getId());
        try {
            validationResponse = middlewareGatewayService.getStatusResponse(commonTransactionLog, txMap.get("traceNumber"), txMap.get("ref"));
            if (StringUtils.equals(suspectLogMonitoring.getStatus(), validationResponse.getRCode()))
                return;
            suspectLogMonitoring.setStatus(validationResponse.getRCode());
            suspectLogMonitoring.setDescription(validationResponse.getMessage());
            suspectLogMonitoringRepository.save(suspectLogMonitoring);
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public SuspectLogAction newSuspectLogAction(SuspectLogAction suspectLogAction) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        suspectLogAction.setUserid(auth.getName());
        SuspectLogMonitoring suspectLogMonitoring = suspectLogMonitoringRepository.findOne(suspectLogAction.getSuspectLogMonitoring().getId());
        String status = suspectLogMonitoring.getStatus();
        Integer statusConv = Integer.valueOf(status) * -1;
        suspectLogMonitoring.setStatus(statusConv.toString());
        suspectLogMonitoringRepository.save(suspectLogMonitoring);
        return suspectLogActionRepository.save(suspectLogAction);
    }

    @Override
    public SuspectLogMonitoring updateSuspect(Long id, String param) {
        SuspectLogMonitoring suspectLogMonitoring = suspectLogMonitoringRepository.findOne(id);
        if(param.equals("KOREKSI")) {
            suspectLogMonitoring.setStatus("07");
        } else if (param.equals("CETAK ULANG")){
            suspectLogMonitoring.setStatus("00");
        }
        return suspectLogMonitoringRepository.save(suspectLogMonitoring);
    }

    private boolean shouldCallCmsStatus(CommonTransactionLog commonLog) {
    	Long delayInSecond = 180L;
    	SysParam delayStatusCallParam = sysParamRepository.findByPropertyName(CALL_STATUS_DELAY_PARAM);
    	if (delayStatusCallParam == null || !StringUtils.isNumeric(delayStatusCallParam.getPropertyValue()))
    		Log.warn("CMS call delay not set in System Parameter Table");
    	else
    		delayInSecond = Long.parseLong(delayStatusCallParam.getPropertyValue());
    	
    	Date trxDate = commonLog.getTrxDate();
    	DateTime trxDateTime = new DateTime(trxDate);
    	
    	DateTime now = DateTime.now();
    	Seconds diff = Seconds.secondsBetween(trxDateTime, now);
    	
    	return diff.getSeconds() > delayInSecond;
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
