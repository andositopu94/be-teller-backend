package com.multipolar.uiteller.backend.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BranchMap {
	@JsonProperty("TYPEBRNCH")
	private String branchType;

	@JsonProperty("BRANCHID")
	private String branchId;

	@JsonProperty("BRANCHNM")
	private String branchName;

	@JsonProperty("OFFCHANID")
	private String offChannelId;

	@JsonProperty("ADDRESS")
	private String address;

	@JsonProperty("FLGOPEN")
	private String flagOpen;

	@JsonProperty("CCYID")
	private String currency;

	@JsonProperty("FLGLKPBU")
	private String flagLkpBu;

	@JsonProperty("CITY")
	private String city;
	
	@JsonProperty("CLRDAY")
	private String clrday;

	@JsonProperty("FLAGTX")
	private String flagTrx;

}
