package com.multipolar.uiteller.backend.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.multipolar.uiteller.backend.model.Holiday;
import com.multipolar.uiteller.backend.model.MpnCutOff;
import com.multipolar.uiteller.backend.repository.HolidayRepository;
import com.multipolar.uiteller.backend.repository.MpnCutOffRepository;

@Service
public class MpnSettlementDateUtil {
    @Value("${mpn.settlement.hour}")
    private Integer mpnSettlementHour;

    @Value("${mpn.settlement.minute}")
    private Integer mpnSettlementMinute;

    @Value("${mpn.settlement.second}")
    private Integer mpnSettlementSecond;

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private MpnCutOffRepository mpnCutOffRepository;

    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("MMdd");
    private static final SimpleDateFormat DB_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public Date getSettlementDate(Date date) {
        // cek trx hari biasa, hari libur atau tanggal cut off
        MpnCutOff cutOff = isOnCutOffPeriod(date);

        if (cutOff != null) {
            return cutOff.getServeDate();
        }

        boolean isTrxOnWeekend = isWeekend(date);
        boolean isTrxOnHoliday = isHoliday(date);

        // jika transaction di hari libur atau hari kerja
        if (isTrxOnHoliday || isTrxOnWeekend) {
            Date nextWorkingDay = getNextWorkingDay(date);
            return nextWorkingDay;
        }

        // jika hari biasa, cek sudah lewat jam cut off
        boolean afterCutHour = afterCutOffHour(date);
        Date settlementDate = null;
        if (!afterCutHour) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            settlementDate = calendar.getTime();
        } else {
            settlementDate = getNextWorkingDay(date);
        }

        return settlementDate;
    }

    public boolean afterCutOffHour(Date trxDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trxDate);

        Calendar copy = (Calendar) calendar.clone();

        // cek sekarang tanggal 31 desember? karena tgl 31 des cutoff time adalah jam 23:59:59
//        if ((calendar.get(Calendar.DATE) == 31 && calendar.get(Calendar.MONTH) == 11)) {
//            copy.set(Calendar.HOUR_OF_DAY, 23);
//            copy.set(Calendar.MINUTE, 59);
//            copy.set(Calendar.SECOND, 59);
//        } else if ((calendar.get(Calendar.DATE) == 30 && calendar.get(Calendar.MONTH) == 11)) {
//            copy.set(Calendar.HOUR_OF_DAY, 21);
//            copy.set(Calendar.MINUTE, 0);
//            copy.set(Calendar.SECOND, 0);
//        } else if ((
//                (calendar.get(Calendar.DATE) == 23 ||
//                calendar.get(Calendar.DATE) == 24 ||
//                calendar.get(Calendar.DATE) == 25 ||
//                calendar.get(Calendar.DATE) == 26 ||
//                calendar.get(Calendar.DATE) == 27 ||
//                calendar.get(Calendar.DATE) == 28 ||
//                calendar.get(Calendar.DATE) == 29 || calendar.get(Calendar.DATE) == 30
//                || calendar.get(Calendar.DATE) == 31)
//                && calendar.get(Calendar.MONTH) == 12)) {
//            copy.set(Calendar.HOUR_OF_DAY, 16);
//            copy.set(Calendar.MINUTE, 0);
//            copy.set(Calendar.SECOND, 0);
//        } else {
//            copy.set(Calendar.HOUR_OF_DAY, 15);
//            copy.set(Calendar.MINUTE, 0);
//            copy.set(Calendar.SECOND, 0);
//        }
        copy.set(Calendar.HOUR_OF_DAY, mpnSettlementHour);
        copy.set(Calendar.MINUTE, mpnSettlementMinute);
        copy.set(Calendar.SECOND, mpnSettlementSecond);


        return calendar.after(copy);
    }

    public boolean isHoliday(Date trxDate) {
        List<Holiday> allHolidays = holidayRepository.findAll();
        for (Holiday holiday : allHolidays) {
            if (DateUtils.isSameDay(holiday.getDate(), trxDate))
                return true;
        }

        return false;
    }

    public MpnCutOff isOnCutOffPeriod(Date trxDate) {
        List<MpnCutOff> allCutOff = mpnCutOffRepository.findAll();
        for (MpnCutOff period : allCutOff) {
            if (trxDate.compareTo(period.getCutoffDate()) >= 0 && trxDate.compareTo(period.getServeDate()) < 0)
                return period;
        }

        return null;
    }

    public boolean isWeekend(Date trxDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trxDate);

        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    public Date getNextWorkingDay(Date trxDate) {
        Date nextDay = null;
        int add = 1;
        boolean isHoliday = true;
        boolean isWeekend = true;
        while (isHoliday || isWeekend) {
            nextDay = DateUtils.addDays(trxDate, add);
            isWeekend = isWeekend(nextDay);
            isHoliday = isHoliday(nextDay);
            add++;
        }

        return nextDay;
    }

    public Date getPreviousWorkingDay(Date trxDate) {
        Date prevDay = null;
        int minus = -1;
        boolean isHoliday = true;
        boolean isWeekend = true;
        while (isHoliday || isWeekend) {
            prevDay = DateUtils.addDays(trxDate, minus);
            isWeekend = isWeekend(prevDay);
            isHoliday = isHoliday(prevDay);
            minus--;
        }

        return prevDay;
    }

    public String getSettlementDateFromResponse(ISOMsg response) {
        Calendar current = Calendar.getInstance();
        int currentMonth = current.get(Calendar.MONTH);
        int currentYear = current.get(Calendar.YEAR);

        String bit15 = response.getString(15);
        int responseMonth = Integer.parseInt(StringUtils.substring(bit15, 0, 2));

        // cek sekarang bulan desember dan settlement date bulan januari, maka telah
        // berganti tahun
        if (currentMonth == 11 && responseMonth == 1)
            currentYear += 1;

        // cek response bit 39 == 00
        String bit39 = response.getString(39);

        try {
            Date isoDate = ISO_FORMAT.parse(bit15);
            Calendar cal = Calendar.getInstance();
            cal.setTime(isoDate);
            cal.set(Calendar.YEAR, currentYear);

            bit15 = DB_FORMAT.format(cal.getTime());
        } catch (ParseException ex) {
            ex.printStackTrace();
            return null;
        }

        if (StringUtils.equalsAny(bit39, "00", "90")) {
            return bit15;
        }

        return null;
    }
}
