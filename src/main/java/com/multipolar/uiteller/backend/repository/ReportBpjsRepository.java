package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.report.mpng2.BpjsPembayaran;
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
public class ReportBpjsRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private TransactionStatus serviceStatus;
    @Autowired
    private ReportService reportService;

    public List<BpjsPembayaran> list(final String startDate, final String endDate, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin)
            query = "select TRACE_NUMBER, SOURCE_NAME, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, CUSTOMER_ID, NOMINAL, ADMIN_CHARGE, ARCHIVE_NUMBER, MONTH_NUMBER, BILLS_TOTAL, MEMBERS_COUNT, TRX_TYPE from BPJS_TRANSACTION_DETAIL where (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) and " + serviceStatus.getLog(sts) + " ORDER BY BRANCH_CODE, CUSTOMER_ID";
        else if (supervisi)
            query = "select TRACE_NUMBER, SOURCE_NAME, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, CUSTOMER_ID, NOMINAL, ADMIN_CHARGE, ARCHIVE_NUMBER, MONTH_NUMBER, BILLS_TOTAL, MEMBERS_COUNT, TRX_TYPE from BPJS_TRANSACTION_DETAIL where (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) and " + serviceStatus.getLog(sts) + " and BRANCH_CODE = '" + branch + "' ORDER BY BRANCH_CODE, CUSTOMER_ID";
        else
            query = "select TRACE_NUMBER, SOURCE_NAME, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, CUSTOMER_ID, NOMINAL, ADMIN_CHARGE, ARCHIVE_NUMBER, MONTH_NUMBER, BILLS_TOTAL, MEMBERS_COUNT, TRX_TYPE from BPJS_TRANSACTION_DETAIL where (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) and " + serviceStatus.getLog(sts) + " and USER_ID = '" + user + "' ORDER BY BRANCH_CODE, CUSTOMER_ID";
        List<BpjsPembayaran> list = this.jdbcTemplate.query(query, params, new RowMapper<BpjsPembayaran>() {
            @Override
            public BpjsPembayaran mapRow(ResultSet rs, int rowNum) throws SQLException {
                BpjsPembayaran bpjsPembayaran = new BpjsPembayaran();
                bpjsPembayaran.setTanggalBank(reportService.format(rs.getString("TRANSACTION_DATE"), Constants.MODEL_REPORT_DATE));
                bpjsPembayaran.setKodeCabang(rs.getString("BRANCH_CODE"));
                bpjsPembayaran.setCustomerId(rs.getString("CUSTOMER_ID"));
                bpjsPembayaran.setCustomerName(rs.getString("SOURCE_NAME"));
                bpjsPembayaran.setMemberCount(rs.getString("MEMBERS_COUNT"));
                bpjsPembayaran.setMontYear(rs.getString("MONTH_NUMBER"));
                bpjsPembayaran.setNominal(rs.getString("NOMINAL") != null ? rs.getBigDecimal("NOMINAL") : BigDecimal.ZERO);
                bpjsPembayaran.setAdmin(rs.getString("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                bpjsPembayaran.setPremi(rs.getString("BILLS_TOTAL") != null ? rs.getBigDecimal("BILLS_TOTAL") : BigDecimal.ZERO);
                bpjsPembayaran.setArchiveNumber(rs.getString("ARCHIVE_NUMBER"));
                bpjsPembayaran.setChannel(Constants.TELLER_DEFINE);
                bpjsPembayaran.setUser(rs.getString("USER_ID"));
                return bpjsPembayaran;
            }
        });
        return list;
    }
}
