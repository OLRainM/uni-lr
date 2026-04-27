package com.tibetan.medicine.vo;

import com.tibetan.medicine.entity.Order;
import com.tibetan.medicine.entity.OrderItem;
import lombok.Data;
import java.util.List;

/**
 * 订单详情VO
 */
@Data
public class OrderDetailVO {
    
    /**
     * 订单信息
     */
    private Order order;
    
    /**
     * 订单明细列表
     */
    private List<OrderItem> items;
}
