package com.tibetan.medicine.common;

/**
 * 缓存常量
 * 定义所有缓存的名称和过期时间
 */
public class CacheConstants {
    
    /**
     * 商品缓存
     */
    public static final String PRODUCT_CACHE = "product";
    public static final String PRODUCT_LIST_CACHE = "product:list";
    
    /**
     * 用户缓存
     */
    public static final String USER_CACHE = "user";
    
    /**
     * 分类缓存
     */
    public static final String CATEGORY_CACHE = "category";
    
    /**
     * 订单缓存
     */
    public static final String ORDER_CACHE = "order";
    
    /**
     * 缓存时间（秒）
     */
    public static final long PRODUCT_CACHE_TTL = 3600; // 1小时
    public static final long USER_CACHE_TTL = 1800; // 30分钟
    public static final long CATEGORY_CACHE_TTL = 86400; // 24小时
    public static final long ORDER_CACHE_TTL = 600; // 10分钟
}
