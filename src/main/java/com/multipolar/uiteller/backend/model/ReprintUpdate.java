package com.multipolar.uiteller.backend.model;

import lombok.Data;

import java.util.List;

@Data
public class ReprintUpdate {

    private Long id;

    private String type;

    private String trace;

    private List<TransactionLog> result;
}
