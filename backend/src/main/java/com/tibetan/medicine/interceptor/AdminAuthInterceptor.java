package com.tibetan.medicine.interceptor;

import com.tibetan.medicine.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 商家后台鉴权拦截器
 * 校验请求头 Admin-Key 是否与配置的秘钥一致
 */
@Slf4j
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Value("${admin.secret-key}")
    private String adminSecretKey;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        // OPTIONS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String key = request.getHeader("Admin-Key");
        if (!StringUtils.hasText(key)) {
            log.warn("[Admin] 请求缺少 Admin-Key 请求头，IP={}", request.getRemoteAddr());
            throw new BusinessException("无权限：缺少 Admin-Key");
        }

        if (!adminSecretKey.equals(key)) {
            log.warn("[Admin] Admin-Key 错误，IP={}", request.getRemoteAddr());
            throw new BusinessException("无权限：Admin-Key 无效");
        }

        log.debug("[Admin] 鉴权通过，IP={}", request.getRemoteAddr());
        return true;
    }
}

