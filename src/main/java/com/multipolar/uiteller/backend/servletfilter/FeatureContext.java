package com.multipolar.uiteller.backend.servletfilter;

import com.multipolar.uiteller.backend.security.model.AllActivity;
import com.multipolar.uiteller.backend.security.model.DetailResult;
import org.jpos.iso.ISOMsg;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public interface FeatureContext extends Serializable {

    String getFeatureName();

    void setFeatureName(String featureName);

    String getSourceName();

    void setSourceName(String sourceName);

    String getDestinationName();

    void setDestinationName(String destinationName);

    Date getFeatureTransactionDate();

    void setFeatureTransactionDate(Date featureTransactionDate);

    Date getSettlementDate();

    void setSettlementDate(Date date);

    Map<String, Object> getReportData();

    void setReportData(Map<String, Object> add);

    String getTraceNumber();

    void setTraceNumber(String traceNumber);

    DetailResult getDetailResult();

    void setDetailResult(DetailResult detailResult);

    ISOMsg getRequest();

    void setRequest(ISOMsg isoMsg);

    String getTraceNumberTmp();

    void setTraceNumberTmp(String traceNumberTmp);

    AllActivity getAllActivity();

    void setAllActivity(AllActivity allActivity);

    String getInstansiParameter();

    void setInstansiParameter(String instansiParameter);

    int getBpihReportUploadFlag();

    void setBpihReportUploadFlag(int flag);
}