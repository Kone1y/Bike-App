package com.bike.mapper;

import com.bike.module.area.entity.ParkingArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ParkingAreaMapper {
    List<ParkingArea> findList(@Param("name") String name);
    ParkingArea findById(@Param("id") Long id);
    int insert(ParkingArea area);
    int update(ParkingArea area);
    int deleteById(@Param("id") Long id);
}
