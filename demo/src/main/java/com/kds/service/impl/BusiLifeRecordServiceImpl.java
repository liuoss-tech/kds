package com.kds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kds.common.context.LoginHelper;
import com.kds.common.exception.BusinessException;
import com.kds.mapper.BusiClassMapper;
import com.kds.mapper.BusiLifeRecordMapper;
import com.kds.mapper.BusiStudentMapper;
import com.kds.mapper.BusiStudentParentMapper;
import com.kds.model.dto.LifeRecordBatchSaveDTO;
import com.kds.model.dto.LifeRecordFormDTO;
import com.kds.model.dto.LifeRecordQueryDTO;
import com.kds.model.entity.BusiClass;
import com.kds.model.entity.BusiLifeRecord;
import com.kds.model.entity.BusiStudent;
import com.kds.model.entity.BusiStudentParent;
import com.kds.model.vo.LifeRecordVO;
import com.kds.model.vo.ParentLifeRecordVO;
import com.kds.service.BusiLifeRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BusiLifeRecordServiceImpl extends ServiceImpl<BusiLifeRecordMapper, BusiLifeRecord> implements BusiLifeRecordService {

    @Autowired
    private LoginHelper loginHelper;

    @Autowired
    private BusiStudentMapper busiStudentMapper;

    @Autowired
    private BusiClassMapper busiClassMapper;

    @Autowired
    private BusiStudentParentMapper busiStudentParentMapper;

    @Override
    public Page<LifeRecordVO> getLifeRecordPage(LifeRecordQueryDTO queryDTO) {
        if (!loginHelper.isTeacher()) {
            throw new BusinessException("只有教师才能查看生活记录列表");
        }

        List<Long> classIds = loginHelper.getClassIds();
        if (classIds == null || classIds.isEmpty()) {
            return new Page<>();
        }

        if (queryDTO.getClassId() != null && !classIds.contains(queryDTO.getClassId())) {
            throw new BusinessException("您只能查看所任班级的生活记录");
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

        List<BusiLifeRecord> existRecords = this.list(
                new LambdaQueryWrapper<BusiLifeRecord>()
                        .eq(BusiLifeRecord::getClassId, targetClassId)
                        .eq(BusiLifeRecord::getRecordDate, recordDate)
                        .eq(BusiLifeRecord::getIsDeleted, 0)
                        .in(BusiLifeRecord::getStudentId, studentIds)
        );

        Map<Long, BusiLifeRecord> recordMap = existRecords.stream()
                .collect(Collectors.toMap(BusiLifeRecord::getStudentId, r -> r));

        BusiClass busiClass = busiClassMapper.selectById(targetClassId);
        String className = busiClass != null ? busiClass.getClassName() : "";

        List<LifeRecordVO> voList = new ArrayList<>();
        for (BusiStudent student : students) {
            LifeRecordVO vo = new LifeRecordVO();
            vo.setStudentId(student.getId());
            vo.setStudentName(student.getStudentName());
            vo.setClassId(targetClassId);
            vo.setClassName(className);
            vo.setRecordDate(recordDate);

            BusiLifeRecord record = recordMap.get(student.getId());
            if (record != null) {
                vo.setId(record.getId());
                vo.setLunchIntake(record.getLunchIntake());
                vo.setWaterCount(record.getWaterCount());
                vo.setSleepStartTime(record.getSleepStartTime());
                vo.setSleepDuration(record.getSleepDuration());
                vo.setSleepQuality(record.getSleepQuality());
                vo.setToiletCount(record.getToiletCount());
                vo.setToiletAbnormal(record.getToiletAbnormal());
                vo.setMorningTemp(record.getMorningTemp());
                vo.setAfternoonTemp(record.getAfternoonTemp());
                vo.setHealthSymptoms(record.getHealthSymptoms());
                vo.setTeacherRemark(record.getTeacherRemark());
                vo.setCreateTime(record.getCreateTime());
            }

            voList.add(vo);
        }

        Page<LifeRecordVO> voPage = new Page<>(queryDTO.getPage(), queryDTO.getSize(), voList.size());
        int fromIndex = (int) ((queryDTO.getPage() - 1) * queryDTO.getSize());
        int toIndex = Math.min(fromIndex + queryDTO.getSize().intValue(), voList.size());
        if (fromIndex < voList.size()) {
            voPage.setRecords(voList.subList(fromIndex, toIndex));
        } else {
            voPage.setRecords(new ArrayList<>());
        }

        return voPage;
    }

    @Override
    @Transactional
    public void saveRecord(LifeRecordFormDTO formDTO) {
        if (formDTO.getStudentId() == null || formDTO.getClassId() == null) {
            throw new BusinessException("学生ID和班级ID不能为空");
        }

        LocalDate recordDate = formDTO.getRecordDate() != null ? formDTO.getRecordDate() : LocalDate.now();

        BusiLifeRecord existRecord = this.getOne(
                new LambdaQueryWrapper<BusiLifeRecord>()
                        .eq(BusiLifeRecord::getStudentId, formDTO.getStudentId())
                        .eq(BusiLifeRecord::getClassId, formDTO.getClassId())
                        .eq(BusiLifeRecord::getRecordDate, recordDate)
                        .eq(BusiLifeRecord::getIsDeleted, 0)
        );

        BusiLifeRecord record = new BusiLifeRecord();
        if (existRecord != null) {
            record.setId(existRecord.getId());
        } else {
            record.setStudentId(formDTO.getStudentId());
            record.setClassId(formDTO.getClassId());
            record.setRecordDate(recordDate);
            Long userId = loginHelper.getUserId();
            if (userId != null) {
                record.setCreatorId(userId);
            }
        }

        record.setLunchIntake(formDTO.getLunchIntake());
        record.setWaterCount(formDTO.getWaterCount());
        if (!StringUtils.isEmpty(formDTO.getSleepStartTime())) {
            record.setSleepStartTime(LocalTime.parse(formDTO.getSleepStartTime()));
        }
        record.setSleepDuration(formDTO.getSleepDuration());
        record.setSleepQuality(formDTO.getSleepQuality());
        record.setToiletCount(formDTO.getToiletCount());
        record.setToiletAbnormal(formDTO.getToiletAbnormal());
        record.setMorningTemp(formDTO.getMorningTemp());
        record.setAfternoonTemp(formDTO.getAfternoonTemp());
        record.setHealthSymptoms(formDTO.getHealthSymptoms());
        record.setTeacherRemark(formDTO.getTeacherRemark());

        this.saveOrUpdate(record);
    }

    @Override
    @Transactional
    public void batchSave(LifeRecordBatchSaveDTO batchDTO) {
        if (!loginHelper.isTeacher()) {
            throw new BusinessException("只有教师才能批量保存生活记录");
        }

        List<Long> classIds = loginHelper.getClassIds();
        if (classIds == null || classIds.isEmpty()) {
            throw new BusinessException("您还没有分配班级");
        }

        if (!classIds.contains(batchDTO.getClassId())) {
            throw new BusinessException("您只能操作所任班级的幼儿");
        }

        LocalDate recordDate = LocalDate.parse(batchDTO.getRecordDate(), DateTimeFormatter.ISO_DATE);

        List<BusiLifeRecord> toSave = new ArrayList<>();

        for (Map<String, Object> recordData : batchDTO.getRecords()) {
            BusiLifeRecord record = new BusiLifeRecord();
            record.setStudentId(((Number) recordData.get("studentId")).longValue());
            record.setClassId(batchDTO.getClassId());
            record.setRecordDate(recordDate);
            record.setCreatorId(loginHelper.getUserId());

            if (recordData.get("lunchIntake") != null) {
                record.setLunchIntake(((Number) recordData.get("lunchIntake")).intValue());
            }
            if (recordData.get("waterCount") != null) {
                record.setWaterCount(((Number) recordData.get("waterCount")).intValue());
            }
            if (recordData.get("sleepStartTime") != null) {
                record.setSleepStartTime(LocalTime.parse((String) recordData.get("sleepStartTime")));
            }
            if (recordData.get("sleepDuration") != null) {
                record.setSleepDuration(((Number) recordData.get("sleepDuration")).intValue());
            }
            if (recordData.get("sleepQuality") != null) {
                record.setSleepQuality(((Number) recordData.get("sleepQuality")).intValue());
            }
            if (recordData.get("toiletCount") != null) {
                record.setToiletCount(((Number) recordData.get("toiletCount")).intValue());
            }
            if (recordData.get("toiletAbnormal") != null) {
                record.setToiletAbnormal((String) recordData.get("toiletAbnormal"));
            }
            if (recordData.get("morningTemp") != null) {
                record.setMorningTemp(new BigDecimal(recordData.get("morningTemp").toString()));
            }
            if (recordData.get("afternoonTemp") != null) {
                record.setAfternoonTemp(new BigDecimal(recordData.get("afternoonTemp").toString()));
            }
            if (recordData.get("healthSymptoms") != null) {
                record.setHealthSymptoms((String) recordData.get("healthSymptoms"));
            }
            if (recordData.get("teacherRemark") != null) {
                record.setTeacherRemark((String) recordData.get("teacherRemark"));
            }

            toSave.add(record);
        }

        if (!toSave.isEmpty()) {
            this.saveOrUpdateBatch(toSave);
        }
    }

    @Override
    public ParentLifeRecordVO getParentLifeRecord(Long studentId, LocalDate date) {
        if (!loginHelper.isParent()) {
            throw new BusinessException("只有家长才能查看生活记录");
        }

        Long parentUserId = loginHelper.getUserId();
        List<Long> childIds = busiStudentParentMapper.selectChildIdsByParentUserId(parentUserId);
        if (childIds == null || childIds.isEmpty() || !childIds.contains(studentId)) {
            throw new BusinessException("您只能查看自己孩子的记录");
        }

        BusiStudent student = busiStudentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("幼儿不存在");
        }

        ParentLifeRecordVO vo = new ParentLifeRecordVO();
        vo.setStudentId(student.getId());
        vo.setStudentName(student.getStudentName());
        vo.setClassId(student.getClassId());

        BusiClass busiClass = busiClassMapper.selectById(student.getClassId());
        vo.setClassName(busiClass != null ? busiClass.getClassName() : "");

        LocalDate recordDate = date != null ? date : LocalDate.now();
        vo.setRecordDate(recordDate);

        BusiLifeRecord record = this.getOne(
                new LambdaQueryWrapper<BusiLifeRecord>()
                        .eq(BusiLifeRecord::getStudentId, studentId)
                        .eq(BusiLifeRecord::getRecordDate, recordDate)
                        .eq(BusiLifeRecord::getIsDeleted, 0)
        );

        if (record != null) {
            vo.setId(record.getId());
            vo.setLunchIntake(record.getLunchIntake());
            vo.setLunchIntakeText(getLunchIntakeText(record.getLunchIntake()));
            vo.setWaterCount(record.getWaterCount());
            vo.setSleepStartTime(record.getSleepStartTime() != null ? record.getSleepStartTime().toString() : null);
            vo.setSleepDuration(record.getSleepDuration());
            vo.setSleepQuality(record.getSleepQuality());
            vo.setSleepQualityText(getSleepQualityText(record.getSleepQuality()));
            vo.setToiletCount(record.getToiletCount());
            vo.setToiletAbnormal(record.getToiletAbnormal());
            vo.setMorningTemp(record.getMorningTemp());
            vo.setAfternoonTemp(record.getAfternoonTemp());
            vo.setHealthSymptoms(record.getHealthSymptoms());
            vo.setTeacherRemark(record.getTeacherRemark());
            vo.setCreateTime(record.getCreateTime());
        }

        return vo;
    }

    private String getLunchIntakeText(Integer lunchIntake) {
        if (lunchIntake == null) {
            return null;
        }
        switch (lunchIntake) {
            case 1: return "全吃光";
            case 2: return "吃大半";
            case 3: return "吃小半";
            case 4: return "没胃口";
            default: return null;
        }
    }

    private String getSleepQualityText(Integer sleepQuality) {
        if (sleepQuality == null) {
            return null;
        }
        switch (sleepQuality) {
            case 1: return "深睡";
            case 2: return "浅睡";
            case 3: return "易醒";
            default: return null;
        }
    }
}