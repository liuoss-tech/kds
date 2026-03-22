package com.kds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kds.mapper.BusiTeacherClassMapper;
import com.kds.model.entity.BusiTeacherClass;
import com.kds.model.entity.SysUser;
import com.kds.service.BusiTeacherClassService;
import com.kds.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教师班级分配 Service 实现类
 */
@Service
public class BusiTeacherClassServiceImpl extends ServiceImpl<BusiTeacherClassMapper, BusiTeacherClass> implements BusiTeacherClassService {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 获取班级下的所有教师列表
     * @param classId 班级ID
     * @return 教师列表
     */
    @Override
    public List<SysUser> getTeachersByClassId(Long classId) {
        // 查询该班级的所有教师关联
        LambdaQueryWrapper<BusiTeacherClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiTeacherClass::getClassId, classId);
        List<BusiTeacherClass> teacherClasses = this.list(wrapper);

        if (teacherClasses.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取教师ID列表
        List<Long> teacherIds = teacherClasses.stream()
                .map(BusiTeacherClass::getTeacherUserId)
                .collect(Collectors.toList());

        // 查询教师信息
        return sysUserService.listByIds(teacherIds);
    }

    /**
     * 分配教师到班级
     * @param classId 班级ID
     * @param teacherIds 教师ID列表
     * @param isHeadTeacher 是否设置为班主任
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTeachers(Long classId, List<Long> teacherIds, Integer isHeadTeacher) {
        // 先删除该班级的所有教师关联
        LambdaQueryWrapper<BusiTeacherClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiTeacherClass::getClassId, classId);
        this.remove(wrapper);

        // 重新插入新的教师关联
        if (teacherIds != null && !teacherIds.isEmpty()) {
            for (Long teacherId : teacherIds) {
                BusiTeacherClass teacherClass = new BusiTeacherClass();
                teacherClass.setClassId(classId);
                teacherClass.setTeacherUserId(teacherId);
                teacherClass.setIsHeadTeacher(isHeadTeacher != null ? isHeadTeacher : 0);
                this.save(teacherClass);
            }
        }
    }
}
