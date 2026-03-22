package com.kds.model.vo;

import lombok.Data;

@Data
public class CurrentUserVO {
    private Long userId;
    private String realName;
    private Integer userType;
    private String roleCode;
    private String roleName;
}
