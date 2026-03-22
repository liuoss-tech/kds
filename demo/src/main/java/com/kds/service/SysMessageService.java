package com.kds.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kds.model.dto.MessageMarkReadDTO;
import com.kds.model.dto.MessageQueryDTO;
import com.kds.model.dto.MessageSendDTO;
import com.kds.model.entity.SysMessage;
import com.kds.model.vo.ContactVO;
import com.kds.model.vo.ConversationVO;
import com.kds.model.vo.MessageVO;

import java.util.List;

public interface SysMessageService extends IService<SysMessage> {

    List<ConversationVO> getConversations();

    Page<MessageVO> getMessageHistory(MessageQueryDTO queryDTO);

    void sendMessage(MessageSendDTO sendDTO);

    void markRead(MessageMarkReadDTO markReadDTO);

    int getUnreadCount();

    List<ContactVO> getContacts();
}
