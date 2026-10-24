package com.multipolar.uiteller.backend.service.report;

import com.multipolar.uiteller.backend.model.report.sisko.SiskohatInitial;
import com.multipolar.uiteller.backend.repository.ReportSiskoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportSiskoService {

    @Autowired
    private ReportSiskoRepository reportSiskoRepository;

    public List<SiskohatInitial> getRsl(String startDate, String endDate, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return reportSiskoRepository.list(startDate, endDate, sts, user, branch, teller, supervisi, admin);
    }
}
