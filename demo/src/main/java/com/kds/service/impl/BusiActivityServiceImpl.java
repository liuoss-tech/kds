package com.kds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kds.common.context.LoginHelper;
import com.kds.common.exception.BusinessException;
import com.kds.mapper.BusiActivityMapper;
import com.kds.mapper.BusiActivityRegistrationMapper;
import com.kds.mapper.BusiClassMapper;
import com.kds.mapper.BusiStudentMapper;
import com.kds.mapper.BusiStudentParentMapper;
import com.kds.mapper.SysUserMapper;
import com.kds.model.dto.ActivityFormDTO;
import com.kds.model.entity.BusiActivity;
import com.kds.model.entity.BusiActivityRegistration;
import com.kds.model.entity.BusiClass;
import com.kds.model.entity.BusiStudent;
import com.kds.model.entity.SysUser;
import com.kds.model.vo.ActivityDetailVO;
import com.kds.model.vo.ActivityVO;
import com.kds.model.vo.RegistrationVO;
import com.kds.service.BusiActivityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BusiActivityServiceImpl extends ServiceImpl<BusiActivityMapper, BusiActivity> implements BusiActivityService {

    @Autowired
    private LoginHelper loginHelper;

    @Autowired
    private BusiActivityMapper busiActivityMapper;

    @Autowired
    private BusiActivityRegistrationMapper busiActivityRegistrationMapper;

    @Autowired
    private BusiClassMapper busiClassMapper;

    @Autowired
    private BusiStudentMapper busiStudentMapper;

    @Autowired
    private BusiStudentParentMapper busiStudentParentMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    private static final Integer STATUS_REGISTRATION_OPEN = 1;
    private static final Integer STATUS_REGISTRATION_CLOSED = 2;
    private static final Integer STATUS_IN_PROGRESS = 3;
    private static final Integer STATUS_ENDED = 4;

    private static final Integer REGISTRATION_STATUS_NORMAL = 1;
    private static final Integer REGISTRATION_STATUS_CANCELLED = 2;

    @Override
    public Page<ActivityVO> getTeacherActivityPage(Integer status, Integer page, Integer size) {
        if (!loginHelper.isTeacher()) {
            throw new BusinessException("只有教师才能查看活动列表");
        }

        Long userId = loginHelper.getUserId();
        Page<BusiActivity> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<BusiActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiActivity::getIsDeleted, 0);
        wrapper.eq(BusiActivity::getPublisherId, userId);

        wrapper.orderByDesc(BusiActivity::getCreateTime);

        Page<BusiActivity> activityPage = this.page(pageParam, wrapper);
        Page<ActivityVO> voPage = convertToActivityVOPage(activityPage);

        if (status != null) {
            voPage.setRecords(voPage.getRecords().stream()
                    .filter(vo -> vo.getStatus().equals(status))
                    .collect(Collectors.toList()));
            voPage.setTotal(voPage.getRecords().size());
        }

        return voPage;
    }

    @Override
    public Page<ActivityVO> getParentActivityPage(String tab, Integer page, Integer size) {
        if (!loginHelper.isParent()) {
            throw new BusinessException("只有家长才能查看活动列表");
        }

        List<Long> childIds = loginHelper.getChildIds();
        if (childIds == null || childIds.isEmpty()) {
            return new Page<>();
        }

        List<Long> classIds = busiStudentMapper.selectBatchIds(childIds).stream()
                .map(BusiStudent::getClassId)
                .distinct()
                .collect(Collectors.toList());

        if (classIds.isEmpty()) {
            return new Page<>();
        }

        Page<BusiActivity> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<BusiActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiActivity::getIsDeleted, 0);
        wrapper.in(BusiActivity::getTargetClassId, classIds);

        wrapper.orderByDesc(BusiActivity::getCreateTime);

        Page<BusiActivity> activityPage = this.page(pageParam, wrapper);
        Page<ActivityVO> voPage = convertToActivityVOPage(activityPage);

        Long userId = loginHelper.getUserId();
        List<Long> activityIds = voPage.getRecords().stream()
                .map(ActivityVO::getId)
                .collect(Collectors.toList());

        List<Long> registeredActivityIds;
        if (!activityIds.isEmpty() && userId != null) {
            LambdaQueryWrapper<BusiActivityRegistration> regWrapper = new LambdaQueryWrapper<>();
            regWrapper.eq(BusiActivityRegistration::getParentUserId, userId);
            regWrapper.eq(BusiActivityRegistration::getStatus, REGISTRATION_STATUS_NORMAL);
            regWrapper.in(BusiActivityRegistration::getActivityId, activityIds);
            List<BusiActivityRegistration> myRegs = busiActivityRegistrationMapper.selectList(regWrapper);
            registeredActivityIds = myRegs.stream()
                    .map(BusiActivityRegistration::getActivityId)
                    .collect(Collectors.toList());
        } else {
            registeredActivityIds = new java.util.ArrayList<>();
        }

        final List<Long> finalRegisteredIds = registeredActivityIds;
        voPage.getRecords().forEach(vo -> {
            vo.setIsRegistered(finalRegisteredIds.contains(vo.getId()));
        });

        if ("ongoing".equals(tab)) {
            voPage.setRecords(voPage.getRecords().stream()
                    .filter(vo -> vo.getStatus().equals(STATUS_REGISTRATION_OPEN) || finalRegisteredIds.contains(vo.getId()))
                    .collect(Collectors.toList()));
        } else if ("history".equals(tab)) {
            voPage.setRecords(voPage.getRecords().stream()
                    .filter(vo -> vo.getStatus().equals(STATUS_ENDED))
                    .collect(Collectors.toList()));
        }
        voPage.setTotal(voPage.getRecords().size());

        return voPage;
    }

    @Override
    @Transactional
    public void publishActivity(ActivityFormDTO formDTO) {
        if (!loginHelper.isTeacher()) {
            throw new BusinessException("只有教师才能发布活动");
        }

        List<Long> classIds = loginHelper.getClassIds();
        if (classIds == null || classIds.isEmpty()) {
            throw new BusinessException("您还没有分配班级，无法发布活动");
        }

        if (formDTO.getTargetClassId() == null || !classIds.contains(formDTO.getTargetClassId())) {
            throw new BusinessException("您只能为自己的班级发布活动");
        }

        if (formDTO.getDeadlineTime().isAfter(formDTO.getActivityTime())) {
            throw new BusinessException("报名截止时间不能晚于活动时间");
        }

        BusiActivity activity = new BusiActivity();
        BeanUtils.copyProperties(formDTO, activity);
        activity.setPublisherId(loginHelper.getUserId());

        this.save(activity);
    }

    @Override
    public ActivityDetailVO getActivityDetail(Long id) {
        BusiActivity activity = this.getById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        ActivityDetailVO vo = new ActivityDetailVO();
        BeanUtils.copyProperties(activity, vo);

        vo.setStatus(calculateActivityStatus(activity));
        vo.setStatusText(getStatusText(vo.getStatus()));
        vo.setActivityTypeText(getActivityTypeText(activity.getActivityType()));

        if (activity.getTargetClassId() != null) {
            BusiClass busiClass = busiClassMapper.selectById(activity.getTargetClassId());
            if (busiClass != null) {
                vo.setTargetClassName(busiClass.getClassName());
            }
        }

        if (activity.getPublisherId() != null) {
            SysUser publisher = sysUserMapper.selectById(activity.getPublisherId());
            if (publisher != null) {
                vo.setPublisherName(publisher.getRealName());
            }
        }

        try {
            if (loginHelper != null && loginHelper.isParent()) {
                Long userId = loginHelper.getUserId();
                if (userId != null) {
                    LambdaQueryWrapper<BusiActivityRegistration> regWrapper = new LambdaQueryWrapper<>();
                    regWrapper.eq(BusiActivityRegistration::getActivityId, id);
                    regWrapper.eq(BusiActivityRegistration::getParentUserId, userId);
                    regWrapper.eq(BusiActivityRegistration::getStatus, REGISTRATION_STATUS_NORMAL);
                    regWrapper.orderByDesc(BusiActivityRegistration::getCreateTime);
                    List<BusiActivityRegistration> registrations = busiActivityRegistrationMapper.selectList(regWrapper);
                    if (registrations != null && !registrations.isEmpty()) {
                        List<RegistrationVO> regVOList = registrations.stream().map(registration -> {
                            RegistrationVO regVO = new RegistrationVO();
                            BeanUtils.copyProperties(registration, regVO);

                            BusiStudent student = busiStudentMapper.selectById(registration.getStudentId());
                            if (student != null) {
                                regVO.setStudentName(student.getStudentName());
                            }

                            SysUser parentUser = sysUserMapper.selectById(userId);
                            if (parentUser != null) {
                                regVO.setParentRelation(parentUser.getRealName());
                            }

                            return regVO;
                        }).collect(Collectors.toList());
                        vo.setMyRegistrations(regVOList);
                    }
                }
            }
        } catch (Exception e) {
            // 忽略查询报名的错误，不影响活动详情返回
        }

        return vo;
    }

    @Override
    public List<RegistrationVO> getActivityRegistrations(Long activityId) {
        BusiActivity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        LambdaQueryWrapper<BusiActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiActivityRegistration::getActivityId, activityId);
        wrapper.eq(BusiActivityRegistration::getStatus, REGISTRATION_STATUS_NORMAL);
        wrapper.orderByDesc(BusiActivityRegistration::getCreateTime);

        List<BusiActivityRegistration> registrations = busiActivityRegistrationMapper.selectList(wrapper);
        if (registrations.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> studentIds = registrations.stream()
                .map(BusiActivityRegistration::getStudentId)
                .collect(Collectors.toList());
        List<Long> parentUserIds = registrations.stream()
                .map(BusiActivityRegistration::getParentUserId)
                .collect(Collectors.toList());

        Map<Long, BusiStudent> studentMap = null;
        if (!studentIds.isEmpty()) {
            List<BusiStudent> students = busiStudentMapper.selectBatchIds(studentIds);
            studentMap = students.stream()
                    .collect(Collectors.toMap(BusiStudent::getId, s -> s));
        }

        Map<Long, SysUser> userMap = null;
        if (!parentUserIds.isEmpty()) {
            List<SysUser> users = sysUserMapper.selectBatchIds(parentUserIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(SysUser::getId, u -> u));
        }

        Map<Long, String> relationMap = null;
        if (!studentIds.isEmpty() && !parentUserIds.isEmpty()) {
            LambdaQueryWrapper<com.kds.model.entity.BusiStudentParent> relationWrapper = new LambdaQueryWrapper<>();
            relationWrapper.in(com.kds.model.entity.BusiStudentParent::getStudentId, studentIds);
            relationWrapper.in(com.kds.model.entity.BusiStudentParent::getParentUserId, parentUserIds);
            List<com.kds.model.entity.BusiStudentParent> relations = busiStudentParentMapper.selectList(relationWrapper);
            relationMap = relations.stream()
                    .collect(Collectors.toMap(
                            r -> r.getStudentId() * 1000 + r.getParentUserId(),
                            com.kds.model.entity.BusiStudentParent::getRelation
                    ));
        }

        final Map<Long, BusiStudent> finalStudentMap = studentMap;
        final Map<Long, SysUser> finalUserMap = userMap;
        final Map<Long, String> finalRelationMap = relationMap;

        return registrations.stream().map(reg -> {
            RegistrationVO vo = new RegistrationVO();
            BeanUtils.copyProperties(reg, vo);

            if (finalStudentMap != null) {
                BusiStudent student = finalStudentMap.get(reg.getStudentId());
                if (student != null) {
                    vo.setStudentName(student.getStudentName());
                }
            }

            if (finalUserMap != null) {
                SysUser parentUser = finalUserMap.get(reg.getParentUserId());
                if (parentUser != null) {
                    vo.setParentRelation(parentUser.getRealName());
                }
            }

            if (finalRelationMap != null) {
                String relation = finalRelationMap.get(reg.getStudentId() * 1000 + reg.getParentUserId());
                vo.setParentRelation(relation);
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void registerActivity(Long activityId, Long studentId, Integer participantCount, String remark) {
        if (!loginHelper.isParent()) {
            throw new BusinessException("只有家长才能报名活动");
        }

        List<Long> childIds = loginHelper.getChildIds();
        if (childIds == null || childIds.isEmpty()) {
            throw new BusinessException("您还没有绑定幼儿，无法报名活动");
        }

        if (!childIds.contains(studentId)) {
            throw new BusinessException("您只能为自己的孩子报名活动");
        }

        BusiActivity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        int status = calculateActivityStatus(activity);
        if (status != STATUS_REGISTRATION_OPEN) {
            throw new BusinessException("当前无法报名，请查看活动状态");
        }

        Long userId = loginHelper.getUserId();

        LambdaQueryWrapper<BusiActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiActivityRegistration::getActivityId, activityId);
        wrapper.eq(BusiActivityRegistration::getStudentId, studentId);
        wrapper.eq(BusiActivityRegistration::getStatus, REGISTRATION_STATUS_NORMAL);
        wrapper.eq(BusiActivityRegistration::getIsDeleted, 0);

        BusiActivityRegistration existReg = busiActivityRegistrationMapper.selectOne(wrapper);
        if (existReg != null) {
            throw new BusinessException("该幼儿已报名此活动，请勿重复报名");
        }

        LambdaQueryWrapper<BusiActivityRegistration> cancelledWrapper = new LambdaQueryWrapper<>();
        cancelledWrapper.eq(BusiActivityRegistration::getActivityId, activityId);
        cancelledWrapper.eq(BusiActivityRegistration::getStudentId, studentId);
        cancelledWrapper.eq(BusiActivityRegistration::getParentUserId, userId);
        List<BusiActivityRegistration> cancelledRegs = busiActivityRegistrationMapper.selectList(cancelledWrapper);

        if (cancelledRegs != null && !cancelledRegs.isEmpty()) {
            BusiActivityRegistration cancelledReg = cancelledRegs.get(0);
            cancelledReg.setStatus(REGISTRATION_STATUS_NORMAL);
            cancelledReg.setRemark(remark);
            cancelledReg.setParticipantCount(participantCount != null ? participantCount : 1);
            cancelledReg.setIsDeleted(0);
            busiActivityRegistrationMapper.updateById(cancelledReg);
        } else {
            BusiActivityRegistration registration = new BusiActivityRegistration();
            registration.setActivityId(activityId);
            registration.setStudentId(studentId);
            registration.setParentUserId(userId);
            registration.setParticipantCount(participantCount != null ? participantCount : 1);
            registration.setRemark(remark);
            registration.setStatus(REGISTRATION_STATUS_NORMAL);
            registration.setIsDeleted(0);
            busiActivityRegistrationMapper.insert(registration);
        }
    }

    @Override
    @Transactional
    public void cancelRegistration(Long registrationId) {
        Long userId = loginHelper.getUserId();

        BusiActivityRegistration registration = busiActivityRegistrationMapper.selectById(registrationId);
        if (registration == null) {
            throw new BusinessException("报名记录不存在");
        }

        if (!registration.getParentUserId().equals(userId)) {
            throw new BusinessException("您只能取消自己的报名");
        }

        registration.setStatus(REGISTRATION_STATUS_CANCELLED);
        registration.setIsDeleted(1);
        busiActivityRegistrationMapper.updateById(registration);
    }

    @Override
    public List<Long> getAvailableStudentIds(Long activityId) {
        BusiActivity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        if (!loginHelper.isParent()) {
            throw new BusinessException("只有家长才能查看可报名幼儿列表");
        }

        List<Long> childIds = loginHelper.getChildIds();
        if (childIds == null || childIds.isEmpty()) {
            return new ArrayList<>();
        }

        if (activity.getTargetClassId() != null) {
            List<BusiStudent> childrenInClass = busiStudentMapper.selectList(
                    new LambdaQueryWrapper<BusiStudent>()
                            .eq(BusiStudent::getClassId, activity.getTargetClassId())
                            .eq(BusiStudent::getStatus, 1)
                            .eq(BusiStudent::getIsDeleted, 0)
            );

            List<Long> childrenInClassIds = childrenInClass.stream()
                    .map(BusiStudent::getId)
                    .collect(Collectors.toList());

            childIds = childIds.stream()
                    .filter(childrenInClassIds::contains)
                    .collect(Collectors.toList());
        }

        LambdaQueryWrapper<BusiActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiActivityRegistration::getActivityId, activityId);
        wrapper.eq(BusiActivityRegistration::getStatus, REGISTRATION_STATUS_NORMAL);
        wrapper.eq(BusiActivityRegistration::getIsDeleted, 0);
        wrapper.in(BusiActivityRegistration::getStudentId, childIds);

        List<BusiActivityRegistration> registeredRegs = busiActivityRegistrationMapper.selectList(wrapper);
        List<Long> registeredStudentIds = registeredRegs.stream()
                .map(BusiActivityRegistration::getStudentId)
                .collect(Collectors.toList());

        return childIds.stream()
                .filter(id -> !registeredStudentIds.contains(id))
                .collect(Collectors.toList());
    }

    @Override
    public int calculateActivityStatus(BusiActivity activity) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadlineTime = activity.getDeadlineTime();
        LocalDateTime activityTime = activity.getActivityTime();

        if (now.isBefore(deadlineTime) && now.isBefore(activityTime)) {
            return STATUS_REGISTRATION_OPEN;
        } else if ((now.isAfter(deadlineTime) || now.isEqual(deadlineTime)) && now.isBefore(activityTime)) {
            return STATUS_REGISTRATION_CLOSED;
        } else if ((now.isAfter(activityTime) || now.isEqual(activityTime)) && now.isBefore(activityTime.plusHours(4))) {
            return STATUS_IN_PROGRESS;
        } else {
            return STATUS_ENDED;
        }
    }

    private Page<ActivityVO> convertToActivityVOPage(Page<BusiActivity> activityPage) {
        List<BusiActivity> records = activityPage.getRecords();
        if (records.isEmpty()) {
            return new Page<>(activityPage.getCurrent(), activityPage.getSize(), activityPage.getTotal());
        }

        List<Long> classIds = records.stream()
                .map(BusiActivity::getTargetClassId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> classNameMap = null;
        if (!classIds.isEmpty()) {
            List<BusiClass> classes = busiClassMapper.selectBatchIds(classIds);
            classNameMap = classes.stream()
                    .collect(Collectors.toMap(BusiClass::getId, BusiClass::getClassName));
        }

        List<Long> activityIds = records.stream()
                .map(BusiActivity::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<BusiActivityRegistration> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(BusiActivityRegistration::getStatus, REGISTRATION_STATUS_NORMAL);
        countWrapper.in(BusiActivityRegistration::getActivityId, activityIds);

        List<BusiActivityRegistration> allRegistrations = busiActivityRegistrationMapper.selectList(countWrapper);
        Map<Long, Long> registrationCountMap = allRegistrations.stream()
                .collect(Collectors.groupingBy(BusiActivityRegistration::getActivityId, Collectors.counting()));

        final Map<Long, String> finalClassNameMap = classNameMap;

        List<ActivityVO> voList = records.stream().map(activity -> {
            ActivityVO vo = new ActivityVO();
            BeanUtils.copyProperties(activity, vo);

            int status = calculateActivityStatus(activity);
            vo.setStatus(status);
            vo.setStatusText(getStatusText(status));
            vo.setActivityTypeText(getActivityTypeText(activity.getActivityType()));

            if (finalClassNameMap != null && activity.getTargetClassId() != null) {
                vo.setTargetClassName(finalClassNameMap.get(activity.getTargetClassId()));
            }

            vo.setRegistrationCount(registrationCountMap.getOrDefault(activity.getId(), 0L).intValue());

            return vo;
        }).collect(Collectors.toList());

        Page<ActivityVO> voPage = new Page<>(activityPage.getCurrent(), activityPage.getSize(), activityPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    private String getStatusText(Integer status) {
        if (status == null) return "";
        if (STATUS_REGISTRATION_OPEN.equals(status)) return "报名中";
        if (STATUS_REGISTRATION_CLOSED.equals(status)) return "已截止";
        if (STATUS_IN_PROGRESS.equals(status)) return "进行中";
        if (STATUS_ENDED.equals(status)) return "已结束";
        return "";
    }

    private String getActivityTypeText(Integer type) {
        if (type == null) return "";
        switch (type) {
            case 1: return "亲子游戏";
            case 2: return "文艺汇演";
            case 3: return "家长会";
            case 4: return "户外踏青";
            case 5: return "其他";
            default: return "";
        }
    }
}