package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.report.sisko.SiskohatInitial;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.TransactionStatus;
import com.multipolar.uiteller.backend.service.report.ReportService;
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
public class ReportSiskoRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private TransactionStatus serviceStatus;
    @Autowired
    private ReportService reportService;

    public List<SiskohatInitial> list(final String startDate, final String endDate, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BRANCH_NAME, SOURCE_NUMBER, JEMAAH_NAME, ARCHIVE_NUMBER, CUSTOMER_ID, VALIDATION_NUMBER, NOMINAL from BPIH_SETORAN_TRANSACTION_DETAIL where (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) and " + serviceStatus.getLog(sts) + " ORDER BY BRANCH_CODE, JEMAAH_NAME";
        else if (supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BRANCH_NAME, SOURCE_NUMBER, JEMAAH_NAME, ARCHIVE_NUMBER, CUSTOMER_ID, VALIDATION_NUMBER, NOMINAL from BPIH_SETORAN_TRANSACTION_DETAIL where (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) and " + serviceStatus.getLog(sts) + " and BRANCH_CODE = '" + branch + "' ORDER BY BRANCH_CODE, JEMAAH_NAME";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BRANCH_NAME, SOURCE_NUMBER, JEMAAH_NAME, ARCHIVE_NUMBER, CUSTOMER_ID, VALIDATION_NUMBER, NOMINAL from BPIH_SETORAN_TRANSACTION_DETAIL where (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) and " + serviceStatus.getLog(sts) + " and USER_ID = '" + user + "' ORDER BY BRANCH_CODE, JEMAAH_NAME";
        List<SiskohatInitial> list = this.jdbcTemplate.query(query, params, new RowMapper<SiskohatInitial>() {
            @Override
            public SiskohatInitial mapRow(ResultSet rs, int rowNum) throws SQLException {
                SiskohatInitial siskohatInitial = new SiskohatInitial();
                siskohatInitial.setTanggalTransaksi(reportService.format(rs.getString("TRANSACTION_DATE"), Constants.MODEL_REPORT_DATE));
                siskohatInitial.setCabang(rs.getString("BRANCH_CODE"));
                siskohatInitial.setChannel(Constants.TELLER_DEFINE);
                siskohatInitial.setNama(rs.getString("JEMAAH_NAME"));
                siskohatInitial.setNomorRekening(rs.getString("SOURCE_NUMBER"));
                siskohatInitial.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                siskohatInitial.setNomorValidation(rs.getString("VALIDATION_NUMBER"));
                siskohatInitial.setTagihan(rs.getString("NOMINAL") != null ? rs.getBigDecimal("NOMINAL").divide(BigDecimal.valueOf(100)) : BigDecimal.ZERO);
                siskohatInitial.setFee(BigDecimal.ZERO);
                siskohatInitial.setUser(rs.getString("USER_ID"));
                return siskohatInitial;
            }
        });
        return list;
    }
}
