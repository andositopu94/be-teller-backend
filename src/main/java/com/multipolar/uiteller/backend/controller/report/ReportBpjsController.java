package com.multipolar.uiteller.backend.controller.report;

import com.multipolar.uiteller.backend.model.report.mpng2.BpjsPembayaran;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.KonversiMataUangKeTerbilang;
import com.multipolar.uiteller.backend.service.report.ReportBpjsService;
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
@RequestMapping("/downloads/pdf/bpjs")
public class ReportBpjsController {

    @Autowired
    private ReportService service;
    @Autowired
    private ReportBpjsService reportBpjsService;

    @GetMapping("/struk/tagihan")
    public ModelAndView strukBpjs(
            ModelAndView view,
            @RequestParam("tanggal") String tanggalBayar,
            @RequestParam("lokasiPembayaran") String lokasiBayar,
            @RequestParam("kodeTeller") String kodeTeller,
            @RequestParam("noReff") String noReff,
            @RequestParam("noVA") String noVa,
            @RequestParam("nama") String nama,
            @RequestParam("jumlahPeserta") String jumlahPeserta,
            @RequestParam("kodeCabangVA") String kodeCabangVA,
            @RequestParam("cabangVA") String cabangVA,
            @RequestParam("tagihan") BigDecimal tagihan,
            @RequestParam("adminCharge") BigDecimal adminCharge,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "period", defaultValue = "", required = false) String period,
            @RequestParam(name = "cuTeller", defaultValue = "", required = false) String cuTeller) {

        view.addObject("format", "pdf");
        view.addObject("datasource", new JREmptyDataSource());
        view.setViewName("/bpjs/report_struk_bpjs");
        view.addObject("tanggal", tanggalBayar);
        view.addObject("lokasiPembayaran", lokasiBayar);
        view.addObject("kodeTeller", kodeTeller);
        view.addObject("noReff", noReff);
        view.addObject("noVA", noVa);
        view.addObject("nama", nama);
        view.addObject("jumlahAnggota", jumlahPeserta);
        view.addObject("kodeCabangVA", kodeCabangVA);
        view.addObject("cabangVA", cabangVA);
        view.addObject("tagihan", tagihan);
        view.addObject("adminBank", adminCharge);
        view.addObject("terbilang", new KonversiMataUangKeTerbilang(tagihan.add(adminCharge)).toString());
        view.addObject("cu", cetakUlang);
        view.addObject("period", period);
        view.addObject("cuTeller", cuTeller);

        return view;
    }

    @GetMapping("/laporan/pendapatan-bpjs")
    public ModelAndView bpjs(
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
        view.setViewName("/bpjs/report_bpjs");
        view.addObject("format", s);
        view.addObject("periode", new StringBuilder(service.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).append(" s/d ").append(service.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).toString());
        List<BpjsPembayaran> list = reportBpjsService.getRsl(startDate, endDate, sts, user, branch, teller, supervisi, admin);
        view.addObject("datasource", new JRBeanCollectionDataSource(list));
        return view;
    }

}
