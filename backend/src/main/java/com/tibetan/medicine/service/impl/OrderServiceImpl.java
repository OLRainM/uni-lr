package com.tibetan.medicine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tibetan.medicine.common.PageResult;
import com.tibetan.medicine.dto.OrderCreateDTO;
import com.tibetan.medicine.entity.Address;
import com.tibetan.medicine.entity.Order;
import com.tibetan.medicine.entity.OrderItem;
import com.tibetan.medicine.mapper.OrderMapper;
import com.tibetan.medicine.service.AddressService;
import com.tibetan.medicine.service.OrderItemService;
import com.tibetan.medicine.service.OrderService;
import com.tibetan.medicine.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    @Autowired
    private AddressService addressService;
    
    @Autowired
    private OrderItemService orderItemService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Long userId, OrderCreateDTO dto) {
        // 获取收货地址
        Address address = addressService.getById(dto.getAddressId());
        
        // 计算金额
        BigDecimal totalAmount = dto.getItems().stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverDistrict(address.getDistrict());
        order.setReceiverAddress(address.getDetail());
        order.setTotalAmount(totalAmount);
        order.setGoodsAmount(totalAmount); // 商品金额
        order.setFreight(BigDecimal.ZERO); // 运费
        order.setDiscount(BigDecimal.ZERO); // 优惠金额
        order.setStatus(0); // 待付款
        order.setRemark(dto.getRemark());
        // 手动设置创建时间（MetaObjectHandler 兜底）
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        this.save(order);
        
        // 创建订单明细
        List<OrderItem> orderItems = dto.getItems().stream().map(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(item.getProductId());
            // productName 兜底：前端未传时使用空字符串，避免数据库 NOT NULL 报错
            orderItem.setProductName(item.getProductName() != null ? item.getProductName() : "");
            orderItem.setImage(item.getImage() != null ? item.getImage() : "");
            orderItem.setSpecName(item.getSpecName() != null ? item.getSpecName() : "");
            orderItem.setPrice(item.getPrice());
            orderItem.setQuantity(item.getQuantity());
            return orderItem;
        }).collect(Collectors.toList());
        
        orderItemService.saveBatch(orderItems);
        
        return order;
    }
    
    @Override
    public PageResult<Order> getOrderList(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null && status >= 0) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> result = this.page(page, wrapper);

        // 批量填充每个订单的商品明细
        List<Order> orders = result.getRecords();
        if (orders != null && !orders.isEmpty()) {
            orders.forEach(order -> {
                List<OrderItem> items = orderItemService.getOrderItems(order.getId());
                order.setItems(items);
            });
        }

        return new PageResult<>(result.getTotal(), pageNum, pageSize, orders);
    }
    
    @Override
    public OrderDetailVO getOrderDetail(Long id) {
        Order order = this.getById(id);
        List<OrderItem> items = orderItemService.getOrderItems(id);
        
        OrderDetailVO vo = new OrderDetailVO();
        vo.setOrder(order);
        vo.setItems(items);
        return vo;
    }
    
    @Override
    public void payOrder(Long id) {
        Order order = this.getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不支持支付");
        }
        order.setStatus(1); // 待发货
        order.setPayTime(LocalDateTime.now());
        order.setPayMethod("WECHAT"); // 模拟微信支付
        this.updateById(order);
    }

    @Override
    public void cancelOrder(Long id) {
        Order order = this.getById(id);
        if (order != null && order.getStatus() == 0) {
            order.setStatus(4); // 已取消
            order.setCancelTime(LocalDateTime.now());
            this.updateById(order);
        }
    }
    
    @Override
    public void confirmOrder(Long id) {
        Order order = this.getById(id);
        if (order != null && order.getStatus() == 2) {
            order.setStatus(3); // 已完成
            order.setCompleteTime(LocalDateTime.now());
            this.updateById(order);
        }
    }
    
    @Override
    public void deleteOrder(Long userId, Long id) {
        Order order = this.getById(id);
        if (order != null && order.getUserId().equals(userId)) {
            // 只有已取消或已完成的订单才能删除
            if (order.getStatus() == 3 || order.getStatus() == 4) {
                this.removeById(id);
                // 同时删除订单明细
                orderItemService.deleteByOrderId(id);
            }
        }
    }
    
    @Override
    public java.util.Map<String, Integer> getOrderCount(Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        List<Order> orders = this.list(wrapper);
        
        java.util.Map<String, Integer> count = new java.util.HashMap<>();
        count.put("unpaid", (int) orders.stream().filter(o -> o.getStatus() == 0).count()); // 待付款
        count.put("unshipped", (int) orders.stream().filter(o -> o.getStatus() == 1).count()); // 待发货
        count.put("shipped", (int) orders.stream().filter(o -> o.getStatus() == 2).count()); // 待收货
        count.put("completed", (int) orders.stream().filter(o -> o.getStatus() == 3).count()); // 已完成
        
        return count;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(Long orderId, String shipCompany, String shipNo) {
        // 查询订单
        Order order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 验证订单状态（只有待发货的订单才能发货）
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不允许发货，当前状态：" + getStatusText(order.getStatus()));
        }
        
        // 更新订单信息
        order.setStatus(2);  // 更新为待收货
        order.setShipCompany(shipCompany);
        order.setShipNo(shipNo);
        order.setShipTime(LocalDateTime.now());
        
        this.updateById(order);
    }
    
    /**
     * 获取订单状态文本
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "待收货";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知状态";
        }
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        return "TM" + timestamp + random;
    }
}
