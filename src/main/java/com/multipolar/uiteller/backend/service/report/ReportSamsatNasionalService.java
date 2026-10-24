package com.multipolar.uiteller.backend.service.report;

import com.multipolar.uiteller.backend.model.report.samsatNasional.ReportPendapatanSamsatNasional;
import com.multipolar.uiteller.backend.repository.ReportSamsatNasionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportSamsatNasionalService {

    @Autowired
    private ReportSamsatNasionalRepository repository;

    public List<ReportPendapatanSamsatNasional> listPendapatan(String startDate, String endDate, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.repository.listPendapatan(startDate, endDate, sts, user, branch, teller, supervisi, admin);
    }
}
