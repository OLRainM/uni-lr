package com.tibetan.medicine.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求DTO
 * 
 * @author Tibetan Medicine Team
 * @date 2025-01-14
 */
@Data
@Schema(description = "用户登录请求")
public class UserLoginRequest {
    
    @Schema(description = "微信登录code", required = true, example = "test_code_123")
    @NotBlank(message = "登录code不能为空")
    private String code;
}
