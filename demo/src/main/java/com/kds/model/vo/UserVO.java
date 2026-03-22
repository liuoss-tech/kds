package com.kds.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserVO {
    private Long id;
    private String realName;
    private String username;
    private Integer userType;
    private Long roleId;
    private String roleName;
    private Integer status;
    private Date createTime;
}
