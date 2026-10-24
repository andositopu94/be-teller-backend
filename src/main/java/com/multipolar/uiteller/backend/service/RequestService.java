package com.multipolar.uiteller.backend.service;

import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service
public class RequestService {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMddHHmmss");
    private static final SimpleDateFormat LOCAL_DATE = new SimpleDateFormat("MMdd");
    private static final SimpleDateFormat LOCAL_TIME = new SimpleDateFormat("HHmmss");
    private static final String defaultAcquirer = "117";
    private static final String uniqueId = "627486";
    private static final String INITIAL_PCODE = "320000";

    @Autowired
    private TraceNumberGenerator stan;

    public ISOMsg generateNetwork(String s) throws ISOException {
        Date date = new Date();
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZONE_MST));
        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setMTI(Constants.MTI_NETWORK);
        isoMsg.set(7, DATE_FORMAT.format(date));
        isoMsg.set(11, stan.getTraceNumber());
        isoMsg.set(32, defaultAcquirer);
        isoMsg.set(70, s);
        return isoMsg;
    }

    public ISOMsg generateDebit(ISOMsg msg) throws ISOException {
        Date date = new Date();
        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String userid = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        String branchId = userDetail.getBranchCode();
        String location = userDetail.getBranchName();
        String pors = StringUtils.equals(msg.getString(3), INITIAL_PCODE) ? StringUtils.leftPad(Constants.ZERO, 10) : Constants.BLANK;
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZONE_GMT));
        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setMTI(Constants.MTI_TRANSACTION);
        isoMsg.set(2, StringUtils.rightPad(uniqueId, 16, Constants.ZERO));
        isoMsg.set(3, Constants.CODE_PAYMENT);
        isoMsg.set(4, StringUtils.leftPad(StringUtils.substring(msg.getString(4), 1, msg.getString(4).length() - 2), 12, Constants.ZERO));
        isoMsg.set(7, DATE_FORMAT.format(date));
        isoMsg.set(11, msg.getString(11));
        isoMsg.set(12, LOCAL_TIME.format(date));
        isoMsg.set(13, LOCAL_DATE.format(date));
        isoMsg.set(15, LOCAL_DATE.format(date));
        isoMsg.set(18, Constants.MERCHANT_TYPE);
        isoMsg.set(32, defaultAcquirer);
        isoMsg.set(37, msg.getString(37));
        isoMsg.set(41, StringUtils.leftPad(branchId + userid, 8));
        isoMsg.set(43, StringUtils.rightPad(location, 40));
        isoMsg.set(49, Constants.IDR_CODE);
        isoMsg.set(57, Constants.OVERBOOKING_PAY_TYPE);
        isoMsg.set(61, StringUtils.leftPad(Constants.BLANK, 13, "0") + StringUtils.rightPad(Constants.BLANK, 14, Constants.SPACE) + pors + msg.getString(61));
        isoMsg.set(63, Constants.SISKO_PRIVATE);
        isoMsg.set(102, msg.getString(2));
        return isoMsg;
    }
}
