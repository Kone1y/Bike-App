package com.bike.module.area.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ParkingArea {
    private Long id;
    private String name;
    private BigDecimal centerLng;
    private BigDecimal centerLat;
    private Integer radius;
    private LocalDateTime createTime;
}
