package com.tibetan.medicine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tibetan.medicine.entity.Coupon;
import com.tibetan.medicine.entity.UserCoupon;
import java.util.List;

/**
 * 优惠券服务接口
 */
public interface CouponService extends IService<Coupon> {
    
    /**
     * 获取可领取的优惠券列表
     */
    List<Coupon> getAvailableCoupons();
    
    /**
     * 获取用户的优惠券列表
     * @param status 0-未使用 1-已使用 2-已过期
     */
    List<UserCoupon> getUserCoupons(Long userId, Integer status);
    
    /**
     * 领取优惠券
     */
    void receiveCoupon(Long userId, Long couponId);
    
    /**
     * 使用优惠券
     */
    void useCoupon(Long userId, Long userCouponId);
}
