package com.kds.model.dto;

import lombok.Data;

@Data
public class RoleQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String roleName;
    private String roleCode;
}
