package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.report.samsatNasional.ReportPendapatanSamsatNasional;
import com.multipolar.uiteller.backend.model.report.samsatNasional.ReportPendapatanSamsatNasionalDetails;
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
public class ReportSamsatNasionalRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private TransactionStatus serviceStatus;

    public List<ReportPendapatanSamsatNasional> listPendapatan(final String startDate, final String endDate, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {

        String query = "";
        if (admin)
            query = "select BRANCH_CODE, STATUS_LOG from SAMSAT_NASIONAL_TRANSACTION " +
                    serviceStatus.getSts(sts) +
                    " group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            query = "select BRANCH_CODE, STATUS_LOG from SAMSAT_NASIONAL_TRANSACTION " +
                    serviceStatus.getSts(sts) +
                    " AND BRANCH_CODE = '" + branch + "' group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanSamsatNasional> list = this.jdbcTemplate.query(query, new HashMap<String, Object>(), new RowMapper<ReportPendapatanSamsatNasional>() {
            @Override
            public ReportPendapatanSamsatNasional mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanSamsatNasional samsatNasional = new ReportPendapatanSamsatNasional();
                String branchCode = rs.getString("BRANCH_CODE");
                String statusLog = rs.getString("STATUS_LOG");
                samsatNasional.setDetails(listDetail(branchCode, statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return samsatNasional;
            }
        });
        return list;
    }

    private List<ReportPendapatanSamsatNasionalDetails> listDetail(String branchCode, final String statusLog, String startDate, String endDate, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", statusLog);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, LOG_ID, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, ADMIN_CHARGE, ARCHIVE_NUMBER, COLOR, CHASIS_NUMBER, CC, CURRENCY, MERK, MODEL, JENIS, TYPE, NAME, OWNERSHIP, PAY_CODE, PKB_DEN, PKB_POK, POLICE_NUMBER, PNBP_DEN, PNBP_POK, REF, SOURCE_NUMBER, SWD_DEN, SWD_POK, PARKIR, ADMIN_FEE, NOMINAL, TOTAL, TRANSACTION_ID, PAY_TYPE, NIK from SAMSAT_NASIONAL_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY PAY_CODE";
        else
            query = "select TRACE_NUMBER, LOG_ID, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, ADMIN_CHARGE, ARCHIVE_NUMBER, COLOR, CHASIS_NUMBER, CC, CURRENCY, MERK, MODEL, JENIS, TYPE, NAME, OWNERSHIP, PAY_CODE, PKB_DEN, PKB_POK, POLICE_NUMBER, PNBP_DEN, PNBP_POK, REF, SOURCE_NUMBER, SWD_DEN, SWD_POK, PARKIR, ADMIN_FEE, NOMINAL, TOTAL, TRANSACTION_ID, PAY_TYPE, NIK from SAMSAT_NASIONAL_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY PAY_CODE";
        List<ReportPendapatanSamsatNasionalDetails> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanSamsatNasionalDetails>() {
            @Override
            public ReportPendapatanSamsatNasionalDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanSamsatNasionalDetails detailSamsatNasional = new ReportPendapatanSamsatNasionalDetails();
                detailSamsatNasional.setStatusTransaksi(statusLog.equalsIgnoreCase("0") ? "Berhasil" : "Gagal");
                detailSamsatNasional.setChannel("Teller");
                detailSamsatNasional.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                detailSamsatNasional.setNama(rs.getString("NAME"));
                detailSamsatNasional.setKodeBayar(rs.getString("PAY_CODE"));
                detailSamsatNasional.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailSamsatNasional.setNomorKtp(rs.getString("NIK"));
                detailSamsatNasional.setPkbDenda(rs.getString("PKB_DEN") != null ? rs.getBigDecimal("PKB_DEN") : BigDecimal.ZERO);
                detailSamsatNasional.setPkbPokok(rs.getString("PKB_POK") != null ? rs.getBigDecimal("PKB_POK") : BigDecimal.ZERO);
                detailSamsatNasional.setPnbpDenda(rs.getString("PNBP_DEN") != null ? rs.getBigDecimal("PNBP_DEN") : BigDecimal.ZERO);
                detailSamsatNasional.setPnbpPokok(rs.getString("PNBP_POK") != null ? rs.getBigDecimal("PNBP_POK") : BigDecimal.ZERO);
                detailSamsatNasional.setSwdDenda(rs.getString("SWD_DEN") != null ? rs.getBigDecimal("SWD_DEN") : BigDecimal.ZERO);
                detailSamsatNasional.setSwdPokok(rs.getString("SWD_POK") != null ? rs.getBigDecimal("SWD_POK") : BigDecimal.ZERO);
                detailSamsatNasional.setParkir(rs.getString("PARKIR") != null ? rs.getBigDecimal("PARKIR") : BigDecimal.ZERO);
                detailSamsatNasional.setBillerFee(rs.getString("ADMIN_FEE") != null ? rs.getBigDecimal("ADMIN_FEE") : BigDecimal.ZERO);
                detailSamsatNasional.setTagihan(rs.getString("NOMINAL") != null ? rs.getBigDecimal("NOMINAL") : BigDecimal.ZERO);
                detailSamsatNasional.setFee(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                detailSamsatNasional.setTanggalTransaksi(rs.getDate("TRANSACTION_DATE"));
                detailSamsatNasional.setUser(rs.getString("USER_ID"));
                return detailSamsatNasional;
            }
        });
        return list;
    }
}

