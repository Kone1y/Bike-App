package com.bike.module.system.controller;

import com.bike.common.annotation.Log;
import com.bike.common.result.Result;
import com.bike.mapper.SysMenuMapper;
import com.bike.module.system.entity.SysMenu;
import com.bike.security.annotation.RequirePermission;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class MenuController {

    private final SysMenuMapper sysMenuMapper;

    @GetMapping("/tree")
    public Result<List<SysMenu>> tree() {
        List<SysMenu> allMenus = sysMenuMapper.findList();
        List<SysMenu> tree = buildTree(allMenus, 0L);
        return Result.success(tree);
    }

    @GetMapping("/user-menus")
    public Result<List<SysMenu>> userMenus(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<SysMenu> menus = sysMenuMapper.getMenusByUserId(userId);
        List<SysMenu> tree = buildTree(menus, 0L);
        return Result.success(tree);
    }

    @PostMapping
    @Log("新增菜单")
    @RequirePermission("system:menu:add")
    public Result<Void> add(@RequestBody SysMenu menu) {
        sysMenuMapper.insert(menu);
        return Result.success();
    }

    @PutMapping
    @Log("编辑菜单")
    @RequirePermission("system:menu:edit")
    public Result<Void> edit(@RequestBody SysMenu menu) {
        sysMenuMapper.update(menu);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Log("删除菜单")
    @RequirePermission("system:menu:delete")
    public Result<Void> delete(@PathVariable Long id) {
        sysMenuMapper.deleteById(id);
        return Result.success();
    }

    private List<SysMenu> buildTree(List<SysMenu> all, Long parentId) {
        return all.stream()
                .filter(m -> m.getParentId().equals(parentId))
                .peek(m -> m.setChildren(buildTree(all, m.getId())))
                .collect(Collectors.toList());
    }
}
