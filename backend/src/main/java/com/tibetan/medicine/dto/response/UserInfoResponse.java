package com.tibetan.medicine.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户信息响应DTO
 * 
 * @author Tibetan Medicine Team
 * @date 2025-01-14
 */
@Data
@Schema(description = "用户信息")
public class UserInfoResponse {
    
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "昵称", example = "测试用户")
    private String nickName;
    
    @Schema(description = "头像URL")
    private String avatar;
    
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
    
    @Schema(description = "积分", example = "100")
    private Integer points;
}
