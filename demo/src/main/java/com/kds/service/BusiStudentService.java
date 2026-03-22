package com.kds.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kds.model.dto.StudentFormDTO;
import com.kds.model.dto.StudentQueryDTO;
import com.kds.model.entity.BusiStudent;
import com.kds.model.vo.ChildInfoVO;
import com.kds.model.vo.StudentDetailVO;
import com.kds.model.vo.StudentVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 幼儿管理 Service 接口
 */
public interface BusiStudentService extends IService<BusiStudent> {

    /**
     * 分页查询幼儿列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<StudentVO> getStudentPage(StudentQueryDTO queryDTO);

    /**
     * 获取幼儿详情
     * @param id 幼儿ID
     * @return 幼儿详情
     */
    StudentDetailVO getStudentDetail(Long id);

    /**
     * 新增幼儿
     * @param formDTO 幼儿表单数据
     */
    void addStudent(StudentFormDTO formDTO);

    /**
     * 修改幼儿
     * @param formDTO 幼儿表单数据
     */
    void updateStudent(StudentFormDTO formDTO);

    /**
     * 删除幼儿
     * @param id 幼儿ID
     */
    void deleteStudent(Long id);

    /**
     * 获取家长用户列表（用于选择）
     * @return 家长用户列表
     */
    List<ParentOptionVO> getParentList();

    /**
     * 获取班级列表（用于下拉选择）
     * @return 班级列表
     */
    List<ClassOptionVO> getClassList();

    /**
     * 获取家长关联的幼儿列表
     * @param parentUserId 家长用户ID
     * @return 幼儿信息列表
     */
    List<ChildInfoVO> getParentChildren(Long parentUserId);

    /**
     * 家长下拉选项 VO
     */
    @Data
    class ParentOptionVO {
        private Long userId;
        private String username;
        private String realName;
        private String phone;
    }

    /**
     * 班级下拉选项 VO
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    class ClassOptionVO {
        private Long id;
        private String className;
        private Integer grade;
    }
}
