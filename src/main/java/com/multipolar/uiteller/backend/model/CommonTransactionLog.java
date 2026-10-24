package com.multipolar.uiteller.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Audited
public class CommonTransactionLog {
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

    @JsonIgnore
    @OneToMany(mappedBy = "commonTransaction")
    private List<TransactionLog> transactionLogs = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "commonTransactionLog")
    private FailedLogMonitoring failedLogMonitoring;
}
