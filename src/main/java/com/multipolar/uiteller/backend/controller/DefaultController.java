package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.*;
import com.multipolar.uiteller.backend.repository.TransactionLogRepository;
import com.multipolar.uiteller.backend.repository.UniversitasLogRepository;
import com.multipolar.uiteller.backend.resource.BusinessException;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.repository.SysParamRepository;
import com.multipolar.uiteller.backend.security.model.AccountResponse;
import com.multipolar.uiteller.backend.security.model.AccountResult;
import com.multipolar.uiteller.backend.security.model.ReversalResponse;
import com.multipolar.uiteller.backend.service.*;
import com.multipolar.uiteller.backend.service.iso.MessageGateway;
import com.multipolar.uiteller.backend.service.logger.*;
import com.multipolar.uiteller.backend.service.report.NotificationService;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.multipolar.uiteller.backend.resource.RkudPbb.MAP_RKUD_PBB;

@Slf4j
@RestController
@RequestMapping("/apiv2")
public class DefaultController {

    @Value("${mpn.settlement.hour}")
    private Integer mpnSettlementHour;

    @Value("${mpn.settlement.minute}")
    private Integer mpnSettlementMinute;

    @Value("${mpn.settlement.second}")
    private Integer mpnSettlementSecond;



    @Autowired
    private MessageGateway gateway;

    @Autowired
    private TransactionLogService logService;

    @Autowired
    private CoreGatewayServiceImpl coreGatewayService;

    @Autowired
    private TransactionLoggerFactory loggerFactory;

    @Autowired
    private RequestLogService requestLogService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MiddlewareGatewayServiceImpl middlewareGatewayService;

    @Autowired
    private ForwardingLogService forwardingLogService;

    @Autowired
    private TransactionLogRepository logRepository;

    @Autowired
    private MonitoringLogService monitoringLogService;

    @Autowired
    private CTASReinquiryService ctasReinquiryService;

    @Autowired
    private CTASManualReinquiry ctasManualReinquiry;

    @Autowired
    private SharedVariableService mpnBlocker;

    @Autowired
    private BifastReinquiryService bifastReinquiryService;
    @Autowired
    private SysParamRepository sysParamRepository;

    // TO DO - this is hardcoding
    private static final String INQUIRY_PCODE = "380000";
    private static final String PAYMENT_PCODE = "500000";
    private static final String CHECK_PAYMENT_PCODE = "502000";
    private static final String PBB_PAYMENT = "pbb";
    private static final String[] MPN_REINQUIRY = {"djpReinquiry", "djbcReinquiry", "djaReinquiry", "djpg3Reinquiry", "djbcg3Reinquiry", "djag3Reinquiry"};
    private static final String[] MPNG3_REINQUIRY = {"djpg3Reinquiry", "djbcg3Reinquiry", "djag3Reinquiry"};
    private static final String[] MPN = {"djp", "dja", "djbc", "djpg3", "djag3", "djbcg3"};
    private static final String[] MPNG2 = {"djp", "dja", "djbc"};
    private static final String [] MPNG3 = {"djpg3", "djag3", "djbcg3"};
    private static final String[] MPN_INQUIRY = {"djpInquiry", "djaInquiry", "djbcInquiry", "djpg3Inquiry", "djag3Inquiry", "djbcg3Inquiry"};
    private static final String[] MPN_REVERSAL_RC = {"02", "27", "88"};
    private static final String[] MPN_NON_REVERSAL_RC = {"00", "08"};
    private static  final String[] HACK_DOUBLE = {"djp", "dja", "djbc","djpg3", "djag3", "djbcg3", "pbb"};

    private static final String BIFAST_REINQUIRY = "bifastReinquiry";
    private  static final String BIFAST = "bifast";
    private  static final String INQUIRY_BIFAST = "bifastInquiry";

    private static final String BIFAST_TRANSFER = "bifast";


    @PostMapping("/**")
    public ISOMsg request(@RequestBody ISOMsg msg) throws ISOException, InterruptedException, BusinessException, IOException {
        String context = FeatureContextHolder.getContext().getFeatureName();
        final String accountNumber = msg.getString(102);
        AccountResponse accountResponse = coreGatewayService.getAccountResponse(accountNumber);
        if (accountResponse == null)
            throw new BusinessException("Time-out exception, host give no response");
        AccountResult accountResult = accountResponse.getResult();
        if (notificationService.transactionException(accountResult, msg) != null)
            return notificationService.transactionException(accountResult, msg);
        String accountName = accountResult.getNICKNM();
        FeatureContextHolder.getContext().setSourceName(accountName);
        CommonTransactionRequestLog commonTransactionRequestLog = requestLogService.request(context, msg);
        if (StringUtils.equalsAny(context, MPNG3_REINQUIRY)) {
            System.out.println("REINQUIRY MANUAL");
            msg = ctasManualReinquiry.sendManualReinquiry(msg);
        }

        //bifast for giro hack
        if (StringUtils.equalsAny(context, BIFAST) && StringUtils.equals(accountNumber.substring(4,5), "1")){
            String modifiedPcode =StringUtils.overlay(msg.getString(3), "2", 2, 3);
            msg.set(3, modifiedPcode);
            System.out.println("PCODEBIFASTMODIFIED"+ modifiedPcode);
        }
        //bifast check transaction time
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        String bifastCutoff = sysParamRepository.findByPropertyName(Constants.BIFAST_CUTOFF) == null ? "15" : sysParamRepository.findByPropertyName(Constants.BIFAST_CUTOFF).getPropertyValue();
        if(bifastCutoff.equals(null))
            bifastCutoff = "15";
        Integer bifastCutoffInt = Integer.valueOf(bifastCutoff);
        if((StringUtils.equalsAny(context, BIFAST_TRANSFER)||StringUtils.equalsAny(context,INQUIRY_BIFAST))&&currentHour>=bifastCutoffInt){
            ISOMsg bifastResponse = (ISOMsg) msg.clone();
            bifastResponse.set(39, "101");
            return bifastResponse;
        }
        //hack for trx double pay mpn only
//        if(StringUtils.equals(msg.getString(3), PAYMENT_PCODE)){
        if((StringUtils.equals(msg.getString(3), PAYMENT_PCODE) || StringUtils.equals(msg.getString(3), CHECK_PAYMENT_PCODE)) && StringUtils.equalsAny(context, HACK_DOUBLE)){
            String ref = msg.getString(37).replaceAll("^0+", "");
            ref.replaceAll("\\s", "");
            String[] type= logRepository.findByRef(ref);
            if (type != null && StringUtils.equalsAny(context, type)){
                ISOMsg doublePay = (ISOMsg) msg.clone();
                doublePay.set(39, "400");
                return doublePay;
            }
        }

        // Reject inquiry atau payment MPN jika payment sedang dalam proses (belum dapat response)
        if(StringUtils.equalsAny(context, MPN_INQUIRY) || StringUtils.equalsAny(context, MPN)){
            String billingCode = msg.getString(61).substring(27, 42);
            if (mpnBlocker.get(billingCode) != null) {
                ISOMsg reject = (ISOMsg) msg.clone();
                reject.set(39, "400");
                return reject;
            }
        }

        // Tambahkan id billing mpn ke mpnBlocker sebelum kirim payment ke IST
        if(StringUtils.equalsAny(context, MPN)){
            String billingCode = msg.getString(61).substring(27, 42);
            mpnBlocker.set(billingCode, System.currentTimeMillis());
            log.info("add block mpn id bill " + billingCode);
        }

        ISOMsg response = gateway.sendToHost(msg);

        // Release mpnBlocker setelah response dari IST
        if(StringUtils.equalsAny(context, MPN)){
            String billingCode = msg.getString(61).substring(27, 42);
            mpnBlocker.remove(billingCode);
            log.info("release block mpn id bill " + billingCode);
        }

        boolean gotResponse = true;
        if (response == null){
            gotResponse = false;
            //no response ist, set 08 supaya di reinquiry
            if (StringUtils.equalsAny( FeatureContextHolder.getContext().getFeatureName(), MPNG3)){
                response = msg;
                response.set(39,"08");
                response.set(48,msg.getString(11));
                response.setMTI("0210");
                gotResponse = true;
            }

            //hack pbb no response, log and fill norek tujuan
            if (StringUtils.equalsAny( FeatureContextHolder.getContext().getFeatureName(), PBB_PAYMENT)){
                String prefixRkud = msg.getString(61).substring(27, 31);
                System.out.println("prefixRkud: " + prefixRkud);
                String norekTujuanRkud = MAP_RKUD_PBB.get(prefixRkud);

                response = msg;
                response.setMTI("0210");
                response.set(39,"08");
                response.set(103, norekTujuanRkud);
                gotResponse = true;
            }
        }


        // hack for inquiry MPN, jika yang diinquiry statusnya 08 cek ke database terlebih dahulu. Jika sudah ada, maka tampilkan pesan
        if (gotResponse && StringUtils.equals(response.getString(39), "00") && StringUtils.equalsAny(context, MPN_INQUIRY)
                && StringUtils.equals(msg.getString(3), INQUIRY_PCODE)) {
            log.debug("request map : ", FeatureContextHolder.getContext().getReportData());
            Map<String, Object> requestMap = FeatureContextHolder.getContext().getReportData();
            String billingCode = requestMap.get("billingCode") != null ? requestMap.get("billingCode").toString() : "";
            if (StringUtils.isNotEmpty(billingCode)) {
                //cek mpn berdasarkan tanggal buku, bukan lagi jam cutoff
                SimpleDateFormat format = new SimpleDateFormat("yyyy");
                String settlementDateResponse = response.getString(15).substring(2,4);
                String settlementMonthResponse = response.getString(15).substring(0,2);
                Date date = new Date();
                String settlementDate = settlementDateResponse + "/" +
                        settlementMonthResponse + "/"+format.format(date);
                log.debug("datetime: " + settlementDate);
                log.debug("billingCode: " + billingCode);

                List<TransactionLog> count1 = logRepository.findInAbnormalOrUnsettle(billingCode);

              for (TransactionLog logs1 : count1){
                 List<TransactionLog> count2 = logRepository.findByCommonTransactionId(logs1.getCommonTransaction().getId());
                 for (TransactionLog logs2 : count2){
                     if((StringUtils.equals(logs2.getFieldName(),"settlementDate")) && StringUtils.equals(logs2.getFieldValue(),settlementDate)){
                         throw new BusinessException("Transaksi sedang diproses");
                     }
                 }
                }
               // if (count > 0)
              //      throw new BusinessException("Transaksi sedang diproses");
//                    throw new BusinessException("Billing code sudah terbayar, silahkan cek di menu reinquiry");
            }
        }

        // hack for pembayaran sts
        if (gotResponse && StringUtils.equals(context, PBB_PAYMENT) ) {
            if(response.getString(103).isEmpty()){
                FeatureContextHolder.getContext().setDestinationName("");
            }else{
                try{
                    final String destAccountNumber = response.getString(103);
                    AccountResponse destAccountResponse = coreGatewayService.getAccountResponse(destAccountNumber);
                    if (destAccountResponse == null)
                        throw new BusinessException("Time-out exception, host give no response");
                    AccountResult destAccountResult = destAccountResponse.getResult();
                    if (destAccountResult != null) {
                        String destAccountName = destAccountResult.getNICKNM();
                        FeatureContextHolder.getContext().setDestinationName(destAccountName);
                    }
                }catch(Exception e){
                    String destAccountName = "--";
                    FeatureContextHolder.getContext().setDestinationName(destAccountName);
                }
            }
        }

        // log transaction here
        // untuk reinquiry menggunakan pcode 380000
        String feature = FeatureContextHolder.getContext().getFeatureName();
        if (StringUtils.equals(msg.getString(3), PAYMENT_PCODE) || StringUtils.equals(msg.getString(3), CHECK_PAYMENT_PCODE)) {
            CommonTransactionLog commonLog = logService.logTransaction(msg, response);
            Map<String, Object> map = FeatureContextHolder.getContext().getReportData();
            TransactionLogger transactionLogger = loggerFactory.get(context);

            // hack for settlementDate MPN
            if (StringUtils.equalsAny(feature, MPN) || StringUtils.equalsAny(feature, MPN_REINQUIRY)) {
                MPNTransactionLogger mpnLogger = (MPNTransactionLogger) transactionLogger;
                mpnLogger.setSettlementDate(map, response);
            }

            List<TransactionLog> logs = transactionLogger.addLog(map);

            // hack for admin charge
            String requestBit28 = msg.getString("28");
            String responseBit28 = response != null ? response.getString("28") : "0";
            if (StringUtils.equals("000000000", requestBit28)) {
                String adminCharge = responseBit28 != null ? responseBit28 : "0";
                TransactionLog chargeLog = new TransactionLog("adminCharge", StringUtils.stripStart(adminCharge, "0"));
                logs.add(chargeLog);
            } else {
                TransactionLog chargeLog = new TransactionLog("adminCharge", "0");
                if(StringUtils.equals(feature, BIFAST)){
                    chargeLog = new TransactionLog("adminCharge", StringUtils.stripStart(requestBit28, "0"));
                }else {
                    chargeLog = new TransactionLog("adminCharge", "0");
                }
                logs.add(chargeLog);
            }
            if (StringUtils.equalsAny(feature, MPNG3) || StringUtils.equalsAny(feature, MPNG3_REINQUIRY)){
                logService.saveAdditionalLog(logs, commonLog, response.getString(48), context);
            }else{
                logService.saveAdditionalLog(logs, commonLog, msg.getString(11), context);
                }

            requestLogService.requestState(commonTransactionRequestLog, response);
            // check status billing djp g3
            if(StringUtils.equalsAny(feature, MPNG3) && StringUtils.equals(response.getString(39), "08")){
                // call reinquiry
                FeatureContextHolder.getContext().setFeatureName(feature+"Reinquiry");
                ISOMsg reinquiryResponse = ctasReinquiryService.sendReinquiry(msg);
                response = (ISOMsg) reinquiryResponse.clone();
            }
//
//            // check status bifast
//            log.debug("Check feature: " + feature);
//            if(StringUtils.equals(feature, "bifast") && StringUtils.equals(response.getString(39), "08")){
//                log.debug("reinquiry Bifast: " + feature);
//                // call reinquiry
//                FeatureContextHolder.getContext().setFeatureName("bifastReinquiry");
//                log.debug("XXXX 63 : " + msg.getString(63));
//                msg.set(63, "02003");
//                msg.set(3, "380000");
//                ISOMsg reinquiryResponse = bifastReinquiryService.sendReinquiry(msg);
//                response = (ISOMsg) reinquiryResponse.clone();
//            }

            // untuk transaksi yang tidak mengembalikan RC00, masuk ke tabel ini untuk di review dan troubleshoot
            //monitoringLogService.failedTransactionLog(commonLog, commonTransactionRequestLog, msg.getString(11), msg.getString(37), response.getString(39));
            // monitoringLogService.saveSuspectTransaction(commonLog, msg, response); <-- commented due to bank request, delay cms call
            monitoringLogService.delayTransactionStatusCall(commonLog, msg, response);
        }

        if (StringUtils.equalsAny(feature, MPN_REINQUIRY)) {
            logService.updateSuccessMpnReinquiry(msg, response);
        }

        if (StringUtils.equalsAny(feature, BIFAST_REINQUIRY)) {
            logService.updateSuccessBifastReinquiry(msg, response);
        }

        if (!gotResponse)
            throw new BusinessException("Time-out exception, host give no response");

        // lakukan reversal saat rc selain 00, dan timeout (08)
//        if (StringUtils.equalsAny(feature, MPNG2) &&
//                StringUtils.equalsAny(response.getString(39), MPN_REVERSAL_RC)) {
//            Long commonRequestId = commonTransactionRequestLog.getId();
//            TransactionRequestLog logs = forwardingLogService.getRequestTransactionLog(commonRequestId, Constants.REQUEST_TIMEOUT_FIELD_NAME);
//            ReversalResponse reversalResponse = middlewareGatewayService.getReversalResponse(String.valueOf(commonRequestId));
////            if (reversalResponse == null)
////                throw new IllegalArgumentException("Time-out exception, host give no response");
//
//            forwardingLogService.setRevFlag(logs, reversalResponse);
//            logService.updateSuccessMpnReversal(msg, reversalResponse);
//        }

        return response;
    }
}
