package com.multipolar.uiteller.backend.service.report;

import com.multipolar.uiteller.backend.model.report.mpng3.Mpng3AdminMonitoringPembayaran;
import com.multipolar.uiteller.backend.model.report.mpng3.Mpng3MonitoringPembayaran;
import com.multipolar.uiteller.backend.repository.ReportMpng3Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportMpng3Service {
    @Autowired
    private ReportMpng3Repository reportMpng3Repository;

    public List<Mpng3AdminMonitoringPembayaran> getRslAdmin(String startDate, String endDate, String settStartDate, String settEndDate, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return reportMpng3Repository.listAdmin(startDate, endDate, settStartDate, settEndDate, sts, user, branch, teller, supervisi, admin);
    }
}
