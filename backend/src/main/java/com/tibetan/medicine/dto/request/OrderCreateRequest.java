package com.tibetan.medicine.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 订单创建请求DTO
 * 
 * @author Tibetan Medicine Team
 * @date 2025-01-14
 */
@Data
@Schema(description = "订单创建请求")
public class OrderCreateRequest {
    
    @Schema(description = "商品列表", required = true)
    @NotEmpty(message = "商品列表不能为空")
    @Valid
    private List<OrderItemRequest> items;
    
    @Schema(description = "收货地址ID", required = true, example = "1")
    @NotNull(message = "收货地址不能为空")
    private Long addressId;
    
    @Schema(description = "优惠券ID", example = "1")
    private Long couponId;
    
    @Schema(description = "备注", example = "请尽快发货")
    @Size(max = 200, message = "备注不能超过200字")
    private String remark;
    
    /**
     * 订单商品项
     */
    @Data
    @Schema(description = "订单商品项")
    public static class OrderItemRequest {
        
        @Schema(description = "商品ID", required = true, example = "1")
        @NotNull(message = "商品ID不能为空")
        private Long productId;
        
        @Schema(description = "规格", example = "5g/盒")
        private String spec;
        
        @Schema(description = "数量", required = true, example = "2")
        @NotNull(message = "数量不能为空")
        @jakarta.validation.constraints.Min(value = 1, message = "数量至少为1")
        @jakarta.validation.constraints.Max(value = 99, message = "数量不能超过99")
        private Integer quantity;
    }
}
