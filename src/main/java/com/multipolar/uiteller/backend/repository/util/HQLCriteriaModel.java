package com.multipolar.uiteller.backend.repository.util;

import lombok.Value;

@Value
public class HQLCriteriaModel {
	private String query;
	private Object[] params;
}
