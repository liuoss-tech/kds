package com.kds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kds.common.context.LoginHelper;
import com.kds.common.exception.BusinessException;
import com.kds.mapper.BusiClassMapper;
import com.kds.mapper.BusiLeaveApplicationMapper;
import com.kds.mapper.BusiStudentMapper;
import com.kds.mapper.BusiStudentParentMapper;
import com.kds.mapper.BusiTeacherClassMapper;
import com.kds.mapper.SysUserMapper;
import com.kds.model.dto.LeaveApproveDTO;
import com.kds.model.dto.LeaveFormDTO;
import com.kds.model.dto.LeaveQueryDTO;
import com.kds.model.entity.BusiClass;
import com.kds.model.entity.BusiLeaveApplication;
import com.kds.model.entity.BusiStudent;
import com.kds.model.entity.SysUser;
import com.kds.model.vo.LeaveVO;
import com.kds.model.vo.StudentSimpleVO;
import com.kds.service.BusiLeaveApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BusiLeaveApplicationServiceImpl extends ServiceImpl<BusiLeaveApplicationMapper, BusiLeaveApplication> implements BusiLeaveApplicationService {

    @Autowired
    private LoginHelper loginHelper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private BusiClassMapper busiClassMapper;

    @Autowired
    private BusiStudentMapper busiStudentMapper;

    @Autowired
    private BusiStudentParentMapper busiStudentParentMapper;

    @Autowired
    private BusiTeacherClassMapper busiTeacherClassMapper;

    private static final Integer AUDIT_STATUS_PENDING = 0;
    private static final Integer AUDIT_STATUS_APPROVED = 1;
    private static final Integer AUDIT_STATUS_REJECTED = 4;

    @Override
    public void addLeave(LeaveFormDTO formDTO) {
        if (!loginHelper.isParent()) {
            throw new BusinessException("只有家长才能提交请假申请");
        }

        List<Long> childIds = loginHelper.getChildIds();
        if (childIds == null || childIds.isEmpty()) {
            throw new BusinessException("您还没有绑定幼儿，无法提交请假");
        }

        if (!childIds.contains(formDTO.getStudentId())) {
            throw new BusinessException("您只能为自己的孩子提交请假");
        }

        BusiStudent student = busiStudentMapper.selectById(formDTO.getStudentId());
        if (student == null) {
            throw new BusinessException("幼儿不存在");
        }

        long leaveDays = ChronoUnit.DAYS.between(formDTO.getStartDate(), formDTO.getEndDate()) + 1;

        BusiLeaveApplication leave = new BusiLeaveApplication();
        BeanUtils.copyProperties(formDTO, leave);
        leave.setParentUserId(loginHelper.getUserId());
        leave.setClassId(student.getClassId());
        leave.setLeaveDays((int) leaveDays);
        leave.setAuditStatus(AUDIT_STATUS_PENDING);

        this.save(leave);
    }

    @Override
    public Page<LeaveVO> getParentLeavePage(LeaveQueryDTO queryDTO) {
        if (!loginHelper.isParent()) {
            throw new BusinessException("只有家长才能查看请假列表");
        }

        List<Long> childIds = loginHelper.getChildIds();
        if (childIds == null || childIds.isEmpty()) {
            return new Page<>();
        }

        Page<BusiLeaveApplication> pageParam = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<BusiLeaveApplication> wrapper = buildParentQueryWrapper(queryDTO, childIds);

        Page<BusiLeaveApplication> leavePage = this.page(pageParam, wrapper);
        return convertToVO(leavePage);
    }

    private LambdaQueryWrapper<BusiLeaveApplication> buildParentQueryWrapper(LeaveQueryDTO queryDTO, List<Long> childIds) {
        LambdaQueryWrapper<BusiLeaveApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiLeaveApplication::getIsDeleted, 0);
        wrapper.in(BusiLeaveApplication::getStudentId, childIds);

        if (queryDTO.getStatus() != null) {
            wrapper.eq(BusiLeaveApplication::getAuditStatus, queryDTO.getStatus());
        }
        if (queryDTO.getLeaveType() != null) {
            wrapper.eq(BusiLeaveApplication::getLeaveType, queryDTO.getLeaveType());
        }

        wrapper.orderByDesc(BusiLeaveApplication::getCreateTime);
        return wrapper;
    }

    @Override
    public Page<LeaveVO> getTeacherLeavePage(LeaveQueryDTO queryDTO) {
        if (!loginHelper.isTeacher()) {
            throw new BusinessException("只有教师才能查看本班请假列表");
        }

        List<Long> classIds = loginHelper.getClassIds();
        if (classIds == null || classIds.isEmpty()) {
            return new Page<>();
        }

        Page<BusiLeaveApplication> pageParam = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<BusiLeaveApplication> wrapper = buildTeacherQueryWrapper(queryDTO, classIds);

        Page<BusiLeaveApplication> leavePage = this.page(pageParam, wrapper);
        return convertToVO(leavePage);
    }

    private LambdaQueryWrapper<BusiLeaveApplication> buildTeacherQueryWrapper(LeaveQueryDTO queryDTO, List<Long> classIds) {
        LambdaQueryWrapper<BusiLeaveApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiLeaveApplication::getIsDeleted, 0);
        wrapper.in(BusiLeaveApplication::getClassId, classIds);

        if (queryDTO.getStatus() != null) {
            wrapper.eq(BusiLeaveApplication::getAuditStatus, queryDTO.getStatus());
        }
        if (queryDTO.getLeaveType() != null) {
            wrapper.eq(BusiLeaveApplication::getLeaveType, queryDTO.getLeaveType());
        }

        wrapper.orderByDesc(BusiLeaveApplication::getCreateTime);
        return wrapper;
    }

    @Override
    public Page<LeaveVO> getAdminLeavePage(LeaveQueryDTO queryDTO) {
        if (!loginHelper.isAdmin()) {
            throw new BusinessException("只有管理员才能查看全园请假列表");
        }
        Page<BusiLeaveApplication> pageParam = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<BusiLeaveApplication> wrapper = buildAdminQueryWrapper(queryDTO);

        Page<BusiLeaveApplication> leavePage = this.page(pageParam, wrapper);
        return convertToVO(leavePage);
    }

    private LambdaQueryWrapper<BusiLeaveApplication> buildAdminQueryWrapper(LeaveQueryDTO queryDTO) {
        LambdaQueryWrapper<BusiLeaveApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiLeaveApplication::getIsDeleted, 0);

        if (queryDTO.getStatus() != null) {
            wrapper.eq(BusiLeaveApplication::getAuditStatus, queryDTO.getStatus());
        }
        if (queryDTO.getStudentId() != null) {
            wrapper.eq(BusiLeaveApplication::getStudentId, queryDTO.getStudentId());
        }
        if (queryDTO.getLeaveType() != null) {
            wrapper.eq(BusiLeaveApplication::getLeaveType, queryDTO.getLeaveType());
        }

        wrapper.orderByDesc(BusiLeaveApplication::getCreateTime);
        return wrapper;
    }

    @Override
    public void approveLeave(LeaveApproveDTO approveDTO) {
        if (!loginHelper.isTeacher()) {
            throw new BusinessException("只有教师才能审批请假");
        }

        BusiLeaveApplication leave = this.getById(approveDTO.getId());
        if (leave == null) {
            throw new BusinessException("请假记录不存在");
        }

        if (!AUDIT_STATUS_PENDING.equals(leave.getAuditStatus())) {
            throw new BusinessException("该请假已审批，无法重复操作");
        }

        leave.setAuditStatus(approveDTO.getAuditStatus());
        leave.setTeacherId(loginHelper.getUserId());
        leave.setTeacherAuditTime(LocalDate.now().atStartOfDay());

        if (AUDIT_STATUS_REJECTED.equals(approveDTO.getAuditStatus())) {
            if (!StringUtils.hasText(approveDTO.getTeacherRemark())) {
                throw new BusinessException("请填写审批意见");
            }
            leave.setTeacherRemark(approveDTO.getTeacherRemark());
        }

        this.updateById(leave);
    }

    @Override
    public List<StudentSimpleVO> getMyStudents() {
        if (!loginHelper.isParent()) {
            throw new BusinessException("只有家长才能查看绑定的幼儿列表");
        }

        List<Long> childIds = loginHelper.getChildIds();
        if (childIds == null || childIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<BusiStudent> students = busiStudentMapper.selectBatchIds(childIds);
        List<Long> classIds = students.stream()
                .map(BusiStudent::getClassId)
                .collect(Collectors.toList());

        final Map<Long, String> classNameMap;
        if (!classIds.isEmpty()) {
            List<BusiClass> classes = busiClassMapper.selectBatchIds(classIds);
            classNameMap = classes.stream()
                    .collect(Collectors.toMap(BusiClass::getId, BusiClass::getClassName));
        } else {
            classNameMap = null;
        }

        return students.stream().map(student -> {
            StudentSimpleVO vo = new StudentSimpleVO();
            vo.setId(student.getId());
            vo.setStudentName(student.getStudentName());
            vo.setClassId(student.getClassId());
            if (classNameMap != null) {
                vo.setClassName(classNameMap.get(student.getClassId()));
            }
            return vo;
        }).collect(Collectors.toList());
    }

    private Page<LeaveVO> convertToVO(Page<BusiLeaveApplication> leavePage) {
        List<BusiLeaveApplication> records = leavePage.getRecords();
        if (records.isEmpty()) {
            return new Page<>(leavePage.getCurrent(), leavePage.getSize(), leavePage.getTotal());
        }

        List<Long> studentIds = records.stream()
                .map(BusiLeaveApplication::getStudentId)
                .collect(Collectors.toList());
        List<Long> parentUserIds = records.stream()
                .map(BusiLeaveApplication::getParentUserId)
                .collect(Collectors.toList());
        List<Long> teacherIds = records.stream()
                .map(BusiLeaveApplication::getTeacherId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        List<Long> classIds = records.stream()
                .map(BusiLeaveApplication::getClassId)
                .collect(Collectors.toList());

        Map<Long, BusiStudent> studentMap = null;
        if (!studentIds.isEmpty()) {
            List<BusiStudent> students = busiStudentMapper.selectBatchIds(studentIds);
            studentMap = students.stream()
                    .collect(Collectors.toMap(BusiStudent::getId, s -> s));
        }

        Map<Long, SysUser> userMap = null;
        List<Long> allUserIds = new ArrayList<>(parentUserIds);
        allUserIds.addAll(teacherIds);
        if (!allUserIds.isEmpty()) {
            List<SysUser> users = sysUserMapper.selectBatchIds(allUserIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(SysUser::getId, u -> u));
        }

        Map<Long, String> classNameMap = null;
        if (!classIds.isEmpty()) {
            List<BusiClass> classes = busiClassMapper.selectBatchIds(classIds);
            classNameMap = classes.stream()
                    .collect(Collectors.toMap(BusiClass::getId, BusiClass::getClassName));
        }

        final Map<Long, BusiStudent> finalStudentMap = studentMap;
        final Map<Long, SysUser> finalUserMap = userMap;
        final Map<Long, String> finalClassNameMap = classNameMap;

        List<LeaveVO> voList = records.stream().map(leave -> {
            LeaveVO vo = new LeaveVO();
            BeanUtils.copyProperties(leave, vo);

            vo.setAuditStatusText(getAuditStatusText(leave.getAuditStatus()));
            vo.setLeaveTypeText(getLeaveTypeText(leave.getLeaveType()));

            if (finalStudentMap != null) {
                BusiStudent student = finalStudentMap.get(leave.getStudentId());
                if (student != null) {
                    vo.setStudentName(student.getStudentName());
                }
            }

            if (finalClassNameMap != null) {
                vo.setClassName(finalClassNameMap.get(leave.getClassId()));
            }

            if (finalUserMap != null) {
                SysUser parentUser = finalUserMap.get(leave.getParentUserId());
                if (parentUser != null) {
                    vo.setParentUserName(parentUser.getRealName());
                }
                if (leave.getTeacherId() != null) {
                    SysUser teacher = finalUserMap.get(leave.getTeacherId());
                    if (teacher != null) {
                        vo.setTeacherName(teacher.getRealName());
                    }
                }
            }

            return vo;
        }).collect(Collectors.toList());

        Page<LeaveVO> voPage = new Page<>(leavePage.getCurrent(), leavePage.getSize(), leavePage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    private String getAuditStatusText(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待审批";
            case 1: return "已通过";
            case 2: return "待园长审批";
            case 3: return "已通过";
            case 4: return "已拒绝";
            case 5: return "已撤销";
            default: return "";
        }
    }

    private String getLeaveTypeText(Integer type) {
        if (type == null) return "";
        switch (type) {
            case 1: return "事假";
            case 2: return "病假";
            case 3: return "其他";
            default: return "";
        }
    }
}
