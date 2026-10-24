package com.multipolar.uiteller.backend.model;

import lombok.Data;

@Data
public class AutodebitLogRequest {

    private String trxName;
    private String trxDate;
    private Integer page;
    private Integer size;
    private String searchValue;
    private String orderColumn;
    private String orderDirection;

    //default value
    public AutodebitLogRequest() {
        this.page = 0;
        this.size = 10;
        this.orderColumn = "id";
        this.orderDirection = "desc";
    }

}
