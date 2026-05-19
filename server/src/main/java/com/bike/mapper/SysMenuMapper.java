package com.bike.mapper;

import com.bike.module.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysMenuMapper {
    List<SysMenu> findList();
    List<SysMenu> getMenusByUserId(@Param("userId") Long userId);
    List<String> getPermissionsByUserId(@Param("userId") Long userId);
    int insert(SysMenu menu);
    int update(SysMenu menu);
    int deleteById(@Param("id") Long id);
}
