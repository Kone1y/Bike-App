package com.bike.module.system.controller;

import com.bike.common.annotation.Log;
import com.bike.common.result.Result;
import com.bike.mapper.SysRoleMapper;
import com.bike.module.system.entity.SysRole;
import com.bike.security.annotation.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final SysRoleMapper sysRoleMapper;

    @GetMapping("/list")
    @Log("查询角色列表")
    public Result<List<SysRole>> list(@RequestParam(required = false) String roleName,
                                      @RequestParam(required = false) Integer status) {
        return Result.success(sysRoleMapper.findList(roleName, status));
    }

    @GetMapping("/{id}")
    public Result<SysRole> getById(@PathVariable Long id) {
        return Result.success(sysRoleMapper.findById(id));
    }

    @GetMapping("/{id}/menus")
    public Result<List<Long>> getRoleMenuIds(@PathVariable Long id) {
        return Result.success(sysRoleMapper.getMenuIdsByRoleId(id));
    }

    @PostMapping("/{id}/menus")
    @Log("分配角色菜单权限")
    @RequirePermission("system:role:edit")
    public Result<Void> assignRoleMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        sysRoleMapper.deleteRoleMenus(id);
        if (menuIds != null && !menuIds.isEmpty()) {
            sysRoleMapper.insertRoleMenus(id, menuIds);
        }
        return Result.success();
    }

    @PostMapping
    @Log("新增角色")
    @RequirePermission("system:role:add")
    public Result<Void> add(@RequestBody SysRole role) {
        sysRoleMapper.insert(role);
        return Result.success();
    }

    @PutMapping
    @Log("编辑角色")
    @RequirePermission("system:role:edit")
    public Result<Void> edit(@RequestBody SysRole role) {
        sysRoleMapper.update(role);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Log("删除角色")
    @RequirePermission("system:role:delete")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleMapper.deleteById(id);
        return Result.success();
    }
}
