package com.multipolar.uiteller.backend.service.report;

import com.multipolar.uiteller.backend.model.report.samsat.ReportPendapatanSamsat;
import com.multipolar.uiteller.backend.repository.ReportSamsatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportSamsatService {

    @Autowired
    private ReportSamsatRepository repository;

    public List<ReportPendapatanSamsat> listPendapatan(String startDate, String endDate, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.repository.listPendapatan(startDate, endDate, sts, user, branch, teller, supervisi, admin);
    }
}
