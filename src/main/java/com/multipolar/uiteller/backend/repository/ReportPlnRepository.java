package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.report.pln.*;
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
public class ReportPlnRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private TransactionStatus serviceStatus;

    public List<ReportPendapatanPlnNonTagihanListrik> nontaglistList(final String startDate, final String endDate, final String jasperDetailLocation, final String jasperCellLocation, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        String query = "select STATUS_LOG from PLN_NONTAGLIST_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanPlnNonTagihanListrik> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPlnNonTagihanListrik>() {
            @Override
            public ReportPendapatanPlnNonTagihanListrik mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPlnNonTagihanListrik nonTaglist = new ReportPendapatanPlnNonTagihanListrik();
                nonTaglist.setJasperLocation(jasperDetailLocation);
                nonTaglist.setDetails(nonTaglistDetail(rs.getString("STATUS_LOG"), startDate, endDate, jasperCellLocation, user, branch, teller, supervisi, admin));
                return nonTaglist;
            }
        });
        return list;
    }

    private List<ReportPendapatanPlnNonTagihanListrikDetails> nonTaglistDetail(final String statusLog, final String startDate, final String endDate, final String jasperCellLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", statusLog);
        String query = "";
        if (admin)
            query = "select BRANCH_CODE, STATUS_LOG from PLN_NONTAGLIST_TRANSACTION where STATUS_LOG = :statusLog group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            query = "select BRANCH_CODE, STATUS_LOG from PLN_NONTAGLIST_TRANSACTION where STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanPlnNonTagihanListrikDetails> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPlnNonTagihanListrikDetails>() {
            @Override
            public ReportPendapatanPlnNonTagihanListrikDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPlnNonTagihanListrikDetails detailNontaglist = new ReportPendapatanPlnNonTagihanListrikDetails();
                detailNontaglist.setJasperLocation(jasperCellLocation);
                detailNontaglist.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailNontaglist.setStatusTransaksi(serviceStatus.getStatus(statusLog));
                detailNontaglist.setDetails(nonTaglistCell(detailNontaglist.getKodeCabang(), statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return detailNontaglist;
            }
        });
        return list;
    }

    private List<ReportPendapatanPlnNonTagihanListrikCells> nonTaglistCell(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, REGISTRATION_NUMBER, CUSTOMER_NAME, NOMINAL, BILL_REF_ONE, BILL_REF_TWO, REFF, CURRENCY_CODE, ADMIN_CHARGE, FOOTER,  ARCHIVE_NUMBER from PLN_NONTAGLIST_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY REGISTRATION_NUMBER";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, REGISTRATION_NUMBER, CUSTOMER_NAME, NOMINAL, BILL_REF_ONE, BILL_REF_TWO, REFF, CURRENCY_CODE, ADMIN_CHARGE, FOOTER,  ARCHIVE_NUMBER from PLN_NONTAGLIST_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY REGISTRATION_NUMBER";
        List<ReportPendapatanPlnNonTagihanListrikCells> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPlnNonTagihanListrikCells>() {

            @Override
            public ReportPendapatanPlnNonTagihanListrikCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPlnNonTagihanListrikCells cellNonTaglist = new ReportPendapatanPlnNonTagihanListrikCells();
                cellNonTaglist.setUser(rs.getString("USER_ID"));
                cellNonTaglist.setBiaya(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                cellNonTaglist.setChannel("Teller");
                cellNonTaglist.setJumlah(rs.getBigDecimal("NOMINAL") != null ? rs.getBigDecimal("NOMINAL") : BigDecimal.ZERO);
                cellNonTaglist.setNamaPelanggan(rs.getString("CUSTOMER_NAME"));
                cellNonTaglist.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                cellNonTaglist.setNomorRegister(rs.getString("REGISTRATION_NUMBER"));
                cellNonTaglist.setTanggalBayar(rs.getDate("TRANSACTION_DATE"));
                return cellNonTaglist;
            }
        });
        return list;
    }

    public List<ReportPendapatanPlnPostpaid> postpaidList(final String startDate, final String endDate, final String reportDetailLocation, final String reportCellLocation, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        HashMap<String, Object> params = new HashMap<>();
        String sql = "select STATUS_LOG from PLN_POSTPAID_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanPlnPostpaid> list = this.jdbcTemplate.query(sql, params, new RowMapper<ReportPendapatanPlnPostpaid>() {
            @Override
            public ReportPendapatanPlnPostpaid mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPlnPostpaid postpaid = new ReportPendapatanPlnPostpaid();
                postpaid.setJasperLocation(reportDetailLocation);
                postpaid.setDetails(postpaidDetail(rs.getString("STATUS_LOG"), startDate, endDate, reportCellLocation, user, branch, teller, supervisi, admin));
                return postpaid;
            }
        });
        return list;
    }

    public List<ReportPendapatanPlnPrepaid> prepaidList(final String startDate, final String endDate, final String reportDetailLocation, final String reportCellLocation, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        HashMap<String, Object> params = new HashMap<>();
        String sql = "select STATUS_LOG from PLN_PREPAID_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanPlnPrepaid> list = this.jdbcTemplate.query(sql, params, new RowMapper<ReportPendapatanPlnPrepaid>() {
            @Override
            public ReportPendapatanPlnPrepaid mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPlnPrepaid prepaid = new ReportPendapatanPlnPrepaid();
                prepaid.setJasperLocation(reportDetailLocation);
                prepaid.setDetails(prepaidDetail(rs.getString("STATUS_LOG"), startDate, endDate, reportCellLocation, user, branch, teller, supervisi, admin));
                return prepaid;
            }
        });
        return list;
    }

    private List<ReportPendapatanPlnPostpaidDetail> postpaidDetail(final String statusLog, final String startDate, final String endDate, final String jasperLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("statusLog", statusLog);
        String query = "";
        if (admin)
            query = "select BRANCH_CODE, STATUS_LOG from PLN_POSTPAID_TRANSACTION where STATUS_LOG = :statusLog group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            query = "select BRANCH_CODE, STATUS_LOG from PLN_POSTPAID_TRANSACTION where STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanPlnPostpaidDetail> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPlnPostpaidDetail>() {
            @Override
            public ReportPendapatanPlnPostpaidDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPlnPostpaidDetail detail = new ReportPendapatanPlnPostpaidDetail();
                detail.setJasperLocation(jasperLocation);
                detail.setTransaksiStatus(serviceStatus.getStatus(statusLog));
                detail.setKodeCabang(rs.getString("BRANCH_CODE"));
                detail.setDetails(postpaidCell(detail.getKodeCabang(), statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return detail;
            }
        });
        return list;
    }

    private List<ReportPendapatanPlnPrepaidDetails> prepaidDetail(final String statusLog, final String startDate, final String endDate, final String jasperLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("statusLog", statusLog);
        String query = "";
        if (admin)
            query = "select BRANCH_CODE, STATUS_LOG from PLN_PREPAID_TRANSACTION where STATUS_LOG = :statusLog group by BRANCH_CODE, STATUS_LOG";
        else
            query = "select BRANCH_CODE, STATUS_LOG from PLN_PREPAID_TRANSACTION where STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' group by BRANCH_CODE, STATUS_LOG";
        List<ReportPendapatanPlnPrepaidDetails> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPlnPrepaidDetails>() {
            @Override
            public ReportPendapatanPlnPrepaidDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPlnPrepaidDetails detail = new ReportPendapatanPlnPrepaidDetails();
                detail.setJasperLocation(jasperLocation);
                detail.setTransaksiStatus(serviceStatus.getStatus(statusLog));
                detail.setKodeCabang(rs.getString("BRANCH_CODE"));
                detail.setDetails(prepaidCell(detail.getKodeCabang(), statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return detail;
            }
        });
        return list;
    }

    private List<ReportPendapatanPlnPostpaidCells> postpaidCell(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, PRODUCT_CODE, CUSTOMER_ID, CUSTOMER_NAME, BILL_NOMINAL, ADMIN_CHARGE, BILL_REF_ONE, BILL_REF_TWO, FOOTER, SOURCE_NUMBER, ARCHIVE_NUMBER, MONTH_YEAR from PLN_POSTPAID_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY CUSTOMER_ID";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, PRODUCT_CODE, CUSTOMER_ID, CUSTOMER_NAME, BILL_NOMINAL, ADMIN_CHARGE, BILL_REF_ONE, BILL_REF_TWO, FOOTER, SOURCE_NUMBER, ARCHIVE_NUMBER, MONTH_YEAR from PLN_POSTPAID_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY CUSTOMER_ID";
        List<ReportPendapatanPlnPostpaidCells> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPlnPostpaidCells>() {
            @Override
            public ReportPendapatanPlnPostpaidCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPlnPostpaidCells cell = new ReportPendapatanPlnPostpaidCells();
                cell.setBiaya(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                cell.setChannel("Teller");
                cell.setDenda(BigDecimal.ZERO);
                cell.setJumlah(rs.getBigDecimal("BILL_NOMINAL") != null ? rs.getBigDecimal("BILL_NOMINAL") : BigDecimal.ZERO);
                cell.setNamaPelanggan(rs.getString("CUSTOMER_NAME"));
                cell.setNop(rs.getString("CUSTOMER_ID"));
                cell.setNoArsip(rs.getString("ARCHIVE_NUMBER"));
                cell.setPeriode(rs.getString("MONTH_YEAR"));
                cell.setTanggalBayar(rs.getDate("TRANSACTION_DATE"));
                cell.setUser(rs.getString("USER_ID"));
                return cell;
            }
        });
        return list;
    }

    private List<ReportPendapatanPlnPrepaidCell> prepaidCell(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, PRODUCT_CODE, CUSTOMER_ID, CUSTOMER_NAME, BILL_NOMINAL, ADMIN_CHARGE, BILL_REF_ONE, BILL_REF_TWO, FOOTER, SOURCE_NUMBER, ARCHIVE_NUMBER from PLN_PREPAID_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY CUSTOMER_ID";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, PRODUCT_CODE, CUSTOMER_ID, CUSTOMER_NAME, BILL_NOMINAL, ADMIN_CHARGE, BILL_REF_ONE, BILL_REF_TWO, FOOTER, SOURCE_NUMBER, ARCHIVE_NUMBER from PLN_PREPAID_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY CUSTOMER_ID";
        List<ReportPendapatanPlnPrepaidCell> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanPlnPrepaidCell>() {
            @Override
            public ReportPendapatanPlnPrepaidCell mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanPlnPrepaidCell cell = new ReportPendapatanPlnPrepaidCell();
                cell.setBiaya(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                cell.setChannel("Teller");
                cell.setNamaPelanggan(rs.getString("CUSTOMER_NAME"));
                cell.setNomorMeter(rs.getString("CUSTOMER_ID"));
                cell.setNomorPelanggan(rs.getString("CUSTOMER_ID"));
                cell.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                cell.setTanggalBayar(rs.getDate("TRANSACTION_DATE"));
                cell.setJumlahBayar(rs.getBigDecimal("BILL_NOMINAL") != null ? rs.getBigDecimal("BILL_NOMINAL") : BigDecimal.ZERO);
                cell.setUser(rs.getString("USER_ID"));
                return cell;
            }
        });
        return list;
    }
}
