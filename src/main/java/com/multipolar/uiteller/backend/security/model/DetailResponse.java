package com.multipolar.uiteller.backend.security.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class DetailResponse {
    private String message;
    private String statusId;
    private DetailResult result;
    private String provinceId;
    private String cityId;
    private String eduId;
    private String marrId;
    private String jobId;
    private String workId;
    private String branchId;
    private String branchName;
    private String ref;
    @JsonProperty("rCode")
    private String rCode;
}
