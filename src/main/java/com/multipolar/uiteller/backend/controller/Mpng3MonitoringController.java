package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.report.mpng3.Mpng3AdminMonitoringPembayaran;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.report.ReportMpng3Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/mpng3")
public class Mpng3MonitoringController {
    @Autowired
    private ReportMpng3Service reportMpng3Service;

    @GetMapping("/laporan/monitoring-pembayaran-admin")
    public List<Mpng3AdminMonitoringPembayaran> laporanAdminMonitoringPembayaran(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("settStartDate") String settStartDate,
            @RequestParam("settEndDate") String settEndDate,
            @RequestParam("user") String user,
            @RequestParam("branch") String branch,
            @RequestParam("teller") Boolean teller,
            @RequestParam("supervisi") Boolean supervisi,
            @RequestParam("admin") Boolean admin,
            @RequestParam("sts") String sts
    ){
        return reportMpng3Service.getRslAdmin(startDate, endDate, settStartDate, settEndDate, StringUtils.equalsIgnoreCase(Constants.REPORT_ALL, sts) ? Constants.REPORT_ALL_CODE : sts.toUpperCase(), user, branch, teller, supervisi, admin);
    }
}
