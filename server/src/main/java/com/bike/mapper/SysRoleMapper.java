package com.bike.mapper;

import com.bike.module.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMapper {
    List<SysRole> getRolesByUserId(@Param("userId") Long userId);
    SysRole findById(@Param("id") Long id);
    List<SysRole> findList(@Param("roleName") String roleName, @Param("status") Integer status);
    int insert(SysRole role);
    int update(SysRole role);
    int deleteById(@Param("id") Long id);
    List<Long> getMenuIdsByRoleId(@Param("roleId") Long roleId);
    int deleteRoleMenus(@Param("roleId") Long roleId);
    int insertRoleMenus(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);
}
