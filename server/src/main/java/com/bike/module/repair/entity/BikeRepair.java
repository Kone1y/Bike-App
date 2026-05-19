package com.bike.module.repair.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BikeRepair {
    private Long id;
    private Long bikeId;
    private Long userId;
    private String description;
    private String images;
    private Integer status;
    private String adminRemark;
    private LocalDateTime createTime;
    private LocalDateTime handleTime;
    private String bikeCode;
    private String username;
}
