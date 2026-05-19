package com.bike.module.order.service;

import com.bike.mapper.BikeOrderMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BikeOrderMapper bikeOrderMapper;

    @Value("${billing.base-fee}")
    private BigDecimal baseFee;

    @Value("${billing.free-duration}")
    private int freeDuration;

    @Value("${billing.rate-per-minute}")
    private BigDecimal ratePerMinute;

    @Value("${billing.daily-cap}")
    private BigDecimal dailyCap;

    public BillingResult calculate(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        long minutes = Duration.between(startTime, endTime).toMinutes();
        if (minutes < 1) minutes = 1;

        BigDecimal amount = baseFee;
        if (minutes > freeDuration) {
            long chargeableMinutes = minutes - freeDuration;
            amount = amount.add(ratePerMinute.multiply(BigDecimal.valueOf(chargeableMinutes)));
        }

        BigDecimal todaySpent = bikeOrderMapper.sumTodayAmountByUserId(userId);
        BigDecimal remaining = dailyCap.subtract(todaySpent);
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            amount = BigDecimal.ZERO;
        } else if (amount.compareTo(remaining) > 0) {
            amount = remaining;
        }

        amount = amount.setScale(2, RoundingMode.HALF_UP);

        return new BillingResult(minutes, amount);
    }

    @Data
    public static class BillingResult {
        private long durationMinutes;
        private BigDecimal amount;

        public BillingResult(long durationMinutes, BigDecimal amount) {
            this.durationMinutes = durationMinutes;
            this.amount = amount;
        }
    }
}
