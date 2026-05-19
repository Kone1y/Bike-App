package com.bike.module.scenic.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScenicSpot {
    private Long id;
    private String name;
    private String description;
    private BigDecimal lng;
    private BigDecimal lat;
    private String coverImage;
    private LocalDateTime createTime;
}
