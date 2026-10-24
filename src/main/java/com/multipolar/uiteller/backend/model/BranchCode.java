package com.multipolar.uiteller.backend.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class BranchCode {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private long id;

	private String branchCode;

	private String name;

	private String address;

}
