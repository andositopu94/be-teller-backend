package com.multipolar.uiteller.backend.service.report;

import com.multipolar.uiteller.backend.resource.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ReportService {

    public String format(String dt, String fmsrc, String fmdst) {
        String s = null;
        try {
            Date date = new SimpleDateFormat(fmsrc).parse(dt);
            s = new SimpleDateFormat(fmdst).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public String dateFormat(Date date, String fmdst) {
        String s = null;
        try {
            s = new SimpleDateFormat(fmdst).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public Date format(String dt, String fmdst) {
        Date date = null;
        try {
            date = new SimpleDateFormat(fmdst).parse(dt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public String type(String s) {
        return StringUtils.equals(Constants.OVERBOOKING_MIDDLEWARE_REPORT, s) ? Constants.OVERBOOKING_PAY_TYPE : Constants.CASH_PAY_TYPE;
    }
}
