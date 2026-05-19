package com.bike.module.scenic.controller;

import com.bike.common.annotation.Log;
import com.bike.common.result.Result;
import com.bike.mapper.ScenicSpotMapper;
import com.bike.module.scenic.entity.ScenicSpot;
import com.bike.security.annotation.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scenic/spot")
@RequiredArgsConstructor
public class ScenicSpotController {

    private final ScenicSpotMapper scenicSpotMapper;

    @GetMapping("/list")
    @Log("查询景点列表")
    @RequirePermission("scenic:list")
    public Result<List<ScenicSpot>> list(@RequestParam(required = false) String name) {
        return Result.success(scenicSpotMapper.findList(name));
    }

    @GetMapping("/{id}")
    @RequirePermission("scenic:list")
    public Result<ScenicSpot> getById(@PathVariable Long id) {
        return Result.success(scenicSpotMapper.findById(id));
    }

    @PostMapping
    @Log("新增景点")
    @RequirePermission("scenic:add")
    public Result<Void> add(@RequestBody ScenicSpot spot) {
        scenicSpotMapper.insert(spot);
        return Result.success();
    }

    @PutMapping
    @Log("编辑景点")
    @RequirePermission("scenic:edit")
    public Result<Void> edit(@RequestBody ScenicSpot spot) {
        scenicSpotMapper.update(spot);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Log("删除景点")
    @RequirePermission("scenic:delete")
    public Result<Void> delete(@PathVariable Long id) {
        scenicSpotMapper.deleteById(id);
        return Result.success();
    }
}
