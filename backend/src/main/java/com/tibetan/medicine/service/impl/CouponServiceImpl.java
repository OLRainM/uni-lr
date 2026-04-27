package com.tibetan.medicine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tibetan.medicine.entity.Coupon;
import com.tibetan.medicine.entity.UserCoupon;
import com.tibetan.medicine.exception.BusinessException;
import com.tibetan.medicine.mapper.CouponMapper;
import com.tibetan.medicine.mapper.UserCouponMapper;
import com.tibetan.medicine.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券服务实现类
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {
    
    @Autowired
    private UserCouponMapper userCouponMapper;
    
    @Override
    public List<Coupon> getAvailableCoupons() {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStatus, 1);  // 启用状态
        wrapper.le(Coupon::getStartTime, LocalDateTime.now());  // 已开始
        wrapper.ge(Coupon::getEndTime, LocalDateTime.now());  // 未结束
        wrapper.orderByDesc(Coupon::getCreateTime);
        return this.list(wrapper);
    }
    
    @Override
    public List<UserCoupon> getUserCoupons(Long userId, Integer status) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        if (status != null) {
            wrapper.eq(UserCoupon::getStatus, status);
        }
        wrapper.orderByDesc(UserCoupon::getCreateTime);
        return userCouponMapper.selectList(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveCoupon(Long userId, Long couponId) {
        // 检查优惠券是否存在且有效
        Coupon coupon = this.getById(couponId);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        
        if (coupon.getStatus() != 1) {
            throw new BusinessException("优惠券已下架");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            throw new BusinessException("优惠券已过期");
        }
        
        if (coupon.getReceived() >= coupon.getTotal()) {
            throw new BusinessException("优惠券已领完");
        }
        
        // 检查用户是否已领取
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        wrapper.eq(UserCoupon::getCouponId, couponId);
        Long count = userCouponMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("您已领取过该优惠券");
        }
        
        // 创建用户优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(0);  // 0-未使用
        userCouponMapper.insert(userCoupon);
        
        // 更新优惠券领取数量
        coupon.setReceived(coupon.getReceived() + 1);
        this.updateById(coupon);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void useCoupon(Long userId, Long userCouponId) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || !userCoupon.getUserId().equals(userId)) {
            throw new BusinessException("优惠券不存在");
        }
        
        if (userCoupon.getStatus() != 0) {  // 0-未使用
            throw new BusinessException("优惠券已使用或已过期");
        }
        
        // 更新状态为已使用
        userCoupon.setStatus(1);  // 1-已使用
        userCouponMapper.updateById(userCoupon);
    }
}
