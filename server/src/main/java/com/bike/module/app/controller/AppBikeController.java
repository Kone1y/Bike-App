package com.bike.module.app.controller;

import com.bike.common.exception.BusinessException;
import com.bike.common.result.Result;
import com.bike.common.utils.DistanceUtils;
import com.bike.mapper.BikeMapper;
import com.bike.mapper.BikeOrderMapper;
import com.bike.module.bike.entity.Bike;
import com.bike.module.order.entity.BikeOrder;
import com.bike.module.order.entity.RentDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/app/bike")
@RequiredArgsConstructor
public class AppBikeController {

    private final BikeMapper bikeMapper;
    private final BikeOrderMapper bikeOrderMapper;

    @GetMapping("/nearby")
    public Result<List<Bike>> nearby(@RequestParam BigDecimal lng,
                                     @RequestParam BigDecimal lat,
                                     @RequestParam(defaultValue = "3000") Integer radius) {
        List<Bike> allAvailable = bikeMapper.findList(null, 0, null);
        List<Bike> nearby = allAvailable.stream()
                .filter(b -> b.getLng() != null && b.getLat() != null)
                .filter(b -> {
                    double dist = DistanceUtils.calculate(
                            lat.doubleValue(), lng.doubleValue(),
                            b.getLat().doubleValue(), b.getLng().doubleValue());
                    return dist <= radius;
                })
                .collect(Collectors.toList());
        return Result.success(nearby);
    }

    @PostMapping("/rent")
    public Result<BikeOrder> rent(@RequestBody RentDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        BikeOrder activeOrder = bikeOrderMapper.findRidingByUserId(userId);
        if (activeOrder != null) {
            throw new BusinessException("您有正在进行的订单，请先结束行程");
        }

        Bike bike = bikeMapper.findByCode(dto.getBikeCode());
        if (bike == null) {
            throw new BusinessException("车辆不存在");
        }
        if (bike.getStatus() != 0) {
            throw new BusinessException("车辆当前不可租用");
        }

        BikeOrder order = new BikeOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setBikeId(bike.getId());
        order.setStartTime(LocalDateTime.now());
        order.setStartLng(dto.getStartLng());
        order.setStartLat(dto.getStartLat());
        order.setStatus(0);
        bikeOrderMapper.insert(order);

        bikeMapper.updateStatus(bike.getId(), 1);

        return Result.success(bikeOrderMapper.findById(order.getId()));
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return timestamp + random;
    }
}
