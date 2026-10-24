package com.multipolar.uiteller.backend.controller.report;

import com.multipolar.uiteller.backend.model.report.samsatNasional.ReportPendapatanSamsatNasional;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.report.ReportSamsatNasionalService;
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
@RequestMapping("/downloads/pdf/samsat-nasional")
public class ReportSamsatNasionalController {

    @Autowired
    private ReportSamsatNasionalService service;
    @Autowired
    private ReportService reportService;

    /**
     * @param startDate
     * @param endDate
     * @return http://localhost:9090/downloads/pdf/samsat-nasional/laporan/pembayaran-samsat-nasional?startDate=&endDate=
     */	
    @GetMapping("/laporan/pembayaran-samsat-nasional")
    public ModelAndView laporanPendapatanSamsatNasional(
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
                .append("jasper/samsat-nasional/")
                .append("report_pendapatan_samsat_nasional_cells.jasper").toString();
        view.setViewName("/samsat-nasional/report_pendapatan_samsat_nasional_details");
        view.addObject("format", "pdf");
        view.addObject("periode", new StringBuilder(reportService.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).append(" s/d ").append(reportService.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).toString());
        view.addObject("reportLocation", reportLocation);

        List<ReportPendapatanSamsatNasional> list = this.service.listPendapatan(startDate, endDate, sts, user, branch, teller, supervisi, admin);
        view.addObject("datasource", new JRBeanCollectionDataSource(list));
        return view;
    }
    /**
	* <parameter name="tanggalTransaksi" class="java.lang.String"/>
	* <parameter name="nomorTransaksi" class="java.lang.String"/>
	* <parameter name="kodeBayar" class="java.lang.String"/>
	* <parameter name="ktp" class="java.lang.String"/>
	* <parameter name="nomorPolisi" class="java.lang.String"/>
	* <parameter name="nama" class="java.lang.String"/>
	* <parameter name="merek" class="java.lang.String"/>
	* <parameter name="tipe" class="java.lang.String"/>
	* <parameter name="jenis" class="java.lang.String"/>
	* <parameter name="model" class="java.lang.String"/>
	* <parameter name="warna" class="java.lang.String"/>
	* <parameter name="roda" class="java.lang.String"/>
	* <parameter name="tahunBuat" class="java.lang.String"/>
	* <parameter name="milikKe" class="java.lang.String"/>
	* <parameter name="cc" class="java.lang.String"/>
	* <parameter name="nomorRangka" class="java.lang.String"/>
	* <parameter name="TBPKBLama" class="java.lang.String"/>
	* <parameter name="TBPKBBaru" class="java.lang.String"/>
	* <parameter name="akhirSTNK" class="java.lang.String"/>
	* <parameter name="pokokPkb" class="java.math.BigDecimal"/>
	* <parameter name="dendaPkb" class="java.math.BigDecimal"/>
	* <parameter name="pokokSwdkllj" class="java.math.BigDecimal"/>
	* <parameter name="dendaSwdkllj" class="java.math.BigDecimal"/>
	* <parameter name="pokokPnbp" class="java.math.BigDecimal"/>
	* <parameter name="dendaPnbp" class="java.math.BigDecimal"/>
	* <parameter name="parkir" class="java.math.BigDecimal"/>
	* <parameter name="adminCharge" class="java.math.BigDecimal"/>
	* <parameter name="total" class="java.math.BigDecimal"/>
	* <parameter name="remark" class="java.lang.String"/>
	* <parameter name="tellerCu" class="java.lang.String"/>
	* <parameter name="cu" class="java.lang.String"/>
	* <parameter name="branchName" class="java.lang.String"/>
	* <parameter name="archiveNumber" class="java.lang.String"/>
	* <parameter name="sourceNumber" class="java.lang.String"/>
	* <parameter name="footer" class="java.lang.String"/>
     *
     * @return http://localhost:9090/downloads/pdf/samsat-nasional/struk/pembayaran-samsat-nasional?tanggalTransaksi=&nomorTransaksi=&kodeBayar=&ktp=&nomorPolisi=&nama=&tipe=&merek=&jenis=&model=&tahunBuat=&milikKe=&warna=&nomorRangka=&roda=&cc=&TBPKBLama=&TBPKBBaru=&akhirSTNK=&pokokPkb=&dendaPkb=&pokokPnbp=&dendaPnbp=&pokokSwdkllj=&dendaSwdkllj=&parkir=&adminCharge=&total=&remark&branchName&archiveNumber=&sourceNumber=&footer=
     */
    @GetMapping("/struk/pembayaran-samsat-nasional")
    public ModelAndView strukPembayaranSamsat(
            ModelAndView view,
            @RequestParam("branchName") String branchName,
            @RequestParam("archiveNumber") String archiveNumber,
            @RequestParam("sourceNumber") String sourceNumber,
            @RequestParam("tanggalTransaksi") String tanggalTransaksi,
            @RequestParam("nomorTransaksi") String nomorTransaksi,
            @RequestParam("kodeBayar") String kodeBayar,
            @RequestParam("ktp") String ktp,
            @RequestParam("nomorPolisi") String nomorPolisi,
            @RequestParam("nama") String nama,
            @RequestParam("merek") String merek,
            @RequestParam("tipe") String tipe,
            @RequestParam("jenis") String jenis,
            @RequestParam("model") String model,
            @RequestParam("tahunBuat") String tahunBuat,
            @RequestParam("milikKe") String milikKe,
            @RequestParam("warna") String warna,
            @RequestParam("roda") String roda,
            @RequestParam("cc") String cc,
            @RequestParam("remark") String remark,
            @RequestParam("nomorRangka") String nomorRangka,
            @RequestParam("TBPKBLama") String TBPKBLama,
            @RequestParam("TBPKBBaru") String TBPKBBaru,
            @RequestParam("akhirSTNK") String akhirSTNK,
            @RequestParam(defaultValue = "BUKTI E-PENGESAHAN STNK DAPAT DILIHAT PADA APLIKASI SAMSAT ONLINE NASIONAL/EMAIL.", name = "footer") String footer,
            @RequestParam(defaultValue = "Contact center 14002", name = "footerSecond") String footerSecond,
            @RequestParam(defaultValue = "0", name = "pokokPkb") BigDecimal pokokPkb,
            @RequestParam(defaultValue = "0", name = "dendaPkb") BigDecimal dendaPkb,
            @RequestParam(defaultValue = "0", name = "pokokPnbp") BigDecimal pokokPnbp,
            @RequestParam(defaultValue = "0", name = "dendaPnbp") BigDecimal dendaPnbp,
            @RequestParam(defaultValue = "0", name = "pokokSwdkllj") BigDecimal pokokSwdkllj,
            @RequestParam(defaultValue = "0", name = "dendaSwdkllj") BigDecimal dendaSwdkllj,
            @RequestParam(defaultValue = "0", name = "parkir") BigDecimal parkir,
            @RequestParam(defaultValue = "", name = "tellerCu") String tellerCu,
            @RequestParam(defaultValue = "", name = "cu") String cu,
            @RequestParam(defaultValue = "0", name = "adminCharge") BigDecimal adminCharge,
            @RequestParam(defaultValue = "0", name = "total") BigDecimal total) {
        view.setViewName("/samsat-nasional/report_pembayaran_samsat_nasional");
        view.addObject("datasource", new JREmptyDataSource());
        view.addObject("format", "pdf");

        view.addObject("branchName", branchName);
        view.addObject("archiveNumber", archiveNumber);
        view.addObject("sourceNumber", sourceNumber);
        view.addObject("tanggalTransaksi", tanggalTransaksi);
        view.addObject("nomorTransaksi", nomorTransaksi);
        view.addObject("kodeBayar", kodeBayar);
        view.addObject("ktp", ktp);
        view.addObject("nomorPolisi", nomorPolisi);
        view.addObject("nama", nama);
        view.addObject("merek", merek);
        view.addObject("tipe", tipe);
        view.addObject("jenis", jenis);
        view.addObject("model", model);
        view.addObject("tahunBuat", tahunBuat);
        view.addObject("milikKe", milikKe);
        view.addObject("warna", warna);
        view.addObject("roda", roda);
        view.addObject("cc", cc);
        view.addObject("remark", remark);
        view.addObject("nomorRangka", nomorRangka);
        view.addObject("TBPKBLama", TBPKBLama);
        view.addObject("TBPKBBaru", TBPKBBaru);
        view.addObject("akhirSTNK", akhirSTNK);
        view.addObject("pokokPkb", pokokPkb);
        view.addObject("dendaPkb", dendaPkb);
        view.addObject("pokokPnbp", pokokPnbp);
        view.addObject("dendaPnbp", dendaPnbp);
        view.addObject("pokokSwdkllj", pokokSwdkllj);
        view.addObject("dendaSwdkllj", dendaSwdkllj);
        view.addObject("parkir", parkir);
        view.addObject("adminCharge", adminCharge);
        view.addObject("total", total);
        view.addObject("footer", footer);
        view.addObject("footerSecond", footerSecond);
        view.addObject("tellerCu", tellerCu);
        view.addObject("cu", cu);
        return view;
    }
}
