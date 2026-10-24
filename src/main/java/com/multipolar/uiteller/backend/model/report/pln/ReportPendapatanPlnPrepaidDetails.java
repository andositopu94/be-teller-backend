package com.multipolar.uiteller.backend.model.report.pln;

import java.util.ArrayList;
import java.util.List;

public class ReportPendapatanPlnPrepaidDetails {

    private String kodeCabang;
    private String transaksiStatus;
    private String statusTransaksi;
    private String jasperLocation;
    private List<ReportPendapatanPlnPrepaidCell> details = new ArrayList<>();


    public String getKodeCabang() {
        return kodeCabang;
    }

    public void setKodeCabang(String kodeCabang) {
        this.kodeCabang = kodeCabang;
    }

    public List<ReportPendapatanPlnPrepaidCell> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPendapatanPlnPrepaidCell> details) {
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

    public String getTransaksiStatus() {
        return transaksiStatus;
    }

    public void setTransaksiStatus(String transaksiStatus) {
        this.transaksiStatus = transaksiStatus;
    }
}
