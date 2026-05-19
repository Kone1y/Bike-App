package com.bike.security.interceptor;

import com.bike.common.exception.BusinessException;
import com.bike.security.annotation.RequirePermission;
import com.bike.security.annotation.RequireRole;
import com.bike.mapper.SysUserMapper;
import com.bike.mapper.SysRoleMapper;
import com.bike.mapper.SysMenuMapper;
import com.bike.module.system.entity.SysRole;
import com.bike.module.auth.entity.SysUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Long userId = (Long) request.getAttribute("userId");

        RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (requirePermission != null) {
            String permission = requirePermission.value();
            List<String> permissions = sysMenuMapper.getPermissionsByUserId(userId);
            if (!permissions.contains(permission)) {
                throw new BusinessException(403, "无操作权限");
            }
        }

        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole != null) {
            String roleCode = requireRole.value();
            List<SysRole> roles = sysRoleMapper.getRolesByUserId(userId);
            boolean hasRole = roles.stream().anyMatch(r -> r.getRoleCode().equals(roleCode));
            if (!hasRole) {
                throw new BusinessException(403, "无角色权限");
            }
        }

        return true;
    }
}
