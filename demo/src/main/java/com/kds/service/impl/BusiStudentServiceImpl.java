package com.kds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kds.common.exception.BusinessException;
import com.kds.mapper.BusiClassMapper;
import com.kds.mapper.BusiStudentMapper;
import com.kds.mapper.BusiStudentParentMapper;
import com.kds.mapper.SysUserMapper;
import com.kds.model.dto.StudentFormDTO;
import com.kds.model.dto.StudentQueryDTO;
import com.kds.model.entity.BusiClass;
import com.kds.model.entity.BusiStudent;
import com.kds.model.entity.BusiStudentParent;
import com.kds.model.entity.SysUser;
import com.kds.model.vo.ChildInfoVO;
import com.kds.model.vo.StudentDetailVO;
import com.kds.model.vo.StudentVO;
import com.kds.service.BusiStudentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 幼儿管理 Service 实现类
 */
@Service
public class BusiStudentServiceImpl extends ServiceImpl<BusiStudentMapper, BusiStudent> implements BusiStudentService {

    @Autowired
    private BusiStudentParentMapper busiStudentParentMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    @Lazy
    private BusiClassMapper busiClassMapper;

    @Override
    public Page<StudentVO> getStudentPage(StudentQueryDTO queryDTO) {
        Page<BusiStudent> pageParam = new Page<>(queryDTO.getPage(), queryDTO.getSize());

        LambdaQueryWrapper<BusiStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiStudent::getIsDeleted, 0);
        wrapper.eq(BusiStudent::getStatus, queryDTO.getStatus() != null ? queryDTO.getStatus() : 1);

        if (StringUtils.hasText(queryDTO.getStudentName())) {
            wrapper.like(BusiStudent::getStudentName, queryDTO.getStudentName());
        }
        if (queryDTO.getClassId() != null) {
            wrapper.eq(BusiStudent::getClassId, queryDTO.getClassId());
        }
        wrapper.orderByDesc(BusiStudent::getCreateTime);

        Page<BusiStudent> studentPage = this.page(pageParam, wrapper);

        List<Long> studentIds = studentPage.getRecords().stream()
                .map(BusiStudent::getId)
                .collect(Collectors.toList());
        List<Long> classIds = studentPage.getRecords().stream()
                .map(BusiStudent::getClassId)
                .distinct()
                .collect(Collectors.toList());

        final Map<Long, BusiClass> classMap;
        if (!classIds.isEmpty()) {
            List<BusiClass> classes = busiClassMapper.selectBatchIds(classIds);
            classMap = classes.stream().collect(Collectors.toMap(BusiClass::getId, c -> c));
        } else {
            classMap = new java.util.HashMap<>();
        }

        final Map<Long, List<BusiStudentParent>> parentMap;
        if (!studentIds.isEmpty()) {
            LambdaQueryWrapper<BusiStudentParent> parentWrapper = new LambdaQueryWrapper<>();
            parentWrapper.in(BusiStudentParent::getStudentId, studentIds);
            List<BusiStudentParent> parents = busiStudentParentMapper.selectList(parentWrapper);
            parentMap = parents.stream().collect(Collectors.groupingBy(BusiStudentParent::getStudentId));
        } else {
            parentMap = new java.util.HashMap<>();
        }

        List<Long> parentUserIds = new ArrayList<>();
        for (List<BusiStudentParent> pList : parentMap.values()) {
            for (BusiStudentParent p : pList) {
                parentUserIds.add(p.getParentUserId());
            }
        }

        final Map<Long, SysUser> parentUserMap;
        if (!parentUserIds.isEmpty()) {
            List<SysUser> parentUsers = sysUserMapper.selectBatchIds(parentUserIds);
            parentUserMap = parentUsers.stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        } else {
            parentUserMap = new java.util.HashMap<>();
        }

        List<StudentVO> voList = studentPage.getRecords().stream().map(student -> {
            StudentVO vo = new StudentVO();
            BeanUtils.copyProperties(student, vo);

            BusiClass cls = classMap.get(student.getClassId());
            if (cls != null) {
                vo.setClassName(cls.getClassName());
                vo.setGrade(cls.getGrade());
            }

            List<BusiStudentParent> studentParents = parentMap.get(student.getId());
            List<StudentVO.ParentSimpleVO> parentVOList = new ArrayList<>();
            if (studentParents != null) {
                for (BusiStudentParent sp : studentParents) {
                    StudentVO.ParentSimpleVO pvo = new StudentVO.ParentSimpleVO();
                    pvo.setParentUserId(sp.getParentUserId());
                    pvo.setRelation(sp.getRelation());
                    pvo.setIsPrimary(sp.getIsPrimary());
                    SysUser parentUser = parentUserMap.get(sp.getParentUserId());
                    if (parentUser != null) {
                        pvo.setParentName(parentUser.getRealName() != null ? parentUser.getRealName() : parentUser.getUsername());
                    }
                    parentVOList.add(pvo);
                }
            }
            vo.setParents(parentVOList);

            return vo;
        }).collect(Collectors.toList());

        Page<StudentVO> voPage = new Page<>(studentPage.getCurrent(), studentPage.getSize(), studentPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public StudentDetailVO getStudentDetail(Long id) {
        BusiStudent student = this.getById(id);
        if (student == null || student.getIsDeleted() == 1) {
            throw new BusinessException("幼儿不存在");
        }

        StudentDetailVO vo = new StudentDetailVO();
        BeanUtils.copyProperties(student, vo);

        BusiClass cls = busiClassMapper.selectById(student.getClassId());
        if (cls != null) {
            vo.setClassName(cls.getClassName());
            vo.setGrade(cls.getGrade());
        }

        LambdaQueryWrapper<BusiStudentParent> parentWrapper = new LambdaQueryWrapper<>();
        parentWrapper.eq(BusiStudentParent::getStudentId, id);
        List<BusiStudentParent> parents = busiStudentParentMapper.selectList(parentWrapper);

        List<Long> parentUserIds = parents.stream()
                .map(BusiStudentParent::getParentUserId)
                .collect(Collectors.toList());

        Map<Long, SysUser> parentUserMap;
        if (!parentUserIds.isEmpty()) {
            List<SysUser> parentUsers = sysUserMapper.selectBatchIds(parentUserIds);
            parentUserMap = parentUsers.stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        } else {
            parentUserMap = new java.util.HashMap<>();
        }

        List<StudentDetailVO.ParentVO> parentVOList = new ArrayList<>();
        for (BusiStudentParent sp : parents) {
            StudentDetailVO.ParentVO pvo = new StudentDetailVO.ParentVO();
            BeanUtils.copyProperties(sp, pvo);
            SysUser parentUser = parentUserMap.get(sp.getParentUserId());
            if (parentUser != null) {
                pvo.setParentName(parentUser.getRealName() != null ? parentUser.getRealName() : parentUser.getUsername());
                pvo.setParentPhone(parentUser.getPhone());
            }
            parentVOList.add(pvo);
        }
        vo.setParents(parentVOList);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStudent(StudentFormDTO formDTO) {
        BusiStudent student = new BusiStudent();
        BeanUtils.copyProperties(formDTO, student);
        if (student.getStatus() == null) {
            student.setStatus(1);
        }
        this.save(student);

        if (formDTO.getParents() != null && !formDTO.getParents().isEmpty()) {
            saveParents(student.getId(), formDTO.getParents());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStudent(StudentFormDTO formDTO) {
        BusiStudent student = new BusiStudent();
        BeanUtils.copyProperties(formDTO, student);
        this.updateById(student);

        LambdaQueryWrapper<BusiStudentParent> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(BusiStudentParent::getStudentId, formDTO.getId());
        busiStudentParentMapper.delete(deleteWrapper);

        if (formDTO.getParents() != null && !formDTO.getParents().isEmpty()) {
            saveParents(formDTO.getId(), formDTO.getParents());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStudent(Long id) {
        LambdaQueryWrapper<BusiStudentParent> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(BusiStudentParent::getStudentId, id);
        busiStudentParentMapper.delete(deleteWrapper);

        this.removeById(id);
    }

    @Override
    public List<ParentOptionVO> getParentList() {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserType, 2)
               .eq(SysUser::getIsDeleted, 0);
        List<SysUser> users = sysUserMapper.selectList(wrapper);

        return users.stream().map(user -> {
            ParentOptionVO vo = new ParentOptionVO();
            vo.setUserId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setRealName(user.getRealName());
            vo.setPhone(user.getPhone());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ClassOptionVO> getClassList() {
        LambdaQueryWrapper<BusiClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiClass::getIsDeleted, 0);
        List<BusiClass> classes = busiClassMapper.selectList(wrapper);

        return classes.stream().map(cls -> {
            ClassOptionVO vo = new ClassOptionVO();
            vo.setId(cls.getId());
            vo.setClassName(cls.getClassName());
            vo.setGrade(cls.getGrade());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ChildInfoVO> getParentChildren(Long parentUserId) {
        List<Long> childIds = busiStudentParentMapper.selectChildIdsByParentUserId(parentUserId);

        if (childIds == null || childIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<BusiStudent> students = this.listByIds(childIds);
        if (students == null || students.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> classIds = students.stream()
                .map(BusiStudent::getClassId)
                .distinct()
                .collect(Collectors.toList());

        final Map<Long, BusiClass> classMap;
        if (!classIds.isEmpty()) {
            List<BusiClass> classes = busiClassMapper.selectBatchIds(classIds);
            classMap = classes.stream().collect(Collectors.toMap(BusiClass::getId, c -> c));
        } else {
            classMap = new java.util.HashMap<>();
        }

        return students.stream().map(student -> {
            ChildInfoVO vo = new ChildInfoVO();
            vo.setStudentId(student.getId());
            vo.setStudentName(student.getStudentName());
            vo.setClassId(student.getClassId());
            BusiClass cls = classMap.get(student.getClassId());
            if (cls != null) {
                vo.setClassName(cls.getClassName());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    private void saveParents(Long studentId, List<StudentFormDTO.StudentParentDTO> parents) {
        for (StudentFormDTO.StudentParentDTO dto : parents) {
            if (dto.getParentUserId() == null || dto.getRelation() == null) {
                continue;
            }
            BusiStudentParent parent = new BusiStudentParent();
            parent.setStudentId(studentId);
            parent.setParentUserId(dto.getParentUserId());
            parent.setRelation(dto.getRelation());
            parent.setIsPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : 0);
            busiStudentParentMapper.insert(parent);
        }
    }
}
