package com.multipolar.uiteller.backend.service.report;

import com.multipolar.uiteller.backend.model.report.pln.ReportPendapatanPlnNonTagihanListrik;
import com.multipolar.uiteller.backend.model.report.pln.ReportPendapatanPlnPostpaid;
import com.multipolar.uiteller.backend.model.report.pln.ReportPendapatanPlnPrepaid;
import com.multipolar.uiteller.backend.repository.ReportPlnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportPlnService {

    @Autowired
    private ReportPlnRepository reportPlnRepository;

    public List<ReportPendapatanPlnNonTagihanListrik> nonTaglistList(String startDate, String endDate, String jasperDetailLocation, String jasperCellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return reportPlnRepository.nontaglistList(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);
    }

    public List<ReportPendapatanPlnPostpaid> postpaidList(String startDate, String endDate, String detailLocation, String cellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.reportPlnRepository.postpaidList(startDate, endDate, detailLocation, cellLocation, sts, user, branch, teller, supervisi, admin);
    }

    public List<ReportPendapatanPlnPrepaid> prepaidList(String startDate, String endDate, String detailLocation, String cellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.reportPlnRepository.prepaidList(startDate, endDate, detailLocation, cellLocation, sts, user, branch, teller, supervisi, admin);
    }
}
