package com.bike.common.aspect;

import com.bike.common.annotation.Log;
import com.bike.mapper.OperationLogMapper;
import com.bike.module.system.entity.OperationLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogMapper operationLogMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(logAnnotation)")
    public Object around(ProceedingJoinPoint point, Log logAnnotation) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = point.proceed();
        long executeTime = System.currentTimeMillis() - startTime;

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) return result;
            HttpServletRequest request = attributes.getRequest();

            OperationLog logEntity = new OperationLog();
            logEntity.setUserId((Long) request.getAttribute("userId"));
            logEntity.setUsername((String) request.getAttribute("username"));
            logEntity.setOperation(logAnnotation.value());
            logEntity.setMethod(point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName());
            logEntity.setIp(getIp(request));
            logEntity.setExecuteTime((int) executeTime);

            Object[] args = point.getArgs();
            if (args != null && args.length > 0) {
                try {
                    String params = objectMapper.writeValueAsString(args);
                    logEntity.setParams(params.length() > 2000 ? params.substring(0, 2000) : params);
                } catch (Exception e) {
                    logEntity.setParams("参数序列化失败");
                }
            }

            operationLogMapper.insert(logEntity);
        } catch (Exception e) {
            log.warn("记录操作日志失败: {}", e.getMessage());
        }

        return result;
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null && ip.contains(",") ? ip.split(",")[0].trim() : ip;
    }
}
