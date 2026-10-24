package com.multipolar.uiteller.backend.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Where(clause="delflag=0")
public class Autodebit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private long id;
	
	private String userCreator;
	
	private String branchCode;
	
	private String transactionName;
	
	private String customerId;
	
	private String sourceNumber;
	
	private Integer startDate;
	
	private Integer endDate;
	
	@Column(name="user_comment")
	private String comment;
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;
    
    @Column(name="delflag")
    private int deleted = 0;
    
    private boolean charged;
    
    @JsonIgnore
    @OneToMany(mappedBy="autodebit")
    private List<AutodebitLog> autodebitLogs = new ArrayList<>();
    
    private String fillerText1;
    
    private String fillerValue1;
    
    private String fillerText2;
    
    private String fillerValue2;
    
    private String fillerText3;
    
    private String fillerValue3;
	
}
