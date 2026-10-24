package com.multipolar.uiteller.backend.model.report.universitas;

import java.util.ArrayList;
import java.util.List;

public class ReportPendapatanInitUangKuliah {

    private List<ReportPendapatanInitUangKuliahDetails> details = new ArrayList<>();
    private String jasperLocation;

    public List<ReportPendapatanInitUangKuliahDetails> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPendapatanInitUangKuliahDetails> details) {
        this.details = details;
    }

    public String getJasperLocation() {
        return jasperLocation;
    }

    public void setJasperLocation(String jasperLocation) {
        this.jasperLocation = jasperLocation;
    }
}
