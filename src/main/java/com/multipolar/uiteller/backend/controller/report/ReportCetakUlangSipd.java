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

@Controller
@RequestMapping("downloads/pdf/sipd")
public class ReportCetakUlangSipd {
    @Autowired
    private ReportService service;

    @GetMapping("/struk/cetak-ulang-djp")
    public ModelAndView struckCetakUlangSipdDjp(
            ModelAndView view,
    @RequestParam("date") String date,
    @RequestParam("time") String time,
    @RequestParam("id") String id,
    @RequestParam(name = "amount", defaultValue = "")
    BigDecimal amount,
    @RequestParam("npwp") String NPWP,
    @RequestParam("name") String name,
    @RequestParam("address") String address,
    @RequestParam(name="budgetCode", defaultValue = "") String budgetCode,
    @RequestParam(name = "depositType", defaultValue = "") String depositType,
    @RequestParam(name = "taxPeriod", defaultValue = "") String taxPeriod,
    @RequestParam(name = "assessmentID", defaultValue = "") String assessmentID,
    @RequestParam("objectID") String objectID,
    @RequestParam("ntpn") String NTPN,
    @RequestParam(name="refId", defaultValue = "") String refID,
    @RequestParam(name="traceID", defaultValue = "") String traceID,
    @RequestParam("settlementDate") String settlementDate,
    @RequestParam(name = "totalDetail") String totalDetail,
    @RequestParam("branch") String branch) {
        view.addObject("format", "pdf");
        view.addObject("datasource", new JREmptyDataSource());
        view.setViewName("/sipd/report_struk_djp-sipd");

        view.addObject("date", date);
        view.addObject("time", time);
        view.addObject("id", id);
        view.addObject("amount", amount);
        view.addObject("npwp", NPWP);
        view.addObject("name", name);
        view.addObject("address", address);
        view.addObject("budgetCode", budgetCode);
        view.addObject("depositType", depositType);
        view.addObject("taxPeriod", taxPeriod);
        view.addObject("assessmentID", assessmentID);
        view.addObject("objectID", objectID);
        view.addObject("ntpn", NTPN);
        view.addObject("ntb", "-");
        view.addObject("refID", refID);
        view.addObject("traceID", traceID);
        view.addObject("settlementDate", settlementDate);
        view.addObject("jenisSetoran", depositType);
        view.addObject("assessmentID", assessmentID);
        view.addObject("mataUang", "IDR");
        view.addObject("kodeCabang", branch);
        view.addObject("jumlahDetail", totalDetail);
        view.addObject("message", "This is computer generated message and requires no signature");
        view.addObject("pesanSingkat", "Informasi ini hasil cetakan komputer dan tidak memerlukan tanda tangan");
        view.addObject("cu", "Salinan BPN Asli");
        view.addObject("terbilang", new KonversiMataUangKeTerbilang(amount).toString());

        return view;
    }
    @GetMapping("/struk/cetak-ulang-dja")
    public ModelAndView struckCetakUlangSipdDja(
            ModelAndView view,
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam("id") String id,
            @RequestParam(name = "amount", defaultValue = "")
            BigDecimal amount,
//            @RequestParam("npwp") String NPWP,
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam(name="budgetCode", defaultValue = "") String budgetCode,
            @RequestParam(name = "depositType", defaultValue = "") String depositType,
            @RequestParam(name = "taxPeriod", defaultValue = "") String taxPeriod,
            @RequestParam(name = "assessmentID", defaultValue = "") String assessmentID,
            @RequestParam("objectID") String objectID,
            @RequestParam("ntpn") String NTPN,
            @RequestParam(name="refId", defaultValue = "") String refID,
            @RequestParam(name="traceID", defaultValue = "") String traceID,
            @RequestParam("settlementDate") String settlementDate,
            @RequestParam("kementrianOrLembaga") String kementrianOrLembaga,
            @RequestParam("unitEselonI") String unitEselonI,
            @RequestParam("satuanKerja") String satuanKerja,
            @RequestParam("branch") String branch) {

        view.addObject("format", "pdf");
        view.addObject("datasource", new JREmptyDataSource());
        view.setViewName("/sipd/report_struk_dja_sipd");

        view.addObject("date", date);
        view.addObject("time", time);
        view.addObject("id", id);
        view.addObject("amount", amount);
//        view.addObject("npwp", NPWP);
        view.addObject("name", name);
        view.addObject("address", address);
        view.addObject("budgetCode", budgetCode);
        view.addObject("depositType", depositType);
        view.addObject("taxPeriod", taxPeriod);
        view.addObject("assessmentID", assessmentID);
        view.addObject("objectID", objectID);
        view.addObject("ntpn", NTPN);
        view.addObject("ntb", "-");
        view.addObject("refID", refID);
        view.addObject("traceID", traceID);
        view.addObject("settlementDate", settlementDate);
        view.addObject("kementrianOrLembaga", kementrianOrLembaga);
        view.addObject("unitEselonI", unitEselonI);
        view.addObject("satuanKerja", satuanKerja);
        view.addObject("jenisSetoran", depositType);
        view.addObject("assessmentID", assessmentID);
        view.addObject("mataUang", "IDR");
        view.addObject("kodeCabang", branch);
        view.addObject("message", "This is computer generated message and requires no signature");
        view.addObject("pesanSingkat", "Informasi ini hasil cetakan komputer dan tidak memerlukan tanda tangan");
        view.addObject("cu", "Salinan BPN Asli");
        view.addObject("terbilang", new KonversiMataUangKeTerbilang(amount).toString());

        return view;
    }
    @GetMapping("/struk/cetak-ulang-djbc")
    public ModelAndView struckCetakUlangSipdDjbc(
            ModelAndView view,
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam("id") String id,
            @RequestParam(name = "amount", defaultValue = "")
            BigDecimal amount,
            @RequestParam("npwp") String NPWP,
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam(name="budgetCode", defaultValue = "") String budgetCode,
            @RequestParam(name = "depositType", defaultValue = "") String depositType,
            @RequestParam(name = "taxPeriod", defaultValue = "") String taxPeriod,
//            @RequestParam(name = "assessmentID", defaultValue = "") String assessmentID,
//            @RequestParam("objectID") String objectID,
            @RequestParam("ntpn") String NTPN,
            @RequestParam(name="refId", defaultValue = "") String refID,
            @RequestParam(name="traceID", defaultValue = "") String traceID,
            @RequestParam("settlementDate") String settlementDate,
            @RequestParam("idWajibBayar") String idWajibBayar,
            @RequestParam("jenisDokumen") String jenisDokumen,
            @RequestParam("nomorDokumen") String nomorDokumen,
            @RequestParam("tanggalDokumen") String tanggalDokumen,
            @RequestParam("kodeKppbc") String kodeKppbc,
            @RequestParam("branch") String branch) {
        view.addObject("format", "pdf");
        view.addObject("datasource", new JREmptyDataSource());
        view.setViewName("/sipd/report_struk_djbc_sipd");

        view.addObject("date", date);
        view.addObject("time", time);
        view.addObject("id", id);
        view.addObject("amount", amount);
        view.addObject("npwp", NPWP);
        view.addObject("name", name);
        view.addObject("address", address);
        view.addObject("budgetCode", budgetCode);
        view.addObject("depositType", depositType);
        view.addObject("taxPeriod", taxPeriod);
//        view.addObject("assessmentID", assessmentID);
//        view.addObject("objectID", objectID);
        view.addObject("ntpn", NTPN);
        view.addObject("ntb", "-");
        view.addObject("refID", refID);
        view.addObject("traceID", traceID);
        view.addObject("settlementDate", settlementDate);
        view.addObject("jenisSetoran", depositType);
        view.addObject("idWajibBayar", idWajibBayar);
        view.addObject("jenisDokumen", jenisDokumen);
        view.addObject("nomorDokumen", nomorDokumen);
        view.addObject("tanggalDokumen", tanggalDokumen);
        view.addObject("kodeKppbc", kodeKppbc);
//        view.addObject("assessmentID", assessmentID);
        view.addObject("mataUang", "IDR");
        view.addObject("kodeCabang", branch);
        view.addObject("message", "This is computer generated message and requires no signature");
        view.addObject("pesanSingkat", "Informasi ini hasil cetakan komputer dan tidak memerlukan tanda tangan");
        view.addObject("cu", "Salinan BPN Asli");
        view.addObject("terbilang", new KonversiMataUangKeTerbilang(amount).toString());

        return view;
    }
}
