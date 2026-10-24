package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.report.mpng2.Mpng2AdminMonitoringPembayaran;
import com.multipolar.uiteller.backend.model.report.mpng2.Mpng2MonitoringPembayaran;
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
public class ReportMpnRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private TransactionStatus serviceStatus;
    @Autowired
    private ReportService reportService;

    public List<Mpng2MonitoringPembayaran> list(final String startDate, final String endDate, final String settStartDate, final String settEndDate, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", startDate);
        params.put("end", endDate);
        params.put("settStart", settStartDate);
        params.put("settEnd", settEndDate);
        String query = "";
        if (admin)
            query = "select TRACE_NUMBER, MESSAGE, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP from MPN_TRANSACTION_DETAIL where ((TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) OR (TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY') BETWEEN TO_DATE(:settStart, 'DDMMYYYYhh24miss') and TO_DATE(:settEnd, 'DDMMYYYYhh24miss'))) and STS LIKE '" + sts + "' and BRANCH_CODE LIKE '%" + branch + "%' ORDER BY BRANCH_CODE, BILLING_CODE";
        else if (supervisi)
            query = "select TRACE_NUMBER, MESSAGE, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP from MPN_TRANSACTION_DETAIL where ((TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) OR (TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY') BETWEEN TO_DATE(:settStart, 'DDMMYYYYhh24miss') and TO_DATE(:settEnd, 'DDMMYYYYhh24miss'))) and STS LIKE '" + sts + "' and BRANCH_CODE = '" + branch + "' ORDER BY BRANCH_CODE, BILLING_CODE";
        else
            query = "select TRACE_NUMBER, MESSAGE, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP from MPN_TRANSACTION_DETAIL where ((TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) OR (TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY') BETWEEN TO_DATE(:settStart, 'DDMMYYYYhh24miss') and TO_DATE(:settEnd, 'DDMMYYYYhh24miss'))) and STS LIKE '" + sts + "' and USER_ID = '" + user + "' ORDER BY BRANCH_CODE, BILLING_CODE";
        List<Mpng2MonitoringPembayaran> list = this.jdbcTemplate.query(query, params, new RowMapper<Mpng2MonitoringPembayaran>() {
            @Override
            public Mpng2MonitoringPembayaran mapRow(ResultSet rs, int rowNum) throws SQLException {
                Mpng2MonitoringPembayaran mpng2MonitoringPembayaran = new Mpng2MonitoringPembayaran();
                mpng2MonitoringPembayaran.setTanggalBank(reportService.format(rs.getString("TRANSACTION_DATE"), Constants.MODEL_REPORT_DATE));
                mpng2MonitoringPembayaran.setTanggalBuku(reportService.format(rs.getString("SETTLEMENT_DATE"), Constants.MODEL_REPORT_SETTLEMENT_DATE));
                mpng2MonitoringPembayaran.setChannel(Constants.TELLER_DEFINE);
                mpng2MonitoringPembayaran.setKodeUser(rs.getString("USER_ID"));
                mpng2MonitoringPembayaran.setJumlah(rs.getString("NOMINAL") != null ? rs.getBigDecimal("NOMINAL") : BigDecimal.ZERO);
                mpng2MonitoringPembayaran.setKodeCabang(rs.getString("BRANCH_CODE"));
                mpng2MonitoringPembayaran.setKodeBilling(rs.getString("BILLING_CODE"));
                mpng2MonitoringPembayaran.setNama(StringUtils.equals(rs.getString("TRX_TYPE"), Constants.MPN_DJP) || StringUtils.equals(rs.getString("TRX_TYPE"), Constants.MPN_DJP_REINQUIRY) ? rs.getString("TAX_NAME") : rs.getString("CUSTOMER_NAME"));
                mpng2MonitoringPembayaran.setNtpn(rs.getString("NTPN") == null ? Constants.DEFAULT_REPORT : rs.getString("NTPN"));
                mpng2MonitoringPembayaran.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                mpng2MonitoringPembayaran.setUser(rs.getString("USER_ID"));
                mpng2MonitoringPembayaran.setNtb(rs.getString("NTB"));
                mpng2MonitoringPembayaran.setStan(rs.getString("TRACE_NUMBER"));
                mpng2MonitoringPembayaran.setStatus(rs.getString("STS"));
                mpng2MonitoringPembayaran.setNpwp(rs.getString("NPWP") == null ? Constants.DEFAULT_REPORT : rs.getString("NPWP"));
                mpng2MonitoringPembayaran.setMessage(rs.getString("MESSAGE"));
                return mpng2MonitoringPembayaran;
            }
        });
        return list;
    }

    public List<Mpng2MonitoringPembayaran> listDjp(final String startDate, final String endDate, final String settStartDate, final String settEndDate, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", startDate);
        params.put("end", endDate);
        params.put("settStart", settStartDate);
        params.put("settEnd", settEndDate);
        String query = "";
        if (admin)
            query = "select TRACE_NUMBER, MESSAGE, TAX_ADDRESS, TAX_TYPE, DEPOSIT_TYPE, TAX_PERIOD, SK_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP from MPN_DJP_TRANSACTION_DETAIL where ((TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) OR (TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY') BETWEEN TO_DATE(:settStart, 'DDMMYYYYhh24miss') and TO_DATE(:settEnd, 'DDMMYYYYhh24miss'))) and STS LIKE '" + sts + "' and BRANCH_CODE LIKE '%" + branch + "%' ORDER BY BRANCH_CODE, BILLING_CODE";
        else if (supervisi)
            query = "select TRACE_NUMBER, MESSAGE, TAX_ADDRESS, TAX_TYPE, DEPOSIT_TYPE, TAX_PERIOD, SK_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP from MPN_DJP_TRANSACTION_DETAIL where ((TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) OR (TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY') BETWEEN TO_DATE(:settStart, 'DDMMYYYYhh24miss') and TO_DATE(:settEnd, 'DDMMYYYYhh24miss'))) and STS LIKE '" + sts + "' and BRANCH_CODE = '" + branch + "' ORDER BY BRANCH_CODE, BILLING_CODE";
        else
            query = "select TRACE_NUMBER, MESSAGE, TAX_ADDRESS, TAX_TYPE, DEPOSIT_TYPE, TAX_PERIOD, SK_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP from MPN_DJP_TRANSACTION_DETAIL where ((TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) OR (TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY') BETWEEN TO_DATE(:settStart, 'DDMMYYYYhh24miss') and TO_DATE(:settEnd, 'DDMMYYYYhh24miss'))) and STS LIKE '" + sts + "' and USER_ID = '" + user + "' ORDER BY BRANCH_CODE, BILLING_CODE";
        List<Mpng2MonitoringPembayaran> list = this.jdbcTemplate.query(query, params, new RowMapper<Mpng2MonitoringPembayaran>() {
            @Override
            public Mpng2MonitoringPembayaran mapRow(ResultSet rs, int rowNum) throws SQLException {
                Mpng2MonitoringPembayaran mpng2MonitoringPembayaran = new Mpng2MonitoringPembayaran();
                mpng2MonitoringPembayaran.setTanggalBank(reportService.format(rs.getString("TRANSACTION_DATE"), Constants.MODEL_REPORT_DATE));
                mpng2MonitoringPembayaran.setTanggalBuku(reportService.format(rs.getString("SETTLEMENT_DATE"), Constants.MODEL_REPORT_SETTLEMENT_DATE));
                mpng2MonitoringPembayaran.setChannel(Constants.TELLER_DEFINE);
                mpng2MonitoringPembayaran.setKodeUser(rs.getString("USER_ID"));
                mpng2MonitoringPembayaran.setJumlah(rs.getString("NOMINAL") != null ? rs.getBigDecimal("NOMINAL") : BigDecimal.ZERO);
                mpng2MonitoringPembayaran.setKodeCabang(rs.getString("BRANCH_CODE"));
                mpng2MonitoringPembayaran.setKodeBilling(rs.getString("BILLING_CODE"));
                mpng2MonitoringPembayaran.setNama(StringUtils.equals(rs.getString("TRX_TYPE"), Constants.MPN_DJP) || StringUtils.equals(rs.getString("TRX_TYPE"), Constants.MPN_DJP_REINQUIRY) ? rs.getString("TAX_NAME") : rs.getString("CUSTOMER_NAME"));
                mpng2MonitoringPembayaran.setNtpn(rs.getString("NTPN") == null ? Constants.DEFAULT_REPORT : rs.getString("NTPN"));
                mpng2MonitoringPembayaran.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                mpng2MonitoringPembayaran.setUser(rs.getString("USER_ID"));
                mpng2MonitoringPembayaran.setNtb(rs.getString("NTB"));
                mpng2MonitoringPembayaran.setStan(rs.getString("TRACE_NUMBER"));
                mpng2MonitoringPembayaran.setStatus(rs.getString("STS"));
                mpng2MonitoringPembayaran.setNpwp(rs.getString("NPWP") == null ? Constants.DEFAULT_REPORT : rs.getString("NPWP"));
                mpng2MonitoringPembayaran.setAddress(rs.getString("TAX_ADDRESS"));
                mpng2MonitoringPembayaran.setTaxType(rs.getString("TAX_TYPE"));
                mpng2MonitoringPembayaran.setDepositType(rs.getString("DEPOSIT_TYPE"));
                mpng2MonitoringPembayaran.setTaxPeriod(rs.getString("TAX_PERIOD"));
                mpng2MonitoringPembayaran.setTaxYear(StringUtils.substring(rs.getString("TAX_PERIOD"), 4, 8));
                mpng2MonitoringPembayaran.setSkNumber(rs.getString("SK_NUMBER"));
                mpng2MonitoringPembayaran.setMessage(rs.getString("MESSAGE"));
                return mpng2MonitoringPembayaran;
            }
        });
        return list;
    }

    public List<Mpng2AdminMonitoringPembayaran> listAdmin(final String startDate, final String endDate, final String settStartDate, final String settEndDate, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", startDate);
        params.put("end", endDate);
        params.put("settStart", settStartDate);
        params.put("settEnd", settEndDate);
        String query = "";
        if (admin)
            query = "select TRACE_NUMBER, MESSAGE, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP from MPN_TRANSACTION_DETAIL where ((TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) OR (TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY') BETWEEN TO_DATE(:settStart, 'DDMMYYYYhh24miss') and TO_DATE(:settEnd, 'DDMMYYYYhh24miss'))) and STS LIKE '" + sts + "' and BRANCH_CODE LIKE '%" + branch + "%' ORDER BY BRANCH_CODE, BILLING_CODE";
        else if (supervisi)
            query = "select TRACE_NUMBER, MESSAGE, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP from MPN_TRANSACTION_DETAIL where ((TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) OR (TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY') BETWEEN TO_DATE(:settStart, 'DDMMYYYYhh24miss') and TO_DATE(:settEnd, 'DDMMYYYYhh24miss'))) and STS LIKE '" + sts + "' and BRANCH_CODE = '" + branch + "' ORDER BY BRANCH_CODE, BILLING_CODE";
        else
            query = "select TRACE_NUMBER, MESSAGE, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP from MPN_TRANSACTION_DETAIL where ((TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) OR (TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY') BETWEEN TO_DATE(:settStart, 'DDMMYYYYhh24miss') and TO_DATE(:settEnd, 'DDMMYYYYhh24miss'))) and STS LIKE '" + sts + "' and USER_ID = '" + user + "' ORDER BY BRANCH_CODE, BILLING_CODE";
        List<Mpng2AdminMonitoringPembayaran> list = this.jdbcTemplate.query(query, params, new RowMapper<Mpng2AdminMonitoringPembayaran>() {
            @Override
            public Mpng2AdminMonitoringPembayaran mapRow(ResultSet rs, int rowNum) throws SQLException {
                Mpng2AdminMonitoringPembayaran mpng2MonitoringPembayaran = new Mpng2AdminMonitoringPembayaran();
                mpng2MonitoringPembayaran.setTanggalBank(rs.getDate("TRANSACTION_DATE") == null ? Constants.STRIP_CODE : reportService.dateFormat(rs.getDate("TRANSACTION_DATE"), Constants.MODEL_REPORT_SETTLEMENT_DATE));
                mpng2MonitoringPembayaran.setTanggalBuku(rs.getString("SETTLEMENT_DATE") == null ? Constants.STRIP_CODE : rs.getString("SETTLEMENT_DATE"));
                mpng2MonitoringPembayaran.setChannel(Constants.TELLER_DEFINE);
                mpng2MonitoringPembayaran.setKodeUser(rs.getString("USER_ID"));
                mpng2MonitoringPembayaran.setJumlah(rs.getString("NOMINAL") != null ? rs.getString("NOMINAL") : Constants.ZERO);
                mpng2MonitoringPembayaran.setKodeCabang(rs.getString("BRANCH_CODE"));
                mpng2MonitoringPembayaran.setKodeBilling(rs.getString("BILLING_CODE"));
                mpng2MonitoringPembayaran.setNama(StringUtils.equals(rs.getString("TRX_TYPE"), Constants.MPN_DJP) || StringUtils.equals(rs.getString("TRX_TYPE"), Constants.MPN_DJP_REINQUIRY) ? rs.getString("TAX_NAME") : rs.getString("CUSTOMER_NAME"));
                mpng2MonitoringPembayaran.setNtpn(rs.getString("NTPN") == null ? Constants.DEFAULT_REPORT : rs.getString("NTPN"));
                mpng2MonitoringPembayaran.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                mpng2MonitoringPembayaran.setUser(rs.getString("USER_ID"));
                mpng2MonitoringPembayaran.setNtb(rs.getString("NTB"));
                mpng2MonitoringPembayaran.setStan(rs.getString("TRACE_NUMBER"));
                mpng2MonitoringPembayaran.setStatus(rs.getString("STS"));
                mpng2MonitoringPembayaran.setNpwp(rs.getString("NPWP") == null ? Constants.DEFAULT_REPORT : rs.getString("NPWP"));
                mpng2MonitoringPembayaran.setMessage(rs.getString("MESSAGE"));
                return mpng2MonitoringPembayaran;
            }
        });
        return list;
    }

    public List<Mpng2AdminMonitoringPembayaran> listDjpAdmin(final String startDate, final String endDate, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin)
            query = "select TRACE_NUMBER, MESSAGE, TAX_ADDRESS, TAX_TYPE, DEPOSIT_TYPE, TAX_PERIOD, SK_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP from MPN_DJP_TRANSACTION_DETAIL where (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) and STS LIKE '" + sts + "' and BRANCH_CODE LIKE '%" + branch + "%' ORDER BY BRANCH_CODE, BILLING_CODE";
        else if (supervisi)
            query = "select TRACE_NUMBER, MESSAGE, TAX_ADDRESS, TAX_TYPE, DEPOSIT_TYPE, TAX_PERIOD, SK_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP from MPN_DJP_TRANSACTION_DETAIL where (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) and STS LIKE '" + sts + "' and BRANCH_CODE = '" + branch + "' ORDER BY BRANCH_CODE, BILLING_CODE";
        else
            query = "select TRACE_NUMBER, MESSAGE, TAX_ADDRESS, TAX_TYPE, DEPOSIT_TYPE, TAX_PERIOD, SK_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, SETTLEMENT_DATE, BILLING_CODE, TAX_NAME, NTPN, ARCHIVE_NUMBER, NOMINAL, TRX_TYPE, CUSTOMER_NAME, NTB, STS, NPWP from MPN_DJP_TRANSACTION_DETAIL where (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) and STS LIKE '" + sts + "' and USER_ID = '" + user + "' ORDER BY BRANCH_CODE, BILLING_CODE";
        List<Mpng2AdminMonitoringPembayaran> list = this.jdbcTemplate.query(query, params, new RowMapper<Mpng2AdminMonitoringPembayaran>() {
            @Override
            public Mpng2AdminMonitoringPembayaran mapRow(ResultSet rs, int rowNum) throws SQLException {
                Mpng2AdminMonitoringPembayaran mpng2MonitoringPembayaran = new Mpng2AdminMonitoringPembayaran();
//                mpng2MonitoringPembayaran.setTanggalBank(reportService.format(rs.getString("TRANSACTION_DATE"), Constants.MODEL_REPORT_DATE));
//                mpng2MonitoringPembayaran.setTanggalBuku(reportService.format(rs.getString("SETTLEMENT_DATE"), Constants.MODEL_REPORT_SETTLEMENT_DATE));
                mpng2MonitoringPembayaran.setChannel(Constants.TELLER_DEFINE);
                mpng2MonitoringPembayaran.setKodeUser(rs.getString("USER_ID"));
                mpng2MonitoringPembayaran.setJumlah(rs.getString("NOMINAL") != null ? rs.getString("NOMINAL") : Constants.ZERO);
                mpng2MonitoringPembayaran.setKodeCabang(rs.getString("BRANCH_CODE"));
                mpng2MonitoringPembayaran.setKodeBilling(rs.getString("BILLING_CODE"));
                mpng2MonitoringPembayaran.setNama(StringUtils.equals(rs.getString("TRX_TYPE"), Constants.MPN_DJP) || StringUtils.equals(rs.getString("TRX_TYPE"), Constants.MPN_DJP_REINQUIRY) ? rs.getString("TAX_NAME") : rs.getString("CUSTOMER_NAME"));
                mpng2MonitoringPembayaran.setNtpn(rs.getString("NTPN") == null ? Constants.DEFAULT_REPORT : rs.getString("NTPN"));
                mpng2MonitoringPembayaran.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                mpng2MonitoringPembayaran.setUser(rs.getString("USER_ID"));
                mpng2MonitoringPembayaran.setNtb(rs.getString("NTB"));
                mpng2MonitoringPembayaran.setStan(rs.getString("TRACE_NUMBER"));
                mpng2MonitoringPembayaran.setStatus(rs.getString("STS"));
                mpng2MonitoringPembayaran.setNpwp(rs.getString("NPWP") == null ? Constants.DEFAULT_REPORT : rs.getString("NPWP"));
                mpng2MonitoringPembayaran.setAddress(rs.getString("TAX_ADDRESS"));
                mpng2MonitoringPembayaran.setTaxType(rs.getString("TAX_TYPE"));
                mpng2MonitoringPembayaran.setDepositType(rs.getString("DEPOSIT_TYPE"));
                mpng2MonitoringPembayaran.setTaxPeriod(rs.getString("TAX_PERIOD"));
                mpng2MonitoringPembayaran.setTaxYear(StringUtils.substring(rs.getString("TAX_PERIOD"), 4, 8));
                mpng2MonitoringPembayaran.setSkNumber(rs.getString("SK_NUMBER"));
                mpng2MonitoringPembayaran.setMessage(rs.getString("MESSAGE"));
                return mpng2MonitoringPembayaran;
            }
        });
        return list;
    }
}
