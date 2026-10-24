package com.multipolar.uiteller.backend.controller.report;

import com.multipolar.uiteller.backend.model.report.samsat.ReportPendapatanSamsat;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.report.ReportSamsatService;
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
import java.util.List;

@Controller
@RequestMapping("/downloads/pdf/samsat")
public class ReportSamsatController {

    @Autowired
    private ReportSamsatService service;
    @Autowired
    private ReportService reportService;

    /**
     * @param startDate
     * @param endDate
     * @return http://localhost:9090/downloads/pdf/samsat/laporan/pembayaran-samsat?startDate=&endDate=
     */
    @GetMapping("/laporan/pembayaran-samsat")
    public ModelAndView laporanPendapatanSamsat(
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
        String reportLocation = new StringBuilder(request.getServletContext().getContextPath())
                .append("jasper/samsat/")
                .append("report_pendapatan_samsat_cells.jasper").toString();
        view.setViewName("/samsat/report_pendapatan_samsat_details");
        view.addObject("format", "pdf");
        view.addObject("periode", new StringBuilder(reportService.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).append(" s/d ").append(reportService.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).toString());
        view.addObject("reportLocation", reportLocation);

        List<ReportPendapatanSamsat> list = this.service.listPendapatan(startDate, endDate, sts, user, branch, teller, supervisi, admin);
        view.addObject("datasource", new JRBeanCollectionDataSource(list));
        return view;
    }

    /**
     * <parameter name="nomorTransaksi" class="java.lang.String"/>
     * <parameter name="kodeBayar" class="java.lang.String"/>
     * <parameter name="nik" class="java.lang.String"/>
     * <parameter name="nomorPolisi" class="java.lang.String"/>
     * <parameter name="nama" class="java.lang.String"/>
     * <parameter name="alamat" class="java.lang.String"/>
     * <parameter name="merek" class="java.lang.String"/>
     * <parameter name="jenis" class="java.lang.String"/>
     * <parameter name="tahunBuat" class="java.lang.String"/>
     * <parameter name="milikKe" class="java.lang.String"/>
     * <parameter name="nomorMesin" class="java.lang.String"/>
     * <parameter name="nomorRangka" class="java.lang.String"/>
     * <parameter name="masaBerlaku" class="java.util.Date"/>
     * <parameter name="pokokPkb" class="java.math.BigDecimal"/>
     * <parameter name="dendaPkb" class="java.math.BigDecimal"/>
     * <parameter name="pokokProgresif" class="java.math.BigDecimal"/>
     * <parameter name="dendaProgresif" class="java.math.BigDecimal"/>
     * <parameter name="pokokSwdkllj" class="java.math.BigDecimal"/>
     * <parameter name="dendaSwdkllj" class="java.math.BigDecimal"/>
     * <parameter name="adminStnk" class="java.math.BigDecimal"/>
     *
     * @return http://localhost:9090/downloads/pdf/samsat/struk/pembayaran-samsat?nomorTransaksi=&kodeBayar=&nik=&nomorPolisi=&nama=&alamat=&merek=&jenis=&tahunBuat=&milikKe=&nomorMesin=&nomorRangka=&masaBerlaku=&pokokPkb=&dendaPkb=&pokokProgresif=&dendaProgresif=&pokokSwdkllj=&dendaSwdkllj=&adminStnk=
     */
    @GetMapping("/struk/pembayaran-samsat")
    public ModelAndView strukPembayaranSamsat(
            ModelAndView view,
            @RequestParam("nomorTransaksi") String nomorTransaksi,
            @RequestParam("kodeBayar") String kodeBayar,
//            @RequestParam("nik") String nik,
            @RequestParam("nomorPolisi") String nomorPolisi,
            @RequestParam("nama") String nama,
            @RequestParam("alamat") String alamat,
            @RequestParam("merek") String merek,
            @RequestParam("jenis") String jenis,
            @RequestParam("tahunBuat") String tahunBuat,
            @RequestParam("milikKe") String milikKe,
            @RequestParam("nomorMesin") String nomorMesin,
            @RequestParam("nomorRangka") String nomorRangka,
            @RequestParam("masaBerlaku") String masaBerlaku,
            @RequestParam(defaultValue = "", name = "secondNote") String secondNote,
            @RequestParam(defaultValue = Constants.ESAMSAT, name = "note") String note,
            @RequestParam(defaultValue = "0", name = "pokokPkb") BigDecimal pokokPkb,
            @RequestParam(defaultValue = "0", name = "dendaPkb") BigDecimal dendaPkb,
            @RequestParam(defaultValue = "0", name = "pokokProgresif") BigDecimal pokokProgresif,
            @RequestParam(defaultValue = "0", name = "dendaProgresif") BigDecimal dendaProgresif,
            @RequestParam(defaultValue = "0", name = "pokokSwdkllj") BigDecimal pokokSwdkllj,
            @RequestParam(defaultValue = "0", name = "dendaSwdkllj") BigDecimal dendaSwdkllj,
            @RequestParam(defaultValue = "", name = "cu") String tellerCu,
            @RequestParam(defaultValue = "0", name = "adminStnk") BigDecimal adminBank) {
        int admin = 5000;
        BigDecimal hardcodeAdmin = new BigDecimal(admin);
        view.setViewName("/samsat/report_bukit_pembayaran_samsat");
        view.addObject("datasource", new JREmptyDataSource());
        view.addObject("format", "pdf");

        view.addObject("nomorTransaksi", nomorTransaksi);
        view.addObject("kodeBayar", kodeBayar);
//        view.addObject("ktp", nik);
        view.addObject("nomorPolisi", nomorPolisi);
        view.addObject("nama", nama);
        view.addObject("alamat", alamat);
        view.addObject("merek", merek);
        view.addObject("jenis", jenis);
        view.addObject("tahunBuat", tahunBuat);
        view.addObject("milikKe", milikKe);
        view.addObject("nomorMesin", nomorMesin);
        view.addObject("nomorRangka", nomorRangka);
        view.addObject("masaBerlaku", masaBerlaku);
        view.addObject("pokokPkb", pokokPkb);
        view.addObject("dendaPkb", dendaPkb);
        view.addObject("pokokProgresif", pokokProgresif);
        view.addObject("dendaProgresif", dendaProgresif);
        view.addObject("pokokSwdkllj", pokokSwdkllj);
        view.addObject("dendaSwdkllj", dendaSwdkllj);
        view.addObject("adminStnk", adminBank);
        view.addObject("hardcodeAdmin", hardcodeAdmin);
        view.addObject("note", note);
        view.addObject("secondNote", secondNote);
        view.addObject("tellerCu", tellerCu);
        return view;
    }
}
