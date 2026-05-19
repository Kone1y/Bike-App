package com.bike.module.app.controller;

import com.bike.common.exception.BusinessException;
import com.bike.common.result.Result;
import com.bike.mapper.BikeMapper;
import com.bike.mapper.BikeRepairMapper;
import com.bike.module.bike.entity.Bike;
import com.bike.module.repair.entity.BikeRepair;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/app/repair")
@RequiredArgsConstructor
public class AppRepairController {

    private final BikeRepairMapper bikeRepairMapper;
    private final BikeMapper bikeMapper;

    @PostMapping("/submit")
    public Result<Void> submit(@RequestBody RepairSubmitDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        if (dto.getBikeCode() == null || dto.getBikeCode().isBlank()) {
            throw new BusinessException("车辆编码不能为空");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new BusinessException("请填写故障描述");
        }

        Bike bike = bikeMapper.findByCode(dto.getBikeCode());
        if (bike == null) {
            throw new BusinessException("车辆不存在");
        }

        BikeRepair repair = new BikeRepair();
        repair.setBikeId(bike.getId());
        repair.setUserId(userId);
        repair.setDescription(dto.getDescription());
        repair.setImages(dto.getImages() != null ? dto.getImages() : "");
        repair.setStatus(0);
        bikeRepairMapper.insert(repair);

        bikeMapper.updateStatus(bike.getId(), 2);

        return Result.success();
    }

    @lombok.Data
    public static class RepairSubmitDTO {
        private String bikeCode;
        private String description;
        private String images;
    }
}
