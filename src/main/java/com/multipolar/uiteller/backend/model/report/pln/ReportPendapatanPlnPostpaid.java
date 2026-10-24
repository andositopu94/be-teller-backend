package com.multipolar.uiteller.backend.model.report.pln;

import java.util.ArrayList;
import java.util.List;

public class ReportPendapatanPlnPostpaid {

    private List<ReportPendapatanPlnPostpaidDetail> details = new ArrayList<>();
    private String jasperLocation;

    public List<ReportPendapatanPlnPostpaidDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPendapatanPlnPostpaidDetail> details) {
        this.details = details;
    }

    public String getJasperLocation() {
        return jasperLocation;
    }

    public void setJasperLocation(String jasperLocation) {
        this.jasperLocation = jasperLocation;
    }
}
