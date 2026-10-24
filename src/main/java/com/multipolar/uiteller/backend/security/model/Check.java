package com.multipolar.uiteller.backend.security.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Check {

	private String sourceNumber;
	private String chkNumber;
	private String chkPage;
}
