package com.multipolar.uiteller.backend.service.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipolar.uiteller.backend.model.CommonTransactionRequestLog;
import com.multipolar.uiteller.backend.model.InstansiPdam;
import com.multipolar.uiteller.backend.model.ResourceMessage;
import com.multipolar.uiteller.backend.model.TransactionRequestLog;
import com.multipolar.uiteller.backend.model.converter.ConverterRule;
import com.multipolar.uiteller.backend.model.converter.RequestRule;
import com.multipolar.uiteller.backend.model.converter.Rule;
import com.multipolar.uiteller.backend.repository.CommonTransactionRequestLogRepository;
import com.multipolar.uiteller.backend.repository.InstansiPdamRepository;
import com.multipolar.uiteller.backend.repository.ResourceMessageRepository;
import com.multipolar.uiteller.backend.repository.TransactionRequestLogRepository;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.ISO87APackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class TransactionRequestLogService {
    private static Logger logger = LoggerFactory.getLogger(TransactionRequestLogService.class);

    private static final String RULE_EXTENSION = ".forward.json";

    private static final String KEY_BIT = "billNumber";
    private static final String REFF_THREE = "billReferenceThree";
    private static final String VALUE_THREE = "billValueThree";
    private static final String REFF_TWO = "billReferenceTwo";
    private static final String VALUE_TWO = "billValueTwo";

    private static final String PSTN_TRX_TYPE = "pstn";
    private static final String SPEEDY_TRX_TYPE = "speedy";

    @Autowired
    private CommonTransactionRequestLogRepository commonLogRepository;

    @Autowired
    private TransactionRequestLogRepository transactionLogRepository;

    @Autowired
    private ResourceMessageRepository messageRepository;

    @Autowired
    private InstansiPdamRepository instansiPdamRepository;

    @Transactional
    public CommonTransactionRequestLog logTransaction(ISOMsg req) throws IOException {
        Rule rule = null;
        try {
            rule = getRule();
        } catch (FileNotFoundException fnfe) {
            logger.error("No rule for this transaction. Bit 63: " + req.getString(63));
        }

        CommonTransactionRequestLog commonLog = generateCommonLog();
        commonLogRepository.save(commonLog);

        Set<TransactionRequestLog> logs = createDetailList(req, rule);
        for (TransactionRequestLog log : logs) {
            log.setCommonTransactionRequest(commonLog);
            transactionLogRepository.save(log);
        }

        return commonLog;
    }

    @Transactional
    public void saveAdditionalLog(List<TransactionRequestLog> logs, CommonTransactionRequestLog commonLog, String traceNumber, String trxType) {
        for (TransactionRequestLog log : logs) {
            log.setCommonTransactionRequest(commonLog);
            log.setTraceNumber(traceNumber);
            log.setTrxType(trxType);
            transactionLogRepository.save(log);
        }
    }

    @Transactional
    public void saveState(CommonTransactionRequestLog commonTransactionRequestLog, ISOMsg res) {
        commonTransactionRequestLog.setRc(res.getString(39));
        commonTransactionRequestLog.setStatus(1);
        commonTransactionRequestLog.setMessage(exception(res, "errorKey"));
        commonLogRepository.save(commonTransactionRequestLog);
    }

    private CommonTransactionRequestLog generateCommonLog() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userid = auth.getPrincipal().toString();
        String branchId = ((UserDetailImpl) auth.getDetails()).getBranchCode();
        Date trxDate = FeatureContextHolder.getContext().getFeatureTransactionDate();
        CommonTransactionRequestLog commonLog = new CommonTransactionRequestLog();
        commonLog.setBranchCode(branchId);
        commonLog.setUserid(userid);
        commonLog.setTrxDate(trxDate);
        commonLog.setStatus(2);
        commonLog.setRc(Constants.NO_RESPONSE_CODE);
        commonLog.setMessage("No Response From Host");
        return commonLog;
    }

    protected Set<TransactionRequestLog> createDetailList(ISOMsg req, Rule rule) throws IOException {
        Set<TransactionRequestLog> allLogs = new HashSet<>();
        if (rule == null)
            return new HashSet<>();
        String transactionKey = rule.getTransaction();
        RequestRule[] reportRules = rule.getReport();
        String traceNumber = req.getString(11);
        for (RequestRule reportRule : reportRules) {
            int bit = reportRule.getBit();
            String source = reportRule.getSource();

            String valueFromBit;
            if (StringUtils.equals("request", source))
                valueFromBit = req.getString(bit);
            else
                throw new IllegalArgumentException("source only can contain value either request or response");

            if (valueFromBit == null) {
                logger.error("Value get from bit is null, bit = " + bit);
                throw new IllegalArgumentException("The rule said get value from bit but empty bit is found. bit " + bit);
            }


            ConverterRule[] values = reportRule.getValue();
            Set<TransactionRequestLog> logs = setTransactionDetailValues(transactionKey, traceNumber, valueFromBit, values);
            allLogs.addAll(logs);
        }
        req.setPackager(new ISO87APackager());
        setTransactionRequest(transactionKey, allLogs, req);
        return allLogs;
    }

    protected Set<TransactionRequestLog> setTransactionDetailValues(String trx, String traceNumber, String bitValue, ConverterRule[] rules) {
        int index = 0;
        Set<TransactionRequestLog> valueList = new HashSet<>();
        String billNumber = "1";
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


            String value = StringUtils.substring(bitValue, index, index + length);
            if (leftpad != null)
                value = StringUtils.stripStart(value, leftpad);
            else if (rightpad != null)
                value = StringUtils.stripEnd(value, rightpad);

            TransactionRequestLog log = new TransactionRequestLog();
            log.setFieldName(key);
            log.setFieldValue(value);
            log.setTrxType(trx);
            // jika pdamDti, hack tambahkan nama instansi nya
            if (StringUtils.startsWith(trx, "pdamDti")) {
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
            index = index + length;
        }

        return valueList;
    }

    protected Set<TransactionRequestLog> setTransactionRequest(String trx, Set<TransactionRequestLog> transactionLogs, ISOMsg req) {
        TransactionRequestLog log = new TransactionRequestLog();
        try {
            log.setFieldName(Constants.REQUEST_TIMEOUT_FIELD_NAME);
            log.setFieldValue(new String(req.pack()));
            log.setTrxType(trx);
            // jika pdamDti, hack tambahkan nama instansi nya
            if (StringUtils.startsWith(trx, "pdamDti")) {
                String routing = FeatureContextHolder.getContext().getInstansiParameter();
                if (StringUtils.isNotEmpty(routing)) {
                    InstansiPdam instansiPdam = instansiPdamRepository.findByKodeInstansi(routing);
                    if (instansiPdam != null)
                        log.setTrxType(trx + StringUtils.capitalize(instansiPdam.getNamaInstansi()));
                }
            }
            log.setTraceNumber(req.getString(11));
            transactionLogs.add(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactionLogs;
    }

    protected Rule getRule() throws IOException {
        String feature = FeatureContextHolder.getContext().getFeatureName();
        String filename = feature + RULE_EXTENSION;

        ObjectMapper objectMapper = new ObjectMapper();

        Resource resource = new ClassPathResource("forward/" + filename);

        return objectMapper.readValue(resource.getInputStream(), Rule.class);
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
