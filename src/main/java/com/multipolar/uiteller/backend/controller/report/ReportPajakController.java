package com.multipolar.uiteller.backend.controller.report;

import com.multipolar.uiteller.backend.model.report.ReportPenerimaanPajak;
import com.multipolar.uiteller.backend.model.report.pajak.ReportPenerimaanKasDaerah;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.KonversiMataUangKeTerbilang;
import com.multipolar.uiteller.backend.service.report.ReportPajakService;
import com.multipolar.uiteller.backend.service.report.ReportService;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/downloads/pdf/pajak")
public class ReportPajakController {

    @Autowired
    private ReportPajakService service;
    @Autowired
    private ReportService reportService;

//    private Logger console = LoggerFactory.getLogger(ReportPajakController.class);

    @GetMapping("/laporan/penerimaan-negara")
    public ModelAndView laporanPenerimaanNegara(ModelAndView mav) {
        mav.setViewName("/pajak/report_penerimaan_negara");
        mav.addObject("format", "pdf");
        mav.addObject("periode", "Periode : 26-03-2018 s/d 26-03-2018");

        List<ReportPenerimaanPajak> list = new ArrayList<>();
        ReportPenerimaanPajak pemasukan = new ReportPenerimaanPajak();
        pemasukan.setTanggalBuku(new Date());
        pemasukan.setTanggalBank(new Date());
        pemasukan.setStan("052454");
        pemasukan.setNo("b1436000125");
        pemasukan.setNtb("180326052454");
        pemasukan.setNpwp("028748382121000");
        pemasukan.setJumlah(new BigDecimal(200000000));
        pemasukan.setNtpn("DDF770ET25JTKU3L");
        pemasukan.setKodeCabang("103");
        pemasukan.setUser("b1326");
        pemasukan.setKodeBilling("01903458691566188");
        pemasukan.setNama("FAJAR JAYA ABDI LESTARI");
        pemasukan.setStatus("Settlement");
        pemasukan.setChannel("Teller");
        list.add(pemasukan);
        mav.addObject("datasource", list);
        return mav;
    }

    /**
     * @param tanggalBayar   <parameter name="tanggalBayar" class="java.util.Date"/>
     * @param tanggalBuku    <parameter name="tanggalBuku" class="java.util.Date"/>
     * @param kodeCabang     <parameter name="kodeCabang" class="java.lang.String"/>
     * @param ntb            <parameter name="ntb" class="java.lang.String"/>
     * @param ntpn           <parameter name="ntpn" class="java.lang.String"/>
     * @param stan           <parameter name="stan" class="java.lang.String"/>
     * @param kodeBilling    <parameter name="kodeBilling" class="java.lang.String"/>
     * @param npwp           <parameter name="npwp" class="java.lang.String"/>
     * @param namaWpWr       <parameter name="namaWajiBPajak" class="java.lang.String"/>
     * @param alamatNpwp     <parameter name="alamatWajibPajak" class="java.lang.String"/>
     * @param njop           <parameter name="NomorObjekPajak" class="java.lang.String"/>
     * @param mataAnggaran   <parameter name="mataAnggaran" class="java.lang.String"/>
     * @param jenisSetoran   <parameter name="jenisSetoran" class="java.lang.String"/>
     * @param masaPajak      <parameter name="masaPajak" class="java.lang.String"/>
     * @param nomorKetetapan <parameter name="nomorKetetapan" class="java.lang.String"/>
     * @param jumlahSetoran  <parameter name="jumlahSetoran" class="java.math.BigDecimal"/>
     * @param mataUang       <parameter name="mataUang" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/pajak/struk/penerimaan-pajak?tanggalBayar=&tanggalBuku=&kodeCabang=&ntb=&ntpn=&stan=&kodeBilling=&npwp=&namaWpWr=&alamatNpwp=&njop=&mataAnggaran=&jenisSetoran=&masaPajak=&nomorKetetapan=&jumlahSetoran=&mataUang=
     */
    @GetMapping("/struk/penerimaan-pajak")
    public ModelAndView laporanPenerimaanPajak(
            ModelAndView view,
            @RequestParam("tanggalBayar") String tanggalBayar,
            @RequestParam("tanggalBuku") String tanggalBuku,
            @RequestParam("kodeCabang") String kodeCabang,
            @RequestParam("ntb") String ntb,
            @RequestParam("ntpn") String ntpn,
            @RequestParam("stan") String stan,
            @RequestParam("kodeBilling") String kodeBilling,
            @RequestParam("npwp") String npwp,
            @RequestParam("namaWpWr") String namaWpWr,
            @RequestParam("alamatNpwp") String alamatNpwp,
            @RequestParam("njop") String njop,
            @RequestParam("mataAnggaran") String mataAnggaran,
            @RequestParam("jenisSetoran") String jenisSetoran,
            @RequestParam("masaPajak") String masaPajak,
            @RequestParam("nomorKetetapan") String nomorKetetapan,
            @RequestParam(name = "jumlahSetoran", defaultValue = "0") BigDecimal jumlahSetoran,
            @RequestParam("mataUang") String mataUang,
            @RequestParam(name = "pemko", defaultValue = "") String pemko,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang) {
        view.setViewName("/pajak/report_bukti_penerimaan_pajak");
        view.addObject("format", "pdf");
        view.addObject("datasource", new JREmptyDataSource());

        view.addObject("tanggalBayar", new Date());
        view.addObject("tanggalBuku", new Date());
        view.addObject("kodeCabang", kodeCabang);
        view.addObject("ntb", ntb);
        view.addObject("ntpn", ntpn);
        view.addObject("stan", stan);
        view.addObject("kodeBilling", kodeBilling);
        view.addObject("npwp", npwp);
        view.addObject("namaWajiBPajak", namaWpWr);
        view.addObject("alamatWajibPajak", alamatNpwp);
        view.addObject("NomorObjekPajak", njop);
        view.addObject("mataAnggaran", mataAnggaran);
        view.addObject("jenisSetoran", jenisSetoran);
        view.addObject("masaPajak", masaPajak);
        view.addObject("nomorKetetapan", nomorKetetapan);
        view.addObject("jumlahSetoran", jumlahSetoran);
        view.addObject("mataUang", mataUang);
        view.addObject("terbilang", new KonversiMataUangKeTerbilang(jumlahSetoran).toString());
        view.addObject("cu", cetakUlang);
        return view;
    }

    /**
     * @param kodeBilling   <parameter name="kodeBilling" class="java.lang.String"/>
     * @param nomorArsip    <parameter name="nomorArsip" class="java.lang.String"/>
     * @param nomorRekening <parameter name="nomorRekening" class="java.lang.String"/>
     * @param kodeCabang    <parameter name="kodeCabang" class="java.lang.String"/>
     * @param bebanRekening <parameter name="bebanRekening" class="java.lang.String"/>
     * @param jumlahSetoran <parameter name="jumlahSetoran" class="java.math.BigDecimal"/>
     * @param tanggalBuku   <parameter name="tanggalBuku" class="java.util.Date"/>
     * @param tanggalBayar  <parameter name="tanggalBayar" class="java.util.Date"/>
     * @return http://localhost:9090/downloads/pdf/pajak/struk/penerimaan-pajak-log?kodeBilling=&nomorArsip=&nomorRekening=&kodeCabang=&bebanRekening=&jumlahSetoran=&tanggalBuku=&tanggalBayar=
     */
    @GetMapping("/struk/penerimaan-pajak-log")
    public ModelAndView strukBuktiPenerimaanPajakLog(
            ModelAndView view,
            @RequestParam("kodeBilling") String kodeBilling,
            @RequestParam("nomorArsip") String nomorArsip,
            @RequestParam("nomorRekening") String nomorRekening,
            @RequestParam("kodeCabang") String kodeCabang,
            @RequestParam("bebanRekening") String bebanRekening,
            @RequestParam(name = "jumlahSetoran", defaultValue = "0") BigDecimal jumlahSetoran,
            @RequestParam("tanggalBuku") String tanggalBuku,
            @RequestParam("tanggalBayar") String tanggalBayar) {
        jumlahSetoran = new BigDecimal(14550523);
        view.setViewName("/pajak/report_bukti_penerimaan_pajak_log");
        view.addObject("format", "pdf");
        view.addObject("datasource", new JREmptyDataSource());

        view.addObject("kodeBilling", kodeBilling);
        view.addObject("nomorArsip", nomorArsip);
        view.addObject("nomorRekening", nomorRekening);
        view.addObject("kodeCabang", kodeCabang);
        view.addObject("bebanRekening", bebanRekening);
        view.addObject("jumlahSetoran", jumlahSetoran);
        view.addObject("tanggalBayar", new Date());
        view.addObject("tanggalBuku", new Date());
        return view;
    }

    /**
     * @param startDate
     * @param endDate
     * @return http://localhost:9090/downloads/pdf/pajak/laporan/pajak-sts?startDate=&endDate=
     */
    @GetMapping("/laporan/pajak-sts")
    public ModelAndView laporanPenerimaanKasDaerah(
            ModelAndView view,
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
        String jasperDetailLocation = new StringBuilder(request.getServletContext().getContextPath())
                .append("jasper/pajak/")
                .append("report_penerimaan_kas_daerah_detail.jasper")
                .toString();
        String jasperCellLocation = new StringBuilder(request.getServletContext().getContextPath())
                .append("jasper/pajak/")
                .append("report_penerimaan_kas_daerah_cell.jasper")
                .toString();
        view.setViewName("/pajak/report_penerimaan_kas_daerah");
        view.addObject("format", s);
        view.addObject("periode",
                new StringBuilder(reportService.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).append(" s/d ").append(reportService.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).toString());

        List<ReportPenerimaanKasDaerah> list = this.service.listPbbSts(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);

        view.addObject("datasource", new JRBeanCollectionDataSource(list));
        return view;
    }

    /**
     * @param tanggalBayar    <parameter name="tanggalBayar" class="java.lang.String"/>
     * @param lokasiTransaksi <parameter name="lokasiTransaksi" class="java.lang.String"/>
     * @param rekeningTujuan  <parameter name="rekeningTujuan" class="java.lang.String"/>
     * @param nomorArsip      <parameter name="nomorArsip" class="java.lang.String"/>
     * @param nomorRekening   <parameter name="nomorRekening" class="java.lang.String"/>
     * @param kodeUser        <parameter name="kodeUser" class="java.lang.String"/>
     * @param nomorSts        <parameter name="nomorSts" class="java.lang.String"/>
     * @param nomorOjectPajak <parameter name="nop" class="java.lang.String"/>
     * @param namaWajibPajak  <parameter name="namaWpWr" class="java.lang.String"/>
     * @param alamat          <parameter name="alamat" class="java.lang.String"/>
     * @param uraian          <parameter name="uraian" class="java.lang.String"/>
     * @param mataAngaran     <parameter name="mataAnggaran" class="java.lang.String"/>
     * @param masaPajak       <parameter name="masaPajak" class="java.lang.String"/>
     * @param nominal         <parameter name="nominal" class="java.math.BigDecimal"/>
     * @param denda           <parameter name="denda" class="java.math.BigDecimal"/>
     * @param admin           <parameter name="admin" class="java.math.BigDecimal"/>
     * @param jenisSetoran    <parameter name="jenisSetoran" class="java.lang.String"/>
     * @param nomorSpk        <parameter name="nomorSpk" class="java.lang.String"/>
     * @param mataUang        <parameter name="mataUang" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/pajak/struk/pajak-sts?tanggalBayar=&lokasiTransaksi=&rekeningTujuan=&nomorArsip=&nomorRekening=&kodeUser=&nomorSts=&nomorOjectPajak=&namaWajibPajak=&alamat=&uraian=&mataAngaran=&masaPajak=&nominal=&denda=&admin=&jenisSetoran=&nomorSpk=&mataUang=
     */
    @GetMapping("/struk/pajak-sts")
    public ModelAndView strukPajakSTS(
            ModelAndView view,
            @RequestParam("tanggalBayar") String tanggalBayar,
            @RequestParam("lokasiTransaksi") String lokasiTransaksi,
            @RequestParam("rekeningTujuan") String rekeningTujuan,
            @RequestParam("nomorArsip") String nomorArsip,
            @RequestParam("nomorRekening") String nomorRekening,
            @RequestParam("kodeUser") String kodeUser,
            @RequestParam("nomorSts") String nomorSts,
            @RequestParam("nomorOjectPajak") String nomorOjectPajak,
            @RequestParam("namaWajibPajak") String namaWajibPajak,
            @RequestParam("alamat") String alamat,
            @RequestParam("uraian") String uraian,
            @RequestParam("mataAngaran") String mataAngaran,
            @RequestParam("masaPajak") String masaPajak,
            @RequestParam(defaultValue = "0", name = "nominal") BigDecimal nominal,
            @RequestParam(defaultValue = "0", name = "denda") BigDecimal denda,
            @RequestParam(defaultValue = "0", name = "admin") BigDecimal admin,
            @RequestParam("jenisSetoran") String jenisSetoran,
            @RequestParam("nomorSpk") String nomorSpk,
            @RequestParam("mataUang") String mataUang,
            @RequestParam(name = "pemko", defaultValue = "") String pemko,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang) {

        view.addObject("format", "pdf");
        view.addObject("datasource", new JREmptyDataSource());
        view.setViewName("/pajak/report_bukti_pajak_sts");

        view.addObject("tanggalBayar", tanggalBayar);
        view.addObject("lokasiTransaksi", lokasiTransaksi);
        view.addObject("rekeningTujuan", rekeningTujuan);
        view.addObject("nomorArsip", nomorArsip);
        view.addObject("nomorRekening", nomorRekening);
        view.addObject("kodeUser", kodeUser);
        view.addObject("nomorSts", nomorSts);
        view.addObject("nop", nomorOjectPajak);
        view.addObject("namaWpWr", namaWajibPajak);
        view.addObject("alamat", alamat);
        view.addObject("uraian", uraian);
        view.addObject("mataAnggaran", mataAngaran);
        view.addObject("masaPajak", masaPajak);
        view.addObject("nominal", nominal);
        view.addObject("denda", denda);
        view.addObject("admin", admin);
        view.addObject("terbilang", new KonversiMataUangKeTerbilang(nominal.add(denda).add(admin)).toString());
        view.addObject("jenisSetoran", jenisSetoran);
        view.addObject("nomorSpk", nomorSpk);
        view.addObject("mataUang", mataUang);
        view.addObject("pemko", pemko);
        view.addObject("cu", cetakUlang);
        return view;
    }


}
