package com.kds.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleAssignDTO {
    private Long roleId;
    private List<Long> permissionIds;
}
