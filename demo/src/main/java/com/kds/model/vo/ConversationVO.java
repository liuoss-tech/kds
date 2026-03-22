package com.kds.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationVO {

    private Long contactUserId;

    private String contactUserName;

    private Integer contactUserType;

    private String lastMessage;

    private LocalDateTime lastMessageTime;

    private Integer unreadCount;
}
