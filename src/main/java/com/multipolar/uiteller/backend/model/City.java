package com.multipolar.uiteller.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class City {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private long id;

	private String cityCode;

	private String name;

	private String coreCode;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="province_id", nullable=false)
	private Province province;
}
