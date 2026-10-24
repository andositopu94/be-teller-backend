package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.report.universitas.ReportPendapatanUangKuliah;
import com.multipolar.uiteller.backend.model.report.universitas.ReportPendapatanUangKuliahCells;
import com.multipolar.uiteller.backend.model.report.universitas.ReportPendapatanUangKuliahDetails;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PendapatanUangKuliahRepository {

    @Autowired
    private TransactionStatus statusService;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<ReportPendapatanUangKuliah> listPendapatanUangKuliah(final String startDate, final String endDate, final String jasperDetailLocation, final String jasperCellLocation, String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("sts", sts);
        String query = "select STATUS_LOG from UNIVERSITAS_TRANSACTION " +
                statusService.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanUangKuliah> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanUangKuliah>() {
            @Override
            public ReportPendapatanUangKuliah mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanUangKuliah uangKuliah = new ReportPendapatanUangKuliah();
                uangKuliah.setJasperLocation(jasperDetailLocation);
                String statusLog = rs.getString("STATUS_LOG");
                uangKuliah.setDetails(listDetailPendapatanUangKuliah(startDate, endDate, statusLog, jasperCellLocation, user, branch, teller, supervisi, admin));
                return uangKuliah;
            }
        });
        return list;
    }

    private List<ReportPendapatanUangKuliahDetails> listDetailPendapatanUangKuliah(final String startDate, final String endDate, final String status, final String jasperCellLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", status);
        String query = "";
        if (admin)
            query = "select BRANCH_CODE, STATUS_LOG from UNIVERSITAS_TRANSACTION where STATUS_LOG = :statusLog group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            query = "select BRANCH_CODE, STATUS_LOG from UNIVERSITAS_TRANSACTION where STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanUangKuliahDetails> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanUangKuliahDetails>() {
            @Override
            public ReportPendapatanUangKuliahDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanUangKuliahDetails detailUangKuliah = new ReportPendapatanUangKuliahDetails();
                detailUangKuliah.setJasperLocation(jasperCellLocation);
                detailUangKuliah.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailUangKuliah.setStatusTransaksi(statusService.getStatus(status));
                detailUangKuliah.setDetails(listCellPendapatanUangKuliah(startDate, endDate, detailUangKuliah.getKodeCabang(), status, user, branch, teller, supervisi, admin));
                return detailUangKuliah;
            }
        });
        return list;
    }

    private List<ReportPendapatanUangKuliahCells> listCellPendapatanUangKuliah(String startDate, String endDate, final String branchCode, String status, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, ADMIN_CHARGE, ARCHIVE_NUMBER, BILLING_CODE, SOURCE_NAME, NOMINAL, SOURCE_NUMBER, UNIVERSITY_NAME, CUSTOMER_ID, CUSTOMER_NAME, JURUSAN from UNIVERSITAS_TRANSACTION_DETAILS where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY CUSTOMER_ID";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, ADMIN_CHARGE, ARCHIVE_NUMBER, BILLING_CODE, SOURCE_NAME, NOMINAL, SOURCE_NUMBER, UNIVERSITY_NAME, CUSTOMER_ID, CUSTOMER_NAME, JURUSAN from UNIVERSITAS_TRANSACTION_DETAILS where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY CUSTOMER_ID";
        List<ReportPendapatanUangKuliahCells> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanUangKuliahCells>() {
            @Override
            public ReportPendapatanUangKuliahCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanUangKuliahCells cellUangKuliah = new ReportPendapatanUangKuliahCells();
                cellUangKuliah.setUser(rs.getString("USER_ID") != null ? rs.getString("USER_ID") : Constants.STRIP_CODE);
                cellUangKuliah.setNomorArsip(rs.getString("ARCHIVE_NUMBER") != null ? rs.getString("ARCHIVE_NUMBER") : Constants.STRIP_CODE);
                cellUangKuliah.setNomorPelanggan(rs.getString("BILLING_CODE") != null ? rs.getString("BILLING_CODE") : Constants.STRIP_CODE);
                cellUangKuliah.setNamaPelanggan(rs.getString("SOURCE_NAME") != null ? rs.getString("SOURCE_NAME") : Constants.STRIP_CODE);
                cellUangKuliah.setJumlah(rs.getString("NOMINAL") != null ? rs.getBigDecimal("NOMINAL") : BigDecimal.ZERO);
                cellUangKuliah.setBiaya(rs.getString("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                cellUangKuliah.setTanggalBayar(rs.getDate("TRANSACTION_DATE"));
                cellUangKuliah.setChannel("Teller");
                cellUangKuliah.setSourceNumber(rs.getString("SOURCE_NUMBER") != null ? rs.getString("SOURCE_NUMBER") : Constants.STRIP_CODE);
                cellUangKuliah.setUniversity(rs.getString("UNIVERSITY_NAME") != null ? rs.getString("UNIVERSITY_NAME") : Constants.STRIP_CODE);
                cellUangKuliah.setJurusan(rs.getString("JURUSAN") != null ? rs.getString("JURUSAN") : Constants.STRIP_CODE);
                cellUangKuliah.setBillingCode(rs.getString("CUSTOMER_ID") != null ? rs.getString("CUSTOMER_ID") : Constants.STRIP_CODE);
                cellUangKuliah.setNama(rs.getString("CUSTOMER_NAME") != null ? rs.getString("CUSTOMER_NAME") : Constants.STRIP_CODE);
                return cellUangKuliah;
            }
        });
        return list;
    }
}
