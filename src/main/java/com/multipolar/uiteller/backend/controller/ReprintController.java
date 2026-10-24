package com.multipolar.uiteller.backend.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.multipolar.uiteller.backend.model.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.multipolar.uiteller.backend.repository.TransactionLogRepository;
import com.multipolar.uiteller.backend.repository.UniversitasInitLogRepository;
import com.multipolar.uiteller.backend.repository.UniversitasLogRepository;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.KonversiMataUangKeTerbilang;

@RestController
@RequestMapping("/apiv2")
public class ReprintController {

    private static Logger logger = LoggerFactory.getLogger(ReprintController.class);

    @Autowired
    private TransactionLogRepository logRepository;

    @Autowired
    private UniversitasLogRepository univLogRepository;

    @Autowired
    private UniversitasInitLogRepository universitasInitLogRepository;

    private static final Map<String, String> MAP_ID;
    private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
    private static final String[] MPN_REINQUIRIES = {"djaReinquiry", "djpReinquiry", "djbcReinquiry", "djag3Reinquiry", "djpg3Reinquiry", "djbcg3Reinquiry"};
    private static final String[] MPN = {"djp", "dja", "djbc", "djpg3", "djag3", "djbcg3"};

    static {
        MAP_ID = new HashMap<>();
        MAP_ID.put("tirtanadi", "customerId");
        MAP_ID.put("tirtaayumiDti", "customerId");
        MAP_ID.put("tirtakualoDti", "customerId");
        MAP_ID.put("tirtatanjungDti", "customerId");
        MAP_ID.put("tirtakualo", "customerId");
        MAP_ID.put("tirtabulian", "customerId");
        MAP_ID.put("tirtauli", "customerId");
        MAP_ID.put("pdamDti", "customerId");
        MAP_ID.put("installation", "customerId");
        MAP_ID.put("tirtaayumiDtiInstallation", "customerId");
        MAP_ID.put("tirtakualoDtiInstallation", "customerId");
        MAP_ID.put("tirtatanjungDtiInstallation", "customerId");
        MAP_ID.put("pdamDtiInstallation", "customerId");
        MAP_ID.put("speedy", "customerId");
        MAP_ID.put("pbb", "noSts");
        MAP_ID.put("djp", "billingCode");
        MAP_ID.put("djbc", "billingCode");
        MAP_ID.put("dja", "billingCode");
        MAP_ID.put("djpg3", "billingCode");
        MAP_ID.put("djbcg3", "billingCode");
        MAP_ID.put("djag3", "billingCode");
        MAP_ID.put("pstn", "phoneNumber");
        MAP_ID.put("speedy", "customerId");
        MAP_ID.put("bpjs", "customerId");
        MAP_ID.put("bpjsKerja", "billNumber");
        MAP_ID.put("bpjsKerjaVa", "billNumber");
        MAP_ID.put("bpjsKerjaBpu", "nik");
        MAP_ID.put("pln", "meterNumber");
        MAP_ID.put("nontaglist", "registrationNumber");
        MAP_ID.put("samsat", "payCode");
        MAP_ID.put("samsatNasional", "payCode");
        MAP_ID.put("token", "meterNumber");
        MAP_ID.put("bpihinitial", "sourceNumber");
        MAP_ID.put("bpihinitialPrint", "validationNumber");
        MAP_ID.put("bpihPayment", "porsiNumber");
        MAP_ID.put("bpihPaymentPrint", "porsiNumber");
        MAP_ID.put("edupay", "kodeBilling");
        MAP_ID.put("eduinit", "identitas");
        MAP_ID.put("bpihinitialPrintDb", "validationNumber");
        MAP_ID.put("bpihPaymentPrintDb", "porsiNumber");
        MAP_ID.put("djpReinquiry", "billingCode");
        MAP_ID.put("djbcReinquiry", "billingCode");
        MAP_ID.put("djaReinquiry", "billingCode");
        MAP_ID.put("djpg3Reinquiry", "billingCode");
        MAP_ID.put("djbcg3Reinquiry", "billingCode");
        MAP_ID.put("djag3Reinquiry", "billingCode");
        MAP_ID.put("bifast", "destinationNumber");
    }

    @GetMapping("byDateBpihTrx")
    public List<Map<String, String>> getByDateBpihSetoranAwal(@RequestParam String date) throws ParseException {
        SimpleDateFormat formatedDate = new SimpleDateFormat("dd-MM-yyyy");
        Date newDate = formatedDate.parse(date);
//        Date formatedDate = LocalDate.parse(date);
        List<Map<String, String>> results = new ArrayList<>();
        List<TransactionLog> logs = logRepository.findByDate(newDate);
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
                if (StringUtils.equals(tl.getFieldName(), "sourceNumber"))
                    map.put("customerId", tl.getFieldValue());
            }

            // ambil nomor validasi
            String validationNumber = map.get("validationNumber");
            if (validationNumber != null) {
                TransactionLog pairLog =
                        logRepository.findFirstByFieldNameAndFieldValueAndTrxTypeOrderByIdDesc
                                ("validationNumber", validationNumber, Constants.INITIAL_PRINT);
                List<TransactionLog> printLogs = new ArrayList<>();
                if (pairLog != null)
                    printLogs = logRepository.findByCommonTransactionId(pairLog.getCommonTransaction().getId());

                String uploadFlag = null;
                for (TransactionLog printLog : printLogs) {
                    if (StringUtils.equals(printLog.getFieldName(), Constants.BPIH_UPLOAD_PDF_FIELD_NAME)) {
                        uploadFlag = printLog.getFieldValue();
                        break;
                    }
                }
                if (uploadFlag != null)
                    map.put("uploadFlag", uploadFlag);
            } else {
                map.put("uploadFlag", "0");
            }

            map.put("traceNumber", log.getTraceNumber());
            map.put("rc", commonLog.getRc());

            results.add(map);
        }

        return results;
    }

    @GetMapping("/todayBpihTrx")
    public List<Map<String, String>> getTodayBpihSetoranAwal() {
        List<Map<String, String>> results = new ArrayList<>();
        //trxType = bpihinitial

        List<TransactionLog> logs = logRepository.findAllTodayBpihSourceNumber();
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
                if (StringUtils.equals(tl.getFieldName(), "sourceNumber"))
                    map.put("customerId", tl.getFieldValue());
            }

            // ambil nomor validasi
            String validationNumber = map.get("validationNumber");
            if (validationNumber != null) {
                TransactionLog pairLog =
                        logRepository.findFirstByFieldNameAndFieldValueAndTrxTypeOrderByIdDesc
                                ("validationNumber", validationNumber, Constants.INITIAL_PRINT);
                List<TransactionLog> printLogs = new ArrayList<>();
                if (pairLog != null)
                    printLogs = logRepository.findByCommonTransactionId(pairLog.getCommonTransaction().getId());

                String uploadFlag = null;
                for (TransactionLog printLog : printLogs) {
                    if (StringUtils.equals(printLog.getFieldName(), Constants.BPIH_UPLOAD_PDF_FIELD_NAME)) {
                        uploadFlag = printLog.getFieldValue();
                        break;
                    }
                }
                if (uploadFlag != null)
                    map.put("uploadFlag", uploadFlag);
            } else {
                map.put("uploadFlag", "0");
            }

            map.put("traceNumber", log.getTraceNumber());
            map.put("rc", commonLog.getRc());

            results.add(map);
        }

        return results;
    }

    @GetMapping("/trxlogs")
    public List<Map<String, String>> getTransaction(@RequestParam String trxType, @RequestParam String customerId,
                                                    @RequestParam(name = "trace", required = false) String trace) {
        List<Map<String, String>> results = new ArrayList<>();

        // if pstn
        String areaCode = null, phoneNumber = null;
        if (StringUtils.equals(trxType, "pstn")) {
            areaCode = StringUtils.substring(customerId, 0, 4);
            customerId = StringUtils.substring(customerId, 4);
        }

        if (StringUtils.equals(trxType, Constants.INITIAL_PRINT))
            trxType = Constants.INITIAL_SISKO;
        else if (StringUtils.equals(trxType, Constants.REPAYMENT_PRINT))
            trxType = Constants.REPAYMENT_SISKO;
        
        if (StringUtils.equals(trxType, Constants.INITIAL_PRINT_DB))
            trxType = Constants.INITIAL_PRINT;
        else if (StringUtils.equals(trxType, Constants.REPAYMENT_PRINT_DB))
            trxType = Constants.REPAYMENT_PRINT;

        String customerKey = MAP_ID.get(trxType);

        List<TransactionLog> logs = null;
        if (StringUtils.isEmpty(trace))
            //logs = logRepository.findByFieldNameAndFieldValueAndTrxType(customerKey, customerId, trxType);
        if(StringUtils.equals(trxType, Constants.PDAMDTI)){
            String likeQuery = '%'+Constants.PDAMDTI+'%';
            logs = logRepository.findFirstByFieldNameAndFieldValueAndTrxTypeLike(customerKey, customerId, likeQuery);
        }else{
            logs = logRepository.findByFieldNameAndFieldValueAndTrxType(customerKey, customerId, trxType);}
        else
            logs = logRepository.findByFieldNameAndFieldValueAndTrxTypeAndTraceNumber(customerKey, customerId, trxType, trace);

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
            if (StringUtils.equals(trxType,"djpg3")||StringUtils.equals(trxType,"djag3")||StringUtils.equals(trxType,"djbcg3")){
                //get transaction time from transaction log
                if(map.containsKey("transactionTime")) {
                    String transactionTime = map.get("transactionTime").toString();
                    LocalTime localTime = DateTimeFormat.forPattern("HHmmss").parseLocalTime(transactionTime);
                    map.put("transactionDate", format.format(commonLog.getTrxDate()).toString().substring(0,11)+localTime.toString().substring(0,8));
                }


            }
            
            // hack for pstn only, remove if areaCode not same
            if (StringUtils.equals(trxType, "pstn") && areaCode != null && phoneNumber != null) {
                String areaCodeValue = map.get("areaCode");
                if (!StringUtils.equals(areaCode, areaCodeValue))
                    continue;
            }

            // hack for pembayaran universitas
            if (StringUtils.equals(trxType, "edupay")) {
                String archiveNumber = map.get("archiveNumber");
                // archiveNumber diinput dengan ref
                String ref = map.get("ref");
                if (archiveNumber == null)
                    logger.warn("Data archiveNumber tidak ditemukan untuk trace number: " + log.getTraceNumber());

//                addUniversitasLog(archiveNumber, map);
                addUniversitasLog(ref, map);
            }

            if (StringUtils.equals(trxType, "eduinit")) {
                String archiveNumber = map.get("archiveNumber");
                // archiveNumber diinput dengan ref
                String ref = map.get("ref");
                if (archiveNumber == null)
                    logger.warn("Data archiveNumber tidak ditemukan untuk trace number: " + log.getTraceNumber());

                //addUniversitasInitLog(archiveNumber, map);
                addUniversitasInitLog(ref, map);
            }

            // hack for bpih
            if (StringUtils.equals(trxType, Constants.INITIAL_SISKO)) {
                // ambil nomor validasi
                String validationNumber = map.get("validationNumber");
                if (validationNumber != null) {
                    TransactionLog pairLog =
                            logRepository.findFirstByFieldNameAndFieldValueAndTrxTypeOrderByIdDesc
                                    ("validationNumber", validationNumber, Constants.INITIAL_PRINT);
                    List<TransactionLog> printLogs = new ArrayList<>();
                    if (pairLog != null)
                        printLogs = logRepository.findByCommonTransactionId(pairLog.getCommonTransaction().getId());

                    String uploadFlag = null;
                    for (TransactionLog printLog : printLogs) {
                        if (StringUtils.equals(printLog.getFieldName(), Constants.BPIH_UPLOAD_PDF_FIELD_NAME)) {
                            uploadFlag = printLog.getFieldValue();
                            break;
                        }
                    }
                    if (uploadFlag != null)
                        map.put("uploadFlag", uploadFlag);
                } else {
                    map.put("uploadFlag", "0");
                }
            }

            map.put("traceNumber", log.getTraceNumber());
            map.put("rc", commonLog.getRc());

            // put trxType to diff cetak mpn
            if(StringUtils.equalsAny(trxType, MPN)){
                map.put("trxType", log.getTrxType());
            }

            // put id as customer id to show on front end
            if (!map.containsKey("customerId")) {
                if (StringUtils.equals(trxType, "pstn"))
                    map.put("customerId", map.get("areaCode") + " - " + map.get(customerKey));
                else
                    map.put("customerId", map.get(customerKey));
            }

            results.add(map);
        }

        return results;
    }

    @GetMapping("/mpnTrxlogs")
    public List<Map<String, String>> getMpnTransaction(@RequestParam String trxType, @RequestParam String customerId) {
    	boolean reinquiry = StringUtils.equalsAny(trxType, MPN_REINQUIRIES);
    	if (reinquiry)
    		trxType = StringUtils.replace(trxType, "Reinquiry", "");

    	List<Map<String, String>> results = getTransaction(trxType, customerId, "");

        // hack cetak bpn mpn g3
        switch (trxType) {
            case "djpg3":
                results.addAll(getTransaction("djp", customerId, ""));
                break;
            case "djag3":
                results.addAll(getTransaction("dja", customerId, ""));
                break;
            case "djbcg3":
                results.addAll(getTransaction("djbc", customerId, ""));
                break;
        }

    	for (Iterator<Map<String, String>> iter = results.iterator(); iter.hasNext();) {
    		Map<String, String> map = iter.next();
    		if (reinquiry && !map.containsKey("reinquiry")) {
    			iter.remove();
    			continue;
    		}
    		
  /*  		if (!reinquiry && map.containsKey("reinquiry")) {
    			iter.remove();
    			continue;
    		}*/
    		
    		if (!StringUtils.equals(map.get("rc"), "00")) {
    			iter.remove();
    			continue;
    		}
    		
    	}
    	return results;
    }

    @GetMapping("/mpng3Trxlogs")
    public List<Map<String, String>> getMpng3Transaction(@RequestParam String trxType, @RequestParam String customerId) {
        boolean reinquiry = StringUtils.equalsAny(trxType, MPN_REINQUIRIES);
        if (reinquiry)
            trxType = StringUtils.replace(trxType, "Reinquiry", "");

        List<Map<String, String>> results = getTransaction(trxType, customerId, "");

        // hack cetak bpn mpn g3
        switch (trxType) {
            case "djpg3":
                results.addAll(getTransaction("djp", customerId, ""));
                break;
            case "djag3":
                results.addAll(getTransaction("dja", customerId, ""));
                break;
            case "djbcg3":
                results.addAll(getTransaction("djbc", customerId, ""));
                break;
        }

        for (Iterator<Map<String, String>> iter = results.iterator(); iter.hasNext();) {
            Map<String, String> map = iter.next();
            if (reinquiry && !map.containsKey("reinquiry")) {
                iter.remove();
                continue;
            }

//            if (!reinquiry && map.containsKey("reinquiry")) {
//                iter.remove();
//                continue;
//            }

            if (!StringUtils.equals(map.get("rc"), "00")) {
                iter.remove();
                continue;
            }

        }
        return results;
    }
    private void addUniversitasInitLog(String archiveNumber, Map<String, String> map) {
        BigDecimal nominal = map.get("nominal") == null ? new BigDecimal("0") : new BigDecimal(map.get("nominal").toString());
        String charge = map.get("adminCharge") != null ? map.get("adminCharge") : "0";
        BigDecimal adminCharge = !StringUtils.isEmpty(charge) ? new BigDecimal(charge) : new BigDecimal("0");
        String terbilang = new KonversiMataUangKeTerbilang(nominal.add(adminCharge)).toString();
        map.put("terbilang", terbilang);

        List<UniversitasInitLog> univLogs = universitasInitLogRepository.findByArchiveNumber(archiveNumber);
        int index61 = 1;
        int index48 = 17;
        for (UniversitasInitLog log : univLogs) {
            int bit = log.getBit();
            if (bit == 61) {
                map.put("label" + index61, log.getFieldName());
                map.put("value" + index61, log.getFieldValue1());
                index61++;
            } else if (bit == 48) {
                int index = 0;
                map.put("label" + String.valueOf(index48) + index++, log.getFieldName());
                map.put("value" + String.valueOf(index48) + index++, log.getFieldValue1());
                map.put("value" + String.valueOf(index48) + index, log.getFieldValue2());
                index48++;
            }
        }
    }

    // reconstruct untuk front-end
    private void addUniversitasLog(String archiveNumber, Map<String, String> map) {
        BigDecimal nominal = map.get("nominal") == null ? new BigDecimal("0") : new BigDecimal(map.get("nominal").toString());
        String charge = map.get("adminCharge") != null ? map.get("adminCharge") : "0";
        BigDecimal adminCharge = !StringUtils.isEmpty(charge) ? new BigDecimal(charge) : new BigDecimal("0");
        String terbilang = new KonversiMataUangKeTerbilang(nominal.add(adminCharge)).toString();
        map.put("terbilang", terbilang);

        List<UniversitasLog> univLogs = univLogRepository.findByArchiveNumber(archiveNumber);
        int index61 = 1;
        int index48 = 17;
        for (UniversitasLog log : univLogs) {
            int bit = log.getBit();
            if (bit == 61) {
                if(log.getFieldName() != null) {
                    map.put("label" + index61, log.getFieldName());
                }
                if(log.getFieldValue1() != null) {
                    map.put("value" + index61, log.getFieldValue1());
                }
                index61++;
            } else if (bit == 48) {
                int index = 0;
                if(log.getFieldName() != null) {
                    map.put("label" + String.valueOf(index48) + index++, log.getFieldName());
                }
                if(log.getFieldValue1() != null) {
                    map.put("value" + String.valueOf(index48) + index++, log.getFieldValue1());
                }
                if(log.getFieldValue2() != null) {
                    map.put("value" + String.valueOf(index48) + index, log.getFieldValue2());
                }
                index48++;
            }
        }
    }
}
