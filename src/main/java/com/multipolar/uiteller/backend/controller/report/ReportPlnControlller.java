package com.multipolar.uiteller.backend.controller.report;

import com.multipolar.uiteller.backend.model.report.pln.ReportPendapatanPlnNonTagihanListrik;
import com.multipolar.uiteller.backend.model.report.pln.ReportPendapatanPlnPostpaid;
import com.multipolar.uiteller.backend.model.report.pln.ReportPendapatanPlnPrepaid;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.report.ReportPlnService;
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
import java.util.Date;
import java.util.List;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@Controller
@RequestMapping("/downloads/pdf/pln")
public class ReportPlnControlller {

    @Autowired
    private ReportPlnService serviceReport;
    @Autowired
    private ReportService reportService;

    /**
     * @param startDate
     * @param endDate
     * @return http://localhost:9090/downloads/pdf/pln/laporan/pendapatan-postpaid?startDate=&endDate=
     */
    @GetMapping("/laporan/pendapatan-postpaid")
    public ModelAndView laporanPendapatanPlnPostpaid(
            ModelAndView mav,
            HttpServletRequest request,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("sts") String sts,
            @RequestParam("user") String user,
            @RequestParam("branch") String branch,
            @RequestParam("teller") Boolean teller,
            @RequestParam("supervisi") Boolean supervisi,
            @RequestParam("admin") Boolean admin,
            @RequestParam(value = "type", defaultValue = "pdf", required = false) String s) {
        mav.setViewName("/pln/report_pendapatan_bank_sumut_pln_postpaid");
        mav.addObject("format", s);
        mav.addObject("periode", new StringBuilder(reportService.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).append(" s/d ").append(reportService.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).toString());
        String reportDetailLocation = new StringBuilder(request.getServletContext()
                .getContextPath())
                .append("jasper/pln/")
                .append("report_pendapatan_pln_postpaid_details.jasper")
                .toString();
        String reportCellLocation = new StringBuilder(request.getServletContext().getContextPath())
                .append("jasper/pln/")
                .append("report_pendapatan_pln_postpaid_cell.jasper")
                .toString();

        List<ReportPendapatanPlnPostpaid> list = this.serviceReport.postpaidList(startDate, endDate, reportDetailLocation, reportCellLocation, sts, user, branch, teller, supervisi, admin);
        mav.addObject("datasource", list);
        return mav;
    }

    /**
     * @param startDate
     * @param endDate
     * @return http://localhost:9090/downloads/pdf/pln/laporan/pendapatan-nontaglist?startDate=&endDate=
     */
    @GetMapping("/laporan/pendapatan-nontaglist")
    public ModelAndView laporanPendapatanPlnNonTagihanListrik(
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
        mav.setViewName("/pln/report_pendapatan_bank_sumut_pln");
        mav.addObject("format", s);
        mav.addObject("periode", new StringBuilder(reportService.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).append(" s/d ").append(reportService.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).toString());
        String reportDetailLocation = new StringBuilder(request.getServletContext()
                .getContextPath())
                .append("jasper/pln/")
                .append("report_pendapatan_pln_non_taglist_details.jasper")
                .toString();
        String reportCellLocation = new StringBuilder(request.getServletContext()
                .getContextPath())
                .append("jasper/pln/")
                .append("report_pendapatan_pln_non_taglist_cells.jasper")
                .toString();

        List<ReportPendapatanPlnNonTagihanListrik> list = serviceReport.nonTaglistList(startDate, endDate, reportDetailLocation, reportCellLocation, sts, user, branch, teller, supervisi, admin);
        mav.addObject("datasource", new JRBeanCollectionDataSource(list));
        return mav;
    }

    /**
     * @param startDate
     * @param endDate
     * @return http://localhost:9090/downloads/pdf/pln/laporan/pendapatan-prepaid?startDate=&endDate=
     */
    @GetMapping("/laporan/pendapatan-prepaid")
    public ModelAndView laporanPendapatanPlnPrepaid(
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
                .append("jasper/pln/")
                .append("report_pendapatan_pln_prepaid_details.jasper")
                .toString();
        String jasperCellLocation = new StringBuilder(request.getServletContext().getContextPath())
                .append("jasper/pln/")
                .append("report_pendapatan_pln_prepaid_cells.jasper")
                .toString();
        mav.setViewName("/pln/report_pendapatan_bank_sumut_pln");
        mav.addObject("format", s);
        mav.addObject("periode", new StringBuilder(reportService.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).append(" s/d ").append(reportService.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).toString());

        List<ReportPendapatanPlnPrepaid> list = serviceReport.prepaidList(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);
        mav.addObject("datasource", new JRBeanCollectionDataSource(list));
        return mav;
    }

    /**
     * <parameter name="transaksiId" class="java.lang.String"/>
     * <parameter name="nomorRegistrasi" class="java.lang.String"/>
     * <parameter name="tanggalRegistrasi" class="java.util.Date"/>
     * <parameter name="nama" class="java.lang.String"/>
     * <parameter name="kodePelanggan" class="java.lang.String"/>
     * <parameter name="biayaPln" class="java.math.BigDecimal"/>
     * <parameter name="noRef" class="java.lang.String"/>
     * <parameter name="biayaAdminBank" class="java.math.BigDecimal"/>
     * <parameter name="footer" class="java.lang.String"/>
     * <parameter name="rekeningBeban" class="java.lang.String"/>
     * <parameter name="tanggal" class="java.util.Date"/>
     * return http://localhost:9090/downloads/pdf/pln/struk/pembayaran-non-tagihan?kodeTransaksi=&nomorRegistrasi=&tanggalTransaksi=&nama=&kodePelanggan=&biayaPln=&nomorRef=&biayaAdmin=&footer=&tanggal=&rekeningBeban=
     */
    @GetMapping("/struk/pembayaran-non-tagihan")
    public ModelAndView strukNonTagihanListrik(
            ModelAndView mav,
            @RequestParam("kodeTransaksi") String kodeTransaksi,
            @RequestParam("nomorRegistrasi") String nomorRegistrasi,
            @RequestParam("tanggalTransaksi") String tanggalTransaksi,
            @RequestParam("tanggalRegistrasi") String tanggalRegistrasi,
            @RequestParam("jenisTransaksi") String jenisTransaksi,
            @RequestParam("nama") String nama,
            @RequestParam("kodePelanggan") String kodePelanggan,
            @RequestParam("nomorRef") String nomorRef,
            @RequestParam("tanggal") String tanggal,
            @RequestParam("rekeningBeban") String rekeningBeban,
            @RequestParam(name = "biayaPln", defaultValue = "0") BigDecimal biayaPln,
            @RequestParam(name = "biayaAdmin", defaultValue = "0") BigDecimal biayaAdmin,
            @RequestParam(name = "footer", defaultValue = "PLN DENGAN INI MENYATANKAN STRUK INI SEBAGAI BUKTI YANG SAH") String footer,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "tellerCu", defaultValue = "", required = false) String tellerCetakUlang
    ) {
        mav.setViewName("/pln/report_pembayaran_non_tagihan_listrik");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');  // Set dot as grouping separator
        symbols.setDecimalSeparator('.');   // Set dot as decimal separator
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        mav.addObject("format", "pdf");
        mav.addObject("datasource", new JREmptyDataSource());
        mav.addObject("transaksiId", "MIGRASI PRABAYAR");
        mav.addObject("nomorRegistrasi", nomorRegistrasi);
        mav.addObject("tanggalRegistrasi", tanggalRegistrasi);
        mav.addObject("jenisTransaksi", jenisTransaksi);
        mav.addObject("nama", nama);
        if(kodePelanggan.equals("000000000000")){kodePelanggan="";}
        mav.addObject("kodePelanggan", kodePelanggan);
        mav.addObject("biayaPln", df.format(biayaPln));
        mav.addObject("noRef", nomorRef);
        mav.addObject("biayaAdminBank", df.format(biayaAdmin));
        mav.addObject("totalBayar", df.format(biayaAdmin.add(biayaPln)));
        mav.addObject("footer", footer);
        mav.addObject("tanggal", new Date());
        mav.addObject("rekeningBeban", rekeningBeban);
        mav.addObject("cu", cetakUlang);
        mav.addObject("tellerCu", tellerCetakUlang);
        return mav;
    }

    /**
     * @param kodePelanggan <parameter name="idPelanggan" class="java.lang.String"/>
     * @param namaPelanggan <parameter name="nama" class="java.lang.String"/>
     * @param tarifDaya     <parameter name="tarifDaya" class="java.lang.String"/>
     * @param biayaPln      <parameter name="biayaPln" class="java.math.BigDecimal"/>
     * @param nomorRef      <parameter name="noRef" class="java.lang.String"/>
     * @param bulanTahun    <parameter name="bulanTahun" class="java.lang.String"/>
     * @param stanMeter     <parameter name="stanMeter" class="java.lang.String"/>
     * @param sisaTunggakan <parameter name="sisaTunggakan" class="java.lang.String"/>
     * @param biayaAdmin    <parameter name="biayaAdminBank" class="java.math.BigDecimal"/>
     * @param rekeningBeban <parameter name="rekeningBeban" class="java.lang.String"/>
     * @param tanggal       <parameter name="tanggal" class="java.util.Date"/>
     * @return http://localhost:9090/downloads/pdf/pln/struk/pembayaran-tagihan?kodePelanggan=&namaPelanggan=&tarifDaya=&biayaPln=&nomorRef=&bulanTahun=&stanMeter=&biayaAdmin=&sisaTunggakan=&footer=&tanggal=&rekeningBeban=
     */
    @GetMapping("/struk/pembayaran-tagihan")
    public ModelAndView strukPembayaranTagihanListrik(
            ModelAndView mav,
            @RequestParam("kodePelanggan") String kodePelanggan,
            @RequestParam("namaPelanggan") String namaPelanggan,
            @RequestParam("tarifDaya") String tarifDaya,
            @RequestParam("nomorRef") String nomorRef,
            @RequestParam("bulanTahun") String bulanTahun,
            @RequestParam("stanMeter") String stanMeter,
            @RequestParam("tanggal") String tanggal,
            @RequestParam("rekeningBeban") String rekeningBeban,
            @RequestParam(name = "biayaPln", defaultValue = "0") BigDecimal biayaPln,
            @RequestParam(name = "biayaAdmin", defaultValue = "0") BigDecimal biayaAdmin,
            @RequestParam("sisaTunggakan") String sisaTunggakan,
            @RequestParam(name = "footer", defaultValue = "PLN DENGAN INI MENYATAKAN STRUK INI SEBAGAI BUKTI YANG SAH") String footer,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "tellerCu", defaultValue = "", required = false) String tellerCetakUlang
    ) {
        mav.setViewName("/pln/report_pembayaran_tagihan_listrik");
        mav.addObject("format", "pdf");
        mav.addObject("datasource", new JREmptyDataSource());
        mav.addObject("idPelanggan", kodePelanggan);
        mav.addObject("nama", namaPelanggan);
        mav.addObject("tarifDaya", tarifDaya);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');  // Set dot as grouping separator
        symbols.setDecimalSeparator('.');   // Set dot as decimal separator
        // DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        BigDecimal totalBayar = biayaPln.add(biayaAdmin);
        mav.addObject("biayaPln", df.format(biayaPln));
        mav.addObject("noRef", nomorRef);
        mav.addObject("bulanTahun", bulanTahun);
        mav.addObject("stanMeter", stanMeter);
        mav.addObject("sisaTunggakan", sisaTunggakan);
        mav.addObject("biayaAdminBank", df.format(biayaAdmin));
        mav.addObject("footer", footer);
        mav.addObject("tanggal", new Date());
        mav.addObject("rekeningBeban", rekeningBeban);
        mav.addObject("cu", cetakUlang);
        mav.addObject("tellerCu", tellerCetakUlang);
        mav.addObject("totalBayar", df.format(totalBayar));
        return mav;
    }

    /**
     * @param nomorMeter    <parameter name="nomorMeter" class="java.lang.String"/>
     * @param kodePelanggan <parameter name="kodePelanggan" class="java.lang.String"/>
     * @param idPelanggan   <parameter name="idPelanggan" class="java.lang.String"/>
     * @param namaPelanggan <parameter name="namaPelanggan" class="java.lang.String"/>
     * @param tarifDaya     <parameter name="tarifDaya" class="java.lang.String"/>
     * @param nomorRef      <parameter name="nomorRef" class="java.lang.String"/>
     * @param rupiahBayar   <parameter name="rupiahBayar" class="java.lang.String"/>
     * @param ppj           <parameter name="ppj" class="java.lang.String"/>
     * @param ppn           <parameter name="ppn" class="java.lang.String"/>
     * @param materai       <parameter name="materai" class="java.lang.String"/>
     * @param jumlahKwh     <parameter name="jmlKwh" class="java.lang.String"/>
     * @param tanggal       <parameter name="tanggal" class="java.util.Date"/>
     * @param token         <parameter name="token" class="java.lang.String"/>
     * @param angsuran      <parameter name="angsuran" class="java.lang.String"/>
     * @param rupiahToken   <parameter name="rupiahToken" class="java.lang.String"/>
     * @param adminBank     <parameter name="adminBank" class="java.lang.String"/>
     * @param rekeningBeban <parameter name="rekeningBeban" class="java.lang.String"/>
     * @param footer        <parameter name="footer" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/pln/struk/pembelian-prabayar?nomorMeter=&kodePelanggan=&namaPelanggan=&tarifDaya=&nomorRef=&rupiahBayar=&ppj=&ppn=&materai=&jumlahKwh=&tanggal=&token=&angsuran=&rupiahToken=&adminBank=&rekeningBeban=&footer=
     */
    @GetMapping("/struk/pembelian-prabayar")
    public ModelAndView sturkPembelianListrikPrabayar(
            ModelAndView mav,
            @RequestParam("nomorMeter") String nomorMeter,
            @RequestParam("kodePelanggan") String kodePelanggan,
            @RequestParam("idPelanggan") String idPelanggan,
            @RequestParam("namaPelanggan") String namaPelanggan,
            @RequestParam("tarifDaya") String tarifDaya,
            @RequestParam("nomorRef") String nomorRef,
            @RequestParam("tanggal") String tanggal,
            @RequestParam("rekeningBeban") String rekeningBeban,
            @RequestParam("token") String token,
            @RequestParam(name = "rupiahBayar", defaultValue = "0") BigDecimal rupiahBayar,
            @RequestParam(name = "ppj", defaultValue = "0") String ppj,
            @RequestParam(name = "ppn", defaultValue = "0") String ppn,
            @RequestParam(name = "materai", defaultValue = "0") String materai,
            @RequestParam(name = "jumlahKwh", defaultValue = "0") String jumlahKwh,
            @RequestParam(name = "angsuran", defaultValue = "0") String angsuran,
            @RequestParam(name = "rupiahToken", defaultValue = "0") String rupiahToken,
            @RequestParam(name = "adminBank", defaultValue = "0") BigDecimal adminBank,
            @RequestParam("footer") String footer,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "tellerCu", defaultValue = "", required = false) String tellerCetakUlang
    ) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');  // Set dot as grouping separator
        symbols.setDecimalSeparator(',');   // Set dot as decimal separator
        // DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        DecimalFormat df1 = new DecimalFormat("#,##0.00", symbols);
        DecimalFormat df2 = new DecimalFormat("#,##0.0", symbols);
        mav.addObject("format", "pdf");
        mav.addObject("datasource", new JREmptyDataSource());
        mav.setViewName("/pln/report_pembelian_listrik_prabayar");

        mav.addObject("nomorMeter", nomorMeter);
        mav.addObject("idPelanggan", idPelanggan);
        mav.addObject("namaPelanggan", namaPelanggan);
        mav.addObject("tarifDaya", removeLeaddingZero(tarifDaya));
        mav.addObject("nomorRef", nomorRef);
        mav.addObject("rupiahBayar", df.format(rupiahBayar));
        mav.addObject("ppj",ppj);
        mav.addObject("ppn", ppn);
        mav.addObject("materai",materai);
        if(jumlahKwh.length()>0){jumlahKwh.substring(0,jumlahKwh.length()-1);}
        mav.addObject("jmlKwh", jumlahKwh);
        mav.addObject("tanggal", new Date());
        mav.addObject("token", token);
        mav.addObject("angsuran", angsuran);
        mav.addObject("rupiahToken", rupiahToken);
        mav.addObject("adminBank", df.format(adminBank));
        mav.addObject("rekeningBeban", rekeningBeban);
        mav.addObject("footer", footer);
        mav.addObject("cu", cetakUlang);
        mav.addObject("tellerCu", tellerCetakUlang);
        return mav;
    }

    public String removeLeaddingZero(String data){
        String[] parts = data.split("/");
        if (parts.length==2){
            parts[1].replaceFirst("^0+(?!$)", "");
            return parts[0]+"/"+parts[1];
        }else {
            return data;
        }
    }
}
