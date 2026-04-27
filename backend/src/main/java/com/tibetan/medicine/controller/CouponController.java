package com.tibetan.medicine.controller;

import com.tibetan.medicine.annotation.SkipAuth;
import com.tibetan.medicine.common.Result;
import com.tibetan.medicine.entity.Coupon;
import com.tibetan.medicine.entity.UserCoupon;
import com.tibetan.medicine.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 优惠券控制器
 */
@RestController
@RequestMapping("/coupon")
public class CouponController {
    
    @Autowired
    private CouponService couponService;
    
    /**
     * 获取可领取的优惠券列表
     */
    @SkipAuth
    @GetMapping("/list")
    public Result<List<Coupon>> getAvailableCoupons() {
        List<Coupon> coupons = couponService.getAvailableCoupons();
        return Result.success(coupons);
    }
    
    /**
     * 获取用户的优惠券
     * @param status 0-未使用 1-已使用 2-已过期
     */
    @GetMapping("/my")
    public Result<List<UserCoupon>> getUserCoupons(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer status) {
        Long userId = 1L;  // TODO: 从token解析
        List<UserCoupon> coupons = couponService.getUserCoupons(userId, status);
        return Result.success(coupons);
    }
    
    /**
     * 领取优惠券
     */
    @PostMapping("/receive")
    public Result<Void> receiveCoupon(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> params) {
        Long userId = 1L;  // TODO: 从token解析
        Long couponId = Long.valueOf(params.get("couponId").toString());
        couponService.receiveCoupon(userId, couponId);
        return Result.success();
    }
    
    /**
     * 使用优惠券（内部调用）
     */
    @PostMapping("/use")
    public Result<Void> useCoupon(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> params) {
        Long userId = 1L;  // TODO: 从token解析
        Long userCouponId = Long.valueOf(params.get("userCouponId").toString());
        couponService.useCoupon(userId, userCouponId);
        return Result.success();
    }
}
