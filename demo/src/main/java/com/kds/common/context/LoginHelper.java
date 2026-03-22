package com.kds.common.context;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kds.mapper.BusiTeacherClassMapper;
import com.kds.mapper.BusiStudentParentMapper;
import com.kds.mapper.SysUserMapper;
import com.kds.model.entity.BusiTeacherClass;
import com.kds.model.entity.SysRole;
import com.kds.model.entity.SysUser;
import com.kds.model.entity.SysUserRole;
import com.kds.mapper.SysUserRoleMapper;
import com.kds.mapper.SysRoleMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LoginHelper {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private BusiTeacherClassMapper busiTeacherClassMapper;

    @Resource
    private BusiStudentParentMapper busiStudentParentMapper;

    public Long getUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    public SysUser getUser() {
        Long userId = getUserId();
        return sysUserMapper.selectById(userId);
    }

    public String getRoleCode() {
        SysUser user = getUser();
        if (user == null) {
            return null;
        }
        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SysUserRole::getUserId, user.getId());
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(userRoleWrapper);
        
        if (userRoles == null || userRoles.isEmpty()) {
            return null;
        }
        
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        List<SysRole> roles = sysRoleMapper.selectBatchIds(roleIds);
        
        if (roles != null && !roles.isEmpty()) {
            return roles.get(0).getRoleCode();
        }
        return null;
    }

    public Integer getDataScope() {
        SysUser user = getUser();
        if (user == null) {
            return 1;
        }
        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SysUserRole::getUserId, user.getId());
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(userRoleWrapper);
        
        if (userRoles == null || userRoles.isEmpty()) {
            return 1;
        }
        
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        List<SysRole> roles = sysRoleMapper.selectBatchIds(roleIds);
        
        if (roles != null && !roles.isEmpty()) {
            return roles.get(0).getDataScope();
        }
        return 1;
    }

    public List<Long> getClassIds() {
        Long userId = getUserId();
        LambdaQueryWrapper<BusiTeacherClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiTeacherClass::getTeacherUserId, userId);
        List<BusiTeacherClass> teacherClasses = busiTeacherClassMapper.selectList(wrapper);
        if (teacherClasses == null || teacherClasses.isEmpty()) {
            return Collections.emptyList();
        }
        return teacherClasses.stream()
                .map(BusiTeacherClass::getClassId)
                .collect(Collectors.toList());
    }

    public List<Long> getChildIds() {
        Long userId = getUserId();
        return busiStudentParentMapper.selectChildIdsByParentUserId(userId);
    }

    public boolean isAdmin() {
        SysUser user = getUser();
        if (user == null) return false;
        String roleCode = getRoleCode();
        return "SUPER_ADMIN".equals(roleCode) || "PRINCIPAL".equals(roleCode);
    }

    public boolean isTeacher() {
        SysUser user = getUser();
        if (user == null) return false;
        return user.getUserType() != null && user.getUserType() == 1;
    }

    public boolean isParent() {
        SysUser user = getUser();
        if (user == null) return false;
        return user.getUserType() != null && user.getUserType() == 2;
    }
}
