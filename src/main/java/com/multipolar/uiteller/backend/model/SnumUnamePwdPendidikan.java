package com.multipolar.uiteller.backend.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class SnumUnamePwdPendidikan {

    @Id
    private String archiveNumber;

    private String commonTransactionId;

    private String sourceNumber;

    private String username;

    private String password;

}
