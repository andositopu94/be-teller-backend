package com.multipolar.uiteller.backend.service.iso;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class CustomerIdService {

    private static final Map<String, String> MAP_ID;

    static {
        MAP_ID = new HashMap<>();
        MAP_ID.put("tirtanadi", "customerId");
        MAP_ID.put("tirtaayumiDti", "customerId");
        MAP_ID.put("tirtakualoDti", "customerId");
        MAP_ID.put("tirtatanjungDti", "customerId");
        MAP_ID.put("tirtakualo", "customerId");
        MAP_ID.put("tirtabulian", "customerId");
        MAP_ID.put("tirtauli", "customerId");
        MAP_ID.put("installation", "customerId");
        MAP_ID.put("tirtaayumiDtiInstallation", "customerId");
        MAP_ID.put("tirtakualoDtiInstallation", "customerId");
        MAP_ID.put("tirtatanjungDtiInstallation", "customerId");
        MAP_ID.put("speedy", "customerId");
        MAP_ID.put("pbbInquiryAtm", "noSts");
        MAP_ID.put("djp", "billingCode");
        MAP_ID.put("djbc", "billingCode");
        MAP_ID.put("dja", "billingCode");
        MAP_ID.put("djpg3", "billingCode");
        MAP_ID.put("djbcg3", "billingCode");
        MAP_ID.put("djag3", "billingCode");
        MAP_ID.put("pstnInquiry", "phoneNumber");
        MAP_ID.put("speedyInquiry", "customerId");
        MAP_ID.put("bpjs", "customerId");
        MAP_ID.put("bpjsKerja", "customerId");
        MAP_ID.put("pln", "meterNumber");
        MAP_ID.put("nontaglist", "registrationNumber");
        MAP_ID.put("samsat", "payCode");
        MAP_ID.put("samsatNasional", "payCode");
        MAP_ID.put("token", "meterNumber");
        MAP_ID.put("bpihinitial", "sourceNumber");
        MAP_ID.put("bpihinitialPrint", "validationNumber");
        MAP_ID.put("bpihPayment", "porsiNumber");
        MAP_ID.put("bpihPaymentPrint", "porsiNumber");
    }

    public String customerId(String s){
        return MAP_ID.get(s);
    }

}
