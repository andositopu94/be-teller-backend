package com.multipolar.uiteller.backend.controller.report;

import com.multipolar.uiteller.backend.service.KonversiMataUangKeTerbilang;
import com.multipolar.uiteller.backend.service.report.ReportService;
import net.sf.jasperreports.engine.JREmptyDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@Controller
@RequestMapping("/downloads/pdf/bpjs-kerja-bpu")
public class ReportBpjsKerjaBpuController {
    @Autowired
    private ReportService service;

    @GetMapping("/struk/tagihan")
    public ModelAndView struckBpjsKerjaBpu(
            ModelAndView view,
            @RequestParam("ref") String ref,
            @RequestParam("nik") String nik,
            @RequestParam("fullName") String name,
            @RequestParam(name = "totalBill", defaultValue = "") BigDecimal totalBill,
            @RequestParam(name = "adminCharge", defaultValue = "") String adminFee,
            @RequestParam(name = "totalAmount", defaultValue = "") BigDecimal totalAmount,
            @RequestParam("efectiveDate") String efectiveDate,
            @RequestParam("expiredDate") String expiredDate,
            @RequestParam("transactionDate") String transactionDate,
            @RequestParam("kodeIuran") String kodeIuran,
            @RequestParam("branchName") String branchName,
            @RequestParam("bankBranch") String bankBranch,
            @RequestParam(name="progMonth", defaultValue = "") String progMonth){

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        totalAmount = totalAmount.divide(BigDecimal.valueOf(100));
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);
        String rpTotalBill = kursIndonesia.format(totalBill);
        System.out.println("total bill aman");
        String rpAdminFee = kursIndonesia.format(BigDecimal.valueOf(Long.parseLong(adminFee)));
        System.out.println("admin fee aman");
        String rpTotalAmount = kursIndonesia.format(totalAmount);
        System.out.println("total amount aman");
        System.out.println("BRANCH NAME"+bankBranch);
        view.addObject("format", "pdf");
        view.addObject("datasource", new JREmptyDataSource());
        view.setViewName("/bpjs-kerja-bpu/report_struk_bpjs-kerja-bpu");

        view.addObject("ref", ref);
        view.addObject("nik", nik);
        view.addObject("fullName", name);
        view.addObject("totalBill", rpTotalBill);
        view.addObject("adminCharge", rpAdminFee);
        view.addObject("totalAmount", rpTotalAmount);
        view.addObject("efectiveDate", efectiveDate);
        view.addObject("transactionDate", transactionDate);
        view.addObject("expiredDate", expiredDate);
        view.addObject("branchName", branchName);
        view.addObject("bankBranch", bankBranch);
        view.addObject("kodeIuran", kodeIuran);
        view.addObject("progMonth", progMonth);
        view.addObject("terbilang", new KonversiMataUangKeTerbilang(totalAmount).toString());

        return view;
    }
}
