package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.CorrectionUpdate;
import com.multipolar.uiteller.backend.model.TransactionRequestLog;
import com.multipolar.uiteller.backend.service.logger.CorrectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class CorrectionUpdateController {

    @Autowired
    private CorrectionService correctionService;

    @GetMapping("correction-update-details/{ref}/{branch}")
    public CorrectionUpdate transactionRequestLogList(@PathVariable String ref, @PathVariable String branch) {
        List<TransactionRequestLog> transactionRequestLogs = correctionService.transactionRequestLogs(ref, branch);
        CorrectionUpdate correctionUpdate = null;
        if (transactionRequestLogs != null) {
            correctionUpdate = new CorrectionUpdate();
            correctionUpdate.setId(transactionRequestLogs.get(0).getCommonTransactionRequest().getId());
            correctionUpdate.setType(transactionRequestLogs.get(0).getTrxType());
            correctionUpdate.setResult(transactionRequestLogs);
        }
        return correctionUpdate;
    }

    @GetMapping("correction-update-flag/{id}")
    public ResponseEntity transactionRequestLogUpdate(@PathVariable String id) {
        return correctionService.updateFlag(id);
    }

    @GetMapping("forwarding-update-flag/{id}")
    public ResponseEntity forwardingTransactionRequestLogUpdate(@PathVariable String id) {
        return correctionService.updateForwardingFlag(id);
    }
}
