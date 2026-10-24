package com.multipolar.uiteller.backend.model.report.samsatNasional;

import java.util.ArrayList;
import java.util.List;

public class ReportPendapatanSamsatNasional {

    private List<ReportPendapatanSamsatNasionalDetails> details = new ArrayList<>();

    public List<ReportPendapatanSamsatNasionalDetails> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPendapatanSamsatNasionalDetails> details) {
        this.details = details;
    }
}
