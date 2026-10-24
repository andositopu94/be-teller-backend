package com.multipolar.uiteller.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Province {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private long id;
	
	private String provinceCode;
	
	private String name;

	private String coreCode;
	
	@JsonIgnore
	@OneToMany(mappedBy="province")
	private List<City> cities = new ArrayList<>();
}
