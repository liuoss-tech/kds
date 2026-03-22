package com.kds.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kds.model.entity.SysRolePermission;

import java.util.List;

public interface SysRolePermissionService extends IService<SysRolePermission> {

    void assignPermissions(Long roleId, List<Long> permissionIds);

    List<Long> getPermissionIdsByRoleId(Long roleId);
}
