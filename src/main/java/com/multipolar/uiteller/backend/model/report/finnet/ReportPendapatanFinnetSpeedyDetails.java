package com.multipolar.uiteller.backend.model.report.finnet;

import java.util.ArrayList;
import java.util.List;

public class ReportPendapatanFinnetSpeedyDetails {

    private String kodeCabang;
    private String statusTransaksi;
    private String jasperLocation;
    private List<ReportPendapatanFinnetSpeedyCells> details = new ArrayList<>();

    public String getKodeCabang() {
        return kodeCabang;
    }

    public void setKodeCabang(String kodeCabang) {
        this.kodeCabang = kodeCabang;
    }

    public String getStatusTransaksi() {
        return statusTransaksi;
    }

    public void setStatusTransaksi(String statusTransaksi) {
        this.statusTransaksi = statusTransaksi;
    }

    public String getJasperLocation() {
        return jasperLocation;
    }

    public void setJasperLocation(String jasperLocation) {
        this.jasperLocation = jasperLocation;
    }

    public List<ReportPendapatanFinnetSpeedyCells> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPendapatanFinnetSpeedyCells> details) {
        this.details = details;
    }
}
