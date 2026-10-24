package com.multipolar.uiteller.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class CommonTransactionRequestLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date trxDate;

    private String userid;

    private String branchCode;

    private int status;

    private String message;

    private String rc;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "commonTransactionRequestLog")
    private FailedLogMonitoring failedLogMonitoring;

    @JsonIgnore
    @OneToMany(mappedBy = "commonTransactionRequest")
    private List<TransactionRequestLog> transactionRequestLogs = new ArrayList<>();

    @Override
    public String toString() {
        return "CommonTransactionRequestLog{" +
                "id=" + id +
                ", trxDate=" + trxDate +
                ", userid='" + userid + '\'' +
                ", branchCode='" + branchCode + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", rc='" + rc + '\'' +
                '}';
    }
}
