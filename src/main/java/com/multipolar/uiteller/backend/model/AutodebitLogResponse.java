package com.multipolar.uiteller.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutodebitLogResponse {

    private String trxName;
    private String trxDate;
    private Integer page;
    private Integer size;
    private String searchValue;
    private String orderColumn;
    private String orderDirection;
    private Integer totalPage;
    private Long totalRecords;
    private List<AutodebitLog> logList;

}
