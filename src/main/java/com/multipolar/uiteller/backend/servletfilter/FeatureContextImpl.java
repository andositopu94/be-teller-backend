package com.multipolar.uiteller.backend.servletfilter;

import com.multipolar.uiteller.backend.security.model.AllActivity;
import com.multipolar.uiteller.backend.security.model.DetailResult;
import org.jpos.iso.ISOMsg;

import java.util.Date;
import java.util.Map;

public class FeatureContextImpl implements FeatureContext {

    private static final long serialVersionUID = 1290690518767141798L;

    private String featureName;
    private String sourceName;
    private String destinationName;
    private Date settlementDate;
    private Date featureTransactionDate;
    private String traceNumber;
    private DetailResult detailResult;
    private ISOMsg request;
    private String traceNumberTmp;
    private AllActivity allActivity;
    private String instansiParameter;
    private int bpihReportUploadFlag = 0;

    private Map<String, Object> reportData;

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    @Override
    public Date getFeatureTransactionDate() {
        return featureTransactionDate;
    }

    @Override
    public void setFeatureTransactionDate(Date featureTransactionDate) {
        this.featureTransactionDate = featureTransactionDate;
    }

    @Override
    public Date getSettlementDate() {
        return settlementDate;
    }

    @Override
    public void setSettlementDate(Date settlementDate) {
        this.settlementDate = settlementDate;
    }

    @Override
    public Map<String, Object> getReportData() {
        return reportData;
    }

    @Override
    public void setReportData(Map<String, Object> add) {
        this.reportData = add;
    }

    @Override
    public String getTraceNumber() {
        return traceNumber;
    }

    @Override
    public void setTraceNumber(String traceNumber) {
        this.traceNumber = traceNumber;
    }

    @Override
    public DetailResult getDetailResult() {
        return detailResult;
    }

    @Override
    public void setDetailResult(DetailResult detailResult) {
        this.detailResult = detailResult;
    }

    @Override
    public ISOMsg getRequest() {
        return request;
    }

    @Override
    public void setRequest(ISOMsg isoMsg) {
        this.request = isoMsg;
    }

    @Override
    public String getTraceNumberTmp() {
        return traceNumberTmp;
    }

    @Override
    public void setTraceNumberTmp(String traceNumberTmp) {
        this.traceNumberTmp = traceNumberTmp;
    }

    @Override
    public AllActivity getAllActivity() {
        return allActivity;
    }

    @Override
    public void setAllActivity(AllActivity allActivity) {
        this.allActivity = allActivity;
    }

    @Override
    public String getInstansiParameter() {
        return instansiParameter;
    }

    @Override
    public void setInstansiParameter(String instansiParameter) {
        this.instansiParameter = instansiParameter;
    }

    @Override
    public int getBpihReportUploadFlag() {
        return bpihReportUploadFlag;
    }

    @Override
    public void setBpihReportUploadFlag(int flag) {
        this.bpihReportUploadFlag = flag;
    }
}
