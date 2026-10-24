package com.multipolar.uiteller.backend.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.icu.text.SimpleDateFormat;
import com.multipolar.uiteller.backend.model.CommonTransactionLog;
import com.multipolar.uiteller.backend.model.CommonTransactionRequestLog;
import com.multipolar.uiteller.backend.model.Sipd;
import com.multipolar.uiteller.backend.model.TransactionRequestLog;
import com.multipolar.uiteller.backend.model.converter.ConverterRule;
import com.multipolar.uiteller.backend.model.converter.RequestRule;
import com.multipolar.uiteller.backend.model.converter.Rule;
import com.multipolar.uiteller.backend.repository.CommonTransactionLogRepository;
import com.multipolar.uiteller.backend.repository.CommonTransactionRequestLogRepository;
import com.multipolar.uiteller.backend.repository.SysParamRepository;
import com.multipolar.uiteller.backend.repository.TransactionRequestLogRepository;
import com.multipolar.uiteller.backend.resource.BusinessException;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.*;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import com.sleepycat.je.log.entry.BINDeltaLogEntry;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.mapping.Any;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class MiddlewareGatewayServiceImpl {
    private static Logger logger = LoggerFactory.getLogger(MiddlewareGatewayServiceImpl.class);
    @Value("${mdw.server.url}")
    private String url;
    @Value("${sipd.server.url}")
    private String sipdUrl;
    @Value("${mdw.server.user}")
    private String user;
    @Value("${mdw.server.token}")
    private String token;
    private static final String RULE_EXTENSION = ".rule.json";
    private RestTemplate restTemplate;
    private static final SimpleDateFormat DATE = new SimpleDateFormat("dd-MMM-yy");
    private static final SimpleDateFormat REPORT_DATE = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    private static final Map<String, String> MAP_ID;
    private static final String OK_CODE = "200";
    private static final String[] BIT_48 = { "time", "billing", "amount", "refID", "traceID", "settlementDate", "branch", "branchCode" };
    private static final String SETTLEMENT_DATE = "settlementDate";
    private static final String TIME = "time";
    @Autowired
    private CommonTransactionRequestLogRepository commonTransactionRequestLogRepository;
    @Autowired
    private CommonTransactionLogRepository commonTransactionLogRepository;
    @Autowired
    private SysParamRepository sysParamRepository;
    @Autowired
    private TransactionRequestLogRepository transactionRequestLogRepository;

    static {
        MAP_ID = new HashMap<>();
        MAP_ID.put("ref", "refnum");
        MAP_ID.put("sourceNumber", "accSrc");
        MAP_ID.put("nominal", "amount");
    }

    public MiddlewareGatewayServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ISOMsg> getDetailResponse(String card, String id) {
        try {
            String urls = url + Constants.MIDDLEWARE_RECEIPT;
            List<ISOMsg> isoMsgList = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> map = new HashMap<>();
            HttpEntity<Map<String, String>> entity = new HttpEntity<Map<String, String>>(map, headers);
            ResponseEntity<String> response = restTemplate.exchange(buildJsonDetailRequestBody(urls, card, id), HttpMethod.GET, entity, String.class);
            JsonFactory jsonFactory = new JsonFactory();
            ObjectMapper objectMapper = new ObjectMapper(jsonFactory);
            jsonFactory.setCodec(objectMapper);
            JsonParser jsonParser = jsonFactory.createParser(response.getBody());
            ObjectCodec objectCodec = jsonParser.getCodec();
            Map<String, Object> stringMap = objectCodec.readValue(jsonParser, Map.class);
            if (StringUtils.equals(Constants.SUCCESS_CODE, stringMap.get("rCode").toString())) {
                List<Map<String, Object>> maps = (List<Map<String, Object>>) stringMap.get("result");
                for (Map<String, Object> entry : maps) {
                    ISOMsg isoMsg = new ISOMsg();
                    for (Map.Entry<String, Object> entry1 : entry.entrySet())
                        isoMsg.set(StringUtils.substring(entry1.getKey(), 1, entry1.getKey().length()), entry1.getValue() == null ? Constants.BLANK : entry1.getValue().toString());
                    isoMsg.set(39, Constants.SUCCESS_CODE);
                    isoMsg.set(49, Constants.IDR_CODE);
                    isoMsg.set(57, Constants.OVERBOOKING_PAY_TYPE);
                    isoMsg.set(58, Constants.BLANK);
                    isoMsgList.add(isoMsg);
                }
            }
            return isoMsgList;
        } catch (Exception ex) {
            logger.error("Exception happen when contacting authentication server", ex);
            throw new UsernameNotFoundException("Exception happen when contacting authentication server", ex);
        }
    }
    public List<ISOMsg> getSipdDetailResponse(String id) {
        try {
            Rule rule = getRule();
            String urls = sipdUrl+"?billing={id}";
            String[] listId = id.split(";");
            String auth = getBasicAuthenticationHeader("tellerAdmin", "tell3R123");
            List<ISOMsg> isoMsgList = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", auth);
            Map<String, String> map = new HashMap<>();
            HttpEntity<Map<String, String>> entity = new HttpEntity<Map<String, String>>(map, headers);
            ResponseEntity<String> response = restTemplate.exchange(buildJsonDetailRequestBodySipd(urls, id), HttpMethod.GET, entity, String.class);
            JsonFactory jsonFactory = new JsonFactory();
            ObjectMapper objectMapper = new ObjectMapper(jsonFactory);
            jsonFactory.setCodec(objectMapper);
            JsonParser jsonParser = jsonFactory.createParser(response.getBody());
            ObjectCodec objectCodec = jsonParser.getCodec();
            Map<String, Map<String, Object>> stringMap = objectCodec.readValue(jsonParser, Map.class);
            logger.info("Response : "+response.getBody());
            if (StringUtils.equals(OK_CODE, response.getStatusCode().toString())) {
                for(String id1 : listId) {
                    Map<String, Object> result = stringMap.get(id1);
//                    logger.info("Result : "+result);
                    if(result == null){
                        continue;
                    }
                    ISOMsg isoMsg = new ISOMsg();
                    String bit48 = null;
                    for (Map.Entry<String, Object> entry1 : result.entrySet()) {
                        String key = entry1.getKey();
                        String value;
                        if (entry1.getValue() != null) {
                            value = entry1.getValue().toString();
                        } else {
                            value = "-";
                        }
                        if (StringUtils.equals(key, "date")) {
                            isoMsg.set(12, value);
                        }
                        if(StringUtils.equals(key, "rawData"))
                            isoMsg.set(61, value.toString());
                        if(StringUtils.equalsAny(key, BIT_48)){
                            value = setValueForMap(key, value, getResponseValue(rule, 48));
                            bit48 += value;
                        }
                    }
                    isoMsg.set(48, bit48.substring(4));
                    isoMsg.set(39, Constants.SUCCESS_CODE);
                    isoMsg.set(49, Constants.IDR_CODE);
                    isoMsg.set(57, Constants.OVERBOOKING_PAY_TYPE);
                    isoMsg.set(58, Constants.BLANK);
                    isoMsgList.add(isoMsg);
//                    logger.info("48 "+isoMsg.getString(48));
//                }
                }
            }
            return isoMsgList;
        } catch (Exception ex) {
            logger.error("Exception happen when contacting authentication server", ex);
            throw new UsernameNotFoundException("Exception happen when contacting authentication server", ex);
        }
    }
    private ConverterRule[] getResponseValue(Rule rule, int bit){
        ConverterRule [] values = {};
        RequestRule[] responseRules = rule.getResponse();
        for (RequestRule responseRule : responseRules) {
            ConverterRule[] newValues = responseRule.getValue();
            if(responseRule.getBit() == bit){
                return newValues;
            }
        }
        return values;
    }
    private Rule getRule() throws IOException {
        String feature = FeatureContextHolder.getContext().getFeatureName();
        String filename = feature + RULE_EXTENSION;
        logger.info("rule name is: " + filename);
        ObjectMapper objectMapper = new ObjectMapper();
        Resource resource = new ClassPathResource("rules/" + filename);
        return objectMapper.readValue(resource.getInputStream(), Rule.class);
    }
    protected String setValueForMap(String keyRes, String valueRes, ConverterRule[] rules) throws IOException {
//        logger.info(""+keyRes +":"+valueRes);
        int ruleLength = 0;
        String pad = "";
        for(ConverterRule rule : rules){
            String key = rule.getKey();
            pad = rule.getLeftpad();
//            System.out.println("KEY : "+keyRes+":"+key);
            if(StringUtils.equals(keyRes, key)){
                ruleLength = rule.getLength();
//                logger.info("SAME" + ruleLength);
            }
        }
        valueRes = StringUtils.leftPad(valueRes, ruleLength, pad);
//        System.out.println("VALUE RES : "+valueRes);
        return valueRes;
    }

    private static final String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + DatatypeConverter.printBase64Binary(valueToEncode.getBytes());
    }

    public List<AllActivity> getAllTellerResponse() {
        List<AllActivity> allActivities = new ArrayList<>();
        Object[] objects = commonTransactionLogRepository.findUserid();
        for (Object o : objects) {
            Object[] objects1 = (Object[]) o;
            AllActivity allActivity = new AllActivity();
            allActivity.setUserId(objects1[0].toString());
            getSomeTellerCreditResponse(objects1[1].toString() + objects1[0].toString(), allActivity);
            allActivities.add(allActivity);
        }
        return allActivities;
    }

    public void getOneTellerResponse(String user, String branch, NoteActivity noteActivity) {
        AllActivity allActivity = new AllActivity();
        allActivity.setUserId(user);
        getSomeTellerCreditResponse(allActivity.getUserId(), allActivity);
        noteActivity.setBranchName(branch);
        noteActivity.setUser(user);
        noteActivity.setTransactionDate(REPORT_DATE.format(new Date()));
        noteActivity.setDescription(sysParamRepository.findByPropertyName(Constants.DESCRIPTION_ACTIVITY_NOTE_MIDDLEWARE_REPORT) == null ? Constants.STRIP_CODE : sysParamRepository.findByPropertyName(Constants.DESCRIPTION_ACTIVITY_NOTE_MIDDLEWARE_REPORT).getPropertyValue());
        noteActivity.setCreditNominal(allActivity.getCreditCash());
        noteActivity.setSpell(new KonversiMataUangKeTerbilang(noteActivity.getCreditNominal().setScale(0)).toString());
    }

    public List<Activity> getSomeTellerResponse(String user, String pay) {
        try {
            String urls = url + Constants.MIDDLEWARE_REPORT;
            BigDecimal debit = BigDecimal.ZERO;
            BigDecimal credit = BigDecimal.ZERO;
            AllActivity allActivity = new AllActivity();
            List<Map<String, String>> t1 = new ArrayList<>();
            List<Activity> activities = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> map = new HashMap<>();
            HttpEntity<Map<String, String>> entity = new HttpEntity<Map<String, String>>(map, headers);
            ResponseEntity<String> response = restTemplate.exchange(buildJsonSomeTellerRequestBody(urls, user), HttpMethod.GET, entity, String.class);
            JsonFactory jsonFactory = new JsonFactory();
            ObjectMapper objectMapper = new ObjectMapper(jsonFactory);
            jsonFactory.setCodec(objectMapper);
            JsonParser jsonParser = jsonFactory.createParser(response.getBody());
            ObjectCodec objectCodec = jsonParser.getCodec();
            Map<String, Object> stringMap = objectCodec.readValue(jsonParser, Map.class);
            if (StringUtils.equals(Constants.SUCCESS_CODE, stringMap.get("rCode").toString())) {
                List<Map<String, Object>> maps = (List<Map<String, Object>>) stringMap.get("result");
                for (Map<String, Object> entry : maps) {
                    Map<String, String> t2 = new HashMap<>();
                    for (Map.Entry<String, Object> entry1 : entry.entrySet()) {
                        t2.put(entry1.getKey(), entry1.getValue().toString());
                        if (StringUtils.equals(entry1.getKey(), Constants.KEY_MIDDLEWARE_REPORT)) {
                            Map<String, String> objectMap = (Map<String, String>) entry1.getValue();
                            t2.putAll(objectMap);
                        }
                    }
                    t1.add(t2);
                }
                for (Map<String, String> map1 : t1) {
                    for (Integer i = 1; i <= 6; i++) {
                        String y = Integer.toString(i);
                        if (!StringUtils.equals(map1.get(Constants.TRANSACTION_TYPE_MIDDLEWARE_REPORT), pay))
                            continue;
                        if (map1.get(Constants.DEBIT_SOURCE_NUMBER_MIDDLEWARE_REPORT + y) != null) {
                            Activity activity = new Activity();
                            activity.setArchiveNumber(map1.get(Constants.ARCHIVE_NUMBER_MIDDLEWARE_REPORT));
                            activity.setSourceName(Constants.STRIP_CODE);
                            activity.setSourceNumber(map1.get(Constants.DEBIT_SOURCE_NUMBER_MIDDLEWARE_REPORT + y));
                            activity.setCurrency(map1.get(Constants.CURRENCY_MIDDLEWARE_REPORT));
                            activity.setDescription(map1.get(Constants.DEBIT_TRANSACTION_MESSAGE_MIDDLEWARE_REPORT + y));
                            activity.setTransactionCode(map1.get(Constants.DEBIT_TRANSACTION_CODE_MIDDLEWARE_REPORT + y));
                            activity.setDebit(new BigDecimal(map1.get(Constants.DEBIT_NOMINAL_MIDDLEWARE_REPORT + y)));
                            activity.setCredit(BigDecimal.ZERO);
                            activities.add(activity);
                        }
                        if (map1.get(Constants.CREDIT_SOURCE_NUMBER_MIDDLEWARE_REPORT + y) != null) {
                            Activity activity = new Activity();
                            activity.setArchiveNumber(map1.get(Constants.ARCHIVE_NUMBER_MIDDLEWARE_REPORT));
                            activity.setSourceNumber(map1.get(Constants.CREDIT_SOURCE_NUMBER_MIDDLEWARE_REPORT + y));
                            activity.setSourceName(Constants.STRIP_CODE);
                            activity.setCurrency(map1.get(Constants.CURRENCY_MIDDLEWARE_REPORT));
                            activity.setDescription(map1.get(Constants.CREDIT_TRANSACTION_MESSAGE_MIDDLEWARE_REPORT + y));
                            activity.setTransactionCode(map1.get(Constants.CREDIT_TRANSACTION_CODE_MIDDLEWARE_REPORT + y));
                            activity.setDebit(BigDecimal.ZERO);
                            activity.setCredit(new BigDecimal(map1.get(Constants.CREDIT_NOMINAL_MIDDLEWARE_REPORT + y)));
                            activities.add(activity);
                        }
                        if (map1.get(Constants.FEE_DEBIT_SOURCE_NUMBER_MIDDLEWARE_REPORT + y) != null) {
                            Activity activity = new Activity();
                            activity.setArchiveNumber(map1.get(Constants.ARCHIVE_NUMBER_MIDDLEWARE_REPORT));
                            activity.setSourceNumber(map1.get(Constants.FEE_DEBIT_SOURCE_NUMBER_MIDDLEWARE_REPORT + y));
                            activity.setSourceName(Constants.STRIP_CODE);
                            activity.setCurrency(map1.get(Constants.CURRENCY_MIDDLEWARE_REPORT));
                            activity.setDescription(map1.get(Constants.FEE_DEBIT_TRANSACTION_MESSAGE_MIDDLEWARE_REPORT + y));
                            activity.setTransactionCode(map1.get(Constants.FEE_DEBIT_TRANSACTION_CODE_MIDDLEWARE_REPORT + y));
                            activity.setDebit(new BigDecimal(map1.get(Constants.FEE_DEBIT_NOMINAL_MIDDLEWARE_REPORT + y)));
                            activity.setCredit(BigDecimal.ZERO);
                            activities.add(activity);
                        }
                        if (map1.get(Constants.FEE_CREDIT_SOURCE_NUMBER_MIDDLEWARE_REPORT + y) != null) {
                            Activity activity = new Activity();
                            activity.setArchiveNumber(map1.get(Constants.ARCHIVE_NUMBER_MIDDLEWARE_REPORT));
                            activity.setSourceNumber(map1.get(Constants.FEE_CREDIT_SOURCE_NUMBER_MIDDLEWARE_REPORT + y));
                            activity.setSourceName(Constants.STRIP_CODE);
                            activity.setCurrency(map1.get(Constants.CURRENCY_MIDDLEWARE_REPORT));
                            activity.setDescription(map1.get(Constants.FEE_CREDIT_TRANSACTION_MESSAGE_MIDDLEWARE_REPORT + y));
                            activity.setTransactionCode(map1.get(Constants.FEE_CREDIT_TRANSACTION_CODE_MIDDLEWARE_REPORT + y));
                            activity.setDebit(BigDecimal.ZERO);
                            activity.setCredit(new BigDecimal(map1.get(Constants.FEE_CREDIT_NOMINAL_MIDDLEWARE_REPORT + y)));
                            activities.add(activity);
                        }
                        if (StringUtils.equals(String.valueOf(map1.get(Constants.MTI_MIDDLEWARE_REPORT)), Constants.NORMAL_MIDDLEWARE_REPORT)) {
                            credit = credit.add(new BigDecimal(map1.get(Constants.CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                            credit = credit.add(new BigDecimal(map1.get(Constants.FEE_CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.FEE_CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                            debit = debit.add(new BigDecimal(map1.get(Constants.DEBIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.DEBIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                            debit = debit.add(new BigDecimal(map1.get(Constants.FEE_DEBIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.FEE_DEBIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                        } else {
                            credit = credit.subtract(new BigDecimal(map1.get(Constants.CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                            credit = credit.subtract(new BigDecimal(map1.get(Constants.FEE_CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.FEE_CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                            debit = debit.subtract(new BigDecimal(map1.get(Constants.DEBIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.DEBIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                            debit = debit.subtract(new BigDecimal(map1.get(Constants.FEE_DEBIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.FEE_DEBIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                        }
                    }
                }
            }
            allActivity.setDebit(debit);
            allActivity.setCredit(credit);
            FeatureContextHolder.getContext().setAllActivity(allActivity);
            return activities;
        } catch (Exception ex) {
            logger.error("Exception happen when contacting authentication server", ex);
            throw new UsernameNotFoundException("Exception happen when contacting authentication server", ex);
        }
    }

    public void getSomeTellerCreditResponse(String user, AllActivity allActivity) {
        try {
            String urls = url + Constants.MIDDLEWARE_REPORT;
            BigDecimal debitOverbooking = BigDecimal.ZERO;
            BigDecimal creditOverbooking = BigDecimal.ZERO;
            BigDecimal debitCash = BigDecimal.ZERO;
            BigDecimal creditCash = BigDecimal.ZERO;
            List<Map<String, String>> t1 = new ArrayList<>();
            List<Activity> activities = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> map = new HashMap<>();
            HttpEntity<Map<String, String>> entity = new HttpEntity<Map<String, String>>(map, headers);
            ResponseEntity<String> response = restTemplate.exchange(buildJsonSomeTellerRequestBody(urls, user), HttpMethod.GET, entity, String.class);
            JsonFactory jsonFactory = new JsonFactory();
            ObjectMapper objectMapper = new ObjectMapper(jsonFactory);
            jsonFactory.setCodec(objectMapper);
            JsonParser jsonParser = jsonFactory.createParser(response.getBody());
            ObjectCodec objectCodec = jsonParser.getCodec();
            Map<String, Object> stringMap = objectCodec.readValue(jsonParser, Map.class);
            if (StringUtils.equals(Constants.SUCCESS_CODE, stringMap.get(Constants.STATE_MIDDLEWARE_REPORT).toString())) {
                List<Map<String, Object>> maps = (List<Map<String, Object>>) stringMap.get(Constants.RESULT_MIDDLEWARE_REPORT);
                for (Map<String, Object> entry : maps) {
                    Map<String, String> t2 = new HashMap<>();
                    for (Map.Entry<String, Object> entry1 : entry.entrySet()) {
                        t2.put(entry1.getKey(), entry1.getValue().toString());
                        if (StringUtils.equals(entry1.getKey(), Constants.KEY_MIDDLEWARE_REPORT)) {
                            Map<String, String> objectMap = (Map<String, String>) entry1.getValue();
                            t2.putAll(objectMap);
                        }
                    }
                    t1.add(t2);
                }
                for (Map<String, String> map1 : t1) {
                    for (Integer i = 1; i <= 6; i++) {
                        String y = Integer.toString(i);
                        if (StringUtils.equals(String.valueOf(map1.get(Constants.TRANSACTION_TYPE_MIDDLEWARE_REPORT)), Constants.CASH_MIDDLEWARE_REPORT)) {
                            if (StringUtils.equals(String.valueOf(map1.get(Constants.MTI_MIDDLEWARE_REPORT)), Constants.NORMAL_MIDDLEWARE_REPORT)) {
                                creditCash = creditCash.add(new BigDecimal(map1.get(Constants.CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                                creditCash = creditCash.add(new BigDecimal(map1.get(Constants.FEE_CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.FEE_CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                            } else {
                                creditCash = creditCash.subtract(new BigDecimal(map1.get(Constants.CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                                creditCash = creditCash.subtract(new BigDecimal(map1.get(Constants.FEE_CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.FEE_CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                            }
                        }
                    }
                }
                allActivity.setDebitOverbooking(debitOverbooking);
                allActivity.setDebitCash(debitCash);
                allActivity.setCreditOverbooking(creditOverbooking);
                allActivity.setCreditCash(creditCash);
            }
        } catch (Exception ex) {
            logger.error("Exception happen when contacting authentication server", ex);
            throw new UsernameNotFoundException("Exception happen when contacting authentication server", ex);
        }
    }

    public void getTotalSomeTellerDebitCreditResponse(String user, AllActivity allActivity) {
        try {
            String urls = url + Constants.MIDDLEWARE_REPORT;
            BigDecimal debitOverbooking = BigDecimal.ZERO;
            BigDecimal creditOverbooking = BigDecimal.ZERO;
            BigDecimal debitCash = BigDecimal.ZERO;
            BigDecimal creditCash = BigDecimal.ZERO;
            List<Map<String, String>> t1 = new ArrayList<>();
            List<Activity> activities = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> map = new HashMap<>();
            HttpEntity<Map<String, String>> entity = new HttpEntity<Map<String, String>>(map, headers);
            ResponseEntity<String> response = restTemplate.exchange(buildJsonSomeTellerRequestBody(urls, user), HttpMethod.GET, entity, String.class);
            JsonFactory jsonFactory = new JsonFactory();
            ObjectMapper objectMapper = new ObjectMapper(jsonFactory);
            jsonFactory.setCodec(objectMapper);
            JsonParser jsonParser = jsonFactory.createParser(response.getBody());
            ObjectCodec objectCodec = jsonParser.getCodec();
            Map<String, Object> stringMap = objectCodec.readValue(jsonParser, Map.class);
            if (StringUtils.equals(Constants.SUCCESS_CODE, stringMap.get("rCode").toString())) {
                List<Map<String, Object>> maps = (List<Map<String, Object>>) stringMap.get("result");
                for (Map<String, Object> entry : maps) {
                    Map<String, String> t2 = new HashMap<>();
                    for (Map.Entry<String, Object> entry1 : entry.entrySet())
                        t2.put(entry1.getKey(), entry1.getValue().toString());
                    t1.add(t2);
                }
                for (Map<String, String> map1 : t1) {
                    for (Integer i = 1; i <= 6; i++) {
                        String y = Integer.toString(i);
                        if (StringUtils.equals(map1.get(Constants.TRANSACTION_TYPE_MIDDLEWARE_REPORT), Constants.OVERBOOKING_MIDDLEWARE_REPORT)) {
                            debitOverbooking = debitOverbooking.add(new BigDecimal(map1.get(Constants.DEBIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.DEBIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                            debitOverbooking = debitOverbooking.add(new BigDecimal(map1.get(Constants.FEE_DEBIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.FEE_DEBIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                            creditOverbooking = creditOverbooking.add(new BigDecimal(map1.get(Constants.CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                            creditOverbooking = creditOverbooking.add(new BigDecimal(map1.get(Constants.FEE_CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.FEE_CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                        } else {
                            debitCash = debitCash.add(new BigDecimal(map1.get(Constants.DEBIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.DEBIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                            debitCash = debitCash.add(new BigDecimal(map1.get(Constants.FEE_DEBIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.FEE_DEBIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                            creditCash = creditCash.add(new BigDecimal(map1.get(Constants.CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                            creditCash = creditCash.add(new BigDecimal(map1.get(Constants.FEE_CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) != null ? map1.get(Constants.FEE_CREDIT_NOMINAL_MIDDLEWARE_REPORT + y) : Constants.ZERO));
                        }
                    }
                }
                allActivity.setDebitOverbooking(debitOverbooking);
                allActivity.setDebitCash(debitCash);
                allActivity.setCreditOverbooking(creditOverbooking);
                allActivity.setCreditCash(creditCash);
            }
        } catch (Exception ex) {
            logger.error("Exception happen when contacting authentication server", ex);
            throw new UsernameNotFoundException("Exception happen when contacting authentication server", ex);
        }
    }

    private String buildJsonDetailRequestBody(String s, String card, String id) throws IOException {
        Rule rule = getRule();
        return UriComponentsBuilder.fromUriString(s).queryParam("user", user).queryParam("token", token).queryParam("kodetrx", rule.getRouting()).queryParam("cardno", card).queryParam("idbil", id).build().toUriString();
    }
    private String buildJsonDetailRequestBodySipd(String s, String id) throws IOException {
        Rule rule = getRule();
        System.out.println("JSONNNNN"+UriComponentsBuilder.fromUriString(s).buildAndExpand(id).toUriString());
        return UriComponentsBuilder.fromUriString(s).buildAndExpand(id).toUriString();
    }



    public ReversalResponse getReversalResponse(String id) throws AuthenticationServiceException {
        try {
            String urls = url + Constants.MIDDLEWARE_REVERSAL;
            Map<String, String> stringMap = buildReversalRequestBody(id);
            urls = buildJsonReversalRequestBody(urls, stringMap);
            ResponseEntity<ReversalResponse> responseEntity = restTemplate.exchange(urls, HttpMethod.POST, null, ReversalResponse.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            logger.error("Exception happen when contacting authentication server", e);
            throw new UsernameNotFoundException("Exception happen when contacting authentication server", e);
        }
    }

    public ValidationResponse getValidationResponse(String id) throws AuthenticationServiceException {
        try {
            String urls = url + Constants.MIDDLEWARE_REVERSAL_STATUS;
            Map<String, String> stringMap = buildValidationRequestBody(id);
            urls = buildJsonReversalRequestBody(urls, stringMap);
            ResponseEntity<ValidationResponse> responseEntity = restTemplate.exchange(urls, HttpMethod.POST, null, ValidationResponse.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            logger.error("Exception happen when contacting authentication server", e);
            throw new UsernameNotFoundException("Exception happen when contacting authentication server", e);
        }
    }

    public ValidationResponse getStatusResponse(CommonTransactionLog commonTransactionLog, String trace, String ref) throws BusinessException {
        try {
            String urls = url + Constants.MIDDLEWARE_TRANSACTION_STATUS;
            Map<String, String> stringMap = buildTransactionRequestBody(commonTransactionLog, trace, ref);
            urls = buildJsonReversalRequestBody(urls, stringMap);
            ResponseEntity<ValidationResponse> responseEntity = restTemplate.exchange(urls, HttpMethod.POST, null, ValidationResponse.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            logger.error("Exception happen when contacting middleware server", e);
            throw new BusinessException("Exception happen when contacting middleware server", e);
        }
    }

    private Map<String, String> buildReversalRequestBody(String id) throws Exception {
        Map<String, String> stringMap = new HashMap<>();
        CommonTransactionRequestLog commonTransactionRequestLog = commonTransactionRequestLogRepository.findOne(Long.parseLong(id));
        commonTransactionRequestLog.setTransactionRequestLogs(transactionRequestLogRepository.findByCommonTransactionRequestId(commonTransactionRequestLog.getId()));
        stringMap.put("termid", commonTransactionRequestLog.getBranchCode() + commonTransactionRequestLog.getUserid());
        stringMap.put("trxDate", DATE.format(commonTransactionRequestLog.getTrxDate()));
        stringMap.put("user", user);
        stringMap.put("token", token);
        stringMap.put("trace", commonTransactionRequestLog.getTransactionRequestLogs().get(0).getTraceNumber());
        for (TransactionRequestLog transactionRequestLog : commonTransactionRequestLog.getTransactionRequestLogs())
            if (MAP_ID.get(transactionRequestLog.getFieldName()) != null)
                stringMap.put(MAP_ID.get(transactionRequestLog.getFieldName()), transactionRequestLog.getFieldValue());
        stringMap.put("refnum", StringUtils.leftPad(stringMap.get("refnum"), 12, Constants.ZERO));
        return stringMap;
    }

    private Map<String, String> buildValidationRequestBody(String id) throws Exception {
        Map<String, String> stringMap = new HashMap<>();
        CommonTransactionRequestLog commonTransactionRequestLog = commonTransactionRequestLogRepository.findOne(Long.parseLong(id));
        commonTransactionRequestLog.setTransactionRequestLogs(transactionRequestLogRepository.findByCommonTransactionRequestId(commonTransactionRequestLog.getId()));
        stringMap.put("termid", commonTransactionRequestLog.getBranchCode() + commonTransactionRequestLog.getUserid());
        stringMap.put("trxDate", DATE.format(commonTransactionRequestLog.getTrxDate()));
        stringMap.put("user", user);
        stringMap.put("token", token);
        stringMap.put("trace", commonTransactionRequestLog.getTransactionRequestLogs().get(0).getTraceNumber());
        for (TransactionRequestLog transactionRequestLog : commonTransactionRequestLog.getTransactionRequestLogs())
            if (MAP_ID.get(transactionRequestLog.getFieldName()) != null)
                stringMap.put(MAP_ID.get(transactionRequestLog.getFieldName()), transactionRequestLog.getFieldValue());
        stringMap.put("refnum", StringUtils.leftPad(stringMap.get("refnum"), 12, Constants.ZERO));
        return stringMap;
    }

    private Map<String, String> buildTransactionRequestBody(CommonTransactionLog commonTransactionLog, String trace, String ref) throws Exception {
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("termid", commonTransactionLog.getBranchCode() + commonTransactionLog.getUserid());
        stringMap.put("trxDate", DATE.format(commonTransactionLog.getTrxDate()));
        stringMap.put("user", user);
        stringMap.put("token", token);
        stringMap.put("trace", trace);
        stringMap.put("refnum", StringUtils.leftPad(ref, 12, Constants.ZERO));
        return stringMap;
    }

    private String buildJsonSomeTellerRequestBody(String s, String user) throws IOException {
        return UriComponentsBuilder.fromUriString(s).queryParam("user", user).queryParam("token", token).queryParam("trxdate", StringUtils.upperCase(DATE.format(new Date()))).build().toUriString();
    }

    private String buildJsonReversalRequestBody(String s, Map<String, String> map) throws IOException {
        return UriComponentsBuilder.fromUriString(s).queryParam("user", user).queryParam("token", token).queryParam("trxDate", map.get("trxDate")).queryParam("termid", map.get("termid")).queryParam("refnum", map.get("refnum")).queryParam("accSrc", map.get("accSrc")).queryParam("amount", map.get("amount")).queryParam("trace", map.get("trace")).build().toUriString();
    }
}
