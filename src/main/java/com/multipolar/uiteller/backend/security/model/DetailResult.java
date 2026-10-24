package com.multipolar.uiteller.backend.security.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(value=Include.NON_NULL)
public class DetailResult {

	@JsonProperty("CIFID")
	private String CIFID;

	@JsonProperty("CIFTYPE")
	private String CIFTYPE;

	@JsonProperty("APPLID")
	private String APPLID;

	@JsonProperty("CCYID")
	private String CCYID;

	@JsonProperty("ACCSTS")
	private String ACCSTS;

	@JsonProperty("NICKNM")
	private String NICKNM;

	@JsonProperty("FULLNM")
	private String FULLNM;

	@JsonProperty("GUARDNM")
	private String GUARDNM;

	@JsonProperty("MARRIAGEID")
	private String MARRIAGEID;

	@JsonProperty("SEX")
	private String SEX;

	@JsonProperty("BRTDT")
	private String BRTDT;

	@JsonProperty("BRTPLACE")
	private String BRTPLACE;

	@JsonProperty("MOTHRNM")
	private String MOTHRNM;

	@JsonProperty("TYPEID")
	private String TYPEID;

	@JsonProperty("IDNBR")
	private String IDNBR;

	@JsonProperty("EXPDT")
	private String EXPDT;

	@JsonProperty("NPWP")
	private String NPWP;

	@JsonProperty("STSNATION")
	private String STSNATION;

	@JsonProperty("FLGCITIZEN")
	private String FLGCITIZEN;

	@JsonProperty("INSURED")
	private String INSURED;

	@JsonProperty("NOTELP")
	private String NOTELP;

	@JsonProperty("NOFAX")
	private String NOFAX;

	@JsonProperty("NOHP")
	private String NOHP;

	@JsonProperty("EMAIL")
	private String EMAIL;

	@JsonProperty("ADDR")
	private String ADDR;

	@JsonProperty("RT")
	private String RT;

	@JsonProperty("RW")
	private String RW;

	@JsonProperty("KECNM")
	private String KECNM;

	@JsonProperty("KELNM")
	private String KELNM;

	@JsonProperty("CITYID")
	private String CITYID;

	@JsonProperty("CITYNM")
	private String CITYNM;

	@JsonProperty("PROVID")
	private String PROVID;

	@JsonProperty("PROVNM")
	private String PROVNM;

	@JsonProperty("COUNTRYID")
	private String COUNTRYID;

	@JsonProperty("POSTALCODE")
	private String POSTALCODE;

	@JsonProperty("HOMEID")
	private String HOMEID;

	@JsonProperty("HOBBY")
	private String HOBBY;

	@JsonProperty("PREDEGREE")
	private String PREDEGREE;

	@JsonProperty("POSTDEGREE")
	private String POSTDEGREE;

	@JsonProperty("NIP")
	private String NIP;

	@JsonProperty("JOBID")
	private String JOBID;

	@JsonProperty("COMNMJOB")
	private String COMNMJOB;

	@JsonProperty("BUSSID")
	private String BUSSID;

	@JsonProperty("FUNCJOB")
	private String FUNCJOB;

	@JsonProperty("FUNCDESC")
	private String FUNCDESC;

	@JsonProperty("DTSTARTJOB")
	private String DTSTARTJOB;

	@JsonProperty("ADDRJOB")
	private String ADDRJOB;

	@JsonProperty("NOTELPJOB")
	private String NOTELPJOB;

	@JsonProperty("NOTEFAXJOB")
	private String NOTEFAXJOB;

	@JsonProperty("CITYIDJOB")
	private String CITYIDJOB;

	@JsonProperty("CITYNMJOB")
	private String CITYNMJOB;

	@JsonProperty("PROVIDJOB")
	private String PROVIDJOB;

	@JsonProperty("PROVNMJOB")
	private String PROVNMJOB;

	@JsonProperty("COUNTRYIDJOB")
	private String COUNTRYIDJOB;

	@JsonProperty("POSTALCODEJOB")
	private String POSTALCODEJOB;

	@JsonProperty("LASTEDUID")
	private String LASTEDUID;
}

