package com.tibetan.medicine.config;

import com.tibetan.medicine.interceptor.AdminAuthInterceptor;
import com.tibetan.medicine.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web MVC配置 — 拦截器 + 静态资源
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private AdminAuthInterceptor adminAuthInterceptor;

    @Value("${admin.web-path:../adminWeb}")
    private String adminWebPath;

    /** 拦截器配置 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 1. 商家后台 API 拦截器（/admin/**，登录接口除外）
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login");

        // 2. 小程序用户 JWT 拦截器
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 放行 admin 路由（已由 adminAuthInterceptor 守护）
                        "/admin/**",
                        // Knife4j 文档
                        "/doc.html",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/favicon.ico",
                        "/error"
                );
    }

    /** 静态资源：将 adminWeb 目录映射到 /admin-web/** */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 解析 adminWeb 目录的绝对路径
        File adminDir = new File(adminWebPath).getAbsoluteFile();
        String location = "file:" + adminDir.getAbsolutePath() + "/";

        registry.addResourceHandler("/admin-web/**")
                .addResourceLocations(location);
    }
}

