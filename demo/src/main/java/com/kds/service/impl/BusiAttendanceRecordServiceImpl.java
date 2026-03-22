package com.kds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kds.common.context.LoginHelper;
import com.kds.common.exception.BusinessException;
import com.kds.mapper.BusiAttendanceRecordMapper;
import com.kds.mapper.BusiClassMapper;
import com.kds.mapper.BusiLeaveApplicationMapper;
import com.kds.mapper.BusiStudentMapper;
import com.kds.mapper.BusiTeacherClassMapper;
import com.kds.model.dto.AttendanceFormDTO;
import com.kds.model.dto.AttendanceQueryDTO;
import com.kds.model.entity.BusiAttendanceRecord;
import com.kds.model.entity.BusiClass;
import com.kds.model.entity.BusiLeaveApplication;
import com.kds.model.entity.BusiStudent;
import com.kds.model.vo.AttendanceVO;
import com.kds.service.BusiAttendanceRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BusiAttendanceRecordServiceImpl extends ServiceImpl<BusiAttendanceRecordMapper, BusiAttendanceRecord> implements BusiAttendanceRecordService {

    @Autowired
    private LoginHelper loginHelper;

    @Autowired
    private BusiStudentMapper busiStudentMapper;

    @Autowired
    private BusiClassMapper busiClassMapper;

    @Autowired
    private BusiLeaveApplicationMapper busiLeaveApplicationMapper;

    @Autowired
    private BusiTeacherClassMapper busiTeacherClassMapper;

    @Autowired
    private BusiStudentParentMapper busiStudentParentMapper;

    private static final Integer STATUS_PRESENT = 1;
    private static final Integer STATUS_ABSENT = 2;
    private static final Integer STATUS_LEAVE = 3;

    @Override
    public Page<AttendanceVO> getAttendancePage(AttendanceQueryDTO queryDTO) {
        if (!loginHelper.isTeacher()) {
            throw new BusinessException("只有教师才能查看考勤列表");
        }

        List<Long> classIds = loginHelper.getClassIds();
        if (classIds == null || classIds.isEmpty()) {
            return new Page<>();
        }

        if (queryDTO.getClassId() != null && !classIds.contains(queryDTO.getClassId())) {
            throw new BusinessException("您只能查看所任班级的考勤");
        }

        Long targetClassId = queryDTO.getClassId();
        if (targetClassId == null) {
            targetClassId = classIds.get(0);
        }

        LocalDate recordDate = queryDTO.getDate() != null ? queryDTO.getDate() : LocalDate.now();

        List<BusiStudent> students = busiStudentMapper.selectList(
                new LambdaQueryWrapper<BusiStudent>()
                        .eq(BusiStudent::getClassId, targetClassId)
                        .eq(BusiStudent::getStatus, 1)
                        .eq(BusiStudent::getIsDeleted, 0)
        );

        if (students.isEmpty()) {
            return new Page<>();
        }

        List<Long> studentIds = students.stream()
                .map(BusiStudent::getId)
                .collect(Collectors.toList());

        List<BusiAttendanceRecord> existRecords = this.list(
                new LambdaQueryWrapper<BusiAttendanceRecord>()
                        .eq(BusiAttendanceRecord::getClassId, targetClassId)
                        .eq(BusiAttendanceRecord::getRecordDate, recordDate)
                        .eq(BusiAttendanceRecord::getIsDeleted, 0)
                        .in(BusiAttendanceRecord::getStudentId, studentIds)
        );

        Map<Long, BusiAttendanceRecord> recordMap = existRecords.stream()
                .collect(Collectors.toMap(BusiAttendanceRecord::getStudentId, r -> r));

        List<BusiLeaveApplication> approvedLeaves = busiLeaveApplicationMapper.selectList(
                new LambdaQueryWrapper<BusiLeaveApplication>()
                        .eq(BusiLeaveApplication::getIsDeleted, 0)
                        .in(BusiLeaveApplication::getStudentId, studentIds)
                        .le(BusiLeaveApplication::getStartDate, recordDate)
                        .ge(BusiLeaveApplication::getEndDate, recordDate)
                        .in(BusiLeaveApplication::getAuditStatus, List.of(1, 3))
        );

        Map<Long, BusiLeaveApplication> leaveMap = approvedLeaves.stream()
                .collect(Collectors.toMap(BusiLeaveApplication::getStudentId, l -> l));

        BusiClass busiClass = busiClassMapper.selectById(targetClassId);
        String className = busiClass != null ? busiClass.getClassName() : "";

        List<AttendanceVO> voList = new ArrayList<>();
        for (BusiStudent student : students) {
            AttendanceVO vo = new AttendanceVO();
            vo.setStudentId(student.getId());
            vo.setStudentName(student.getStudentName());
            vo.setClassId(targetClassId);
            vo.setClassName(className);
            vo.setRecordDate(recordDate);

            BusiLeaveApplication leave = leaveMap.get(student.getId());
            BusiAttendanceRecord record = recordMap.get(student.getId());

            if (leave != null) {
                vo.setStatus(leave.getLeaveType() + 2);
                vo.setStatusText(getStatusText(leave.getLeaveType() + 2));
                vo.setLeaveType(leave.getLeaveType());
                vo.setLeaveTypeText(getLeaveTypeText(leave.getLeaveType()));
                vo.setLeaveApplicationId(leave.getId());
            } else if (record != null) {
                vo.setId(record.getId());
                vo.setStatus(record.getStatus());
                vo.setStatusText(getStatusText(record.getStatus()));
                vo.setSignInTime(record.getSignInTime());
                vo.setSignOutTime(record.getSignOutTime());
                vo.setLeaveApplicationId(record.getLeaveApplicationId());
                vo.setCreateTime(record.getCreateTime());
            } else {
                vo.setStatus(null);
                vo.setStatusText("未考勤");
            }

            voList.add(vo);
        }

        Page<AttendanceVO> voPage = new Page<>(queryDTO.getPage(), queryDTO.getSize(), voList.size());
        int fromIndex = (int) ((queryDTO.getPage() - 1) * queryDTO.getSize());
        int toIndex = Math.min(fromIndex + queryDTO.getSize(), voList.size());
        if (fromIndex < voList.size()) {
            voPage.setRecords(voList.subList(fromIndex, toIndex));
        } else {
            voPage.setRecords(new ArrayList<>());
        }

        return voPage;
    }

    @Override
    public List<AttendanceVO> getParentAttendanceList(Long studentId, LocalDate date) {
        if (!loginHelper.isParent()) {
            throw new BusinessException("只有家长才能查看考勤记录");
        }

        Long parentUserId = loginHelper.getUserId();
        List<Long> childIds = busiStudentParentMapper.selectChildIdsByParentUserId(parentUserId);
        if (childIds == null || childIds.isEmpty() || !childIds.contains(studentId)) {
            throw new BusinessException("您只能查看自己孩子的考勤记录");
        }

        BusiStudent student = busiStudentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("幼儿不存在");
        }

        LocalDate recordDate = date != null ? date : LocalDate.now();

        BusiClass busiClass = busiClassMapper.selectById(student.getClassId());
        String className = busiClass != null ? busiClass.getClassName() : "";

        BusiAttendanceRecord record = this.getOne(
                new LambdaQueryWrapper<BusiAttendanceRecord>()
                        .eq(BusiAttendanceRecord::getStudentId, studentId)
                        .eq(BusiAttendanceRecord::getRecordDate, recordDate)
                        .eq(BusiAttendanceRecord::getIsDeleted, 0)
        );

        BusiLeaveApplication leave = busiLeaveApplicationMapper.selectOne(
                new LambdaQueryWrapper<BusiLeaveApplication>()
                        .eq(BusiLeaveApplication::getIsDeleted, 0)
                        .eq(BusiLeaveApplication::getStudentId, studentId)
                        .le(BusiLeaveApplication::getStartDate, recordDate)
                        .ge(BusiLeaveApplication::getEndDate, recordDate)
                        .in(BusiLeaveApplication::getAuditStatus, List.of(1, 3))
        );

        AttendanceVO vo = new AttendanceVO();
        vo.setStudentId(student.getId());
        vo.setStudentName(student.getStudentName());
        vo.setClassId(student.getClassId());
        vo.setClassName(className);
        vo.setRecordDate(recordDate);

        if (leave != null) {
            vo.setStatus(leave.getLeaveType() + 2);
            vo.setStatusText(getStatusText(leave.getLeaveType() + 2));
            vo.setLeaveType(leave.getLeaveType());
            vo.setLeaveTypeText(getLeaveTypeText(leave.getLeaveType()));
        } else if (record != null) {
            vo.setId(record.getId());
            vo.setStatus(record.getStatus());
            vo.setStatusText(getStatusText(record.getStatus()));
            vo.setSignInTime(record.getSignInTime());
            vo.setSignOutTime(record.getSignOutTime());
        } else {
            vo.setStatus(null);
            vo.setStatusText("未考勤");
        }

        return List.of(vo);
    }

    @Override
    @Transactional
    public void signIn(AttendanceFormDTO formDTO) {
        if (!loginHelper.isTeacher()) {
            throw new BusinessException("只有教师才能进行签到操作");
        }

        List<Long> classIds = loginHelper.getClassIds();
        if (classIds == null || classIds.isEmpty()) {
            throw new BusinessException("您还没有分配班级");
        }

        if (!classIds.contains(formDTO.getClassId())) {
            throw new BusinessException("您只能操作所任班级的幼儿");
        }

        LocalDate recordDate = formDTO.getDate() != null ? formDTO.getDate() : LocalDate.now();

        List<Long> studentIds = formDTO.getStudentIds();
        List<BusiLeaveApplication> approvedLeaves = busiLeaveApplicationMapper.selectList(
                new LambdaQueryWrapper<BusiLeaveApplication>()
                        .eq(BusiLeaveApplication::getIsDeleted, 0)
                        .in(BusiLeaveApplication::getStudentId, studentIds)
                        .le(BusiLeaveApplication::getStartDate, recordDate)
                        .ge(BusiLeaveApplication::getEndDate, recordDate)
                        .in(BusiLeaveApplication::getAuditStatus, List.of(1, 3))
        );

        Map<Long, BusiLeaveApplication> leaveMap = approvedLeaves.stream()
                .collect(Collectors.toMap(BusiLeaveApplication::getStudentId, l -> l));

        List<BusiAttendanceRecord> existRecords = this.list(
                new LambdaQueryWrapper<BusiAttendanceRecord>()
                        .eq(BusiAttendanceRecord::getClassId, formDTO.getClassId())
                        .eq(BusiAttendanceRecord::getRecordDate, recordDate)
                        .eq(BusiAttendanceRecord::getIsDeleted, 0)
                        .in(BusiAttendanceRecord::getStudentId, studentIds)
        );

        Map<Long, BusiAttendanceRecord> recordMap = existRecords.stream()
                .collect(Collectors.toMap(BusiAttendanceRecord::getStudentId, r -> r));

        LocalDateTime now = LocalDateTime.now();
        List<BusiAttendanceRecord> toSave = new ArrayList<>();

        for (Long studentId : studentIds) {
            BusiLeaveApplication leave = leaveMap.get(studentId);
            if (leave != null) {
                continue;
            }

            BusiAttendanceRecord record = recordMap.get(studentId);
            if (record == null) {
                record = new BusiAttendanceRecord();
                record.setStudentId(studentId);
                record.setClassId(formDTO.getClassId());
                record.setRecordDate(recordDate);
                record.setCreatorId(loginHelper.getUserId());
                record.setSignInTime(now);
                record.setStatus(STATUS_PRESENT);
                toSave.add(record);
            } else {
                record.setSignInTime(now);
                record.setStatus(STATUS_PRESENT);
                toSave.add(record);
            }
        }

        if (!toSave.isEmpty()) {
            this.saveOrUpdateBatch(toSave);
        }
    }

    @Override
    @Transactional
    public void signOut(AttendanceFormDTO formDTO) {
        if (!loginHelper.isTeacher()) {
            throw new BusinessException("只有教师才能进行签退操作");
        }

        List<Long> classIds = loginHelper.getClassIds();
        if (classIds == null || classIds.isEmpty()) {
            throw new BusinessException("您还没有分配班级");
        }

        if (!classIds.contains(formDTO.getClassId())) {
            throw new BusinessException("您只能操作所任班级的幼儿");
        }

        LocalDate recordDate = formDTO.getDate() != null ? formDTO.getDate() : LocalDate.now();

        List<Long> studentIds = formDTO.getStudentIds();
        List<BusiAttendanceRecord> existRecords = this.list(
                new LambdaQueryWrapper<BusiAttendanceRecord>()
                        .eq(BusiAttendanceRecord::getClassId, formDTO.getClassId())
                        .eq(BusiAttendanceRecord::getRecordDate, recordDate)
                        .eq(BusiAttendanceRecord::getIsDeleted, 0)
                        .in(BusiAttendanceRecord::getStudentId, studentIds)
        );

        Map<Long, BusiAttendanceRecord> recordMap = existRecords.stream()
                .collect(Collectors.toMap(BusiAttendanceRecord::getStudentId, r -> r));

        LocalDateTime now = LocalDateTime.now();
        List<BusiAttendanceRecord> toSave = new ArrayList<>();

        for (Long studentId : studentIds) {
            BusiAttendanceRecord record = recordMap.get(studentId);
            if (record != null && record.getSignInTime() != null) {
                record.setSignOutTime(now);
                toSave.add(record);
            }
        }

        if (!toSave.isEmpty()) {
            this.saveOrUpdateBatch(toSave);
        }
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未考勤";
        switch (status) {
            case 1: return "出勤";
            case 2: return "缺勤";
            case 3: return "事假";
            case 4: return "病假";
            case 5: return "其他请假";
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
