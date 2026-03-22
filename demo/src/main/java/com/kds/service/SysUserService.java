package com.kds.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kds.model.dto.LoginDTO;
import com.kds.model.dto.UserFormDTO;
import com.kds.model.dto.UserQueryDTO;
import com.kds.model.entity.SysUser;
import com.kds.model.vo.CurrentUserVO;
import com.kds.model.vo.LoginVO;
import com.kds.model.vo.UserVO;

public interface SysUserService extends IService<SysUser> {
    LoginVO login(LoginDTO loginDTO);

    CurrentUserVO getCurrentUserInfo();

    Page<UserVO> getUserPage(UserQueryDTO queryDTO);

    void addUser(UserFormDTO formDTO);

    void updateUser(UserFormDTO formDTO);

    void deleteUser(Long id);
}
