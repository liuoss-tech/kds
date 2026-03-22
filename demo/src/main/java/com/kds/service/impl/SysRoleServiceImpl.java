package com.kds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kds.common.exception.BusinessException;
import com.kds.mapper.SysRoleMapper;
import com.kds.mapper.SysUserRoleMapper;
import com.kds.model.dto.RoleFormDTO;
import com.kds.model.dto.RoleQueryDTO;
import com.kds.model.entity.SysRole;
import com.kds.model.entity.SysUserRole;
import com.kds.service.SysRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public Page<SysRole> getRolePage(RoleQueryDTO queryDTO) {
        Page<SysRole> pageParam = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryDTO.getRoleName())) {
            wrapper.like(SysRole::getRoleName, queryDTO.getRoleName());
        }
        if (StringUtils.hasText(queryDTO.getRoleCode())) {
            wrapper.like(SysRole::getRoleCode, queryDTO.getRoleCode());
        }
        wrapper.orderByDesc(SysRole::getCreateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public void addRole(RoleFormDTO formDTO) {
        checkRoleCodeUnique(formDTO.getRoleCode(), null);
        SysRole role = new SysRole();
        BeanUtils.copyProperties(formDTO, role);
        this.save(role);
    }

    @Override
    public void updateRole(RoleFormDTO formDTO) {
        checkRoleCodeUnique(formDTO.getRoleCode(), formDTO.getId());
        SysRole role = new SysRole();
        BeanUtils.copyProperties(formDTO, role);
        this.updateById(role);
    }

    @Override
    public void deleteRole(Long id) {
        Long count = sysUserRoleMapper.selectCount(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, id)
        );
        if (count > 0) {
            throw new BusinessException("当前角色下还有绑定的用户，无法删除！请先解除用户关联。");
        }
        this.removeById(id);
    }

    private void checkRoleCodeUnique(String roleCode, Long excludeId) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, roleCode);
        if (excludeId != null) {
            wrapper.ne(SysRole::getId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException("角色编码已存在，请更换！");
        }
    }
}
