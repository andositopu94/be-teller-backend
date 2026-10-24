package com.multipolar.uiteller.backend.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AuthLimit {
	@JsonProperty("MXDROBAC")
	@JsonIgnore
	private String MXDROBAC;

	@JsonProperty("BRANCHID")
	//@JsonIgnore
    private String BRANCHID;

	@JsonProperty("MAXCROB")
    private String maxAuthCreditOverbook;

	@JsonProperty("MXCROBAC")
	@JsonIgnore
    private String MXCROBAC;

	@JsonProperty("MXCSHSTR")
	@JsonIgnore
    private String MXCSHSTR;

	@JsonProperty("MXCLACST")
	@JsonIgnore
    private String MXCLACST;

	private String maxAuthDebitOverbook;
	
	@JsonProperty("MAXDROB")
	public void setMaxAuthDebitOverbook(String maxAuthDebitOverbook) {
		this.maxAuthDebitOverbook = maxAuthDebitOverbook;
	}
	
	@JsonProperty("MAXDROB")
	public String getMaxAuthDebitOverbook() {
		return this.maxAuthDebitOverbook;
	}

	@JsonProperty("PWD_MD5")
	@JsonIgnore
    private String PWD_MD5;

	@JsonProperty("CCYID")
	@JsonIgnore
    private String currency;

	@JsonProperty("MXCLRWDR")
	@JsonIgnore
    private String MXCLRWDR;

	@JsonProperty("PWD")
	@JsonIgnore
    private String PWD;

	@JsonProperty("MXCSHWDR")
	@JsonIgnore
    private String MXCSHWDR;

	@JsonProperty("MXCLRSTR")
	@JsonIgnore
    private String MXCLRSTR;

	@JsonProperty("MXACCWDR")
	@JsonIgnore
    private String MXACCWDR;

	@JsonProperty("MXCLACWD")
	@JsonIgnore
    private String MXCLACWD;

	@JsonProperty("USERNM")
    private String userName;

	@JsonProperty("MXACCSTR")
	@JsonIgnore
    private String MXACCSTR;

	@JsonProperty("USERID")
    private String userId;
}
