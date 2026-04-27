package com.tibetan.medicine.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细实体类
 */
@Data
@TableName("order_items")
public class OrderItem implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品名称（数据库：product_name）
     */
    @TableField("product_name")
    private String productName;
    
    /**
     * 规格名称（数据库：spec_name）
     */
    @TableField("spec_name")
    private String specName;
    
    /**
     * 商品价格
     */
    private BigDecimal price;
    
    /**
     * 购买数量
     */
    private Integer quantity;
    
    /**
     * 商品图片（数据库：image）
     */
    private String image;
}
