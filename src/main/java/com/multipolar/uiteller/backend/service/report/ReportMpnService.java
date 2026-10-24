package com.multipolar.uiteller.backend.service.report;

import com.multipolar.uiteller.backend.model.report.mpng2.Mpng2AdminMonitoringPembayaran;
import com.multipolar.uiteller.backend.model.report.mpng2.Mpng2MonitoringPembayaran;
import com.multipolar.uiteller.backend.repository.ReportMpnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportMpnService {

    @Autowired
    private ReportMpnRepository reportMpnRepository;

    public List<Mpng2MonitoringPembayaran> getRsl(String startDate, String endDate, String settStartDate, String settEndDate, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return reportMpnRepository.list(startDate, endDate, settStartDate, settEndDate, sts, user, branch, teller, supervisi, admin);
    }

    public List<Mpng2MonitoringPembayaran> getRslDjp(String startDate, String endDate, String settStartDate, String settEndDate, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return reportMpnRepository.listDjp(startDate, endDate, settStartDate, settEndDate, sts, user, branch, teller, supervisi, admin);
    }

    public List<Mpng2AdminMonitoringPembayaran> getRslAdmin(String startDate, String endDate, String settStartDate, String settEndDate, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return reportMpnRepository.listAdmin(startDate, endDate, settStartDate, settEndDate, sts, user, branch, teller, supervisi, admin);
    }

    public List<Mpng2AdminMonitoringPembayaran> getRslDjpAdmin(String startDate, String endDate, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return reportMpnRepository.listDjpAdmin(startDate, endDate, sts, user, branch, teller, supervisi, admin);
    }
}
