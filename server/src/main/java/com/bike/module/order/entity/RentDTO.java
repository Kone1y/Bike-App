package com.bike.module.order.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RentDTO {
    private String bikeCode;
    private BigDecimal startLng;
    private BigDecimal startLat;
}
