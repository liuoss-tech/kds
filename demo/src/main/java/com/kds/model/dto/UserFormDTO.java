package com.kds.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserFormDTO {
    private Long id;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "账号不能为空")
    private String username;

    private String password;

    @NotNull(message = "用户类型不能为空")
    private Integer userType;

    @NotNull(message = "角色不能为空")
    private Long roleId;
}
