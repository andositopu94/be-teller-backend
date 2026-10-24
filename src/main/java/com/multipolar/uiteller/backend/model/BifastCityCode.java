package com.multipolar.uiteller.backend.model;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@Entity
@Table(name = "BIFAST_CITY_CODE")
@EntityListeners(AuditingEntityListener.class)
public class BifastCityCode {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BIFAST_CITY_CODE_GENERATOR")
    @SequenceGenerator(name = "BIFAST_CITY_CODE_GENERATOR", sequenceName = "BIFAST_CITY_CODE_ID_SEQUENCE", allocationSize = 0)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(name = "CITY_NAME")
    private String cityName;

    @Column(name = "CITY_CODE")
    private String cityCode;

    @Column(name = "DATA1")
    private String data1;

    @Column(name = "DATA2")
    private String data2;

    @Column(name = "DATA3")
    private String data3;
}
