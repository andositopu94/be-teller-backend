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
@RequestMapping("/downloads/pdf/bifast")
public class ReportBifastController {
    @Autowired
    private ReportService service;

    @GetMapping("/struk/tagihan")
    public ModelAndView struckBifast(
            ModelAndView view,
//            @RequestParam(name = "totalAmount", defaultValue = "") BigDecimal totalAmount,
            @RequestParam("ref") String ref,
            @RequestParam("transactionDate") String transactionDate,
            @RequestParam("branchCode") String branchCode,
            @RequestParam("branchName") String branchName,
            @RequestParam("prvtBicCodeSource") String prvtBicCodeSource,
            @RequestParam("prvtBicCodeDestination") String prvtBicCodeDestination,
            @RequestParam("destinationNumber") String destinationNumber,
            @RequestParam("transferPurposeLocal") String transferPurposeLocal,
            @RequestParam("prvtTransactionNote") String prvtTransactionNote,
            @RequestParam("prvtSourceNumber") String prvtSourceNumber,
            @RequestParam("prvtSourceName") String prvtSourceName,
            @RequestParam("transactionAmount") String transactionAmount,
            @RequestParam("adminCharge") String adminCharge,
            @RequestParam("cashHandling") String cashHandling,
            @RequestParam("total") String total,
            @RequestParam("destinationName") String prvtDestinationName,
            @RequestParam("currencyLocal") String currencyLocal){

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);
        String rpTotalBill = kursIndonesia.format(BigDecimal.valueOf(Long.parseLong(transactionAmount)));
        String rpAdminFee = kursIndonesia.format(BigDecimal.valueOf(Long.parseLong(adminCharge)));
        String rpTotalAmount = kursIndonesia.format(BigDecimal.valueOf(Long.parseLong(total)));
        String rpCashHandling = kursIndonesia.format(BigDecimal.valueOf(Long.parseLong(cashHandling)/100));
        view.addObject("format", "pdf");
        view.addObject("datasource", new JREmptyDataSource());
        view.setViewName("/bifast/report_struk_bifast");

        view.addObject("ref", ref);
        view.addObject("transactionDate", transactionDate);
        view.addObject("branchCode", branchCode);
        view.addObject("branchName", branchName);
        view.addObject("prvtBicCodeSource", prvtBicCodeSource);
        view.addObject("prvtBicCodeDestination", prvtBicCodeDestination);
        view.addObject("destinationNumber", destinationNumber);
        view.addObject("transferPurposeLocal", transferPurposeLocal);
        view.addObject("prvtTransactionNote", prvtTransactionNote);
        view.addObject("prvtSourceNumber", prvtSourceNumber);
        view.addObject("prvtSourceName", prvtSourceName);
        view.addObject("totalBill", rpTotalBill);
        view.addObject("adminCharge", rpAdminFee);
        view.addObject("cashHandling", rpCashHandling);
        view.addObject("totalAmount", rpTotalAmount);
        view.addObject("destinationName", prvtDestinationName);
        view.addObject("currencyLocal", currencyLocal);

        view.addObject("terbilang", new KonversiMataUangKeTerbilang(BigDecimal.valueOf(Long.parseLong(total))).toString());

        return view;
    }

    @GetMapping("/struk/tagihan-ob")
    public ModelAndView struckBifastOb(
            ModelAndView view,
//            @RequestParam(name = "totalAmount", defaultValue = "") BigDecimal totalAmount,
            @RequestParam("ref") String ref,
            @RequestParam("transactionDate") String transactionDate,
            @RequestParam("branchCode") String branchCode,
            @RequestParam("branchName") String branchName,
            @RequestParam("prvtBicCodeSource") String prvtBicCodeSource,
            @RequestParam("prvtBicCodeDestination") String prvtBicCodeDestination,
            @RequestParam("destinationNumber") String destinationNumber,
            @RequestParam("transferPurposeLocal") String transferPurposeLocal,
            @RequestParam("prvtTransactionNote") String prvtTransactionNote,
            @RequestParam("prvtSourceNumber") String prvtSourceNumber,
            @RequestParam("prvtSourceName") String prvtSourceName,
            @RequestParam("transactionAmount") String transactionAmount,
            @RequestParam("adminCharge") String adminCharge,
            @RequestParam("total") String total,
            @RequestParam("destinationName") String prvtDestinationName,
            @RequestParam("currencyLocal") String currencyLocal){

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);
        String rpTotalBill = kursIndonesia.format(BigDecimal.valueOf(Long.parseLong(transactionAmount)));
        String rpAdminFee = kursIndonesia.format(BigDecimal.valueOf(Long.parseLong(adminCharge)));
        String rpTotalAmount = kursIndonesia.format(BigDecimal.valueOf(Long.parseLong(total)));
        view.addObject("format", "pdf");
        view.addObject("datasource", new JREmptyDataSource());
        view.setViewName("/bifast/report_struk_bifast_ob");

        view.addObject("ref", ref);
        view.addObject("transactionDate", transactionDate);
        view.addObject("branchCode", branchCode);
        view.addObject("branchName", branchName);
        view.addObject("prvtBicCodeSource", prvtBicCodeSource);
        view.addObject("prvtBicCodeDestination", prvtBicCodeDestination);
        view.addObject("destinationNumber", destinationNumber);
        view.addObject("transferPurposeLocal", transferPurposeLocal);
        view.addObject("prvtTransactionNote", prvtTransactionNote);
        view.addObject("prvtSourceNumber", prvtSourceNumber);
        view.addObject("prvtSourceName", prvtSourceName);
        view.addObject("totalBill", rpTotalBill);
        view.addObject("adminCharge", rpAdminFee);
        view.addObject("totalAmount", rpTotalAmount);
        view.addObject("destinationName", prvtDestinationName);
        view.addObject("currencyLocal", currencyLocal);

        view.addObject("terbilang", new KonversiMataUangKeTerbilang(BigDecimal.valueOf(Long.parseLong(total))).toString());

        return view;
    }
}
