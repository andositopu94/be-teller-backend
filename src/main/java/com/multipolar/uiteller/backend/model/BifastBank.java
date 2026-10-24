package com.multipolar.uiteller.backend.model;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;

@Data
@Entity
@Table(name = "BIFAST_BANK")
@EntityListeners(AuditingEntityListener.class)
public class BifastBank {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BIFAST_BANK_GENERATOR")
    @SequenceGenerator(name = "BIFAST_BANK_GENERATOR", sequenceName = "BIFAST_BANK_ID_SEQUENCE", allocationSize = 0)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(name = "BANK_CODE")
    private String bankCode;

    @Column(name = "BANK_NAME")
    private String bankName;

    @Column(name = "BANK_BIC")
    private String bankBIC;

    @Column(name = "ACTIVE")
    private String active;

    @Column(name = "PLACEHOLDER1")
    private String placeholder1;

    @Column(name = "PLACEHOLDER2")
    private String placeholder2;

    @Column(name = "PLACEHOLDER3")
    private String placeholder3;
}
