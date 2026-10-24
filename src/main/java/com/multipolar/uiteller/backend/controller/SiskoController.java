package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.BpihUploadModel;
import com.multipolar.uiteller.backend.model.CommonTransactionLog;
import com.multipolar.uiteller.backend.model.CommonTransactionSiskoRequestLog;
import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.repository.SysParamRepository;
import com.multipolar.uiteller.backend.repository.TransactionLogRepository;
import com.multipolar.uiteller.backend.resource.BusinessException;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.BpihService;
import com.multipolar.uiteller.backend.service.RequestService;
import com.multipolar.uiteller.backend.service.iso.DummySiskoGateway;
import com.multipolar.uiteller.backend.service.iso.MessageGateway;
import com.multipolar.uiteller.backend.service.iso.SiskoGateway;
import com.multipolar.uiteller.backend.service.logger.RequestLogService;
import com.multipolar.uiteller.backend.service.logger.TransactionLogService;
import com.multipolar.uiteller.backend.service.logger.TransactionLogger;
import com.multipolar.uiteller.backend.service.logger.TransactionLoggerFactory;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.*;
import org.jpos.util.NameRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/apiv3")
public class SiskoController {

    @Autowired
    private SiskoGateway siskoGateway;
    @Autowired
    private DummySiskoGateway dummySiskoGateway;
    @Autowired
    private MessageGateway messageGateway;
    @Autowired
    private TransactionLogService logService;
    @Autowired
    private TransactionLoggerFactory loggerFactory;
    @Autowired
    private RequestService requestService;
    @Autowired
    private BpihService bpihService;
    @Autowired
    private TransactionLogRepository logRepository;

    @Autowired
    private RequestLogService requestLogService;
    private static final String INITIAL_PCODE = "320000";
    private static final String INQUIRY_PAYMENT_PCODE = "312000";
    private static final String PAYMENT_PCODE = "322000";
    private static final String INITIAL_PRINT_PCODE = "340000";
    private static final String PAYMENT_PRINT_PCODE = "342000";

    @Value("${SiskoController.flDebit.modeTest}")
    private boolean SiskoControllerFlDebitModeTest;

    @Value("${SiskoController.siskoGateway.modeTest}")
    private boolean SiskoControllerSiskoGatewayModeTest;

    @Value("${SiskoController.settlement.date}")
    private String SiskoControllerSettlementDate;

    @Autowired
    private SysParamRepository sysParamRepository;

    @GetMapping("/checkConnectionSisko")
    public ResponseEntity<?> checkConnectionSisko() {
        String muxName = "mux.siskomux";
        MUX mux = (MUX) NameRegistrar.getIfExists(muxName);

        String connection = "not connected";
        if(siskoGateway.isConnected(mux)){
            connection = "connected";
        }

        Map<String, String> map = new HashMap<>();
        map.put("ConnectionSisko", connection);
        return ResponseEntity.ok().body(map);
    }

    @GetMapping("/disconnectConnectionSisko")
    public ResponseEntity<?> disconnectConnectionSisko() throws IOException {
        String muxName = "mux.siskomux";
        MUX mux = (MUX) NameRegistrar.getIfExists(muxName);

        BaseChannel baseChannel = (BaseChannel) NameRegistrar.getIfExists("channel.siskochannel");
        baseChannel.setHeader(ISOUtil.asciiToEbcdic("V117     117        "));

        baseChannel.disconnect();

        String connection = "not connected";
        if(siskoGateway.isConnected(mux)){
            connection = "connected";
        }

        Map<String, String> map = new HashMap<>();
        map.put("ConnectionSisko", connection);
        return ResponseEntity.ok().body(map);
    }

    @GetMapping("/reconnectConnectionSisko")
    public ResponseEntity<?> reconnectConnectionSisko() throws IOException {
        String muxName = "mux.siskomux";
        MUX mux = (MUX) NameRegistrar.getIfExists(muxName);

        BaseChannel baseChannel = (BaseChannel) NameRegistrar.getIfExists("channel.siskochannel");
        baseChannel.setHeader(ISOUtil.asciiToEbcdic("V117     117        "));

        baseChannel.connect();

        String connection = "not connected";
        if(siskoGateway.isConnected(mux)){
            connection = "connected";
        }

        Map<String, String> map = new HashMap<>();
        map.put("ConnectionSisko", connection);
        return ResponseEntity.ok().body(map);
    }

    @PostMapping("/reUpload")
    public BpihUploadModel reuploadReport(@RequestBody BpihUploadModel model) {
        String noValidasi = model.getNoValidasi();
        return bpihService.reUploadReport(noValidasi);
    }

    @PostMapping("/**")
    public ISOMsg request(@RequestBody ISOMsg msg) throws ISOException, InterruptedException, BusinessException, IOException {
        boolean flDebit = false;
        boolean flSisko = false;
        boolean pay, prt, inq;
        ISOMsg resDebit = new ISOMsg();
        ISOMsg resSisko = new ISOMsg();
        ISOMsg reqDebit = new ISOMsg();
        String context = FeatureContextHolder.getContext().getFeatureName();
        CommonTransactionSiskoRequestLog commonTransactionSiskoRequestLog = null;
        System.out.println("ini sout => "+msg.getString(3));
        inq = StringUtils.equals(msg.getString(3), INQUIRY_PAYMENT_PCODE);
        pay = StringUtils.equals(msg.getString(3), INITIAL_PCODE) || StringUtils.equals(msg.getString(3), PAYMENT_PCODE);
        prt = StringUtils.equals(msg.getString(3), INITIAL_PRINT_PCODE) || StringUtils.equals(msg.getString(3), PAYMENT_PRINT_PCODE);

        if((StringUtils.equals(msg.getString(3), PAYMENT_PCODE)) || (StringUtils.equals(msg.getString(3), INITIAL_PCODE))){
            String ref = msg.getString(37).replaceAll("^0+", "");
            ref.replaceAll("\\s", "");
            String[] type= logRepository.findByRef(ref);
            if (type != null && StringUtils.equalsAny(context, type)){
                ISOMsg doublePay = (ISOMsg) msg.clone();
                doublePay.set(39, "400");
                return doublePay;
            }
        }

        if (pay) {
            reqDebit = requestService.generateDebit(msg);
            commonTransactionSiskoRequestLog = requestLogService.siskoRequest(context, msg, reqDebit);

            if(SiskoControllerFlDebitModeTest){
                flDebit = false;
            } else {
            resDebit = messageGateway.sendToHost(reqDebit);
            flDebit = messageGateway.state(resDebit);
            }
        }

        if ((!flDebit && pay) || inq || prt) {
//            msg.set(7, new SimpleDateFormat("MMddHHmmss").format(new Date(System.currentTimeMillis() - 3600*7 * 1000)));
            SiskoControllerSettlementDate = sysParamRepository.findByPropertyName("SiskoController.settlement.date").getPropertyValue();
            if(SiskoControllerSettlementDate == null || SiskoControllerSettlementDate.equals("today")) {
                msg.set(15, new SimpleDateFormat("yyMMdd").format(new Date()));
            }else{
                msg.set(15, SiskoControllerSettlementDate);
            }
            System.out.println("siskohat settlement ==> "+msg.getString(15));

            if(SiskoControllerSiskoGatewayModeTest){
                resSisko = dummySiskoGateway.sendToHost(msg);
                flSisko = dummySiskoGateway.state(resSisko);
            } else {
            resSisko = siskoGateway.sendToHost(msg);
            flSisko = siskoGateway.state(resSisko);
            }
        }

        Map<String, Object> map = FeatureContextHolder.getContext().getReportData();
        TransactionLogger transactionLogger = loggerFactory.get(context);
        List<TransactionLog> logs = transactionLogger.addLog(map);

        if (resSisko != null && (pay || prt)) {
            CommonTransactionLog commonLog = logService.logTransaction(msg, resSisko);
            logService.saveAdditionalLog(logs, commonLog, msg.getString(11), context);

            // jika print setoran awal, create, save dan upload pdf file
            if (StringUtils.equals(msg.getString(3), INITIAL_PRINT_PCODE)) {
                // construct parameter dari transaction logs
                Map<String, Object> reportParameter = bpihService.remap(map);
                // ambil no validasi
                String nomorValidasi =
                        reportParameter.get("nomorValidasi") != null ?
                                reportParameter.get("nomorValidasi").toString() : "-";

                File pdfFile = bpihService.createAndSavePdf(nomorValidasi, reportParameter);
                if (pdfFile != null) {
                    List<TransactionLog> pdfLogs = new ArrayList<>(2);
                    // save transaction log untuk save pdf
                    TransactionLog saveLog = new TransactionLog(Constants.BPIH_SAVE_PDF_FIELD_NAME, Constants.BPIH_SAVE_PDF_FIELD_VALUE);
                    pdfLogs.add(saveLog);

                    /* commented upload for bulk by scheduler
                    // upload pdf to bpih server
                    BpihUploadModel uploadModel = bpihService.loginAndUploadReport(pdfFile.getPath(), nomorValidasi);
                    if (StringUtils.equals(uploadModel.getRc(), "00")) {
                        TransactionLog uploadLog = new TransactionLog(Constants.BPIH_UPLOAD_PDF_FIELD_NAME, Constants.BPIH_UPLOAD_PDF_FIELD_VALUE);
                        pdfLogs.add(uploadLog);
                        // simpan juga di FeatureContextHolder
                        FeatureContextHolder.getContext().setBpihReportUploadFlag(1);
                    }
                     */
                    logService.saveAdditionalLog(pdfLogs, commonLog, msg.getString(11), context);
                }
            }
        }

        if (flDebit) {
            ISOMsg isoMsg = new ISOMsg();
            if (resDebit == null || StringUtils.equals(resDebit.getString(39), Constants.TIMEOUT_CODE)) {
                isoMsg.set(39, Constants.TIMEOUT_DEBIT_CODE_SISKO);
                resDebit = new ISOMsg();
                resDebit.set(39, Constants.SISKO_DEBIT_RC);
                requestLogService.requestSiskoState(commonTransactionSiskoRequestLog, isoMsg);
                return resDebit;
            }
            isoMsg.set(39, Constants.FAILED_DEBIT_CODE_SISKO);
            resDebit.set(39, resDebit.getString(39) + Constants.ZERO);
            requestLogService.requestSiskoState(commonTransactionSiskoRequestLog, isoMsg);
            return resDebit;
        } else if (flSisko && !inq && !prt) {
            if (resSisko == null) {
                resSisko = new ISOMsg();
                resSisko.set(39, Constants.SISKO_SISKO_RC);
            }
            requestLogService.requestSiskoState(commonTransactionSiskoRequestLog, resSisko);
            return resSisko;
        } else if (!inq && !prt) {
            requestLogService.requestSiskoState(commonTransactionSiskoRequestLog, resSisko);
        }
        return resSisko;
    }
}
