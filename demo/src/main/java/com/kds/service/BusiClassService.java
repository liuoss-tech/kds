package com.kds.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kds.model.dto.ClassFormDTO;
import com.kds.model.dto.ClassQueryDTO;
import com.kds.model.entity.BusiClass;
import com.kds.model.vo.ClassVO;

/**
 * 班级管理 Service 接口
 */
public interface BusiClassService extends IService<BusiClass> {

    /**
     * 分页查询班级列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<ClassVO> getClassPage(ClassQueryDTO queryDTO);

    /**
     * 新增班级
     * @param formDTO 班级表单数据
     */
    void addClass(ClassFormDTO formDTO);

    /**
     * 修改班级
     * @param formDTO 班级表单数据
     */
    void updateClass(ClassFormDTO formDTO);

    /**
     * 删除班级
     * @param id 班级ID
     */
    void deleteClass(Long id);
}
