package com.tibetan.medicine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tibetan.medicine.entity.Cart;
import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService extends IService<Cart> {
    
    /**
     * 获取购物车列表
     */
    List<Cart> getCartList(Long userId);
    
    /**
     * 添加商品到购物车
     */
    void addToCart(Long userId, Long productId, String specName, Integer quantity);
    
    /**
     * 更新购物车商品数量
     */
    void updateCart(Long id, Integer quantity);
    
    /**
     * 删除购物车商品
     */
    void deleteCart(List<Long> ids);
    
    /**
     * 清空购物车
     */
    void clearCart(Long userId);
}
