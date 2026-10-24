package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.report.pdam.ReportPendapatanPdam;
import com.multipolar.uiteller.backend.model.report.pdam.ReportPendapatanPdamCells;
import com.multipolar.uiteller.backend.model.report.pdam.ReportPendapatanPdamDetails;
import com.multipolar.uiteller.backend.service.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ReportPdamRepository {

    private final static Logger console = LoggerFactory.getLogger(ReportPdamRepository.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private TransactionStatus serviceStatus;

    public List<ReportPendapatanPdam> listTirtabulian(final String startDate, final String endDate, final String detailLocation, final String cellLocation, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        final Map<String, Object> params = new HashMap<>();
        params.put("sts", sts);
        String sql = "select STATUS_LOG from PDAM_TIRTABULIAN_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanPdam> list = this.jdbcTemplate.query(sql, params, new RowMapper<ReportPendapatanPdam>() {
            @Override
            public ReportPendapatanPdam mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdam pdam = new ReportPendapatanPdam();
                pdam.setJasperLocation(detailLocation);
                pdam.setDetails(listDetailTirtabulian(rs.getString("STATUS_LOG"), startDate, endDate, cellLocation, user, branch, teller, supervisi, admin));
                return pdam;
            }
        });
        return list;
    }

    private List<ReportPendapatanPdamDetails> listDetailTirtabulian(final String statusLog, final String startDate, final String endDate, final String jasperLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", statusLog);
        String sql = "";
        if (admin)
            sql = "select BRANCH_CODE, STATUS_LOG from PDAM_TIRTABULIAN_TRANSACTION where STATUS_LOG = :statusLog group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            sql = "select BRANCH_CODE, STATUS_LOG from PDAM_TIRTABULIAN_TRANSACTION where STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanPdamDetails> list = this.jdbcTemplate.query(sql, params, new RowMapper<ReportPendapatanPdamDetails>() {
            @Override
            public ReportPendapatanPdamDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamDetails detailPdam = new ReportPendapatanPdamDetails();
                detailPdam.setJasperLocation(jasperLocation);
                detailPdam.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailPdam.setStatusTransaksi(serviceStatus.getStatus(statusLog));
                detailPdam.setDetails(listCellTirtabulian(detailPdam.getKodeCabang(), statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return detailPdam;
            }
        });
        return list;
    }

    private List<ReportPendapatanPdamCells> listCellTirtabulian(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String sql = "";
        if (admin || supervisi)
            sql = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_ACCOUNT, BILL_AMOUNT, BILL_INFO, BILL_PERIOD, BILL_REPORT, CUBICATION, CURRENCY_CODE, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, PENALTIES, REFF, REFERENCE_NUMBER, SOURCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTABULIAN_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY CUSTOMER_ID";
        else
            sql = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_ACCOUNT, BILL_AMOUNT, BILL_INFO, BILL_PERIOD, BILL_REPORT, CUBICATION, CURRENCY_CODE, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, PENALTIES, REFF, REFERENCE_NUMBER, SOURCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTABULIAN_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY CUSTOMER_ID";
        List<ReportPendapatanPdamCells> list = this.jdbcTemplate.query(sql, params, new RowMapper<ReportPendapatanPdamCells>() {
            @Override
            public ReportPendapatanPdamCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamCells cellPdam = new ReportPendapatanPdamCells();
                cellPdam.setBiaya(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                cellPdam.setChannel("Teller");
                cellPdam.setJumlah(rs.getBigDecimal("BILL_AMOUNT") != null ? rs.getBigDecimal("BILL_AMOUNT") : BigDecimal.ZERO);
                cellPdam.setBills(rs.getInt("BILL_REPORT"));
                cellPdam.setLainLain(rs.getBigDecimal("PENALTIES") != null ? rs.getBigDecimal("PENALTIES") : BigDecimal.ZERO);
                cellPdam.setNamaPelanggan(rs.getString("CUSTOMER_NAME"));
                cellPdam.setPrd(rs.getString("BILL_PERIOD"));
                cellPdam.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                cellPdam.setTanggalTransaksi(rs.getDate("TRANSACTION_DATE"));
                cellPdam.setUser(rs.getString("USER_ID"));
                cellPdam.setNomorPelangan(rs.getString("CUSTOMER_ID"));
                return cellPdam;
            }
        });
        return list;
    }

    public List<ReportPendapatanPdam> listTirtauli(final String startDate, final String endDate, final String detailLocation, final String cellLocation, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        final Map<String, Object> params = new HashMap<>();
        params.put("sts", sts);
        String query = "select STATUS_LOG from PDAM_TIRTAULI_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanPdam> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdam>() {
            @Override
            public ReportPendapatanPdam mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdam pdam = new ReportPendapatanPdam();
                pdam.setJasperLocation(detailLocation);
                pdam.setDetails(listDetailTirtauli(rs.getString("STATUS_LOG"), startDate, endDate, cellLocation, user, branch, teller, supervisi, admin));
                return pdam;
            }
        });
        return list;
    }

    private List<ReportPendapatanPdamDetails> listDetailTirtauli(final String statusLog, final String startDate, final String endDate, final String jasperLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", statusLog);
        //language=MySQL
        String query = "";
        if (admin)
            query = "SELECT BRANCH_CODE, STATUS_LOG FROM PDAM_TIRTAULI_TRANSACTION WHERE STATUS_LOG = :statusLog GROUP BY BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            query = "SELECT BRANCH_CODE, STATUS_LOG FROM PDAM_TIRTAULI_TRANSACTION WHERE STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' GROUP BY BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanPdamDetails> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamDetails>() {
            @Override
            public ReportPendapatanPdamDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamDetails detailPdam = new ReportPendapatanPdamDetails();
                detailPdam.setJasperLocation(jasperLocation);
                detailPdam.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailPdam.setStatusTransaksi(serviceStatus.getStatus(statusLog));
                detailPdam.setDetails(listCellTirtauli(detailPdam.getKodeCabang(), statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return detailPdam;
            }
        });
        return list;
    }

    private List<ReportPendapatanPdamCells> listCellTirtauli(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_AMOUNT, BILL_ACCOUNT, BILL_INFO, BILL_PERIOD, BILL_REPORT, CUBICATION, CURRENCY_CODE, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, PENALTIES, REFF, REFERENCE_NUMBER, SOURCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER, RETRIBUTION from PDAM_TIRTAULI_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY CUSTOMER_ID";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_AMOUNT, BILL_ACCOUNT, BILL_INFO, BILL_PERIOD, BILL_REPORT, CUBICATION, CURRENCY_CODE, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, PENALTIES, REFF, REFERENCE_NUMBER, SOURCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER, RETRIBUTION from PDAM_TIRTAULI_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY CUSTOMER_ID";
        List<ReportPendapatanPdamCells> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamCells>() {
            @Override
            public ReportPendapatanPdamCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamCells cellPdam = new ReportPendapatanPdamCells();
                cellPdam.setBiaya(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                cellPdam.setChannel("Teller");
                cellPdam.setBills(rs.getInt("BILL_REPORT"));
                cellPdam.setJumlah(rs.getBigDecimal("BILL_AMOUNT") != null ? rs.getBigDecimal("BILL_AMOUNT") : BigDecimal.ZERO);
                BigDecimal denda = rs.getBigDecimal("PENALTIES") != null ? rs.getBigDecimal("PENALTIES") : BigDecimal.ZERO;
                BigDecimal retribution = rs.getBigDecimal("RETRIBUTION") != null ? rs.getBigDecimal("RETRIBUTION") : BigDecimal.ZERO;
                cellPdam.setLainLain(denda);
                cellPdam.setNamaPelanggan(rs.getString("CUSTOMER_NAME"));
                cellPdam.setPrd(rs.getString("BILL_PERIOD"));
                cellPdam.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                cellPdam.setTanggalTransaksi(rs.getDate("TRANSACTION_DATE"));
                cellPdam.setUser(rs.getString("USER_ID"));
                cellPdam.setNomorPelangan(rs.getString("CUSTOMER_ID"));
                return cellPdam;
            }
        });
        return list;
    }

    public List<ReportPendapatanPdam> listTirtanadi(final String startDate, final String endDate, final String detailLocation, final String cellLocation, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        final Map<String, Object> params = new HashMap<>();
        params.put("sts", sts);
        String query = "select STATUS_LOG from PDAM_TIRTANADI_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanPdam> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdam>() {
            @Override
            public ReportPendapatanPdam mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdam pdam = new ReportPendapatanPdam();
                pdam.setJasperLocation(detailLocation);
                pdam.setDetails(listDetailTirtanadi(rs.getString("STATUS_LOG"), startDate, endDate, cellLocation, user, branch, teller, supervisi, admin));
                return pdam;
            }
        });
        console.info("pdam tirtanadi list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamDetails> listDetailTirtanadi(final String statusLog, final String startDate, final String endDate, final String jasperLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", statusLog);
        String query = "";
        if (admin)
            query = "select BRANCH_CODE, STATUS_LOG from PDAM_TIRTANADI_TRANSACTION where STATUS_LOG = :statusLog group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            query = "select BRANCH_CODE, STATUS_LOG from PDAM_TIRTANADI_TRANSACTION where STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanPdamDetails> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamDetails>() {
            @Override
            public ReportPendapatanPdamDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamDetails detailPdam = new ReportPendapatanPdamDetails();
                detailPdam.setJasperLocation(jasperLocation);
                detailPdam.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailPdam.setStatusTransaksi(serviceStatus.getStatus(statusLog));
                detailPdam.setDetails(listCellTirtanadi(detailPdam.getKodeCabang(), statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return detailPdam;
            }
        });
        console.info("tirtanadi details list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamCells> listCellTirtanadi(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_AMOUNT, BILL_ACCOUNT, BILL_INFO, BILL_PERIOD, BILL_REPORT, CUBICATION, CURRENCY_CODE, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, PENALTIES, REFF, REFERENCE_NUMBER, SOURCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTANADI_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY USER_ID";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_AMOUNT, BILL_ACCOUNT, BILL_INFO, BILL_PERIOD, BILL_REPORT, CUBICATION, CURRENCY_CODE, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, PENALTIES, REFF, REFERENCE_NUMBER, SOURCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTANADI_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY USER_ID";
        List<ReportPendapatanPdamCells> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamCells>() {
            @Override
            public ReportPendapatanPdamCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamCells cellPdam = new ReportPendapatanPdamCells();
                cellPdam.setBiaya(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                cellPdam.setChannel("Teller");
                cellPdam.setJumlah(rs.getBigDecimal("BILL_AMOUNT") != null ? rs.getBigDecimal("BILL_AMOUNT") : BigDecimal.ZERO);
                cellPdam.setLainLain(rs.getBigDecimal("PENALTIES") != null ? rs.getBigDecimal("PENALTIES") : BigDecimal.ZERO);
                cellPdam.setBills(rs.getInt("BILL_REPORT"));
                cellPdam.setNamaPelanggan(rs.getString("CUSTOMER_NAME"));
                cellPdam.setPrd(rs.getString("BILL_PERIOD"));
                cellPdam.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                cellPdam.setTanggalTransaksi(rs.getDate("TRANSACTION_DATE"));
                cellPdam.setUser(rs.getString("USER_ID"));
                cellPdam.setNomorPelangan(rs.getString("CUSTOMER_ID"));
                return cellPdam;
            }
        });
        console.info("tirtanadi cell list size : {}", list.size());
        return list;
    }
    
    public List<ReportPendapatanPdam> listTirtaayumiDti(final String startDate, final String endDate, final String detailLocation, final String cellLocation, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        final Map<String, Object> params = new HashMap<>();
        params.put("sts", sts);
        String query = "select STATUS_LOG from PDAM_TIRTAAYUMI_DTI_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanPdam> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdam>() {
            @Override
            public ReportPendapatanPdam mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdam pdam = new ReportPendapatanPdam();
                pdam.setJasperLocation(detailLocation);
                pdam.setDetails(listDetailTirtaayumiDti(rs.getString("STATUS_LOG"), startDate, endDate, cellLocation, user, branch, teller, supervisi, admin));
                return pdam;
            }
        });
        console.info("pdam tirtaayumiDti list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamDetails> listDetailTirtaayumiDti(final String statusLog, final String startDate, final String endDate, final String jasperLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", statusLog);
        String query = "";
        if (admin)
            query = "select BRANCH_CODE, STATUS_LOG from PDAM_TIRTAAYUMI_DTI_TRANSACTION where STATUS_LOG = :statusLog group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            query = "select BRANCH_CODE, STATUS_LOG from PDAM_TIRTAAYUMI_DTI_TRANSACTION where STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanPdamDetails> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamDetails>() {
            @Override
            public ReportPendapatanPdamDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamDetails detailPdam = new ReportPendapatanPdamDetails();
                detailPdam.setJasperLocation(jasperLocation);
                detailPdam.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailPdam.setStatusTransaksi(serviceStatus.getStatus(statusLog));
                detailPdam.setDetails(listCellTirtaayumiDti(detailPdam.getKodeCabang(), statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return detailPdam;
            }
        });
        console.info("tirtaayumiDti details list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamCells> listCellTirtaayumiDti(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_AMOUNT, BILL_ACCOUNT, BILL_INFO, BILL_PERIOD, BILL_REPORT, CUBICATION, CURRENCY_CODE, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, PENALTIES, REFF, REFERENCE_NUMBER, SOURCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTAAYUMI_DTI_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY USER_ID";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_AMOUNT, BILL_ACCOUNT, BILL_INFO, BILL_PERIOD, BILL_REPORT, CUBICATION, CURRENCY_CODE, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, PENALTIES, REFF, REFERENCE_NUMBER, SOURCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTAAYUMI_DTI_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY USER_ID";
        List<ReportPendapatanPdamCells> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamCells>() {
            @Override
            public ReportPendapatanPdamCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamCells cellPdam = new ReportPendapatanPdamCells();
                cellPdam.setBiaya(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                cellPdam.setChannel("Teller");
                cellPdam.setJumlah(rs.getBigDecimal("BILL_AMOUNT") != null ? rs.getBigDecimal("BILL_AMOUNT") : BigDecimal.ZERO);
                cellPdam.setLainLain(rs.getBigDecimal("PENALTIES") != null ? rs.getBigDecimal("PENALTIES") : BigDecimal.ZERO);
                cellPdam.setBills(rs.getInt("BILL_REPORT"));
                cellPdam.setNamaPelanggan(rs.getString("CUSTOMER_NAME"));
                cellPdam.setPrd(rs.getString("BILL_PERIOD"));
                cellPdam.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                cellPdam.setTanggalTransaksi(rs.getDate("TRANSACTION_DATE"));
                cellPdam.setUser(rs.getString("USER_ID"));
                cellPdam.setNomorPelangan(rs.getString("CUSTOMER_ID"));
                return cellPdam;
            }
        });
        console.info("tirtaayumiDti cell list size : {}", list.size());
        return list;
    }
    
    public List<ReportPendapatanPdam> listTirtakualoDti(final String startDate, final String endDate, final String detailLocation, final String cellLocation, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        final Map<String, Object> params = new HashMap<>();
        params.put("sts", sts);
        String query = "select STATUS_LOG from PDAM_TIRTAKUALO_DTI_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanPdam> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdam>() {
            @Override
            public ReportPendapatanPdam mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdam pdam = new ReportPendapatanPdam();
                pdam.setJasperLocation(detailLocation);
                pdam.setDetails(listDetailTirtakualoDti(rs.getString("STATUS_LOG"), startDate, endDate, cellLocation, user, branch, teller, supervisi, admin));
                return pdam;
            }
        });
        console.info("pdam tirtakualoDti list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamDetails> listDetailTirtakualoDti(final String statusLog, final String startDate, final String endDate, final String jasperLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", statusLog);
        String query = "";
        if (admin)
            query = "select BRANCH_CODE, STATUS_LOG from PDAM_TIRTAKUALO_DTI_TRANSACTION where STATUS_LOG = :statusLog group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            query = "select BRANCH_CODE, STATUS_LOG from PDAM_TIRTAKUALO_DTI_TRANSACTION where STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanPdamDetails> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamDetails>() {
            @Override
            public ReportPendapatanPdamDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamDetails detailPdam = new ReportPendapatanPdamDetails();
                detailPdam.setJasperLocation(jasperLocation);
                detailPdam.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailPdam.setStatusTransaksi(serviceStatus.getStatus(statusLog));
                detailPdam.setDetails(listCellTirtakualoDti(detailPdam.getKodeCabang(), statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return detailPdam;
            }
        });
        console.info("tirtakualoDti details list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamCells> listCellTirtakualoDti(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_AMOUNT, BILL_ACCOUNT, BILL_INFO, BILL_PERIOD, BILL_REPORT, CUBICATION, CURRENCY_CODE, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, PENALTIES, REFF, REFERENCE_NUMBER, SOURCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTAKUALO_DTI_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY USER_ID";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_AMOUNT, BILL_ACCOUNT, BILL_INFO, BILL_PERIOD, BILL_REPORT, CUBICATION, CURRENCY_CODE, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, PENALTIES, REFF, REFERENCE_NUMBER, SOURCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTAKUALO_DTI_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY USER_ID";
        List<ReportPendapatanPdamCells> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamCells>() {
            @Override
            public ReportPendapatanPdamCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamCells cellPdam = new ReportPendapatanPdamCells();
                cellPdam.setBiaya(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                cellPdam.setChannel("Teller");
                cellPdam.setJumlah(rs.getBigDecimal("BILL_AMOUNT") != null ? rs.getBigDecimal("BILL_AMOUNT") : BigDecimal.ZERO);
                cellPdam.setLainLain(rs.getBigDecimal("PENALTIES") != null ? rs.getBigDecimal("PENALTIES") : BigDecimal.ZERO);
                cellPdam.setBills(rs.getInt("BILL_REPORT"));
                cellPdam.setNamaPelanggan(rs.getString("CUSTOMER_NAME"));
                cellPdam.setPrd(rs.getString("BILL_PERIOD"));
                cellPdam.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                cellPdam.setTanggalTransaksi(rs.getDate("TRANSACTION_DATE"));
                cellPdam.setUser(rs.getString("USER_ID"));
                cellPdam.setNomorPelangan(rs.getString("CUSTOMER_ID"));
                return cellPdam;
            }
        });
        console.info("tirtakualoDti cell list size : {}", list.size());
        return list;
    }
    
    public List<ReportPendapatanPdam> listTirtatanjungDti(final String startDate, final String endDate, final String detailLocation, final String cellLocation, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        final Map<String, Object> params = new HashMap<>();
        params.put("sts", sts);
        String query = "select STATUS_LOG from PDAM_TIRTATANJUNG_DTI_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanPdam> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdam>() {
            @Override
            public ReportPendapatanPdam mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdam pdam = new ReportPendapatanPdam();
                pdam.setJasperLocation(detailLocation);
                pdam.setDetails(listDetailTirtatanjungDti(rs.getString("STATUS_LOG"), startDate, endDate, cellLocation, user, branch, teller, supervisi, admin));
                return pdam;
            }
        });
        console.info("pdam tirtatanjungDti list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamDetails> listDetailTirtatanjungDti(final String statusLog, final String startDate, final String endDate, final String jasperLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", statusLog);
        String query = "";
        if (admin)
            query = "select BRANCH_CODE, STATUS_LOG from PDAM_TIRTATANJUNG_DTI_TRANSACTION where STATUS_LOG = :statusLog group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            query = "select BRANCH_CODE, STATUS_LOG from PDAM_TIRTATANJUNG_DTI_TRANSACTION where STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanPdamDetails> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamDetails>() {
            @Override
            public ReportPendapatanPdamDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamDetails detailPdam = new ReportPendapatanPdamDetails();
                detailPdam.setJasperLocation(jasperLocation);
                detailPdam.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailPdam.setStatusTransaksi(serviceStatus.getStatus(statusLog));
                detailPdam.setDetails(listCellTirtatanjungDti(detailPdam.getKodeCabang(), statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return detailPdam;
            }
        });
        console.info("tirtatanjungDti details list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamCells> listCellTirtatanjungDti(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_AMOUNT, BILL_ACCOUNT, BILL_INFO, BILL_PERIOD, BILL_REPORT, CUBICATION, CURRENCY_CODE, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, PENALTIES, REFF, REFERENCE_NUMBER, SOURCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTATANJUNG_DTI_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY USER_ID";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_AMOUNT, BILL_ACCOUNT, BILL_INFO, BILL_PERIOD, BILL_REPORT, CUBICATION, CURRENCY_CODE, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, PENALTIES, REFF, REFERENCE_NUMBER, SOURCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTATANJUNG_DTI_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY USER_ID";
        List<ReportPendapatanPdamCells> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamCells>() {
            @Override
            public ReportPendapatanPdamCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamCells cellPdam = new ReportPendapatanPdamCells();
                cellPdam.setBiaya(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                cellPdam.setChannel("Teller");
                cellPdam.setJumlah(rs.getBigDecimal("BILL_AMOUNT") != null ? rs.getBigDecimal("BILL_AMOUNT") : BigDecimal.ZERO);
                cellPdam.setLainLain(rs.getBigDecimal("PENALTIES") != null ? rs.getBigDecimal("PENALTIES") : BigDecimal.ZERO);
                cellPdam.setBills(rs.getInt("BILL_REPORT"));
                cellPdam.setNamaPelanggan(rs.getString("CUSTOMER_NAME"));
                cellPdam.setPrd(rs.getString("BILL_PERIOD"));
                cellPdam.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                cellPdam.setTanggalTransaksi(rs.getDate("TRANSACTION_DATE"));
                cellPdam.setUser(rs.getString("USER_ID"));
                cellPdam.setNomorPelangan(rs.getString("CUSTOMER_ID"));
                return cellPdam;
            }
        });
        console.info("tirtatanjungDti cell list size : {}", list.size());
        return list;
    }

    public List<ReportPendapatanPdam> listTirtanadiInstallation(final String startDate, final String endDate, final String detailLocation, final String cellLocation, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        final Map<String, Object> params = new HashMap<>();
        params.put("sts", sts);
        String query = "select STATUS_LOG from PDAM_TIRTANADI_INSTALLATION_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanPdam> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdam>() {
            @Override
            public ReportPendapatanPdam mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdam pdam = new ReportPendapatanPdam();
                pdam.setJasperLocation(detailLocation);
                pdam.setDetails(listDetailTirtanadiInstallation(rs.getString("STATUS_LOG"), startDate, endDate, cellLocation, user, branch, teller, supervisi, admin));
                return pdam;
            }
        });
        console.info("pdam tirtanadi installation list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamDetails> listDetailTirtanadiInstallation(final String statusLog, final String startDate, final String endDate, final String cellLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", statusLog);
        //language=MySQL
        String query = "";
        if (admin)
            query = "SELECT BRANCH_CODE, STATUS_LOG FROM PDAM_TIRTANADI_INSTALLATION_TRANSACTION WHERE STATUS_LOG = :statusLog GROUP BY BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            query = "SELECT BRANCH_CODE, STATUS_LOG FROM PDAM_TIRTANADI_INSTALLATION_TRANSACTION WHERE STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' GROUP BY BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanPdamDetails> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamDetails>() {
            @Override
            public ReportPendapatanPdamDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamDetails detailPdam = new ReportPendapatanPdamDetails();
                detailPdam.setJasperLocation(cellLocation);
                detailPdam.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailPdam.setStatusTransaksi(serviceStatus.getStatus(statusLog));
                detailPdam.setDetails(listCellTirtanadiInstallation(detailPdam.getKodeCabang(), statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return detailPdam;
            }
        });
        console.info("tirtanadi installation details list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamCells> listCellTirtanadiInstallation(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_ACCOUNT, BILL_FORM, BILL_INFO, BILL_INSTALLATION, BILL_PERIOD, BILL_REPORT, CUBICATION, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, REFF, REFERENCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTANADI_INSTALLATION_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY CUSTOMER_ID";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_ACCOUNT, BILL_FORM, BILL_INFO, BILL_INSTALLATION, BILL_PERIOD, BILL_REPORT, CUBICATION, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, REFF, REFERENCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTANADI_INSTALLATION_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY CUSTOMER_ID";
        List<ReportPendapatanPdamCells> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamCells>() {
            @Override
            public ReportPendapatanPdamCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamCells cellPdam = new ReportPendapatanPdamCells();
                cellPdam.setBiaya(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                cellPdam.setChannel("Teller");
                BigDecimal formulir = rs.getBigDecimal("BILL_FORM") != null ? rs.getBigDecimal("BILL_FORM") : BigDecimal.ZERO;
                cellPdam.setJumlah(rs.getBigDecimal("NOMINAL") != null ? rs.getBigDecimal("NOMINAL").subtract(formulir) : BigDecimal.ZERO);
                BigDecimal installation = rs.getBigDecimal("BILL_INSTALLATION") != null ? rs.getBigDecimal("BILL_INSTALLATION") : BigDecimal.ZERO;
                cellPdam.setLainLain(formulir);
                cellPdam.setBills(rs.getInt("BILL_REPORT"));
                cellPdam.setNamaPelanggan(rs.getString("CUSTOMER_NAME"));
                cellPdam.setPrd(rs.getString("BILL_PERIOD"));
                cellPdam.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                cellPdam.setTanggalTransaksi(rs.getDate("TRANSACTION_DATE"));
                cellPdam.setUser(rs.getString("USER_ID"));
                cellPdam.setNomorPelangan(rs.getString("CUSTOMER_ID"));
                return cellPdam;
            }
        });
        console.info("tirtanadi installation cell list size : {}", list.size());
        return list;
    }
    
    public List<ReportPendapatanPdam> listTirtaayumiDtiInstallation(final String startDate, final String endDate, final String detailLocation, final String cellLocation, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        final Map<String, Object> params = new HashMap<>();
        params.put("sts", sts);
        String query = "select STATUS_LOG from PDAM_TIRTAAYUMI_DTI_INSTALLATION_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanPdam> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdam>() {
            @Override
            public ReportPendapatanPdam mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdam pdam = new ReportPendapatanPdam();
                pdam.setJasperLocation(detailLocation);
                pdam.setDetails(listDetailTirtaayumiDtiInstallation(rs.getString("STATUS_LOG"), startDate, endDate, cellLocation, user, branch, teller, supervisi, admin));
                return pdam;
            }
        });
        console.info("pdam tirtaayumiDti installation list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamDetails> listDetailTirtaayumiDtiInstallation(final String statusLog, final String startDate, final String endDate, final String cellLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", statusLog);
        //language=MySQL
        String query = "";
        if (admin)
            query = "SELECT BRANCH_CODE, STATUS_LOG FROM PDAM_TIRTAAYUMI_DTI_INSTALLATION_TRANSACTION WHERE STATUS_LOG = :statusLog GROUP BY BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            query = "SELECT BRANCH_CODE, STATUS_LOG FROM PDAM_TIRTAAYUMI_DTI_INSTALLATION_TRANSACTION WHERE STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' GROUP BY BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanPdamDetails> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamDetails>() {
            @Override
            public ReportPendapatanPdamDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamDetails detailPdam = new ReportPendapatanPdamDetails();
                detailPdam.setJasperLocation(cellLocation);
                detailPdam.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailPdam.setStatusTransaksi(serviceStatus.getStatus(statusLog));
                detailPdam.setDetails(listCellTirtaayumiDtiInstallation(detailPdam.getKodeCabang(), statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return detailPdam;
            }
        });
        console.info("tirtaayumiDti installation details list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamCells> listCellTirtaayumiDtiInstallation(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_ACCOUNT, BILL_FORM, BILL_INFO, BILL_INSTALLATION, BILL_PERIOD, BILL_REPORT, CUBICATION, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, REFF, REFERENCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTAAYUMI_DTI_INSTALLATION_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY CUSTOMER_ID";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_ACCOUNT, BILL_FORM, BILL_INFO, BILL_INSTALLATION, BILL_PERIOD, BILL_REPORT, CUBICATION, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, REFF, REFERENCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTAAYUMI_DTI_INSTALLATION_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY CUSTOMER_ID";
        List<ReportPendapatanPdamCells> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamCells>() {
            @Override
            public ReportPendapatanPdamCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamCells cellPdam = new ReportPendapatanPdamCells();
                cellPdam.setBiaya(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                cellPdam.setChannel("Teller");
                BigDecimal formulir = rs.getBigDecimal("BILL_FORM") != null ? rs.getBigDecimal("BILL_FORM") : BigDecimal.ZERO;
                cellPdam.setJumlah(rs.getBigDecimal("NOMINAL") != null ? rs.getBigDecimal("NOMINAL").subtract(formulir) : BigDecimal.ZERO);
                BigDecimal installation = rs.getBigDecimal("BILL_INSTALLATION") != null ? rs.getBigDecimal("BILL_INSTALLATION") : BigDecimal.ZERO;
                cellPdam.setLainLain(formulir);
                cellPdam.setBills(rs.getInt("BILL_REPORT"));
                cellPdam.setNamaPelanggan(rs.getString("CUSTOMER_NAME"));
                cellPdam.setPrd(rs.getString("BILL_PERIOD"));
                cellPdam.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                cellPdam.setTanggalTransaksi(rs.getDate("TRANSACTION_DATE"));
                cellPdam.setUser(rs.getString("USER_ID"));
                cellPdam.setNomorPelangan(rs.getString("CUSTOMER_ID"));
                return cellPdam;
            }
        });
        console.info("tirtaayumiDti installation cell list size : {}", list.size());
        return list;
    }
    
    public List<ReportPendapatanPdam> listTirtakualoDtiInstallation(final String startDate, final String endDate, final String detailLocation, final String cellLocation, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        final Map<String, Object> params = new HashMap<>();
        params.put("sts", sts);
        String query = "select STATUS_LOG from PDAM_TIRTAKUALO_DTI_INSTALLATION_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanPdam> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdam>() {
            @Override
            public ReportPendapatanPdam mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdam pdam = new ReportPendapatanPdam();
                pdam.setJasperLocation(detailLocation);
                pdam.setDetails(listDetailTirtakualoDtiInstallation(rs.getString("STATUS_LOG"), startDate, endDate, cellLocation, user, branch, teller, supervisi, admin));
                return pdam;
            }
        });
        console.info("pdam tirtakualoDti installation list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamDetails> listDetailTirtakualoDtiInstallation(final String statusLog, final String startDate, final String endDate, final String cellLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", statusLog);
        //language=MySQL
        String query = "";
        if (admin)
            query = "SELECT BRANCH_CODE, STATUS_LOG FROM PDAM_TIRTAKUALO_DTI_INSTALLATION_TRANSACTION WHERE STATUS_LOG = :statusLog GROUP BY BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            query = "SELECT BRANCH_CODE, STATUS_LOG FROM PDAM_TIRTAKUALO_DTI_INSTALLATION_TRANSACTION WHERE STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' GROUP BY BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanPdamDetails> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamDetails>() {
            @Override
            public ReportPendapatanPdamDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamDetails detailPdam = new ReportPendapatanPdamDetails();
                detailPdam.setJasperLocation(cellLocation);
                detailPdam.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailPdam.setStatusTransaksi(serviceStatus.getStatus(statusLog));
                detailPdam.setDetails(listCellTirtakualoDtiInstallation(detailPdam.getKodeCabang(), statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return detailPdam;
            }
        });
        console.info("tirtakualoDti installation details list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamCells> listCellTirtakualoDtiInstallation(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_ACCOUNT, BILL_FORM, BILL_INFO, BILL_INSTALLATION, BILL_PERIOD, BILL_REPORT, CUBICATION, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, REFF, REFERENCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTAKUALO_DTI_INSTALLATION_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY CUSTOMER_ID";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_ACCOUNT, BILL_FORM, BILL_INFO, BILL_INSTALLATION, BILL_PERIOD, BILL_REPORT, CUBICATION, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, REFF, REFERENCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTAKUALO_DTI_INSTALLATION_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY CUSTOMER_ID";
        List<ReportPendapatanPdamCells> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamCells>() {
            @Override
            public ReportPendapatanPdamCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamCells cellPdam = new ReportPendapatanPdamCells();
                cellPdam.setBiaya(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                cellPdam.setChannel("Teller");
                BigDecimal formulir = rs.getBigDecimal("BILL_FORM") != null ? rs.getBigDecimal("BILL_FORM") : BigDecimal.ZERO;
                cellPdam.setJumlah(rs.getBigDecimal("NOMINAL") != null ? rs.getBigDecimal("NOMINAL").subtract(formulir) : BigDecimal.ZERO);
                BigDecimal installation = rs.getBigDecimal("BILL_INSTALLATION") != null ? rs.getBigDecimal("BILL_INSTALLATION") : BigDecimal.ZERO;
                cellPdam.setLainLain(formulir);
                cellPdam.setBills(rs.getInt("BILL_REPORT"));
                cellPdam.setNamaPelanggan(rs.getString("CUSTOMER_NAME"));
                cellPdam.setPrd(rs.getString("BILL_PERIOD"));
                cellPdam.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                cellPdam.setTanggalTransaksi(rs.getDate("TRANSACTION_DATE"));
                cellPdam.setUser(rs.getString("USER_ID"));
                cellPdam.setNomorPelangan(rs.getString("CUSTOMER_ID"));
                return cellPdam;
            }
        });
        console.info("tirtakualoDti installation cell list size : {}", list.size());
        return list;
    }
    
    public List<ReportPendapatanPdam> listTirtatanjungDtiInstallation(final String startDate, final String endDate, final String detailLocation, final String cellLocation, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        final Map<String, Object> params = new HashMap<>();
        params.put("sts", sts);
        String query = "select STATUS_LOG from PDAM_TIRTATANJUNG_DTI_INSTALLATION_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanPdam> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdam>() {
            @Override
            public ReportPendapatanPdam mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdam pdam = new ReportPendapatanPdam();
                pdam.setJasperLocation(detailLocation);
                pdam.setDetails(listDetailTirtatanjungDtiInstallation(rs.getString("STATUS_LOG"), startDate, endDate, cellLocation, user, branch, teller, supervisi, admin));
                return pdam;
            }
        });
        console.info("pdam tirtatanjungDti installation list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamDetails> listDetailTirtatanjungDtiInstallation(final String statusLog, final String startDate, final String endDate, final String cellLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", statusLog);
        //language=MySQL
        String query = "";
        if (admin)
            query = "SELECT BRANCH_CODE, STATUS_LOG FROM PDAM_TIRTATANJUNG_DTI_INSTALLATION_TRANSACTION WHERE STATUS_LOG = :statusLog GROUP BY BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            query = "SELECT BRANCH_CODE, STATUS_LOG FROM PDAM_TIRTATANJUNG_DTI_INSTALLATION_TRANSACTION WHERE STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' GROUP BY BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanPdamDetails> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamDetails>() {
            @Override
            public ReportPendapatanPdamDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamDetails detailPdam = new ReportPendapatanPdamDetails();
                detailPdam.setJasperLocation(cellLocation);
                detailPdam.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailPdam.setStatusTransaksi(serviceStatus.getStatus(statusLog));
                detailPdam.setDetails(listCellTirtatanjungDtiInstallation(detailPdam.getKodeCabang(), statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return detailPdam;
            }
        });
        console.info("tirtatanjungDti installation details list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamCells> listCellTirtatanjungDtiInstallation(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_ACCOUNT, BILL_FORM, BILL_INFO, BILL_INSTALLATION, BILL_PERIOD, BILL_REPORT, CUBICATION, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, REFF, REFERENCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTATANJUNG_DTI_INSTALLATION_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY CUSTOMER_ID";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, BILL_ACCOUNT, BILL_FORM, BILL_INFO, BILL_INSTALLATION, BILL_PERIOD, BILL_REPORT, CUBICATION, CUSTOMER_ADDRESS, CUSTOMER_ID, CUSTOMER_NAME, GROUP_RATE, NOMINAL, REFF, REFERENCE_NUMBER, ADMIN_CHARGE, ARCHIVE_NUMBER from PDAM_TIRTATANJUNG_DTI_INSTALLATION_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY CUSTOMER_ID";
        List<ReportPendapatanPdamCells> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamCells>() {
            @Override
            public ReportPendapatanPdamCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamCells cellPdam = new ReportPendapatanPdamCells();
                cellPdam.setBiaya(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                cellPdam.setChannel("Teller");
                BigDecimal formulir = rs.getBigDecimal("BILL_FORM") != null ? rs.getBigDecimal("BILL_FORM") : BigDecimal.ZERO;
                cellPdam.setJumlah(rs.getBigDecimal("NOMINAL") != null ? rs.getBigDecimal("NOMINAL").subtract(formulir) : BigDecimal.ZERO);
                BigDecimal installation = rs.getBigDecimal("BILL_INSTALLATION") != null ? rs.getBigDecimal("BILL_INSTALLATION") : BigDecimal.ZERO;
                cellPdam.setLainLain(formulir);
                cellPdam.setBills(rs.getInt("BILL_REPORT"));
                cellPdam.setNamaPelanggan(rs.getString("CUSTOMER_NAME"));
                cellPdam.setPrd(rs.getString("BILL_PERIOD"));
                cellPdam.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                cellPdam.setTanggalTransaksi(rs.getDate("TRANSACTION_DATE"));
                cellPdam.setUser(rs.getString("USER_ID"));
                cellPdam.setNomorPelangan(rs.getString("CUSTOMER_ID"));
                return cellPdam;
            }
        });
        console.info("tirtatanjungDti installation cell list size : {}", list.size());
        return list;
    }

    public List<ReportPendapatanPdam> listTirtakualo(final String startDate, final String endDate, final String detailLocation, final String cellLocation, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        final Map<String, Object> params = new HashMap<>();
        params.put("sts", sts);
        String query = "select STATUS_LOG from PDAM_TIRTAKUALO_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanPdam> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdam>() {
            @Override
            public ReportPendapatanPdam mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdam pdam = new ReportPendapatanPdam();
                pdam.setJasperLocation(detailLocation);
                pdam.setDetails(listDetailTirtakualo(rs.getString("STATUS_LOG"), startDate, endDate, cellLocation, user, branch, teller, supervisi, admin));
                return pdam;
            }
        });
        console.info("pdam tirtanadi installation list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamDetails> listDetailTirtakualo(final String statusLog, final String startDate, final String endDate, final String cellLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", statusLog);
        //language=MySQL
        String query = "";
        if (admin)
            query = "SELECT BRANCH_CODE, STATUS_LOG FROM PDAM_TIRTAKUALO_TRANSACTION WHERE STATUS_LOG = :statusLog GROUP BY BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            query = "SELECT BRANCH_CODE, STATUS_LOG FROM PDAM_TIRTAKUALO_TRANSACTION WHERE STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' GROUP BY BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanPdamDetails> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamDetails>() {
            @Override
            public ReportPendapatanPdamDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamDetails detailPdam = new ReportPendapatanPdamDetails();
                detailPdam.setJasperLocation(cellLocation);
                detailPdam.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailPdam.setStatusTransaksi(serviceStatus.getStatus(statusLog));
                detailPdam.setDetails(listCellTirtakualo(detailPdam.getKodeCabang(), statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return detailPdam;
            }
        });
        console.info("tirtanadi installation details list size : {}", list.size());
        return list;
    }

    private List<ReportPendapatanPdamCells> listCellTirtakualo(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, ADMIN_CHARGE, ALAMAT, ANGSURAN_SAMBUNGAN, ARCHIVE_NUMBER, BEA_SALURAN, BULAN_SAMBUNGAN, CURRENCY_CODE, GOLONGAN, HARGA, MATERAI, METER_AKHIR, METER_AWAL, NAMA_PELANGGAN, NOMOR_PELANGGAN, NOMINAL, PERIODE, RAWAT, REFF, SOURCE_NUMBER, TANGGAL_CEK, TOTAL_SAMPAH from PDAM_TIRTAKUALO_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY USER_ID";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, ADMIN_CHARGE, ALAMAT, ANGSURAN_SAMBUNGAN, ARCHIVE_NUMBER, BEA_SALURAN, BULAN_SAMBUNGAN, CURRENCY_CODE, GOLONGAN, HARGA, MATERAI, METER_AKHIR, METER_AWAL, NAMA_PELANGGAN, NOMOR_PELANGGAN, NOMINAL, PERIODE, RAWAT, REFF, SOURCE_NUMBER, TANGGAL_CEK, TOTAL_SAMPAH from PDAM_TIRTAKUALO_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY USER_ID";
        List<ReportPendapatanPdamCells> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPdamCells>() {
            @Override
            public ReportPendapatanPdamCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPdamCells cellPdam = new ReportPendapatanPdamCells();
                //                cellPdam.setBiaya();
                cellPdam.setTanggalTransaksi(rs.getDate("TRANSACTION_DATE"));
                cellPdam.setUser(rs.getString("USER_ID"));
                cellPdam.setBiaya(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                cellPdam.setNamaPelanggan(rs.getString("NAMA_PELANGGAN"));
                cellPdam.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                cellPdam.setNomorPelangan(rs.getString("NOMOR_PELANGGAN"));
                cellPdam.setJumlah(rs.getBigDecimal("HARGA") != null ? rs.getBigDecimal("HARGA") : BigDecimal.ZERO);
                BigDecimal materai = rs.getBigDecimal("MATERAI") != null ? rs.getBigDecimal("MATERAI") : BigDecimal.ZERO;
                BigDecimal rawat = rs.getBigDecimal("RAWAT") != null ? rs.getBigDecimal("RAWAT") : BigDecimal.ZERO;
                cellPdam.setLainLain(materai.add(rawat));
                cellPdam.setChannel("Teller");
                cellPdam.setPrd(rs.getString("PERIODE"));
                return cellPdam;
            }
        });
        console.info("tirtanadi installation cell list size : {}", list.size());
        return list;
    }
}
