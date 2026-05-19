package com.bike.module.area.controller;

import com.bike.common.annotation.Log;
import com.bike.common.result.Result;
import com.bike.mapper.ParkingAreaMapper;
import com.bike.module.area.entity.ParkingArea;
import com.bike.security.annotation.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scenic/area")
@RequiredArgsConstructor
public class ParkingAreaController {

    private final ParkingAreaMapper parkingAreaMapper;

    @GetMapping("/list")
    @Log("查询停车区域列表")
    @RequirePermission("area:list")
    public Result<List<ParkingArea>> list(@RequestParam(required = false) String name) {
        return Result.success(parkingAreaMapper.findList(name));
    }

    @GetMapping("/{id}")
    @RequirePermission("area:list")
    public Result<ParkingArea> getById(@PathVariable Long id) {
        return Result.success(parkingAreaMapper.findById(id));
    }

    @PostMapping
    @Log("新增停车区域")
    @RequirePermission("area:add")
    public Result<Void> add(@RequestBody ParkingArea area) {
        if (area.getRadius() == null) {
            area.setRadius(200);
        }
        parkingAreaMapper.insert(area);
        return Result.success();
    }

    @PutMapping
    @Log("编辑停车区域")
    @RequirePermission("area:edit")
    public Result<Void> edit(@RequestBody ParkingArea area) {
        parkingAreaMapper.update(area);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Log("删除停车区域")
    @RequirePermission("area:delete")
    public Result<Void> delete(@PathVariable Long id) {
        parkingAreaMapper.deleteById(id);
        return Result.success();
    }
}
