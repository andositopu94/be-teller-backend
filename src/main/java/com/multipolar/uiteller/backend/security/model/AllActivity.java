package com.multipolar.uiteller.backend.security.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class AllActivity {

    private String userId;
    private BigDecimal debitCash;
    private BigDecimal creditCash;
    private BigDecimal debitOverbooking;
    private BigDecimal creditOverbooking;
    private BigDecimal debit;
    private BigDecimal credit;
}
