package com.multipolar.uiteller.backend.model.report.pajak;

import java.util.LinkedList;
import java.util.List;

public class ReportPenerimaanKasDaerah {

    private String jasperLocation;
    private List<ReportPenerimaanKasDaerahDetails> details = new LinkedList<>();

    public String getJasperLocation() {
        return jasperLocation;
    }

    public void setJasperLocation(String jasperLocation) {
        this.jasperLocation = jasperLocation;
    }

    public List<ReportPenerimaanKasDaerahDetails> getDetails() {
        return details;
    }

    public void setDetails(List<ReportPenerimaanKasDaerahDetails> details) {
        this.details = details;
    }
}
