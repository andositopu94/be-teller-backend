package com.multipolar.uiteller.backend.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Sipd {
    @Data
    private class Title{
        private Content content;
    }
    @Data
    private class Content{
        private String date;
        private String time;
        private String billing;
        private String amount;
        private String NPWP;
        private String name;
        private String address;
        private String budgetCode;
        private String depositType;
        private String taxPeriod;
        private String assessmentID;
        private String objectID;
        private String NTPN;
        private String refID;
        private String traceID;
        private String settlementDate;
        private String branchCode;
        private String branch;
        private String rawData;

        private Content() {
        }
    }
}














//    @Override
//    public String toString() {
//        return "YourDataClass{" +
//                "date=" + date +
//                ", time=" + time +
//                ", billing='" + billing + '\'' +
//                ", amount=" + amount +
//                ", NPWP=" + npwp +
//                ", name='" + name + '\'' +
//                ", address='" + address + '\'' +
//                ", budgetCode='" + budgetCode + '\'' +
//                ", depositType=" + depositType +
//                ", taxPeriod='" + taxPeriod + '\'' +
//                ", assessmentID='" + assessmentID + '\'' +
//                ", objectID='" + objectID + '\'' +
//                ", NTPN='" + NTPN + '\'' +
//                ", refID='" + refID + '\'' +
//                ", traceID=" + traceID +
//                ", settlementDate=" + settlementDate +
//                '}';
//    }

