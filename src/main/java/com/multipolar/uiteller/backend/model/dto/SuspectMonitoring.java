package com.multipolar.uiteller.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuspectMonitoring {

    private Long id;
    private String customerId;
    private Long commonRequestId;
    private String archiveNumber;
    private String transactionType;
    private Date transactionDate;
    private String nominal;
    private String traceNumber;
    private String reference;
    private String status;
    private String description;
    private String middlewareStatus;
    private String sourceNumber;
    private String customerName;
    private String branchCode;
    private String userid;
    private String action;
}
