package com.tibetan.medicine.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
@TableName("products")
public class Product implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 商品名称
     */
    private String name;
    
    /**
     * 副标题
     */
    private String subtitle;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 原价（数据库：original_price）
     */
    @TableField("original_price")
    private BigDecimal originalPrice;
    
    /**
     * 库存
     */
    private Integer stock;
    
    /**
     * 销量
     */
    private Integer sales;
    
    /**
     * 主图URL
     */
    private String image;
    
    /**
     * 商品图片JSON数组
     */
    private String images;
    
    /**
     * 商品详情HTML
     */
    private String detail;
    
    /**
     * 商品参数JSON
     */
    private String params;
    
    /**
     * 商品规格JSON
     */
    private String specs;
    
    /**
     * 产地
     */
    private String origin;
    
    /**
     * 保质期（数据库：shelf_life）
     */
    @TableField("shelf_life")
    private String shelfLife;
    
    /**
     * 等级（数据库中不存在）
     */
    @TableField(exist = false)
    private String grade;
    
    /**
     * 规格（数据库中不存在）
     */
    @TableField(exist = false)
    private String specification;
    
    /**
     * 储存方式（数据库中不存在）
     */
    @TableField(exist = false)
    private String storage;
    
    /**
     * 商品标签（数据库中不存在）
     */
    @TableField(exist = false)
    private String tags;
    
    /**
     * 是否新品 0-否 1-是（数据库：is_new）
     */
    @TableField("is_new")
    private Integer isNew;
    
    /**
     * 是否热销 0-否 1-是（数据库：is_hot）
     */
    @TableField("is_hot")
    private Integer isHot;
    
    /**
     * 商品状态 0-下架 1-上架
     */
    private Integer status;
    
    /**
     * 排序（数据库中不存在）
     */
    @TableField(exist = false)
    private Integer sort;
    
    /**
     * 是否推荐（数据库中不存在）
     */
    @TableField(exist = false)
    private Integer isRecommend;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
