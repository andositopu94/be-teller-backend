package com.multipolar.uiteller.backend.controller.report;

import com.multipolar.uiteller.backend.model.report.mpng2.Mpng2MonitoringPembayaran;
import com.multipolar.uiteller.backend.model.report.mpng3.Mpng3Ednp;
import com.multipolar.uiteller.backend.model.report.mpng3.Mpng3MonitoringPembayaran;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.KonversiMataUangKeTerbilang;
import com.multipolar.uiteller.backend.service.report.ReportMpnService;
import com.multipolar.uiteller.backend.service.report.ReportMpng3Service;
import com.multipolar.uiteller.backend.service.report.ReportService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller

@RequestMapping("/downloads/pdf/mpng3")
public class ReportMpng3Controller {

    //@Autowired
    //private ReportMpnService reportMpnService;
    @Autowired
    private ReportService service;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JRFileVirtualizer virtualizer;
   
    /**
     * @param tanggalBayar     <parameter name="tanggalBayar" class="java.lang.String"/>
     * @param tanggalBuku      <parameter name="tanggalBuku" class="java.lang.String"/>
     * @param kodeCabangBank   <parameter name="kodeCabangBank" class="java.lang.String"/>
     * @param ntb              <parameter name="ntb" class="java.lang.String"/>
     * @param ntpn             <parameter name="ntpn" class="java.lang.String"/>
     * @param stan             <parameter name="stan" class="java.lang.String"/>
     * @param kodeBilling      <parameter name="kodeBiling" class="java.lang.String"/>
     * @param namaWajibPajak   <parameter name="namaWajibPajak" class="java.lang.String"/>
     * @param npwp             <parameter name="npwp" class="java.lang.String"/>
     * @param alamat           <parameter name="alamat" class="java.lang.String"/>
     * @param jumlahDetail     <parameter name="jumlahDetail" class="java.lang.String"/>
     * @param jumlahSetoran    <parameter name="jumlahSetoran" class="java.math.BigDecimal"/>
     * @param mataUang         <parameter name="mataUang" class="java.lang.String"/>
     * @param message          @param mataUang <parameter na me="mataUang" class="java.lang.String"/>
     * @param pesanSingkat     <parameter name="pesanSingkat" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/mpng2/struk/djp?tanggalBayar=&tanggalBuku=&kodeCabangBank=&ntb=&ntpn=&stan=&kodeBilling=&namaWajibPajak=&npwp=&alamat=&jumlahDetail=&jumlahSetoran=&mataUang=&message=&pesanSingkat=
     */
    @GetMapping("/struk/djp")
    public ModelAndView strukDjp(
            ModelAndView view,
            @RequestParam("tanggalBayar") String tanggalBayar,
            @RequestParam("tanggalBuku") String tanggalBuku,
            @RequestParam("kodeCabangBank") String kodeCabangBank,
            @RequestParam("ntb") String ntb,
            @RequestParam("ntpn") String ntpn,
            @RequestParam("stan") String stan,
            @RequestParam("kodeBilling") String kodeBilling,
            @RequestParam("namaWajibPajak") String namaWajibPajak,
            @RequestParam("npwp") String npwp,
            @RequestParam("alamat") String alamat,
            @RequestParam("jumlahDetail") String jumlahDetail,
            @RequestParam(name = "jumlahSetoran", defaultValue = "0") BigDecimal jumlahSetoran,
            @RequestParam("mataUang") String mataUang,
            @RequestParam(name = "message", defaultValue = "This is computer generated message and requires no signature") String message,
            @RequestParam(name = "pesanSingkat", defaultValue = "Informasi ini hasil cetakan komputer dan tidak memerlukan tanda tangan") String pesanSingkat,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang
    ) {
        if (StringUtils.isBlank(ntpn)) {
            message = "";
            // pesanSingkat = "";
        }

        view.setViewName("/mpng3/report_struk_djp");
        view.addObject("datasource", new JREmptyDataSource());
        view.addObject("format", "pdf");

        view.addObject("tanggalBayar", tanggalBayar);
        view.addObject("tanggalBuku", tanggalBuku);
        view.addObject("kodeCabangBank", kodeCabangBank);
        String ntbFormatted = StringUtils.leftPad(ntb, 12, "0");
        view.addObject("ntb", ntbFormatted);
        view.addObject("ntpn", ntpn);
        view.addObject("stan", stan);
        view.addObject("kodeBiling", kodeBilling);
        if(namaWajibPajak.length()>38){
        view.addObject("namaWajibPajak", namaWajibPajak.substring(0,38));
        }else{
            view.addObject("namaWajibPajak", namaWajibPajak);
        }
        view.addObject("npwp", npwp);
        if(alamat.length()>38){
            view.addObject("alamat", alamat.substring(0,38));
        }else{
            view.addObject("alamat", alamat);
        }

        view.addObject("jumlahDetail", jumlahDetail);
        view.addObject("jumlahSetoran", jumlahSetoran);
        view.addObject("terbilang", new KonversiMataUangKeTerbilang(jumlahSetoran).toString());
        view.addObject("mataUang", mataUang);
        view.addObject("message", message);
        view.addObject("pesanSingkat", pesanSingkat);
        view.addObject("cu", cetakUlang);
        return view;
    }

    /**
     * @param tanggalBayar        <parameter name="tanggalBayar" class="java.lang.String"/>
     * @param tanggalBuku         <parameter name="tanggalBuku" class="java.lang.String"/>
     * @param kodeCabangBank      <parameter name="kodeCabangBank" class="java.lang.String"/>
     * @param ntb                 <parameter name="ntb" class="java.lang.String"/>
     * @param ntpn                <parameter name="ntpn" class="java.lang.String"/>
     * @param stan                <parameter name="stan" class="java.lang.String"/>
     * @param kodeBilling         <parameter name="kodeBiling" class="java.lang.String"/>
     * @param namaWajibBayar      <parameter name="namaWajibBayar" class="java.lang.String"/>
     * @param kementrianOrLembaga <parameter name="kementrianOrLembaga" class="java.lang.String"/>
     * @param unitEselonI         <parameter name="unitEselonI" class="java.lang.String"/>
     * @param satuanKerja         <parameter name="satuanKerja" class="java.lang.String"/>
     * @param jumlahSetoran       <parameter name="jumlahSetoran" class="java.math.BigDecimal"/>
     * @param mataUang            <parameter name="mataUang" class="java.lang.String"/>
     * @param message             <parameter name="message" class="java.lang.String"/>
     * @param pesanSingkat        <parameter name="pesanSingkat" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/mpng2/struk/dja?tanggalBayar=&tanggalBuku=&kodeCabangBank=&ntb=&ntpn=&stan=&kodeBilling=&namaWajibBayar=&kementrianOrLembaga=&unitEselonI=&satuanKerja=&jumlahSetoran=&mataUang=&message=&pesanSingkat=
     */
    @GetMapping("/struk/dja")
    public ModelAndView strukDJA(
            ModelAndView view,
            @RequestParam("tanggalBayar") String tanggalBayar,
            @RequestParam("tanggalBuku") String tanggalBuku,
            @RequestParam("kodeCabangBank") String kodeCabangBank,
            @RequestParam("ntb") String ntb,
            @RequestParam("ntpn") String ntpn,
            @RequestParam("stan") String stan,
            @RequestParam("kodeBilling") String kodeBilling,
            @RequestParam("namaWajibBayar") String namaWajibBayar,
            @RequestParam("kementrianOrLembaga") String kementrianOrLembaga,
            @RequestParam("unitEselonI") String unitEselonI,
            @RequestParam("satuanKerja") String satuanKerja,
            @RequestParam(defaultValue = "0", name = "jumlahSetoran") BigDecimal jumlahSetoran,
            @RequestParam("mataUang") String mataUang,
            @RequestParam(defaultValue = "This is computer generated message and requires no signature", name = "message") String message,
            @RequestParam(defaultValue = "Infromasi ini hasil cetakan komputer dan tidak memerlukan tanda tangan", name = "pesanSingkat") String pesanSingkat,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang
    ) {
        if (StringUtils.isBlank(ntpn)) {
            message = "";
            // pesanSingkat = "";
        }

        view.addObject("datasource", new JREmptyDataSource());
        view.addObject("format", "pdf");
        view.setViewName("/mpng3/report_struk_dja");
        view.addObject("tanggalBayar", tanggalBayar);
        view.addObject("tanggalBuku", tanggalBuku);
        view.addObject("kodeCabangBank", kodeCabangBank);
        String ntbFormatted = StringUtils.leftPad(ntb, 12, "0");
        view.addObject("ntb", ntbFormatted);
        view.addObject("ntpn", ntpn);
        view.addObject("stan", stan);
        view.addObject("kodeBiling", kodeBilling);
        view.addObject("namaWajibBayar", namaWajibBayar);
        view.addObject("kementrianOrLembaga", kementrianOrLembaga);
        view.addObject("unitEselonI", unitEselonI);
        view.addObject("satuanKerja", satuanKerja);
        view.addObject("jumlahSetoran", jumlahSetoran);
        view.addObject("terbilang", new KonversiMataUangKeTerbilang(jumlahSetoran).toString());
        view.addObject("mataUang", mataUang);
        view.addObject("message", message);
        view.addObject("pesanSingkat", pesanSingkat);
        view.addObject("cu", cetakUlang);
        return view;
    }


    /**
     * @param tanggalBayar   <parameter name="tanggalBayar" class="java.lang.String"/>
     * @param tanggalBuku    <parameter name="tanggalBuku" class="java.lang.String"/>
     * @param kodeCabangBank <parameter name="kodeCabangBank" class="java.lang.String"/>
     * @param ntb            <parameter name="ntb" class="java.lang.String"/>
     * @param ntpn           <parameter name="ntpn" class="java.lang.String"/>
     * @param stan           <parameter name="stan" class="java.lang.String"/>
     * @param kodeBilling    <parameter name="kodeBiling" class="java.lang.String"/>
     * @param namaWajibBayar <parameter name="namaWajibBayar" class="java.lang.String"/>
     * @param idWajibBayar   <parameter name="idWajibBayar" class="java.lang.String"/>
     * @param jenisDokumen   <parameter name="jenisDokumen" class="java.lang.String"/>
     * @param nomorDokumen   <parameter name="nomorDokumen" class="java.lang.String"/>
     * @param tanggalDokumen <parameter name="tanggalDokumen" class="java.util.Date"/>
     * @param kodeKppbc      <parameter name="kodeKppbc" class="java.lang.String"/>
     * @param jumlahSetoran  <parameter name="jumlahSetoran" class="java.math.BigDecimal"/>
     * @param mataUang       <parameter name="mataUang" class="java.lang.String"/>
     * @param message        <parameter name="message" class="java.lang.String"/>
     * @param pesanSingkat   <parameter name="pesanSingkat" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/mpng2/struk/djbc?tanggalBayar=&tanggalBuku=&kodeCabangBank=&ntb=&ntpn=&stan=&kodeBilling=&namaWajibBayar=&idWajibBayar=&jenisDokumen=&nomorDokumen=&tanggalDokumen=&kodeKppbc=&jumlahSetoran=&mataUang=&message=&pesanSingkat=
     */
    @GetMapping("/struk/djbc")
    public ModelAndView strukDJBC(
            ModelAndView view,
            @RequestParam("tanggalBayar") String tanggalBayar,
            @RequestParam("tanggalBuku") String tanggalBuku,
            @RequestParam("kodeCabangBank") String kodeCabangBank,
            @RequestParam("ntb") String ntb,
            @RequestParam("ntpn") String ntpn,
            @RequestParam("stan") String stan,
            @RequestParam("kodeBilling") String kodeBilling,
            @RequestParam("namaWajibBayar") String namaWajibBayar,
            @RequestParam("idWajibBayar") String idWajibBayar,
            @RequestParam("jenisDokumen") String jenisDokumen,
            @RequestParam("nomorDokumen") String nomorDokumen,
            @RequestParam("tanggalDokumen") String tanggalDokumen,
            @RequestParam("kodeKppbc") String kodeKppbc,
            @RequestParam(defaultValue = "0", name = "jumlahSetoran") BigDecimal jumlahSetoran,
            @RequestParam("mataUang") String mataUang,
            @RequestParam(defaultValue = "This is computer generated message and requires no signature", name = "message") String message,
            @RequestParam(defaultValue = "Informasi ini hasil cetak komputer dan tidak memerlukan tanda tangan", name = "pesanSingkat") String pesanSingkat,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang) {

        if (StringUtils.isBlank(ntpn)) {
            message = "";
            // pesanSingkat = "";
        }

        view.setViewName("/mpng3/report_struk_djbc");
        view.addObject("format", "pdf");
        view.addObject("datasource", new JREmptyDataSource());

        view.addObject("tanggalBayar", tanggalBayar);
        view.addObject("tanggalBuku", tanggalBuku);
        view.addObject("kodeCabangBank", kodeCabangBank);
        String ntbFormatted = StringUtils.leftPad(ntb, 12, "0");
        view.addObject("ntb", ntbFormatted);
        view.addObject("ntpn", ntpn);
        view.addObject("stan", stan);
        view.addObject("kodeBilling", kodeBilling);
        view.addObject("namaWajibBayar", namaWajibBayar);
        view.addObject("idWajibBayar", idWajibBayar);
        view.addObject("jenisDokumen", jenisDokumen);
        view.addObject("nomorDokumen", nomorDokumen);
        view.addObject("tanggalDokumen", tanggalDokumen);
        view.addObject("kodeKppbc", kodeKppbc);
        view.addObject("jumlahSetoran", jumlahSetoran);
        view.addObject("mataUang", mataUang);
        view.addObject("terbilang", new KonversiMataUangKeTerbilang(jumlahSetoran).toString());
        view.addObject("message", message);
        view.addObject("pesanSingkat", pesanSingkat);
        view.addObject("cu", cetakUlang);
        return view;
    }

    /**
     * @param tanggalBuku
     * @return http://localhost:9090/downloads/pdf/laporan/mpng3/e-dnp?tanggalBuku=
     */
    @GetMapping("/laporan/e-dnp")
    public ModelAndView laporanEDnp(
            ModelAndView view,
            @RequestParam("tanggalBuku") String tanggalBuku) {
        view.setViewName("/mpng2/report_e_dnp");
        view.addObject("format", "pdf");

        List<Mpng3Ednp> list = new ArrayList<>();
        Mpng3Ednp transaksi = new Mpng3Ednp();
        transaksi.setChannel("Teller");
        transaksi.setKodeBank("23434");
        transaksi.setKodeBilling("324334");
        transaksi.setMataUang("IDR");
        transaksi.setTanggalBayar(new Date());
        transaksi.setNtb("23434");
        transaksi.setNomorBukti("234343");
        transaksi.setNilaiPembayaran(new BigDecimal(34343));
        list.add(transaksi);
        view.addObject("datasource", new JRBeanCollectionDataSource(list));
        return view;
    }

    /**
     * @return http://localhost:9090/downloads/pdf/mpng3/laporan/monitoring-pembayaran
     */
    @GetMapping("/laporan/monitoring-pembayaran")
    public ModelAndView laporanMonitoringPembayaran(
            ModelAndView view,
            HttpServletRequest request,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("settStartDate") String settStartDate,
            @RequestParam("settEndDate") String settEndDate,
            @RequestParam("user") String user,
            @RequestParam("branch") String branch,
            @RequestParam("teller") Boolean teller,
            @RequestParam("supervisi") Boolean supervisi,
            @RequestParam("admin") Boolean admin,
            @RequestParam("sts") String sts,
            @RequestParam(value = "type", defaultValue = "pdf", required = false) String s) {
        view.setViewName("/mpng3/report_monitoring_pembayaran");
        view.addObject("format", s);
        
        boolean bankPeriod = StringUtils.isAnyBlank(startDate, endDate) ? false : true;
        StringBuilder builder = new StringBuilder();
        StringBuilder whereClause = new StringBuilder();
        StringBuilder outerWhereClause = new StringBuilder();
        if (bankPeriod) {
        	whereClause.append("AND TRUNC(TRX_DATE) BETWEEN TRUNC(TO_DATE('" + startDate + "', 'DDMMYYYYHH24MISS')) AND TRUNC(TO_DATE('" + endDate + "', 'DDMMYYYYHH24MISS')) ");
        	builder.append(service.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME));
        	builder.append(" s/d ");
        	builder.append(service.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME));
        } else {
        	outerWhereClause.append("AND TRUNC(TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY')) BETWEEN TRUNC(TO_DATE('" + settStartDate + "', 'DDMMYYYYHH24MISS')) AND TRUNC(TO_DATE('" + settEndDate + "', 'DDMMYYYYHH24MISS')) ");
        	builder.append(service.format(settStartDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME));
        	builder.append(" s/d ");
        	builder.append(service.format(settEndDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME));
        }
        view.addObject("periode", builder.toString());
        
        if (!StringUtils.equals(sts, Constants.REPORT_ALL)) 
        	outerWhereClause.append("AND STS='" + sts + "' ");
        
        if ((admin && StringUtils.isNotBlank(branch)) || supervisi) 
        	whereClause.append("AND BRANCH_CODE='" + branch + "' ");
        
        if (!admin && !supervisi)
        	whereClause.append("AND USERID='" + user + "' ");

        view.addObject("whereClause", whereClause);
        view.addObject("outerWhereClause", outerWhereClause);

        System.out.println("WhereClause"+ whereClause);
        System.out.println("OuterWhereClause"+ outerWhereClause);
//        System.out.println("DATA : "+ dataSource);

//        List<Mpng3MonitoringPembayaran> list = reportMpng3Service.getRsl(startDate, endDate, settStartDate, settEndDate, StringUtils.equals(Constants.REPORT_ALL, sts) ? Constants.REPORT_ALL_CODE : sts, user, branch, teller, supervisi, admin);
        view.addObject("datasource", dataSource);
        view.addObject(JRParameter.REPORT_VIRTUALIZER, virtualizer);
        return view;
    }

    @GetMapping("/laporan/monitoring-pembayaran-djp")
    public ModelAndView laporanMonitoringPembayaranDjp(
            ModelAndView view,
            HttpServletRequest request,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("settStartDate") String settStartDate,
            @RequestParam("settEndDate") String settEndDate,
            @RequestParam("user") String user,
            @RequestParam("branch") String branch,
            @RequestParam("teller") Boolean teller,
            @RequestParam("supervisi") Boolean supervisi,
            @RequestParam("admin") Boolean admin,
            @RequestParam("sts") String sts,
            @RequestParam(value = "type", defaultValue = "pdf", required = false) String s) {
        view.setViewName("/mpng3/report_monitoring_pembayaran_djp");
        view.addObject("format", s);
        boolean bankPeriod = StringUtils.isAnyBlank(startDate, endDate) ? false : true;
        StringBuilder builder = new StringBuilder();
        StringBuilder whereClause = new StringBuilder();
        StringBuilder outerWhereClause = new StringBuilder();
        if (bankPeriod) {
        	whereClause.append("AND TRUNC(TRX_DATE) BETWEEN TRUNC(TO_DATE('" + startDate + "', 'DDMMYYYYHH24MISS')) AND TRUNC(TO_DATE('" + endDate + "', 'DDMMYYYYHH24MISS')) ");
        	builder.append(service.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME));
        	builder.append(" s/d ");
        	builder.append(service.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME));
        } else {
        	outerWhereClause.append("AND TRUNC(TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY')) BETWEEN TRUNC(TO_DATE('" + settStartDate + "', 'DDMMYYYYHH24MISS')) AND TRUNC(TO_DATE('" + settEndDate + "', 'DDMMYYYYHH24MISS')) ");
        	builder.append(service.format(settStartDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME));
        	builder.append(" s/d ");
        	builder.append(service.format(settEndDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME));
        }
        view.addObject("periode", builder.toString());

        
        if (!StringUtils.equals(sts, Constants.REPORT_ALL)) 
        	outerWhereClause.append("AND STS='" + sts + "' ");
        
        if ((admin && StringUtils.isNotBlank(branch)) || supervisi) 
        	whereClause.append("AND BRANCH_CODE='" + branch + "' ");
        
        if (!admin && !supervisi)
        	whereClause.append("AND USERID='" + user + "' ");
        
        System.out.println("whereClause: " + whereClause);
        System.out.println("outerWhereClause: " + outerWhereClause);
        view.addObject("whereClause", whereClause);
        view.addObject("outerWhereClause", outerWhereClause);
//         List<Mpng2MonitoringPembayaran> list = reportMpnService.getRslDjp(startDate, endDate, settStartDate, settEndDate, StringUtils.equals(Constants.REPORT_ALL, sts) ? Constants.REPORT_ALL_CODE : sts, user, branch, teller, supervisi, admin);
        view.addObject("datasource", dataSource);
        view.addObject(JRParameter.REPORT_VIRTUALIZER, virtualizer);
        return view;
    }

    @GetMapping("/laporan/monitoring-pembayaran-admins")
    public ModelAndView laporanMonitoringPembayaranAdmin(
            ModelAndView view,
            HttpServletRequest request,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("settStartDate") String settStartDate,
            @RequestParam("settEndDate") String settEndDate,
            @RequestParam("user") String user,
            @RequestParam("branch") String branch,
            @RequestParam("teller") Boolean teller,
            @RequestParam("supervisi") Boolean supervisi,
            @RequestParam("admin") Boolean admin,
            @RequestParam("sts") String sts,
            @RequestParam(value = "type", defaultValue = "pdf", required = false) String s) {
        view.setViewName("/mpng3/report_monitoring_pembayaran_admin");
        view.addObject("format", s);
        
        boolean bankPeriod = StringUtils.isAnyBlank(startDate, endDate) ? false : true;
        StringBuilder builder = new StringBuilder();
        StringBuilder whereClause = new StringBuilder();
        StringBuilder outerWhereClause = new StringBuilder();
        if (bankPeriod) {
        	whereClause.append("AND TRUNC(TRX_DATE) BETWEEN TRUNC(TO_DATE('" + startDate + "', 'DDMMYYYYHH24MISS')) AND TRUNC(TO_DATE('" + endDate + "', 'DDMMYYYYHH24MISS')) ");
        	builder.append(service.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME));
        	builder.append(" s/d ");
        	builder.append(service.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME));
        } else {
        	outerWhereClause.append("AND TRUNC(TO_DATE(SETTLEMENT_DATE, 'DD/MM/YYYY')) BETWEEN TRUNC(TO_DATE('" + settStartDate + "', 'DDMMYYYYHH24MISS')) AND TRUNC(TO_DATE('" + settEndDate + "', 'DDMMYYYYHH24MISS')) ");
        	builder.append(service.format(settStartDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME));
        	builder.append(" s/d ");
        	builder.append(service.format(settEndDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME));
        }
        view.addObject("periode", builder.toString());
        
        if (!StringUtils.equalsIgnoreCase(sts, Constants.REPORT_ALL)) 
        	outerWhereClause.append("AND STS='" + sts + "' ");
        
        if ((admin && StringUtils.isNotBlank(branch)) || supervisi) 
        	whereClause.append("AND BRANCH_CODE='" + branch + "' ");
        
        if (!admin && !supervisi)
        	whereClause.append("AND USERID='" + user + "' ");
        
        System.out.println(whereClause);
        System.out.println(outerWhereClause);
        view.addObject("whereClause", whereClause);
        view.addObject("outerWhereClause", outerWhereClause);
        
        // List<Mpng2MonitoringPembayaran> list = reportMpnService.getRsl(startDate, endDate, settStartDate, settEndDate, StringUtils.equalsIgnoreCase(Constants.REPORT_ALL, sts) ? Constants.REPORT_ALL_CODE : sts.toUpperCase(), user, branch, teller, supervisi, admin);
        view.addObject("datasource", dataSource);
        view.addObject(JRParameter.REPORT_VIRTUALIZER, virtualizer);
        return view;
    }
}
