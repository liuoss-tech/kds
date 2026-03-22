package com.kds.controller.busi;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kds.common.result.Result;
import com.kds.model.dto.MessageMarkReadDTO;
import com.kds.model.dto.MessageQueryDTO;
import com.kds.model.dto.MessageSendDTO;
import com.kds.model.vo.ContactVO;
import com.kds.model.vo.ConversationVO;
import com.kds.model.vo.MessageVO;
import com.kds.service.SysMessageService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/busi/message")
@SaCheckLogin
public class MessageController {

    @Resource
    private SysMessageService sysMessageService;

    @GetMapping("/conversations")
    public Result<List<ConversationVO>> getConversations() {
        return Result.success(sysMessageService.getConversations());
    }

    @GetMapping("/history")
    public Result<Page<MessageVO>> getMessageHistory(MessageQueryDTO queryDTO) {
        return Result.success(sysMessageService.getMessageHistory(queryDTO));
    }

    @PostMapping("/send")
    public Result<Void> sendMessage(@Validated @RequestBody MessageSendDTO sendDTO) {
        sysMessageService.sendMessage(sendDTO);
        return Result.success();
    }

    @PostMapping("/mark-read")
    public Result<Void> markRead(@Validated @RequestBody MessageMarkReadDTO markReadDTO) {
        sysMessageService.markRead(markReadDTO);
        return Result.success();
    }

    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount() {
        return Result.success(sysMessageService.getUnreadCount());
    }

    @GetMapping("/contacts")
    public Result<List<ContactVO>> getContacts() {
        return Result.success(sysMessageService.getContacts());
    }
}
