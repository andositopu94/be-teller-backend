package com.multipolar.uiteller.backend.repository.util;

import lombok.Getter;
import lombok.Value;


@Value
public class CriteriaModel {

	public enum OPERATORS {
		EQUAL("="), 
		NOT_EQUAL("<>"), 
		LIKE("LIKE"), 
		GREATER(">"), 
		LESSER("<"), 
		GREATER_EQUAL(">="), 
		LESSER_EQUAL("<="), 
		IN("IN"), 
		DATE_EQUAL("="), 
		BETWEEN("BETWEEN");
		
		@Getter
		private String expression;
		private OPERATORS(String expression) {
			this.expression = expression;
		}
	}

	private OPERATORS operator;
	private String fieldName;
	private Object[] values;

	public CriteriaModel(String fieldName, OPERATORS operator, Object... value) {
		this.operator = operator;
		this.fieldName = fieldName;
		this.values = value;
	}

	public Object getValue() {
		return values != null && values.length > 0 ? values[0] : null;
	}

}
