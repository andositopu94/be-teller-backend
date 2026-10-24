package com.multipolar.uiteller.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BpihUploadModel {
    @JsonProperty("RC")
    private String rc;
    private String message;
    @JsonProperty("no_validasi")
    private String noValidasi;
    private String token;

    public BpihUploadModel(String rc, String message) {
        this.rc = rc;
        this.message = message;
    }
}
