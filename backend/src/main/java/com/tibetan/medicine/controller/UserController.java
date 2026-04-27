package com.tibetan.medicine.controller;

import com.tibetan.medicine.annotation.SkipAuth;
import com.tibetan.medicine.common.Result;
import com.tibetan.medicine.entity.User;
import com.tibetan.medicine.exception.BusinessException;
import com.tibetan.medicine.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 微信登录
     */
    @SkipAuth
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        String token = userService.login(code);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);

        return Result.success(data);
    }

    /**
     * 测试登录（开发环境用，跳过微信认证）
     * 直接使用测试账号登录，返回token
     */
    @SkipAuth
    @PostMapping("/test-login")
    public Result<Map<String, Object>> testLogin() {
        // 固定测试用户 openId，login() 会自动查或创建
        String testOpenId = "test_user_openid_001";
        String token = userService.login(testOpenId);

        // 解析 token 获取 userId，查询用户信息
        Long userId = com.tibetan.medicine.util.JwtUtil.getUserId(token);
        User user = userService.getUserInfo(userId);
        if (user == null) {
            throw new BusinessException("测试用户不存在，请检查数据库");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);

        // 返回用户基本信息
        Map<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("id", user.getId());
        userInfoMap.put("nickname", user.getNickname() != null ? user.getNickname() : "测试用户");
        userInfoMap.put("avatar", user.getAvatar() != null ? user.getAvatar() : "");
        userInfoMap.put("phone", user.getPhone() != null ? user.getPhone() : "");
        data.put("userInfo", userInfoMap);

        return Result.success(data);
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException("用户未登录或登录已失效");
        }
        User user = userService.getUserInfo(userId);
        return Result.success(user);
    }
    
    /**
     * 更新用户信息
     */
    @PostMapping("/update")
    public Result<Void> updateUserInfo(
            HttpServletRequest request,
            @RequestBody Map<String, String> params) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException("用户未登录或登录已失效");
        }

        String nickname = params.get("nickname");
        String phone = params.get("phone");
        String avatar = params.get("avatar");
        
        userService.updateUserInfo(userId, nickname, phone, avatar);
        return Result.success();
    }
}
