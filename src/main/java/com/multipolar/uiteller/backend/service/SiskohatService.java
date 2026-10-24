package com.multipolar.uiteller.backend.service;

import com.multipolar.uiteller.backend.model.City;
import com.multipolar.uiteller.backend.model.Job;
import com.multipolar.uiteller.backend.repository.CityRepository;
import com.multipolar.uiteller.backend.repository.JobRepository;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.DetailResponse;
import com.multipolar.uiteller.backend.security.model.DetailResult;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SiskohatService {
    private static Logger logger = LoggerFactory.getLogger(SiskohatService.class);

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private TraceNumberGenerator stan;
    private static final SimpleDateFormat MONTH = new SimpleDateFormat("MM");
    private static final SimpleDateFormat DATE = new SimpleDateFormat("dd");
    private final static Map<String, String> MAP = new HashMap<String, String>() {
        {
            put("1", "2");
            put("2", "1");
            put("31", "3");
            put("32", "4");
        }
    };
    private static final Map<String, String> STRING_MAP = new HashMap<String, String>() {
        {
            put("01", "1");
            put("02", "2");
            put("03", "3");
            put("04", "4");
            put("05", "5");
            put("06", "6");
            put("07", "7");
            put("08", "8");
            put("09", "9");
            put("10", "A");
            put("11", "B");
            put("12", "C");
        }
    };
    private static final String defaultAcquirer = "117";

    public City getCity(String s) {
        return cityRepository.findByCoreCode(s);
    }

    public Job getJob(String s) {
        return jobRepository.findByCoreJob(s);
    }

    public DetailResponse getDetail(DetailResponse detailResponse) {
        String edu, work;
        Date date = new Date();
        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (detailResponse == null)
            throw new IllegalArgumentException("Time-out exception, host give no response");
        DetailResult detailResult = detailResponse.getResult();
        if (detailResult != null) {
            edu = detailResult.getLASTEDUID();
            City city = getCity(detailResponse.getResult().getCITYID());
            if (city == null)
                throw new IllegalArgumentException("City not found, please contact administrator");
            if(detailResponse.getResult().getJOBID().equals("")){
                detailResponse.getResult().setJOBID("000");
            }
            Job job = getJob(detailResponse.getResult().getJOBID());
            if (job == null)
                throw new IllegalArgumentException("Job not found, please contact administrator ");
            work = job.getJobCode();
            detailResponse.setCityId(city.getCityCode());
            detailResponse.setEduId(StringUtils.substring(edu, edu.length() - 1, edu.length()));
            detailResponse.setMarrId(MAP.get(StringUtils.equals(detailResult.getMARRIAGEID(), "3") ? detailResult.getMARRIAGEID() + detailResult.getSEX() : detailResult.getMARRIAGEID()));
            detailResponse.setWorkId(StringUtils.substring(work, work.length() - 1, work.length()));
            detailResponse.setBranchId(userDetail.getBranchCode());
            detailResponse.setBranchName(userDetail.getBranchName());
            detailResponse.setRef(defaultAcquirer + STRING_MAP.get(MONTH.format(date)) + DATE.format(date) + stan.getTraceNumber());
            detailResponse.getResult().setFULLNM(detailResponse.getResult().getFULLNM() == null ? Constants.BLANK : StringUtils.replaceAll(StringUtils.substring(detailResponse.getResult().getFULLNM(), 0, 39), Constants.REGEX_CORE, Constants.SPACE));
            detailResponse.getResult().setIDNBR(detailResponse.getResult().getIDNBR() == null ? Constants.BLANK : StringUtils.substring(detailResponse.getResult().getIDNBR(), 0, 19));
            detailResponse.getResult().setBRTPLACE(detailResponse.getResult().getBRTPLACE() == null ? Constants.BLANK : StringUtils.replaceAll(StringUtils.substring(detailResponse.getResult().getBRTPLACE(), 0, 24), Constants.REGEX_CORE, Constants.SPACE));
            detailResponse.getResult().setADDR(detailResponse.getResult().getADDR() == null ? Constants.BLANK : StringUtils.replaceAll(StringUtils.substring(detailResponse.getResult().getADDR(), 0, 24), Constants.REGEX_CORE_ADDRESS, Constants.SPACE));
            detailResponse.getResult().setKELNM(detailResponse.getResult().getKELNM() == null ? Constants.BLANK : StringUtils.replaceAll(StringUtils.substring(detailResponse.getResult().getKELNM(), 0, 24), Constants.REGEX_CORE, Constants.SPACE));
            detailResponse.getResult().setKECNM(detailResponse.getResult().getKECNM() == null ? Constants.BLANK : StringUtils.replaceAll(StringUtils.substring(detailResponse.getResult().getKECNM(), 0, 24), Constants.REGEX_CORE, Constants.SPACE));
            detailResponse.getResult().setCITYNM(detailResponse.getResult().getCITYNM() == null ? Constants.BLANK : StringUtils.replaceAll(StringUtils.substring(detailResponse.getResult().getCITYNM(), 0, 24), Constants.REGEX_CORE, Constants.SPACE));
            detailResponse.getResult().setCOMNMJOB(job.getCoreName() == null ? Constants.BLANK : job.getCoreName());
        }
        return detailResponse;
    }

}
