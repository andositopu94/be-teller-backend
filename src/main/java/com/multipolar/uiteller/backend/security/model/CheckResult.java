package com.multipolar.uiteller.backend.security.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(value=Include.NON_NULL)
public class CheckResult {
	@JsonProperty("RESIDATE")
	private String resiDate;
	@JsonProperty("PAGESTS")
	private String pageSts;
	@JsonProperty("ISCHQ")
	private String ischq;
	@JsonProperty("RELDATE")
	private String relDate;
}

