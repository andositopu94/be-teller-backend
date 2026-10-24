package com.multipolar.uiteller.backend.model;

import com.multipolar.uiteller.backend.resource.Troubleshoot;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "logs")
public class MonitoringLog {
    @Id
    private Long id;
    @Column(name = "mid_status")
    private String middlewareStatus;
    private String description;
    private Troubleshoot troubleshoot;
    @Temporal(TemporalType.TIMESTAMP)
    private Date troubleshootDate;
    private String troubleshootUserId;
    private Long commonId;
    private Long commonRequestId;
    private String traceNumber;
    private String branchCode;
    private String status;
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;
    private String userId;
    private String transactionType;
    private String reference;
    private String archiveNumber;
    private String nominal;
    private String currencyCode;
    private String sourceNumber;
    private String customerId;
    private String customerName;
    private String timeoutResourceId;
    private Troubleshoot troubleshootFlagAdmin;
    private Date troubleshootDateFlagAdmin;
    private String troubleshootUserIdFlagAdmin;
}
