package com.multipolar.uiteller.backend.model.report.mpng2;

import java.math.BigDecimal;
import java.util.Date;

public class BpjsPembayaran {

    private Date tanggalBank;
    private String kodeCabang;
    private String customerId;
    private String customerName;
    private String memberCount;
    private String montYear;
    private BigDecimal nominal;
    private BigDecimal admin;
    private BigDecimal premi;
    private String archiveNumber;
    private String channel;
    private String user;

    public Date getTanggalBank() {
        return tanggalBank;
    }

    public void setTanggalBank(Date tanggalBank) {
        this.tanggalBank = tanggalBank;
    }

    public String getKodeCabang() {
        return kodeCabang;
    }

    public void setKodeCabang(String kodeCabang) {
        this.kodeCabang = kodeCabang;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(String memberCount) {
        this.memberCount = memberCount;
    }

    public String getMontYear() {
        return montYear;
    }

    public void setMontYear(String montYear) {
        this.montYear = montYear;
    }

    public BigDecimal getNominal() {
        return nominal;
    }

    public void setNominal(BigDecimal nominal) {
        this.nominal = nominal;
    }

    public BigDecimal getAdmin() {
        return admin;
    }

    public void setAdmin(BigDecimal admin) {
        this.admin = admin;
    }

    public BigDecimal getPremi() {
        return premi;
    }

    public void setPremi(BigDecimal premi) {
        this.premi = premi;
    }

    public String getArchiveNumber() {
        return archiveNumber;
    }

    public void setArchiveNumber(String archiveNumber) {
        this.archiveNumber = archiveNumber;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
