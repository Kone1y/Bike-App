package com.bike.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;

@Mapper
public interface BikeOrderMapper {
    int countToday();
    int countByStatus(@Param("status") Integer status);
}
