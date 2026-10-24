package com.multipolar.uiteller.backend.service.iso;


import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.service.TraceNumberGenerator;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public abstract class AbstractMessageConverter implements MessageConverterHandler {

    private static Logger logger = LoggerFactory.getLogger(AbstractMessageConverter.class);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMddHHmmss");
    private static final SimpleDateFormat LOCAL_DATE = new SimpleDateFormat("MMdd");
    private static final SimpleDateFormat LOCAL_TIME = new SimpleDateFormat("HHmmss");
    private String defaultMTI = "0200";
    private String defaultBIN = "627486";
    private String defaultAcquirer = "117";

    @Autowired
    private TraceNumberGenerator stan;

    public Map<String, Object> doConvertToJSon(ISOMsg msg, boolean fl) throws InvalidMessageException {
        // log iso msg here

        // call spesific conversion
        try {
            return translatePrivateMessage(msg, fl);
        } catch (ISOException e) {
            throw new InvalidMessageException(e.getMessage());
        }
    }

    @Override
    public ISOMsg doConvertToISO(Map<String, Object> map) throws InvalidMessageException {
        Date date = new Date();
        FeatureContextHolder.getContext().setFeatureTransactionDate(date);
        try {
            ISOMsg msg = new ISOMsg();
            setMti(msg, defaultMTI);
            setPan(msg, defaultBIN);
            setTransmissionDateTime(msg, date);
            setTransmissionTime(msg, date);
            setTransmissionDate(msg, date);
            setSettlementDate(msg, date);
            setMerchantType(msg, Constants.MERCHANT_TYPE);
            setAcquirer(msg, defaultAcquirer);
            //setRefferenceNumber(msg, getStan());
            setTraceAuditNumber(msg);
            setTerminal(msg);
            setTerminalLocation(msg);
            setPrivateMessage(msg, map);
            setCheckNumber(msg);

            return msg;
        } catch (ISOException ex) {
            throw new InvalidMessageException(ex.getMessage());
        }
    }

    protected void setMti(ISOMsg msg, String mti) throws ISOException {
        try {
            msg.setMTI(mti);
        } catch (ISOException e) {
            logger.error("Failed to set MTI for ISO Message: " + mti);
            throw new ISOException(e);
        }
    }

    protected void setPan(ISOMsg msg, String bin) throws ISOException {
        msg.set(2, StringUtils.rightPad(bin, 16, '0'));
    }

    protected void setTransmissionDateTime(ISOMsg msg, Date date) throws ISOException {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZONE_GMT));
        msg.set(7, DATE_FORMAT.format(FeatureContextHolder.getContext().getFeatureTransactionDate()));
    }

    protected void setTraceAuditNumber(ISOMsg msg) throws ISOException {
        String s = stan.getTraceNumber();
        FeatureContextHolder.getContext().setTraceNumber(s);
        msg.set(11, s);
    }

    protected void setTransmissionTime(ISOMsg msg, Date date) throws ISOException {
        msg.set(12, LOCAL_TIME.format(date));
    }

    protected void setTransmissionDate(ISOMsg msg, Date date) throws ISOException {
        msg.set(13, LOCAL_DATE.format(date));
    }

    protected void setSettlementDate(ISOMsg msg, Date date) throws ISOException {
        msg.set(15, LOCAL_DATE.format(date));
    }

    protected void setMerchantType(ISOMsg msg, String merchant) throws ISOException {
        msg.set(18, merchant);
    }

    protected void setAcquirer(ISOMsg msg, String s) throws ISOException {
        msg.set(32, s);
    }

    protected void setRefferenceNumber(ISOMsg msg, String s) throws ISOException {
        msg.set(37, StringUtils.leftPad(s, 12, "0"));
    }

    private UserDetailImpl getUserDetails() {
        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return userDetail;
    }

    protected void setTerminal(ISOMsg msg) throws ISOException {
        UserDetailImpl details = getUserDetails();
        String branchId = details.getBranchCode();
        String userid = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        msg.set(41, StringUtils.leftPad(branchId + userid, 8));
    }

    protected void setTerminalLocation(ISOMsg msg) throws ISOException {
        UserDetailImpl userDetail = getUserDetails();
        String location = userDetail.getBranchName();
        msg.set(43, StringUtils.rightPad(location, 40));
    }

    protected void setCheckNumber(ISOMsg isoMsg) throws ISOException {
        String checkNumber = isoMsg.getString(58);
        if (checkNumber == null) {
            return;
        } else if (StringUtils.isBlank(checkNumber)) {
            isoMsg.unset(58);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder(isoMsg.getString(3));
        stringBuilder.setCharAt(2, '2');
        isoMsg.set(3, stringBuilder.toString());
    }

    protected void setDefaultMTI(String mti) {
        defaultMTI = mti;
    }

    protected void setDefaultBIN(String bin) {
        defaultBIN = bin;
    }

    protected abstract void setPrivateMessage(ISOMsg msg, Map<String, Object> map) throws ISOException;

    protected abstract Map<String, Object> translatePrivateMessage(ISOMsg msg, boolean fl) throws ISOException;

    protected String getStan() {
        return stan.getTraceNumber();
    }

    protected String getReferenceNumber() {
        return stan.getReferenceNumber();
    }

}
