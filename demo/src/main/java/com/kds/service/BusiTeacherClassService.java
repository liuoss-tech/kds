package com.kds.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kds.model.entity.BusiTeacherClass;
import com.kds.model.entity.SysUser;

import java.util.List;

/**
 * 教师班级分配 Service 接口
 */
public interface BusiTeacherClassService extends IService<BusiTeacherClass> {

    /**
     * 获取班级下的所有教师列表
     * @param classId 班级ID
     * @return 教师列表
     */
    List<SysUser> getTeachersByClassId(Long classId);

    /**
     * 分配教师到班级
     * @param classId 班级ID
     * @param teacherIds 教师ID列表
     * @param isHeadTeacher 是否设置为班主任
     */
    void assignTeachers(Long classId, List<Long> teacherIds, Integer isHeadTeacher);
}
