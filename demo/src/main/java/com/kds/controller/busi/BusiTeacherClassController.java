package com.kds.controller.busi;

import com.kds.common.context.LoginHelper;
import com.kds.common.result.Result;
import com.kds.mapper.BusiClassMapper;
import com.kds.model.dto.TeacherClassAssignDTO;
import com.kds.model.entity.BusiClass;
import com.kds.model.entity.SysUser;
import com.kds.service.BusiTeacherClassService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 教师班级分配 Controller
 */
@RestController
@RequestMapping("/api/busi/teacher-class")
public class BusiTeacherClassController {

    @Autowired
    private BusiTeacherClassService busiTeacherClassService;

    @Autowired
    private BusiClassMapper busiClassMapper;

    @Autowired
    private LoginHelper loginHelper;

    /**
     * 获取当前用户的班级列表
     * 管理员/园长返回所有班级，教师返回所任班级
     * @return 班级列表
     */
    @GetMapping("/my-classes")
    public Result<List<ClassVO>> getMyClasses() {
        List<Long> classIds;
        if (loginHelper.isAdmin()) {
            classIds = busiClassMapper.selectList(null).stream()
                    .map(BusiClass::getId)
                    .collect(Collectors.toList());
        } else {
            classIds = loginHelper.getClassIds();
        }

        if (classIds == null || classIds.isEmpty()) {
            return Result.success(List.of());
        }

        List<BusiClass> classes = busiClassMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BusiClass>()
                        .in(BusiClass::getId, classIds)
                        .eq(BusiClass::getIsDeleted, 0)
        );

        List<ClassVO> result = classes.stream().map(cls -> {
            ClassVO vo = new ClassVO();
            vo.setId(cls.getId());
            vo.setClassName(cls.getClassName());
            vo.setGrade(cls.getGrade());
            return vo;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    /**
     * 获取班级下的所有教师列表
     * @param classId 班级ID
     * @return 教师列表
     */
    @GetMapping("/list/{classId}")
    public Result<List<SysUser>> getTeachersByClassId(@PathVariable Long classId) {
        return Result.success(busiTeacherClassService.getTeachersByClassId(classId));
    }

    /**
     * 分配教师到班级
     * @param assignDTO 分配参数
     * @return 操作结果
     */
    @PostMapping("/assign")
    public Result<?> assignTeachers(@RequestBody TeacherClassAssignDTO assignDTO) {
        busiTeacherClassService.assignTeachers(
                assignDTO.getClassId(),
                assignDTO.getTeacherIds(),
                assignDTO.getIsHeadTeacher()
        );
        return Result.success("教师分配成功");
    }

    @Data
    public static class ClassVO {
        private Long id;
        private String className;
        private Integer grade;
    }
}
