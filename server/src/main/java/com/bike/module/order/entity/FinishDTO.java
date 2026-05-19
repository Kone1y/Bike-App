package com.bike.module.order.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FinishDTO {
    private Long orderId;
    private BigDecimal endLng;
    private BigDecimal endLat;
}
