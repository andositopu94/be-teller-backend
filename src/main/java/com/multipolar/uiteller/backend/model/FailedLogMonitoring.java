package com.multipolar.uiteller.backend.model;

import com.multipolar.uiteller.backend.resource.Troubleshoot;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class FailedLogMonitoring {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Long id;
    private String status;
    private String description;
    @Enumerated(EnumType.ORDINAL)
    private Troubleshoot troubleshoot;
    @Temporal(TemporalType.TIMESTAMP)
    private Date troubleshootDate;
    private String troubleshootUserId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "common_transaction_log_id")
    private CommonTransactionLog commonTransactionLog;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "common_transaction_request_log_id")
    private CommonTransactionRequestLog commonTransactionRequestLog;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeout_resource_id")
    private TimeoutResource timeoutResource;
    @Enumerated(EnumType.ORDINAL)
    private Troubleshoot troubleshootFlagAdmin;
    @Temporal(TemporalType.TIMESTAMP)
    private Date troubleshootDateFlagAdmin;
    private String troubleshootUserIdFlagAdmin;

    public FailedLogMonitoring() {
    }

    public FailedLogMonitoring(Troubleshoot troubleshoot, CommonTransactionLog commonTransactionLog, CommonTransactionRequestLog commonTransactionRequestLog, TimeoutResource timeoutResource, Troubleshoot flag) {
        this.troubleshoot = troubleshoot;
        this.commonTransactionLog = commonTransactionLog;
        this.commonTransactionRequestLog = commonTransactionRequestLog;
        this.timeoutResource = timeoutResource;
        this.troubleshootFlagAdmin = flag;
    }
}
