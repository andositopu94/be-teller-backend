package com.multipolar.uiteller.backend.service.iso;

import com.multipolar.uiteller.backend.model.ResourceMessage;
import com.multipolar.uiteller.backend.repository.ResourceMessageRepository;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.MpnSettlementDateUtil;
import com.multipolar.uiteller.backend.service.TraceNumberGenerator;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class MpnReinquiryConverterHandler extends DefaultConverterHandler {

    private static final String DJP_REINQUIRY = "djpReinquiry";
    private static final String DJPG3_REINQUIRY = "djpg3Reinquiry";
    private static final String DJBC_REINQUIRY = "djbcReinquiry";
    private static final String DJBCG3_REINQUIRY = "djbcg3Reinquiry";
    private static final String DJA_REINQUIRY = "djaReinquiry";
    private static final String DJAG3_REINQUIRY = "djag3Reinquiry";

    private static final SimpleDateFormat SDATE = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat TRX_DATE = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @Autowired
    private TraceNumberGenerator stanGenerator;
    @Autowired
    private ResourceMessageRepository messageRepository;

    @Autowired
    private MpnSettlementDateUtil mpnUtil;

    @Override
    public boolean supports(String s) {
        return StringUtils.equals(DJP_REINQUIRY, s) ||
                StringUtils.equals(DJPG3_REINQUIRY, s) ||
                StringUtils.equals(DJBC_REINQUIRY, s) ||
                StringUtils.equals(DJBCG3_REINQUIRY, s) ||
                StringUtils.equals(DJA_REINQUIRY, s) ||
                StringUtils.equals(DJAG3_REINQUIRY, s);
    }

    public ISOMsg doConvertToISO(Map<String, Object> map) throws InvalidMessageException {
        ISOMsg msg = super.doConvertToISO(map);
        FeatureContextHolder.getContext().setReportData(map);

        try {
            String settlementDate = map.get("settlementDate") != null ? map.get("settlementDate").toString() : "";
            String trxDate = map.get("transactionDate") != null ? map.get("transactionDate").toString() : "";
            FeatureContextHolder.getContext().setSettlementDate(SDATE.parse(settlementDate));
            FeatureContextHolder.getContext().setFeatureTransactionDate(TRX_DATE.parse(trxDate));
            // set bit 11 sama dengan trx sebelumnya
//            String traceNumber =  stanGenerator.getTraceNumber();
            String traceNumber = (String) map.get("traceNumber");
            if (StringUtils.isEmpty(traceNumber))
                traceNumber = stanGenerator.getTraceNumber();

            FeatureContextHolder.getContext().setTraceNumber(traceNumber);
            String refNum = (String) map.get("refNum");
            if (!StringUtils.isEmpty(refNum))
                msg.set(37, refNum);

            msg.set(11, traceNumber);
            if(map.get("payType").equals("CASH")) {
                setMerchantType(msg, Constants.MERCHANT_TYPE);
            } else {
                setMerchantType(msg, Constants.OVERBOOKING_MERCHANT_TYPE);
            }
            return msg;
        } catch (ParseException ex) {
            throw new InvalidMessageException("Gagal melakukan parsing settlement date");
        } catch (ISOException ex) {
            throw new InvalidMessageException("Gagal melakukan setting STAN");
        }
    }

    @Override
    public Map<String, Object> doConvertToJSon(ISOMsg msg, boolean fl) throws InvalidMessageException {
        Map<String, Object> map = super.doConvertToJSon(msg, true);

        Map<String, Object> queryData = FeatureContextHolder.getContext().getReportData();
        String trxDate = (String) queryData.get("transactionDate");
        if (!StringUtils.isEmpty(trxDate))
            map.put("transactionDate", trxDate);

        if(map.containsKey("transactionTime")) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
            String transactionTime = map.get("transactionTime").toString();
            LocalTime localTime = DateTimeFormat.forPattern("HHmmss").parseLocalTime(transactionTime);
            map.remove("transactionDate");
            map.put("transactionDate",trxDate.substring(0,11)+localTime.toString().substring(0,8));
        }
        String settlementDate = mpnUtil.getSettlementDateFromResponse(msg);
        if (StringUtils.isNotEmpty(settlementDate))
        {
            map.put("settlementDate", settlementDate);
        }
        else {
            settlementDate = (String) queryData.get("settlementDate");
            if (!StringUtils.isEmpty(settlementDate))
                map.put("settlementDate", settlementDate);
            else {
                Date bookDate = FeatureContextHolder.getContext().getSettlementDate();
                map.put("settlementDate", SDATE.format(bookDate));
            }
        }
        String userid = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        String traceNumber = msg.getString(11);
        map.put("archiveNumber", userid + traceNumber);

        return map;
    }

    protected boolean exception(ISOMsg msg, Map<String, Object> map, String errorKey) {
        String resultCode = msg.getString(39);
        log.debug("resultCode: " + resultCode);
        if (StringUtils.equals(resultCode, "00"))
            return false;

        ResourceMessage message = messageRepository.findByCodeAndMessage(errorKey, resultCode);
        String messageDesc = message == null ? "Unmapping Error Number (" + resultCode + ")" : message.getValue();
        Map<String, String> statusMap = new HashMap<String, String>();
        statusMap.put("status", "01");
        statusMap.put("code", resultCode);
        statusMap.put("message", messageDesc);
        statusMap.put("stan", FeatureContextHolder.getContext().getTraceNumber());
        map.put("status", statusMap);
        log.debug("map: " + map);

        String feature = FeatureContextHolder.getContext().getFeatureName();
        log.debug("feature: " + feature);

        if (StringUtils.equalsAny(resultCode, "08")
                && (StringUtils.equals(feature, DJA_REINQUIRY)
                || StringUtils.equals(feature, DJBC_REINQUIRY)
                || StringUtils.equals(feature, DJP_REINQUIRY)
                || StringUtils.equals(feature, DJPG3_REINQUIRY)
                || StringUtils.equals(feature, DJBCG3_REINQUIRY)
                || StringUtils.equals(feature, DJAG3_REINQUIRY)))
            return false;

        return true;
    }
}
