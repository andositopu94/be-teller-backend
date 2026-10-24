package com.multipolar.uiteller.backend.model.report.pln;

import java.util.ArrayList;
import java.util.List;

public class ReportPendapatanPlnPrepaid {

    private List<ReportPendapatanPlnPrepaidDetails> details = new ArrayList<>();
    private String jasperLocation;

    public List<ReportPendapatanPlnPrepaidDetails> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPendapatanPlnPrepaidDetails> details) {
        this.details = details;
    }

    public String getJasperLocation() {
        return jasperLocation;
    }

    public void setJasperLocation(String jasperLocation) {
        this.jasperLocation = jasperLocation;
    }
}
