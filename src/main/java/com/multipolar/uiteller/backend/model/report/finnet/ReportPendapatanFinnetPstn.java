package com.multipolar.uiteller.backend.model.report.finnet;

import java.util.ArrayList;
import java.util.List;

public class ReportPendapatanFinnetPstn {

    private String jasperLocation;
    private List<ReportPendapatanFinnetPstnDetails> details = new ArrayList<>();

    public String getJasperLocation() {
        return jasperLocation;
    }

    public void setJasperLocation(String jasperLocation) {
        this.jasperLocation = jasperLocation;
    }

    public List<ReportPendapatanFinnetPstnDetails> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPendapatanFinnetPstnDetails> details) {
        this.details = details;
    }
}
