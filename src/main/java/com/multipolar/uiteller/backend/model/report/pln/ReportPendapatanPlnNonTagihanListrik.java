package com.multipolar.uiteller.backend.model.report.pln;

import java.util.ArrayList;
import java.util.List;

public class ReportPendapatanPlnNonTagihanListrik {

    private String jasperLocation;
    private List<ReportPendapatanPlnNonTagihanListrikDetails> details = new ArrayList<>();

    public String getJasperLocation() {
        return jasperLocation;
    }

    public void setJasperLocation(String jasperLocation) {
        this.jasperLocation = jasperLocation;
    }

    public List<ReportPendapatanPlnNonTagihanListrikDetails> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPendapatanPlnNonTagihanListrikDetails> details) {
        this.details = details;
    }
}
