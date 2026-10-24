package com.multipolar.uiteller.backend.service;

import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.iso.MessageGateway;
import com.multipolar.uiteller.backend.service.logger.RequestLogService;
import com.multipolar.uiteller.backend.service.logger.TransactionLogService;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
@Service
public class CTASReinquiryService {

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

    protected void setTransmissionDateTime(ISOMsg msg, Date date) throws ISOException {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZONE_GMT));
        msg.set(7, DATE_FORMAT.format(date));
    }

    public ISOMsg sendReinquiry(ISOMsg requestReinquiry) throws ISOException, IOException {
        ISOMsg reinquiryRequest = (ISOMsg) requestReinquiry.clone();
        setTransmissionDateTime(reinquiryRequest, new Date());
        Date date = new Date();
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZONE_GMT));

        String context = FeatureContextHolder.getContext().getFeatureName();
        requestLogService.request(context, reinquiryRequest);

        reinquiryRequest.setMTI(defaultMTI);
        reinquiryRequest.set(7, DATE_FORMAT.format(date));
        reinquiryRequest.set(12, LOCAL_TIME.format(date));
        reinquiryRequest.set(13, LOCAL_DATE.format(date));
        reinquiryRequest.set(63, REINQUIRY_ROUTING);

        // ambil dua digit ke 3 dan ke 4 untuk pcode
        String accountBit = StringUtils.substring(reinquiryRequest.getString(3), 2, 4);
        String checkStatusPcode = "38" + accountBit + "00";
        reinquiryRequest.set(3, checkStatusPcode);

        ISOMsg response = gateway.sendToHost(reinquiryRequest);

        logService.updateSuccessMpnReinquiry(reinquiryRequest, response);

        return response;
    }
}
