package com.multipolar.uiteller.backend.service;

import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.iso.MessageGateway;
import com.multipolar.uiteller.backend.service.logger.RequestLogService;
import com.multipolar.uiteller.backend.service.logger.TransactionLogService;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service
public class CTASManualReinquiry {
    @Autowired
    private MessageGateway gateway;

    @Autowired
    private TransactionLogService logService;

    @Autowired
    private RequestLogService requestLogService;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMddHHmmss");
    private static final SimpleDateFormat LOCAL_DATE = new SimpleDateFormat("MMdd");
    private static final SimpleDateFormat LOCAL_TIME = new SimpleDateFormat("HHmmss");
    private static final String REINQUIRY_ROUTING = "03027";

    private final String defaultMTI = "0200";

    protected  void setTransmissionDateTime(ISOMsg msg, Date date) throws ISOException {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZONE_GMT));
        msg.set(7, DATE_FORMAT.format(date));
    }

    public ISOMsg sendManualReinquiry(ISOMsg request) throws ISOException, IOException {
        ISOMsg reinquiry = (ISOMsg) request.clone();
        setTransmissionDateTime(reinquiry, new Date());
        Date date = new Date();
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZONE_GMT));

        String context = FeatureContextHolder.getContext().getFeatureName();
        requestLogService.request(context, reinquiry);

        reinquiry.setMTI(defaultMTI);
        reinquiry.set(7, DATE_FORMAT.format(date));
        reinquiry.set(12, LOCAL_TIME.format(date));
        reinquiry.set(13, LOCAL_DATE.format(date));
        reinquiry.set(63, REINQUIRY_ROUTING);

        String accountBit = StringUtils.substring(reinquiry.getString(3), 2, 4);
        String statusPcode = "38" + accountBit + "00";
        reinquiry.set(3, statusPcode);

        return reinquiry;
    }
}
