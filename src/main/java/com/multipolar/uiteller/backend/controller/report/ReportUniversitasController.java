package com.multipolar.uiteller.backend.controller.report;

import com.multipolar.uiteller.backend.model.report.universitas.ReportPendapatanInitUangKuliah;
import com.multipolar.uiteller.backend.model.report.universitas.ReportPendapatanUangKuliah;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.report.PendapatanInitUangKuliahService;
import com.multipolar.uiteller.backend.service.report.PendapatanUangKuliahService;
import com.multipolar.uiteller.backend.service.report.ReportService;
import net.sf.jasperreports.engine.JREmptyDataSource;
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
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/downloads/pdf/universitas")
public class ReportUniversitasController {

    @Autowired
    private PendapatanUangKuliahService service;
    @Autowired
    private PendapatanInitUangKuliahService pendapatanInitUangKuliahService;
    @Autowired
    private ReportService reportService;

    /**
     * @param startDate
     * @param endDate
     * @return http://localhost:9090/downloads/pdf/universitas/laporan/pembayaran-uang-kuliah?startDate=&endDate=
     */
    @GetMapping("/laporan/pembayaran-uang-kuliah")
    public ModelAndView laporanPendapatanPembayaranUangKuliah(
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
        String jasperDetailLocation = new StringBuilder(request.getServletContext()
                .getContextPath())
                .append("jasper/universitas/")
                .append("report_pendapatan_uang_kuliah_details.jasper")
                .toString();
        String jasperCellLocation = new StringBuilder(request.getServletContext().getContextPath())
                .append("jasper/universitas/")
                .append("report_pendapatan_uang_kuliah_cells.jasper")
                .toString();
        mav.setViewName("/universitas/report_pendapatan_uang_kuliah");
        mav.addObject("format", s);
        mav.addObject("periode", new StringBuilder(reportService.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).append(" s/d ").append(reportService.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).toString());
        List<ReportPendapatanUangKuliah> list = this.service.listPendapatanUangKuliah(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);

        mav.addObject("datasource", new JRBeanCollectionDataSource(list));
        return mav;
    }

    @GetMapping("/laporan/pendaftaran-uang-kuliah")
    public ModelAndView laporanPendapatanPendaftaranUangKuliah(
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
        String jasperDetailLocation = new StringBuilder(request.getServletContext()
                .getContextPath())
                .append("jasper/universitas/")
                .append("report_pendapatan_pendaftaran_uang_kuliah_details.jasper")
                .toString();
        String jasperCellLocation = new StringBuilder(request.getServletContext().getContextPath())
                .append("jasper/universitas/")
                .append("report_pendapatan_pendaftaran_uang_kuliah_cells.jasper")
                .toString();
        mav.setViewName("/universitas/report_pendapatan_pendaftaran_uang_kuliah");
        mav.addObject("format", s);
        mav.addObject("periode", new StringBuilder(reportService.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).append(" s/d ").append(reportService.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).toString());
        List<ReportPendapatanInitUangKuliah> list = this.pendapatanInitUangKuliahService.listPendapatanInitUangKuliah(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);

        mav.addObject("datasource", new JRBeanCollectionDataSource(list));
        return mav;
    }

    /**
     * <parameter name="tanggalPembayaran" class="java.util.Date"/>
     * <parameter name="lokasiTransaksi" class="java.lang.String"/>
     * <parameter name="nomorRekeningNasabah" class="java.lang.String"/>
     * <parameter name="nomorArsipBank" class="java.lang.String"/>
     * <parameter name="kodeUser" class="java.lang.String"/>
     * <parameter name="instansi" class="java.lang.String"/>
     * <parameter name="nim" class="java.lang.String"/>
     * <parameter name="namaMahasiswa" class="java.lang.String"/>
     * <parameter name="alamat" class="java.lang.String"/>
     * <parameter name="fakultas" class="java.lang.String"/>
     * <parameter name="prodi" class="java.lang.String"/>
     * <parameter name="angkatan" class="java.lang.String"/>
     * <parameter name="tahunBayar" class="java.lang.String"/>
     * <parameter name="cicilanKe" class="java.lang.String"/>
     * <parameter name="semester" class="java.lang.String"/>
     * <parameter name="thnAkademik" class="java.lang.String"/>
     * <parameter name="nomorPeserta" class="java.lang.String"/>
     * <parameter name="semestarBayar" class="java.lang.String"/>
     * <parameter name="kodeBayar" class="java.lang.String"/>
     * <parameter name="spp" class="java.math.BigDecimal"/>
     * <parameter name="dka" class="java.math.BigDecimal"/>
     * <parameter name="meteraikulasi" class="java.math.BigDecimal"/>
     * <parameter name="denda" class="java.math.BigDecimal"/>
     * <parameter name="adminBank" class="java.math.BigDecimal"/>
     * <parameter name="terbilang" class="java.lang.String"/>
     * <parameter name="rekTujuan1" class="java.lang.String"/>
     * <parameter name="rekTujuan2" class="java.lang.String"/>
     * <parameter name="rekTujuan3" class="java.lang.String"/>
     * <parameter name="rekTujuan4" class="java.lang.String"/>
     *
     * @param view
     * @return http://localhost:9090/downloads/pdf/universitas/struk/pembayaran-uangkuliah?tanggalPembayaran=&lokasiTransaksi=&nomorRekeningNasabah=&nomorArsipBank=&kodeUser=&instansi=&nim=&namaMahasiswa=&alamatMahasiswa=&fakultas=&prodi=&angkatan=&tahunBayar=&cicilanKe=&semester=&tahunAkademik=&nomorPeserta=&semeseterBayar=&kodeBayar=&spp=&dka=&materiaikulasi=&denda=&adminBank=&rekeningTujuan1=&rekeningTujuan2=&rekeningTujuan3=&rekeningTujuan4=
     */
    @GetMapping("/struk/pembayaran-uangkuliah")
    public ModelAndView strukPembayaranUangKuliah(@RequestParam Map<String, String> requestMap, ModelAndView view) {
        view.setViewName("/universitas/report_pembayaran_uang_kuliah");
        view.addObject("format", "pdf");
        view.addObject("datasource", new JREmptyDataSource());
        for (Map.Entry<String, String> objectEntry : requestMap.entrySet()) {
            if (StringUtils.equalsAny(objectEntry.getKey(), "adminCharge", "nominal")) {
                String value = objectEntry.getValue();
                BigDecimal number = StringUtils.isNumeric(value) ? new BigDecimal(value) : new BigDecimal("0");
                view.addObject(objectEntry.getKey(), number);
            } else
                view.addObject(objectEntry.getKey(), objectEntry.getValue());

            System.out.println("Report key: " + objectEntry.getKey() + ", Report Value: " + objectEntry.getValue());
        }
        // view.addObject("terbilang", new KonversiMataUangKeTerbilang(new BigDecimal(stringObjectMap.get("nominal"))).toString());
        return view;
    }

    @GetMapping("/struk/pendaftaran-uangkuliah")
    public ModelAndView strukPendaftaranUangKuliah(@RequestParam Map<String, String> requestMap, ModelAndView view) {
        view.setViewName("/universitas/report_pendaftaran_uang_kuliah");
        view.addObject("format", "pdf");
        view.addObject("datasource", new JREmptyDataSource());
        for (Map.Entry<String, String> objectEntry : requestMap.entrySet()) {
            if (StringUtils.equalsAny(objectEntry.getKey(), "adminCharge", "nominal")) {
                String value = objectEntry.getValue();
                BigDecimal number = StringUtils.isNumeric(value) ? new BigDecimal(value) : new BigDecimal("0");
                view.addObject(objectEntry.getKey(), number);
            } else
                view.addObject(objectEntry.getKey(), objectEntry.getValue());

            System.out.println("Report key: " + objectEntry.getKey() + ", Report Value: " + objectEntry.getValue());
        }
        return view;
    }

}
