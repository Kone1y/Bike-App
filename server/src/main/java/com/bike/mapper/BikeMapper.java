package com.bike.mapper;

import com.bike.module.bike.entity.Bike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BikeMapper {
    List<Bike> findList(@Param("bikeCode") String bikeCode, @Param("status") Integer status, @Param("areaId") Long areaId);
    Bike findById(@Param("id") Long id);
    int insert(Bike bike);
    int update(Bike bike);
    int deleteById(@Param("id") Long id);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int countByStatus(@Param("status") Integer status);
    int countAll();
}
