package com.tibetan.medicine.controller;

import com.tibetan.medicine.annotation.SkipAuth;
import com.tibetan.medicine.common.Result;
import com.tibetan.medicine.converter.UserConverter;
import com.tibetan.medicine.dto.request.UserLoginRequest;
import com.tibetan.medicine.dto.response.LoginResponse;
import com.tibetan.medicine.dto.response.UserInfoResponse;
import com.tibetan.medicine.entity.User;
import com.tibetan.medicine.exception.BusinessException;
import com.tibetan.medicine.service.UserService;
import com.tibetan.medicine.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器（优化版）
 * 
 * 优化点：
 * 1. 使用Swagger注解生成API文档
 * 2. 使用DTO进行数据传输
 * 3. 使用@Valid进行参数校验
 * 4. 使用@SkipAuth跳过登录接口的认证
 * 5. 从request中获取userId
 * 
 * @author Tibetan Medicine Team
 * @date 2025-01-14
 */
@RestController
@RequestMapping("/user-v2")
@Tag(name = "用户管理（优化版）", description = "用户相关接口 - 展示优化后的写法")
public class UserControllerV2 {
    
    @Autowired
    private UserService userService;
    

    @Autowired
    private UserConverter userConverter;
    
    /**
     * 用户登录
     */
    @SkipAuth  // 登录接口不需要认证
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "微信小程序登录（Mock版本）")
    public Result<LoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        // Service返回token
        String token = userService.login(request.getCode());

        // 根据token解析userId并获取用户信息
        Long userId = JwtUtil.getUserId(token);
        User user = userService.getUserInfo(userId);

        // 转换为DTO并返回
        LoginResponse response = userConverter.toLoginResponse(token, user);
        return Result.success(response);
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的信息")
    public Result<UserInfoResponse> getUserInfo(HttpServletRequest request) {
        // 从request中获取userId（由拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException("用户未登录或登录已失效");
        }

        // 查询用户信息
        User user = userService.getUserInfo(userId);
        
        // 转换为DTO并返回
        UserInfoResponse response = userConverter.toUserInfoResponse(user);
        return Result.success(response);
    }
    
    /**
     * 更新用户信息
     */
    @PostMapping("/update")
    @Operation(summary = "更新用户信息", description = "更新用户昵称、手机号、头像")
    public Result<Void> updateUserInfo(
            HttpServletRequest request,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String avatar) {
        
        // 从request中获取userId
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException("用户未登录或登录已失效");
        }

        // 调用Service更新用户信息
        userService.updateUserInfo(userId, nickname, phone, avatar);
        
        return Result.success();
    }
}
