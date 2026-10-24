package com.multipolar.uiteller.backend.service.report;

import com.multipolar.uiteller.backend.model.report.mpng2.BpjsPembayaran;
import com.multipolar.uiteller.backend.repository.ReportBpjsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportBpjsService {

    @Autowired
    private ReportBpjsRepository reportBpjsRepository;

    public List<BpjsPembayaran> getRsl(String startDate, String endDate, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return reportBpjsRepository.list(startDate, endDate, sts, user, branch, teller, supervisi, admin);
    }
}
