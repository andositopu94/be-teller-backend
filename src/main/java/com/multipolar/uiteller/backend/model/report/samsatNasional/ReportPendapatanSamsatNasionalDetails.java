package com.multipolar.uiteller.backend.model.report.samsatNasional;

import java.math.BigDecimal;
import java.util.Date;

public class ReportPendapatanSamsatNasionalDetails {
    private String statusTransaksi;
    private Date tanggalTransaksi;
    private String kodeCabang;
    private String kodeBayar;
    private String nama;
    private String nomorArsip;
    private BigDecimal pkbDenda;
    private BigDecimal pkbPokok;
    private BigDecimal pnbpDenda;
    private BigDecimal pnbpPokok;
    private BigDecimal swdDenda;
    private BigDecimal swdPokok;
    private BigDecimal parkir;
    private BigDecimal billerFee;
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

    public BigDecimal getPkbDenda() {
		return pkbDenda;
	}

	public void setPkbDenda(BigDecimal pkbDenda) {
		this.pkbDenda = pkbDenda;
	}

	public BigDecimal getPkbPokok() {
		return pkbPokok;
	}

	public void setPkbPokok(BigDecimal pkbPokok) {
		this.pkbPokok = pkbPokok;
	}

	public BigDecimal getPnbpDenda() {
		return pnbpDenda;
	}

	public void setPnbpDenda(BigDecimal pnbpDenda) {
		this.pnbpDenda = pnbpDenda;
	}

	public BigDecimal getPnbpPokok() {
		return pnbpPokok;
	}

	public void setPnbpPokok(BigDecimal pnbpPokok) {
		this.pnbpPokok = pnbpPokok;
	}

	public BigDecimal getSwdDenda() {
		return swdDenda;
	}

	public void setSwdDenda(BigDecimal swdDenda) {
		this.swdDenda = swdDenda;
	}

	public BigDecimal getSwdPokok() {
		return swdPokok;
	}

	public void setSwdPokok(BigDecimal swdPokok) {
		this.swdPokok = swdPokok;
	}

	public BigDecimal getParkir() {
		return parkir;
	}

	public void setParkir(BigDecimal parkir) {
		this.parkir = parkir;
	}

	public BigDecimal getBillerFee() {
		return billerFee;
	}

	public void setBillerFee(BigDecimal billerFee) {
		this.billerFee = billerFee;
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
