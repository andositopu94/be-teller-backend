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
@RequestMapping("/downloads/pdf/bpjs-kerja-va")
public class ReportBpjsKerjaVaController {

    @Autowired
    private ReportService service;
    @Autowired
    private ReportBpjsService reportBpjsService;

    @GetMapping("/struk/tagihan")
    public ModelAndView strukBpjs(
            ModelAndView view,
            @RequestParam(name = "currency", defaultValue = "", required = false) String currency,
            @RequestParam(name = "sourceNumber", defaultValue = "", required = false) String sourceNumber,
            @RequestParam(name = "sourceName", defaultValue = "", required = false) String sourceName,
            @RequestParam(name = "nominal", defaultValue = "", required = false) String nominal,
            @RequestParam(name = "ref", defaultValue = "", required = false) String ref,
            @RequestParam(name = "branchCode", defaultValue = "", required = false) String branchCode,
            @RequestParam(name = "branchName", defaultValue = "", required = false) String branchName,
            @RequestParam(name = "payType", defaultValue = "", required = false) String payType,
            @RequestParam(name = "transactionDate", defaultValue = "", required = false) String transactionDate,
            @RequestParam(name = "date", defaultValue = "", required = false) String date,
            @RequestParam(name = "divisi", defaultValue = "", required = false) String divisi,
            @RequestParam(name = "companyName", defaultValue = "", required = false) String companyName,
            @RequestParam(name = "npp", defaultValue = "", required = false) String npp,
            @RequestParam(name = "billNumber", defaultValue = "", required = false) String billNumber,
            @RequestParam(name = "billJht", defaultValue = "", required = false) BigDecimal billJht,
            @RequestParam(name = "billJkk", defaultValue = "", required = false) BigDecimal billJkk,
            @RequestParam(name = "billJkm", defaultValue = "", required = false) BigDecimal billJkm,
            @RequestParam(name = "billJpk", defaultValue = "", required = false) BigDecimal billJpk,
            @RequestParam(name = "billJpn", defaultValue = "", required = false) BigDecimal billJpn,
            @RequestParam(name = "billBpjstk", defaultValue = "", required = false) BigDecimal billBpjstk,
            @RequestParam(name = "due", defaultValue = "", required = false) BigDecimal due,
            @RequestParam(name = "adminFee", defaultValue = "", required = false) BigDecimal adminFee,
            @RequestParam(name = "totalPayment", defaultValue = "", required = false) BigDecimal totalPayment,
            @RequestParam(name = "kodeIuran", defaultValue = "", required = false) String kodeIuran,
            @RequestParam(name = "dataParameter", defaultValue = "", required = false) String dataParameter,
            @RequestParam(name = "cetakUlang", defaultValue = "", required = false) String cetakUlang) {

        view.addObject("format", "pdf");
        view.addObject("datasource", new JREmptyDataSource());
        view.setViewName("/bpjs-kerja-va/report_struk_bpjs-kerja-va");
        view.addObject("currency", currency);
        view.addObject("sourceNumber", sourceNumber);
        view.addObject("sourceName", sourceName);
        view.addObject("nominal", nominal);
        view.addObject("ref", ref);
        view.addObject("branchCode", branchCode);
        view.addObject("branchName", branchName);
        view.addObject("payType", payType);
        view.addObject("transactionDate", transactionDate);
        view.addObject("date", date);
        view.addObject("divisi", divisi);
        view.addObject("companyName", companyName);
        view.addObject("npp", npp);
        view.addObject("billNumber", billNumber);
        view.addObject("billJht", billJht);
        view.addObject("billJkk", billJkk);
        view.addObject("billJkm", billJkm);
        view.addObject("billJpk", billJpk);
        view.addObject("billJpn", billJpn);
        view.addObject("billBpjstk", billBpjstk);
        view.addObject("due", due);
        view.addObject("adminFee", adminFee);
        view.addObject("totalPayment", totalPayment);
        view.addObject("kodeIuran", kodeIuran);
        view.addObject("dataParameter", dataParameter);
        view.addObject("terbilang", new KonversiMataUangKeTerbilang(totalPayment).toString());
        view.addObject("cetakUlang", cetakUlang);

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
