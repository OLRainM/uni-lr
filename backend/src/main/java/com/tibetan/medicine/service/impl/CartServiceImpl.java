package com.tibetan.medicine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tibetan.medicine.entity.Cart;
import com.tibetan.medicine.mapper.CartMapper;
import com.tibetan.medicine.service.CartService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {
    
    @Override
    public List<Cart> getCartList(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        wrapper.orderByDesc(Cart::getCreateTime);
        return this.list(wrapper);
    }
    
    @Override
    public void addToCart(Long userId, Long productId, String specName, Integer quantity) {
        // 查询购物车中是否已存在该商品（同一商品和规格）
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        wrapper.eq(Cart::getProductId, productId);
        
        // 检查规格名称
        if (specName != null && !specName.isEmpty()) {
            wrapper.eq(Cart::getSpecName, specName);
        } else {
            wrapper.isNull(Cart::getSpecName);
        }
        
        Cart cart = this.getOne(wrapper);
        if (cart != null) {
            // 已存在，更新数量
            cart.setQuantity(cart.getQuantity() + quantity);
            this.updateById(cart);
        } else {
            // 不存在，新增
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setSpecName(specName);
            cart.setQuantity(quantity);
            this.save(cart);
        }
    }
    
    @Override
    public void updateCart(Long id, Integer quantity) {
        Cart cart = this.getById(id);
        if (cart != null) {
            cart.setQuantity(quantity);
            this.updateById(cart);
        }
    }
    
    @Override
    public void deleteCart(List<Long> ids) {
        this.removeByIds(ids);
    }
    
    @Override
    public void clearCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        this.remove(wrapper);
    }
}
