package com.kds.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kds.model.dto.LeaveApproveDTO;
import com.kds.model.dto.LeaveFormDTO;
import com.kds.model.dto.LeaveQueryDTO;
import com.kds.model.entity.BusiLeaveApplication;
import com.kds.model.vo.LeaveVO;
import com.kds.model.vo.StudentSimpleVO;

import java.util.List;

/**
 * 请假申请 Service 接口
 */
public interface BusiLeaveApplicationService extends IService<BusiLeaveApplication> {

    /**
     * 家长提交请假申请
     * @param formDTO 请假表单数据
     */
    void addLeave(LeaveFormDTO formDTO);

    /**
     * 家长获取请假列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<LeaveVO> getParentLeavePage(LeaveQueryDTO queryDTO);

    /**
     * 班主任获取请假列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<LeaveVO> getTeacherLeavePage(LeaveQueryDTO queryDTO);

    /**
     * 管理员获取请假列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<LeaveVO> getAdminLeavePage(LeaveQueryDTO queryDTO);

    /**
     * 审批请假（班主任审批）
     * @param approveDTO 审批数据
     */
    void approveLeave(LeaveApproveDTO approveDTO);

    /**
     * 获取家长绑定的幼儿列表
     * @return 幼儿列表
     */
    List<StudentSimpleVO> getMyStudents();
}
