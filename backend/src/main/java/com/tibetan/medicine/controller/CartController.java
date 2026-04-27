package com.tibetan.medicine.controller;

import com.tibetan.medicine.common.Result;
import com.tibetan.medicine.entity.Cart;
import com.tibetan.medicine.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    /**
     * 购物车列表
     */
    @GetMapping("/list")
    public Result<List<Cart>> getCartList(@RequestHeader("Authorization") String token) {
        // TODO: 从token解析userId
        Long userId = 1L;
        List<Cart> cartList = cartService.getCartList(userId);
        return Result.success(cartList);
    }
    
    /**
     * 添加到购物车
     */
    @PostMapping("/add")
    public Result<Void> addToCart(@RequestHeader("Authorization") String token,
                                   @RequestBody Map<String, Object> params) {
        Long userId = 1L;
        Long productId = Long.valueOf(params.get("productId").toString());
        String spec = params.get("spec") != null ? params.get("spec").toString() : null;
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        
        cartService.addToCart(userId, productId, spec, quantity);
        return Result.success();
    }
    
    /**
     * 更新购物车
     */
    @PostMapping("/update")
    public Result<Void> updateCart(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        
        cartService.updateCart(id, quantity);
        return Result.success();
    }
    
    /**
     * 删除购物车商品
     */
    @PostMapping("/delete")
    public Result<Void> deleteCart(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        cartService.deleteCart(ids);
        return Result.success();
    }
    
    /**
     * 清空购物车
     */
    @PostMapping("/clear")
    public Result<Void> clearCart(@RequestHeader("Authorization") String token) {
        Long userId = 1L;
        cartService.clearCart(userId);
        return Result.success();
    }
}
