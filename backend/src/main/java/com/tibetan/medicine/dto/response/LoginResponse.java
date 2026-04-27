package com.tibetan.medicine.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录响应DTO
 * 
 * @author Tibetan Medicine Team
 * @date 2025-01-14
 */
@Data
@Schema(description = "登录响应")
public class LoginResponse {
    
    @Schema(description = "JWT Token")
    private String token;
    
    @Schema(description = "用户信息")
    private UserInfoResponse userInfo;
}
