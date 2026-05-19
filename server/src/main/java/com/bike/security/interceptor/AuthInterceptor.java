package com.bike.security.interceptor;

import com.bike.common.exception.BusinessException;
import com.bike.common.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BusinessException(401, "未登录或token已过期");
        }

        token = token.substring(7);
        if (jwtUtils.isTokenExpired(token)) {
            throw new BusinessException(401, "token已过期，请重新登录");
        }

        try {
            Long userId = jwtUtils.getUserId(token);
            String username = jwtUtils.parseToken(token).get("username", String.class);
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
            return true;
        } catch (Exception e) {
            throw new BusinessException(401, "无效的token");
        }
    }
}
