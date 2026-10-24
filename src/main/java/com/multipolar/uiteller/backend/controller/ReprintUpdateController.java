package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.ReprintUpdate;
import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.model.UniversitasInitLog;
import com.multipolar.uiteller.backend.model.UniversitasLog;
import com.multipolar.uiteller.backend.repository.UniversitasInitLogRepository;
import com.multipolar.uiteller.backend.repository.UniversitasLogRepository;
import com.multipolar.uiteller.backend.service.logger.ReprintCorrectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class ReprintUpdateController {

    @Autowired
    private ReprintCorrectionService reprintCorrectionService;

    @Autowired
    private UniversitasLogRepository universitasLogRepository;

    @Autowired
    private UniversitasInitLogRepository universitasInitLogRepository;

    @GetMapping("reprint-update-details/{ref}/{archive}")
    public ReprintUpdate transactionRequestLogList(@PathVariable String ref, @PathVariable String archive) {
        List<TransactionLog> transactionLogs = reprintCorrectionService.transactionRequestLogs(ref, archive);
        ReprintUpdate reprintUpdate = null;
        if (transactionLogs != null) {
            reprintUpdate = new ReprintUpdate();
            reprintUpdate.setId(transactionLogs.get(0).getCommonTransaction().getId());
            reprintUpdate.setType(transactionLogs.get(0).getTrxType());
            reprintUpdate.setTrace(transactionLogs.get(0).getTraceNumber());
            edupayAndEduinitHack(ref, transactionLogs, reprintUpdate);
            reprintUpdate.setResult(transactionLogs);
        }
        return reprintUpdate;
    }

    @GetMapping("mpnTransaction")
    public ReprintUpdate findMpnTrxFromTraceAndDate(@RequestParam String trace, @RequestParam String trxDate) {
        // trxDate format: DDMMYYYY
        List<TransactionLog> transactionLogs = reprintCorrectionService.findTransactionByTraceAndDate(trace, trxDate);
        ReprintUpdate reprintUpdate = null;
        if (transactionLogs != null) {
            reprintUpdate = new ReprintUpdate();
            reprintUpdate.setId(transactionLogs.get(0).getCommonTransaction().getId());
            reprintUpdate.setType(transactionLogs.get(0).getTrxType());
            reprintUpdate.setTrace(transactionLogs.get(0).getTraceNumber());
            reprintUpdate.setResult(transactionLogs);
        }
        return reprintUpdate;
    }

    @PostMapping("reprint-update-flag-admin")
    public ResponseEntity transactionRequestLogAdminUpdate(@RequestBody ReprintUpdate reprintUpdate) {
        return reprintCorrectionService.updateFlagAdmin(reprintUpdate);
    }

    @PostMapping("reprint-update-flag")
    public ResponseEntity transactionRequestLogUpdate(@RequestBody ReprintUpdate reprintUpdate) {
        return reprintCorrectionService.updateFlag(reprintUpdate);
    }

    private void edupayAndEduinitHack(@PathVariable String archive, List<TransactionLog> transactionLogs, ReprintUpdate reprintUpdate) {
        if (reprintUpdate.getType().equals("edupay")) {
            TransactionLog mahasiswaName = new TransactionLog();
            mahasiswaName.setAlias("Nama Mahasiswa");
            mahasiswaName.setCommonTransaction(transactionLogs.get(0).getCommonTransaction());
            mahasiswaName.setFieldName("namaMahasiswa");
            List<UniversitasLog> universitasLogList = universitasLogRepository.findByArchiveNumber(archive);
            String namaMahasiswa = "";
            for (UniversitasLog universitasLog : universitasLogList) {
                if (universitasLog.getFieldName().equalsIgnoreCase("Nama")) {
                    namaMahasiswa = (universitasLog.getFieldValue1().isEmpty() || universitasLog.getFieldValue1() == null) ? universitasLog.getFieldValue2() : universitasLog.getFieldValue1();
                }
            }
            mahasiswaName.setFieldValue(namaMahasiswa);
            mahasiswaName.setTraceNumber(transactionLogs.get(0).getTraceNumber());
            mahasiswaName.setTrxType(transactionLogs.get(0).getTrxType());
            transactionLogs.add(mahasiswaName);
        } if (reprintUpdate.getType().equalsIgnoreCase("eduinit")){
            TransactionLog mahasiswaName = new TransactionLog();
            mahasiswaName.setAlias("Nama Mahasiswa");
            mahasiswaName.setCommonTransaction(transactionLogs.get(0).getCommonTransaction());
            mahasiswaName.setFieldName("namaMahasiswa");
            List<UniversitasInitLog> universitasInitLogs = universitasInitLogRepository.findByArchiveNumber(archive);
            String namaMahasiswa = "";
            for (UniversitasInitLog universitasInitLog : universitasInitLogs) {
                if (universitasInitLog.getFieldName().equalsIgnoreCase("Nama")) {
                    namaMahasiswa = (universitasInitLog.getFieldValue1().isEmpty() || universitasInitLog.getFieldValue1() == null) ? universitasInitLog.getFieldValue2() : universitasInitLog.getFieldValue1();
                }
            }
            mahasiswaName.setFieldValue(namaMahasiswa);
            mahasiswaName.setTraceNumber(transactionLogs.get(0).getTraceNumber());
            mahasiswaName.setTrxType(transactionLogs.get(0).getTrxType());
            transactionLogs.add(mahasiswaName);
        }
    }
}
