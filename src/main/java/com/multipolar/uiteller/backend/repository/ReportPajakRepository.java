package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.report.pajak.ReportPenerimaanKasDaerah;
import com.multipolar.uiteller.backend.model.report.pajak.ReportPenerimaanKasDaerahCells;
import com.multipolar.uiteller.backend.model.report.pajak.ReportPenerimaanKasDaerahDetails;
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
public class ReportPajakRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private TransactionStatus serviceStatus;

    public List<ReportPenerimaanKasDaerah> listPbbSts(final String startDate, final String endDate, final String detailLocation, final String cellLocation, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("sts", sts);
        String query = "select STATUS_LOG from PBB_STS_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by  STATUS_LOG";
        List<ReportPenerimaanKasDaerah> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPenerimaanKasDaerah>() {
            @Override
            public ReportPenerimaanKasDaerah mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPenerimaanKasDaerah kas = new ReportPenerimaanKasDaerah();
                String statusTransaksi = rs.getString("STATUS_LOG");
                kas.setJasperLocation(detailLocation);
                kas.setDetails(listDetailPbbSts(statusTransaksi, startDate, endDate, cellLocation, user, branch, teller, supervisi, admin));
                return kas;
            }
        });
        return list;
    }

    private List<ReportPenerimaanKasDaerahDetails> listDetailPbbSts(final String status, final String startDate, final String endDate, final String jasperLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", status);
        String query = "";
        if (admin)
            query = "select BRANCH_CODE, STATUS_LOG from PBB_STS_TRANSACTION where STATUS_LOG = :statusLog group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            query = "select BRANCH_CODE, STATUS_LOG from PBB_STS_TRANSACTION where STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPenerimaanKasDaerahDetails> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPenerimaanKasDaerahDetails>() {
            @Override
            public ReportPenerimaanKasDaerahDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPenerimaanKasDaerahDetails detailKas = new ReportPenerimaanKasDaerahDetails();
                detailKas.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailKas.setStatusTransaksi(serviceStatus.getStatus(status));
                detailKas.setJasperLocation(jasperLocation);
                detailKas.setDetails(listCellPbbSts(detailKas.getKodeCabang(), status, startDate, endDate, user, branch, teller, supervisi, admin));
                return detailKas;
            }
        });
        return list;
    }

    private List<ReportPenerimaanKasDaerahCells> listCellPbbSts(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, LOG_ID, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, ALAMAT, ARCHIVE_NUMBER, BIAYA_ADMIN, DENDA, JUMLAH_POKOK, MATA_ANGGARAN, MATA_UANG, NAMA_WP, NO_ARSIP, NO_SKP, NO_STS, NO_WP, NOMINAL, REFF, REKENING_SUMBER, REKENING_TUJUAN, SOURCE_NAME, URAIAN from  PBB_STS_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY NO_STS";
        else
            query = "select TRACE_NUMBER, LOG_ID, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, ALAMAT, ARCHIVE_NUMBER, BIAYA_ADMIN, DENDA, JUMLAH_POKOK, MATA_ANGGARAN, MATA_UANG, NAMA_WP, NO_ARSIP, NO_SKP, NO_STS, NO_WP, NOMINAL, REFF, REKENING_SUMBER, REKENING_TUJUAN, SOURCE_NAME, URAIAN from  PBB_STS_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY NO_STS";
        List<ReportPenerimaanKasDaerahCells> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPenerimaanKasDaerahCells>() {
            @Override
            public ReportPenerimaanKasDaerahCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPenerimaanKasDaerahCells cellKas = new ReportPenerimaanKasDaerahCells();
                cellKas.setTanggalBayar(rs.getDate("TRANSACTION_DATE"));
                cellKas.setChannel("Teller");
                cellKas.setNomorSts(rs.getString("NO_STS"));
                cellKas.setNamaWpWr(rs.getString("NAMA_WP"));
                cellKas.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                cellKas.setBiaya(rs.getBigDecimal("BIAYA_ADMIN") != null ? rs.getBigDecimal("BIAYA_ADMIN") : BigDecimal.ZERO);
                cellKas.setDenda(rs.getBigDecimal("DENDA") != null ? rs.getBigDecimal("DENDA") : BigDecimal.ZERO);
                cellKas.setPokok(rs.getBigDecimal("JUMLAH_POKOK") != null ? rs.getBigDecimal("JUMLAH_POKOK") : BigDecimal.ZERO);
                cellKas.setJumlah(BigDecimal.ZERO);
                cellKas.setUser(rs.getString("USER_ID"));
                return cellKas;
            }
        });
        return list;
    }
}
