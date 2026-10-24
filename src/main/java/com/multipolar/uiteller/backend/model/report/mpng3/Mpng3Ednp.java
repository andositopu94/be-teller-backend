package com.multipolar.uiteller.backend.model.report.mpng3;

import java.math.BigDecimal;
import java.util.Date;

public class Mpng3Ednp {

    private String kodeBank;
    private Date tanggalBayar;
    private String kodeBilling;
    private String ntb;
    private String ntpn;
    private BigDecimal nilaiPembayaran;
    private String mataUang;
    private String nomorBukti;
    private String nomorArsip;
    private String user;
    private String channel;

    public String getKodeBank() {
        return kodeBank;
    }

    public void setKodeBank(String kodeBank) {
        this.kodeBank = kodeBank;
    }

    public Date getTanggalBayar() {
        return tanggalBayar;
    }

    public void setTanggalBayar(Date tanggalBayar) {
        this.tanggalBayar = tanggalBayar;
    }

    public String getKodeBilling() {
        return kodeBilling;
    }

    public void setKodeBilling(String kodeBilling) {
        this.kodeBilling = kodeBilling;
    }

    public String getNtb() {
        return ntb;
    }

    public void setNtb(String ntb) {
        this.ntb = ntb;
    }

    public String getNtpn() {
        return ntpn;
    }

    public void setNtpn(String ntpn) {
        this.ntpn = ntpn;
    }

    public BigDecimal getNilaiPembayaran() {
        return nilaiPembayaran;
    }

    public void setNilaiPembayaran(BigDecimal nilaiPembayaran) {
        this.nilaiPembayaran = nilaiPembayaran;
    }

    public String getMataUang() {
        return mataUang;
    }

    public void setMataUang(String mataUang) {
        this.mataUang = mataUang;
    }

    public String getNomorBukti() {
        return nomorBukti;
    }

    public void setNomorBukti(String nomorBukti) {
        this.nomorBukti = nomorBukti;
    }

    public String getNomorArsip() {
        return nomorArsip;
    }

    public void setNomorArsip(String nomorArsip) {
        this.nomorArsip = nomorArsip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
