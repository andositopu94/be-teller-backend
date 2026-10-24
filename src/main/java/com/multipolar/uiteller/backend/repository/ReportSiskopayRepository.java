package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.report.sisko.SiskohatPayment;
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
public class ReportSiskopayRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private TransactionStatus serviceStatus;
    @Autowired
    private ReportService reportService;

    public List<SiskohatPayment> list(final String startDate, final String endDate, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BRANCH_NAME, SOURCE_NUMBER, JEMAAH_NAME, ARCHIVE_NUMBER, PORSI_NUMBER, NOMINAL from BPIH_PAYMENT_TRANSACTION_DETAIL where (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) and " + serviceStatus.getLog(sts) + " ORDER BY BRANCH_CODE, JEMAAH_NAME";
        else if (supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BRANCH_NAME, SOURCE_NUMBER, JEMAAH_NAME, ARCHIVE_NUMBER, PORSI_NUMBER, NOMINAL from BPIH_PAYMENT_TRANSACTION_DETAIL where (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) and " + serviceStatus.getLog(sts) + " and BRANCH_CODE = '" + branch + "' ORDER BY BRANCH_CODE, JEMAAH_NAME";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BRANCH_NAME, SOURCE_NUMBER, JEMAAH_NAME, ARCHIVE_NUMBER, PORSI_NUMBER, NOMINAL from BPIH_PAYMENT_TRANSACTION_DETAIL where (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) and " + serviceStatus.getLog(sts) + " and USER_ID = '" + user + "' ORDER BY BRANCH_CODE, JEMAAH_NAME";
        List<SiskohatPayment> list = this.jdbcTemplate.query(query, params, new RowMapper<SiskohatPayment>() {
            @Override
            public SiskohatPayment mapRow(ResultSet rs, int rowNum) throws SQLException {
                SiskohatPayment siskohatPayment = new SiskohatPayment();
                siskohatPayment.setTanggalTransaksi(reportService.format(rs.getString("TRANSACTION_DATE"), Constants.MODEL_REPORT_DATE));
                siskohatPayment.setCabang(rs.getString("BRANCH_CODE"));
                siskohatPayment.setChannel(Constants.TELLER_DEFINE);
                siskohatPayment.setNama(rs.getString("JEMAAH_NAME"));
                siskohatPayment.setNomorRekening(rs.getString("SOURCE_NUMBER"));
                siskohatPayment.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                siskohatPayment.setNomorPorsi(rs.getString("PORSI_NUMBER"));
                siskohatPayment.setTagihan(rs.getString("NOMINAL") != null ? rs.getBigDecimal("NOMINAL").divide(BigDecimal.valueOf(100)) : BigDecimal.ZERO);
                siskohatPayment.setFee(BigDecimal.ZERO);
                siskohatPayment.setUser(rs.getString("USER_ID"));
                return siskohatPayment;
            }
        });
        return list;
    }
}
