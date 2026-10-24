package com.multipolar.uiteller.backend.service.report;

import com.multipolar.uiteller.backend.model.report.pdam.ReportPendapatanPdam;
import com.multipolar.uiteller.backend.repository.ReportPdamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportPdamService {

    @Autowired
    private ReportPdamRepository reportRepository;

    public List<ReportPendapatanPdam> listTirtabulian(String startDate, String endDate, String detailLocation, String cellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.reportRepository.listTirtabulian(startDate, endDate, detailLocation, cellLocation, sts, user, branch, teller, supervisi, admin);
    }

    public List<ReportPendapatanPdam> listTirtauli(String startDate, String endDate, String detailLocation, String cellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.reportRepository.listTirtauli(startDate, endDate, detailLocation, cellLocation, sts, user, branch, teller, supervisi, admin);
    }

    public List<ReportPendapatanPdam> listTirtanadi(String startDate, String endDate, String detailLocation, String cellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.reportRepository.listTirtanadi(startDate, endDate, detailLocation, cellLocation, sts, user, branch, teller, supervisi, admin);
    }
    
    public List<ReportPendapatanPdam> listTirtaayumiDti(String startDate, String endDate, String detailLocation, String cellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.reportRepository.listTirtaayumiDti(startDate, endDate, detailLocation, cellLocation, sts, user, branch, teller, supervisi, admin);
    }
    
    public List<ReportPendapatanPdam> listTirtakualoDti(String startDate, String endDate, String detailLocation, String cellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.reportRepository.listTirtakualoDti(startDate, endDate, detailLocation, cellLocation, sts, user, branch, teller, supervisi, admin);
    }
    
    public List<ReportPendapatanPdam> listTirtatanjungDti(String startDate, String endDate, String detailLocation, String cellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.reportRepository.listTirtatanjungDti(startDate, endDate, detailLocation, cellLocation, sts, user, branch, teller, supervisi, admin);
    }

    public List<ReportPendapatanPdam> listTirtanadiInstallation(String startDate, String endDate, String detailLocation, String cellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.reportRepository.listTirtanadiInstallation(startDate, endDate, detailLocation, cellLocation, sts, user, branch, teller, supervisi, admin);
    }
    
    public List<ReportPendapatanPdam> listTirtaayumiDtiInstallation(String startDate, String endDate, String detailLocation, String cellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.reportRepository.listTirtaayumiDtiInstallation(startDate, endDate, detailLocation, cellLocation, sts, user, branch, teller, supervisi, admin);
    }
    
    public List<ReportPendapatanPdam> listTirtakualoDtiInstallation(String startDate, String endDate, String detailLocation, String cellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.reportRepository.listTirtakualoDtiInstallation(startDate, endDate, detailLocation, cellLocation, sts, user, branch, teller, supervisi, admin);
    }
    
    public List<ReportPendapatanPdam> listTirtatanjungDtiInstallation(String startDate, String endDate, String detailLocation, String cellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.reportRepository.listTirtatanjungDtiInstallation(startDate, endDate, detailLocation, cellLocation, sts, user, branch, teller, supervisi, admin);
    }

    public List<ReportPendapatanPdam> listTirtakualo(String startDate, String endDate, String detailLocation, String cellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.reportRepository.listTirtakualo(startDate, endDate, detailLocation, cellLocation, sts, user, branch, teller, supervisi, admin);
    }
}
