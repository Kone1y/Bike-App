package com.bike.mapper;

import com.bike.module.order.entity.BikeOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface BikeOrderMapper {
    int countToday();
    int countByStatus(@Param("status") Integer status);

    int insert(BikeOrder order);
    int update(BikeOrder order);
    BikeOrder findById(@Param("id") Long id);
    BikeOrder findRidingByUserId(@Param("userId") Long userId);
    List<BikeOrder> findListByUserId(@Param("userId") Long userId, @Param("status") Integer status);
    int countTodayByUserId(@Param("userId") Long userId);
    BigDecimal sumTodayAmountByUserId(@Param("userId") Long userId);
}
