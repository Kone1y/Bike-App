package com.bike.module.order.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BikeOrder {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long bikeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal startLng;
    private BigDecimal startLat;
    private BigDecimal endLng;
    private BigDecimal endLat;
    private Integer duration;
    private BigDecimal amount;
    private Integer status;
    private LocalDateTime createTime;
    private String bikeCode;
}
