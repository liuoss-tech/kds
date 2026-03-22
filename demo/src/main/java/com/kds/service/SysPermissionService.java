package com.kds.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kds.model.entity.SysPermission;
import com.kds.model.vo.PermissionTreeVO;

import java.util.List;

public interface SysPermissionService extends IService<SysPermission> {

    List<PermissionTreeVO> getPermissionTree();

    List<SysPermission> getMyMenus(Long userId);
}
