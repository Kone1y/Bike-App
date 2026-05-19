package com.bike.mapper;

import com.bike.module.scenic.entity.ScenicSpot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ScenicSpotMapper {
    List<ScenicSpot> findList(@Param("name") String name);
    ScenicSpot findById(@Param("id") Long id);
    int insert(ScenicSpot spot);
    int update(ScenicSpot spot);
    int deleteById(@Param("id") Long id);
}
