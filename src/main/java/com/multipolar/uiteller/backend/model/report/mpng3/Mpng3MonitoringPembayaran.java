package com.multipolar.uiteller.backend.model.report.mpng3;

import java.math.BigDecimal;
import java.util.Date;

public class Mpng3MonitoringPembayaran {

    private Date tanggalBuku;
    private Date tanggalBank;
    private String kodeCabang;
    private String kodeUser;
    private String kodeBilling;
    private String nama;
    private String ntpn;
    private BigDecimal jumlah;
    private String nomorArsip;
    private String channel;
    private String user;
    private String ntb;
    private String stan;
    private String status;
    private String npwp;
    private String address;
    private String taxType;
    private String countDetail;
    private String message;

    public Date getTanggalBuku() {
        return tanggalBuku;
    }

    public void setTanggalBuku(Date tanggalBuku) {
        this.tanggalBuku = tanggalBuku;
    }

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

    public String getKodeUser() {
        return kodeUser;
    }

    public void setKodeUser(String kodeUser) {
        this.kodeUser = kodeUser;
    }

    public String getKodeBilling() {
        return kodeBilling;
    }

    public void setKodeBilling(String kodeBilling) {
        this.kodeBilling = kodeBilling;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNtpn() {
        return ntpn;
    }

    public void setNtpn(String ntpn) {
        this.ntpn = ntpn;
    }

    public BigDecimal getJumlah() {
        return jumlah;
    }

    public void setJumlah(BigDecimal jumlah) {
        this.jumlah = jumlah;
    }

    public String getNomorArsip() {
        return nomorArsip;
    }

    public void setNomorArsip(String nomorArsip) {
        this.nomorArsip = nomorArsip;
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

    public String getNtb() {
        return ntb;
    }

    public void setNtb(String ntb) {
        this.ntb = ntb;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNpwp() {
        return npwp;
    }

    public void setNpwp(String npwp) {
        this.npwp = npwp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getCountDetail() {
        return countDetail;
    }

    public void setCountDetail(String countDetail) {
        this.countDetail = countDetail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
