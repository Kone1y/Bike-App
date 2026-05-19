package com.bike.module.repair.controller;

import com.bike.common.annotation.Log;
import com.bike.common.exception.BusinessException;
import com.bike.common.result.Result;
import com.bike.mapper.BikeMapper;
import com.bike.mapper.BikeRepairMapper;
import com.bike.module.repair.entity.BikeRepair;
import com.bike.security.annotation.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repair")
@RequiredArgsConstructor
public class BikeRepairController {

    private final BikeRepairMapper bikeRepairMapper;
    private final BikeMapper bikeMapper;

    @GetMapping("/list")
    @Log("查询报修列表")
    @RequirePermission("repair:list")
    public Result<List<BikeRepair>> list(@RequestParam(required = false) Integer status,
                                         @RequestParam(required = false) String bikeCode) {
        return Result.success(bikeRepairMapper.findList(status, bikeCode));
    }

    @GetMapping("/{id}")
    @RequirePermission("repair:list")
    public Result<BikeRepair> getById(@PathVariable Long id) {
        return Result.success(bikeRepairMapper.findById(id));
    }

    @PutMapping("/{id}/handle")
    @Log("处理报修")
    @RequirePermission("repair:handle")
    public Result<Void> handle(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer status = (Integer) body.get("status");
        String adminRemark = (String) body.get("adminRemark");
        if (status == null) {
            throw new BusinessException("状态不能为空");
        }
        bikeRepairMapper.handle(id, status, adminRemark != null ? adminRemark : "");
        if (status == 2) {
            BikeRepair repair = bikeRepairMapper.findById(id);
            if (repair != null) {
                bikeMapper.updateStatus(repair.getBikeId(), 0);
            }
        }
        return Result.success();
    }
}
