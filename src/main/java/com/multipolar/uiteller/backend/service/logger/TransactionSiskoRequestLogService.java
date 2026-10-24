package com.multipolar.uiteller.backend.service.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipolar.uiteller.backend.model.CommonTransactionSiskoRequestLog;
import com.multipolar.uiteller.backend.model.ResourceMessage;
import com.multipolar.uiteller.backend.model.TransactionSiskoRequestLog;
import com.multipolar.uiteller.backend.model.converter.ConverterRule;
import com.multipolar.uiteller.backend.model.converter.RequestRule;
import com.multipolar.uiteller.backend.model.converter.Rule;
import com.multipolar.uiteller.backend.model.converter.SiskoRule;
import com.multipolar.uiteller.backend.repository.CommonTransactionSiskoRequestLogRepository;
import com.multipolar.uiteller.backend.repository.ResourceMessageRepository;
import com.multipolar.uiteller.backend.repository.TransactionSiskoRequestLogRepository;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
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
public class TransactionSiskoRequestLogService {
    private static Logger logger = LoggerFactory.getLogger(TransactionSiskoRequestLogService.class);

    private static final String RULE_EXTENSION = ".forward.json";

    private static final String KEY_BIT = "billNumber";
    private static final String REFF_THREE = "billReferenceThree";
    private static final String VALUE_THREE = "billValueThree";
    private static final String REFF_TWO = "billReferenceTwo";
    private static final String VALUE_TWO = "billValueTwo";

    private static final String PSTN_TRX_TYPE = "pstn";
    private static final String SPEEDY_TRX_TYPE = "speedy";

    @Autowired
    private CommonTransactionSiskoRequestLogRepository commonLogRepository;

    @Autowired
    private TransactionSiskoRequestLogRepository transactionLogRepository;

    @Autowired
    private ResourceMessageRepository messageRepository;

    @Transactional
    public CommonTransactionSiskoRequestLog logTransaction(ISOMsg req, ISOMsg debitReq) throws IOException, ISOException {
        Rule rule = null;
        try {
            rule = getRule();
        } catch (FileNotFoundException fnfe) {
            logger.error("No rule for this transaction. Bit 63: " + req.getString(63));
        }

        CommonTransactionSiskoRequestLog commonLog = generateCommonLog();
        commonLogRepository.save(commonLog);

        Set<TransactionSiskoRequestLog> logs = createDetailList(req, rule, debitReq);
        for (TransactionSiskoRequestLog log : logs) {
            log.setCommonTransactionSiskoRequestLog(commonLog);
            transactionLogRepository.save(log);
        }

        return commonLog;
    }

    @Transactional
    public void saveAdditionalLog(List<TransactionSiskoRequestLog> logs, CommonTransactionSiskoRequestLog commonLog, String traceNumber, String trxType) {
        for (TransactionSiskoRequestLog log : logs) {
            log.setCommonTransactionSiskoRequestLog(commonLog);
            log.setTraceNumber(traceNumber);
            log.setTrxType(trxType);
            transactionLogRepository.save(log);
        }
    }

    @Transactional
    public void saveState(CommonTransactionSiskoRequestLog commonTransactionSiskoRequestLog, ISOMsg res) {
        commonTransactionSiskoRequestLog.setRc(res.getString(39));
        commonTransactionSiskoRequestLog.setStatus(1);
        commonTransactionSiskoRequestLog.setMessage(exception(res, "errorKey"));
        commonLogRepository.save(commonTransactionSiskoRequestLog);
    }

    private CommonTransactionSiskoRequestLog generateCommonLog() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userid = auth.getPrincipal().toString();
        String branchId = ((UserDetailImpl) auth.getDetails()).getBranchCode();
        Date trxDate = FeatureContextHolder.getContext().getFeatureTransactionDate();
        CommonTransactionSiskoRequestLog commonLog = new CommonTransactionSiskoRequestLog();
        commonLog.setBranchCode(branchId);
        commonLog.setUserid(userid);
        commonLog.setTrxDate(trxDate);
        commonLog.setStatus(2);
        commonLog.setRc(Constants.NO_RESPONSE_CODE);
        commonLog.setMessage("No Response From Host");
        return commonLog;
    }

    protected Set<TransactionSiskoRequestLog> createDetailList(ISOMsg req, Rule rule, ISOMsg debitReq) throws IOException, ISOException {
        Set<TransactionSiskoRequestLog> allLogs = new HashSet<>();
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
            Set<TransactionSiskoRequestLog> logs = setTransactionDetailValues(transactionKey, traceNumber, valueFromBit, values);
            allLogs.addAll(logs);
        }
        debitReq.setPackager(new ISO87APackager());
        setTransactionRequest(Constants.DEBIT_REQUEST_TIMEOUT_FIELD_NAME, transactionKey, allLogs, debitReq);
        req.setPackager(new GenericPackager(new ClassPathResource("packager/" + Constants.EBCDIC).getInputStream()));
        setTransactionRequest(Constants.REQUEST_TIMEOUT_FIELD_NAME, transactionKey, allLogs, req);
        return allLogs;
    }

    protected Set<TransactionSiskoRequestLog> setTransactionDetailValues(String trx, String traceNumber, String bitValue, ConverterRule[] rules) {
        int index = 0;
        Set<TransactionSiskoRequestLog> valueList = new HashSet<>();
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

            TransactionSiskoRequestLog log = new TransactionSiskoRequestLog();
            log.setFieldName(key);
            log.setFieldValue(value);
            log.setTrxType(trx);
            log.setTraceNumber(traceNumber);

            valueList.add(log);

            if (StringUtils.equals(KEY_BIT, key) && (StringUtils.equals(PSTN_TRX_TYPE, trx) || StringUtils.equals(SPEEDY_TRX_TYPE, trx))) {
                billNumber = value;
            }
            index = index + length;
        }

        return valueList;
    }

    protected Set<TransactionSiskoRequestLog> setTransactionRequest(String nm, String trx, Set<TransactionSiskoRequestLog> transactionLogs, ISOMsg req) {
        TransactionSiskoRequestLog log = new TransactionSiskoRequestLog();
        try {
            log.setFieldName(nm);
            log.setFieldValue(StringUtils.equals(nm, Constants.REQUEST_TIMEOUT_FIELD_NAME) ? new String(ISOUtil.ebcdicToAscii(req.pack())) : new String(req.pack()));
            log.setTrxType(trx);
            log.setTraceNumber(req.getString(11));
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

        ObjectMapper objectMapper = new ObjectMapper();

        Resource resource = new ClassPathResource("forward/" + filename);

        // convert json string to object
        return objectMapper.readValue(resource.getInputStream(), Rule.class);
    }

    protected SiskoRule getSiskoRule() throws IOException, FileNotFoundException {
        String feature = FeatureContextHolder.getContext().getFeatureName();
        String filename = feature + RULE_EXTENSION;

        ObjectMapper objectMapper = new ObjectMapper();

        Resource resource = new ClassPathResource("forward/" + filename);

        // convert json string to object
        return objectMapper.readValue(resource.getInputStream(), SiskoRule.class);
    }

    protected String exception(ISOMsg msg, String errorKey) {
        String messageDesc = "";
        String resultCode = msg.getString(39);
        ResourceMessage message = messageRepository.findByCodeAndMessage(errorKey, resultCode);
        messageDesc = message == null ? "Unmapping Error Number (" + resultCode + ")" : message.getValue();
        return messageDesc;
    }
}
