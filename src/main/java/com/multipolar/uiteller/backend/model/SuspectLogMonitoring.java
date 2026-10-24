package com.multipolar.uiteller.backend.model;



import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class SuspectLogMonitoring {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Long id;
	
    private String status;
    
    private String description;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "common_transaction_log_id")
    private CommonTransactionLog commonTransactionLog;

    @JsonIgnore
    @OneToMany(mappedBy = "suspectLogMonitoring")
    private List<SuspectLogAction> transactionLogs = new ArrayList<>();

    public SuspectLogMonitoring() {}
    
    public SuspectLogMonitoring(String status, String description, CommonTransactionLog commonLog) {
    	this.status = status;
    	this.description = description;
    	this.commonTransactionLog = commonLog;
    }
}
