package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.CommonTransactionLog;
import com.multipolar.uiteller.backend.model.MpnCutOff;
import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.repository.TransactionLogRepository;
import com.multipolar.uiteller.backend.resource.BusinessException;
import com.multipolar.uiteller.backend.service.MpnSettlementDateUtil;
import com.multipolar.uiteller.backend.service.iso.MessageGateway;
import com.multipolar.uiteller.backend.service.logger.TransactionLogService;
import com.multipolar.uiteller.backend.service.logger.TransactionLogger;
import com.multipolar.uiteller.backend.service.logger.TransactionLoggerFactory;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.ConnectException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/apiv2")
public class MpnReinquiryController {

    @Autowired
    private TransactionLogRepository logRepository;

    @Autowired
    private MpnSettlementDateUtil mpnUtil;

    @Autowired
    private MessageGateway gateway;

    @Autowired
    private TransactionLoggerFactory loggerFactory;

    @Autowired
    private TransactionLogService logService;

    private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");

    private static final String UNSETTLEMENT = "unsettlement";

    // parameter dalam format dd/MM/yyyy
    @GetMapping("/mpnReinquiryList")
    public List<Map<String, String>> getTransaction(
            @RequestParam(name = "settlementDate", required = false) String settlementDate,
            @RequestParam(name = "type", required = false) String trxType) {
        List<Map<String, String>> results = new ArrayList<>();

        String rc = "08";
        if (StringUtils.equals(UNSETTLEMENT, trxType))
            rc = "90";

        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        List<TransactionLog> logs = logRepository.findTrxWithoutNtpn(settlementDate, username, rc);
        for (TransactionLog log : logs) {
            Map<String, String> map = new HashMap<>();

            CommonTransactionLog commonLog = log.getCommonTransaction();
            Long commonId = commonLog.getId();
            List<TransactionLog> trxLogs = logRepository.findByCommonTransactionId(commonId);

            map.put("branchCode", commonLog.getBranchCode());
            map.put("transactionDate", format.format(commonLog.getTrxDate()));
            map.put("status", commonLog.getStatus() == 0 ? "Berhasil" : "Gagal");
            map.put("tellerId", commonLog.getUserid());
            map.put("commonId", String.valueOf(commonId));

            for (TransactionLog tl : trxLogs) {
                map.put(tl.getFieldName(), tl.getFieldValue());
            }

            map.put("traceNumber", log.getTraceNumber());
            map.put("stan", log.getTraceNumber());

            results.add(map);
        }

        if (results.size() == 0)
            return results;

        // cek sudah di reinquiry?
        List<String> billingCodes = new ArrayList<>();
        for (Map<String, String> map : results) {
            billingCodes.add(map.get("billingCode"));
        }

        List<TransactionLog> successList = logRepository.findSuccessfulMpnReinquiry(billingCodes);
        for (Map<String, String> map : results) {
            String billingCode = map.get("billingCode");
            if (checkExists(successList, billingCode))
                map.put("reinquiry", "1");
        }

        return results;
    }

    // parameter dalam format dd/MM/yyyy
    @GetMapping("/mpng3ReinquiryList")
    public List<Map<String, String>> getTransactionMpnG3(
            @RequestParam(name = "settlementDate", required = false) String settlementDate) throws ParseException {
        List<Map<String, String>> results = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        Date date=new SimpleDateFormat("dd/mm/yyyy").parse(settlementDate);
        Date minusOne = new Date(date.getTime() - (1000 * 60 * 60 * 24));
        Date minusTwo = new Date(date.getTime() - (1000 * 60 * 60 * 48));
        String strMinusOne = dateFormat.format(minusOne);
        String strMinusTwo = dateFormat.format(minusTwo);
        System.out.println("MINUS DATE " + strMinusOne + strMinusTwo);

        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

//        List<TransactionLog> logs = logRepository.findTrxMpnG3WithoutNtpn(settlementDate, username);
        //remove cutt off time
       // List<TransactionLog> logs = logRepository.findAllTrxMpnG3WithoutNtpn(settlementDate, strMinusOne, strMinusTwo,username);
        //req 5 Juni 2024, list yang ditampilkan hanya yang sesuai dengan settdate saat ini
        List<TransactionLog> logs = logRepository.findAllTrxMpnG3WithoutNtpnByDate(username, settlementDate);
        System.out.println("GET ALL TRX WHITOUT NTPN");
        for (TransactionLog log : logs) {
            Map<String, String> map = new HashMap<>();

            CommonTransactionLog commonLog = log.getCommonTransaction();
            Long commonId = commonLog.getId();
            System.out.println("COMMON ID "+ commonId);
            List<TransactionLog> trxLogs = logRepository.findByCommonTransactionId(commonId);

            map.put("branchCode", commonLog.getBranchCode());
            map.put("transactionDate", format.format(commonLog.getTrxDate()));
            map.put("status", commonLog.getStatus() == 0 ? "Berhasil" : "Gagal");
            map.put("tellerId", commonLog.getUserid());
            map.put("commonId", String.valueOf(commonId));

            for (TransactionLog tl : trxLogs) {
                map.put(tl.getFieldName(), tl.getFieldValue());
            }

            map.put("traceNumber", log.getTraceNumber());
            map.put("stan", log.getTraceNumber());

            results.add(map);
        }
        System.out.println("ADD TRX TO MAP");

        if (results.size() == 0)
            return results;

        // cek sudah di reinquiry?
        List<String> billingCodes = new ArrayList<>();
        for (Map<String, String> map : results) {
            billingCodes.add(map.get("billingCode"));
        }

        List<TransactionLog> successList = logRepository.findSuccessfulMpnReinquiry(billingCodes);
        for (Map<String, String> map : results) {
            String billingCode = map.get("billingCode");
            if (checkExists(successList, billingCode))
                map.put("reinquiry", "1");
        }

        return results;
    }
    @GetMapping("/mpng3ReinquiryListByDate")
    public List<Map<String, String>> getTransactionMpnG3byIdBilling(
            @RequestParam(name = "settlementDate", required = false) String settlementDate) throws ParseException {
        List<Map<String, String>> results = new ArrayList<>();

        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

//        List<TransactionLog> logs = logRepository.findTrxMpnG3WithoutNtpn(settlementDate, username);

        //remove cutt off time
        List<TransactionLog> logs = logRepository.findAllTrxMpnG3WithoutNtpnByDate(username, settlementDate);
        System.out.println("GET ALL TRX WHITOUT NTPN");
        for (TransactionLog log : logs) {
            Map<String, String> map = new HashMap<>();

            CommonTransactionLog commonLog = log.getCommonTransaction();
            Long commonId = commonLog.getId();
            System.out.println("COMMON ID "+commonId);
            List<TransactionLog> trxLogs = logRepository.findByCommonTransactionId(commonId);

            map.put("branchCode", commonLog.getBranchCode());
            map.put("transactionDate", format.format(commonLog.getTrxDate()));
            map.put("status", commonLog.getStatus() == 0 ? "Berhasil" : "Gagal");
            map.put("tellerId", commonLog.getUserid());
            map.put("commonId", String.valueOf(commonId));

            for (TransactionLog tl : trxLogs) {
                map.put(tl.getFieldName(), tl.getFieldValue());
            }

                //get transaction time from transaction log
                if(map.containsKey("transactionTime")) {
                    String transactionTime = map.get("transactionTime").toString();
                    LocalTime localTime = DateTimeFormat.forPattern("HHmmss").parseLocalTime(transactionTime);
                    map.put("transactionDate", format.format(commonLog.getTrxDate()).toString().substring(0,11)+localTime.toString().substring(0,8));
                }



            map.put("traceNumber", log.getTraceNumber());
            map.put("stan", log.getTraceNumber());

            results.add(map);
        }
        System.out.println("ADD TRX TO MAP");

        if (results.size() == 0)
            return results;

        // cek sudah di reinquiry?
        List<String> billingCodes = new ArrayList<>();
        for (Map<String, String> map : results) {
            billingCodes.add(map.get("billingCode"));
        }

        List<TransactionLog> successList = logRepository.findSuccessfulMpnReinquiry(billingCodes);
        for (Map<String, String> map : results) {
            String billingCode = map.get("billingCode");
            if (checkExists(successList, billingCode))
                map.put("reinquiry", "1");
        }

        return results;
    }

    @GetMapping("/getSettlementDate")
    public Map<String, String> getSettlementDate() throws ISOException {
        Date date = new Date();
        Date settlementDate;
        Map<String, String> map = new HashMap<>();

        // cek trx hari biasa, hari libur atau tanggal cut off
        MpnCutOff cutOff = mpnUtil.isOnCutOffPeriod(date);

        if (cutOff != null) {
            settlementDate = cutOff.getServeDate();
        }

        boolean isTrxOnWeekend = mpnUtil.isWeekend(date);
        boolean isTrxOnHoliday = mpnUtil.isHoliday(date);

        // jika transaction di hari libur atau hari kerja
        if (isTrxOnHoliday || isTrxOnWeekend) {
            settlementDate = mpnUtil.getNextWorkingDay(date);
            map.put("settlementDate", format.format(settlementDate));
            return map;
        }
        // request remove cut off time
      //  Calendar calendar = Calendar.getInstance();
      //  calendar.setTime(date);
      //  settlementDate = calendar.getTime();

        // jika hari biasa, cek sudah lewat jam cut off
        boolean afterCutHour = mpnUtil.afterCutOffHour(date);
        if (!afterCutHour) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            settlementDate = calendar.getTime();
        } else {
            settlementDate = mpnUtil.getNextWorkingDay(date);
        }


        map.put("settlementDate", format.format(settlementDate));

        return map;
    }
    @GetMapping("/getSettlementDateMpng2")
    public Map<String, String> getSettlementDateMpng2() throws ISOException {
        Date date = new Date();
        Date settlementDate;
        Map<String, String> map = new HashMap<>();

        // cek trx hari biasa, hari libur atau tanggal cut off
        MpnCutOff cutOff = mpnUtil.isOnCutOffPeriod(date);

        if (cutOff != null) {
            settlementDate = cutOff.getServeDate();
        }

        boolean isTrxOnWeekend = mpnUtil.isWeekend(date);
        boolean isTrxOnHoliday = mpnUtil.isHoliday(date);

        // jika transaction di hari libur atau hari kerja
        if (isTrxOnHoliday || isTrxOnWeekend) {
            settlementDate = mpnUtil.getNextWorkingDay(date);
            map.put("settlementDate", format.format(settlementDate));
            return map;
        }

        // jika hari biasa, cek sudah lewat jam cut off
        boolean afterCutHour = mpnUtil.afterCutOffHour(date);
        if (!afterCutHour) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            settlementDate = calendar.getTime();
        } else {
            settlementDate = mpnUtil.getNextWorkingDay(date);
        }


        map.put("settlementDate", format.format(settlementDate));

        return map;
    }

    @PostMapping(value = {"/djpAtmReinquiry", "/djbcAtmReinquiry", "/djaAtmReinquiry"})
    public ISOMsg request(@RequestBody ISOMsg msg) throws ConnectException, ISOException, InterruptedException, BusinessException, IOException {
        ISOMsg response = gateway.sendToHost(msg);
        if (response == null)
            throw new BusinessException("Time-out exception, host give no response");

        // log transaction here
        String context = FeatureContextHolder.getContext().getFeatureName();
        CommonTransactionLog commonLog = logService.logTransaction(msg, response);
        Map<String, Object> map = FeatureContextHolder.getContext().getReportData();
        TransactionLogger transactionLogger = loggerFactory.get(context);
        List<TransactionLog> logs = transactionLogger.addLog(map);

        logService.saveAdditionalLog(logs, commonLog, msg.getString(11), context);
        return response;
    }

    private boolean checkExists(List<TransactionLog> logs, String billingCode) {
        for (TransactionLog log : logs) {
            if (StringUtils.equals(log.getFieldValue(), billingCode))
                return true;
        }
        return false;
    }


} 