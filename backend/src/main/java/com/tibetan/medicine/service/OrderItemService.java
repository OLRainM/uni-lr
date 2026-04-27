package com.tibetan.medicine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tibetan.medicine.entity.OrderItem;
import java.util.List;

/**
 * 订单明细服务接口
 */
public interface OrderItemService extends IService<OrderItem> {
    
    /**
     * 根据订单ID获取订单明细列表
     */
    List<OrderItem> getOrderItems(Long orderId);
    
    /**
     * 根据订单ID删除订单明细
     */
    void deleteByOrderId(Long orderId);
}
