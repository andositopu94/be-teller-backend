package com.multipolar.uiteller.backend.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class UniversitasLog {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private long id;
	
	private String fieldName;
	
	private String fieldValue1;
	
	private String fieldValue2;
	
	private String statusTampil;
	
	private Integer bit;
	
	private String archiveNumber;
		
}
