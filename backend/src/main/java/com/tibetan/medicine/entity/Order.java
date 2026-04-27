package com.tibetan.medicine.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("orders")
public class Order implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单号（数据库：order_no）
     */
    @TableField("order_no")
    private String orderNo;
    
    /**
     * 用户ID（数据库：user_id）
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 收货人姓名（数据库：receiver_name）
     */
    @TableField("receiver_name")
    private String receiverName;
    
    /**
     * 收货人电话（数据库：receiver_phone）
     */
    @TableField("receiver_phone")
    private String receiverPhone;
    
    /**
     * 收货省份（数据库：receiver_province）
     */
    @TableField("receiver_province")
    private String receiverProvince;
    
    /**
     * 收货城市（数据库：receiver_city）
     */
    @TableField("receiver_city")
    private String receiverCity;
    
    /**
     * 收货区县（数据库：receiver_district）
     */
    @TableField("receiver_district")
    private String receiverDistrict;
    
    /**
     * 详细地址（数据库：receiver_address）
     */
    @TableField("receiver_address")
    private String receiverAddress;
    
    /**
     * 订单总额（数据库：total_amount）
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;
    
    /**
     * 商品金额（数据库：goods_amount，必填）
     */
    @TableField("goods_amount")
    private BigDecimal goodsAmount;
    
    /**
     * 运费
     */
    private BigDecimal freight;
    
    /**
     * 优惠金额
     */
    private BigDecimal discount;
    
    /**
     * 优惠券ID（数据库：coupon_id）
     */
    @TableField("coupon_id")
    private Long couponId;
    
    /**
     * 支付方式（数据库：pay_method）
     */
    @TableField("pay_method")
    private String payMethod;
    
    /**
     * 支付时间（数据库：pay_time）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("pay_time")
    private LocalDateTime payTime;
    
    /**
     * 订单状态
     */
    private Integer status;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 发货时间（数据库：ship_time）
     */
    @TableField("ship_time")
    private LocalDateTime shipTime;
    
    /**
     * 快递单号（数据库：ship_no）
     */
    @TableField("ship_no")
    private String shipNo;
    
    /**
     * 快递公司（数据库：ship_company）
     */
    @TableField("ship_company")
    private String shipCompany;
    
    /**
     * 完成时间（数据库：complete_time）
     */
    @TableField("complete_time")
    private LocalDateTime completeTime;
    
    /**
     * 取消时间（数据库：cancel_time）
     */
    @TableField("cancel_time")
    private LocalDateTime cancelTime;
    
    /**
     * 取消原因（数据库：cancel_reason）
     */
    @TableField("cancel_reason")
    private String cancelReason;
    
    /**
     * 订单商品明细（非数据库字段，查询时填充）
     */
    @TableField(exist = false)
    private java.util.List<OrderItem> items;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
