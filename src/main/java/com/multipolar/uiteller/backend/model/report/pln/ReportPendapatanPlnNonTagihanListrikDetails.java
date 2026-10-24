package com.multipolar.uiteller.backend.model.report.pln;

import java.util.ArrayList;
import java.util.List;

public class ReportPendapatanPlnNonTagihanListrikDetails {

    private String kodeCabang;
    private String jasperLocation;
    private String statusTransaksi;
    private List<ReportPendapatanPlnNonTagihanListrikCells> details = new ArrayList<>();

    public String getKodeCabang() {
        return kodeCabang;
    }

    public void setKodeCabang(String kodeCabang) {
        this.kodeCabang = kodeCabang;
    }

    public List<ReportPendapatanPlnNonTagihanListrikCells> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPendapatanPlnNonTagihanListrikCells> details) {
        this.details = details;
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
}
