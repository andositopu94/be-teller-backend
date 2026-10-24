package com.multipolar.uiteller.backend.model.report.samsat;

import java.util.ArrayList;
import java.util.List;

public class ReportPendapatanSamsat {

    private List<ReportPendapatanSamsatDetails> details = new ArrayList<>();

    public List<ReportPendapatanSamsatDetails> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPendapatanSamsatDetails> details) {
        this.details = details;
    }
}
