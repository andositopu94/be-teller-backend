package com.multipolar.uiteller.backend.controller.report;

import com.multipolar.uiteller.backend.model.report.finnet.ReportPendapatanFinnetPstn;
import com.multipolar.uiteller.backend.model.report.finnet.ReportPendapatanFinnetSpeedy;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.KonversiMataUangKeTerbilang;
import com.multipolar.uiteller.backend.service.report.ReportFinnetService;
import com.multipolar.uiteller.backend.service.report.ReportService;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/downloads/pdf/finnet")
public class ReportFinnetController {

    private final static Logger console = LoggerFactory.getLogger(ReportFinnetController.class);

    @Autowired
    private ReportFinnetService reportService;
    @Autowired
    private ReportService service;

    /**
     * @param startDate
     * @param endDate
     * @return http://localhost:9090/downloads/pdf/finnet/laporan/pendapatan-speedy?startDate=&endDate=
     */
    @GetMapping(value = "/laporan/pendapatan-speedy")
    public ModelAndView laporanPendapatanBankSpeedy(
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

        String jasperDetailLocation = new StringBuilder(request.getServletContext().getContextPath())
                .append("jasper/finnet/speedy/")
                .append("report_pendapatan_speedy_details.jasper").toString();
        mav.addObject("format", s);
        mav.setViewName("/finnet/report_pendapatan_finnet");
        String jasperCellLocation = new StringBuilder(request.getServletContext().getContextPath())
                .append("jasper/finnet/speedy/")
                .append("report_pendapatan_speedy_cells.jasper").toString();
        mav.addObject("periode", new StringBuilder(service.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).append(" s/d ").append(service.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).toString());
        List<ReportPendapatanFinnetSpeedy> list = this.reportService.findByDateStartAndEndSpeedy(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);
        mav.addObject("datasource", new JRBeanCollectionDataSource(list));
        return mav;
    }

    /**
     * @param startDate
     * @param endDate
     * @return http://localhost:9090/downloads/pdf/finnet/laporan/pendapatan-pstn?startDate=&endDate=
     */
    @GetMapping(value = "/laporan/pendapatan-pstn")
    public ModelAndView laporanPendapatanBankSumutPTSN(
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
            @RequestParam(value = "type", defaultValue = "pdf", required = false) String s
    ) {
        String jasperDetailLocation = new StringBuilder(request.getServletContext().getContextPath())
                .append("jasper/finnet/pstn/")
                .append("report_pendapatan_pstn_details.jasper")
                .toString();
        String jasperCellLocation = new StringBuilder(request.getServletContext().getContextPath())
                .append("jasper/finnet/pstn/")
                .append("report_pendapatan_pstn_cells.jasper")
                .toString();
        mav.addObject("format", s);
        mav.setViewName("/finnet/report_pendapatan_finnet");
        mav.addObject("periode", new StringBuilder(service.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).append(" s/d ").append(service.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).toString());

        List<ReportPendapatanFinnetPstn> list = reportService.findByDateStartAndEndPstn(jasperDetailLocation, jasperCellLocation, startDate, endDate, sts, user, branch, teller, supervisi, admin);
        for (ReportPendapatanFinnetPstn pstn : list) {
            console.info("report location: {}", pstn.getJasperLocation());
        }
        mav.addObject("datasource", new JRBeanCollectionDataSource(list));
        return mav;
    }

    /**
     * @param nomorJastel       <parameter name="nomorJastel" class="java.lang.String"/>
     * @param jenisJasa         <parameter name="jenisJasa" class="java.lang.String"/>
     * @param kodeDivre         <parameter name="kodeDivre" class="java.lang.String"/>
     * @param kodeDatel         <parameter name="kodeDatel" class="java.lang.String"/>
     * @param namaPelanggan     <parameter name="namaPelanggan" class="java.lang.String"/>
     * @param npwp              <parameter name="npwp" class="java.lang.String"/>
     * @param bulanBerjalan     <parameter name="bulanBerjalan" class="java.math.BigDecimal"/>
     * @param tunggakanSebulan  <parameter name="tunggakanSebulan" class="java.math.BigDecimal"/>
     * @param tunggakanDuaBulan <parameter name="tunggakanDuaBulan" class="java.math.BigDecimal"/>
     * @param biayaAdmin        <parameter name="biayaAdminBank" class="java.math.BigDecimal"/>
     * @param nomorRef          <parameter name="nomorRef" class="java.lang.String"/>
     * @param pembayaran        <parameter name="pembayaran" class="java.lang.String"/>
     * @param kodeCabangLokasi  <parameter name="kodeCabangLokasi" class="java.lang.String"/>
     * @param rekeningBeban     <parameter name="rekeningBeban" class="java.lang.String"/>
     * @param nomorArsip        <parameter name="nomorArsip" class="java.lang.String"/>
     * @param tanggalTransaksi  <parameter name="tanggalTransaksi" class="java.sql.Date"/>
     * @param jumlahBill        <parameter name="jumlahBill" class="java.lang.String"/>
     * @param nomorRef1         <parameter name="nomorRef1" class="java.lang.String"/>
     * @param nomorRef2         <parameter name="nomorRef2" class="java.lang.String"/>
     * @param nomorRef3         <parameter name="nomorRef3" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/finnet/struk/pembayaran-telepon?nomorJastel=&jenisJasa=&kodeDivre=&kodeDatel=&namaPelanggan=&npwp=&bulanBerjalan=&tunggakanSebulan=&tunggakanDuaBulan=&biayaAdmin=&nomorRef=&pembayaran=&kodeCabangLokasi=&rekeningBeban=&nomorArsip=&tanggalTransaksi=&jumlahBill=&nomorRef1=&nomorRef2=&nomorRef3=&nomorResi=
     */

    @GetMapping(value = "/struk/pembayaran-telepon")
    public ModelAndView strukPembayaranTelephon(
            ModelAndView mav,
            @RequestParam("nomorJastel") String nomorJastel,
            @RequestParam("jenisJasa") String jenisJasa,
            @RequestParam("kodeDivre") String kodeDivre,
            @RequestParam("kodeDatel") String kodeDatel,
            @RequestParam("namaPelanggan") String namaPelanggan,
            @RequestParam("npwp") String npwp,
            @RequestParam(name = "bulanBerjalan", defaultValue = "0") BigDecimal bulanBerjalan,
            @RequestParam(name = "tunggakanSebulan", defaultValue = "0") BigDecimal tunggakanSebulan,
            @RequestParam(name = "tunggakanDuaBulan", defaultValue = "0") BigDecimal tunggakanDuaBulan,
            @RequestParam(name = "biayaAdmin", defaultValue = "0") BigDecimal biayaAdmin,
            @RequestParam("nomorRef") String nomorRef,
            @RequestParam("pembayaran") String pembayaran,
            @RequestParam("kodeCabangLokasi") String kodeCabangLokasi,
            @RequestParam("rekeningBeban") String rekeningBeban,
            @RequestParam("nomorArsip") String nomorArsip,
            @RequestParam("tanggalTransaksi") String tanggalTransaksi,
            @RequestParam("nomorRef1") String nomorRef1,
            @RequestParam("nomorRef2") String nomorRef2,
            @RequestParam("nomorRef3") String nomorRef3,
            @RequestParam("jumlahBill") String jumlahBill,
            @RequestParam("nomorResi") String nomorResi,
            @RequestParam(name = "footer", defaultValue = "") String footer,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "tellerCu", defaultValue = "", required = false) String tellerCetakUlang
    ) {
        mav.addObject("format", "pdf");
        mav.addObject("datasource", new JREmptyDataSource());
        mav.setViewName("/finnet/report_pembayaran_telepon");
        mav.addObject("nomorJastel", nomorJastel);
        mav.addObject("jenisJasa", jenisJasa);
        mav.addObject("kodeDivre", kodeDivre);
        mav.addObject("kodeDatel", kodeDatel);
        mav.addObject("namaPelanggan", namaPelanggan);
        mav.addObject("npwp", npwp);
        mav.addObject("bulanBerjalan", bulanBerjalan);
        mav.addObject("tunggakanSebulan", tunggakanSebulan);
        mav.addObject("tunggakanDuaBulan", tunggakanDuaBulan);
        mav.addObject("biayaAdminBank", biayaAdmin);
        mav.addObject("terbilang", new KonversiMataUangKeTerbilang(
                bulanBerjalan.add(tunggakanSebulan).add(tunggakanDuaBulan).add(biayaAdmin)).toString()
        );
        mav.addObject("nomorRef", nomorRef);
        mav.addObject("pembayaran", pembayaran);
        mav.addObject("kodeCabangLokasi", kodeCabangLokasi);
        mav.addObject("rekeningBeban", rekeningBeban);
        mav.addObject("nomorArsip", nomorArsip);
        mav.addObject("tanggalTransaksi", new Date());
        mav.addObject("nomorRef1", nomorRef1);
        mav.addObject("nomorRef2", nomorRef2);
        mav.addObject("nomorRef3", nomorRef3);
        mav.addObject("jumlahBill", jumlahBill);
        mav.addObject("nomorResi", nomorResi);
        mav.addObject("footer", footer);
        mav.addObject("cu", cetakUlang);
        mav.addObject("tellerCu", tellerCetakUlang);
        return mav;
    }

    /**
     * @param nomorJastel       <parameter name="nomorJastel" class="java.lang.String"/>
     * @param jenisJasa         <parameter name="jenisJasa" class="java.lang.String"/>
     * @param kodeDivre         <parameter name="kodeDivre" class="java.lang.String"/>
     * @param kodeDatel         <parameter name="kodeDatel" class="java.lang.String"/>
     * @param namaPelanggan     <parameter name="namaPelanggan" class="java.lang.String"/>
     * @param npwp              <parameter name="npwp" class="java.lang.String"/>
     * @param bulanBerjalan     <parameter name="bulanBerjalan" class="java.math.BigDecimal"/>
     * @param tunggakanSebulan  <parameter name="tunggakanSebulan" class="java.math.BigDecimal"/>
     * @param tunggakanDuaBulan <parameter name="tunggakanDuaBulan" class="java.math.BigDecimal"/>
     * @param biayaAdmin        <parameter name="biayaAdminBank" class="java.math.BigDecimal"/>
     * @param nomorRef          <parameter name="nomorRef" class="java.lang.String"/>
     * @param pembayaran        <parameter name="pembayaran" class="java.lang.String"/>
     * @param kodeCabangLokasi  <parameter name="kodeCabangLokasi" class="java.lang.String"/>
     * @param rekeningBeban     <parameter name="rekeningBeban" class="java.lang.String"/>
     * @param nomorArsip        <parameter name="nomorArsip" class="java.lang.String"/>
     * @param tanggalTransaksi  <parameter name="tanggalTransaksi" class="java.sql.Date"/>
     * @param kodeRef1          <parameter name="kodeRef1"class="java.lang.String"/>
     * @param kodeRef2          <parameter name="kodeRef2"class="java.lang.String"/>
     * @param kodeRef3          <parameter name="kodeRef3"class="java.lang.String"/>
     * @param jumlahBill        <parameter name="jumlahBill"class="java.lang.String"/>
     * @param nomorResi
     * @return http://localhost:9090/downloads/pdf/finnet/struk/pembayaran-ptsn?nomorJastel=&jenisJasa=&kodeDivre=&kodeDatel=&namaPelanggan=&npwp=&bulanBerjalan=&tunggakanSebulan=&tunggakanDuaBulan=&biayaAdmin=&nomorRef=&pembayaran=&kodeCabangLokasi=&rekeningBeban=&nomorArsip=&tanggalTransaksi=&kodeRef1=&kodeRef2=&kodeRef3=&jumlahBill=&nomorResi=
     */

    @GetMapping(value = "/struk/pembayaran-ptsn")
    public ModelAndView strukPembayaranPtsn(
            ModelAndView mav,
            @RequestParam("nomorJastel") String nomorJastel,
            @RequestParam("jenisJasa") String jenisJasa,
            @RequestParam("kodeDivre") String kodeDivre,
            @RequestParam("kodeDatel") String kodeDatel,
            @RequestParam("namaPelanggan") String namaPelanggan,
            @RequestParam("npwp") String npwp,
            @RequestParam(name = "bulanBerjalan", defaultValue = "0") BigDecimal bulanBerjalan,
            @RequestParam(name = "tunggakanSebulan", defaultValue = "0") BigDecimal tunggakanSebulan,
            @RequestParam(name = "tunggakanDuaBulan", defaultValue = "0") BigDecimal tunggakanDuaBulan,
            @RequestParam(name = "biayaAdmin", defaultValue = "0") BigDecimal biayaAdmin,
            @RequestParam("nomorRef") String nomorRef,
            @RequestParam("pembayaran") String pembayaran,
            @RequestParam("kodeCabangLokasi") String kodeCabangLokasi,
            @RequestParam("rekeningBeban") String rekeningBeban,
            @RequestParam("nomorArsip") String nomorArsip,
            @RequestParam("tanggalTransaksi") String tanggalTransaksi,
            @RequestParam("kodeRef1") String kodeRef1,
            @RequestParam("kodeRef2") String kodeRef2,
            @RequestParam("kodeRef3") String kodeRef3,
            @RequestParam("jumlahBill") String jumlahBill,
            @RequestParam("nomorResi") String nomorResi,
            @RequestParam(name = "footer", defaultValue = "") String footer,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "tellerCu", defaultValue = "", required = false) String tellerCetakUlang
    ) {
        mav.addObject("format", "pdf");
        mav.addObject("datasource", new JREmptyDataSource());
        mav.setViewName("/finnet/report_pembayaran_pstn");
        mav.addObject("nomorJastel", nomorJastel);
        mav.addObject("jenisJasa", jenisJasa);
        mav.addObject("kodeDivre", kodeDivre);
        mav.addObject("kodeDatel", kodeDatel);
        mav.addObject("namaPelanggan", namaPelanggan);
        mav.addObject("npwp", npwp);
        mav.addObject("bulanBerjalan", bulanBerjalan);
        mav.addObject("tunggakanSebulan", tunggakanSebulan);
        mav.addObject("tunggakanDuaBulan", tunggakanDuaBulan);
        mav.addObject("biayaAdminBank", biayaAdmin);
        mav.addObject("terbilang", new KonversiMataUangKeTerbilang(
                bulanBerjalan.add(tunggakanSebulan).add(tunggakanDuaBulan).add(biayaAdmin)).toString()
        );
        mav.addObject("nomorRef", nomorRef);
        mav.addObject("pembayaran", pembayaran);
        mav.addObject("kodeCabangLokasi", kodeCabangLokasi);
        mav.addObject("rekeningBeban", rekeningBeban);
        mav.addObject("nomorArsip", nomorArsip);
        mav.addObject("tanggalTransaksi", new Date());
        mav.addObject("kodeRef1", kodeRef1);
        mav.addObject("kodeRef2", kodeRef2);
        mav.addObject("kodeRef3", kodeRef3);
        mav.addObject("jumlahBill", jumlahBill);
        mav.addObject("nomorResi", nomorResi);
        mav.addObject("footer", footer);
        mav.addObject("cu", cetakUlang);
        mav.addObject("tellerCu", tellerCetakUlang);
        return mav;
    }

}
