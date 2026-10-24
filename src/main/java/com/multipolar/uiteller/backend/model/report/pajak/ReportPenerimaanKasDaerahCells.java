package com.multipolar.uiteller.backend.model.report.pajak;

import java.math.BigDecimal;
import java.util.Date;

public class ReportPenerimaanKasDaerahCells {

    private String namaWpWr;
    private String nomorSts;
    private Date tanggalBayar;
    private BigDecimal jumlah;
    private BigDecimal pokok;
    private BigDecimal denda;
    private BigDecimal biaya;
    private String nomorArsip;
    private String user;
    private String channel;

    public String getNamaWpWr() {
        return namaWpWr;
    }

    public void setNamaWpWr(String namaWpWr) {
        this.namaWpWr = namaWpWr;
    }

    public String getNomorSts() {
        return nomorSts;
    }

    public void setNomorSts(String nomorSts) {
        this.nomorSts = nomorSts;
    }

    public Date getTanggalBayar() {
        return tanggalBayar;
    }

    public void setTanggalBayar(Date tanggalBayar) {
        this.tanggalBayar = tanggalBayar;
    }

    public BigDecimal getJumlah() {
        return jumlah;
    }

    public void setJumlah(BigDecimal jumlah) {
        this.jumlah = jumlah;
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

    public BigDecimal getPokok() {
        return pokok;
    }

    public void setPokok(BigDecimal pokok) {
        this.pokok = pokok;
    }

    public BigDecimal getDenda() {
        return denda;
    }

    public void setDenda(BigDecimal denda) {
        this.denda = denda;
    }
}
