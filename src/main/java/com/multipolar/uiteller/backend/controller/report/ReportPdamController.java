package com.multipolar.uiteller.backend.controller.report;

import com.multipolar.uiteller.backend.model.InstansiPdam;
import com.multipolar.uiteller.backend.model.report.pdam.ReportPendapatanPdam;
import com.multipolar.uiteller.backend.repository.InstansiPdamRepository;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.service.KonversiMataUangKeTerbilang;
import com.multipolar.uiteller.backend.service.report.ReportPdamService;
import com.multipolar.uiteller.backend.service.report.ReportService;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/downloads/pdf/pdam")
public class ReportPdamController {

    @Autowired
    private ReportPdamService reportService;
    @Autowired
    private ReportService service;
    @Autowired
    private InstansiPdamRepository instansiPdamRepository;

    /**
     * @param startDate
     * @param endDate
     * @param type      conditional 'TIRTANADI' | 'TIRTABULIAN' | 'TIRTAULI' | 'TIRTANADI_INSTALLATION' | 'TIRTAKUALO'
     * @return linknya dibawah
     * <p>
     * http://localhost:9090/downloads/pdf/pdam/laporan/pendapatan-pdam/TIRTABULIAN?startDate=&endDate=
     * http://localhost:9090/downloads/pdf/pdam/laporan/pendapatan-pdam/TIRTANADI?startDate=&endDate=
     * http://localhost:9090/downloads/pdf/pdam/laporan/pendapatan-pdam/TIRTAULI?startDate=&endDate=
     * http://localhost:9090/downloads/pdf/pdam/laporan/pendapatan-pdam/TIRTANADI_INSTALLATION?startDate=&endDate=
     * http://localhost:9090/downloads/pdf/pdam/laporan/pendapatan-pdam/TIRTAKUALO?startDate=&endDate=
     * </p>
     */
    @GetMapping("/laporan/pendapatan-pdam/{transactionType}")
    public ModelAndView laporanPendapantanBankPdam(
            ModelAndView mav,
            HttpServletRequest request,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @PathVariable("transactionType") String type,
            @RequestParam("sts") String sts,
            @RequestParam("user") String user,
            @RequestParam("branch") String branch,
            @RequestParam("teller") Boolean teller,
            @RequestParam("supervisi") Boolean supervisi,
            @RequestParam("admin") Boolean admin,
            @RequestParam(value = "type", defaultValue = "pdf", required = false) String s) {
        String jasperDetailLocation = new StringBuilder(request.getServletContext().getContextPath())
                .append("jasper/pdam/")
                .append("report_pendapatan_pdam_details.jasper")
                .toString();
        String jasperCellLocation = new StringBuilder(request.getServletContext().getContextPath())
                .append("jasper/pdam/")
                .append("report_pendapatan_pdam_cells.jasper")
                .toString();
        mav.setViewName("/pdam/report_pendapatan_pdam");
        mav.addObject("format", s);
        mav.addObject("periode", new StringBuilder(service.format(startDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).append(" s/d ").append(service.format(endDate, Constants.MODEL_DATE_TIME, Constants.MODEL_REPORT_DATE_TIME)).toString());

        List<ReportPendapatanPdam> list = null;
        if (type.equalsIgnoreCase("TIRTABULIAN")) {
            list = this.reportService.listTirtabulian(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);
        } else if (type.equalsIgnoreCase("TIRTAULI")) {
            list = this.reportService.listTirtauli(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);
        } else if (type.equalsIgnoreCase("TIRTANADI")) {
            list = this.reportService.listTirtanadi(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);
        } else if (type.equalsIgnoreCase("TIRTAAYUMI_DTI")) {
            list = this.reportService.listTirtaayumiDti(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);
        } else if (type.equalsIgnoreCase("TIRTAKUALO_DTI")) {
            list = this.reportService.listTirtakualoDti(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);
        } else if (type.equalsIgnoreCase("TIRTATANJUNG_DTI")) {
            list = this.reportService.listTirtatanjungDti(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);
        } else if (type.equalsIgnoreCase("TIRTANADI_INSTALLATION")) {
            list = this.reportService.listTirtanadiInstallation(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);
        } else if (type.equalsIgnoreCase("TIRTAAYUMI_DTI_INSTALLATION")) {
            list = this.reportService.listTirtaayumiDtiInstallation(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);
        } else if (type.equalsIgnoreCase("TIRTAKUALO_DTI_INSTALLATION")) {
            list = this.reportService.listTirtakualoDtiInstallation(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);
        } else if (type.equalsIgnoreCase("TIRTATANJUNG_DTI_INSTALLATION")) {
            list = this.reportService.listTirtatanjungDtiInstallation(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);
        } else if (type.equalsIgnoreCase("TIRTAKUALO")) {
            list = this.reportService.listTirtakualo(startDate, endDate, jasperDetailLocation, jasperCellLocation, sts, user, branch, teller, supervisi, admin);
        } else {
            list = new ArrayList<>();
        }
        mav.addObject("datasource", new JRBeanCollectionDataSource(list));
        return mav;
    }

    /**
     * @param stanMeter        <parameter name="stanMeter" class="java.lang.String"/>
     * @param lokasiPembayaran <parameter name="lokasiPembayaran" class="java.lang.String"/>
     * @param kodeTeller       <parameter name="kodeTeller" class="java.lang.String"/>
     * @param nomorArsip       <parameter name="nomorArsip" class="java.lang.String"/>
     * @param nomorPelanggan   <parameter name="nomorPelanggan" class="java.lang.String"/>
     * @param nama             <parameter name="nama" class="java.lang.String"/>
     * @param alamat           <parameter name="alamat" class="java.lang.String"/>
     * @param golTarif         <parameter name="golTarif" class="java.lang.String"/>
     * @param standMeter       <parameter name="standMeter" class="java.lang.String"/>
     * @param tanggal          <parameter name="tanggal" class="java.util.Date"/>
     * @param periode          <parameter name="periode" class="java.lang.String"/>
     * @param pemakaian        <parameter name="pemakaian" class="java.lang.String"/>
     * @param jumlah           <parameter name="jumlahTanggunganAir" class="java.math.BigDecimal"/>
     * @param denda            <parameter name="denda" class="java.math.BigDecimal"/>
     * @param adminBank        <parameter name="adminBank" class="java.math.BigDecimal"/>
     * @return http://localhost:9090/downloads/pdf/pdam/struk/tirtanadi/tagihan?stanMeter=&lokasiPembayaran=&kodeTeller=&nomorArsip=&nomorPelanggan=&nama=&alamat=&golTarif=&standMeter=&tanggal=&periode=&pemakaian=&jumlah=&denda=&adminBank=&footer=
     */
    @GetMapping(value = "/struk/tirtanadi/tagihan")
    public ModelAndView strukPDAMTirtanadiTagihan(
            ModelAndView mav,
            @RequestParam("stanMeter") String stanMeter,
            @RequestParam("lokasiPembayaran") String lokasiPembayaran,
            @RequestParam("kodeTeller") String kodeTeller,
            @RequestParam("nomorArsip") String nomorArsip,
            @RequestParam("nomorPelanggan") String nomorPelanggan,
            @RequestParam("nama") String nama,
            @RequestParam("alamat") String alamat,
            @RequestParam("golTarif") String golTarif,
            @RequestParam("standMeter") String standMeter,
            @RequestParam("tanggal") String tanggal,
            @RequestParam("periode") String periode,
            @RequestParam(name = "pemakaian", defaultValue = "0") String pemakaian,
            @RequestParam(name = "jumlahTanggunganAir", defaultValue = "0") BigDecimal jumlah,
            @RequestParam(name = "denda", defaultValue = "0") BigDecimal denda,
            @RequestParam(name = "adminBank", defaultValue = "0") BigDecimal adminBank,
            @RequestParam(name = "referenceNumber", defaultValue = "") String referenceNumber,
            @RequestParam(name = "footer", defaultValue = "PDAM MENYATAKAN STRUK INI SEBAGAI BUKTI PEMBAYARAN YANG SAH") String footer,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "tellerCu", defaultValue = "", required = false) String tellerCetakUlang
    ) {
        mav.addObject("format", "pdf");
        mav.addObject("datasource", new JREmptyDataSource());
        mav.setViewName("/pdam/report_pdam_tirtanadi");
        mav.addObject("stanMeter", stanMeter);
        mav.addObject("lokasiPembayaran", lokasiPembayaran);
        mav.addObject("kodeTeller", kodeTeller);
        mav.addObject("nomorArsip", nomorArsip);
        mav.addObject("nomorPelanggan", nomorPelanggan);
        mav.addObject("nama", nama);
        mav.addObject("alamat", alamat);
        mav.addObject("golTarif", golTarif);
        mav.addObject("standMeter", standMeter);
        mav.addObject("pemakaian", pemakaian);
        BigDecimal total = jumlah.add(denda).add(adminBank);
        mav.addObject("terbilang", new KonversiMataUangKeTerbilang(total.setScale(0, BigDecimal.ROUND_CEILING)).toString());
        mav.addObject("tanggal", tanggal);
        mav.addObject("periode", periode);
        mav.addObject("jumlahTanggunganAir", jumlah);
        mav.addObject("denda", denda);
        mav.addObject("adminBank", adminBank);
        mav.addObject("referenceNumber", referenceNumber);
        mav.addObject("footer", footer);
        mav.addObject("cetakUlang", cetakUlang);
        mav.addObject("tellerCu", tellerCetakUlang);
        return mav;
    }
    
    /**
     * @param stanMeter        <parameter name="stanMeter" class="java.lang.String"/>
     * @param lokasiPembayaran <parameter name="lokasiPembayaran" class="java.lang.String"/>
     * @param kodeTeller       <parameter name="kodeTeller" class="java.lang.String"/>
     * @param nomorArsip       <parameter name="nomorArsip" class="java.lang.String"/>
     * @param nomorPelanggan   <parameter name="nomorPelanggan" class="java.lang.String"/>
     * @param nama             <parameter name="nama" class="java.lang.String"/>
     * @param alamat           <parameter name="alamat" class="java.lang.String"/>
     * @param golTarif         <parameter name="golTarif" class="java.lang.String"/>
     * @param standMeter       <parameter name="standMeter" class="java.lang.String"/>
     * @param tanggal          <parameter name="tanggal" class="java.util.Date"/>
     * @param periode          <parameter name="periode" class="java.lang.String"/>
     * @param pemakaian        <parameter name="pemakaian" class="java.lang.String"/>
     * @param jumlah           <parameter name="jumlahTanggunganAir" class="java.math.BigDecimal"/>
     * @param denda            <parameter name="denda" class="java.math.BigDecimal"/>
     * @param adminBank        <parameter name="adminBank" class="java.math.BigDecimal"/>
     * @return http://localhost:9090/downloads/pdf/pdam/struk/tirtaayumi-dti/tagihan?stanMeter=&lokasiPembayaran=&kodeTeller=&nomorArsip=&nomorPelanggan=&nama=&alamat=&golTarif=&standMeter=&tanggal=&periode=&pemakaian=&jumlah=&denda=&adminBank=&footer=
     */
    @GetMapping(value = "/struk/tirtaayumi-dti/tagihan")
    public ModelAndView strukPDAMTirtayumiDtiTagihan(
            ModelAndView mav,
            @RequestParam("stanMeter") String stanMeter,
            @RequestParam("lokasiPembayaran") String lokasiPembayaran,
            @RequestParam("kodeTeller") String kodeTeller,
            @RequestParam("nomorArsip") String nomorArsip,
            @RequestParam("nomorPelanggan") String nomorPelanggan,
            @RequestParam("nama") String nama,
            @RequestParam("alamat") String alamat,
            @RequestParam("golTarif") String golTarif,
            @RequestParam("standMeter") String standMeter,
            @RequestParam("tanggal") String tanggal,
            @RequestParam("periode") String periode,
            @RequestParam(name = "pemakaian", defaultValue = "0") String pemakaian,
            @RequestParam(name = "jumlahTanggunganAir", defaultValue = "0") BigDecimal jumlah,
            @RequestParam(name = "denda", defaultValue = "0") BigDecimal denda,
            @RequestParam(name = "adminBank", defaultValue = "0") BigDecimal adminBank,
            @RequestParam(name = "referenceNumber", defaultValue = "") String referenceNumber,
            @RequestParam(name = "kodeInstansi", defaultValue = "") String kodeInstansi,
            @RequestParam(name = "footer", defaultValue = "PDAM MENYATAKAN STRUK INI SEBAGAI BUKTI PEMBAYARAN YANG SAH") String footer,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "tellerCu", defaultValue = "", required = false) String tellerCetakUlang
    ) {
        if (footer == null || footer.isEmpty() || "null".equalsIgnoreCase(footer)) {
            footer = "PDAM MENYATAKAN STRUK INI SEBAGAI BUKTI PEMBAYARAN YANG SAH";
        }
        mav.addObject("format", "pdf");
        mav.addObject("datasource", new JREmptyDataSource());
        mav.setViewName("/pdam/report_pdam_tirtaayumi_dti");
        mav.addObject("stanMeter", stanMeter);
        mav.addObject("lokasiPembayaran", lokasiPembayaran);
        mav.addObject("kodeTeller", kodeTeller);
        mav.addObject("nomorArsip", nomorArsip);
        mav.addObject("nomorPelanggan", nomorPelanggan);
        mav.addObject("nama", nama);
        mav.addObject("alamat", alamat);
        mav.addObject("golTarif", golTarif);
        mav.addObject("standMeter", standMeter);
        mav.addObject("pemakaian", pemakaian);
        BigDecimal total = jumlah.add(denda).add(adminBank);
        mav.addObject("terbilang", new KonversiMataUangKeTerbilang(total.setScale(0, BigDecimal.ROUND_CEILING)).toString());
        mav.addObject("tanggal", tanggal);
        mav.addObject("periode", periode);
        mav.addObject("jumlahTanggunganAir", jumlah);
        mav.addObject("denda", denda);
        mav.addObject("adminBank", adminBank);
        mav.addObject("referenceNumber", referenceNumber);
        mav.addObject("footer", footer);
        mav.addObject("cetakUlang", cetakUlang);
        mav.addObject("tellerCu", tellerCetakUlang);

        // ambil nama instansi pdam berdasarkan kode nya
        String reportTitle = "PEMBAYARAN PDAM DTI SUMUT";
        if (StringUtils.isNotEmpty(kodeInstansi)) {
            InstansiPdam instansiPdam = instansiPdamRepository.findByKodeInstansi(kodeInstansi);
            if (instansiPdam != null) {
                if (instansiPdam.getKodeInstansi().equalsIgnoreCase("07009")){
                    reportTitle = "PEMBAYARAN PERUMDA " + StringUtils.upperCase(instansiPdam.getNamaInstansi());
                } else {
                    reportTitle = "PEMBAYARAN PDAM " + StringUtils.upperCase(instansiPdam.getNamaInstansi());
                }
            }
        }
        mav.addObject("titlePdam", reportTitle);

        return mav;
    }
    
    /**
     * @param stanMeter        <parameter name="stanMeter" class="java.lang.String"/>
     * @param lokasiPembayaran <parameter name="lokasiPembayaran" class="java.lang.String"/>
     * @param kodeTeller       <parameter name="kodeTeller" class="java.lang.String"/>
     * @param nomorArsip       <parameter name="nomorArsip" class="java.lang.String"/>
     * @param nomorPelanggan   <parameter name="nomorPelanggan" class="java.lang.String"/>
     * @param nama             <parameter name="nama" class="java.lang.String"/>
     * @param alamat           <parameter name="alamat" class="java.lang.String"/>
     * @param golTarif         <parameter name="golTarif" class="java.lang.String"/>
     * @param standMeter       <parameter name="standMeter" class="java.lang.String"/>
     * @param tanggal          <parameter name="tanggal" class="java.util.Date"/>
     * @param periode          <parameter name="periode" class="java.lang.String"/>
     * @param pemakaian        <parameter name="pemakaian" class="java.lang.String"/>
     * @param jumlah           <parameter name="jumlahTanggunganAir" class="java.math.BigDecimal"/>
     * @param denda            <parameter name="denda" class="java.math.BigDecimal"/>
     * @param adminBank        <parameter name="adminBank" class="java.math.BigDecimal"/>
     * @return http://localhost:9090/downloads/pdf/pdam/struk/tirtakualo-dti/tagihan?stanMeter=&lokasiPembayaran=&kodeTeller=&nomorArsip=&nomorPelanggan=&nama=&alamat=&golTarif=&standMeter=&tanggal=&periode=&pemakaian=&jumlah=&denda=&adminBank=&footer=
     */
    @GetMapping(value = "/struk/tirtakualo-dti/tagihan")
    public ModelAndView strukPDAMTirtakualoDtiTagihan(
            ModelAndView mav,
            @RequestParam("stanMeter") String stanMeter,
            @RequestParam("lokasiPembayaran") String lokasiPembayaran,
            @RequestParam("kodeTeller") String kodeTeller,
            @RequestParam("nomorArsip") String nomorArsip,
            @RequestParam("nomorPelanggan") String nomorPelanggan,
            @RequestParam("nama") String nama,
            @RequestParam("alamat") String alamat,
            @RequestParam("golTarif") String golTarif,
            @RequestParam("standMeter") String standMeter,
            @RequestParam("tanggal") String tanggal,
            @RequestParam("periode") String periode,
            @RequestParam(name = "pemakaian", defaultValue = "0") String pemakaian,
            @RequestParam(name = "jumlahTanggunganAir", defaultValue = "0") BigDecimal jumlah,
            @RequestParam(name = "denda", defaultValue = "0") BigDecimal denda,
            @RequestParam(name = "adminBank", defaultValue = "0") BigDecimal adminBank,
            @RequestParam(name = "referenceNumber", defaultValue = "") String referenceNumber,
            @RequestParam(name = "footer", defaultValue = "PDAM MENYATAKAN STRUK INI SEBAGAI BUKTI PEMBAYARAN YANG SAH") String footer,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "tellerCu", defaultValue = "", required = false) String tellerCetakUlang
    ) {
        mav.addObject("format", "pdf");
        mav.addObject("datasource", new JREmptyDataSource());
        mav.setViewName("/pdam/report_pdam_tirtakualo_dti");
        mav.addObject("stanMeter", stanMeter);
        mav.addObject("lokasiPembayaran", lokasiPembayaran);
        mav.addObject("kodeTeller", kodeTeller);
        mav.addObject("nomorArsip", nomorArsip);
        mav.addObject("nomorPelanggan", nomorPelanggan);
        mav.addObject("nama", nama);
        mav.addObject("alamat", alamat);
        mav.addObject("golTarif", golTarif);
        mav.addObject("standMeter", standMeter);
        mav.addObject("pemakaian", pemakaian);
        BigDecimal total = jumlah.add(denda).add(adminBank);
        mav.addObject("terbilang", new KonversiMataUangKeTerbilang(total.setScale(0, BigDecimal.ROUND_CEILING)).toString());
        mav.addObject("tanggal", tanggal);
        mav.addObject("periode", periode);
        mav.addObject("jumlahTanggunganAir", jumlah);
        mav.addObject("denda", denda);
        mav.addObject("adminBank", adminBank);
        mav.addObject("referenceNumber", referenceNumber);
        mav.addObject("footer", footer);
        mav.addObject("cetakUlang", cetakUlang);
        mav.addObject("tellerCu", tellerCetakUlang);
        return mav;
    }
    
    /**
     * @param stanMeter        <parameter name="stanMeter" class="java.lang.String"/>
     * @param lokasiPembayaran <parameter name="lokasiPembayaran" class="java.lang.String"/>
     * @param kodeTeller       <parameter name="kodeTeller" class="java.lang.String"/>
     * @param nomorArsip       <parameter name="nomorArsip" class="java.lang.String"/>
     * @param nomorPelanggan   <parameter name="nomorPelanggan" class="java.lang.String"/>
     * @param nama             <parameter name="nama" class="java.lang.String"/>
     * @param alamat           <parameter name="alamat" class="java.lang.String"/>
     * @param golTarif         <parameter name="golTarif" class="java.lang.String"/>
     * @param standMeter       <parameter name="standMeter" class="java.lang.String"/>
     * @param tanggal          <parameter name="tanggal" class="java.util.Date"/>
     * @param periode          <parameter name="periode" class="java.lang.String"/>
     * @param pemakaian        <parameter name="pemakaian" class="java.lang.String"/>
     * @param jumlah           <parameter name="jumlahTanggunganAir" class="java.math.BigDecimal"/>
     * @param denda            <parameter name="denda" class="java.math.BigDecimal"/>
     * @param adminBank        <parameter name="adminBank" class="java.math.BigDecimal"/>
     * @return http://localhost:9090/downloads/pdf/pdam/struk/tirtakualo-dti/tagihan?stanMeter=&lokasiPembayaran=&kodeTeller=&nomorArsip=&nomorPelanggan=&nama=&alamat=&golTarif=&standMeter=&tanggal=&periode=&pemakaian=&jumlah=&denda=&adminBank=&footer=
     */
    @GetMapping(value = "/struk/tirtatanjung-dti/tagihan")
    public ModelAndView strukPDAMTirtatanjungDtiTagihan(
            ModelAndView mav,
            @RequestParam("stanMeter") String stanMeter,
            @RequestParam("lokasiPembayaran") String lokasiPembayaran,
            @RequestParam("kodeTeller") String kodeTeller,
            @RequestParam("nomorArsip") String nomorArsip,
            @RequestParam("nomorPelanggan") String nomorPelanggan,
            @RequestParam("nama") String nama,
            @RequestParam("alamat") String alamat,
            @RequestParam("golTarif") String golTarif,
            @RequestParam("standMeter") String standMeter,
            @RequestParam("tanggal") String tanggal,
            @RequestParam("periode") String periode,
            @RequestParam(name = "pemakaian", defaultValue = "0") String pemakaian,
            @RequestParam(name = "jumlahTanggunganAir", defaultValue = "0") BigDecimal jumlah,
            @RequestParam(name = "denda", defaultValue = "0") BigDecimal denda,
            @RequestParam(name = "adminBank", defaultValue = "0") BigDecimal adminBank,
            @RequestParam(name = "referenceNumber", defaultValue = "") String referenceNumber,
            @RequestParam(name = "footer", defaultValue = "PDAM MENYATAKAN STRUK INI SEBAGAI BUKTI PEMBAYARAN YANG SAH") String footer,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "tellerCu", defaultValue = "", required = false) String tellerCetakUlang
    ) {
        mav.addObject("format", "pdf");
        mav.addObject("datasource", new JREmptyDataSource());
        mav.setViewName("/pdam/report_pdam_tirtatanjung_dti");
        mav.addObject("stanMeter", stanMeter);
        mav.addObject("lokasiPembayaran", lokasiPembayaran);
        mav.addObject("kodeTeller", kodeTeller);
        mav.addObject("nomorArsip", nomorArsip);
        mav.addObject("nomorPelanggan", nomorPelanggan);
        mav.addObject("nama", nama);
        mav.addObject("alamat", alamat);
        mav.addObject("golTarif", golTarif);
        mav.addObject("standMeter", standMeter);
        mav.addObject("pemakaian", pemakaian);
        BigDecimal total = jumlah.add(denda).add(adminBank);
        mav.addObject("terbilang", new KonversiMataUangKeTerbilang(total.setScale(0, BigDecimal.ROUND_CEILING)).toString());
        mav.addObject("tanggal", tanggal);
        mav.addObject("periode", periode);
        mav.addObject("jumlahTanggunganAir", jumlah);
        mav.addObject("denda", denda);
        mav.addObject("adminBank", adminBank);
        mav.addObject("referenceNumber", referenceNumber);
        mav.addObject("footer", footer);
        mav.addObject("cetakUlang", cetakUlang);
        mav.addObject("tellerCu", tellerCetakUlang);
        return mav;
    }

    /**
     * @param stanMeter        <parameter name="stanMeter" class="java.lang.String"/>
     * @param lokasiPembayaran <parameter name="lokasiPembayaran" class="java.lang.String"/>
     * @param kodeTeller       <parameter name="kodeTeller" class="java.lang.String"/>
     * @param nomorArsip       <parameter name="nomorArsip" class="java.lang.String"/>
     * @param nomorPelanggan   <parameter name="nomorPelanggan" class="java.lang.String"/>
     * @param nama             <parameter name="nama" class="java.lang.String"/>
     * @param alamat           <parameter name="alamat" class="java.lang.String"/>
     * @param golTarif         <parameter name="golTarif" class="java.lang.String"/>
     * @param standMeter       <parameter name="standMeter" class="java.lang.String"/>
     * @param tanggal          <parameter name="tanggal" class="java.util.Date"/>
     * @param periode          <parameter name="periode" class="java.lang.String"/>
     * @param pemakaian        <parameter name="pemakaian" class="java.lang.String"/>
     * @param jumlah           <parameter name="jumlahTanggunganAir" class="java.math.BigDecimal"/>
     * @param denda            <parameter name="denda" class="java.math.BigDecimal"/>
     * @param adminBank        <parameter name="adminBank" class="java.math.BigDecimal"/>
     * @param footer           <parameter name="footer" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/pdam/struk/tirtabulian/tagihan?stanMeter=&lokasiPembayaran=&kodeTeller=&nomorArsip=&nomorPelanggan=&nama=&alamat=&golTarif=&standMeter=&tanggal=&periode=&pemakaian=&jumlah=&denda=&adminBank=&footer=
     */
    @GetMapping(value = "/struk/tirtabulian/tagihan")
    public ModelAndView strukPDAMtirtabulianTagihan(
            ModelAndView mav,
            @RequestParam("stanMeter") String stanMeter,
            @RequestParam("lokasiPembayaran") String lokasiPembayaran,
            @RequestParam("kodeTeller") String kodeTeller,
            @RequestParam("nomorArsip") String nomorArsip,
            @RequestParam("nomorPelanggan") String nomorPelanggan,
            @RequestParam("nama") String nama,
            @RequestParam("alamat") String alamat,
            @RequestParam("golTarif") String golTarif,
            @RequestParam("standMeter") String standMeter,
            @RequestParam("tanggal") String tanggal,
            @RequestParam("periode") String periode,
            @RequestParam(name = "pemakaian", defaultValue = "0") String pemakaian,
            @RequestParam(name = "jumlahTanggunganAir", defaultValue = "0") BigDecimal jumlah,
            @RequestParam(name = "denda", defaultValue = "0") BigDecimal denda,
            @RequestParam(name = "adminBank", defaultValue = "0") BigDecimal adminBank,
            @RequestParam(name = "footer", defaultValue = "PDAM MENYATAKAN STRUK INI SEBAGAI BUKTI PEMBAYARAN YANG SAH") String footer,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "tellerCu", defaultValue = "", required = false) String tellerCetakUlang
    ) {
        mav.addObject("format", "pdf");
        mav.addObject("datasource", new JREmptyDataSource());
        mav.setViewName("/pdam/report_pdam_tirtabulian");
        mav.addObject("stanMeter", stanMeter);
        mav.addObject("lokasiPembayaran", lokasiPembayaran);
        mav.addObject("kodeTeller", kodeTeller);
        mav.addObject("nomorArsip", nomorArsip);
        mav.addObject("nomorPelanggan", nomorPelanggan);
        mav.addObject("nama", nama);
        mav.addObject("alamat", alamat);
        mav.addObject("golTarif", golTarif);
        mav.addObject("standMeter", standMeter);
        mav.addObject("pemakaian", pemakaian);
        mav.addObject("terbilang", new KonversiMataUangKeTerbilang(
                jumlah.add(denda).add(adminBank)).toString()
        );
        mav.addObject("tanggal", tanggal);
        mav.addObject("periode", periode);
        mav.addObject("jumlahTanggunganAir", jumlah);
        mav.addObject("denda", denda);
        mav.addObject("adminBank", adminBank);
        mav.addObject("footer", footer);
        mav.addObject("cetakUlang", cetakUlang);
        mav.addObject("tellerCu", tellerCetakUlang);
        return mav;
    }

    /**
     * @param stanMeter        <parameter name="stanMeter" class="java.lang.String"/>
     * @param lokasiPembayaran <parameter name="lokasiPembayaran" class="java.lang.String"/>
     * @param kodeTeller       <parameter name="kodeTeller" class="java.lang.String"/>
     * @param nomorArsip       <parameter name="nomorArsip" class="java.lang.String"/>
     * @param nomorPelanggan   <parameter name="nomorPelanggan" class="java.lang.String"/>
     * @param nama             <parameter name="nama" class="java.lang.String"/>
     * @param alamat           <parameter name="alamat" class="java.lang.String"/>
     * @param golTarif         <parameter name="golTarif" class="java.lang.String"/>
     * @param standMeter       <parameter name="standMeter" class="java.lang.String"/>
     * @param tanggal          <parameter name="tanggal" class="java.util.Date"/>
     * @param periode          <parameter name="periode" class="java.lang.String"/>
     * @param pemakaian        <parameter name="pemakaian" class="java.lang.String"/>
     * @param jumlah           <parameter name="jumlahTanggunganAir" class="java.math.BigDecimal"/>
     * @param denda            <parameter name="denda" class="java.math.BigDecimal"/>
     * @param adminBank        <parameter name="adminBank" class="java.math.BigDecimal"/>
     * @param retribusi        <parameter name="retribusi" class="java.math.BigDecimal"/>
     * @param footer           <parameter name="footer" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/pdam/struk/tirtauli/tagihan?stanMeter=&lokasiPembayaran=&kodeTeller=&nomorArsip=&nomorPelanggan=&nama=&alamat=&golTarif=&standMeter=&tanggal=&periode=&pemakaian=&jumlah=&denda=&adminBank=&retribusi=
     */
    @GetMapping(value = "/struk/tirtauli/tagihan")
    public ModelAndView strukPDAMtirtauliTagihan(
            ModelAndView mav,
            @RequestParam("stanMeter") String stanMeter,
            @RequestParam("lokasiPembayaran") String lokasiPembayaran,
            @RequestParam("kodeTeller") String kodeTeller,
            @RequestParam("nomorArsip") String nomorArsip,
            @RequestParam("nomorPelanggan") String nomorPelanggan,
            @RequestParam("nama") String nama,
            @RequestParam("alamat") String alamat,
            @RequestParam("golTarif") String golTarif,
            @RequestParam("standMeter") String standMeter,
            @RequestParam("tanggal") String tanggal,
            @RequestParam("periode") String periode,
            @RequestParam(name = "pemakaian", defaultValue = "0") String pemakaian,
            @RequestParam(name = "retribusi", defaultValue = "0") BigDecimal retribusi,
            @RequestParam(name = "jumlahTanggunganAir", defaultValue = "0") BigDecimal jumlah,
            @RequestParam(name = "denda", defaultValue = "0") BigDecimal denda,
            @RequestParam(name = "adminBank", defaultValue = "0") BigDecimal adminBank,
            @RequestParam(name = "footer", defaultValue = "PDAM MENYATAKAN STRUK INI SEBAGAI BUKTI PEMBAYARAN YANG SAH") String footer,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "tellerCu", defaultValue = "", required = false) String tellerCetakUlang
    ) {
        mav.addObject("format", "pdf");
        mav.addObject("datasource", new JREmptyDataSource());
        mav.setViewName("/pdam/report_pdam_tirtauli");
        mav.addObject("stanMeter", stanMeter);
        mav.addObject("lokasiPembayaran", lokasiPembayaran);
        mav.addObject("kodeTeller", kodeTeller);
        mav.addObject("nomorArsip", nomorArsip);
        mav.addObject("nomorPelanggan", nomorPelanggan);
        mav.addObject("nama", nama);
        mav.addObject("alamat", alamat);
        mav.addObject("golTarif", golTarif);
        mav.addObject("standMeter", standMeter);
        mav.addObject("pemakaian", pemakaian);
        mav.addObject("terbilang", new KonversiMataUangKeTerbilang(
                jumlah.add(denda).add(adminBank)).toString()
        );
        mav.addObject("tanggal", tanggal);
        mav.addObject("periode", periode);
        mav.addObject("jumlahTanggunganAir", jumlah);
        mav.addObject("denda", denda);
        mav.addObject("adminBank", adminBank);
        mav.addObject("retribusi", retribusi);
        mav.addObject("footer", footer);
        mav.addObject("cetakUlang", cetakUlang);
        mav.addObject("tellerCu", tellerCetakUlang);
        return mav;
    }

    /**
     * @param stanMeter        <parameter name="stanMeter" class="java.lang.String"/>
     * @param lokasiPembayaran <parameter name="lokasiPembayaran" class="java.lang.String"/>
     * @param kodeTeller       <parameter name="kodeTeller" class="java.lang.String"/>
     * @param nomorArsip       <parameter name="nomorArsip" class="java.lang.String"/>
     * @param nomorPelanggan   <parameter name="nomorPelanggan" class="java.lang.String"/>
     * @param namaPelanggan    <parameter name="nama" class="java.lang.String"/>
     * @param alamatPelanggan  <parameter name="alamat" class="java.lang.String"/>
     * @param golTarif         <parameter name="golTarif" class="java.lang.String"/>
     * @param standMeter       <parameter name="standMeter" class="java.lang.String"/>
     * @param pemakaian        <parameter name="pemakaian" class="java.lang.String"/>
     * @param tanggal          <parameter name="tanggal" class="java.util.Date"/>
     * @param periode          <parameter name="periode" class="java.lang.String"/>
     * @param biayaPasang      <parameter name="biayaPasang" class="java.math.BigDecimal"/>
     * @param formulir         <parameter name="formulir" class="java.math.BigDecimal"/>
     * @param adminBank        <parameter name="adminBank" class="java.math.BigDecimal"/>
     * @param footer           <parameter name="footer" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/pdam/struk/tirtanadi/pasang-baru?stanMeter=&lokasiPembayaran=&kodeTeller=&nomorArsip=&nomorPelanggan=&namaPelanggan=&alamatPelanggan=&golTarif=&standMeter=&pemakaian=&tanggal=&periode=&biayaPasang=&formulir=&adminBank=&footer=
     */
    @GetMapping(value = "/struk/tirtanadi/pasang-baru")
    public ModelAndView strukPDAMTirtanadiPasangBaru(
            ModelAndView mav,
            @RequestParam("stanMeter") String stanMeter,
            @RequestParam("lokasiPembayaran") String lokasiPembayaran,
            @RequestParam("kodeTeller") String kodeTeller,
            @RequestParam("nomorArsip") String nomorArsip,
            @RequestParam("nomorPelanggan") String nomorPelanggan,
            @RequestParam("namaPelanggan") String namaPelanggan,
            @RequestParam("alamatPelanggan") String alamatPelanggan,
            @RequestParam("golTarif") String golTarif,
            @RequestParam("standMeter") String standMeter,
            @RequestParam(name = "pemakaian", defaultValue = "0") String pemakaian,
            @RequestParam("tanggal") String tanggal,
            @RequestParam("periode") String periode,
            @RequestParam(name = "biayaPasang", defaultValue = "0") BigDecimal biayaPasang,
            @RequestParam(name = "formulir", defaultValue = "0") BigDecimal formulir,
            @RequestParam(name = "adminBank", defaultValue = "0") BigDecimal adminBank,
            @RequestParam(name = "footer", defaultValue = "PDAM MENYATAKAN STRUK INI SEBAGAI BUKTI PEMBAYARAN YANG SAH") String footer,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "tellerCu", defaultValue = "", required = false) String tellerCetakUlang
    ) {
        mav.addObject("format", "pdf");
        mav.addObject("datasource", new JREmptyDataSource());
        mav.setViewName("/pdam/report_pdam_tirtanadi_baru");

        mav.addObject("stanMeter", stanMeter);
        mav.addObject("lokasiPembayaran", lokasiPembayaran);
        mav.addObject("kodeTeller", kodeTeller);
        mav.addObject("nomorArsip", nomorArsip);
        mav.addObject("nomorPelanggan", nomorPelanggan);
        mav.addObject("nama", namaPelanggan);
        mav.addObject("alamat", alamatPelanggan);
        mav.addObject("golTarif", golTarif);
        mav.addObject("standMeter", standMeter);
        mav.addObject("pemakaian", pemakaian);
        BigDecimal total = biayaPasang.add(formulir).add(adminBank);
        mav.addObject("terbilang", new KonversiMataUangKeTerbilang(total.setScale(0, BigDecimal.ROUND_CEILING)).toString());
        mav.addObject("tanggal", tanggal);
        mav.addObject("periode", periode);
        mav.addObject("biayaPasang", biayaPasang);
        mav.addObject("formulir", formulir);
        mav.addObject("adminBank", adminBank);
        mav.addObject("footer", footer);
        mav.addObject("cetakUlang", cetakUlang);
        mav.addObject("tellerCu", tellerCetakUlang);
        return mav;
    }
    
    /**
     * @param stanMeter        <parameter name="stanMeter" class="java.lang.String"/>
     * @param lokasiPembayaran <parameter name="lokasiPembayaran" class="java.lang.String"/>
     * @param kodeTeller       <parameter name="kodeTeller" class="java.lang.String"/>
     * @param nomorArsip       <parameter name="nomorArsip" class="java.lang.String"/>
     * @param nomorPelanggan   <parameter name="nomorPelanggan" class="java.lang.String"/>
     * @param namaPelanggan    <parameter name="nama" class="java.lang.String"/>
     * @param alamatPelanggan  <parameter name="alamat" class="java.lang.String"/>
     * @param golTarif         <parameter name="golTarif" class="java.lang.String"/>
     * @param standMeter       <parameter name="standMeter" class="java.lang.String"/>
     * @param pemakaian        <parameter name="pemakaian" class="java.lang.String"/>
     * @param tanggal          <parameter name="tanggal" class="java.util.Date"/>
     * @param periode          <parameter name="periode" class="java.lang.String"/>
     * @param biayaPasang      <parameter name="biayaPasang" class="java.math.BigDecimal"/>
     * @param formulir         <parameter name="formulir" class="java.math.BigDecimal"/>
     * @param adminBank        <parameter name="adminBank" class="java.math.BigDecimal"/>
     * @param footer           <parameter name="footer" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/pdam/struk/tirtaayumi-dti/pasang-baru?stanMeter=&lokasiPembayaran=&kodeTeller=&nomorArsip=&nomorPelanggan=&namaPelanggan=&alamatPelanggan=&golTarif=&standMeter=&pemakaian=&tanggal=&periode=&biayaPasang=&formulir=&adminBank=&footer=
     */
    @GetMapping(value = "/struk/tirtaayumi-dti/pasang-baru")
    public ModelAndView strukPDAMTirtaayumiDtiPasangBaru(
            ModelAndView mav,
            @RequestParam("stanMeter") String stanMeter,
            @RequestParam("lokasiPembayaran") String lokasiPembayaran,
            @RequestParam("kodeTeller") String kodeTeller,
            @RequestParam("nomorArsip") String nomorArsip,
            @RequestParam("nomorPelanggan") String nomorPelanggan,
            @RequestParam("namaPelanggan") String namaPelanggan,
            @RequestParam("alamatPelanggan") String alamatPelanggan,
            @RequestParam("golTarif") String golTarif,
            @RequestParam("standMeter") String standMeter,
            @RequestParam(name = "pemakaian", defaultValue = "0") String pemakaian,
            @RequestParam("tanggal") String tanggal,
            @RequestParam("periode") String periode,
            @RequestParam(name = "biayaPasang", defaultValue = "0") BigDecimal biayaPasang,
            @RequestParam(name = "formulir", defaultValue = "0") BigDecimal formulir,
            @RequestParam(name = "adminBank", defaultValue = "0") BigDecimal adminBank,
            @RequestParam(name = "kodeInstansi", defaultValue = "") String kodeInstansi,
            @RequestParam(name = "footer", defaultValue = "PDAM MENYATAKAN STRUK INI SEBAGAI BUKTI PEMBAYARAN YANG SAH") String footer,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "tellerCu", defaultValue = "", required = false) String tellerCetakUlang
    ) {
        mav.addObject("format", "pdf");
        mav.addObject("datasource", new JREmptyDataSource());
        mav.setViewName("/pdam/report_pdam_tirtaayumi_dti_baru");

        mav.addObject("stanMeter", stanMeter);
        mav.addObject("lokasiPembayaran", lokasiPembayaran);
        mav.addObject("kodeTeller", kodeTeller);
        mav.addObject("nomorArsip", nomorArsip);
        mav.addObject("nomorPelanggan", nomorPelanggan);
        mav.addObject("nama", namaPelanggan);
        mav.addObject("alamat", alamatPelanggan);
        mav.addObject("golTarif", golTarif);
        mav.addObject("standMeter", standMeter);
        mav.addObject("pemakaian", pemakaian);
        BigDecimal total = biayaPasang.add(formulir).add(adminBank);
        mav.addObject("terbilang", new KonversiMataUangKeTerbilang(total.setScale(0, BigDecimal.ROUND_CEILING)).toString());
        mav.addObject("tanggal", tanggal);
        mav.addObject("periode", periode);
        mav.addObject("biayaPasang", biayaPasang);
        mav.addObject("formulir", formulir);
        mav.addObject("adminBank", adminBank);
        mav.addObject("footer", footer);
        mav.addObject("cetakUlang", cetakUlang);
        mav.addObject("tellerCu", tellerCetakUlang);

        // ambil nama instansi pdam berdasarkan kode nya
        String reportTitle = "PEMBAYARAN PDAM DTI SUMUT";
        if (StringUtils.isNotEmpty(kodeInstansi)) {
            InstansiPdam instansiPdam = instansiPdamRepository.findByKodeInstansi(kodeInstansi);
            if (instansiPdam != null) {
                if (instansiPdam.getKodeInstansi().equalsIgnoreCase("07009")){
                    reportTitle = "PEMBAYARAN PERUMDA " + StringUtils.upperCase(instansiPdam.getNamaInstansi());
                } else {
                    reportTitle = "PEMBAYARAN PDAM " + StringUtils.upperCase(instansiPdam.getNamaInstansi());
                }
            }
        }
        mav.addObject("titlePdam", reportTitle);

        return mav;
    }
    
    /**
     * @param stanMeter        <parameter name="stanMeter" class="java.lang.String"/>
     * @param lokasiPembayaran <parameter name="lokasiPembayaran" class="java.lang.String"/>
     * @param kodeTeller       <parameter name="kodeTeller" class="java.lang.String"/>
     * @param nomorArsip       <parameter name="nomorArsip" class="java.lang.String"/>
     * @param nomorPelanggan   <parameter name="nomorPelanggan" class="java.lang.String"/>
     * @param namaPelanggan    <parameter name="nama" class="java.lang.String"/>
     * @param alamatPelanggan  <parameter name="alamat" class="java.lang.String"/>
     * @param golTarif         <parameter name="golTarif" class="java.lang.String"/>
     * @param standMeter       <parameter name="standMeter" class="java.lang.String"/>
     * @param pemakaian        <parameter name="pemakaian" class="java.lang.String"/>
     * @param tanggal          <parameter name="tanggal" class="java.util.Date"/>
     * @param periode          <parameter name="periode" class="java.lang.String"/>
     * @param biayaPasang      <parameter name="biayaPasang" class="java.math.BigDecimal"/>
     * @param formulir         <parameter name="formulir" class="java.math.BigDecimal"/>
     * @param adminBank        <parameter name="adminBank" class="java.math.BigDecimal"/>
     * @param footer           <parameter name="footer" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/pdam/struk/tirtakualo-dti/pasang-baru?stanMeter=&lokasiPembayaran=&kodeTeller=&nomorArsip=&nomorPelanggan=&namaPelanggan=&alamatPelanggan=&golTarif=&standMeter=&pemakaian=&tanggal=&periode=&biayaPasang=&formulir=&adminBank=&footer=
     */
    @GetMapping(value = "/struk/tirtakualo-dti/pasang-baru")
    public ModelAndView strukPDAMTirtakualoDtiPasangBaru(
            ModelAndView mav,
            @RequestParam("stanMeter") String stanMeter,
            @RequestParam("lokasiPembayaran") String lokasiPembayaran,
            @RequestParam("kodeTeller") String kodeTeller,
            @RequestParam("nomorArsip") String nomorArsip,
            @RequestParam("nomorPelanggan") String nomorPelanggan,
            @RequestParam("namaPelanggan") String namaPelanggan,
            @RequestParam("alamatPelanggan") String alamatPelanggan,
            @RequestParam("golTarif") String golTarif,
            @RequestParam("standMeter") String standMeter,
            @RequestParam(name = "pemakaian", defaultValue = "0") String pemakaian,
            @RequestParam("tanggal") String tanggal,
            @RequestParam("periode") String periode,
            @RequestParam(name = "biayaPasang", defaultValue = "0") BigDecimal biayaPasang,
            @RequestParam(name = "formulir", defaultValue = "0") BigDecimal formulir,
            @RequestParam(name = "adminBank", defaultValue = "0") BigDecimal adminBank,
            @RequestParam(name = "footer", defaultValue = "PDAM MENYATAKAN STRUK INI SEBAGAI BUKTI PEMBAYARAN YANG SAH") String footer,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "tellerCu", defaultValue = "", required = false) String tellerCetakUlang
    ) {
        mav.addObject("format", "pdf");
        mav.addObject("datasource", new JREmptyDataSource());
        mav.setViewName("/pdam/report_pdam_tirtakualo_dti_baru");

        mav.addObject("stanMeter", stanMeter);
        mav.addObject("lokasiPembayaran", lokasiPembayaran);
        mav.addObject("kodeTeller", kodeTeller);
        mav.addObject("nomorArsip", nomorArsip);
        mav.addObject("nomorPelanggan", nomorPelanggan);
        mav.addObject("nama", namaPelanggan);
        mav.addObject("alamat", alamatPelanggan);
        mav.addObject("golTarif", golTarif);
        mav.addObject("standMeter", standMeter);
        mav.addObject("pemakaian", pemakaian);
        BigDecimal total = biayaPasang.add(formulir).add(adminBank);
        mav.addObject("terbilang", new KonversiMataUangKeTerbilang(total.setScale(0, BigDecimal.ROUND_CEILING)).toString());
        mav.addObject("tanggal", tanggal);
        mav.addObject("periode", periode);
        mav.addObject("biayaPasang", biayaPasang);
        mav.addObject("formulir", formulir);
        mav.addObject("adminBank", adminBank);
        mav.addObject("footer", footer);
        mav.addObject("cetakUlang", cetakUlang);
        mav.addObject("tellerCu", tellerCetakUlang);
        return mav;
    }
    
    /**
     * @param stanMeter        <parameter name="stanMeter" class="java.lang.String"/>
     * @param lokasiPembayaran <parameter name="lokasiPembayaran" class="java.lang.String"/>
     * @param kodeTeller       <parameter name="kodeTeller" class="java.lang.String"/>
     * @param nomorArsip       <parameter name="nomorArsip" class="java.lang.String"/>
     * @param nomorPelanggan   <parameter name="nomorPelanggan" class="java.lang.String"/>
     * @param namaPelanggan    <parameter name="nama" class="java.lang.String"/>
     * @param alamatPelanggan  <parameter name="alamat" class="java.lang.String"/>
     * @param golTarif         <parameter name="golTarif" class="java.lang.String"/>
     * @param standMeter       <parameter name="standMeter" class="java.lang.String"/>
     * @param pemakaian        <parameter name="pemakaian" class="java.lang.String"/>
     * @param tanggal          <parameter name="tanggal" class="java.util.Date"/>
     * @param periode          <parameter name="periode" class="java.lang.String"/>
     * @param biayaPasang      <parameter name="biayaPasang" class="java.math.BigDecimal"/>
     * @param formulir         <parameter name="formulir" class="java.math.BigDecimal"/>
     * @param adminBank        <parameter name="adminBank" class="java.math.BigDecimal"/>
     * @param footer           <parameter name="footer" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/pdam/struk/tirtatanjung-dti/pasang-baru?stanMeter=&lokasiPembayaran=&kodeTeller=&nomorArsip=&nomorPelanggan=&namaPelanggan=&alamatPelanggan=&golTarif=&standMeter=&pemakaian=&tanggal=&periode=&biayaPasang=&formulir=&adminBank=&footer=
     */
    @GetMapping(value = "/struk/tirtatanjung-dti/pasang-baru")
    public ModelAndView strukPDAMTirtatanjungDtiPasangBaru(
            ModelAndView mav,
            @RequestParam("stanMeter") String stanMeter,
            @RequestParam("lokasiPembayaran") String lokasiPembayaran,
            @RequestParam("kodeTeller") String kodeTeller,
            @RequestParam("nomorArsip") String nomorArsip,
            @RequestParam("nomorPelanggan") String nomorPelanggan,
            @RequestParam("namaPelanggan") String namaPelanggan,
            @RequestParam("alamatPelanggan") String alamatPelanggan,
            @RequestParam("golTarif") String golTarif,
            @RequestParam("standMeter") String standMeter,
            @RequestParam(name = "pemakaian", defaultValue = "0") String pemakaian,
            @RequestParam("tanggal") String tanggal,
            @RequestParam("periode") String periode,
            @RequestParam(name = "biayaPasang", defaultValue = "0") BigDecimal biayaPasang,
            @RequestParam(name = "formulir", defaultValue = "0") String formulir,
            @RequestParam(name = "adminBank", defaultValue = "0") BigDecimal adminBank,
            @RequestParam(name = "footer", defaultValue = "PDAM MENYATAKAN STRUK INI SEBAGAI BUKTI PEMBAYARAN YANG SAH") String footer,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "tellerCu", defaultValue = "", required = false) String tellerCetakUlang
    ) {
        mav.addObject("format", "pdf");
        mav.addObject("datasource", new JREmptyDataSource());
        mav.setViewName("/pdam/report_pdam_tirtatanjung_dti_baru");

        mav.addObject("stanMeter", stanMeter);
        mav.addObject("lokasiPembayaran", lokasiPembayaran);
        mav.addObject("kodeTeller", kodeTeller);
        mav.addObject("nomorArsip", nomorArsip);
        mav.addObject("nomorPelanggan", nomorPelanggan);
        mav.addObject("nama", namaPelanggan);
        mav.addObject("alamat", alamatPelanggan);
        mav.addObject("golTarif", golTarif);
        mav.addObject("standMeter", standMeter);
        mav.addObject("pemakaian", pemakaian);

        BigDecimal formulirDec = new BigDecimal("0.01");
        if(!formulir.equals("undefined")){
            formulirDec = new BigDecimal(formulir);
        }

        BigDecimal total = biayaPasang.add(formulirDec).add(adminBank);
        mav.addObject("terbilang", new KonversiMataUangKeTerbilang(total.setScale(0, BigDecimal.ROUND_CEILING)).toString());
        mav.addObject("tanggal", tanggal);
        mav.addObject("periode", periode);
        mav.addObject("biayaPasang", biayaPasang);
        mav.addObject("formulir", formulir);
        mav.addObject("adminBank", adminBank);
        mav.addObject("footer", footer);
        mav.addObject("cetakUlang", cetakUlang);
        mav.addObject("tellerCu", tellerCetakUlang);
        return mav;
    }

    /**
     * @param stanMeter         <parameter name="stanMeter" class="java.lang.String"/>
     * @param lokasiPembayaran  <parameter name="lokasiPembayaran" class="java.lang.String"/>
     * @param kodeTeller        <parameter name="kodeTeller" class="java.lang.String"/>
     * @param nomorArsip        <parameter name="nomorArsip" class="java.lang.String"/>
     * @param nomorPelanggan    <parameter name="nomorPelanggan" class="java.lang.String"/>
     * @param namaPelanggan     <parameter name="nama" class="java.lang.String"/>
     * @param alamatPelanggan   <parameter name="alamat" class="java.lang.String"/>
     * @param golonganTarif     <parameter name="golTarif" class="java.lang.String"/>
     * @param standMeter        <parameter name="standMeter" class="java.lang.String"/>
     * @param pemakaian         <parameter name="pemakaian" class="java.lang.String"/>
     * @param tanggal           <parameter name="terbilang" class="java.lang.String"/>
     * @param periode           <parameter name="periode" class="java.lang.String"/>
     * @param harga             <parameter name="harga" class="java.math.BigDecimal"/>
     * @param materai           <parameter name="materai" class="java.math.BigDecimal"/>
     * @param rawat             <parameter name="rawat" class="java.math.BigDecimal"/>
     * @param beaSaluran        <parameter name="beaSaluran" class="java.math.BigDecimal"/>
     * @param totalSampah       <parameter name="totalSampah" class="java.math.BigDecimal"/>
     * @param bulanKeSambung    <parameter name="bulanKeSambung" class="java.lang.Integer"/>
     * @param angsuranSambungan <parameter name="angsuranSambungan" class="java.math.BigDecimal"/>
     * @param sisaAngsuran      <parameter name="sisaAngsuran" class="java.math.BigDecimal"/>
     * @param rekeningBeban     <parameter name="rekeningBeban" class="java.lang.String"/>
     * @param footer            <parameter name="footer" class="java.lang.String"/>
     * @return http://localhost:9090/downloads/pdf/pdam/struk/tirtakualo/tagihan?stanMeter=&lokasiPembayaran=&kodeTeller=&nomorArsip=&nomorPelanggan=&namaPelanggan=&alamatPelanggan=&golonganTarif=&standMeter=&standMeter=&pemakaian=&tanggal=&periode=&harga=&materai=&rawat=&beaSaluran=&totalSampah=&bulanKeSambungan=&angsuranSambungan=&sisaAngsuran=&adminBank=&footer=
     */
    @GetMapping(value = "/struk/tirtakualo/tagihan")
    public ModelAndView strukPdamTirtakualo(
            ModelAndView mav,
            @RequestParam("stanMeter") String stanMeter,
            @RequestParam("lokasiPembayaran") String lokasiPembayaran,
            @RequestParam("kodeTeller") String kodeTeller,
            @RequestParam("nomorArsip") String nomorArsip,
            @RequestParam("nomorPelanggan") String nomorPelanggan,
            @RequestParam("namaPelanggan") String namaPelanggan,
            @RequestParam("alamatPelanggan") String alamatPelanggan,
            @RequestParam("golonganTarif") String golonganTarif,
            @RequestParam("standMeter") String standMeter,
            @RequestParam(name = "pemakaian", defaultValue = "0") String pemakaian,
            @RequestParam("tanggal") String tanggal,
            @RequestParam("periode") String periode,
            @RequestParam(name = "harga", defaultValue = "0") BigDecimal harga,
            @RequestParam(name = "materai", defaultValue = "0") BigDecimal materai,
            @RequestParam(name = "rawat", defaultValue = "0") BigDecimal rawat,
            @RequestParam(name = "beaSaluran", defaultValue = "0") BigDecimal beaSaluran,
            @RequestParam(name = "totalSampah", defaultValue = "0") BigDecimal totalSampah,
            @RequestParam(name = "bulanKeSambungan", defaultValue = "0") Integer bulanKeSambung,
            @RequestParam(name = "angsuranSambungan", defaultValue = "0") BigDecimal angsuranSambungan,
            @RequestParam(name = "sisaAngsuran", defaultValue = "0") BigDecimal sisaAngsuran,
            @RequestParam(name = "adminCharge", defaultValue = "0") BigDecimal adminCharge,
            @RequestParam("rekeningBeban") String rekeningBeban,
            @RequestParam(name = "footer", defaultValue = "PDAM MENYATAKAN STRUK INI SEBAGAI BUKTI PEMBAYARAN YANG SAH") String footer,
            @RequestParam(name = "cu", defaultValue = "", required = false) String cetakUlang,
            @RequestParam(name = "tellerCu", defaultValue = "", required = false) String tellerCetakUlang

    ) {
        mav.addObject("format", "pdf");
        mav.addObject("datasource", new JREmptyDataSource());
        mav.setViewName("/pdam/report_pdam_tirtakualo");

        mav.addObject("stanMeter", stanMeter);
        mav.addObject("lokasiPembayaran", lokasiPembayaran);
        mav.addObject("kodeTeller", kodeTeller);
        mav.addObject("nomorArsip", nomorArsip);
        mav.addObject("nomorPelanggan", nomorPelanggan);
        mav.addObject("nama", namaPelanggan);
        mav.addObject("alamat", alamatPelanggan);
        mav.addObject("golTarif", golonganTarif);
        mav.addObject("standMeter", standMeter);
        mav.addObject("pemakaian", pemakaian);
        mav.addObject("adminCharge", adminCharge);
        mav.addObject("rekeningBeban", rekeningBeban);
        mav.addObject("total", harga.add(materai).add(rawat).add(beaSaluran).add(totalSampah).add(adminCharge));
        mav.addObject("terbilang",
                new KonversiMataUangKeTerbilang(
                        harga.add(materai).add(rawat).add(beaSaluran).add(totalSampah).add(adminCharge)).toString()
        );
        mav.addObject("tanggal", tanggal);
        mav.addObject("periode", periode);
        mav.addObject("harga", harga);
        mav.addObject("materai", materai);
        mav.addObject("rawat", rawat);
        mav.addObject("beaSaluran", beaSaluran);
        mav.addObject("totalSampah", totalSampah);
        mav.addObject("bulanKeSambungan", bulanKeSambung);
        mav.addObject("angsuranSambungan", angsuranSambungan);
        mav.addObject("sisaAngsuran", sisaAngsuran);
        mav.addObject("footer", footer);
        mav.addObject("cu", cetakUlang);
        mav.addObject("tellerCu", tellerCetakUlang);
        return mav;
    }
}
