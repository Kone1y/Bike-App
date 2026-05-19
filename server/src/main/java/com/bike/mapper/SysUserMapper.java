package com.bike.mapper;

import com.bike.module.auth.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper {
    SysUser findByUsername(@Param("username") String username);
    SysUser findById(@Param("id") Long id);
    List<SysUser> findList(@Param("username") String username, @Param("status") Integer status, @Param("userType") Integer userType);
    int insert(SysUser user);
    int update(SysUser user);
    int deleteById(@Param("id") Long id);
}
