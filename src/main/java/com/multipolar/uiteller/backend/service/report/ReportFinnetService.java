package com.multipolar.uiteller.backend.service.report;

import com.multipolar.uiteller.backend.model.report.finnet.ReportPendapatanFinnetPstn;
import com.multipolar.uiteller.backend.model.report.finnet.ReportPendapatanFinnetSpeedy;
import com.multipolar.uiteller.backend.repository.ReportFinnetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportFinnetService {

    @Autowired
    private ReportFinnetRepository repository;

    public List<ReportPendapatanFinnetPstn> findByDateStartAndEndPstn(String jasperDetailsLocation, String jasperCellLocation, String start, String end, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return repository.listPstn(start, end, jasperDetailsLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);
    }

    public List<ReportPendapatanFinnetSpeedy> findByDateStartAndEndSpeedy(String startDate, String endDate, String detailLocation, String cellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return repository.listSpeedy(startDate, endDate, detailLocation, cellLocation, sts, user, branch, teller, supervisi, admin);
    }
}
