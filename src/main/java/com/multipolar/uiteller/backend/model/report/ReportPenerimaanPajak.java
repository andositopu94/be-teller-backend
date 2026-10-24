package com.multipolar.uiteller.backend.model.report;

import java.math.BigDecimal;
import java.util.Date;

public class ReportPenerimaanPajak {

    private String kodeCabang;
    private Date tanggalBuku;
    private Date tanggalBank;
    private String stan;
    private String ntb;
    private String ntpn;
    private String no;
    private String npwp;
    private BigDecimal jumlah;
    private String user;
    private String kodeBilling;
    private String nama;
    private String alamat;
    private String nomorWajibPajak;
    private String mataAnggaran;
    private String jenisSetoran;
    private String nomorKetetapan;
    private String mataUang;
    private String status;
    private String channel;

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

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public String getNtb() {
        return ntb;
    }

    public void setNtb(String ntb) {
        this.ntb = ntb;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getNpwp() {
        return npwp;
    }

    public void setNpwp(String npwp) {
        this.npwp = npwp;
    }

    public BigDecimal getJumlah() {
        return jumlah;
    }

    public void setJumlah(BigDecimal jumlah) {
        this.jumlah = jumlah;
    }

    public String getNtpn() {
        return ntpn;
    }

    public void setNtpn(String ntpn) {
        this.ntpn = ntpn;
    }

    public String getKodeCabang() {
        return kodeCabang;
    }

    public void setKodeCabang(String kodeCabang) {
        this.kodeCabang = kodeCabang;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNomorWajibPajak() {
        return nomorWajibPajak;
    }

    public void setNomorWajibPajak(String nomorWajibPajak) {
        this.nomorWajibPajak = nomorWajibPajak;
    }

    public String getMataAnggaran() {
        return mataAnggaran;
    }

    public void setMataAnggaran(String mataAnggaran) {
        this.mataAnggaran = mataAnggaran;
    }

    public String getJenisSetoran() {
        return jenisSetoran;
    }

    public void setJenisSetoran(String jenisSetoran) {
        this.jenisSetoran = jenisSetoran;
    }

    public String getNomorKetetapan() {
        return nomorKetetapan;
    }

    public void setNomorKetetapan(String nomorKetetapan) {
        this.nomorKetetapan = nomorKetetapan;
    }

    public String getMataUang() {
        return mataUang;
    }

    public void setMataUang(String mataUang) {
        this.mataUang = mataUang;
    }
}
