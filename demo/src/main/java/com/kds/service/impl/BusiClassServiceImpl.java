package com.kds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kds.common.exception.BusinessException;
import com.kds.mapper.BusiClassMapper;
import com.kds.mapper.BusiTeacherClassMapper;
import com.kds.model.dto.ClassFormDTO;
import com.kds.model.dto.ClassQueryDTO;
import com.kds.model.entity.BusiClass;
import com.kds.model.entity.BusiTeacherClass;
import com.kds.model.entity.SysUser;
import com.kds.model.vo.ClassVO;
import com.kds.model.vo.TeacherSimpleVO;
import com.kds.service.BusiClassService;
import com.kds.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 班级管理 Service 实现类
 */
@Service
public class BusiClassServiceImpl extends ServiceImpl<BusiClassMapper, BusiClass> implements BusiClassService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private BusiTeacherClassMapper busiTeacherClassMapper;

    /**
     * 分页查询班级列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public Page<ClassVO> getClassPage(ClassQueryDTO queryDTO) {
        // 构建分页参数
        Page<BusiClass> pageParam = new Page<>(queryDTO.getPage(), queryDTO.getSize());

        // 构建查询条件
        LambdaQueryWrapper<BusiClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiClass::getIsDeleted, 0);
        if (StringUtils.hasText(queryDTO.getClassName())) {
            wrapper.like(BusiClass::getClassName, queryDTO.getClassName());
        }
        if (queryDTO.getGrade() != null) {
            wrapper.eq(BusiClass::getGrade, queryDTO.getGrade());
        }
        wrapper.orderByDesc(BusiClass::getCreateTime);

        // 查询班级数据
        Page<BusiClass> classPage = this.page(pageParam, wrapper);

        // 获取班级ID列表，用于查询教师关联
        List<Long> classIds = classPage.getRecords().stream()
                .map(BusiClass::getId)
                .collect(Collectors.toList());

        // 批量查询班级教师关联
        Map<Long, List<BusiTeacherClass>> teacherClassMap;
        if (!classIds.isEmpty()) {
            LambdaQueryWrapper<BusiTeacherClass> tcWrapper = new LambdaQueryWrapper<>();
            tcWrapper.in(BusiTeacherClass::getClassId, classIds);
            List<BusiTeacherClass> teacherClasses = busiTeacherClassMapper.selectList(tcWrapper);
            teacherClassMap = teacherClasses.stream()
                    .collect(Collectors.groupingBy(BusiTeacherClass::getClassId));
        } else {
            teacherClassMap = new java.util.HashMap<>();
        }

        // 获取所有教师ID
        List<Long> allTeacherIds = new ArrayList<>();
        for (List<BusiTeacherClass> tcs : teacherClassMap.values()) {
            for (BusiTeacherClass tc : tcs) {
                allTeacherIds.add(tc.getTeacherUserId());
            }
        }

        // 批量查询教师信息
        Map<Long, SysUser> teacherMap;
        if (!allTeacherIds.isEmpty()) {
            List<SysUser> teachers = sysUserService.listByIds(allTeacherIds);
            teacherMap = teachers.stream()
                    .collect(Collectors.toMap(SysUser::getId, u -> u));
        } else {
            teacherMap = new java.util.HashMap<>();
        }
        final Map<Long, SysUser> finalTeacherMap = teacherMap;

        // 实体转VO，并组装教师列表
        List<ClassVO> voList = classPage.getRecords().stream().map(cls -> {
            ClassVO vo = new ClassVO();
            BeanUtils.copyProperties(cls, vo);

            // 获取班级教师列表
            List<BusiTeacherClass> teacherClassList = teacherClassMap.get(cls.getId());
            List<TeacherSimpleVO> teachers = new ArrayList<>();
            if (teacherClassList != null) {
                for (BusiTeacherClass tc : teacherClassList) {
                    SysUser teacherUser = finalTeacherMap.get(tc.getTeacherUserId());
                    if (teacherUser != null) {
                        TeacherSimpleVO teacherVO = new TeacherSimpleVO();
                        teacherVO.setId(teacherUser.getId());
                        teacherVO.setRealName(teacherUser.getRealName());
                        teacherVO.setIsHeadTeacher(tc.getIsHeadTeacher());
                        teachers.add(teacherVO);
                    }
                }
            }
            vo.setTeachers(teachers);

            return vo;
        }).collect(Collectors.toList());

        // 构建返回的分页对象
        Page<ClassVO> voPage = new Page<>(classPage.getCurrent(), classPage.getSize(), classPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 新增班级
     * @param formDTO 班级表单数据
     */
    @Override
    public void addClass(ClassFormDTO formDTO) {
        // 校验班级名称唯一性
        checkClassNameUnique(formDTO.getClassName(), null);

        // 复制属性到实体
        BusiClass busiClass = new BusiClass();
        BeanUtils.copyProperties(formDTO, busiClass);

        // 保存数据
        this.save(busiClass);
    }

    /**
     * 修改班级
     * @param formDTO 班级表单数据
     */
    @Override
    public void updateClass(ClassFormDTO formDTO) {
        // 校验班级名称唯一性
        checkClassNameUnique(formDTO.getClassName(), formDTO.getId());

        // 复制属性到实体
        BusiClass busiClass = new BusiClass();
        BeanUtils.copyProperties(formDTO, busiClass);

        // 更新数据
        this.updateById(busiClass);
    }

    /**
     * 删除班级
     * @param id 班级ID
     */
    @Override
    public void deleteClass(Long id) {
        // 删除班级时，同时删除教师关联
        LambdaQueryWrapper<BusiTeacherClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiTeacherClass::getClassId, id);
        busiTeacherClassMapper.delete(wrapper);

        // 逻辑删除班级
        this.removeById(id);
    }

    /**
     * 校验班级名称唯一性
     * @param className 班级名称
     * @param excludeId 排除的班级ID（修改时使用）
     */
    private void checkClassNameUnique(String className, Long excludeId) {
        LambdaQueryWrapper<BusiClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiClass::getClassName, className)
               .eq(BusiClass::getIsDeleted, 0);
        if (excludeId != null) {
            wrapper.ne(BusiClass::getId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException("该班级名称已存在！");
        }
    }
}
