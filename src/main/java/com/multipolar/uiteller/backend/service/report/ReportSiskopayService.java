package com.multipolar.uiteller.backend.service.report;

import com.multipolar.uiteller.backend.model.report.sisko.SiskohatPayment;
import com.multipolar.uiteller.backend.repository.ReportSiskopayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportSiskopayService {

    @Autowired
    private ReportSiskopayRepository reportSiskopayRepository;

    public List<SiskohatPayment> getRsl(String startDate, String endDate, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return reportSiskopayRepository.list(startDate, endDate, sts, user, branch, teller, supervisi, admin);
    }
}
