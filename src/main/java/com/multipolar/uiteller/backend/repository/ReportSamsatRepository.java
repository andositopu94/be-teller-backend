package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.report.samsat.ReportPendapatanSamsat;
import com.multipolar.uiteller.backend.model.report.samsat.ReportPendapatanSamsatDetails;
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
public class ReportSamsatRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private TransactionStatus serviceStatus;

    public List<ReportPendapatanSamsat> listPendapatan(final String startDate, final String endDate, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {

        String query = "";
        if (admin)
            query = "select BRANCH_CODE, STATUS_LOG from SAMSAT_TRANSACTION " +
                    serviceStatus.getSts(sts) +
                    " group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            query = "select BRANCH_CODE, STATUS_LOG from SAMSAT_TRANSACTION " +
                    serviceStatus.getSts(sts) +
                    " AND BRANCH_CODE = '" + branch + "' group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanSamsat> list = this.jdbcTemplate.query(query, new HashMap<String, Object>(), new RowMapper<ReportPendapatanSamsat>() {
            @Override
            public ReportPendapatanSamsat mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanSamsat samsat = new ReportPendapatanSamsat();
                String branchCode = rs.getString("BRANCH_CODE");
                String statusLog = rs.getString("STATUS_LOG");
                samsat.setDetails(listDetail(branchCode, statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return samsat;
            }
        });
        return list;
    }

    private List<ReportPendapatanSamsatDetails> listDetail(String branchCode, final String statusLog, String startDate, String endDate, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", statusLog);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, LOG_ID, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, ADDRESS, ADMIN_CHARGE, ARCHIVE_NUMBER, BEA_STNK, COLOR, CURRENCY_CODE, DATI_CODE, MACHINE_NUMBER, MERK, MODEL, NAME,NOMINAL, NUMBER_OF_PROGRAMS, OWNERSHIP, PAY_CODE, PKB_DENDA, PKB_POKOK, POLICE_NUMBER, PROGRESIF_DENDA, PROGRESIF_POKOK, REFF, REFERENCE_NUMBER, SOURCE_NUMBER, SWDLJJ_DENDA, SWDLJJ_POKOK, TAX_PERIOD, TOTAL, TRANSACTION_ID, TRANSACTION_TYPE, KTP from SAMSAT_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY PAY_CODE";
        else
            query = "select TRACE_NUMBER, LOG_ID, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, ADDRESS, ADMIN_CHARGE, ARCHIVE_NUMBER, BEA_STNK, COLOR, CURRENCY_CODE, DATI_CODE, MACHINE_NUMBER, MERK, MODEL, NAME,NOMINAL, NUMBER_OF_PROGRAMS, OWNERSHIP, PAY_CODE, PKB_DENDA, PKB_POKOK, POLICE_NUMBER, PROGRESIF_DENDA, PROGRESIF_POKOK, REFF, REFERENCE_NUMBER, SOURCE_NUMBER, SWDLJJ_DENDA, SWDLJJ_POKOK, TAX_PERIOD, TOTAL, TRANSACTION_ID, TRANSACTION_TYPE, KTP from SAMSAT_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY PAY_CODE";
        List<ReportPendapatanSamsatDetails> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanSamsatDetails>() {
            @Override
            public ReportPendapatanSamsatDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanSamsatDetails detailSamsat = new ReportPendapatanSamsatDetails();
                detailSamsat.setStatusTransaksi(statusLog.equalsIgnoreCase("0") ? "Berhasil" : "Gagal");
                detailSamsat.setChannel("Teller");
                detailSamsat.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                detailSamsat.setNama(rs.getString("NAME"));
                detailSamsat.setKodeBayar(rs.getString("PAY_CODE"));
                detailSamsat.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailSamsat.setNomorKtp(rs.getString("KTP"));
                detailSamsat.setTagihan(rs.getString("NOMINAL") != null ? rs.getBigDecimal("NOMINAL") : BigDecimal.ZERO);
                detailSamsat.setFee(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                detailSamsat.setTanggalTransaksi(rs.getDate("TRANSACTION_DATE"));
                detailSamsat.setUser(rs.getString("USER_ID"));
                return detailSamsat;
            }
        });
        return list;
    }
}
