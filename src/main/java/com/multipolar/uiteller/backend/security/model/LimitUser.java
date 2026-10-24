package com.multipolar.uiteller.backend.security.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LimitUser {
	
	@JsonProperty("MXDROBAC")
	private String MXDROBAC;

	@JsonProperty("LIMITID")
    private String LIMITID;

	@JsonProperty("BRANCHID")
    private String BRANCHID;

	@JsonProperty("MAXCROB")
    private String maxCreditOverbook;

	@JsonProperty("MXCROBAC")
    private String MXCROBAC;

	
    private String maxCashTransaction;
	
    @JsonProperty("MXCSHSTR")
	public void setMaxCashTrx(String maxTrx) {
		this.maxCashTransaction = maxTrx;
	}
    
    @JsonProperty("MXCSHSTR")
    public String getMaxCashTrx() {
    	return this.maxCashTransaction;
    }

	@JsonProperty("MXCLACST")
    private String MXCLACST;

	@JsonProperty("AUTH_LIMIT")
    private List<AuthLimit> AUTH_LIMIT;

    private String maxDebitOverbook;
	
    @JsonProperty("MAXDROB")
	public void setMaxDebitOverbook(String maxDebitOverbook) {
		this.maxDebitOverbook = maxDebitOverbook;
	}
	
    @JsonProperty("MAXDROB")
	public String getMaxDebitOverbook() {
		return this.maxDebitOverbook;
	}

	@JsonProperty("CRTDATE")
    private String CRTDATE;

	@JsonProperty("CCYID")
    private String CCYID;

    private List<UserLimit> userLimits;
    
    @JsonProperty("USER_LIMIT")
	public void setUserLimits(List<UserLimit> userLimits) {
		this.userLimits = userLimits;
	}
    
    @JsonProperty("USER_LIMIT")
    public List<UserLimit> getUserLimits() {
    	return this.userLimits;
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

	@JsonProperty("IACCNBR")
    private String accountNumber;

	@JsonProperty("CRTUSER")
    private String CRTUSER;

	@JsonProperty("MXACCSTR")
    private String MXACCSTR;

	@JsonProperty("USERID")
    private String USERID;
}

