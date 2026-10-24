package com.multipolar.uiteller.backend.controller.report;

import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.*;
import com.multipolar.uiteller.backend.service.CoreGatewayServiceImpl;
import com.multipolar.uiteller.backend.service.MiddlewareGatewayServiceImpl;
import com.multipolar.uiteller.backend.service.report.ReportPdamService;
import com.multipolar.uiteller.backend.service.report.ReportService;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/downloads/pdf/activity")
public class ReportActivityController {

    @Autowired
    private ReportPdamService reportService;
    @Autowired
    private ReportService service;
    @Autowired
    private MiddlewareGatewayServiceImpl middlewareGatewayService;
    @Autowired
    private CoreGatewayServiceImpl coreGatewayService;

    @GetMapping("/laporan/someteller")
    public ModelAndView laporanActivity(
            ModelAndView view,
            HttpServletRequest request,
            @RequestParam("user") String user,
            @RequestParam("branch") String branch,
            @RequestParam("payType") String payType,
            @RequestParam("account") String account,
            @RequestParam(value = "type", defaultValue = "pdf", required = false) String s) {
        String kas = null;
        AccountResponse accountResponse = coreGatewayService.getAccountResponse(account);
        if (accountResponse != null) {
            AccountResult accountResult = accountResponse.getResult();
            kas = accountResult.getSsaldoAkhir();
        }
        view.setViewName("/activity/report_activity_someteller");
        view.addObject("format", s);
        view.addObject("periode", service.dateFormat(new Date(), Constants.MODEL_REPORT_SETTLEMENT_DATE));
        view.addObject("pay", service.type(payType));
        view.addObject("branch", branch);
        List<Activity> activities = middlewareGatewayService.getSomeTellerResponse(user, payType);
        view.addObject("debitTotal", FeatureContextHolder.getContext().getAllActivity().getDebit());
        view.addObject("creditTotal", FeatureContextHolder.getContext().getAllActivity().getCredit());
        view.addObject("kas", kas);
        view.addObject("datasource", new JRBeanCollectionDataSource(activities));
        return view;
    }

    @GetMapping("/laporan/allteller")
    public ModelAndView Activity(
            ModelAndView view,
            HttpServletRequest request,
            @RequestParam("user") String user,
            @RequestParam(value = "type", defaultValue = "pdf", required = false) String s) {
        view.setViewName("/activity/report_activity_allteller");
        view.addObject("format", s);
        view.addObject("periode", service.dateFormat(new Date(), Constants.MODEL_REPORT_SETTLEMENT_DATE));
        List<AllActivity> allActivities = middlewareGatewayService.getAllTellerResponse();
        view.addObject("datasource", new JRBeanCollectionDataSource(allActivities));
        return view;
    }

    @GetMapping("/laporan/noteteller")
    public ModelAndView ActivityNote(
            ModelAndView view,
            @RequestParam("branch") String branch,
            @RequestParam("branchName") String branchName,
            @RequestParam("sourceNumber") String sourceNumber,
            @RequestParam("user") String user,
            @RequestParam(value = "type", defaultValue = "pdf", required = false) String s) {
        view.addObject("format", "pdf");
        view.addObject("datasource", new JREmptyDataSource());
        view.setViewName("/activity/report_activity_note");
        NoteActivity noteActivity = new NoteActivity();
        middlewareGatewayService.getOneTellerResponse(branch + user, branch, noteActivity);
        view.addObject("branchName", branchName);
        view.addObject("creditSource", sourceNumber);
        view.addObject("creditNominal", noteActivity.getCreditNominal());
        view.addObject("spell", noteActivity.getSpell());
        view.addObject("description", noteActivity.getDescription());
        view.addObject("user", user);
        view.addObject("transactionDate", noteActivity.getTransactionDate());
        return view;
    }
}
