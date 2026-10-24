package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.report.finnet.*;
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
public class ReportFinnetRepository {

    private final static Logger console = LoggerFactory.getLogger(ReportFinnetRepository.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private TransactionStatus serviceStatus;

    public List<ReportPendapatanFinnetPstn> listPstn(final String startDate, final String endDate, final String jasperDetailLocation, final String jasperCellLocation, final String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        console.info("{detailsLocation: {}, cellLocation: {}}", jasperDetailLocation, jasperCellLocation);
        Map<String, Object> params = new HashMap<>();
        params.put("sts", sts);
        String sql = "select STATUS_LOG  from FINNET_PSTN_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanFinnetPstn> list = this.jdbcTemplate.query(sql, params, new RowMapper<ReportPendapatanFinnetPstn>() {
            @Override
            public ReportPendapatanFinnetPstn mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanFinnetPstn master = new ReportPendapatanFinnetPstn();
                master.setJasperLocation(jasperDetailLocation);
                String statusLog = rs.getString("STATUS_LOG");
                master.setDetails(pstnListDetails(statusLog, jasperCellLocation, startDate, endDate, user, branch, teller, supervisi, admin));
                return master;
            }
        });
        console.info("jumlah master finnet pstn {}", list.size());
        return list;
    }

    private List<ReportPendapatanFinnetPstnDetails> pstnListDetails(final String statusLog, final String jasperLocation, final String startDate, final String endDate, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", statusLog);
        String sql = "";
        if (admin)
            sql = "select BRANCH_CODE, STATUS_LOG from FINNET_PSTN_TRANSACTION where STATUS_LOG = :statusLog group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            sql = "select BRANCH_CODE, STATUS_LOG from FINNET_PSTN_TRANSACTION where STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanFinnetPstnDetails> list = this.jdbcTemplate.query(sql, params, new RowMapper<ReportPendapatanFinnetPstnDetails>() {
            @Override
            public ReportPendapatanFinnetPstnDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanFinnetPstnDetails details = new ReportPendapatanFinnetPstnDetails();
                details.setJasperLocation(jasperLocation);
                details.setKodeCabang(rs.getString("BRANCH_CODE"));
                details.setStatusTransaksi(serviceStatus.getStatus(statusLog));
                details.setDetails(pstnListCell(details.getKodeCabang(), statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return details;
            }
        });
        console.info("jumlah detail finnet pstn by brancCode: {}, jumlah: {}", statusLog, list.size());
        return list;
    }

    private List<ReportPendapatanFinnetPstnCells> pstnListCell(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", status);
        params.put("branchCode", branchCode);
        params.put("start", startDate);
        params.put("end", endDate);
        String query = "";
        if (admin || supervisi)
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, AREA_CODE, PHONE_NUMBER, DIVRE_CODE, DATEL_CODE, BILL_NUMBER, BILL_REF_ONE, BILL_VALUE_ONE, BILL_REF_TWO, BILL_VALUE_TWO, BILL_REF_THREE, BILL_VALUE_THREE, NOMINAL, CURRENCY_CODE, SOURCE_NUMBER, CUSTOMER_NAME, ARCHIVE_NUMBER, ADMIN_CHARGE from FINNET_PSTN_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY CUSTOMER_NAME";
        else
            query = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, AREA_CODE, PHONE_NUMBER, DIVRE_CODE, DATEL_CODE, BILL_NUMBER, BILL_REF_ONE, BILL_VALUE_ONE, BILL_REF_TWO, BILL_VALUE_TWO, BILL_REF_THREE, BILL_VALUE_THREE, NOMINAL, CURRENCY_CODE, SOURCE_NUMBER, CUSTOMER_NAME, ARCHIVE_NUMBER, ADMIN_CHARGE from FINNET_PSTN_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY CUSTOMER_NAME";
        List<ReportPendapatanFinnetPstnCells> list = this.jdbcTemplate.query(query, params, new RowMapper<ReportPendapatanFinnetPstnCells>() {
            @Override
            public ReportPendapatanFinnetPstnCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanFinnetPstnCells cell = new ReportPendapatanFinnetPstnCells();
                BigDecimal bill1 = rs.getBigDecimal("bill_value_one") != null ? rs.getBigDecimal("bill_value_one") : BigDecimal.ZERO;
                BigDecimal bill2 = rs.getBigDecimal("bill_value_two") != null ? rs.getBigDecimal("bill_value_two") : BigDecimal.ZERO;
                BigDecimal bill3 = rs.getBigDecimal("bill_value_three") != null ? rs.getBigDecimal("bill_value_three") : BigDecimal.ZERO;

                cell.setBiaya(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                cell.setJumlah(bill1.add(bill2).add(bill3));
                cell.setNamaPelanggan(rs.getString("customer_name"));
                String codeArea = rs.getString("area_code") != null ? rs.getString("area_code") : "";
                String phoneNumber = rs.getString("phone_number") != null ? rs.getString("phone_number") : "";
                cell.setNomorPelanggan(new StringBuilder(codeArea).append(phoneNumber).toString());
                cell.setChannel("Teller");
                cell.setUser(rs.getString("user_id"));
                cell.setTanggalBayar(rs.getDate("transaction_date"));
                cell.setNomorArsip(rs.getString("archive_number"));
                return cell;
            }
        });
        console.info("jumlah master finnet pstn {branchCode: {}, status: {}, jumlah: {}} ", branchCode, status, list.size());
        return list;
    }

    public List<ReportPendapatanFinnetSpeedy> listSpeedy(final String startDate, final String endDate, final String detailLocation, final String cellLocation, String sts, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("sts", sts);
        String sql = "select STATUS_LOG from FINNET_SPEEDY_TRANSACTION " +
                serviceStatus.getSts(sts) +
                " group by STATUS_LOG";
        List<ReportPendapatanFinnetSpeedy> list = this.jdbcTemplate.query(sql, params, new RowMapper<ReportPendapatanFinnetSpeedy>() {
            @Override
            public ReportPendapatanFinnetSpeedy mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanFinnetSpeedy speedy = new ReportPendapatanFinnetSpeedy();
                speedy.setJasperLocation(detailLocation);
                String statusLog = rs.getString("STATUS_LOG");
                speedy.setDetails(speedyListDetail(statusLog, startDate, endDate, cellLocation, user, branch, teller, supervisi, admin));
                return speedy;
            }
        });
        return list;
    }

    private List<ReportPendapatanFinnetSpeedyDetails> speedyListDetail(final String statusLog, final String startDate, final String endDate, final String jasperLocation, final String user, final String branch, final Boolean teller, final Boolean supervisi, final Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("statusLog", statusLog);
        String sql = "";
        if (admin)
            sql = "select BRANCH_CODE, STATUS_LOG from FINNET_SPEEDY_TRANSACTION where STATUS_LOG = :statusLog group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        else
            sql = "select BRANCH_CODE, STATUS_LOG from FINNET_SPEEDY_TRANSACTION where STATUS_LOG = :statusLog AND BRANCH_CODE = '" + branch + "' group by BRANCH_CODE, STATUS_LOG ORDER BY BRANCH_CODE";
        List<ReportPendapatanFinnetSpeedyDetails> list = this.jdbcTemplate.query(sql, params, new RowMapper<ReportPendapatanFinnetSpeedyDetails>() {
            @Override
            public ReportPendapatanFinnetSpeedyDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanFinnetSpeedyDetails detailSpeedy = new ReportPendapatanFinnetSpeedyDetails();
                detailSpeedy.setJasperLocation(jasperLocation);
                detailSpeedy.setKodeCabang(rs.getString("BRANCH_CODE"));
                detailSpeedy.setStatusTransaksi(serviceStatus.getStatus(statusLog));
                detailSpeedy.setDetails(speedyListCell(detailSpeedy.getKodeCabang(), statusLog, startDate, endDate, user, branch, teller, supervisi, admin));
                return detailSpeedy;
            }
        });
        return list;
    }

    private List<ReportPendapatanFinnetSpeedyCells> speedyListCell(String branchCode, String status, String startDate, String endDate, String user, String branch, Boolean teller, Boolean supervisi, Boolean admin) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
        params.put("statusLog", status);
        params.put("start", startDate);
        params.put("end", endDate);
        String sql = "";
        if (admin || supervisi)
            sql = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, CUSTOMER_ID, DIVRE_CODE, DATEL_CODE, BILL_REF_THREE, BILL_VALUE_THREE, BILL_REF_TWO, BILL_VALUE_TWO, BILL_REF_ONE, BILL_VALUE_ONE, ADMIN_CHARGE, NOMINAL, REFF, SOURCE_NUMBER, CURRENCY_CODE, CUSTOMER_NAME, ARCHIVE_NUMBER from FINNET_SPEEDY_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) ORDER BY CUSTOMER_NAME";
        else
            sql = "select TRACE_NUMBER, BRANCH_CODE, STATUS_LOG, TRANSACTION_DATE, USER_ID, CUSTOMER_ID, DIVRE_CODE, DATEL_CODE, BILL_REF_THREE, BILL_VALUE_THREE, BILL_REF_TWO, BILL_VALUE_TWO, BILL_REF_ONE, BILL_VALUE_ONE, ADMIN_CHARGE, NOMINAL, REFF, SOURCE_NUMBER, CURRENCY_CODE, CUSTOMER_NAME, ARCHIVE_NUMBER from FINNET_SPEEDY_TRANSACTION_DETAIL where BRANCH_CODE = :branchCode and STATUS_LOG = :statusLog and (TRANSACTION_DATE BETWEEN TO_DATE(:start, 'DDMMYYYYhh24miss') and TO_DATE(:end, 'DDMMYYYYhh24miss')) AND USER_ID = '" + user + "' ORDER BY CUSTOMER_NAME";
        List<ReportPendapatanFinnetSpeedyCells> list = this.jdbcTemplate.query(sql, params, new RowMapper<ReportPendapatanFinnetSpeedyCells>() {
            @Override
            public ReportPendapatanFinnetSpeedyCells mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportPendapatanFinnetSpeedyCells cellSpeedy = new ReportPendapatanFinnetSpeedyCells();
                cellSpeedy.setBiaya(rs.getBigDecimal("ADMIN_CHARGE") != null ? rs.getBigDecimal("ADMIN_CHARGE") : BigDecimal.ZERO);
                BigDecimal bil1 = rs.getBigDecimal("BILL_VALUE_ONE") != null ? rs.getBigDecimal("BILL_VALUE_ONE") : BigDecimal.ZERO;
                BigDecimal bil2 = rs.getBigDecimal("BILL_VALUE_TWO") != null ? rs.getBigDecimal("BILL_VALUE_TWO") : BigDecimal.ZERO;
                BigDecimal bil3 = rs.getBigDecimal("BILL_VALUE_THREE") != null ? rs.getBigDecimal("BILL_VALUE_THREE") : BigDecimal.ZERO;
                cellSpeedy.setJumlah(bil1.add(bil2).add(bil3));
                cellSpeedy.setNamaPelanggan(rs.getString("CUSTOMER_NAME"));
                cellSpeedy.setNomorPelanggan(rs.getString("CUSTOMER_ID"));
                cellSpeedy.setChannel("Teller");
                cellSpeedy.setNomorArsip(rs.getString("ARCHIVE_NUMBER"));
                cellSpeedy.setUser(rs.getString("USER_ID"));
                cellSpeedy.setTanggalBayar(rs.getDate("TRANSACTION_DATE"));
                return cellSpeedy;
            }
        });
        return list;
    }
}
