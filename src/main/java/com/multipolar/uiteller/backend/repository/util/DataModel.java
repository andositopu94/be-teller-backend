package com.multipolar.uiteller.backend.repository.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.multipolar.uiteller.backend.repository.util.CriteriaModel.OPERATORS;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataModel {
	public enum CONJ_OPERATORS {
		AND, OR
	}

	private Map<String, CriteriaModel> criterias;
	private CONJ_OPERATORS operator;

	public DataModel() {
		this.criterias = new HashMap<String,CriteriaModel>();
		this.operator = CONJ_OPERATORS.AND;
	}
	
	public DataModel(Map<String, Object> mapCriterias) {
		super();
		this.operator = CONJ_OPERATORS.AND;
		this.criterias = new HashMap<String,CriteriaModel>();
		addSearchCriteria(mapCriterias);
	}

	public DataModel(Map<String, Object> mapCriterias, CONJ_OPERATORS operator) {
		super();
		this.operator = operator;
		this.criterias = new HashMap<String,CriteriaModel>();
		addSearchCriteria(mapCriterias);
	}
	
	public void addSearchCriteria(String fieldName, OPERATORS operator, Object... values){
		this.criterias.put(fieldName, new CriteriaModel(fieldName, operator, values));
	}
	
	public void addSearchCriteria(Map<String, Object> mapCriteria) {
		for (String key : mapCriteria.keySet()) {
			Object value = mapCriteria.get(key);
			this.criterias.put(key, new CriteriaModel(key, getOperatorByValueType(value, OPERATORS.EQUAL), value));
		}
	}
	
	public boolean deleteCriteria(String fieldName) {
		if (this.criterias.containsKey(fieldName)){
			this.criterias.remove(fieldName);
			return true;
		}
		return false;
	}

	public OPERATORS getOperatorByValueType(Object value, OPERATORS operatorDefault) {
		Class<?> oClass = value.getClass();
		OPERATORS operator = operatorDefault;
		
		if (oClass.isAssignableFrom(Date.class) || oClass.isAssignableFrom(Long.class)) {
			operator = OPERATORS.DATE_EQUAL;
		}else if (oClass.isArray()){
			if (oClass.isAssignableFrom(String[].class)) {
				operator = OPERATORS.IN;
			}else if (oClass.isAssignableFrom(Date[].class) || oClass.getClass().isAssignableFrom(Long[].class)) {
				operator = OPERATORS.BETWEEN;
			}
		}
		else if (Collection.class.isAssignableFrom(oClass)) {
			Object collectionElement = ((Collection<?>) value).iterator().next();
			
			if (collectionElement.getClass().isAssignableFrom(String.class))
				operator = OPERATORS.IN;
			else if (collectionElement.getClass().isAssignableFrom(Date.class) || collectionElement.getClass().isAssignableFrom(Long.class))
				operator = OPERATORS.BETWEEN;
		}

		return operator;
	}

	public HQLCriteriaModel generateHQLCriteria() {
		StringBuilder builder = new StringBuilder();
		List<Object> entries = new ArrayList<Object>();
		
		Set<String> keySet = criterias.keySet();
		
		int i = 0;
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();i++) {
			String key = iterator.next();
			CriteriaModel criteria = criterias.get(key);
			String field = criteria.getFieldName();
			OPERATORS operator = criteria.getOperator();
			switch (operator) {
				default:
					builder.append(String.format("%s %s ?", field, operator.getExpression()));
					entries.add(criteria.getValue());
					break;
					
				case IN:
					builder.append(String.format("%s %s (?)", field, operator.getExpression()));
					entries.add(criteria.getValue());
					break;
					
				case DATE_EQUAL:
					builder.append(String.format("date(%s) %s ?", field, operator.getExpression()));
					entries.add(criteria.getValue());
					break;
					
				case LIKE:
					builder.append(String.format("%s %s '%%%s%%'", field, operator.getExpression(), criteria.getValue()));
					break;
					
				case BETWEEN:
					builder.append(String.format("%s %s ? AND ?", field, operator.getExpression(), criteria.getValue()));
					Object[] array = (Object[]) criteria.getValue();
					entries.add(array[0]);
					entries.add(array[1]);
					break;
			}

			if (i < criterias.size()-1)
				builder.append(String.format(" %s ", this.operator.toString()));
		}

		return new HQLCriteriaModel(builder.toString(), entries.toArray());
	}
	

}
