package com.kds.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kds.model.dto.ActivityFormDTO;
import com.kds.model.vo.ActivityDetailVO;
import com.kds.model.vo.ActivityVO;
import com.kds.model.vo.RegistrationVO;

import java.util.List;

public interface BusiActivityService extends IService<com.kds.model.entity.BusiActivity> {

    Page<ActivityVO> getTeacherActivityPage(Integer status, Integer page, Integer size);

    Page<ActivityVO> getParentActivityPage(String tab, Integer page, Integer size);

    void publishActivity(ActivityFormDTO formDTO);

    ActivityDetailVO getActivityDetail(Long id);

    List<RegistrationVO> getActivityRegistrations(Long activityId);

    void registerActivity(Long activityId, Long studentId, Integer participantCount, String remark);

    void cancelRegistration(Long registrationId);

    List<Long> getAvailableStudentIds(Long activityId);

    int calculateActivityStatus(com.kds.model.entity.BusiActivity activity);
}