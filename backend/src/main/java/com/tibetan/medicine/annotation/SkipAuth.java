package com.tibetan.medicine.annotation;

import java.lang.annotation.*;

/**
 * 跳过JWT认证注解
 * 
 * 使用场景：
 * 1. 登录接口
 * 2. 公开的商品列表
 * 3. 不需要登录的接口
 * 
 * 使用方法：
 * @SkipAuth
 * public Result<xxx> publicMethod() { ... }
 * 
 * @author Tibetan Medicine Team
 * @date 2025-01-14
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SkipAuth {
}
