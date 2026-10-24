package com.multipolar.uiteller.backend.security.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class AccountResult {
    @JsonProperty("SMUTDB")
    private String SMUTDB;

    @JsonProperty("BRANCHID")
    private String BRANCHID;

    @JsonProperty("SSALDO_AKHIR")
    private String ssaldoAkhir;

    @JsonProperty("SALDO_AKHIR")
    private String saldoAkhir;

    @JsonProperty("SBLOKIR_AFILIASI")
    private String SBLOKIR_AFILIASI;

    @JsonProperty("FLGDRAWMIN")
    private String FLGDRAWMIN;

    @JsonProperty("ACCNBR")
    private String ACCNBR;

    @JsonProperty("PRODID")
    private String productCode;

    @JsonProperty("NICKNM")
    private String NICKNM;

    @JsonProperty("FULLNM")
    private String fullName;

    @JsonProperty("ACCESLVL")
    private String ACCESLVL;

    @JsonProperty("MOTHRNM")
    private String MOTHRNM;

    @JsonProperty("FLGSWEEP")
    private String FLGSWEEP;

    @JsonProperty("FLGAFI")
    private String FLGAFI;

    @JsonProperty("CIFID")
    private String CIFID;

    @JsonProperty("ACCSTS")
    private String accountStatus;

    @JsonProperty("ADDR")
    private String ADDR;

    @JsonProperty("BODBAL")
    private String beginningBalance;

    @JsonProperty("MUTCR")
    private String MUTCR;

    @JsonProperty("APPLID")
    private String applicationCode;

    @JsonProperty("SMUTCR")
    private String SMUTCR;

    @JsonProperty("DTBAL")
    private String DTBAL;

    @JsonProperty("MINSVG")
    private String MINSVG;

    @JsonProperty("MINBAL")
    private String MINBAL;

    @JsonProperty("CCYID")
    private String CCYID;

    @JsonProperty("FIRSTDEP")
    private String FIRSTDEP;

    @JsonProperty("FLGOVRDFT")
    private String FLGOVRDFT;

    @JsonProperty("MUTDB")
    private String MUTDB;

    @JsonProperty("SALDO_KLIRING")
    private String SALDO_KLIRING;

    @JsonProperty("FLGJOIN")
    private String FLGJOIN;

    @JsonProperty("SBODBAL")
    private String SBODBAL;

    @JsonProperty("SSALDO_AKHIR")
    private String SSALDO_AKHIR;

    @JsonProperty("BRTDT")
    private String BRTDT;

    @JsonProperty("APPLID_BE")
    private String APPLID_BE;

    @JsonProperty("SSALDO_BLOKIR")
    private String SSALDO_BLOKIR;

    @JsonProperty("IDNBR")
    private String IDNBR;

    @JsonProperty("TXSTS")
    private String TXSTS;
}

