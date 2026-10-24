package com.multipolar.uiteller.backend.service.iso;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.multipolar.uiteller.backend.model.converter.ConverterRule;
import com.multipolar.uiteller.backend.model.converter.RequestRule;
import com.multipolar.uiteller.backend.model.converter.Rule;
import com.multipolar.uiteller.backend.repository.ResourceMessageRepository;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class BpjsKerjaBpuConverterHandler extends DefaultConverterHandler{

    private static final String SUPPORTED_URL_BPU_STATUS = "bpjsKerjaBpuStatusInquiry";
    private static final String SUPPORTED_URL_BPU_PERIODE = "bpjsKerjaBpuPeriodeInquiry";
    private static final String SUPPORTED_URL_BPU = "bpjsKerjaBpu";

    private static final SimpleDateFormat DATE = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
    private static final SimpleDateFormat TIME = new SimpleDateFormat("kk:mm:ss");
    private static final SimpleDateFormat TIME_SPACE = new SimpleDateFormat("kkmmss");

    private static final String RULE_EXTENSION = ".rule.json";
    private static Logger logger = LoggerFactory.getLogger(DefaultConverterHandler.class);
    @Autowired
    private ResourceMessageRepository messageRepository;
    @Autowired
    private CustomerIdService customerIdService;
    @Override
    public boolean supports(String s) {
        return StringUtils.equals(SUPPORTED_URL_BPU, s) || StringUtils.equals(SUPPORTED_URL_BPU_STATUS, s) || StringUtils.equals(SUPPORTED_URL_BPU_PERIODE, s);
    }
    @Override
    protected Map<String, Object> translatePrivateMessage(ISOMsg msg, boolean fl) throws ISOException {
        Map<String, Object> map = new HashMap<>();
        try {
            Rule rule = getRule();

            String errorKey = rule.getErrorKey();
            if (exception(msg, map, errorKey))
                return map;

            RequestRule[] responseRules = rule.getResponse();
            for (RequestRule responseRule : responseRules) {
                int bit = responseRule.getBit();
                String valueFromBit = msg.getString(bit);
                if (valueFromBit == null) {
                    logger.error("Value get from bit is null, bit = " + bit);
                    throw new ISOException("The rule said get value from bit but empty bit is found. bit " + bit);
                }

                ConverterRule[] values = responseRule.getValue();
                setValueForMap(map, valueFromBit, values);
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return generalFail();
        } catch (JsonMappingException jme) {
            logger.error("Can't parsing rule file, please validate the json format");
            jme.printStackTrace();
            return generalFail();
        } catch (IOException iox) {
            logger.error(iox.getMessage());
            iox.printStackTrace();
            return generalFail();
        }
        if (fl) {
            // we need put source account number in map also
            String sourceName = FeatureContextHolder.getContext().getSourceName();
            UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication()
                    .getDetails();
            String bpjsBranch = msg.getString(61).substring(165,215);
            map.put("sourceName", sourceName);
            map.put("userId", userDetail.getUsername());
            map.put("branchCode", userDetail.getBranchCode());
            map.put("branchName", bpjsBranch);
            map.put("bankBranch", userDetail.getBranchName());
            if (msg.getString(76) == null) {
                map.put("archiveNumber", userDetail.getUsername() + FeatureContextHolder.getContext().getTraceNumber());
            }
            map.put("transactionDate", DATE.format(FeatureContextHolder.getContext().getFeatureTransactionDate()));

            String destName = FeatureContextHolder.getContext().getDestinationName();
            if (!StringUtils.isEmpty(destName))
                map.put("destinationName", destName);
        } else {
            map.put("cardIdAtm", msg.getString(2));
            map.put("transactionDateAtm", msg.getString(13));
            try {
                map.put("transactionTimeAtm", TIME.format(TIME_SPACE.parse(msg.getString(12))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            map.put("traceNumberAtm", msg.getString(37));
            map.put("atmCodeAtm", msg.getString(41));
            map.put("atmNameAtm", msg.getString(43));
            map.put("customerIdAtm", map.get(customerIdService.customerId(FeatureContextHolder.getContext().getFeatureName())));
        }
        if(!map.containsKey("status")){
            Map<String, String> statusMap = new HashMap<String, String>();
            statusMap.put("status", "00");
            statusMap.put("code", "00");
            statusMap.put("message", "Request Success");
            map.put("status", statusMap);
        }
        return map;
    }

}
