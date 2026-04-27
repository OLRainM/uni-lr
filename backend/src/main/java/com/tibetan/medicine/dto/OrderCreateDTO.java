package com.tibetan.medicine.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单DTO
 */
@Data
public class OrderCreateDTO {
    
    /**
     * 收货地址ID
     */
    private Long addressId;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 商品列表
     */
    private List<OrderItemDTO> items;
    
    @Data
    public static class OrderItemDTO {
        private Long productId;
        private String productName;
        private String image;
        private String specName;
        private BigDecimal price;
        private Integer quantity;
    }
}
