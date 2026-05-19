package com.bike.module.system.controller;

import com.bike.common.annotation.Log;
import com.bike.common.result.Result;
import com.bike.mapper.SysUserMapper;
import com.bike.module.auth.entity.SysUser;
import com.bike.security.annotation.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserMapper sysUserMapper;

    @GetMapping("/list")
    @Log("查询用户列表")
    @RequirePermission("system:user:list")
    public Result<List<SysUser>> list(@RequestParam(required = false) String username,
                                      @RequestParam(required = false) Integer status,
                                      @RequestParam(required = false) Integer userType) {
        List<SysUser> list = sysUserMapper.findList(username, status, userType);
        list.forEach(u -> u.setPassword(null));
        return Result.success(list);
    }

    @GetMapping("/{id}")
    @RequirePermission("system:user:list")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserMapper.findById(id);
        if (user != null) user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping
    @Log("新增用户")
    @RequirePermission("system:user:add")
    public Result<Void> add(@RequestBody SysUser user) {
        sysUserMapper.insert(user);
        return Result.success();
    }

    @PutMapping
    @Log("编辑用户")
    @RequirePermission("system:user:edit")
    public Result<Void> edit(@RequestBody SysUser user) {
        sysUserMapper.update(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Log("删除用户")
    @RequirePermission("system:user:delete")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserMapper.deleteById(id);
        return Result.success();
    }
}
