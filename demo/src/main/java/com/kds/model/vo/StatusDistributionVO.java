package com.kds.model.vo;

import lombok.Data;

@Data
public class StatusDistributionVO {

    private Integer status;

    private String statusText;

    private Integer count;

    private Double rate;
}