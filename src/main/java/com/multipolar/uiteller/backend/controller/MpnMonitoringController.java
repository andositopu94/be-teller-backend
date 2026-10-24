package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.report.mpng2.Mpng2AdminMonitoringPembayaran;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.report.ReportMpnService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class MpnMonitoringController {

    @Autowired
    private ReportMpnService reportMpnService;

    @GetMapping("/laporan/monitoring-pembayaran-admin")
    public List<Mpng2AdminMonitoringPembayaran> laporanAdminMonitoringPembayaran(
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
    ) {
        System.out.println("MASUK SINI");
        return reportMpnService.getRslAdmin(startDate, endDate, settStartDate, settEndDate, StringUtils.equalsIgnoreCase(Constants.REPORT_ALL, sts) ? Constants.REPORT_ALL_CODE : sts.toUpperCase(), user, branch, teller, supervisi, admin);
    }
}
