package com.multipolar.uiteller.backend.service.report.dynamic;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.multipolar.uiteller.backend.repository.util.DataModel;
import com.multipolar.uiteller.backend.repository.util.HQLCriteriaModel;
import com.multipolar.uiteller.backend.service.TransactionStatus;
import com.multipolar.uiteller.backend.util.report.Templates;

import lombok.Getter;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.VariableBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.group.ColumnGroupBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.builder.subtotal.AggregationSubtotalBuilder;
import net.sf.dynamicreports.report.constant.Calculation;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.dynamicreports.report.definition.datatype.DRIDataType;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

public abstract class BaseReportPendapatanServiceImpl implements BaseReportPendapatanService {

	private static Logger logger = LoggerFactory.getLogger(BaseReportPendapatanServiceImpl.class);
	protected static final String REPORT_VERSION = "V1";

	@Autowired
	protected JdbcTemplate jdbcTemplate;
	@Autowired
	protected TransactionStatus serviceStatus;
	@Autowired
	protected DynamicReportService serviceReport;

	@Override
	public abstract JasperReportBuilder buildReport(String viewName,Map<String, String> params)
			throws SQLException, DRException, IOException, ParseException;

	@Override
	public String getViewName(String trxType, String subTrxType, String reportType) {
		final String result = REPORT_VERSION
				+ "_" + trxType
				+ (StringUtils.isNotBlank(subTrxType) ? "_" + subTrxType : "")
				+ (StringUtils.isNotBlank(reportType) ? "_" + reportType : "") + "_transaction";

		return result.toUpperCase();
	}

	@Override
	public String getPdfReportName(String trxType, String subTrxType, String reportType) {
		final String result = "report_income_"
				+ trxType
				+ (StringUtils.isNotBlank(subTrxType) ? "_" + subTrxType : "")
				+ (StringUtils.isNotBlank(reportType) ? "_" + reportType : "");
		return result.toLowerCase() + ".pdf";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map<String, TextColumnBuilder<?>> extractViewMetadata(ResultSet rs) throws SQLException {
		Map<String, TextColumnBuilder<?>> mapColumnBuilders = new LinkedHashMap<String, TextColumnBuilder<?>>();

		mapColumnBuilders.put("NO", serviceReport.buildRowNumberColumn("NO"));
		
		ResultSetMetaData rsMetaData = rs.getMetaData();
		for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
			final String name = rsMetaData.getColumnName(i);
			final String type = rsMetaData.getColumnTypeName(i);
			logger.info("View column : " + name + " : " + type);

			if (StringUtils.equalsAny(name, "STATUS_LOG", "TRANSACTION_DATE"))
				continue;

			DRIDataType columnDataType = serviceReport.getDataType(type);
			StyleBuilder style = stl.style(Templates.columnStyle);
			if (i == 1) {
				style.setTextAlignment(HorizontalTextAlignment.LEFT, VerticalTextAlignment.MIDDLE);
			} else {
				serviceReport.setTextAlign(style, columnDataType);
			}

			TextColumnBuilder<?> columnBuilder = (TextColumnBuilder<?>) col.column(name, name, columnDataType).setStyle(style);

			mapColumnBuilders.put(name, columnBuilder);
			

		}

		return mapColumnBuilders;
	}

	protected Map<String, Object> getReportRelatedData(final String viewName, final DataModel dataModel) {
		final Map<String, Object> result = new HashMap<String, Object>();
		final HQLCriteriaModel hqlCriteria = dataModel.generateHQLCriteria();
		
		final String queryStatusLog = "select STATUS_LOG from " + viewName + " where "+hqlCriteria.getQuery() +" group by STATUS_LOG";
		final List<String> listStatusLog = this.jdbcTemplate.query(queryStatusLog, hqlCriteria.getParams(), new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				final String statusLog = rs.getString(1);
				
				if (!result.containsKey("COLUMN_BUILDERS")) {
					final String queryExtractData = "select * from " + viewName;
					jdbcTemplate.query(queryExtractData, new ResultSetExtractor<Void>() {

						@Override
						public Void extractData(ResultSet rs) throws SQLException, DataAccessException {
							result.put("COLUMN_BUILDERS",extractViewMetadata(rs));
							return null;
						}
					});
				}

				return statusLog;
			}
		});

		result.put("LIST_STATUS_LOG", listStatusLog);
		return result;
	}

	protected class SubreportExpression extends AbstractSimpleExpression<JasperReportBuilder> {

		private static final long serialVersionUID = 1L;
		private Map<String, TextColumnBuilder<?>> mapColumnBuilders;
		private List<String> listStatusLog;

		public SubreportExpression(Map<String, TextColumnBuilder<?>> mapColumnBuilders, List<String> listStatusLog) {
			super();
			this.mapColumnBuilders = mapColumnBuilders;
			this.listStatusLog = listStatusLog;
		}

		@Override
		public JasperReportBuilder evaluate(ReportParameters reportParameters) {
			final int rowNumber = reportParameters.getReportRowNumber();

			final TextFieldBuilder<String> title = serviceReport
					.buildTitlteTextField("Transaksi : " + serviceStatus.getStatus(listStatusLog.get(rowNumber - 1)));
			final ColumnGroupBuilder groupBranchCode = serviceReport.buildColumnGroupBuilder(mapColumnBuilders.get("BRANCH_CODE"),
					"Kode Cabang");
			final List<AggregationSubtotalBuilder<?>> subtotalBuilders = serviceReport.buildListSubtotal("SUB TOTAL", mapColumnBuilders);

			final TextColumnBuilder<?>[] columns = new TextColumnBuilder<?>[mapColumnBuilders.size()];
			final AggregationSubtotalBuilder<?>[] subtotalCols = new AggregationSubtotalBuilder<?>[subtotalBuilders.size()];
			mapColumnBuilders.values().toArray(columns);
			subtotalBuilders.toArray(subtotalCols);
			
			VariableBuilder<Integer> rowVariable = DynamicReports.variable("groupRowNumber", DynamicReports.exp.value(1), Calculation.SUM);
			rowVariable.setResetGroup(groupBranchCode);

			JasperReportBuilder report = report()
					.setTemplate(Templates.reportTemplate)
					.title(cmp.verticalList()
							.add(title)
							.add(cmp.verticalGap(5)))
					.columns(columns)
					.groupBy(groupBranchCode)
					.subtotalsAtGroupFooter(groupBranchCode, subtotalCols)
					.setGroupFooterStyle(groupBranchCode, stl.style().setBorder(stl.pen1Point()))
					.variables(rowVariable);
			return report;
		}
	}

	@Getter
	protected class SubreportDBDataSourceExpression extends AbstractSimpleExpression<JRDataSource> {

		private static final long serialVersionUID = 1L;
		private String viewName;
		private List<String> listStatusLog;
		private HQLCriteriaModel hqlCriteria;
		
		public SubreportDBDataSourceExpression(String viewName, List<String> listStatusLog, DataModel dataModel) {
			super();
			dataModel.deleteCriteria("STATUS_LOG");
			this.viewName = viewName;
			this.listStatusLog = listStatusLog;
			this.hqlCriteria = dataModel.generateHQLCriteria();
		}
		
		@Override
		public JRDataSource evaluate(ReportParameters reportParameters) {
			int rowNumber = reportParameters.getReportRowNumber();
			final String statusLog = listStatusLog.get(rowNumber - 1);
			return getDataSource(statusLog);
		}

		private JRDataSource getDataSource(final String statusLog) {
			final String queryExtractData = "select * from " + viewName + " where "+hqlCriteria.getQuery()+" AND STATUS_LOG = "+statusLog;

			JRDataSource resultSetDataSource = jdbcTemplate.query(queryExtractData, hqlCriteria.getParams(),
					new ResultSetExtractor<JRDataSource>() {

						@Override
						public JRDataSource extractData(ResultSet rs) throws SQLException, DataAccessException {
							return new JRMapCollectionDataSource(serviceReport.resultSetToList(rs));
						}
					});
			return resultSetDataSource;
		}
	}
}
