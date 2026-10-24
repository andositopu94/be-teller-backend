package com.multipolar.uiteller.backend.model.report.finnet;

import java.util.ArrayList;
import java.util.List;

public class ReportPendapatanFinnetPstnDetails {

    private String kodeCabang;
    private String jasperLocation;
    private String statusTransaksi;
    private List<ReportPendapatanFinnetPstnCells> details = new ArrayList<>();

    public String getKodeCabang() {
        return kodeCabang;
    }

    public void setKodeCabang(String kodeCabang) {
        this.kodeCabang = kodeCabang;
    }

    public String getJasperLocation() {
        return jasperLocation;
    }

    public void setJasperLocation(String jasperLocation) {
        this.jasperLocation = jasperLocation;
    }

    public String getStatusTransaksi() {
        return statusTransaksi;
    }

    public void setStatusTransaksi(String statusTransaksi) {
        this.statusTransaksi = statusTransaksi;
    }

    public List<ReportPendapatanFinnetPstnCells> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPendapatanFinnetPstnCells> details) {
        this.details = details;
    }
}
