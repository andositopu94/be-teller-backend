package com.multipolar.uiteller.backend.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Job {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private long id;

	private String jobCode;

	private String name;

	private String coreJob;

	private String coreName;
}
