package com.kds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kds.mapper.SysPermissionMapper;
import com.kds.mapper.SysRolePermissionMapper;
import com.kds.mapper.SysUserRoleMapper;
import com.kds.model.entity.SysPermission;
import com.kds.model.entity.SysRolePermission;
import com.kds.model.entity.SysUserRole;
import com.kds.model.vo.PermissionTreeVO;
import com.kds.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public List<PermissionTreeVO> getPermissionTree() {
        List<SysPermission> allPerms = this.list();

        List<PermissionTreeVO> allNodes = allPerms.stream()
                .filter(perm -> perm.getId() != 4 && perm.getId() != 5)
                .map(perm -> {
                    PermissionTreeVO vo = new PermissionTreeVO();
                    vo.setId(perm.getId());
                    vo.setParentId(perm.getParentId());
                    vo.setName(perm.getPermName());
                    return vo;
                }).collect(Collectors.toList());

        return allNodes.stream()
                .filter(node -> node.getParentId() == 0L)
                .peek(node -> node.setChildren(getChildren(node, allNodes)))
                .collect(Collectors.toList());
    }

    private List<PermissionTreeVO> getChildren(PermissionTreeVO root, List<PermissionTreeVO> allNodes) {
        return allNodes.stream()
                .filter(node -> node.getParentId().equals(root.getId()))
                .peek(node -> node.setChildren(getChildren(node, allNodes)))
                .collect(Collectors.toList());
    }

    @Override
    public List<SysPermission> getMyMenus(Long userId) {
        List<SysPermission> result = new ArrayList<>();
        
        SysPermission dashboardParent = this.getById(4L);
        if (dashboardParent != null) {
            result.add(dashboardParent);
        }
        SysPermission dashboardChild = this.getById(5L);
        if (dashboardChild != null) {
            result.add(dashboardChild);
        }

        List<Long> roleIds = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        ).stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

        if (!roleIds.isEmpty()) {
            List<Long> permIds = sysRolePermissionMapper.selectList(
                    new LambdaQueryWrapper<SysRolePermission>().in(SysRolePermission::getRoleId, roleIds)
            ).stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());

            if (!permIds.isEmpty()) {
                List<SysPermission> otherMenus = this.list(new LambdaQueryWrapper<SysPermission>()
                        .in(SysPermission::getId, permIds)
                        .in(SysPermission::getPermType, 1, 2)
                        .ne(SysPermission::getId, 4L)
                        .ne(SysPermission::getId, 5L)
                        .orderByAsc(SysPermission::getSortOrder)
                );
                result.addAll(otherMenus);
            }
        }

        return result;
    }
}
