package com.multipolar.uiteller.backend.model.report.finnet;

import java.util.ArrayList;
import java.util.List;

public class ReportPendapatanFinnetSpeedy {

    private String jasperLocation;
    private List<ReportPendapatanFinnetSpeedyDetails> details = new ArrayList<>();

    public String getJasperLocation() {
        return jasperLocation;
    }

    public void setJasperLocation(String jasperLocation) {
        this.jasperLocation = jasperLocation;
    }

    public List<ReportPendapatanFinnetSpeedyDetails> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPendapatanFinnetSpeedyDetails> details) {
        this.details = details;
    }
}
