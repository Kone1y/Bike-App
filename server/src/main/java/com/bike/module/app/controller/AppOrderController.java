package com.bike.module.app.controller;

import com.bike.common.exception.BusinessException;
import com.bike.common.result.Result;
import com.bike.mapper.BikeMapper;
import com.bike.mapper.BikeOrderMapper;
import com.bike.module.order.entity.BikeOrder;
import com.bike.module.order.entity.FinishDTO;
import com.bike.module.order.service.BillingService;
import com.bike.module.order.service.BillingService.BillingResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/app/order")
@RequiredArgsConstructor
public class AppOrderController {

    private final BikeOrderMapper bikeOrderMapper;
    private final BikeMapper bikeMapper;
    private final BillingService billingService;

    @GetMapping("/riding")
    public Result<BikeOrder> riding(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BikeOrder order = bikeOrderMapper.findRidingByUserId(userId);
        if (order == null) {
            return Result.success(null);
        }
        return Result.success(order);
    }

    @PostMapping("/finish")
    public Result<BikeOrder> finish(@RequestBody FinishDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BikeOrder order = bikeOrderMapper.findById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单已结束");
        }

        LocalDateTime endTime = LocalDateTime.now();
        BillingResult billing = billingService.calculate(userId, order.getStartTime(), endTime);

        order.setEndTime(endTime);
        order.setEndLng(dto.getEndLng());
        order.setEndLat(dto.getEndLat());
        order.setDuration((int) billing.getDurationMinutes());
        order.setAmount(billing.getAmount());
        order.setStatus(1);
        bikeOrderMapper.update(order);

        bikeMapper.updateStatus(order.getBikeId(), 0);

        return Result.success(bikeOrderMapper.findById(order.getId()));
    }

    @GetMapping("/list")
    public Result<List<BikeOrder>> list(@RequestParam(required = false) Integer status,
                                        HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(bikeOrderMapper.findListByUserId(userId, status));
    }

    @GetMapping("/{id}")
    public Result<BikeOrder> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BikeOrder order = bikeOrderMapper.findById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权查看此订单");
        }
        return Result.success(order);
    }
}
