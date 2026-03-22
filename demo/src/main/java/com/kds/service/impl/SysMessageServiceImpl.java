package com.kds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kds.common.context.LoginHelper;
import com.kds.common.exception.BusinessException;
import com.kds.mapper.BusiClassMapper;
import com.kds.mapper.BusiStudentMapper;
import com.kds.mapper.BusiStudentParentMapper;
import com.kds.mapper.BusiTeacherClassMapper;
import com.kds.mapper.SysMessageMapper;
import com.kds.mapper.SysUserMapper;
import com.kds.model.dto.MessageMarkReadDTO;
import com.kds.model.dto.MessageQueryDTO;
import com.kds.model.dto.MessageSendDTO;
import com.kds.model.entity.BusiClass;
import com.kds.model.entity.BusiStudent;
import com.kds.model.entity.BusiStudentParent;
import com.kds.model.entity.BusiTeacherClass;
import com.kds.model.entity.SysMessage;
import com.kds.model.entity.SysUser;
import com.kds.model.vo.ContactVO;
import com.kds.model.vo.ConversationVO;
import com.kds.model.vo.MessageVO;
import com.kds.service.SysMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements SysMessageService {

    @Resource
    private LoginHelper loginHelper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private BusiTeacherClassMapper busiTeacherClassMapper;

    @Resource
    private BusiStudentMapper busiStudentMapper;

    @Resource
    private BusiStudentParentMapper busiStudentParentMapper;

    @Resource
    private BusiClassMapper busiClassMapper;

    private static final Integer MESSAGE_TYPE_PRIVATE = 2;

    @Override
    public List<ConversationVO> getConversations() {
        Long userId = loginHelper.getUserId();
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMessage::getIsDeleted, 0)
                .and(w -> w.eq(SysMessage::getSenderId, userId).or().eq(SysMessage::getReceiverId, userId))
                .orderByDesc(SysMessage::getCreateTime);
        List<SysMessage> messages = this.list(wrapper);

        Map<Long, ConversationVO> conversationMap = new HashMap<>();
        for (SysMessage msg : messages) {
            Long contactUserId = msg.getSenderId().equals(userId) ? msg.getReceiverId() : msg.getSenderId();
            if (conversationMap.containsKey(contactUserId)) {
                continue;
            }
            SysUser contactUser = sysUserMapper.selectById(contactUserId);
            if (contactUser == null) {
                continue;
            }

            ConversationVO vo = new ConversationVO();
            vo.setContactUserId(contactUserId);
            vo.setContactUserName(contactUser.getRealName());
            vo.setContactUserType(contactUser.getUserType());
            vo.setLastMessage(msg.getContent());
            vo.setLastMessageTime(msg.getCreateTime());
            vo.setUnreadCount((int) messages.stream()
                    .filter(m -> m.getSenderId().equals(contactUserId)
                            && m.getReceiverId().equals(userId)
                            && m.getIsRead() != null && m.getIsRead() == 0)
                    .count());
            conversationMap.put(contactUserId, vo);
        }

        return new ArrayList<>(conversationMap.values());
    }

    @Override
    public Page<MessageVO> getMessageHistory(MessageQueryDTO queryDTO) {
        Long userId = loginHelper.getUserId();
        Long contactUserId = queryDTO.getContactUserId();
        if (contactUserId == null) {
            throw new BusinessException("联系人不能为空");
        }

        int pageNum = queryDTO.getPage() != null ? queryDTO.getPage() : 1;
        int pageSize = queryDTO.getSize() != null ? queryDTO.getSize() : 20;
        int offset = (pageNum - 1) * pageSize;

        List<SysMessage> messages = baseMapper.selectMessageHistory(userId, contactUserId, offset, pageSize);

        long total = count(new LambdaQueryWrapper<SysMessage>()
                .eq(SysMessage::getIsDeleted, 0)
                .and(w -> w.eq(SysMessage::getSenderId, userId).eq(SysMessage::getReceiverId, contactUserId)
                        .or().eq(SysMessage::getSenderId, contactUserId).eq(SysMessage::getReceiverId, userId)));

        SysUser contactUser = sysUserMapper.selectById(contactUserId);
        String contactUserName = contactUser != null ? contactUser.getRealName() : "";

        List<MessageVO> voList = messages.stream().map(msg -> {
            MessageVO vo = new MessageVO();
            vo.setId(msg.getId());
            vo.setSenderId(msg.getSenderId());
            vo.setSenderName(msg.getSenderId().equals(userId) ? "我" : contactUserName);
            vo.setReceiverId(msg.getReceiverId());
            vo.setReceiverName(msg.getReceiverId().equals(userId) ? "我" : contactUserName);
            vo.setContent(msg.getContent());
            vo.setIsRead(msg.getIsRead() != null && msg.getIsRead() == 1);
            vo.setCreateTime(msg.getCreateTime());
            return vo;
        }).collect(Collectors.toList());

        Page<MessageVO> page = new Page<>(pageNum, pageSize);
        page.setTotal(total);
        page.setRecords(voList);
        return page;
    }

    @Override
    @Transactional
    public void sendMessage(MessageSendDTO sendDTO) {
        Long userId = loginHelper.getUserId();
        Long receiverId = sendDTO.getReceiverId();

        if (receiverId.equals(userId)) {
            throw new BusinessException("不能给自己发送消息");
        }

        if (!StringUtils.hasText(sendDTO.getContent())) {
            throw new BusinessException("消息内容不能为空");
        }

        boolean canSend = checkCanSendToUser(userId, receiverId);
        if (!canSend) {
            throw new BusinessException("您没有权限向该用户发送消息");
        }

        SysUser receiver = sysUserMapper.selectById(receiverId);
        if (receiver == null) {
            throw new BusinessException("接收者不存在");
        }

        SysMessage message = new SysMessage();
        message.setSenderId(userId);
        message.setReceiverId(receiverId);
        message.setMessageType(MESSAGE_TYPE_PRIVATE);
        message.setContent(sendDTO.getContent());
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        this.save(message);
    }

    @Override
    @Transactional
    public void markRead(MessageMarkReadDTO markReadDTO) {
        Long userId = loginHelper.getUserId();
        Long contactUserId = markReadDTO.getContactUserId();

        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMessage::getIsDeleted, 0)
                .eq(SysMessage::getSenderId, contactUserId)
                .eq(SysMessage::getReceiverId, userId)
                .eq(SysMessage::getIsRead, 0);

        List<SysMessage> unreadMessages = this.list(wrapper);
        if (unreadMessages != null && !unreadMessages.isEmpty()) {
            for (SysMessage msg : unreadMessages) {
                msg.setIsRead(1);
                msg.setReadTime(LocalDateTime.now());
            }
            this.updateBatchById(unreadMessages);
        }
    }

    @Override
    public int getUnreadCount() {
        Long userId = loginHelper.getUserId();
        return baseMapper.countTotalUnread(userId);
    }

    @Override
    public List<ContactVO> getContacts() {
        if (loginHelper.isTeacher()) {
            return getTeacherContacts();
        } else {
            return getParentContacts();
        }
    }

    private List<ContactVO> getTeacherContacts() {
        Long userId = loginHelper.getUserId();
        List<Long> classIds = loginHelper.getClassIds();
        if (classIds == null || classIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<ContactVO> result = new ArrayList<>();
        for (Long classId : classIds) {
            BusiClass busiClass = busiClassMapper.selectById(classId);
            if (busiClass == null) {
                continue;
            }

            LambdaQueryWrapper<BusiStudent> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.eq(BusiStudent::getClassId, classId)
                    .eq(BusiStudent::getIsDeleted, 0);
            List<BusiStudent> students = busiStudentMapper.selectList(studentWrapper);

            for (BusiStudent student : students) {
                LambdaQueryWrapper<BusiStudentParent> parentWrapper = new LambdaQueryWrapper<>();
                parentWrapper.eq(BusiStudentParent::getStudentId, student.getId());
                List<BusiStudentParent> parentRelations = busiStudentParentMapper.selectList(parentWrapper);

                for (BusiStudentParent relation : parentRelations) {
                    SysUser parent = sysUserMapper.selectById(relation.getParentUserId());
                    if (parent == null || parent.getIsDeleted() != null && parent.getIsDeleted() == 1) {
                        continue;
                    }

                    ContactVO.ParentVO parentVO = new ContactVO.ParentVO();
                    parentVO.setUserId(parent.getId());
                    parentVO.setRealName(parent.getRealName());
                    parentVO.setRelation(relation.getRelation());

                    ContactVO contactVO = new ContactVO();
                    contactVO.setClassId(classId);
                    contactVO.setClassName(busiClass.getClassName());
                    contactVO.setStudentId(student.getId());
                    contactVO.setStudentName(student.getStudentName());
                    contactVO.setParents(Collections.singletonList(parentVO));
                    result.add(contactVO);
                }
            }
        }
        return result;
    }

    private List<ContactVO> getParentContacts() {
        Long userId = loginHelper.getUserId();
        List<Long> childIds = loginHelper.getChildIds();
        if (childIds == null || childIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<ContactVO> result = new ArrayList<>();
        for (Long childId : childIds) {
            BusiStudent student = busiStudentMapper.selectById(childId);
            if (student == null) {
                continue;
            }

            BusiClass busiClass = busiClassMapper.selectById(student.getClassId());
            if (busiClass == null) {
                continue;
            }

            LambdaQueryWrapper<BusiTeacherClass> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(BusiTeacherClass::getClassId, student.getClassId());
            List<BusiTeacherClass> teacherRelations = busiTeacherClassMapper.selectList(teacherWrapper);

            List<ContactVO.TeacherVO> teachers = new ArrayList<>();
            for (BusiTeacherClass relation : teacherRelations) {
                SysUser teacher = sysUserMapper.selectById(relation.getTeacherUserId());
                if (teacher == null || teacher.getIsDeleted() != null && teacher.getIsDeleted() == 1) {
                    continue;
                }

                ContactVO.TeacherVO teacherVO = new ContactVO.TeacherVO();
                teacherVO.setUserId(teacher.getId());
                teacherVO.setRealName(teacher.getRealName());
                teacherVO.setIsHeadTeacher(relation.getIsHeadTeacher() != null && relation.getIsHeadTeacher() == 1);
                teachers.add(teacherVO);
            }

            if (!teachers.isEmpty()) {
                ContactVO contactVO = new ContactVO();
                contactVO.setClassId(student.getClassId());
                contactVO.setClassName(busiClass.getClassName());
                contactVO.setStudentId(student.getId());
                contactVO.setStudentName(student.getStudentName());
                contactVO.setTeachers(teachers);
                result.add(contactVO);
            }
        }
        return result;
    }

    private boolean checkCanSendToUser(Long senderId, Long receiverId) {
        SysUser sender = sysUserMapper.selectById(senderId);
        SysUser receiver = sysUserMapper.selectById(receiverId);
        if (sender == null || receiver == null) {
            return false;
        }

        if (sender.getUserType() == 1) {
            return checkTeacherCanSendToParent(senderId, receiverId);
        } else if (sender.getUserType() == 2) {
            return checkParentCanSendToTeacher(senderId, receiverId);
        }
        return false;
    }

    private boolean checkTeacherCanSendToParent(Long teacherId, Long parentId) {
        List<Long> classIds = loginHelper.getClassIds();
        if (classIds == null || classIds.isEmpty()) {
            return false;
        }

        LambdaQueryWrapper<BusiTeacherClass> teacherWrapper = new LambdaQueryWrapper<>();
        teacherWrapper.eq(BusiTeacherClass::getTeacherUserId, teacherId)
                .in(BusiTeacherClass::getClassId, classIds);
        List<BusiTeacherClass> teacherClasses = busiTeacherClassMapper.selectList(teacherWrapper);
        List<Long> teacherClassIds = teacherClasses.stream()
                .map(BusiTeacherClass::getClassId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<BusiStudent> studentWrapper = new LambdaQueryWrapper<>();
        studentWrapper.in(BusiStudent::getClassId, teacherClassIds)
                .eq(BusiStudent::getIsDeleted, 0);
        List<BusiStudent> students = busiStudentMapper.selectList(studentWrapper);
        List<Long> studentIds = students.stream()
                .map(BusiStudent::getId)
                .collect(Collectors.toList());

        if (studentIds.isEmpty()) {
            return false;
        }

        LambdaQueryWrapper<BusiStudentParent> parentWrapper = new LambdaQueryWrapper<>();
        parentWrapper.eq(BusiStudentParent::getParentUserId, parentId)
                .in(BusiStudentParent::getStudentId, studentIds);
        return busiStudentParentMapper.selectCount(parentWrapper) > 0;
    }

    private boolean checkParentCanSendToTeacher(Long parentId, Long teacherId) {
        List<Long> childIds = loginHelper.getChildIds();
        if (childIds == null || childIds.isEmpty()) {
            return false;
        }

        LambdaQueryWrapper<BusiStudent> studentWrapper = new LambdaQueryWrapper<>();
        studentWrapper.in(BusiStudent::getId, childIds)
                .eq(BusiStudent::getIsDeleted, 0);
        List<BusiStudent> children = busiStudentMapper.selectList(studentWrapper);
        List<Long> childClassIds = children.stream()
                .map(BusiStudent::getClassId)
                .distinct()
                .collect(Collectors.toList());

        LambdaQueryWrapper<BusiTeacherClass> teacherWrapper = new LambdaQueryWrapper<>();
        teacherWrapper.eq(BusiTeacherClass::getTeacherUserId, teacherId)
                .in(BusiTeacherClass::getClassId, childClassIds);
        return busiTeacherClassMapper.selectCount(teacherWrapper) > 0;
    }
}
