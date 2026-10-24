package com.multipolar.uiteller.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class CommonTransactionSiskoRequestLog {
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
	@OneToMany(mappedBy="commonTransactionSiskoRequestLog")
	private List<TransactionSiskoRequestLog> transactionSiskoRequestLogs = new ArrayList<>();
}
