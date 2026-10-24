package com.multipolar.uiteller.backend.service.report.dynamic;


import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.margin;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.multipolar.uiteller.backend.repository.util.DataModel;
import com.multipolar.uiteller.backend.service.Utils;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.SubreportBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JREmptyDataSource;

@Service("defaultReportPendapatanService")
public class DefaultReportPendapatanService extends BaseReportPendapatanServiceImpl {

	private static Logger logger = LoggerFactory.getLogger(DefaultReportPendapatanService.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public JasperReportBuilder buildReport(String viewName, Map<String, String> params)
			throws SQLException, DRException, ParseException {
	
		final SimpleDateFormat formatter = new SimpleDateFormat("M/d/yyyy");
		final Date startDate = StringUtils.isEmpty(params.get("startDate")) ?  Utils.getFirstDateOfYear() : formatter.parse(params.get("startDate")) ;
		final Date endDate = StringUtils.isEmpty(params.get("endDate")) ? new Date() : formatter.parse(params.get("endDate"));
		final String headerTitle = "LAPORAN PENDAPATAN BANK SUMUT";
		final String period = new StringBuilder(formatter.format(startDate)).append(" s/d ").append(formatter.format(endDate)).toString();
		final String headerPeriod = "PERIODE : " + period;
		
		final Map<String, Object> dbParams = prepareDBParams(params);
		dbParams.put("TRANSACTION_DATE", new Date[] {startDate, endDate});
		DataModel dataModel = new DataModel(dbParams);
		
		final Map<String, Object> viewReportData = getReportRelatedData(viewName, dataModel);
		final List<String> listStatusLog = (List<String>) viewReportData.get("LIST_STATUS_LOG");
		final Map<String, TextColumnBuilder<?>> sbrColumnBuilders = (Map<String, TextColumnBuilder<?>>) viewReportData.get("COLUMN_BUILDERS");
		
		try {
			final VerticalListBuilder titleBuilder = DynamicReports.cmp.verticalList()
					.add(DynamicReports.cmp.verticalGap(10))
					.add(DynamicReports.cmp.text(headerTitle)
							.setStyle(DynamicReports.stl.style().setFontSize(12).bold())
							.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER))
					.add(DynamicReports.cmp.verticalGap(15))
					.add(DynamicReports.cmp.text(headerPeriod)
							.setStyle(DynamicReports.stl.style().setFontSize(10))
							.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER))
					.add(DynamicReports.cmp.verticalGap(10));
			
			final SubreportDBDataSourceExpression subreportDatasource = new SubreportDBDataSourceExpression(viewName, listStatusLog, dataModel);
			SubreportBuilder subreport = cmp.subreport(new SubreportExpression(sbrColumnBuilders, listStatusLog))
					.setDataSource(subreportDatasource);
			
			JasperReportBuilder builder = DynamicReports.report()
					.setPageMargin(margin(20))
					.title(titleBuilder)
					.detail(subreport, DynamicReports.cmp.verticalGap(20))
					.setDataSource(new JREmptyDataSource(listStatusLog.size()));

			return builder;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	protected Map<String, Object> prepareDBParams(Map<String, String> requestParams){
		requestParams.remove("subTransactionType");
		requestParams.remove("reportType");
		requestParams.remove("startDate");
		requestParams.remove("endDate");
		return new HashMap<String,Object>(requestParams);
	}
	
	
}

