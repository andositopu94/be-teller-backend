package com.multipolar.uiteller.backend.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserMap {

	// passsword expired date (yyyy-MM-dd)
	@JsonProperty("EXPDT")
	private String expiryDate;
	
	// user's branch code
	@JsonProperty("BRANCHID")
    private String branchCode;

    // user identity number
	@JsonProperty("NIK")
    private String nik;
    
    // status of user: 1 --> active, 0 --> not active
	@JsonProperty("STATUS")
    private String status;

    // authorization status: 1 --> yes, 0 --> no
	@JsonProperty("FLGAUTH")
    private String authorizationFlag;
    
    // name of user
	@JsonProperty("USERNM")
    private String userName;
    
	@JsonProperty("LASTUPD")
    private String lastUpdate;

	@JsonProperty("LMTFAIL")
    private String lmtFail;
	
	@JsonProperty("FLG_TERMNL")
    private String FLG_TERMNL;

	@JsonProperty("CASHOFCID")
    private String CASHOFCID;

	@JsonProperty("UNITID")
    private String UNITID;

	@JsonProperty("PWDPRD")
    private String PWDPRD;

	@JsonProperty("FLGTRX")
    private String FLGTRX;

	@JsonProperty("MAXLOGIN")
    private String MAXLOGIN;

	@JsonProperty("PWD")
    private String PWD;

	@JsonProperty("FLG_TRXAC")
    private String FLG_TRXAC;

	@JsonProperty("ACCESLVL")
    private String ACCESLVL;

	@JsonProperty("MINLOGIN")
    private String MINLOGIN;

	@JsonProperty("USRTYPCD")
    private String USRTYPCD;

	@JsonProperty("AMTFAIL")
    private String AMTFAIL;

	@JsonProperty("FLGCASH")
    private String FLGCASH;

	@JsonProperty("IANBR")
    private String IANBR;

	@JsonProperty("USERID")
    private String USERID;
}
