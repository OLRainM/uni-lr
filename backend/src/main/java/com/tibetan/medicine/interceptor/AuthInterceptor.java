package com.tibetan.medicine.interceptor;

import com.tibetan.medicine.annotation.SkipAuth;
import com.tibetan.medicine.exception.BusinessException;
import com.tibetan.medicine.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT认证拦截器
 * 
 * 功能：
 * 1. 拦截所有请求，验证JWT Token
 * 2. 如果方法或类上有@SkipAuth注解，则跳过验证
 * 3. 验证通过后，将userId存入request attribute
 * 
 * @author Tibetan Medicine Team
 * @date 2025-01-14
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果不是方法处理器，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 检查方法上是否有@SkipAuth注解
        SkipAuth methodAnnotation = handlerMethod.getMethodAnnotation(SkipAuth.class);
        if (methodAnnotation != null) {
            log.debug("方法 {} 标记了@SkipAuth，跳过认证", handlerMethod.getMethod().getName());
            return true;
        }

        // 检查类上是否有@SkipAuth注解
        SkipAuth classAnnotation = handlerMethod.getBeanType().getAnnotation(SkipAuth.class);
        if (classAnnotation != null) {
            log.debug("类 {} 标记了@SkipAuth，跳过认证", handlerMethod.getBeanType().getSimpleName());
            return true;
        }

        // 获取token
        String token = request.getHeader("Authorization");
        
        if (!StringUtils.hasText(token)) {
            log.warn("请求头中未找到Authorization");
            throw new BusinessException("未登录，请先登录");
        }

        // 去除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // 验证token并获取userId
            Long userId = JwtUtil.getUserId(token);
            
            // 将userId存入request attribute，供Controller使用
            request.setAttribute("userId", userId);
            
            log.debug("用户 {} 认证成功", userId);
            return true;
            
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            throw new BusinessException("登录已过期，请重新登录");
        }
    }
}
