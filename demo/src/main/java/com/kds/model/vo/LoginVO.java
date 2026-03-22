package com.kds.model.vo;

import lombok.Data;

@Data
public class LoginVO {
    private String token;
    private Long userId;
    private String realName;
    private Integer userType;
    private String roleCode;
}
