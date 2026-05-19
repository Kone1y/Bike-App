package com.bike.mapper;

import com.bike.module.repair.entity.BikeRepair;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BikeRepairMapper {
    List<BikeRepair> findList(@Param("status") Integer status, @Param("bikeCode") String bikeCode);
    BikeRepair findById(@Param("id") Long id);
    int handle(@Param("id") Long id, @Param("status") Integer status, @Param("adminRemark") String adminRemark);
    int countByStatus(@Param("status") Integer status);
}
