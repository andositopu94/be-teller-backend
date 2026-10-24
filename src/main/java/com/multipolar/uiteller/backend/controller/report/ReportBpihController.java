package com.multipolar.uiteller.backend.controller.report;

import com.multipolar.uiteller.backend.model.report.sisko.SiskohatInitial;
import com.multipolar.uiteller.backend.model.report.sisko.SiskohatPayment;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.BpihService;
import com.multipolar.uiteller.backend.service.KonversiMataUangKeTerbilang;
import com.multipolar.uiteller.backend.service.report.ReportService;
import com.multipolar.uiteller.backend.service.report.ReportSiskoService;
import com.multipolar.uiteller.backend.service.report.ReportSiskopayService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/downloads/pdf/bpih")
public class ReportBpihController {

    @Autowired
    private ReportSiskoService reportSiskoService;
    @Autowired
    private ReportSiskopayService reportSiskopayService;
    @Autowired
    private ReportService service;
    @Autowired
    private BpihService bpihService;

    /**
     * @param startDate
     * @param endDate
     * @return http://localhost:9090/downloads/pdf/bpih/laporan/pendapatan-setoran-awal?startDate=&endDate=
     */
    @GetMapping("/laporan/pendapatan-setoran-awal")
    public ModelAndView lapoaranSetoranAwal(
            ModelAndView mav,
            HttpServletRequest request,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("sts") String sts,
            @RequestParam("user") String user,
            @RequestParam("branch") String branch,
            @RequestParam("teller") Boolean teller,
            @RequestParam("supervisi") Boolean supervisi,
            @RequestParam("admin") Boolean admin,
            @RequestParam(value = "type", defaultValue = "pdf", required = false) String s) {
        mav.addObject("format", s);
        mav.addObject("periode", new StringBuilder(service.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).append(" s/d ").append(service.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).toString());
        mav.setViewName("/bpih/report_pendapatan_bpih_setoran_awal");

        List<SiskohatInitial> list = reportSiskoService.getRsl(startDate, endDate, sts, user, branch, teller, supervisi, admin);
        mav.addObject("datasource", new JRBeanCollectionDataSource(list));
        return mav;
    }

    @GetMapping("/laporan/pendapatan-pelunasan")
    public ModelAndView lapoaranPelunasan(
            ModelAndView mav,
            HttpServletRequest request,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("user") String user,
            @RequestParam("branch") String branch,
            @RequestParam("teller") Boolean teller,
            @RequestParam("supervisi") Boolean supervisi,
            @RequestParam("admin") Boolean admin,
            @RequestParam("sts") String sts,
            @RequestParam(value = "type", defaultValue = "pdf", required = false) String s) {
        mav.addObject("format", s);
        mav.addObject("periode", new StringBuilder(service.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).append(" s/d ").append(service.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).toString());
        mav.setViewName("/bpih/report_pendapatan_bpih_pelunasan");

        List<SiskohatPayment> list = reportSiskopayService.getRsl(startDate, endDate, sts, user, branch, teller, supervisi, admin);
        mav.addObject("datasource", new JRBeanCollectionDataSource(list));
        return mav;
    }

    /**
     * @param tanggalTransaksi <parameter name="tanggalTransaksi" class="java.lang.String"/>
     * @param kodeBankBPS      <parameter name="kodeBankBPS" class="java.lang.String"/>
     * @param kodeCabangBPS    <parameter name="kodeCabangBPS" class="java.lang.String"/>
     * @param nomorTransaksi   <parameter name="nomorTransaksi" class="java.lang.String"/>
     * @param channel          <parameter name="channel" class="java.lang.String"/>
     * @param jamTransaksi     <parameter name="channel" class="java.lang.String"/>
     * @param nomorTerminal    <parameter name="jamTransaksi" class="java.lang.String"/>
     * @param nomorRekening    <parameter name="nomorTerminal" class="java.lang.String"/>
     * @param namaRekening     <parameter name="nomorRekening" class="java.lang.String"/>
     * @param kodeMataUang     <parameter name="kodeMataUang" class="java.lang.String"/>
     * @param nomorPorsi       <parameter name="nomorPorsi" class="java.lang.String"/>
     * @param namaCalonHaji    <parameter name="namaCalonHaji" class="java.lang.String"/>
     * @param nilaiSetoran     <parameter name="nilaiSetoran" class="java.math.BigDecimal"/>
     * @param message          <parameter name="message" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/bpih/resi/pelunasan-bpih?tanggalTransaksi=&kodeBankBPS=&kodeCabangBPS=&nomorTransaksi=&channel=&jamTransaksi=&nomorTerminal=&nomorRekening=&namaRekening=&kodeMataUang=&nomorPorsi=&namaCalonHaji=&nilaiSetoran=&message=
     */
    @GetMapping("/resi/pelunasan-bpih")
    public ModelAndView resiPelunasanBPIH(
            ModelAndView view,
            @RequestParam("tanggalTransaksi") String tanggalTransaksi,
            @RequestParam("kodeBankBPS") String kodeBankBPS,
            @RequestParam("kodeCabangBPS") String kodeCabangBPS,
            @RequestParam("nomorTransaksi") String nomorTransaksi,
            @RequestParam("channel") String channel,
            @RequestParam("jamTransaksi") String jamTransaksi,
            @RequestParam("nomorTerminal") String nomorTerminal,
            @RequestParam("nomorRekening") String nomorRekening,
            @RequestParam("namaRekening") String namaRekening,
            @RequestParam("kodeMataUang") String kodeMataUang,
            @RequestParam("nomorPorsi") String nomorPorsi,
            @RequestParam("namaCalonHaji") String namaCalonHaji,
            @RequestParam(defaultValue = "0", name = "nilaiSetoran") BigDecimal nilaiSetoran,
            @RequestParam(defaultValue = "Resi harap disimpan sebagai bukti pembayaran yang sah setoran pelunasan BPIH", name = "message") String message) {
        view.setViewName("/bpih/report_resi_setoran_pelunasan_bpih");
        view.addObject("datasource", new JREmptyDataSource());
        view.addObject("format", "pdf");
        view.addObject("tanggalTransaksi", tanggalTransaksi);
        view.addObject("kodeBankBPS", kodeBankBPS);
        view.addObject("kodeCabangBPS", kodeCabangBPS);
        view.addObject("nomorTransaksi", nomorTransaksi);
        view.addObject("channel", channel);
        view.addObject("jamTransaksi", jamTransaksi);
        view.addObject("nomorTerminal", nomorTerminal);
        view.addObject("nomorRekening", nomorRekening);
        view.addObject("namaRekening", namaRekening);
        view.addObject("kodeMataUang", kodeMataUang);
        view.addObject("nomorPorsi", nomorPorsi);
        view.addObject("namaCalonHaji", namaCalonHaji);
        view.addObject("nilaiSetoran", nilaiSetoran);
        view.addObject("terbilang", new KonversiMataUangKeTerbilang(nilaiSetoran).toString());
        view.addObject("message", message);
        return view;
    }

    @GetMapping("/resi/setoran")
    public ModelAndView resiSetoran(
            ModelAndView view,
            @RequestParam("tanggalTransaksi") String tanggalTransaksi,
            @RequestParam("kodeBankBPS") String kodeBankBPS,
            @RequestParam("kodeCabangBPS") String kodeCabangBPS,
            @RequestParam("nomorTransaksi") String nomorTransaksi,
            @RequestParam("channel") String channel,
            @RequestParam("jamTransaksi") String jamTransaksi,
            @RequestParam("nomorTerminal") String nomorTerminal,
            @RequestParam("nomorRekening") String nomorRekening,
            @RequestParam("namaRekening") String namaRekening,
            @RequestParam("kodeMataUang") String kodeMataUang,
            @RequestParam("nomorValidasi") String nomorValidasi,
            @RequestParam("namaCalonHaji") String namaCalonHaji,
            @RequestParam("noKtp") String noKtp,
            @RequestParam(name = "nomorVirtualAccount", defaultValue = "-") String nomorVirtualAccount,
            @RequestParam(defaultValue = "0", name = "nilaiSetoran") BigDecimal nilaiSetoran,
            @RequestParam(defaultValue = "Resi harap disimpan sebagai bukti pembayaran yang sah setoran awal BPIH", name = "message") String message) {

        noKtp = StringUtils.overlay(noKtp, StringUtils.repeat("X", noKtp.length()-4), 0, noKtp.length() - 4);
        nomorRekening = StringUtils.overlay(nomorRekening, StringUtils.repeat("X", nomorRekening.length()-4), 0, nomorRekening.length() - 4);

        view.setViewName("/bpih/report_resi_setoran_awal_bpih_r1");
        view.addObject("datasource", new JREmptyDataSource());
        view.addObject("format", "pdf");
        view.addObject("tanggalTransaksi", tanggalTransaksi);
        view.addObject("kodeBankBPS", kodeBankBPS);
        view.addObject("kodeCabangBPS", kodeCabangBPS);
        view.addObject("nomorTransaksi", nomorTransaksi);
        view.addObject("channel", channel);
        view.addObject("jamTransaksi", jamTransaksi);
        view.addObject("nomorTerminal", nomorTerminal);
        view.addObject("nomorRekening", nomorRekening);
        view.addObject("namaRekening", namaRekening);
        view.addObject("kodeMataUang", kodeMataUang);
        view.addObject("nomorValidasi", nomorValidasi);
        view.addObject("namaCalonHaji", namaCalonHaji);
        view.addObject("nilaiSetoran", nilaiSetoran);
        view.addObject("nomorVirtualAccount", nomorVirtualAccount);
        view.addObject("terbilang", new KonversiMataUangKeTerbilang(nilaiSetoran).toString());
        view.addObject("message", message);
        view.addObject("noKtp", noKtp);

        return view;
    }

    /**
     * @param nomorValidasi       <parameter name="nomorValidasi" class="java.lang.String"/>
     * @param bank                <parameter name="bank" class="java.lang.String"/>
     * @param namaKantor          <parameter name="namaKantor" class="java.lang.String"/>
     * @param alamatKantor        <parameter name="alamatKantor" class="java.lang.String"/>
     * @param nomorRekening       <parameter name="nomorRekening" class="java.lang.String"/>
     * @param namaCalonHaji       <parameter name="namaCalonHaji" class="java.lang.String"/>
     * @param jenisKelamin        <parameter name="jenisKelamin" class="java.lang.String"/>
     * @param tempatLahirNasabah  <parameter name="tempatLahirNasabah" class="java.lang.String"/>
     * @param tanggalLahirNasabah <parameter name="tanggalLahirNasabah" class="java.lang.String"/>
     * @param alamatNasabah       <parameter name="alamatNasabah" class="java.lang.String"/>
     * @param kodePosNasabah      <parameter name="kodePosNasabah" class="java.lang.String"/>
     * @param desaKelurahan       <parameter name="desaKelurahan" class="java.lang.String"/>
     * @param kecamatan           <parameter name="kecamatan" class="java.lang.String"/>
     * @param kabupaten           <parameter name="kabupaten" class="java.lang.String"/>
     * @param provinsi            <parameter name="provinsi" class="java.lang.String"/>
     * @param pendidikan          <parameter name="pendidikan" class="java.lang.String"/>
     * @param pekerjaan           <parameter name="pekerjaan" class="java.lang.String"/>
     * @param jumlahPembayaran    <parameter name="jumlahPembayaran" class="java.math.BigDecimal"/>
     * @param lokasiTandatangan   <parameter name="lokasiTandaTangan" class="java.lang.String"/>
     * @param nomorVirtualAccount <parameter name="nomorVirtualAccount" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/bpih/struk/pembayaran-awal?nomorValidasi=&bank=&namaKantor=&alamatKantor=&nomorRekening=&namaCalonHaji=&jenisKelamin=&tempatLahirNasabah=&tanggalLahirNasabah=&alamatNasabah=&kodePosNasabah=&desaKelurahan=&kecamatan=&kabupaten=&provinsi=&pendidikan=&pekerjaan=&jumlahPembayaran=&lokasiTandatangan=&nomorVirtualAccount=
     */
    @GetMapping("/struk/pembayaran-awal-bpih")
    public ModelAndView strukPembayaranAwal(
            ModelAndView view,
            @RequestParam("nomorValidasi") String nomorValidasi,
            @RequestParam("bank") String bank,
            @RequestParam("namaKantor") String namaKantor,
            @RequestParam("alamatKantor") String alamatKantor,
            @RequestParam("nomorRekening") String nomorRekening,
            @RequestParam("namaCalonHaji") String namaCalonHaji,
            @RequestParam("jenisKelamin") String jenisKelamin,
            @RequestParam("tempatLahirNasabah") String tempatLahirNasabah,
            @RequestParam("tanggalLahirNasabah") String tanggalLahirNasabah,
            @RequestParam("alamatNasabah") String alamatNasabah,
            @RequestParam("kodePosNasabah") String kodePosNasabah,
            @RequestParam("desaKelurahan") String desaKelurahan,
            @RequestParam("kecamatan") String kecamatan,
            @RequestParam("kabupaten") String kabupaten,
            @RequestParam("provinsi") String provinsi,
            @RequestParam("pendidikan") String pendidikan,
            @RequestParam("pekerjaan") String pekerjaan,
            @RequestParam("sourceName") String sourceName,
            @RequestParam(name = "jumlahPembayaran", defaultValue = "0") BigDecimal jumlahPembayaran,
            @RequestParam(name = "lokasiTandatangan", defaultValue = "kab.aceh selatan") String lokasiTandatangan,
            @RequestParam("nomorVirtualAccount") String nomorVirtualAccount) {
        view.setViewName("/bpih/report_bukti_setoran_awal_bpih");
        view.addObject("datasource", new JREmptyDataSource());
        view.addObject("format", "pdf");

        view.addObject("nomorValidasi", nomorValidasi);
        view.addObject("bank", bank);
        view.addObject("namaKantor", namaKantor);
        view.addObject("alamatKantor", alamatKantor);
        view.addObject("nomorRekening", nomorRekening);
        view.addObject("namaCalonHaji", namaCalonHaji);
        view.addObject("jenisKelamin", jenisKelamin);
        view.addObject("tempatLahirNasabah", tempatLahirNasabah);
        view.addObject("tanggalLahirNasabah", tanggalLahirNasabah);
        view.addObject("alamatNasabah", alamatNasabah);
        view.addObject("kodePosNasabah", kodePosNasabah);
        view.addObject("desaKelurahan", desaKelurahan);
        view.addObject("kecamatan", kecamatan);
        view.addObject("kabupaten", kabupaten);
        view.addObject("provinsi", provinsi);
        view.addObject("pendidikan", pendidikan);
        view.addObject("pekerjaan", pekerjaan);
        view.addObject("jumlahPembayaran", jumlahPembayaran);
        view.addObject("terbilang", new KonversiMataUangKeTerbilang(jumlahPembayaran).toString());
        view.addObject("lokasiTandaTangan", lokasiTandatangan.toUpperCase());
        view.addObject("nomorVirtualAccount", nomorVirtualAccount);
        view.addObject("sourceName", sourceName);

        return view;
    }

    /**
     * @param nomorPorsi         <parameter name="nomorProsi" class="java.lang.String"/>
     * @param bank               <parameter name="bank" class="java.lang.String"/>
     * @param namaKantor         <parameter name="namaKantor" class="java.lang.String"/>
     * @param alamatKantor       <parameter name="alamatKantor" class="java.lang.String"/>
     * @param namaJamaah         <parameter name="namaJamaahHaji" class="java.lang.String"/>
     * @param binBinti           <parameter name="binOrBinti" class="java.lang.String"/>
     * @param jenisKelamin       <parameter name="jenisKelamin" class="java.lang.String"/>
     * @param tempatLahirJamaah  <parameter name="tempatLahirNasabah" class="java.lang.String"/>
     * @param umurJamaahHaji     <parameter name="umurJamaahHaji" class="java.lang.String"/>
     * @param tanggalLahirJamaah <parameter name="tanggalLahirNasabah" class="java.lang.String"/>
     * @param alamatNasabah      <parameter name="alamatNasabah" class="java.lang.String"/>
     * @param kodePosNasabah     <parameter name="kodePosNasabah" class="java.lang.String"/>
     * @param desaKelurahan      <parameter name="desaKelurahan" class="java.lang.String"/>
     * @param kecamatan          <parameter name="kecamatan" class="java.lang.String"/>
     * @param kabupaten          <parameter name="kabupaten" class="java.lang.String"/>
     * @param provinsi           <parameter name="provinsi" class="java.lang.String"/>
     * @param embarkasi          <parameter name="embarkasi" class="java.lang.String"/>
     * @param setoranAwal        <parameter name="setoranAwal" class="java.math.BigDecimal"/>
     * @param jumlahPembayaran   <parameter name="jumlahPembayaran" class="java.math.BigDecimal"/>
     * @param tahunHijriah       <parameter name="tahunHijriah" class="java.lang.String"/>
     * @param tahunMasehi        <parameter name="tahunMasehi" class="java.lang.String"/>
     * @param lokasiTandaTangan  <parameter name="lokasiTandaTangan" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/bpih/struk/pelunasan-bpih?nomorPorsi=&bank=&namaKantor=&alamatKantor=&namaJamaah=&binBinti=&jenisKelamin=&tempatLahirJamaah=&umurJamaahHaji=&tanggalLahirJamaah=&alamatNasabah=&kodePosNasabah=&desaKelurahan=&kecamatan=&kabupaten=&provinsi=&embarkasi=&setoranAwal=&jumlahPembayaran=&tahunHijriah=&tahunMasehi=&lokasiTandaTangan=
     */
    @GetMapping("/struk/pelunasan-bpih")
    public ModelAndView buktiPelunasanBpih(
            ModelAndView view,
            @RequestParam("nomorPorsi") String nomorPorsi,
            @RequestParam("bank") String bank,
            @RequestParam("namaKantor") String namaKantor,
            @RequestParam("alamatKantor") String alamatKantor,
            @RequestParam("namaJamaah") String namaJamaah,
            @RequestParam("binBinti") String binBinti,
            @RequestParam("umurJamaahHaji") String umurJamaahHaji,
            @RequestParam("pelunasanMonthAge") String pelunasanMonthAge,
            @RequestParam("jenisKelamin") String jenisKelamin,
            @RequestParam("tempatLahirJamaah") String tempatLahirJamaah,
            @RequestParam("tanggalLahirJamaah") String tanggalLahirJamaah,
            @RequestParam("alamatNasabah") String alamatNasabah,
            @RequestParam("kodePosNasabah") String kodePosNasabah,
            @RequestParam("desaKelurahan") String desaKelurahan,
            @RequestParam("kecamatan") String kecamatan,
            @RequestParam("kabupaten") String kabupaten,
            @RequestParam("provinsi") String provinsi,
            @RequestParam("embarkasi") String embarkasi,
            @RequestParam(defaultValue = "0", name = "setoranAwal") BigDecimal setoranAwal,
            @RequestParam(defaultValue = "0", name = "jumlahPembayaran") BigDecimal jumlahPembayaran,
            @RequestParam(defaultValue = "0", name = "biayaBpih") BigDecimal biayaBpih,
            @RequestParam("tahunHijriah") String tahunHijriah,
            @RequestParam("tahunMasehi") String tahunMasehi,
            @RequestParam("lokasiTandaTangan") String lokasiTandaTangan) {
        view.setViewName("/bpih/report_bukti_pelunasan_bpih");
        view.addObject("datasource", new JREmptyDataSource());
        view.addObject("format", "pdf");
        view.addObject("nomorProsi", nomorPorsi);
        view.addObject("bank", bank);
        view.addObject("namaKantor", namaKantor);
        view.addObject("alamatKantor", alamatKantor);
        view.addObject("namaJamaahHaji", namaJamaah);
        view.addObject("binOrBinti", binBinti);
        view.addObject("umurJamaahHaji", umurJamaahHaji);
        view.addObject("pelunasanMonthAge", pelunasanMonthAge);
        view.addObject("jenisKelamin", jenisKelamin);
        view.addObject("tempatLahirNasabah", tempatLahirJamaah);
        view.addObject("tanggalLahirNasabah", tanggalLahirJamaah);
        view.addObject("alamatNasabah", alamatNasabah);
        view.addObject("kodePosNasabah", kodePosNasabah);
        view.addObject("desaKelurahan", desaKelurahan);
        view.addObject("kecamatan", kecamatan);
        view.addObject("kabupaten", kabupaten);
        view.addObject("provinsi", provinsi);
        view.addObject("embarkasi", embarkasi);
        view.addObject("setoranAwal", setoranAwal);
        view.addObject("jumlahPembayaran", jumlahPembayaran);
        view.addObject("biayaBpih", biayaBpih);
        view.addObject("terbilang", new KonversiMataUangKeTerbilang(biayaBpih).toString());
        view.addObject("lokasiTandaTangan", lokasiTandaTangan);
        view.addObject("tahunHijriah", tahunHijriah);
        view.addObject("tahunMasehi", tahunMasehi);

        return view;
    }
}