package com.multipolar.uiteller.backend.service.report;

import com.multipolar.uiteller.backend.model.report.pajak.ReportPenerimaanKasDaerah;
import com.multipolar.uiteller.backend.repository.ReportPajakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportPajakService {

    @Autowired
    private ReportPajakRepository repository;

    public List<ReportPenerimaanKasDaerah> listPbbSts(String startDate, String endDate, String detailLocation, String cellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.repository.listPbbSts(startDate, endDate, detailLocation, cellLocation, sts, user, branch, teller, supervisi, admin);
    }
}
