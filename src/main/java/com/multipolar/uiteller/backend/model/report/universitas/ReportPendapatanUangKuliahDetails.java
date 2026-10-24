package com.multipolar.uiteller.backend.model.report.universitas;

import java.util.ArrayList;
import java.util.List;

public class ReportPendapatanUangKuliahDetails {
    private String kodeCabang;
    private String statusTransaksi;
    private String jasperLocation;
    private List<ReportPendapatanUangKuliahCells> details = new ArrayList<>();

    public String getKodeCabang() {
        return kodeCabang;
    }

    public void setKodeCabang(String kodeCabang) {
        this.kodeCabang = kodeCabang;
    }

    public List<ReportPendapatanUangKuliahCells> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPendapatanUangKuliahCells> details) {
        this.details = details;
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
}
