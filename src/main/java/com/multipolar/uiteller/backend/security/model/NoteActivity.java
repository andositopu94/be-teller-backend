package com.multipolar.uiteller.backend.security.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class NoteActivity {

    private String branchName;
    private BigDecimal creditNominal;
    private String spell;
    private String description;
    private String user;
    private String transactionDate;
}
