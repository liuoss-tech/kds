package com.kds.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kds.model.dto.RoleFormDTO;
import com.kds.model.dto.RoleQueryDTO;
import com.kds.model.entity.SysRole;

public interface SysRoleService extends IService<SysRole> {

    Page<SysRole> getRolePage(RoleQueryDTO queryDTO);

    void addRole(RoleFormDTO formDTO);

    void updateRole(RoleFormDTO formDTO);

    void deleteRole(Long id);
}
