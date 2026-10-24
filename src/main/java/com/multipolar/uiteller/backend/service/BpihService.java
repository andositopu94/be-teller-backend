package com.multipolar.uiteller.backend.service;

import com.multipolar.uiteller.backend.model.BpihUploadModel;
import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.repository.TransactionLogRepository;
import com.multipolar.uiteller.backend.resource.Constants;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.nio.file.FileSystems;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BpihService {
    private final Logger logScheduler = LogManager.getLogger("scheduler-log");
    private final Logger logUpload = LogManager.getLogger("bpih-upload");

    @Value("${report.setoran.pdf.userid}")
    private String username;
    @Value("${report.setoran.pdf.password}")
    private String password;
    @Value("${report.setoran.pdf.loginUrl}")
    private String bpihLoginUrl;
    @Value("${report.setoran.pdf.accessKey}")
    private String accessKey;
    @Value(("${report.setoran.pdf.url}"))
    private String bpihUrl;
    @Value("${resi.setoran.pdf.folder}")
    private String reportFolder;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TransactionLogRepository logRepository;

    private static final SimpleDateFormat originFormat = new SimpleDateFormat("ddMMyyyy");
    private static final SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat targetTime = new SimpleDateFormat("HH:mm:ss");

    private static BpihUploadModel cacheToken;

    private void ignoreCertificates() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {


        }
    }

    public BpihUploadModel loginAndUploadReport(String filePath, String noValidasi) {
        BpihUploadModel uploadModel = null;
        // coba 2x in case cacheToken sudah invalid
        int counter = 0;
        do {
            counter++;
            try {
                logUpload.info("login start ");
                BpihUploadModel loginResponse = login();
                logUpload.info("loginResponse, No Validasi : " + loginResponse.getNoValidasi() + ", RC : " + loginResponse.getRc() + ", Message : " + loginResponse.getMessage());
                if (!StringUtils.equals(loginResponse.getRc(), "00")) {
                    cacheToken = null;
                    continue;
                }
                String token = loginResponse.getToken();
                cacheToken = loginResponse;
                logUpload.info("upload start ");
                uploadModel = uploadReport(token, filePath, noValidasi);
                logUpload.info("uploadReport, No Validasi : " + uploadModel.getNoValidasi() + ", RC : " + uploadModel.getRc() + ", Message : " + uploadModel.getMessage());
                // jika token invalid, invalidate token cache dan re-loginUpload
                if (StringUtils.equals(uploadModel.getRc(), "96") || StringUtils.equals(uploadModel.getRc(), "98"))
                    cacheToken = null;
                else
                    break;
            } catch (RestClientException | SocketTimeoutException rx) {
                rx.printStackTrace();
                // jika exception, karena token invalid, invalidate token cache dan re-loginUpload
                if (rx instanceof RestClientResponseException
                        && ((RestClientResponseException) rx).getRawStatusCode() == 400)
                    cacheToken = null;
                logUpload.info("loginAndUploadReportResponse, " + rx.getMessage());
            } catch (Exception ex) {
                logUpload.info("loginAndUploadReportResponse, " + ex.getMessage());
            }
        } while (counter <= 1);

        if (uploadModel == null)
            uploadModel = new BpihUploadModel("999", "Terjadi kesalahan pada systems");

        logUpload.info("loginAndUploadReport (done), " + uploadModel);
        return uploadModel;
    }

    @Transactional
    public BpihUploadModel reUploadReport(String noValidasi) {
        logUpload.info("reUploadReport start");
        TransactionLog keyLog = logRepository.findFirstByFieldNameAndFieldValueAndTrxTypeOrderByIdDesc(
                "validationNumber", noValidasi, "bpihinitial");
        TransactionLog keyLogPrint = logRepository.findFirstByFieldNameAndFieldValueAndTrxTypeOrderByIdDesc(
                "validationNumber", noValidasi, "bpihinitial");
        if (keyLog == null) {
            logUpload.info("reUploadReport, RC : 888, Message : Data Tidak ditemukan di database");
            return new BpihUploadModel("888", "Data Tidak ditemukan di database");
        }

        // cek pdf file
        String filePath = reportFolder + FileSystems.getDefault().getSeparator() + noValidasi + ".pdf";
        File file = new File(filePath);
        if (!file.exists()) {
            Map<String, Object> parameter = remapFromLogs(keyLog);
            file = createAndSavePdf(noValidasi, parameter);
            logUpload.info("reUploadReport, File tidak ditemukan");
        }
        logUpload.info("loginAndUploadReport start");
        BpihUploadModel response = loginAndUploadReport(file.getPath(), noValidasi);
        logUpload.info("loginAndUploadReport, RC : " + response.getRc());
        if (StringUtils.equals(response.getRc(), "00") || StringUtils.equals(response.getRc(), "29")) {
            TransactionLog uploadLog = new TransactionLog(Constants.BPIH_UPLOAD_PDF_FIELD_NAME,
                    Constants.BPIH_UPLOAD_PDF_FIELD_VALUE,
                    "bpihinitial",
                    keyLogPrint.getTraceNumber(),
                    keyLogPrint.getCommonTransaction());
            logUpload.info("reUploadReport, "+uploadLog);
            logRepository.save(uploadLog);
        }
        return response;
    }

    private BpihUploadModel login() {
        // if token sudah ada di cache, gunakan token
//        if (cacheToken != null)
//            return cacheToken;
        ignoreCertificates();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-key", accessKey);

        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("userid", username);
        bodyMap.put("password", password);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(bodyMap, headers);
        try {
            ResponseEntity<BpihUploadModel> response = restTemplate.exchange(bpihLoginUrl, HttpMethod.POST, entity, BpihUploadModel.class);
            logUpload.info("loginResponse, " + response.getBody());
            return response.getBody();
        } catch (Exception ex) {
            logUpload.info("loginResponse, " + ex.getMessage());
            return null;
        }
    }

    private BpihUploadModel uploadReport(String token, String filePath, String noValidasi)
            throws SocketTimeoutException {
        ignoreCertificates();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("x-access-key", token);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        File file = new File(filePath);
        body.add("upload", new FileSystemResource(file));
        body.add("no_validasi", noValidasi);

        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<BpihUploadModel> response = restTemplate
                    .postForEntity(bpihUrl, requestEntity, BpihUploadModel.class);
            logUpload.info("uploadResponse, " + response.getBody());
            if(response.getBody().getRc()=="29"){

            }
            return response.getBody();
        } catch (Exception ex) {
            logUpload.info("uploadResponse, " + ex.getMessage());
            return null;
        }
    }

    public Map<String, Object> remap(Map<String, Object> logs) {
        Map<String, Object> map = new HashMap<>();
        String trxDate = null, trxTime = null;
        for (Map.Entry<String, Object> entry : logs.entrySet()) {
            if (StringUtils.equals(entry.getKey(), "validationNumber"))
                map.put("nomorValidasi", entry.getValue());
            if (StringUtils.equals(entry.getKey(), "archiveNumber"))
                map.put("nomorTransaksi", entry.getValue());
            if (StringUtils.equals(entry.getKey(), "transactionDate") && entry.getValue() != null) {
                Date date;
                try {
                    date = originFormat.parse(entry.getValue().toString());
                } catch (ParseException e) {
                    date = new Date();
                }
                trxDate = targetFormat.format(date);
            }
            if (StringUtils.equals(entry.getKey(), "transactionTime")) {
                if (entry.getValue() != null)
                    trxTime = entry.getValue().toString();
                else
                    trxTime = targetTime.format(new Date());
            }
            if (StringUtils.equals(entry.getKey(), "sourceNumber")) {
                String rekening = entry.getValue().toString();
                rekening =
                        StringUtils.overlay(rekening, StringUtils.repeat("X", rekening.length() - 4), 0, rekening.length() - 4);
                map.put("nomorRekening", rekening);
            }
            if (StringUtils.equals(entry.getKey(), "ktp")) {
                String ktp = entry.getValue().toString();
                ktp =
                        StringUtils.overlay(ktp, StringUtils.repeat("X", ktp.length() - 4), 0, ktp.length() - 4);
                map.put("noKtp", ktp);
            }
            if (StringUtils.equals(entry.getKey(), "jemaahName"))
                map.put("namaCalonHaji", entry.getValue());
            if (StringUtils.equals(entry.getKey(), "nominal") && entry.getValue() != null) {
                String setoran = StringUtils.removeStart(entry.getValue().toString(), "0");
                setoran = StringUtils.substring(setoran, 0, setoran.length() - 2);
                String terbilangSetoran = new KonversiMataUangKeTerbilang(new BigDecimal(setoran)).toString();
                map.put("nilaiSetoran", new BigDecimal(setoran));
                map.put("terbilang", terbilangSetoran);
            }
            if (StringUtils.equals(entry.getKey(), "virtualAccount")) {
                if (entry.getValue() != null)
                    map.put("nomorVirtualAccount", entry.getValue());
                else
                    map.put("nomorVirtualAccount", "-");
            }
        }

        // construct trxDate and time
        if (trxDate == null)
            trxDate = targetFormat.format(new Date());
        if (trxTime == null)
            trxTime = targetTime.format(new Date());
        map.put("tanggalTransaksi", trxDate + " " + trxTime);
        return map;
    }

    private Map<String, Object> remapFromLogs(TransactionLog fromLog) {
        Map<String, Object> map = new HashMap<>();
        List<TransactionLog> logs = logRepository.findByCommonTransactionId(fromLog.getCommonTransaction().getId());
        String trxDate = null, trxTime = null;
        for (TransactionLog log : logs) {
            if (StringUtils.equals(log.getFieldName(), "validationNumber"))
                map.put("nomorValidasi", log.getFieldValue());
            if (StringUtils.equals(log.getFieldName(), "archiveNumber"))
                map.put("nomorTransaksi", log.getFieldValue());
            if (StringUtils.equals(log.getFieldName(), "transactionDate") && log.getFieldValue() != null) {
                Date date;
                try {
                    date = originFormat.parse(log.getFieldValue());
                } catch (ParseException e) {
                    date = new Date();
                }
                trxDate = targetFormat.format(date);
            }
            if (StringUtils.equals(log.getFieldName(), "transactionTime")) {
                if (log.getFieldValue() != null)
                    trxTime = log.getFieldValue();
                else
                    trxTime = targetTime.format(new Date());
            }
            if (StringUtils.equals(log.getFieldName(), "sourceNumber")) {
                String rekening = log.getFieldValue();
                rekening =
                        StringUtils.overlay(rekening, StringUtils.repeat("X", rekening.length() - 4), 0, rekening.length() - 4);
                map.put("nomorRekening", rekening);
            }
            if (StringUtils.equals(log.getFieldName(), "ktp")) {
                String ktp = log.getFieldValue();
                ktp =
                        StringUtils.overlay(ktp, StringUtils.repeat("X", ktp.length() - 4), 0, ktp.length() - 4);
                map.put("noKtp", ktp);
            }
            if (StringUtils.equals(log.getFieldName(), "jemaahName"))
                map.put("namaCalonHaji", log.getFieldValue());
            if (StringUtils.equals(log.getFieldName(), "nominal")) {
                String setoran = StringUtils.removeStart(log.getFieldValue(), "0");
                setoran = StringUtils.substring(setoran, 0, setoran.length() - 2);
                String terbilangSetoran = new KonversiMataUangKeTerbilang(new BigDecimal(setoran)).toString();
                map.put("nilaiSetoran", new BigDecimal(setoran));
                map.put("terbilang", terbilangSetoran);
            }
            if (StringUtils.equals(log.getFieldName(), "virtualAccount")) {
                if (log.getFieldValue() != null)
                    map.put("nomorVirtualAccount", log.getFieldValue());
                else
                    map.put("nomorVirtualAccount", "-");
            }
        }

        // construct trxDate and time
        if (trxDate == null)
            trxDate = targetFormat.format(new Date());
        if (trxTime == null)
            trxTime = targetTime.format(new Date());
        map.put("tanggalTransaksi", trxDate + " " + trxTime);
        return map;

    }

    public File createAndSavePdf(String nomorValidasi, Map<String, Object> parameter) {
        final String filename = nomorValidasi + ".pdf";
        final String pdfPath = reportFolder + FileSystems.getDefault().getSeparator() + filename;
        // read jasper file
        Resource resource = new ClassPathResource("jasper/bpih/report_resi_setoran_awal_bpih_r1.jasper");
        try {
            JasperReport jasperReport
                    = (JasperReport) JRLoader.loadObject(resource.getInputStream());
            JasperPrint jasperPrint
                    = JasperFillManager.fillReport(jasperReport, parameter, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return new File(pdfPath);
    }

    @Scheduled(cron = "${bpihUpload.scheduler.cron}")
    public void uploadDocumentOnSchedule() {
        logScheduler.info("Scheduler bpihUpload mulai pada : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        int uploadNull = 0;
        int rc00 = 0;
        int otherRc = 0;
        int loopCounter = 1;
        do {
            // get list validationNumber untuk trx yang sudah di print tp belum upload
            List<TransactionLog> allValidationNumbers = logRepository.findTodayValidationNumberNotUploaded();
            for (TransactionLog log : allValidationNumbers) {
                BpihUploadModel model = reUploadReport(log.getFieldValue());
                // log the model
                if (model == null) {
                    logScheduler.warn("Response is NULL for validation number: " + log.getFieldValue());
                    uploadNull++;
                } else if (StringUtils.equals(model.getRc(), "00")) {
                    //ini untuk block yang success
//                    logScheduler.info(model);
                    logScheduler.info("Response is 00 for validation number: " + log.getFieldValue());
                    rc00++;
                } else {
//                    logScheduler.warn(model);
                    logScheduler.warn("Response is " + model.getRc() + " for validation number: " + log.getFieldValue());
                    otherRc++;
                }
            }
            loopCounter++;
        } while (loopCounter <= 1);

        logScheduler.info("Scheduler bpihUpload response null : " + uploadNull + ", rc00 : " + rc00 + ", otherRc : " + otherRc);
        logScheduler.info("Scheduler bpihUpload selesai pada : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
