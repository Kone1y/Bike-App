package com.bike.module.app.controller;

import com.bike.common.result.Result;
import com.bike.mapper.ParkingAreaMapper;
import com.bike.mapper.ScenicSpotMapper;
import com.bike.module.area.entity.ParkingArea;
import com.bike.module.scenic.entity.ScenicSpot;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app")
@RequiredArgsConstructor
public class AppScenicController {

    private final ScenicSpotMapper scenicSpotMapper;
    private final ParkingAreaMapper parkingAreaMapper;

    @GetMapping("/scenic/list")
    public Result<List<ScenicSpot>> scenicList() {
        return Result.success(scenicSpotMapper.findList(null));
    }

    @GetMapping("/area/list")
    public Result<List<ParkingArea>> areaList() {
        return Result.success(parkingAreaMapper.findList(null));
    }
}
