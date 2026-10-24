package com.multipolar.uiteller.backend.service.report.dynamic;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.grp;
import static net.sf.dynamicreports.report.builder.DynamicReports.sbt;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.multipolar.uiteller.backend.util.report.Templates;

import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.group.ColumnGroupBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.builder.subtotal.AggregationSubtotalBuilder;
import net.sf.dynamicreports.report.constant.GroupHeaderLayout;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.dynamicreports.report.definition.datatype.DRIDataType;

@Service
public class DynamicReportService {
	
	public Collection<Map<String, ?>> resultSetToList(ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		Collection<Map<String, ?>> rows = new ArrayList<Map<String, ?>>();
		while (rs.next()) {
			Map<String, Object> row = new HashMap<String, Object>(columns);
			for (int i = 1; i <= columns; ++i) {
				row.put(md.getColumnName(i), rs.getObject(i));
			}
			rows.add(row);
		}
		return rows;
	}

	public void setTextAlign(StyleBuilder style, DRIDataType<?, ?> dataType) {
		if (dataType.equals(DynamicReports.type.bigDecimalType())) {
			style
					.setTextAlignment(HorizontalTextAlignment.RIGHT, VerticalTextAlignment.MIDDLE)
					.setPattern("#,###");
		} else if (dataType.equals(DynamicReports.type.dateType())) {
			style
					.setTextAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.MIDDLE)
					.setPattern("dd-MM-yyyy");
		} else {
			style.setTextAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.MIDDLE);
		}
	}

	public DRIDataType getDataType(String columnDataType) {
		if (StringUtils.equalsIgnoreCase(columnDataType, "decimal")) {
			return DynamicReports.type.bigDecimalType();
		} else if (StringUtils.equalsIgnoreCase(columnDataType, "date")) {
			return DynamicReports.type.dateType();
		}

		return DynamicReports.type.stringType();
	}

	@SuppressWarnings("unchecked")
	public List<AggregationSubtotalBuilder<?>> buildListSubtotal(String titleSubtotal,
			Map<String, TextColumnBuilder<?>> mapColumnBuilders) {
		List<AggregationSubtotalBuilder<?>> list = new ArrayList<AggregationSubtotalBuilder<?>>();
		Iterator<TextColumnBuilder<?>> iterator = mapColumnBuilders.values().iterator();
		iterator.next();
		list.add(sbt.text("SUB TOTAL : ", iterator.next()));

		for (Map.Entry<String, TextColumnBuilder<?>> entry : mapColumnBuilders.entrySet()) {
			DRIDataType<?, ?> dataType = entry.getValue().getColumn().getComponent().getDataType();
			if (dataType != null && dataType.equals(type.bigDecimalType())) {
				list.add(sbt.sum((TextColumnBuilder<BigDecimal>) entry.getValue()));
			}
		}
		return list;
	}

	public TextFieldBuilder<String> buildTitlteTextField(String title) {
		return cmp.text(title).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
	}

	public ColumnGroupBuilder buildColumnGroupBuilder(TextColumnBuilder<?> columnBuilder, String customTitle) {
		return grp.group(columnBuilder)
				.setPadding(0)
				.setHeaderLayout(GroupHeaderLayout.EMPTY)
				.header(cmp.text(new GroupTextExpression(customTitle, columnBuilder.getName()))
						.setStyle(Templates.rootStyleWithBorder));
	}

	public TextColumnBuilder<?> buildRowNumberColumn(String title) {
		return col.column(title, new GroupRowNumberExpression())
				.setStyle(
						DynamicReports.stl.style(Templates.columnStyle)
						.setTextAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.MIDDLE))
				.setFixedWidth(15);
	}
	
	private class GroupTextExpression extends AbstractSimpleExpression<String> {

		private static final long serialVersionUID = 1L;
		private String customTitle;
		private String name;

		public GroupTextExpression(String customTitle, String name) {
			super();
			this.customTitle = customTitle;
			this.name = name;
		}

		public String evaluate(ReportParameters reportParameters) {
			return customTitle + " : " + reportParameters.getValue(name);
		}
	}
	
	private class GroupRowNumberExpression extends AbstractSimpleExpression<Integer> {
		private static final long serialVersionUID = 1L;

		public Integer evaluate(ReportParameters reportParameters) {
		    return reportParameters.getValue("groupRowNumber");
		  }
		}

}
