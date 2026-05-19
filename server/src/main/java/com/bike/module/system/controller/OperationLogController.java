package com.bike.module.system.controller;

import com.bike.common.result.Result;
import com.bike.mapper.OperationLogMapper;
import com.bike.module.system.entity.OperationLog;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/log")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogMapper operationLogMapper;

    @GetMapping("/list")
    public Result<List<OperationLog>> list(@RequestParam(required = false) String username,
                                           @RequestParam(required = false) String operation) {
        return Result.success(operationLogMapper.findList(username, operation));
    }
}
