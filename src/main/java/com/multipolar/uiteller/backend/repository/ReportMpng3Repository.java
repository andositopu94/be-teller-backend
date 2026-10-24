package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.report.mpng2.Mpng2AdminMonitoringPembayaran;
import com.multipolar.uiteller.backend.model.report.mpng3.Mpng3AdminMonitoringPembayaran;
import com.multipolar.uiteller.backend.model.report.mpng3.Mpng3MonitoringPembayaran;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.TransactionStatus;
import com.multipolar.uiteller.backend.service.report.ReportService;
import org.apache.commons.lang3.StringUtils;
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
public class ReportMpng3Repository {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    TransactionStatus serviceStatus;
    @Autowired
    private ReportService reportService;

    public List<Mpng3MonitoringPembayaran> list(final String startDate, final String endDate, final String settStartDate, final String settEndDate, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin){
        Map<String, Object> params = new HashMap<>();
        params.put("start", startDate);
        params.put("end", endDate);
        params.put("settStart", settStartDate);
        params.put("settEnd", settEndDate);
        System.out.println("STS"+ sts);
        String query = "";
        if(admin)
            query = "select TRACE_NUMBER, MESSAGE, BRANCH_CODE, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP, TAX_ADDRES, COUNT_DETAIL from MPNG3_TRANSACTION_DETAIL where ((TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) OR (TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY') BETWEEN TO_DATE(:settStart, 'DDMMYYYYhh24miss') and TO_DATE(:settEnd, 'DDMMYYYYhh24miss'))) and STS LIKE '" + sts + "' and BRANCH_CODE LIKE '%" + branch + "%' ORDER BY BRANCH_CODE, BILLING_CODE";
        else if (supervisi)
            query = "select TRACE_NUMBER, MESSAGE, BRANCH_CODE, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP, TAX_ADDRES, COUNT_DETAIL from MPNG3_TRANSACTION_DETAIL where ((TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) OR (TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY') BETWEEN TO_DATE(:settStart, 'DDMMYYYYhh24miss') and TO_DATE(:settEnd, 'DDMMYYYYhh24miss'))) and STS LIKE '" + sts + "' and BRANCH_CODE = '" + branch + "' ORDER BY BRANCH_CODE, BILLING_CODE";
        else
            query = "select TRACE_NUMBER, MESSAGE, BRANCH_CODE, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP, TAX_ADDRES, COUNT_DETAIL from MPNG3_TRANSACTION_DETAIL where ((TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) OR (TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY') BETWEEN TO_DATE(:settStart, 'DDMMYYYYhh24miss') and TO_DATE(:settEnd, 'DDMMYYYYhh24miss'))) and STS LIKE '" + sts + "' and USER_ID = '" + user + "' ORDER BY BRANCH_CODE, BILLING_CODE";
        List<Mpng3MonitoringPembayaran> list = this.jdbcTemplate.query(query, params, new RowMapper<Mpng3MonitoringPembayaran>() {
            @Override
            public Mpng3MonitoringPembayaran mapRow(ResultSet rs, int rowNum) throws SQLException {
                Mpng3MonitoringPembayaran mpng3MonitoringPembayaran = new Mpng3MonitoringPembayaran();
                mpng3MonitoringPembayaran.setTanggalBank(reportService.format(rs.getString("TRANSACTION_DATE"), Constants.MODEL_REPORT_DATE));
                mpng3MonitoringPembayaran.setTanggalBuku(reportService.format(rs.getString("SETTLEMENT_DATE"), Constants.MODEL_REPORT_SETTLEMENT_DATE));
                mpng3MonitoringPembayaran.setKodeCabang(rs.getString("BRANCH_CODE"));
                mpng3MonitoringPembayaran.setKodeUser(rs.getString("USER_ID"));
                mpng3MonitoringPembayaran.setKodeBilling(rs.getString("BILLING_CODE"));
                mpng3MonitoringPembayaran.setNama(StringUtils.equals(rs.getString("TRX_TYPE"), Constants.MPN_DJP) || StringUtils.equals(rs.getString("TRX_TYPE"), Constants.MPN_DJP_REINQUIRY) ? rs.getString("TAX_NAME") : rs.getString("CUSTOMER_NAME"));
                mpng3MonitoringPembayaran.setNtpn(rs.getString("NTPN") == null ? Constants.DEFAULT_REPORT : rs.getString("NTPN"));
                mpng3MonitoringPembayaran.setJumlah(rs.getString("NOMINAL") != null ? rs.getBigDecimal("NOMINAL") : BigDecimal.ZERO);
                mpng3MonitoringPembayaran.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                mpng3MonitoringPembayaran.setChannel(Constants.TELLER_DEFINE);
                mpng3MonitoringPembayaran.setUser(rs.getString("USER_ID"));
                mpng3MonitoringPembayaran.setNtb(rs.getString("NTB"));
                mpng3MonitoringPembayaran.setStan(rs.getString("TRACE_NUMBER"));
                mpng3MonitoringPembayaran.setStatus(rs.getString("STS"));
                mpng3MonitoringPembayaran.setNpwp(rs.getString("NPWP") == null ? Constants.DEFAULT_REPORT : rs.getString("NPWP"));
                mpng3MonitoringPembayaran.setAddress(rs.getString("TAX_ADDRES"));
                mpng3MonitoringPembayaran.setTaxType(rs.getString("TRX_TYPE"));
                mpng3MonitoringPembayaran.setCountDetail(rs.getString("COUNT_DETAIL"));
                mpng3MonitoringPembayaran.setMessage(rs.getString("MESSAGE"));
                return mpng3MonitoringPembayaran;
            }
        });
        return list;
    }
    public List<Mpng3AdminMonitoringPembayaran> listAdmin(final String startDate, final String endDate, final String settStartDate, final String settEndDate, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", startDate);
        params.put("end", endDate);
        params.put("settStart", settStartDate);
        params.put("settEnd", settEndDate);
        String query = "";
        if (admin)
            query = "select TRACE_NUMBER, MESSAGE, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP from MPNG3_TRANSACTION_DETAIL where ((TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) OR (TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY') BETWEEN TO_DATE(:settStart, 'DDMMYYYYhh24miss') and TO_DATE(:settEnd, 'DDMMYYYYhh24miss'))) and STS LIKE '" + sts + "' and BRANCH_CODE LIKE '%" + branch + "%' ORDER BY BRANCH_CODE, BILLING_CODE";
        else if (supervisi)
            query = "select TRACE_NUMBER, MESSAGE, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP from MPNG3_TRANSACTION_DETAIL where ((TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) OR (TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY') BETWEEN TO_DATE(:settStart, 'DDMMYYYYhh24miss') and TO_DATE(:settEnd, 'DDMMYYYYhh24miss'))) and STS LIKE '" + sts + "' and BRANCH_CODE = '" + branch + "' ORDER BY BRANCH_CODE, BILLING_CODE";
        else
            query = "select TRACE_NUMBER, MESSAGE, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP from MPNG3_TRANSACTION_DETAIL where ((TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) OR (TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY') BETWEEN TO_DATE(:settStart, 'DDMMYYYYhh24miss') and TO_DATE(:settEnd, 'DDMMYYYYhh24miss'))) and STS LIKE '" + sts + "' and USER_ID = '" + user + "' ORDER BY BRANCH_CODE, BILLING_CODE";
        System.out.println("START : "+ startDate);
        System.out.println("END : "+ endDate);
        System.out.println("Set Start : "+ settStartDate);
        System.out.println("Set End : "+ settEndDate);
        System.out.println("QUERY : "+ query);
        List<Mpng3AdminMonitoringPembayaran> list = this.jdbcTemplate.query(query, params, new RowMapper<Mpng3AdminMonitoringPembayaran>() {
            @Override
            public Mpng3AdminMonitoringPembayaran mapRow(ResultSet rs, int rowNum) throws SQLException {
                Mpng3AdminMonitoringPembayaran mpng3MonitoringPembayaran = new Mpng3AdminMonitoringPembayaran();
                mpng3MonitoringPembayaran.setTanggalBank(rs.getDate("TRANSACTION_DATE") == null ? Constants.STRIP_CODE : reportService.dateFormat(rs.getDate("TRANSACTION_DATE"), Constants.MODEL_REPORT_SETTLEMENT_DATE));
                mpng3MonitoringPembayaran.setTanggalBuku(rs.getString("SETTLEMENT_DATE") == null ? Constants.STRIP_CODE : rs.getString("SETTLEMENT_DATE"));
                mpng3MonitoringPembayaran.setChannel(Constants.TELLER_DEFINE);
                mpng3MonitoringPembayaran.setKodeUser(rs.getString("USER_ID"));
                mpng3MonitoringPembayaran.setJumlah(rs.getString("NOMINAL") != null ? rs.getString("NOMINAL") : Constants.ZERO);
                mpng3MonitoringPembayaran.setKodeCabang(rs.getString("BRANCH_CODE"));
                mpng3MonitoringPembayaran.setKodeBilling(rs.getString("BILLING_CODE"));
                mpng3MonitoringPembayaran.setNama(StringUtils.equals(rs.getString("TRX_TYPE"), Constants.MPN_DJPG3) || StringUtils.equals(rs.getString("TRX_TYPE"), Constants.MPN_DJPG3_REINQUIRY) ? rs.getString("TAX_NAME") : rs.getString("CUSTOMER_NAME"));
                mpng3MonitoringPembayaran.setNtpn(rs.getString("NTPN") == null ? Constants.DEFAULT_REPORT : rs.getString("NTPN"));
                mpng3MonitoringPembayaran.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                mpng3MonitoringPembayaran.setUser(rs.getString("USER_ID"));
                mpng3MonitoringPembayaran.setNtb(rs.getString("NTB"));
                mpng3MonitoringPembayaran.setStan(rs.getString("TRACE_NUMBER"));
                mpng3MonitoringPembayaran.setStatus(rs.getString("STS"));
                mpng3MonitoringPembayaran.setNpwp(rs.getString("NPWP") == null ? Constants.DEFAULT_REPORT : rs.getString("NPWP"));
                mpng3MonitoringPembayaran.setMessage(rs.getString("MESSAGE"));
                return mpng3MonitoringPembayaran;
            }
        });
        return list;
    }

}
