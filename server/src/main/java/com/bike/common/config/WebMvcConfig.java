package com.bike.common.config;

import com.bike.security.interceptor.AuthInterceptor;
import com.bike.security.interceptor.PermissionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final PermissionInterceptor permissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/app/auth/login",
                        "/api/app/scenic/list",
                        "/api/app/area/list",
                        "/api/app/bike/nearby"
                );

        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**");
    }
}
