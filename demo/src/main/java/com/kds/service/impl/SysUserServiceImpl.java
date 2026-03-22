package com.kds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kds.common.exception.BusinessException;
import com.kds.mapper.SysRoleMapper;
import com.kds.mapper.SysUserMapper;
import com.kds.mapper.SysUserRoleMapper;
import com.kds.model.dto.LoginDTO;
import com.kds.model.dto.UserFormDTO;
import com.kds.model.dto.UserQueryDTO;
import com.kds.model.entity.SysRole;
import com.kds.model.entity.SysUser;
import com.kds.model.entity.SysUserRole;
import com.kds.model.vo.CurrentUserVO;
import com.kds.model.vo.LoginVO;
import com.kds.model.vo.UserVO;
import com.kds.service.SysUserService;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, loginDTO.getUsername())
                .eq(SysUser::getIsDeleted, 0));

        if (user == null) {
            throw new BusinessException("账号或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("该账号已被禁用，请联系幼儿园管理员");
        }

        boolean isPasswordMatch = loginDTO.getPassword().equals(user.getPassword());
        if (!isPasswordMatch) {
            throw new BusinessException("账号或密码错误");
        }

        SysUserRole userRole = sysUserRoleMapper.selectOne(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, user.getId())
                        .last("LIMIT 1")
        );

        String roleCode = "UNKNOWN";
        if (userRole != null) {
            SysRole role = sysRoleMapper.selectById(userRole.getRoleId());
            if (role != null) {
                roleCode = role.getRoleCode();
            }
        }

        StpUtil.login(user.getId());

        LoginVO vo = new LoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setUserId(user.getId());
        vo.setRealName(user.getRealName());
        vo.setUserType(user.getUserType());
        vo.setRoleCode(roleCode);

        return vo;
    }

    @Override
    public CurrentUserVO getCurrentUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = this.getById(userId);
        
        CurrentUserVO vo = new CurrentUserVO();
        vo.setUserId(user.getId());
        vo.setRealName(user.getRealName());
        vo.setUserType(user.getUserType());
        
        SysUserRole userRole = sysUserRoleMapper.selectOne(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId)
                        .last("LIMIT 1")
        );

        String roleCode = "UNKNOWN";
        String roleName = "未知角色";
        if (userRole != null) {
            SysRole role = sysRoleMapper.selectById(userRole.getRoleId());
            if (role != null) {
                roleCode = role.getRoleCode();
                roleName = role.getRoleName();
            }
        }
        vo.setRoleCode(roleCode);
        vo.setRoleName(roleName);
        
        return vo;
    }

    @Override
    public Page<UserVO> getUserPage(UserQueryDTO queryDTO) {
        Page<SysUser> pageParam = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(SysUser::getIsDeleted, 0);
        if (StringUtils.hasText(queryDTO.getRealName())) {
            wrapper.like(SysUser::getRealName, queryDTO.getRealName());
        }
        if (StringUtils.hasText(queryDTO.getUsername())) {
            wrapper.like(SysUser::getUsername, queryDTO.getUsername());
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> userPage = this.page(pageParam, wrapper);

        List<UserVO> voList = userPage.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);

            SysUserRole userRole = sysUserRoleMapper.selectOne(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId()).last("LIMIT 1")
            );
            if (userRole != null) {
                vo.setRoleId(userRole.getRoleId());
                SysRole role = sysRoleMapper.selectById(userRole.getRoleId());
                vo.setRoleName(role != null ? role.getRoleName() : "未知");
            }
            return vo;
        }).collect(Collectors.toList());

        Page<UserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(UserFormDTO formDTO) {
        checkUsernameUnique(formDTO.getUsername(), null);

        SysUser user = new SysUser();
        BeanUtils.copyProperties(formDTO, user);

        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword("123456");
        }

        this.save(user);

        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(formDTO.getRoleId());
        sysUserRoleMapper.insert(userRole);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserFormDTO formDTO) {
        checkUsernameUnique(formDTO.getUsername(), formDTO.getId());

        SysUser user = new SysUser();
        BeanUtils.copyProperties(formDTO, user);

        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword(null);
        }
        this.updateById(user);

        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId()));
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(formDTO.getRoleId());
        sysUserRoleMapper.insert(userRole);
    }

    @Override
    public void deleteUser(Long id) {
        // 使用 UpdateWrapper 绕过 MyBatis Plus 的逻辑删除自动过滤
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<SysUser> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        wrapper.eq("id", id).set("is_deleted", 1);
        this.update(null, wrapper);
    }

    private void checkUsernameUnique(String username, Long excludeId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username).eq(SysUser::getIsDeleted, 0);
        if (excludeId != null) {
            wrapper.ne(SysUser::getId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException("该手机号/账号已被注册！");
        }
    }
}
