package com.multipolar.uiteller.backend.controller;

import com.ibm.icu.text.SimpleDateFormat;
import com.multipolar.uiteller.backend.model.CommonTransactionRequestLog;
import com.multipolar.uiteller.backend.model.CommonTransactionSiskoRequestLog;
import com.multipolar.uiteller.backend.model.TransactionRequestLog;
import com.multipolar.uiteller.backend.model.TransactionSiskoRequestLog;
import com.multipolar.uiteller.backend.repository.TransactionRequestLogRepository;
import com.multipolar.uiteller.backend.repository.TransactionSiskoRequestLogRepository;
import com.multipolar.uiteller.backend.resource.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/apiv2")
public class ForwardController {

    @Autowired
    private TransactionRequestLogRepository logRepository;
    @Autowired
    private TransactionSiskoRequestLogRepository transactionSiskoRequestLogRepository;

    private static final Map<String, String> MAP_ID;
    private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
    private static final SimpleDateFormat DATE = new SimpleDateFormat("yyyyMMdd");

    static {
        MAP_ID = new HashMap<>();
        MAP_ID.put("tirtanadi", "customerId");
        MAP_ID.put("tirtakualo", "customerId");
        MAP_ID.put("tirtabulian", "customerId");
        MAP_ID.put("tirtauli", "customerId");
        MAP_ID.put("installation", "customerId");
        MAP_ID.put("tirtaayumiDti", "customerId");
        MAP_ID.put("tirtakualoDti", "customerId");
        MAP_ID.put("tirtatanjungDti", "customerId");
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
        MAP_ID.put("speedy", "customerId");
        MAP_ID.put("bpjs", "customerId");
        MAP_ID.put("bpjsKerja", "customerId");
        MAP_ID.put("bpjsKerjaBpu", "nik");
        MAP_ID.put("pln", "meterNumber");
        MAP_ID.put("nontaglist", "registrationNumber");
        MAP_ID.put("samsat", "payCode");
        MAP_ID.put("samsatNasional", "payCode");
        MAP_ID.put("token", "meterNumber");
        MAP_ID.put("bpihinitial", "sourceNumber");
        MAP_ID.put("bpihPayment", "porsiNumber");
        MAP_ID.put("eduinit", "identitas");
        MAP_ID.put("edupay", "kodeBilling");
        MAP_ID.put("tirtanadi", "customerId");
    }

    @GetMapping("/forward")
    public List<Map<String, String>> getTransaction(@RequestParam String trxType, @RequestParam String customerId, @RequestParam(name = "trace", required = false) String trace, @RequestParam String userId, @RequestParam int rev) {
        List<Map<String, String>> results = new ArrayList<>();

        String areaCode = null, phoneNumber = null;
        if (StringUtils.equals(trxType, "pstn")) {
            areaCode = StringUtils.substring(customerId, 0, 4);
            customerId = StringUtils.substring(customerId, 4);
        }

        String customerKey = MAP_ID.get(trxType);
        if (customerKey == null)
            customerKey = "customerId";

        List<TransactionRequestLog> logs = null;
        if (StringUtils.isEmpty(trace))
            logs = rev == 0 ?
                    logRepository.findByFieldNameAndFieldValueAndTrxTypeAndCommonTransactionRequestUseridOrderByCommonTransactionRequestIdDesc(customerKey, customerId, trxType, userId)
                    :
                    logRepository.findDateWithoutTrace(customerKey, customerId, trxType, userId, DATE.format(new Date()));
        else
            logs = rev == 0 ?
                    logRepository.findByFieldNameAndFieldValueAndTrxTypeAndTraceNumberAndCommonTransactionRequestUseridOrderByCommonTransactionRequestIdDesc(customerKey, customerId, trxType, trace, userId)
                    :
                    logRepository.findDateWithTrace(customerKey, customerId, trxType, trace, userId, DATE.format(new Date()));

        for (TransactionRequestLog log : logs) {
            Map<String, String> map = new HashMap<>();

            CommonTransactionRequestLog commonLog = log.getCommonTransactionRequest();
            Long commonId = commonLog.getId();
            String sts = commonLog.getRc();
            if (logRepository.findByCommonTransactionRequestIdAndFieldName(commonId, Constants.FORWARDING_FIELD_NAME) != null || logRepository.findByCommonTransactionRequestIdAndFieldName(commonId, Constants.REVERSAL_FIELD_NAME) != null
                    || !(StringUtils.equals(sts, Constants.NO_RESPONSE_CODE) || StringUtils.equals(sts, Constants.TIMEOUT_CODE)
                    || StringUtils.equals(sts, Constants.TIMEOUT_CODE_ONE) || StringUtils.equals(sts, Constants.TIMEOUT_CODE_TWO) || StringUtils.equals(sts, Constants.TIMEOUT_CODE_THREE))
                    || StringUtils.equals(sts, Constants.SUCCESS_CODE))
                continue;
            List<TransactionRequestLog> trxLogs = logRepository.findByCommonTransactionRequestId(commonId);

            map.put("branchCode", commonLog.getBranchCode());
            map.put("transactionDate", format.format(commonLog.getTrxDate()));
            map.put("status", commonLog.getStatus() == 0 ? "Berhasil" : "Gagal");
            map.put("tellerId", commonLog.getUserid());
            map.put("commonId", String.valueOf(commonId));

            for (TransactionRequestLog tl : trxLogs)
                map.put(tl.getFieldName(), tl.getFieldValue());

            if (StringUtils.equals(trxType, "pstn") && areaCode != null && phoneNumber != null) {
                String areaCodeValue = map.get("areaCode");
                if (!StringUtils.equals(areaCode, areaCodeValue))
                    continue;
            }

            map.put("traceNumber", log.getTraceNumber());

            if (!map.containsKey("customerId")) {
                if (StringUtils.equals(trxType, "pstn"))
                    map.put("customerId", map.get("areaCode") + " - " + map.get(customerKey));
                else
                    map.put("customerId", map.get(customerKey));
            }

            results.add(map);
        }
        return results;
    }

    @GetMapping("/forwarddetail")
    public Map<String, String> getTransaction(@RequestParam Long id) {
        Map<String, String> map = new HashMap<>();
        List<TransactionRequestLog> trxLogs = logRepository.findByCommonTransactionRequestId(id);
        for (TransactionRequestLog tl : trxLogs)
            map.put(tl.getFieldName(), tl.getFieldValue());
        return map;
    }

    @GetMapping("/siskoforward")
    public List<Map<String, String>> getSiskoTransaction(@RequestParam String trxType, @RequestParam String customerId, @RequestParam(name = "trace", required = false) String trace) {
        List<Map<String, String>> results = new ArrayList<>();

        String customerKey = MAP_ID.get(trxType);

        List<TransactionSiskoRequestLog> logs = null;
        if (StringUtils.isEmpty(trace))
            logs = transactionSiskoRequestLogRepository.findByFieldNameAndFieldValueAndTrxTypeOrderByCommonTransactionSiskoRequestLogIdDesc(customerKey, customerId, trxType);
        else
            logs = transactionSiskoRequestLogRepository.findByFieldNameAndFieldValueAndTrxTypeAndTraceNumberOrderByCommonTransactionSiskoRequestLogIdDesc(customerKey, customerId, trxType, trace);

        for (TransactionSiskoRequestLog log : logs) {
            Map<String, String> map = new HashMap<>();

            CommonTransactionSiskoRequestLog commonLog = log.getCommonTransactionSiskoRequestLog();
            Long commonId = commonLog.getId();
            String sts = commonLog.getRc();
            if (transactionSiskoRequestLogRepository.findByCommonTransactionSiskoRequestLogIdAndFieldName(commonId, Constants.FORWARDING_FIELD_NAME) != null
                    || transactionSiskoRequestLogRepository.findByCommonTransactionSiskoRequestLogIdAndFieldName(commonId, Constants.REVERSAL_FIELD_NAME) != null
                    || StringUtils.equals(sts, Constants.SUCCESS_CODE) || StringUtils.equals(sts, Constants.FAILED_DEBIT_CODE_SISKO))
                continue;
            List<TransactionSiskoRequestLog> trxLogs = transactionSiskoRequestLogRepository.findByCommonTransactionSiskoRequestLogId(commonId);

            map.put("branchCode", commonLog.getBranchCode());
            map.put("transactionDate", format.format(commonLog.getTrxDate()));
            map.put("status", commonLog.getStatus() == 0 ? "Berhasil" : "Gagal");
            map.put("tellerId", commonLog.getUserid());
            map.put("commonId", String.valueOf(commonId));

            for (TransactionSiskoRequestLog tl : trxLogs)
                map.put(tl.getFieldName(), tl.getFieldValue());

            map.put("traceNumber", log.getTraceNumber());

            if (!map.containsKey("customerId"))
                map.put("customerId", map.get(customerKey));

            results.add(map);
        }
        return results;
    }
}
