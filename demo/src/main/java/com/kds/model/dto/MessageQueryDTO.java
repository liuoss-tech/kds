package com.kds.model.dto;

import lombok.Data;

@Data
public class MessageQueryDTO {
    private Long contactUserId;
    private Integer page = 1;
    private Integer size = 20;
}
