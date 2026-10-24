package com.multipolar.uiteller.backend.model.report.pdam;

import java.util.LinkedList;
import java.util.List;

public class ReportPendapatanPdam {

    private String jasperLocation;
    private List<ReportPendapatanPdamDetails> details = new LinkedList<>();

    public String getJasperLocation() {
        return jasperLocation;
    }

    public void setJasperLocation(String jasperLocation) {
        this.jasperLocation = jasperLocation;
    }

    public List<ReportPendapatanPdamDetails> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPendapatanPdamDetails> details) {
        this.details = details;
    }
}
