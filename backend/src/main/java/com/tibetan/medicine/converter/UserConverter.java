package com.tibetan.medicine.converter;

import com.tibetan.medicine.dto.response.LoginResponse;
import com.tibetan.medicine.dto.response.UserInfoResponse;
import com.tibetan.medicine.entity.User;
import org.springframework.stereotype.Component;

/**
 * 用户转换器
 * 
 * 功能：Entity <-> DTO 转换
 * 
 * @author Tibetan Medicine Team
 * @date 2025-01-14
 */
@Component
public class UserConverter {
    
    /**
     * Entity转UserInfo DTO
     */
    public UserInfoResponse toUserInfoResponse(User user) {
        if (user == null) {
            return null;
        }
        
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setNickName(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setPhone(user.getPhone());
        response.setPoints(user.getPoints());

        return response;
    }
    
    /**
     * 构建登录响应
     */
    public LoginResponse toLoginResponse(String token, User user) {
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserInfo(toUserInfoResponse(user));
        
        return response;
    }
}
