package com.multipolar.uiteller.backend.model.report.pln;

import java.util.ArrayList;
import java.util.List;

public class ReportPendapatanPlnPostpaidDetail {

    private String kodeCabang;
    private String transaksiStatus;
    private String jasperLocation;
    private List<ReportPendapatanPlnPostpaidCells> details = new ArrayList<>();

    public String getKodeCabang() {
        return kodeCabang;
    }

    public void setKodeCabang(String kodeCabang) {
        this.kodeCabang = kodeCabang;
    }

    public List<ReportPendapatanPlnPostpaidCells> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPendapatanPlnPostpaidCells> details) {
        this.details = details;
    }

    public String getTransaksiStatus() {
        return transaksiStatus;
    }

    public void setTransaksiStatus(String transaksiStatus) {
        this.transaksiStatus = transaksiStatus;
    }

    public String getJasperLocation() {
        return jasperLocation;
    }

    public void setJasperLocation(String jasperLocation) {
        this.jasperLocation = jasperLocation;
    }
}
