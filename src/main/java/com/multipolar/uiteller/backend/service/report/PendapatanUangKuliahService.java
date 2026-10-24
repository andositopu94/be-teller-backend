package com.multipolar.uiteller.backend.service.report;

import com.multipolar.uiteller.backend.model.report.universitas.ReportPendapatanUangKuliah;
import com.multipolar.uiteller.backend.repository.PendapatanUangKuliahRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PendapatanUangKuliahService {

    @Autowired
    private PendapatanUangKuliahRepository repository;

    public List<ReportPendapatanUangKuliah> listPendapatanUangKuliah(String startDate, String endDate, String jasperDetailLocation, String jasperCellLocation, String sts, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        return this.repository.listPendapatanUangKuliah(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);
    }
}
