package com.multipolar.uiteller.backend.security.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class Activity {

    private String archiveNumber;
    private String sourceNumber;
    private String sourceName;
    private String currency;
    private String description;
    private String transactionCode;
    private BigDecimal debit;
    private BigDecimal credit;

}
