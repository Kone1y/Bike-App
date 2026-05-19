package com.bike.module.system.controller;

import com.bike.mapper.BikeMapper;
import com.bike.mapper.BikeOrderMapper;
import com.bike.mapper.BikeRepairMapper;
import com.bike.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final BikeMapper bikeMapper;
    private final BikeRepairMapper bikeRepairMapper;
    private final BikeOrderMapper bikeOrderMapper;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalBikes", bikeMapper.countAll());
        data.put("availableBikes", bikeMapper.countByStatus(0));
        data.put("inUseBikes", bikeMapper.countByStatus(1));
        data.put("repairingBikes", bikeMapper.countByStatus(2));
        data.put("pendingRepairs", bikeRepairMapper.countByStatus(0));
        data.put("todayOrders", bikeOrderMapper.countToday());
        return Result.success(data);
    }
}
