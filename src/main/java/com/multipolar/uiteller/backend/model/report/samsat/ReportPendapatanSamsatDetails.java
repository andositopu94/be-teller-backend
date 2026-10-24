package com.multipolar.uiteller.backend.model.report.samsat;

import java.math.BigDecimal;
import java.util.Date;

public class ReportPendapatanSamsatDetails {
    private String statusTransaksi;
    private Date tanggalTransaksi;
    private String kodeCabang;
    private String kodeBayar;
    private String nama;
    private String nomorArsip;
    private BigDecimal tagihan;
    private BigDecimal fee;
    private String nomorKtp;
    private String user;
    private String channel;

    public String getStatusTransaksi() {
        return statusTransaksi;
    }

    public void setStatusTransaksi(String statusTransaksi) {
        this.statusTransaksi = statusTransaksi;
    }

    public Date getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public void setTanggalTransaksi(Date tanggalTransaksi) {
        this.tanggalTransaksi = tanggalTransaksi;
    }

    public String getKodeCabang() {
        return kodeCabang;
    }

    public void setKodeCabang(String kodeCabang) {
        this.kodeCabang = kodeCabang;
    }

    public String getKodeBayar() {
        return kodeBayar;
    }

    public void setKodeBayar(String kodeBayar) {
        this.kodeBayar = kodeBayar;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNomorArsip() {
        return nomorArsip;
    }

    public void setNomorArsip(String nomorArsip) {
        this.nomorArsip = nomorArsip;
    }

    public BigDecimal getTagihan() {
        return tagihan;
    }

    public void setTagihan(BigDecimal tagihan) {
        this.tagihan = tagihan;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getNomorKtp() {
        return nomorKtp;
    }

    public void setNomorKtp(String nomorKtp) {
        this.nomorKtp = nomorKtp;
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
