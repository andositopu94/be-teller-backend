package com.multipolar.uiteller.backend.model;

import lombok.Data;

import java.util.List;

@Data
public class CorrectionUpdate {

    private Long id;

    private String type;

    private List<TransactionRequestLog> result;
}
