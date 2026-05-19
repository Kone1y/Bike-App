package com.bike.mapper;

import com.bike.module.system.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OperationLogMapper {
    List<OperationLog> findList(@Param("username") String username, @Param("operation") String operation);
    int insert(OperationLog log);
}
