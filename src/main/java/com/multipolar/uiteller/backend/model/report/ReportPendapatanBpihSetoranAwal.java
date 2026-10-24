package com.multipolar.uiteller.backend.model.report;

import java.math.BigDecimal;
import java.util.Date;

public class ReportPendapatanBpihSetoranAwal {

    private Date tanggalTransaksi;
    private String cabang;
    private String nomorRekening;
    private String nama;
    private String nomorArsip;
    private BigDecimal tagihan;
    private BigDecimal fee;
    private String nomorValidasi;
    private String user;
    private String channel;

    public Date getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public void setTanggalTransaksi(Date tanggalTransaksi) {
        this.tanggalTransaksi = tanggalTransaksi;
    }

    public String getCabang() {
        return cabang;
    }

    public void setCabang(String cabang) {
        this.cabang = cabang;
    }

    public String getNomorRekening() {
        return nomorRekening;
    }

    public void setNomorRekening(String nomorRekening) {
        this.nomorRekening = nomorRekening;
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

    public String getNomorValidasi() {
        return nomorValidasi;
    }

    public void setNomorValidasi(String nomorValidasi) {
        this.nomorValidasi = nomorValidasi;
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
