package com.multipolar.uiteller.backend.service.report.dynamic;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.exception.DRException;

public interface BaseReportPendapatanService {
	String getViewName(String trxType, String subTrxType, String reportType);
	String getPdfReportName(String trxType, String subTrxType, String reportType);
	JasperReportBuilder buildReport(String viewName, Map<String, String> params) throws SQLException, DRException, IOException, ParseException ;
}
