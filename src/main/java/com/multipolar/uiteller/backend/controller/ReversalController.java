package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.CommonTransactionRequestLog;
import com.multipolar.uiteller.backend.model.ReverseHistory;
import com.multipolar.uiteller.backend.model.TransactionRequestLog;
import com.multipolar.uiteller.backend.repository.CommonTransactionRequestLogRepository;
import com.multipolar.uiteller.backend.repository.ReverseHistoryRepository;
import com.multipolar.uiteller.backend.resource.BusinessException;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.resource.Troubleshoot;
import com.multipolar.uiteller.backend.security.model.Reversal;
import com.multipolar.uiteller.backend.security.model.ReversalResponse;
import com.multipolar.uiteller.backend.service.MiddlewareGatewayServiceImpl;
import com.multipolar.uiteller.backend.service.MonitoringLogService;
import com.multipolar.uiteller.backend.service.logger.ForwardingLogService;
import com.multipolar.uiteller.backend.service.logger.TransactionLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/apiv2")
public class ReversalController {

    @Autowired
    private MiddlewareGatewayServiceImpl middlewareGatewayService;
    @Autowired
    private ForwardingLogService forwardingLogService;
    @Autowired
    private CommonTransactionRequestLogRepository trxRequestLogRepository;
    @Autowired
    private ReverseHistoryRepository historyRepository;
    @Autowired
    private TransactionLogService logService;
    @Autowired
    private MonitoringLogService monitoringLogService;

    @PostMapping("/reversal")
    public ReversalResponse reversal(@RequestBody Reversal reversal) throws BusinessException {
        TransactionRequestLog logs;
        logs = forwardingLogService.getRequestTransactionLog(Long.parseLong(reversal.getId()), Constants.REQUEST_TIMEOUT_FIELD_NAME);
        ReversalResponse reversalResponse = middlewareGatewayService.getReversalResponse(reversal.getId());
        if (reversalResponse == null)
            throw new IllegalArgumentException("Time-out exception, host give no response");
        forwardingLogService.setRevFlag(logs, reversalResponse);
        monitoringLogService.updateByCommonTransactionRequestLog(logs.getCommonTransactionRequest(), Troubleshoot.CORRECTION);
        try {
            saveHistory(reversal, reversalResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return reversalResponse;
    }

    private void saveHistory(Reversal reversal, ReversalResponse response) {
        CommonTransactionRequestLog commonRequestLog = trxRequestLogRepository.findOne(Long.parseLong(reversal.getId()));
        String teller = commonRequestLog.getUserid();
        List<TransactionRequestLog> trxLogs = commonRequestLog.getTransactionRequestLogs();
        String trace = null;
        if (trxLogs.size() > 0)
            trace = trxLogs.get(0).getTraceNumber();

        // find ref
        String ref = null;
        for (TransactionRequestLog log : trxLogs) {
            if (StringUtils.equals(log.getFieldName(), "ref"))
                ref = StringUtils.stripStart(log.getFieldValue(), "0");
        }

        if (StringUtils.isNotEmpty(trace) && StringUtils.isNotEmpty(ref)) {
            Long commonId = logService.findCommonIdFromTraceNumber(trace, ref);

            ReverseHistory history = new ReverseHistory();
            history.setTeller(teller);
            history.setTrace(trace);
            history.setMessage(response.getMessage());
            history.setRcode(response.getRCode());
            history.setCommonRequestId(commonRequestLog.getId());
            history.setCommonId(commonId);

            historyRepository.save(history);
        }
    }
}
