package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.*;
import com.multipolar.uiteller.backend.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class BifastController {

    @Autowired
    private BifastRepository bifastRepository;
    @Autowired
    private BifastCityCodeRepository bifastCityCodeRepository;
    @Autowired
    private BifastProvCodeRepository bifastProvCodeRepository;

    @Autowired
    private TransactionLogRepository logRepository;

    private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");

    @GetMapping("/bifast/allBank")
    public List<BifastBank> getAll() {
        return bifastRepository.findAll();
    }

    @GetMapping("/bifast/activeBank")
    public List<BifastBank> getActiveBank() {
        return bifastRepository.findByActive("0");
    }

    @PostMapping("/bifast/addBank")
    public List<BifastBank> addBank(@RequestBody BifastBank bifastBank) {
        bifastRepository.save(bifastBank);
        return bifastRepository.findAll();
    }

    @GetMapping("/bifast/allProv")
    public List<BifastProvCode> getAllProvCode() {return bifastProvCodeRepository.findAll();}

    @GetMapping("/bifast/allCity")
    public List<BifastCityCode> getAllCityCode() {return bifastCityCodeRepository.findAll();}

    @PostMapping("/bifast/updateBank")
    public List<BifastBank> updateBank(@RequestBody BifastBank bifastBank) {
        BifastBank old = bifastRepository.findOne(bifastBank.getId());
        old.setActive(bifastBank.getActive());
        old.setBankCode(bifastBank.getBankCode());
        old.setBankBIC(bifastBank.getBankBIC());
        old.setBankName(bifastBank.getBankName());
        old.setPlaceholder1(bifastBank.getPlaceholder1());
        old.setPlaceholder2(bifastBank.getPlaceholder2());
        old.setPlaceholder3(bifastBank.getPlaceholder3());
        bifastRepository.save(old);
        return bifastRepository.findAll();
    }

    @GetMapping("/bifast/updateStatus/{id}")
    public List<BifastBank> updateStatus(@PathVariable String id) {
        BifastBank old = bifastRepository.findByBankBIC(id);
        String newStatus = Objects.equals(old.getActive(), "0") ? "1" : "0";
        old.setActive(newStatus);
        bifastRepository.save(old);
        return bifastRepository.findAll();
    }

    @DeleteMapping("/bifast/delete/{id}")
    public List<BifastBank> delete(@PathVariable String id) {
        BifastBank old = bifastRepository.findByBankBIC(id);
        bifastRepository.delete(old);
        return bifastRepository.findAll();
    }

    @GetMapping("/bifast/reinquiryList")
    public List<Map<String, String>> getTransactionBifast() {
        System.out.println("MASUKKKKKKK");
        List<Map<String, String>> results = new ArrayList<>();

        //remove cutt off time
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        List<TransactionLog> logs = logRepository.findAllTrxBifast(username);
//        System.out.println("DATAINQ "+ logs);

        // Create a set to store unique trace numbers
        Set<String> uniqueTraceNumbers = new HashSet<>();
//        System.out.println("USERNAME : "+ username);

        for (TransactionLog log : logs) {
            Map<String, String> map = new HashMap<>();

            if (!uniqueTraceNumbers.contains(log.getTraceNumber())) {
                uniqueTraceNumbers.add(log.getTraceNumber());
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
//                map.put("ref", log.getFieldValue());

                results.add(map);
            }
        }

        return results;
    }
}
