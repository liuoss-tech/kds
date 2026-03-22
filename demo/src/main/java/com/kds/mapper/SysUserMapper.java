package com.kds.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kds.model.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
