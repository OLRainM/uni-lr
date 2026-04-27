package com.tibetan.medicine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tibetan.medicine.entity.OrderItem;
import com.tibetan.medicine.mapper.OrderItemMapper;
import com.tibetan.medicine.service.OrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单明细服务实现类
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
    
    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        return this.list(wrapper);
    }
    
    @Override
    public void deleteByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        this.remove(wrapper);
    }
}
