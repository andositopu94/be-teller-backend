package com.multipolar.uiteller.backend.model.report.universitas;

import java.util.ArrayList;
import java.util.List;

public class ReportPendapatanUangKuliah {

    private List<ReportPendapatanUangKuliahDetails> details = new ArrayList<>();
    private String jasperLocation;

    public List<ReportPendapatanUangKuliahDetails> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPendapatanUangKuliahDetails> details) {
        this.details = details;
    }

    public String getJasperLocation() {
        return jasperLocation;
    }

    public void setJasperLocation(String jasperLocation) {
        this.jasperLocation = jasperLocation;
    }
}
