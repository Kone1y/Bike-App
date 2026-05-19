package com.bike.module.bike.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Bike {
    private Long id;
    private String bikeCode;
    private Integer status;
    private BigDecimal lng;
    private BigDecimal lat;
    private Long areaId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String areaName;
}
