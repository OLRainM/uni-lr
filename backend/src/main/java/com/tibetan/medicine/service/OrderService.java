package com.tibetan.medicine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tibetan.medicine.common.PageResult;
import com.tibetan.medicine.dto.OrderCreateDTO;
import com.tibetan.medicine.entity.Order;
import com.tibetan.medicine.vo.OrderDetailVO;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {
    
    /**
     * 创建订单
     */
    Order createOrder(Long userId, OrderCreateDTO dto);
    
    /**
     * 获取订单列表
     */
    PageResult<Order> getOrderList(Long userId, Integer status, Integer pageNum, Integer pageSize);
    
    /**
     * 获取订单详情
     */
    OrderDetailVO getOrderDetail(Long id);
    
    /**
     * 模拟支付（状态 0→1）
     */
    void payOrder(Long id);

    /**
     * 取消订单
     */
    void cancelOrder(Long id);
    
    /**
     * 确认收货
     */
    void confirmOrder(Long id);
    
    /**
     * 删除订单
     */
    void deleteOrder(Long userId, Long id);
    
    /**
     * 获取订单统计
     */
    java.util.Map<String, Integer> getOrderCount(Long userId);
    
    /**
     * 订单发货
     */
    void shipOrder(Long orderId, String shipCompany, String shipNo);
}
