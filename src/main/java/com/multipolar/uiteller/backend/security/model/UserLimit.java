package com.multipolar.uiteller.backend.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserLimit {
	@JsonProperty("MXDROBAC")
	private String MXDROBAC;

	@JsonProperty("LIMITID")
    private String LIMITID;

	@JsonProperty("BRANCHID")
    private String BRANCHID;

	@JsonProperty("MAXCROB")
    private String MAXCROB;

	@JsonProperty("MXCROBAC")
    private String MXCROBAC;

	@JsonProperty("MXCSHSTR")
    private String MXCSHSTR;

	@JsonProperty("MXCLACST")
    private String MXCLACST;

    private String maxDebitOverbook;
	
	@JsonProperty("MAXDROB")
	public void setMaxDebitOverbook(String maxDebitOverbook) {
		this.maxDebitOverbook = maxDebitOverbook;
	}
	
    @JsonProperty("MAXDROB")
	public String getMaxDebitOverbook() {
		return this.maxDebitOverbook;
	}
    
    private String maxCashTransaction;
    
    @JsonProperty("MXCSHSTR")
    public void setMaxCashTransaction(String maxTrx) {
    	this.maxCashTransaction = maxTrx;
    }
    
    @JsonProperty("MXCSHSTR")
    public String getMaxCashTransaction() {
    	return this.maxCashTransaction;
    }

	@JsonProperty("CRTDATE")
    private String CRTDATE;

	
    private String currency;
	
    @JsonProperty("CCYID")
	public void setCurrency(String currency) {
		this.currency = currency;
	}
    
    @JsonProperty("CCYID")
    public String getCurrency() {
    	return this.currency;
    }

	@JsonProperty("MXCLRWDR")
    private String MXCLRWDR;

	@JsonProperty("MXCSHWDR")
    private String MXCSHWDR;

	@JsonProperty("MXCLRSTR")
    private String MXCLRSTR;

	@JsonProperty("MXACCWDR")
    private String MXACCWDR;

	@JsonProperty("MXCLACWD")
    private String MXCLACWD;

	@JsonProperty("CRTUSER")
    private String CRTUSER;

	@JsonProperty("MXACCSTR")
    private String MXACCSTR;

	@JsonProperty("USERID")
    private String USERID;
}

