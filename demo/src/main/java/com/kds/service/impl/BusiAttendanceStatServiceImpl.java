package com.kds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kds.common.context.LoginHelper;
import com.kds.mapper.BusiAttendanceRecordMapper;
import com.kds.mapper.BusiClassMapper;
import com.kds.mapper.BusiLeaveApplicationMapper;
import com.kds.mapper.BusiStudentMapper;
import com.kds.model.dto.AttendanceStatQueryDTO;
import com.kds.model.entity.BusiAttendanceRecord;
import com.kds.model.entity.BusiClass;
import com.kds.model.entity.BusiLeaveApplication;
import com.kds.model.entity.BusiStudent;
import com.kds.model.vo.*;
import com.kds.service.BusiAttendanceStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusiAttendanceStatServiceImpl implements BusiAttendanceStatService {

    @Autowired
    private LoginHelper loginHelper;

    @Autowired
    private BusiClassMapper busiClassMapper;

    @Autowired
    private BusiStudentMapper busiStudentMapper;

    @Autowired
    private BusiAttendanceRecordMapper busiAttendanceRecordMapper;

    @Autowired
    private BusiLeaveApplicationMapper busiLeaveApplicationMapper;

    private static final Integer STATUS_PRESENT = 1;
    private static final Integer STATUS_ABSENT = 2;
    private static final Integer STATUS_LEAVE_PERSONAL = 3;
    private static final Integer STATUS_LEAVE_SICK = 4;
    private static final Integer STATUS_LEAVE_OTHER = 5;

    @Override
    public AttendanceOverviewVO getOverview(AttendanceStatQueryDTO queryDTO) {
        List<Long> classIds = getAllowedClassIds(queryDTO.getClassId());
        if (classIds.isEmpty()) {
            return buildEmptyOverview();
        }

        LocalDate startDate = queryDTO.getStartDate();
        LocalDate endDate = queryDTO.getEndDate();
        long workDays = calculateWorkDays(startDate, endDate);

        int totalShouldAttend = 0;
        int totalPresent = 0;
        int totalAbsent = 0;
        int totalLeave = 0;

        Map<Integer, Integer> statusCountMap = new HashMap<>();
        statusCountMap.put(STATUS_PRESENT, 0);
        statusCountMap.put(STATUS_ABSENT, 0);
        statusCountMap.put(STATUS_LEAVE_PERSONAL, 0);
        statusCountMap.put(STATUS_LEAVE_SICK, 0);
        statusCountMap.put(STATUS_LEAVE_OTHER, 0);

        for (Long classId : classIds) {
            int studentCount = getStudentCount(classId);
            totalShouldAttend += studentCount * workDays;

            List<Long> studentIds = getStudentIds(classId);

            List<BusiAttendanceRecord> records = busiAttendanceRecordMapper.selectList(
                    new LambdaQueryWrapper<BusiAttendanceRecord>()
                            .eq(BusiAttendanceRecord::getClassId, classId)
                            .ge(BusiAttendanceRecord::getRecordDate, startDate)
                            .le(BusiAttendanceRecord::getRecordDate, endDate)
                            .eq(BusiAttendanceRecord::getIsDeleted, 0)
            );

            for (BusiAttendanceRecord record : records) {
                Integer status = record.getStatus();
                if (status != null && status.equals(STATUS_PRESENT)) {
                    totalPresent++;
                    statusCountMap.put(STATUS_PRESENT, statusCountMap.get(STATUS_PRESENT) + 1);
                }
            }

            List<BusiLeaveApplication> approvedLeaves = busiLeaveApplicationMapper.selectList(
                    new LambdaQueryWrapper<BusiLeaveApplication>()
                            .in(BusiLeaveApplication::getStudentId, studentIds)
                            .le(BusiLeaveApplication::getStartDate, endDate)
                            .ge(BusiLeaveApplication::getEndDate, startDate)
                            .in(BusiLeaveApplication::getAuditStatus, List.of(1, 3))
                            .eq(BusiLeaveApplication::getIsDeleted, 0)
            );

            for (BusiLeaveApplication leave : approvedLeaves) {
                totalLeave++;
                Integer leaveType = leave.getLeaveType();
                if (leaveType != null) {
                    if (leaveType == 1) {
                        statusCountMap.put(STATUS_LEAVE_PERSONAL, statusCountMap.get(STATUS_LEAVE_PERSONAL) + 1);
                    } else if (leaveType == 2) {
                        statusCountMap.put(STATUS_LEAVE_SICK, statusCountMap.get(STATUS_LEAVE_SICK) + 1);
                    } else if (leaveType == 3) {
                        statusCountMap.put(STATUS_LEAVE_OTHER, statusCountMap.get(STATUS_LEAVE_OTHER) + 1);
                    }
                }
            }
        }

        totalAbsent = totalShouldAttend - totalPresent - totalLeave;
        if (totalAbsent < 0) {
            totalAbsent = 0;
        }
        statusCountMap.put(STATUS_ABSENT, totalAbsent);

        AttendanceOverviewVO vo = new AttendanceOverviewVO();
        vo.setTotalShouldAttend(totalShouldAttend);
        vo.setTotalPresent(totalPresent);
        vo.setTotalAbsent(totalAbsent);
        vo.setTotalLeave(totalLeave);

        double attendanceRate = totalShouldAttend > 0 ? (double) totalPresent / totalShouldAttend * 100 : 0;
        double presentRate = totalShouldAttend > 0 ? (double) totalPresent / totalShouldAttend * 100 : 0;
        double absentRate = totalShouldAttend > 0 ? (double) totalAbsent / totalShouldAttend * 100 : 0;
        double leaveRate = totalShouldAttend > 0 ? (double) totalLeave / totalShouldAttend * 100 : 0;

        vo.setAttendanceRate(Math.round(attendanceRate * 100) / 100.0);
        vo.setPresentRate(Math.round(presentRate * 100) / 100.0);
        vo.setAbsentRate(Math.round(absentRate * 100) / 100.0);
        vo.setLeaveRate(Math.round(leaveRate * 100) / 100.0);

        List<StatusDistributionVO> statusDistribution = new ArrayList<>();
        int totalRecords = totalPresent + totalAbsent + totalLeave;
        if (totalRecords > 0) {
            statusDistribution.add(buildStatusDistribution(STATUS_PRESENT, "出勤", statusCountMap.get(STATUS_PRESENT), totalRecords));
            statusDistribution.add(buildStatusDistribution(STATUS_ABSENT, "缺勤", statusCountMap.get(STATUS_ABSENT), totalRecords));
            statusDistribution.add(buildStatusDistribution(STATUS_LEAVE_PERSONAL, "事假", statusCountMap.get(STATUS_LEAVE_PERSONAL), totalRecords));
            statusDistribution.add(buildStatusDistribution(STATUS_LEAVE_SICK, "病假", statusCountMap.get(STATUS_LEAVE_SICK), totalRecords));
            statusDistribution.add(buildStatusDistribution(STATUS_LEAVE_OTHER, "其他请假", statusCountMap.get(STATUS_LEAVE_OTHER), totalRecords));
        }
        vo.setStatusDistribution(statusDistribution);

        return vo;
    }

    @Override
    public List<AttendanceClassRateVO> getClassRateList(AttendanceStatQueryDTO queryDTO) {
        List<Long> classIds = getAllowedClassIds(queryDTO.getClassId());
        if (classIds.isEmpty()) {
            return new ArrayList<>();
        }

        LocalDate startDate = queryDTO.getStartDate();
        LocalDate endDate = queryDTO.getEndDate();
        long workDays = calculateWorkDays(startDate, endDate);

        List<AttendanceClassRateVO> result = new ArrayList<>();

        for (Long classId : classIds) {
            BusiClass busiClass = busiClassMapper.selectById(classId);
            if (busiClass == null) {
                continue;
            }

            int studentCount = getStudentCount(classId);
            int shouldAttend = studentCount * (int) workDays;

            List<Long> studentIds = getStudentIds(classId);

            LambdaQueryWrapper<BusiAttendanceRecord> wrapper = new LambdaQueryWrapper<BusiAttendanceRecord>()
                    .eq(BusiAttendanceRecord::getClassId, classId)
                    .ge(BusiAttendanceRecord::getRecordDate, startDate)
                    .le(BusiAttendanceRecord::getRecordDate, endDate)
                    .eq(BusiAttendanceRecord::getIsDeleted, 0);

            List<BusiAttendanceRecord> records = busiAttendanceRecordMapper.selectList(wrapper);

            int present = 0;
            int absent = 0;
            int leave = 0;

            for (BusiAttendanceRecord record : records) {
                Integer status = record.getStatus();
                if (status != null && status.equals(STATUS_PRESENT)) {
                    present++;
                }
            }

            List<BusiLeaveApplication> approvedLeaves = busiLeaveApplicationMapper.selectList(
                    new LambdaQueryWrapper<BusiLeaveApplication>()
                            .in(BusiLeaveApplication::getStudentId, studentIds)
                            .le(BusiLeaveApplication::getStartDate, endDate)
                            .ge(BusiLeaveApplication::getEndDate, startDate)
                            .in(BusiLeaveApplication::getAuditStatus, List.of(1, 3))
                            .eq(BusiLeaveApplication::getIsDeleted, 0)
            );
            leave = approvedLeaves.size();

            absent = shouldAttend - present - leave;
            if (absent < 0) {
                absent = 0;
            }

            AttendanceClassRateVO vo = new AttendanceClassRateVO();
            vo.setClassId(classId);
            vo.setClassName(busiClass.getClassName());
            vo.setGrade(busiClass.getGrade());
            vo.setStudentCount(studentCount);
            vo.setWorkDays((int) workDays);
            vo.setShouldAttend(shouldAttend);
            vo.setPresent(present);
            vo.setAbsent(absent);
            vo.setLeave(leave);
            double rate = shouldAttend > 0 ? (double) present / shouldAttend * 100 : 0;
            vo.setAttendanceRate(Math.round(rate * 100) / 100.0);

            result.add(vo);
        }

        return result;
    }

    @Override
    public AttendanceLeaveStatVO getLeaveStat(AttendanceStatQueryDTO queryDTO) {
        List<Long> classIds = getAllowedClassIds(queryDTO.getClassId());
        if (classIds.isEmpty()) {
            return buildEmptyLeaveStat();
        }

        LocalDate startDate = queryDTO.getStartDate();
        LocalDate endDate = queryDTO.getEndDate();

        List<Long> studentIds = new ArrayList<>();
        for (Long classId : classIds) {
            studentIds.addAll(getStudentIds(classId));
        }
        if (studentIds.isEmpty()) {
            return buildEmptyLeaveStat();
        }

        LambdaQueryWrapper<BusiLeaveApplication> wrapper = new LambdaQueryWrapper<BusiLeaveApplication>()
                .in(BusiLeaveApplication::getStudentId, studentIds)
                .ge(BusiLeaveApplication::getStartDate, startDate)
                .le(BusiLeaveApplication::getEndDate, endDate)
                .in(BusiLeaveApplication::getAuditStatus, List.of(1, 3))
                .eq(BusiLeaveApplication::getIsDeleted, 0);

        List<BusiLeaveApplication> leaves = busiLeaveApplicationMapper.selectList(wrapper);

        int totalLeave = leaves.size();
        int personalLeave = 0;
        int sickLeave = 0;
        int otherLeave = 0;

        for (BusiLeaveApplication leave : leaves) {
            Integer leaveType = leave.getLeaveType();
            if (leaveType != null) {
                if (leaveType == 1) {
                    personalLeave++;
                } else if (leaveType == 2) {
                    sickLeave++;
                } else if (leaveType == 3) {
                    otherLeave++;
                }
            }
        }

        AttendanceLeaveStatVO vo = new AttendanceLeaveStatVO();
        vo.setTotalLeave(totalLeave);

        List<LeaveDistributionVO> distributions = new ArrayList<>();
        if (totalLeave > 0) {
            distributions.add(buildLeaveDistribution(1, "事假", personalLeave, totalLeave));
            distributions.add(buildLeaveDistribution(2, "病假", sickLeave, totalLeave));
            distributions.add(buildLeaveDistribution(3, "其他", otherLeave, totalLeave));
        }
        vo.setLeaveDistribution(distributions);

        return vo;
    }

    @Override
    public List<AttendanceTrendVO> getTrend(AttendanceStatQueryDTO queryDTO) {
        List<Long> classIds = getAllowedClassIds(queryDTO.getClassId());
        if (classIds.isEmpty()) {
            return new ArrayList<>();
        }

        LocalDate startDate = queryDTO.getStartDate();
        LocalDate endDate = queryDTO.getEndDate();

        int totalStudentCount = 0;
        for (Long classId : classIds) {
            totalStudentCount += getStudentCount(classId);
        }

        if (totalStudentCount == 0) {
            return new ArrayList<>();
        }

        List<AttendanceTrendVO> result = new ArrayList<>();
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                List<BusiAttendanceRecord> records = busiAttendanceRecordMapper.selectList(
                        new LambdaQueryWrapper<BusiAttendanceRecord>()
                                .in(BusiAttendanceRecord::getClassId, classIds)
                                .eq(BusiAttendanceRecord::getRecordDate, date)
                                .eq(BusiAttendanceRecord::getIsDeleted, 0)
                );

                int present = 0;
                for (BusiAttendanceRecord record : records) {
                    if (STATUS_PRESENT.equals(record.getStatus())) {
                        present++;
                    }
                }

                AttendanceTrendVO vo = new AttendanceTrendVO();
                vo.setDate(date.toString());
                vo.setShouldAttend(totalStudentCount);
                vo.setPresent(present);
                double rate = (double) present / totalStudentCount * 100;
                vo.setAttendanceRate(Math.round(rate * 100) / 100.0);

                result.add(vo);
            }
            date = date.plusDays(1);
        }

        return result;
    }

    private List<Long> getAllowedClassIds(Long classId) {
        if (loginHelper.isAdmin()) {
            if (classId != null) {
                return List.of(classId);
            }
            return busiClassMapper.selectList(new LambdaQueryWrapper<BusiClass>()
                    .eq(BusiClass::getIsDeleted, 0))
                    .stream()
                    .map(BusiClass::getId)
                    .collect(Collectors.toList());
        } else {
            List<Long> teacherClassIds = loginHelper.getClassIds();
            if (teacherClassIds == null || teacherClassIds.isEmpty()) {
                return new ArrayList<>();
            }
            if (classId != null) {
                if (teacherClassIds.contains(classId)) {
                    return List.of(classId);
                }
                return new ArrayList<>();
            }
            return teacherClassIds;
        }
    }

    private int getStudentCount(Long classId) {
        return busiStudentMapper.selectCount(new LambdaQueryWrapper<BusiStudent>()
                .eq(BusiStudent::getClassId, classId)
                .eq(BusiStudent::getStatus, 1)
                .eq(BusiStudent::getIsDeleted, 0)).intValue();
    }

    private List<Long> getStudentIds(Long classId) {
        return busiStudentMapper.selectList(new LambdaQueryWrapper<BusiStudent>()
                        .eq(BusiStudent::getClassId, classId)
                        .eq(BusiStudent::getStatus, 1)
                        .eq(BusiStudent::getIsDeleted, 0))
                .stream()
                .map(BusiStudent::getId)
                .collect(Collectors.toList());
    }

    private long calculateWorkDays(LocalDate startDate, LocalDate endDate) {
        long workDays = 0;
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                workDays++;
            }
            date = date.plusDays(1);
        }
        return workDays;
    }

    private AttendanceOverviewVO buildEmptyOverview() {
        AttendanceOverviewVO vo = new AttendanceOverviewVO();
        vo.setTotalShouldAttend(0);
        vo.setTotalPresent(0);
        vo.setTotalAbsent(0);
        vo.setTotalLeave(0);
        vo.setAttendanceRate(0.0);
        vo.setPresentRate(0.0);
        vo.setAbsentRate(0.0);
        vo.setLeaveRate(0.0);
        vo.setStatusDistribution(new ArrayList<>());
        return vo;
    }

    private AttendanceLeaveStatVO buildEmptyLeaveStat() {
        AttendanceLeaveStatVO vo = new AttendanceLeaveStatVO();
        vo.setTotalLeave(0);
        vo.setLeaveDistribution(new ArrayList<>());
        return vo;
    }

    private StatusDistributionVO buildStatusDistribution(Integer status, String statusText, Integer count, Integer total) {
        StatusDistributionVO vo = new StatusDistributionVO();
        vo.setStatus(status);
        vo.setStatusText(statusText);
        vo.setCount(count);
        double rate = total > 0 ? (double) count / total * 100 : 0;
        vo.setRate(Math.round(rate * 100) / 100.0);
        return vo;
    }

    private LeaveDistributionVO buildLeaveDistribution(Integer leaveType, String leaveTypeText, Integer count, Integer total) {
        LeaveDistributionVO vo = new LeaveDistributionVO();
        vo.setLeaveType(leaveType);
        vo.setLeaveTypeText(leaveTypeText);
        vo.setCount(count);
        double rate = total > 0 ? (double) count / total * 100 : 0;
        vo.setRate(Math.round(rate * 100) / 100.0);
        return vo;
    }
}