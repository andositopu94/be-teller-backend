package com.multipolar.uiteller.backend.controller.report;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.multipolar.uiteller.backend.resource.ReportException;
import com.multipolar.uiteller.backend.service.report.dynamic.BaseReportPendapatanService;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.exception.DRException;

@Controller
@RequestMapping("/downloads/pdf/income")
public class ReportPendapatanController {
	
	@Autowired
	@Qualifier("defaultReportPendapatanService")
	private BaseReportPendapatanService reportService;
	
	/**
	 * Request URL Example : http://localhost:9090/downloads/pdf/income/report/MPN/?startDate=&endDate=&reportType=ednp&subTransactionType=
	 * Request URL Example : http://localhost:9090/downloads/pdf/income/report/PBB-STS/?startDate=1/1/2018&endDate=5/4/2018&reportType=&subTransactionType=PBB-STS
	 * Request URL Example : http://localhost:9090/downloads/pdf/income/report/PLN/?startDate=1/1/2018&endDate=5/4/2018&reportType=&subTransactionType=POSTPAID
	 * @param trasactionType
	 * @param subTransactionType
	 * @param reportType
	 * @param startDate : format MM/dd/YYYY
	 * @param endDate : format MM/dd/YYYY
	 * query params that have to match with DB FIELDS
	 * @param BRANCH_CODE
	 * @param CHANNEL
	 * @param STATUS_LOG
	 */
	@RequestMapping(value = "/report/{trasactionType}", method = RequestMethod.GET)
	public String laporanPendapatan(
			ModelMap model,
			HttpServletRequest request, 
			HttpServletResponse response,
			@PathVariable("trasactionType") String trxType,
			@RequestParam Map<String,String> allRequestParams) throws ReportException, SQLException, DRException, IOException, ParseException {

		final String subTrxType = allRequestParams.containsKey("subTransactionType") ? allRequestParams.get("subTransactionType") : null;
		final String reportType = allRequestParams.containsKey("reportType") ? allRequestParams.get("reportType") : null;
		
		final String viewName = reportService.getViewName(trxType, subTrxType, reportType);
		final String reportName = reportService.getPdfReportName(trxType, subTrxType, reportType);
		
		response.setContentType("application/pdf");
		response.setHeader("Content-disposition","attachment; filename="+reportName);
		
		JasperReportBuilder builder = reportService.buildReport(viewName.replaceAll("[-]", ""), allRequestParams);
		builder.setReportName(reportName);
		
		final OutputStream os = response.getOutputStream();
		try {
			builder.toPdf(os);
		} catch (Exception e) {
			os.flush();
			os.close();
			throw e;
		}
		
		return null;
	}
}
