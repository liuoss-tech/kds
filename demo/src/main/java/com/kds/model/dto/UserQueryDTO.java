package com.kds.model.dto;

import lombok.Data;

@Data
public class UserQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String realName;
    private String username;
}
