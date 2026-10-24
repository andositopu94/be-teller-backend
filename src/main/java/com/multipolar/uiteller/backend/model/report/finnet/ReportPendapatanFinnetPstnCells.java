package com.multipolar.uiteller.backend.model.report.finnet;

import java.math.BigDecimal;
import java.sql.Date;

public class ReportPendapatanFinnetPstnCells {

    private String namaPelanggan;
    private String nomorPelanggan;
    private Date tanggalBayar;
    private BigDecimal jumlah;
    private BigDecimal biaya;
    private BigDecimal total;
    private String nomorArsip;
    private String user;
    private String channel;

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }

    public String getNomorPelanggan() {
        return nomorPelanggan;
    }

    public void setNomorPelanggan(String nomorPelanggan) {
        this.nomorPelanggan = nomorPelanggan;
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
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
