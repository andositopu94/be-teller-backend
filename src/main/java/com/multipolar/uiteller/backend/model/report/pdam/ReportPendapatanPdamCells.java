package com.multipolar.uiteller.backend.model.report.pdam;

import java.math.BigDecimal;
import java.util.Date;

public class ReportPendapatanPdamCells {

    private String namaPelanggan;
    private String nomorPelangan;
    private Integer bills;
    private String prd;
    private Date tanggalTransaksi;
    private BigDecimal jumlah;
    private BigDecimal lainLain;
    private BigDecimal biaya;
    private String nomorArsip;
    private String user;
    private String channel;

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }

    public String getNomorPelangan() {
        return nomorPelangan;
    }

    public void setNomorPelangan(String nomorPelangan) {
        this.nomorPelangan = nomorPelangan;
    }

    public Integer getBills() {
        return bills;
    }

    public void setBills(Integer bills) {
        this.bills = bills;
    }

    public String getPrd() {
        return prd;
    }

    public void setPrd(String prd) {
        this.prd = prd;
    }

    public Date getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public void setTanggalTransaksi(Date tanggalTransaksi) {
        this.tanggalTransaksi = tanggalTransaksi;
    }

    public BigDecimal getJumlah() {
        return jumlah;
    }

    public void setJumlah(BigDecimal jumlah) {
        this.jumlah = jumlah;
    }

    public BigDecimal getLainLain() {
        return lainLain;
    }

    public void setLainLain(BigDecimal lainLain) {
        this.lainLain = lainLain;
    }

    public BigDecimal getBiaya() {
        return biaya;
    }

    public void setBiaya(BigDecimal biaya) {
        this.biaya = biaya;
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
