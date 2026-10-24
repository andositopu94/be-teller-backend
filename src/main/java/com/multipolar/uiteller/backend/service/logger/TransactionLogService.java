package com.multipolar.uiteller.backend.service.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipolar.uiteller.backend.model.*;
import com.multipolar.uiteller.backend.model.converter.ConverterRule;
import com.multipolar.uiteller.backend.model.converter.RequestRule;
import com.multipolar.uiteller.backend.model.converter.Rule;
import com.multipolar.uiteller.backend.model.converter.SiskoRule;
import com.multipolar.uiteller.backend.repository.*;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.ReversalResponse;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.service.MpnSettlementDateUtil;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.multipolar.uiteller.backend.service.SharedRuleService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class TransactionLogService {
    private static Logger logger = LoggerFactory.getLogger(TransactionLogService.class);
    @Autowired
    private SharedRuleService cacheRule;
    private static final String RULE_EXTENSION = ".report.json";

    private static final String KEY_BIT = "billNumber";
    private static final String REFF_THREE = "billReferenceThree";
    private static final String VALUE_THREE = "billValueThree";
    private static final String REFF_TWO = "billReferenceTwo";
    private static final String VALUE_TWO = "billValueTwo";

    private static final String PSTN_TRX_TYPE = "pstn";
    private static final String SPEEDY_TRX_TYPE = "speedy";

    private static final String TIRTANADI_TRX_TYPE = "tirtanadi";
    private static final String TIRTAAYUMI_DTI_TRX_TYPE = "tirtaayumiDti";
    private static final String TIRTAKUALO_DTI_TRX_TYPE = "tirtakualoDti";
    private static final String TIRTATANJUNG_DTI_TRX_TYPE = "tirtatanjungDti";
    private static final String PDAM_DTI_TRX_TYPE = "pdamDti";
    private static final String INFO_ONE = "billInfoOne";
    private static final String PERIOD_ONE = "billPeriodOne";
    private static final String ACCOUNT_ONE = "billAccountOne";
    private static final String AMOUNT_ONE = "billAmountOne";
    private static final String PENALTY_ONE = "penaltyOne";
    private static final String ADDITIONAL_ONE = "additionalAmountOne";
    private static final String ADMIN_ONE = "adminFeeOne";
    private static final String START_ONE = "startMeterOne";
    private static final String END_ONE = "endMeterOne";
    private static final String CUBICATION_ONE = "cubicationOne";
    private static final String INFO_TWO = "billInfoTwo";
    private static final String PERIOD_TWO = "billPeriodTwo";
    private static final String ACCOUNT_TWO = "billAccountTwo";
    private static final String AMOUNT_TWO = "billAmountTwo";
    private static final String PENALTY_TWO = "penaltyTwo";
    private static final String ADDITIONAL_TWO = "additionalAmountTwo";
    private static final String ADMIN_TWO = "adminFeeTwo";
    private static final String START_TWO = "startMeterTwo";
    private static final String END_TWO = "endMeterTwo";
    private static final String CUBICATION_TWO = "cubicationTwo";
    private static final String INFO_THREE = "billInfoThree";
    private static final String PERIOD_THREE = "billPeriodThree";
    private static final String ACCOUNT_THREE = "billAccountThree";
    private static final String AMOUNT_THREE = "billAmountThree";
    private static final String PENALTY_THREE = "penaltyThree";
    private static final String ADDITIONAL_THREE = "additionalAmountThree";
    private static final String ADMIN_THREE = "adminFeeThree";
    private static final String START_THREE = "startMeterThree";
    private static final String END_THREE = "endMeterThree";
    private static final String CUBICATION_THREE = "cubicationThree";
    private static final String INFO_FOUR = "billInfoFour";
    private static final String PERIOD_FOUR = "billPeriodFour";
    private static final String ACCOUNT_FOUR = "billAccountFour";
    private static final String AMOUNT_FOUR = "billAmountFour";
    private static final String PENALTY_FOUR = "penaltyFour";
    private static final String ADDITIONAL_FOUR = "additionalAmountFour";
    private static final String ADMIN_FOUR = "adminFeeFour";
    private static final String START_FOUR = "startMeterFour";
    private static final String END_FOUR = "endMeterFour";
    private static final String CUBICATION_FOUR = "cubicationFour";
    private static final String INFO_FIVE = "billInfoFive";
    private static final String PERIOD_FIVE = "billPeriodFive";
    private static final String ACCOUNT_FIVE = "billAccountFive";
    private static final String AMOUNT_FIVE = "billAmountFive";
    private static final String PENALTY_FIVE = "penaltyFive";
    private static final String ADDITIONAL_FIVE = "additionalAmountFive";
    private static final String ADMIN_FIVE = "adminFeeFive";
    private static final String START_FIVE = "startMeterFive";
    private static final String END_FIVE = "endMeterFive";
    private static final String CUBICATION_FIVE = "cubicationFive";
    private static final String INFO_SIX = "billInfoSix";
    private static final String PERIOD_SIX = "billPeriodSix";
    private static final String ACCOUNT_SIX = "billAccountSix";
    private static final String AMOUNT_SIX = "billAmountSix";
    private static final String PENALTY_SIX = "penaltySix";
    private static final String ADDITIONAL_SIX = "additionalAmountSix";
    private static final String ADMIN_SIX = "adminFeeSix";
    private static final String START_SIX = "startMeterSix";
    private static final String END_SIX = "endMeterSix";
    private static final String CUBICATION_SIX = "cubicationSix";
    private static final String INFO_SEVEN = "billInfoSeven";
    private static final String PERIOD_SEVEN = "billPeriodSeven";
    private static final String ACCOUNT_SEVEN = "billAccountSeven";
    private static final String AMOUNT_SEVEN = "billAmountSeven";
    private static final String PENALTY_SEVEN = "penaltySeven";
    private static final String ADDITIONAL_SEVEN = "additionalAmountSeven";
    private static final String ADMIN_SEVEN = "adminFeeSeven";
    private static final String START_SEVEN = "startMeterSeven";
    private static final String END_SEVEN = "endMeterSeven";
    private static final String CUBICATION_SEVEN = "cubicationSeven";
    private static final String INFO_EIGHT = "billInfoEight";
    private static final String PERIOD_EIGHT = "billPeriodEight";
    private static final String ACCOUNT_EIGHT = "billAccountEight";
    private static final String AMOUNT_EIGHT = "billAmountEight";
    private static final String PENALTY_EIGHT = "penaltyEight";
    private static final String ADDITIONAL_EIGHT = "additionalAmountEight";
    private static final String ADMIN_EIGHT = "adminFeeEight";
    private static final String START_EIGHT = "startMeterEight";
    private static final String END_EIGHT = "endMeterEight";
    private static final String CUBICATION_EIGHT = "cubicationEight";
    private static final String TIRTA_KEY_BIT = "billReport";
    private static final String KODE_BILLING = "kodeBilling";
    private static final String ARCHIVE_NUMBER = "archiveNumber";
    private static final String NOMINAL = "nominal";
    private static final String QR_STRING = "qrString";
    private static final String[] MPNG3 = {"djpg3", "djag3", "djbcg3"};
    private static final String[] MPNG3_REINQUIRY = {"djpg3Reinquiry", "djbcg3Reinquiry", "djag3Reinquiry"};

    @Autowired
    private CommonTransactionLogRepository commonLogRepository;

    @Autowired
    private TransactionLogRepository transactionLogRepository;

    @Autowired
    private ResourceMessageRepository messageRepository;

    @Autowired
    private TransactionRequestLogRepository transactionRequestLogRepository;

    @Autowired
    private InstansiPdamRepository instansiPdamRepository;

    @Autowired
    private PDAMRepository pdamRepository;

    @Autowired
    private ReprintTimeoutResourceRepository reprintTimeoutResourceRepository;

    @Autowired
    private MpnSettlementDateUtil mpnUtil;

    public Long findCommonIdFromTraceNumber(String stan, String ref) {
        String refWoZero = StringUtils.stripStart(ref, "0");
        List<Long> commonIds = transactionLogRepository.findCommonIdFromTraceNumber(stan, refWoZero);
        return commonIds.get(commonIds.size() - 1);
    }

    public Long findCommonIdBifast(String stan, String ref) {
        String refWoZero = StringUtils.stripStart(ref, "0");
        List<Long> commonIds = transactionLogRepository.findCommonIdBifast(stan, refWoZero);
        return commonIds.get(commonIds.size() - 1);
    }

    @Transactional
    public void updateSuccessMpnReversal(ISOMsg request, ReversalResponse response) {
        String stan = request.getString(11);
        String ref = request.getString(37);
        Long commonId = findCommonIdFromTraceNumber(stan, StringUtils.stripStart(ref, "0"));
        CommonTransactionLog commonLog = commonLogRepository.findOne(commonId);

        // jika rcode adalah 00 atau 98 berarti sukses di reversal
        // update status menjadi 270 dan RC sesui rcode nya
        String rcode = response == null ? "99" : response.getRCode();
        if (StringUtils.equals("0", rcode) || StringUtils.equals("98", rcode)) {
            commonLog.setStatus(270);
        } else {
            commonLog.setStatus(271);
        }

        commonLog.setRc("R" + rcode);
        commonLogRepository.save(commonLog);
    }

    @Transactional
    public void updateSuccessMpnReinquiry(ISOMsg request, ISOMsg response) throws IOException {
        boolean responseExists = response != null ? true : false;
        if (!responseExists)
            return;

        // search di common_transaction_log untuk trace payment nya
        String stan = request.getString(11);
        String ref = request.getString(37);
        Long commonId = findCommonIdFromTraceNumber(stan, StringUtils.stripStart(ref, "0"));
        CommonTransactionLog commonLog = commonLogRepository.findOne(commonId);

        String responseRc = response.getString(39);
        if (!StringUtils.equals(responseRc, "00")){
//            ResourceMessage message = messageRepository.findByCodeAndMessage(getRule().getErrorKey(), responseRc);
//            String messageDesc = message == null ? "Unmapping Error Number ("+responseRc+")" : message.getValue();
//            commonLog.setMessage(messageDesc);
            commonLog.setRc(responseRc);
            commonLog.setMessage(exception(response, FeatureContextHolder.getContext().getFeatureName()));
            commonLogRepository.save(commonLog);
            return;
        }

        // update menjadi RC 00 dan status 1
        commonLog.setRc("00");
        commonLog.setStatus(0);
        commonLog.setMessage(Constants.MPN_REINQUIRY);
        commonLogRepository.save(commonLog);

        Rule rule = null;
        try {
            rule = getRule();
        } catch (FileNotFoundException fnfe) {
            logger.error("No rule for this transaction. Bit 63: " + request.getString(63));
        }

        // cari nilai ntpn dari response
        String ntpn = null;
        String trxType = null;
        String settlementDate = mpnUtil.getSettlementDateFromResponse(response);
        String transactionTime = null;
        Set<TransactionLog> logs = createDetailList(request, response, responseExists, rule);
        boolean updated =false;
        for (TransactionLog log : logs) {
            if (StringUtils.equals(log.getFieldName(), "ntpn")) {
                ntpn = log.getFieldValue();
                trxType = StringUtils.replace(log.getTrxType(), "Reinquiry", "");
                if (updated == true)
                    break;
                updated = true;
            }
            if (StringUtils.equals(log.getFieldName(), "transactionTime")) {
                transactionTime = log.getFieldValue();
                if (updated == true)
                    break;
                updated = true;
            }
        }

        // update nilai ntpn
        List<TransactionLog> trxLogs = transactionLogRepository.findByCommonTransactionId(transactionLogRepository.findFirstByTraceNumberAndFieldNameAndFieldValueOrderByIdDesc(stan, Constants.REFFERENCE_FIELD_NAME, StringUtils.stripStart(request.getString(37), Constants.ZERO)).getCommonTransaction().getId());
        for (TransactionLog log : trxLogs) {
            if (StringUtils.equals(log.getFieldName(), "ntpn")) {
                log.setFieldValue(ntpn);
                transactionLogRepository.save(log);
            }
            if (StringUtils.equals(log.getFieldName(), "settlementDate")) {
                log.setFieldValue(settlementDate);
                transactionLogRepository.save(log);
            }
            if (StringUtils.equals(log.getFieldName(), "transactionTime")) {
                log.setFieldValue(transactionTime);
                transactionLogRepository.save(log);
            }
            if (StringUtils.equals(log.getFieldName(), "ref")) {
                log.setFieldValue(response.getString(37));
                transactionLogRepository.save(log);
            }
            if(response.getString(48)!=null){
                log.setTraceNumber(response.getString(48));
                transactionLogRepository.save(log);
            }
        }

        // tambahkan record penanda re-inquiry
        TransactionLog log = new TransactionLog("reinquiry", "true", trxType, response.getString(48)==null?stan:response.getString(48));
        log.setCommonTransaction(commonLog);
        transactionLogRepository.save(log);
    }

    @Transactional
    public void updateSuccessBifastReinquiry(ISOMsg request, ISOMsg response) throws IOException {
        boolean responseExists = response != null ? true : false;
        if (!responseExists)
            return;

        String responseRc = response.getString(39);
        if (!StringUtils.equals(responseRc, "00"))
            return;

        // search di common_transaction_log untuk trace payment nya
        String stan = request.getString(11);
        String ref = request.getString(37);
        Long commonId = findCommonIdBifast(stan, StringUtils.stripStart(ref, "0"));
        CommonTransactionLog commonLog = commonLogRepository.findOne(commonId);

        // update menjadi RC 00 dan status 1
        commonLog.setRc("00");
        commonLog.setStatus(0);
        commonLog.setMessage(Constants.BIFAST_REINQUIRY);
        commonLogRepository.save(commonLog);

        Rule rule = null;
        try {
            rule = getRule();
        } catch (FileNotFoundException fnfe) {
            logger.error("No rule for this transaction. Bit 63: " + request.getString(63));
        }

        // tambahkan record penanda re-inquiry
        TransactionLog log = new TransactionLog("reinquiry", "true", "bifast", stan);
        log.setCommonTransaction(commonLog);
        transactionLogRepository.save(log);
    }

    @Transactional
    public CommonTransactionLog logTransaction(ISOMsg request, ISOMsg response) throws IOException {
        Rule rule = null;
        try {
            rule = getRule();
        } catch (FileNotFoundException fnfe) {
            logger.error("No rule for this transaction. Bit 63: " + request.getString(63));
        }

        boolean responseExists = response != null ? true : false;

        CommonTransactionLog commonLog = generateCommonLog(response);
        commonLogRepository.save(commonLog);

        Set<TransactionLog> logs = createDetailList(request, response, responseExists, rule);
        for (TransactionLog log : logs) {
            log.setCommonTransaction(commonLog);
            transactionLogRepository.save(log);
        }

        return commonLog;
    }

    @Transactional
    public void saveAdditionalLog(List<TransactionLog> logs, CommonTransactionLog commonLog, String traceNumber, String trxType) {
        String kodeBilling = "";
        String archiveNumber = "";
        String nominal = "";
        String qrString = "";

        for (TransactionLog log : logs) {
            log.setCommonTransaction(commonLog);
            log.setTraceNumber(traceNumber);
            log.setTrxType(trxType);
            if (log.getFieldName().equals(KODE_BILLING)) {
                kodeBilling = log.getFieldValue();
            }
            if (log.getFieldName().equals(ARCHIVE_NUMBER)) {
                archiveNumber = log.getFieldValue();
            }
            if (log.getFieldName().equals(NOMINAL)) {
                nominal = log.getFieldValue();
            }
            transactionLogRepository.save(log);
        }
        if(FeatureContextHolder.getContext().getFeatureName().equals("edupay")
        || FeatureContextHolder.getContext().getFeatureName().equals("eduinit")
        || FeatureContextHolder.getContext().getFeatureName().equals("edupayInquiry")
        || FeatureContextHolder.getContext().getFeatureName().equals("eduinitInquiry")) {
            qrString = kodeBilling + "+" + archiveNumber + "+" + nominal;
            transactionLogRepository.updateByFieldNameAndTraceNumber(qrString, QR_STRING, traceNumber);
            transactionRequestLogRepository.updateByFieldNameAndTraceNumber(qrString, QR_STRING, traceNumber);
        }
    }

    private CommonTransactionLog generateCommonLog(ISOMsg response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userid = auth.getPrincipal().toString();
        String branchId = ((UserDetailImpl) auth.getDetails()).getBranchCode();
        Date trxDate = FeatureContextHolder.getContext().getFeatureTransactionDate();
        String trxType = FeatureContextHolder.getContext().getFeatureName();

        // hack untuk PBB dan pendidikan dimana RC != 00 masih bisa dicetak ulang
        // daftar RC yang boleh cetak ulang walaupun tidak 00 ada di model ReprintTimeoutResource

        int status = 1;
        if (response == null) {
            status = 2;
        } else if (StringUtils.equals(response.getString(39), "00")){
            status = 0;
        } else if (StringUtils.equalsAny(trxType,"pbb", "edupay", "eduinit")){
            String rc = response.getString(39);
            // hack for sts -- start
            Map<String, Object> contextData = FeatureContextHolder.getContext().getReportData();
            String noSts = contextData.get("noSts") != null ? contextData.get("noSts").toString() : "";
//            boolean filterBillerFlag = StringUtils.startsWithAny(noSts, "1294");
            ReprintTimeoutResource print = reprintTimeoutResourceRepository.findFirstByTrxTypeAndCode(trxType,rc);
            // end
//            if (print != null && filterBillerFlag){
            if (print != null){
                status = 0;
            }
        }

//        int status = response != null ? (StringUtils.equals(response.getString(39), "00") ? 0 : 1) : 2;

        CommonTransactionLog commonLog = new CommonTransactionLog();
        commonLog.setBranchCode(branchId);
        commonLog.setUserid(userid);
        commonLog.setTrxDate(trxDate);
        commonLog.setStatus(status);
        commonLog.setRc(response.getString(39));

        if (status == 0)
            commonLog.setMessage("SUKSES");
        else if (status == 1)
            commonLog.setMessage(exception(response, FeatureContextHolder.getContext().getFeatureName()));
        else if (status == 2)
            commonLog.setMessage("Request time-out");

        return commonLog;
    }

    protected Set<TransactionLog> createDetailList(ISOMsg request, ISOMsg response, boolean responseExists, Rule rule) throws IOException {
        if (rule == null)
            return new HashSet<>();

        String transactionKey = rule.getTransaction();
        RequestRule[] reportRules = rule.getReport();
        Set<TransactionLog> allLogs = new HashSet<>();
        for (RequestRule reportRule : reportRules) {
            int bit = reportRule.getBit();
            String source = reportRule.getSource();
            String rcListInclude = reportRule.getRcInclude();
            String rcListExclude = reportRule.getRcExclude();

            boolean needInclude = StringUtils.isEmpty(rcListInclude) || (responseExists && StringUtils.contains(rcListInclude, response.getString(39)));
            boolean needExclude = StringUtils.isNotEmpty(rcListExclude) && (responseExists && StringUtils.contains(rcListExclude, response.getString(39)));

            String valueFromBit;
            if (StringUtils.equals("request", source) && needInclude && !needExclude)
                valueFromBit = request.getString(bit);
            else if (StringUtils.equals("response", source) && needInclude && !needExclude) {
                if (responseExists)
                    valueFromBit = response.getString(bit);
                else
                    continue;
            } else if (!StringUtils.equals("request", source) && !StringUtils.equals("response", source))
                throw new IllegalArgumentException("source only can contain value either request or response");
            else
                continue;

            if (valueFromBit == null) {
                logger.error("Value get from bit is null, bit = " + bit);
                //throw new IllegalArgumentException("The rule said get value from bit but empty bit is found. bit " + bit);
                continue;
            }
            String traceNumber = response.getString(11);
            String bit48 = response.getString(48);
            if (StringUtils.equalsAny(transactionKey, MPNG3) && bit48!=null || StringUtils.equalsAny(transactionKey, MPNG3_REINQUIRY)&& bit48!=null) {
                traceNumber = bit48;
            }


            ConverterRule[] values = reportRule.getValue();
            Set<TransactionLog> logs = setTransactionDetailValues(transactionKey, traceNumber, valueFromBit, values);
            allLogs.addAll(logs);
        }

        return allLogs;
    }

    protected Set<TransactionLog> setTransactionDetailValues(String trx, String traceNumber, String bitValue, ConverterRule[] rules) {
        int index = 0;
        Set<TransactionLog> valueList = new HashSet<>();
        String billNumber = "1";
        String billReport = "0";
        for (ConverterRule rule : rules) {
            String key = rule.getKey();
            int length = rule.getLength();
            if (key == null) {
                index = index + length;
                continue;
            }

            String leftpad = rule.getLeftpad();
            String rightpad = rule.getRightpad();

            if (length == 0)
                length = bitValue.length();

            // cek for pstn & speedy
            if (StringUtils.equals(PSTN_TRX_TYPE, trx) || StringUtils.equals(SPEEDY_TRX_TYPE, trx)) {
                if (StringUtils.equals(billNumber, "1") &&
                        (StringUtils.equals(key, REFF_THREE) || StringUtils.equals(key, VALUE_THREE) ||
                                StringUtils.equals(key, REFF_TWO) || StringUtils.equals(key, VALUE_TWO)))
                    continue;
                if (StringUtils.equals(billNumber, "2") &&
                        (StringUtils.equals(key, REFF_THREE) || StringUtils.equals(key, VALUE_THREE)))
                    continue;
            }

            // cek for tirtanadi
            if (StringUtils.equalsAny(trx, TIRTANADI_TRX_TYPE, TIRTAAYUMI_DTI_TRX_TYPE, PDAM_DTI_TRX_TYPE)) {
                if (StringUtils.equals(billReport, "0") &&
                        (StringUtils.equals(key, INFO_ONE) || StringUtils.equals(key, PERIOD_ONE) || StringUtils.equals(key, ACCOUNT_ONE) || StringUtils.equals(key, AMOUNT_ONE) || StringUtils.equals(key, PENALTY_ONE) || StringUtils.equals(key, ADDITIONAL_ONE) || StringUtils.equals(key, ADMIN_ONE) || StringUtils.equals(key, START_ONE) || StringUtils.equals(key, END_ONE) || StringUtils.equals(key, CUBICATION_ONE) ||
                                StringUtils.equals(key, INFO_TWO) || StringUtils.equals(key, PERIOD_TWO) || StringUtils.equals(key, ACCOUNT_TWO) || StringUtils.equals(key, AMOUNT_TWO) || StringUtils.equals(key, PENALTY_TWO) || StringUtils.equals(key, ADDITIONAL_TWO) || StringUtils.equals(key, ADMIN_TWO) || StringUtils.equals(key, START_TWO) || StringUtils.equals(key, END_TWO) || StringUtils.equals(key, CUBICATION_TWO) ||
                                StringUtils.equals(key, INFO_THREE) || StringUtils.equals(key, PERIOD_THREE) || StringUtils.equals(key, ACCOUNT_THREE) || StringUtils.equals(key, AMOUNT_THREE) || StringUtils.equals(key, PENALTY_THREE) || StringUtils.equals(key, ADDITIONAL_THREE) || StringUtils.equals(key, ADMIN_THREE) || StringUtils.equals(key, START_THREE) || StringUtils.equals(key, END_THREE) || StringUtils.equals(key, CUBICATION_THREE) ||
                                StringUtils.equals(key, INFO_FOUR) || StringUtils.equals(key, PERIOD_FOUR) || StringUtils.equals(key, ACCOUNT_FOUR) || StringUtils.equals(key, AMOUNT_FOUR) || StringUtils.equals(key, PENALTY_FOUR) || StringUtils.equals(key, ADDITIONAL_FOUR) || StringUtils.equals(key, ADMIN_FOUR) || StringUtils.equals(key, START_FOUR) || StringUtils.equals(key, END_FOUR) || StringUtils.equals(key, CUBICATION_FOUR) ||
                                StringUtils.equals(key, INFO_FIVE) || StringUtils.equals(key, PERIOD_FIVE) || StringUtils.equals(key, ACCOUNT_FIVE) || StringUtils.equals(key, AMOUNT_FIVE) || StringUtils.equals(key, PENALTY_FIVE) || StringUtils.equals(key, ADDITIONAL_FIVE) || StringUtils.equals(key, ADMIN_FIVE) || StringUtils.equals(key, START_FIVE) || StringUtils.equals(key, END_FIVE) || StringUtils.equals(key, CUBICATION_FIVE) ||
                                StringUtils.equals(key, INFO_SIX) || StringUtils.equals(key, PERIOD_SIX) || StringUtils.equals(key, ACCOUNT_SIX) || StringUtils.equals(key, AMOUNT_SIX) || StringUtils.equals(key, PENALTY_SIX) || StringUtils.equals(key, ADDITIONAL_SIX) || StringUtils.equals(key, ADMIN_SIX) || StringUtils.equals(key, START_SIX) || StringUtils.equals(key, END_SIX) || StringUtils.equals(key, CUBICATION_SIX) ||
                                StringUtils.equals(key, INFO_SEVEN) || StringUtils.equals(key, PERIOD_SEVEN) || StringUtils.equals(key, ACCOUNT_SEVEN) || StringUtils.equals(key, AMOUNT_SEVEN) || StringUtils.equals(key, PENALTY_SEVEN) || StringUtils.equals(key, ADDITIONAL_SEVEN) || StringUtils.equals(key, ADMIN_SEVEN) || StringUtils.equals(key, START_SEVEN) || StringUtils.equals(key, END_SEVEN) || StringUtils.equals(key, CUBICATION_SEVEN) ||
                                StringUtils.equals(key, INFO_EIGHT) || StringUtils.equals(key, PERIOD_EIGHT) || StringUtils.equals(key, ACCOUNT_EIGHT) || StringUtils.equals(key, AMOUNT_EIGHT) || StringUtils.equals(key, PENALTY_EIGHT) || StringUtils.equals(key, ADDITIONAL_EIGHT) || StringUtils.equals(key, ADMIN_EIGHT) || StringUtils.equals(key, START_EIGHT) || StringUtils.equals(key, END_EIGHT) || StringUtils.equals(key, CUBICATION_EIGHT)))
                    continue;
                if (StringUtils.equals(billReport, "1") &&
                        (StringUtils.equals(key, INFO_TWO) || StringUtils.equals(key, PERIOD_TWO) || StringUtils.equals(key, ACCOUNT_TWO) || StringUtils.equals(key, AMOUNT_TWO) || StringUtils.equals(key, PENALTY_TWO) || StringUtils.equals(key, ADDITIONAL_TWO) || StringUtils.equals(key, ADMIN_TWO) || StringUtils.equals(key, START_TWO) || StringUtils.equals(key, END_TWO) || StringUtils.equals(key, CUBICATION_TWO) ||
                                StringUtils.equals(key, INFO_THREE) || StringUtils.equals(key, PERIOD_THREE) || StringUtils.equals(key, ACCOUNT_THREE) || StringUtils.equals(key, AMOUNT_THREE) || StringUtils.equals(key, PENALTY_THREE) || StringUtils.equals(key, ADDITIONAL_THREE) || StringUtils.equals(key, ADMIN_THREE) || StringUtils.equals(key, START_THREE) || StringUtils.equals(key, END_THREE) || StringUtils.equals(key, CUBICATION_THREE) ||
                                StringUtils.equals(key, INFO_FOUR) || StringUtils.equals(key, PERIOD_FOUR) || StringUtils.equals(key, ACCOUNT_FOUR) || StringUtils.equals(key, AMOUNT_FOUR) || StringUtils.equals(key, PENALTY_FOUR) || StringUtils.equals(key, ADDITIONAL_FOUR) || StringUtils.equals(key, ADMIN_FOUR) || StringUtils.equals(key, START_FOUR) || StringUtils.equals(key, END_FOUR) || StringUtils.equals(key, CUBICATION_FOUR) ||
                                StringUtils.equals(key, INFO_FIVE) || StringUtils.equals(key, PERIOD_FIVE) || StringUtils.equals(key, ACCOUNT_FIVE) || StringUtils.equals(key, AMOUNT_FIVE) || StringUtils.equals(key, PENALTY_FIVE) || StringUtils.equals(key, ADDITIONAL_FIVE) || StringUtils.equals(key, ADMIN_FIVE) || StringUtils.equals(key, START_FIVE) || StringUtils.equals(key, END_FIVE) || StringUtils.equals(key, CUBICATION_FIVE) ||
                                StringUtils.equals(key, INFO_SIX) || StringUtils.equals(key, PERIOD_SIX) || StringUtils.equals(key, ACCOUNT_SIX) || StringUtils.equals(key, AMOUNT_SIX) || StringUtils.equals(key, PENALTY_SIX) || StringUtils.equals(key, ADDITIONAL_SIX) || StringUtils.equals(key, ADMIN_SIX) || StringUtils.equals(key, START_SIX) || StringUtils.equals(key, END_SIX) || StringUtils.equals(key, CUBICATION_SIX) ||
                                StringUtils.equals(key, INFO_SEVEN) || StringUtils.equals(key, PERIOD_SEVEN) || StringUtils.equals(key, ACCOUNT_SEVEN) || StringUtils.equals(key, AMOUNT_SEVEN) || StringUtils.equals(key, PENALTY_SEVEN) || StringUtils.equals(key, ADDITIONAL_SEVEN) || StringUtils.equals(key, ADMIN_SEVEN) || StringUtils.equals(key, START_SEVEN) || StringUtils.equals(key, END_SEVEN) || StringUtils.equals(key, CUBICATION_SEVEN) ||
                                StringUtils.equals(key, INFO_EIGHT) || StringUtils.equals(key, PERIOD_EIGHT) || StringUtils.equals(key, ACCOUNT_EIGHT) || StringUtils.equals(key, AMOUNT_EIGHT) || StringUtils.equals(key, PENALTY_EIGHT) || StringUtils.equals(key, ADDITIONAL_EIGHT) || StringUtils.equals(key, ADMIN_EIGHT) || StringUtils.equals(key, START_EIGHT) || StringUtils.equals(key, END_EIGHT) || StringUtils.equals(key, CUBICATION_EIGHT)))
                    continue;
                if (StringUtils.equals(billReport, "2") &&
                        (StringUtils.equals(key, INFO_THREE) || StringUtils.equals(key, PERIOD_THREE) || StringUtils.equals(key, ACCOUNT_THREE) || StringUtils.equals(key, AMOUNT_THREE) || StringUtils.equals(key, PENALTY_THREE) || StringUtils.equals(key, ADDITIONAL_THREE) || StringUtils.equals(key, ADMIN_THREE) || StringUtils.equals(key, START_THREE) || StringUtils.equals(key, END_THREE) || StringUtils.equals(key, CUBICATION_THREE) ||
                                StringUtils.equals(key, INFO_FOUR) || StringUtils.equals(key, PERIOD_FOUR) || StringUtils.equals(key, ACCOUNT_FOUR) || StringUtils.equals(key, AMOUNT_FOUR) || StringUtils.equals(key, PENALTY_FOUR) || StringUtils.equals(key, ADDITIONAL_FOUR) || StringUtils.equals(key, ADMIN_FOUR) || StringUtils.equals(key, START_FOUR) || StringUtils.equals(key, END_FOUR) || StringUtils.equals(key, CUBICATION_FOUR) ||
                                StringUtils.equals(key, INFO_FIVE) || StringUtils.equals(key, PERIOD_FIVE) || StringUtils.equals(key, ACCOUNT_FIVE) || StringUtils.equals(key, AMOUNT_FIVE) || StringUtils.equals(key, PENALTY_FIVE) || StringUtils.equals(key, ADDITIONAL_FIVE) || StringUtils.equals(key, ADMIN_FIVE) || StringUtils.equals(key, START_FIVE) || StringUtils.equals(key, END_FIVE) || StringUtils.equals(key, CUBICATION_FIVE) ||
                                StringUtils.equals(key, INFO_SIX) || StringUtils.equals(key, PERIOD_SIX) || StringUtils.equals(key, ACCOUNT_SIX) || StringUtils.equals(key, AMOUNT_SIX) || StringUtils.equals(key, PENALTY_SIX) || StringUtils.equals(key, ADDITIONAL_SIX) || StringUtils.equals(key, ADMIN_SIX) || StringUtils.equals(key, START_SIX) || StringUtils.equals(key, END_SIX) || StringUtils.equals(key, CUBICATION_SIX) ||
                                StringUtils.equals(key, INFO_SEVEN) || StringUtils.equals(key, PERIOD_SEVEN) || StringUtils.equals(key, ACCOUNT_SEVEN) || StringUtils.equals(key, AMOUNT_SEVEN) || StringUtils.equals(key, PENALTY_SEVEN) || StringUtils.equals(key, ADDITIONAL_SEVEN) || StringUtils.equals(key, ADMIN_SEVEN) || StringUtils.equals(key, START_SEVEN) || StringUtils.equals(key, END_SEVEN) || StringUtils.equals(key, CUBICATION_SEVEN) ||
                                StringUtils.equals(key, INFO_EIGHT) || StringUtils.equals(key, PERIOD_EIGHT) || StringUtils.equals(key, ACCOUNT_EIGHT) || StringUtils.equals(key, AMOUNT_EIGHT) || StringUtils.equals(key, PENALTY_EIGHT) || StringUtils.equals(key, ADDITIONAL_EIGHT) || StringUtils.equals(key, ADMIN_EIGHT) || StringUtils.equals(key, START_EIGHT) || StringUtils.equals(key, END_EIGHT) || StringUtils.equals(key, CUBICATION_EIGHT)))
                    continue;
                if (StringUtils.equals(billReport, "3") &&
                        (StringUtils.equals(key, INFO_FOUR) || StringUtils.equals(key, PERIOD_FOUR) || StringUtils.equals(key, ACCOUNT_FOUR) || StringUtils.equals(key, AMOUNT_FOUR) || StringUtils.equals(key, PENALTY_FOUR) || StringUtils.equals(key, ADDITIONAL_FOUR) || StringUtils.equals(key, ADMIN_FOUR) || StringUtils.equals(key, START_FOUR) || StringUtils.equals(key, END_FOUR) || StringUtils.equals(key, CUBICATION_FOUR) ||
                                StringUtils.equals(key, INFO_FIVE) || StringUtils.equals(key, PERIOD_FIVE) || StringUtils.equals(key, ACCOUNT_FIVE) || StringUtils.equals(key, AMOUNT_FIVE) || StringUtils.equals(key, PENALTY_FIVE) || StringUtils.equals(key, ADDITIONAL_FIVE) || StringUtils.equals(key, ADMIN_FIVE) || StringUtils.equals(key, START_FIVE) || StringUtils.equals(key, END_FIVE) || StringUtils.equals(key, CUBICATION_FIVE) ||
                                StringUtils.equals(key, INFO_SIX) || StringUtils.equals(key, PERIOD_SIX) || StringUtils.equals(key, ACCOUNT_SIX) || StringUtils.equals(key, AMOUNT_SIX) || StringUtils.equals(key, PENALTY_SIX) || StringUtils.equals(key, ADDITIONAL_SIX) || StringUtils.equals(key, ADMIN_SIX) || StringUtils.equals(key, START_SIX) || StringUtils.equals(key, END_SIX) || StringUtils.equals(key, CUBICATION_SIX) ||
                                StringUtils.equals(key, INFO_SEVEN) || StringUtils.equals(key, PERIOD_SEVEN) || StringUtils.equals(key, ACCOUNT_SEVEN) || StringUtils.equals(key, AMOUNT_SEVEN) || StringUtils.equals(key, PENALTY_SEVEN) || StringUtils.equals(key, ADDITIONAL_SEVEN) || StringUtils.equals(key, ADMIN_SEVEN) || StringUtils.equals(key, START_SEVEN) || StringUtils.equals(key, END_SEVEN) || StringUtils.equals(key, CUBICATION_SEVEN) ||
                                StringUtils.equals(key, INFO_EIGHT) || StringUtils.equals(key, PERIOD_EIGHT) || StringUtils.equals(key, ACCOUNT_EIGHT) || StringUtils.equals(key, AMOUNT_EIGHT) || StringUtils.equals(key, PENALTY_EIGHT) || StringUtils.equals(key, ADDITIONAL_EIGHT) || StringUtils.equals(key, ADMIN_EIGHT) || StringUtils.equals(key, START_EIGHT) || StringUtils.equals(key, END_EIGHT) || StringUtils.equals(key, CUBICATION_EIGHT)))
                    continue;
                if (StringUtils.equals(billReport, "4") &&
                        (StringUtils.equals(key, INFO_FIVE) || StringUtils.equals(key, PERIOD_FIVE) || StringUtils.equals(key, ACCOUNT_FIVE) || StringUtils.equals(key, AMOUNT_FIVE) || StringUtils.equals(key, PENALTY_FIVE) || StringUtils.equals(key, ADDITIONAL_FIVE) || StringUtils.equals(key, ADMIN_FIVE) || StringUtils.equals(key, START_FIVE) || StringUtils.equals(key, END_FIVE) || StringUtils.equals(key, CUBICATION_FIVE) ||
                                StringUtils.equals(key, INFO_SIX) || StringUtils.equals(key, PERIOD_SIX) || StringUtils.equals(key, ACCOUNT_SIX) || StringUtils.equals(key, AMOUNT_SIX) || StringUtils.equals(key, PENALTY_SIX) || StringUtils.equals(key, ADDITIONAL_SIX) || StringUtils.equals(key, ADMIN_SIX) || StringUtils.equals(key, START_SIX) || StringUtils.equals(key, END_SIX) || StringUtils.equals(key, CUBICATION_SIX) ||
                                StringUtils.equals(key, INFO_SEVEN) || StringUtils.equals(key, PERIOD_SEVEN) || StringUtils.equals(key, ACCOUNT_SEVEN) || StringUtils.equals(key, AMOUNT_SEVEN) || StringUtils.equals(key, PENALTY_SEVEN) || StringUtils.equals(key, ADDITIONAL_SEVEN) || StringUtils.equals(key, ADMIN_SEVEN) || StringUtils.equals(key, START_SEVEN) || StringUtils.equals(key, END_SEVEN) || StringUtils.equals(key, CUBICATION_SEVEN) ||
                                StringUtils.equals(key, INFO_EIGHT) || StringUtils.equals(key, PERIOD_EIGHT) || StringUtils.equals(key, ACCOUNT_EIGHT) || StringUtils.equals(key, AMOUNT_EIGHT) || StringUtils.equals(key, PENALTY_EIGHT) || StringUtils.equals(key, ADDITIONAL_EIGHT) || StringUtils.equals(key, ADMIN_EIGHT) || StringUtils.equals(key, START_EIGHT) || StringUtils.equals(key, END_EIGHT) || StringUtils.equals(key, CUBICATION_EIGHT)))
                    continue;
                if (StringUtils.equals(billReport, "5") &&
                        (StringUtils.equals(key, INFO_SIX) || StringUtils.equals(key, PERIOD_SIX) || StringUtils.equals(key, ACCOUNT_SIX) || StringUtils.equals(key, AMOUNT_SIX) || StringUtils.equals(key, PENALTY_SIX) || StringUtils.equals(key, ADDITIONAL_SIX) || StringUtils.equals(key, ADMIN_SIX) || StringUtils.equals(key, START_SIX) || StringUtils.equals(key, END_SIX) || StringUtils.equals(key, CUBICATION_SIX) ||
                                StringUtils.equals(key, INFO_SEVEN) || StringUtils.equals(key, PERIOD_SEVEN) || StringUtils.equals(key, ACCOUNT_SEVEN) || StringUtils.equals(key, AMOUNT_SEVEN) || StringUtils.equals(key, PENALTY_SEVEN) || StringUtils.equals(key, ADDITIONAL_SEVEN) || StringUtils.equals(key, ADMIN_SEVEN) || StringUtils.equals(key, START_SEVEN) || StringUtils.equals(key, END_SEVEN) || StringUtils.equals(key, CUBICATION_SEVEN) ||
                                StringUtils.equals(key, INFO_EIGHT) || StringUtils.equals(key, PERIOD_EIGHT) || StringUtils.equals(key, ACCOUNT_EIGHT) || StringUtils.equals(key, AMOUNT_EIGHT) || StringUtils.equals(key, PENALTY_EIGHT) || StringUtils.equals(key, ADDITIONAL_EIGHT) || StringUtils.equals(key, ADMIN_EIGHT) || StringUtils.equals(key, START_EIGHT) || StringUtils.equals(key, END_EIGHT) || StringUtils.equals(key, CUBICATION_EIGHT)))
                    continue;
                if (StringUtils.equals(billReport, "6") &&
                        (StringUtils.equals(key, INFO_SEVEN) || StringUtils.equals(key, PERIOD_SEVEN) || StringUtils.equals(key, ACCOUNT_SEVEN) || StringUtils.equals(key, AMOUNT_SEVEN) || StringUtils.equals(key, PENALTY_SEVEN) || StringUtils.equals(key, ADDITIONAL_SEVEN) || StringUtils.equals(key, ADMIN_SEVEN) || StringUtils.equals(key, START_SEVEN) || StringUtils.equals(key, END_SEVEN) || StringUtils.equals(key, CUBICATION_SEVEN) ||
                                StringUtils.equals(key, INFO_EIGHT) || StringUtils.equals(key, PERIOD_EIGHT) || StringUtils.equals(key, ACCOUNT_EIGHT) || StringUtils.equals(key, AMOUNT_EIGHT) || StringUtils.equals(key, PENALTY_EIGHT) || StringUtils.equals(key, ADDITIONAL_EIGHT) || StringUtils.equals(key, ADMIN_EIGHT) || StringUtils.equals(key, START_EIGHT) || StringUtils.equals(key, END_EIGHT) || StringUtils.equals(key, CUBICATION_EIGHT)))
                    continue;
                if (StringUtils.equals(billReport, "7") &&
                        (StringUtils.equals(key, INFO_EIGHT) || StringUtils.equals(key, PERIOD_EIGHT) || StringUtils.equals(key, ACCOUNT_EIGHT) || StringUtils.equals(key, AMOUNT_EIGHT) || StringUtils.equals(key, PENALTY_EIGHT) || StringUtils.equals(key, ADDITIONAL_EIGHT) || StringUtils.equals(key, ADMIN_EIGHT) || StringUtils.equals(key, START_EIGHT) || StringUtils.equals(key, END_EIGHT) || StringUtils.equals(key, CUBICATION_EIGHT)))
                    continue;
            }


            String value = StringUtils.substring(bitValue, index, index + length);
            if (leftpad != null)
                value = StringUtils.stripStart(value, leftpad);
            else if (rightpad != null)
                value = StringUtils.stripEnd(value, rightpad);
            if(StringUtils.equalsAny(trx, MPNG3)){
                try{
                    String pattern = "&[^;]+;";
                    value = value.replaceAll(pattern, "").replace("#", "");
                } catch (Exception e) {
                }
            }

            TransactionLog log = new TransactionLog();
            log.setFieldName(key);
            log.setFieldValue(value);

            log.setTrxType(trx);
            // override routing from parameter
            if(!(trx.equalsIgnoreCase("tirtanadi") ||
                    trx.equalsIgnoreCase("tirtaayumiDti") ||
                    trx.equalsIgnoreCase("tirtakualoDti") ||
                    trx.equalsIgnoreCase("tirtatanjungDti") ||
                    trx.equalsIgnoreCase("tirtakualo") ||
                    trx.equalsIgnoreCase("tirtabulian") ||
                    trx.equalsIgnoreCase("tirtauli") ||
                    trx.equalsIgnoreCase("installation") ||
                    trx.equalsIgnoreCase("tirtaayumiDtiInstallation") ||
                    trx.equalsIgnoreCase("tirtakualoDtiInstallation") ||
                    trx.equalsIgnoreCase("tirtatanjungDtiInstallation") ||
                    trx.equalsIgnoreCase("speedy") ||
                    trx.equalsIgnoreCase("pbb") ||
                    trx.equalsIgnoreCase("djp") ||
                    trx.equalsIgnoreCase("djbc") ||
                    trx.equalsIgnoreCase("dja") ||
                    trx.equalsIgnoreCase("pstn") ||
                    trx.equalsIgnoreCase("bpjs") ||
                    trx.equalsIgnoreCase("bpjsKerja") ||
                    trx.equalsIgnoreCase("bpjsKerjaBpu") ||
                    trx.equalsIgnoreCase("bpjsKerjaVa") ||
                    trx.equalsIgnoreCase("pln") ||
                    trx.equalsIgnoreCase("nontaglist") ||
                    trx.equalsIgnoreCase("samsat") ||
                    trx.equalsIgnoreCase("samsatNasional") ||
                    trx.equalsIgnoreCase("token") ||
                    trx.equalsIgnoreCase("bpihinitial") ||
                    trx.equalsIgnoreCase("bpihinitialPrint") ||
                    trx.equalsIgnoreCase("bpihPayment") ||
                    trx.equalsIgnoreCase("bpihPaymentPrint") ||
                    trx.equalsIgnoreCase("edupay") ||
                    trx.equalsIgnoreCase("eduinit") ||
                    trx.equalsIgnoreCase("bpihinitialPrintDb") ||
                    trx.equalsIgnoreCase("bpihPaymentPrintDb") ||
                    trx.equalsIgnoreCase("djpReinquiry") ||
                    trx.equalsIgnoreCase("djbcReinquiry") ||
                    trx.equalsIgnoreCase("djaReinquiry") ||
                    trx.equalsIgnoreCase("djpg3") ||
                    trx.equalsIgnoreCase("djag3") ||
                    trx.equalsIgnoreCase("djbcg3") ||
                    trx.equalsIgnoreCase("djpg3Reinquiry") ||
                    trx.equalsIgnoreCase("djbcg3Reinquiry") ||
                    trx.equalsIgnoreCase("djag3Reinquiry") ||
                    trx.equalsIgnoreCase("paymentPSTN") ||
                    trx.equalsIgnoreCase("university") ||
                    trx.equalsIgnoreCase("bifast")
            )){
                String routing = FeatureContextHolder.getContext().getInstansiParameter();
                if (StringUtils.isNotEmpty(routing)) {
                    InstansiPdam instansiPdam = instansiPdamRepository.findByKodeInstansi(routing);
                    if (instansiPdam != null)
                        log.setTrxType(trx + StringUtils.capitalize(instansiPdam.getNamaInstansi()));
                }
            }


            log.setTraceNumber(traceNumber);

            valueList.add(log);

            if (StringUtils.equals(KEY_BIT, key) && (StringUtils.equals(PSTN_TRX_TYPE, trx) || StringUtils.equals(SPEEDY_TRX_TYPE, trx))) {
                billNumber = value;
            }
            if (StringUtils.equals(TIRTA_KEY_BIT, key) && (StringUtils.equalsAny(trx, TIRTANADI_TRX_TYPE, PDAM_DTI_TRX_TYPE))) {
                billReport = value;
            }
            index = index + length;
        }

        return valueList;
    }

    protected Set<TransactionLog> setTransactionRequest(String trx, String traceNumber, Set<TransactionLog> transactionLogs, ISOMsg isoMsg) {
        TransactionLog log = new TransactionLog();
        try {
            log.setFieldName(Constants.REQUEST_TIMEOUT_FIELD_NAME);
            log.setFieldValue(new String(isoMsg.pack()));
            log.setTrxType(trx);
            log.setTraceNumber(traceNumber);
            transactionLogs.add(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactionLogs;
    }

    protected Rule getRule() throws IOException, FileNotFoundException {
        // default implementasion is read the rule file base on feature --> feature.rule.json
        String feature = FeatureContextHolder.getContext().getFeatureName();
        String filename = feature + RULE_EXTENSION;
        logger.info("Feature Name on  Transaction Log : "+feature);
        String[] PDAMName = pdamRepository.fetchPDAMName();

        if(StringUtils.equalsAny(feature,PDAMName)){
            filename = "pdamDti" + RULE_EXTENSION;
            logger.info("Masuk If DTI TransactionLogService");
        }

        logger.info("Rule Filename : "+filename);
        if(cacheRule.hasContains(feature)){
            logger.info("Rule Filename from cache: "+feature);
            return cacheRule.get(feature);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        Resource resource = new ClassPathResource("reports/" + filename);
        Rule newRule= objectMapper.readValue(resource.getInputStream(), Rule.class);
        cacheRule.set(feature,newRule);
        logger.info("Rule Filename from resource: "+feature);
        return newRule;
        // convert json string to object
    }

    protected SiskoRule getSiskoRule() throws IOException, FileNotFoundException {
        String feature = FeatureContextHolder.getContext().getFeatureName();
        String filename = feature + RULE_EXTENSION;

        ObjectMapper objectMapper = new ObjectMapper();

        Resource resource = new ClassPathResource("reports/" + filename);

        // convert json string to object
        return objectMapper.readValue(resource.getInputStream(), SiskoRule.class);
    }

    protected String exception(ISOMsg msg, String errorKey) {
        String messageDesc = msg.getString(62);
        String resultCode = msg.getString(39);
        if (messageDesc == null) {
            ResourceMessage message = messageRepository.findByCodeAndMessage(errorKey, resultCode);
            messageDesc = message == null ? "Unmapping Error Number (" + resultCode + ")" : message.getValue();
        }

        return messageDesc;
    }
}
