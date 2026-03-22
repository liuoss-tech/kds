package com.kds.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MessageMarkReadDTO {

    @NotNull(message = "联系人不能为空")
    private Long contactUserId;
}
