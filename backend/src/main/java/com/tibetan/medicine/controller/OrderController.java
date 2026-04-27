package com.tibetan.medicine.controller;

import com.tibetan.medicine.common.PageResult;
import com.tibetan.medicine.common.Result;
import com.tibetan.medicine.dto.OrderCreateDTO;
import com.tibetan.medicine.dto.OrderShipDTO;
import com.tibetan.medicine.entity.Order;
import com.tibetan.medicine.service.OrderService;
import com.tibetan.medicine.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<Order> createOrder(@RequestHeader("Authorization") String token,
                                     @RequestBody OrderCreateDTO dto) {
        Long userId = 1L;
        Order order = orderService.createOrder(userId, dto);
        return Result.success(order);
    }
    
    /**
     * 订单列表
     */
    @GetMapping("/list")
    public Result<PageResult<Order>> getOrderList(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Long userId = 1L;
        PageResult<Order> result = orderService.getOrderList(userId, status, pageNum, pageSize);
        return Result.success(result);
    }
    
    /**
     * 订单详情
     */
    @GetMapping("/detail/{id}")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable Long id) {
        OrderDetailVO detail = orderService.getOrderDetail(id);
        return Result.success(detail);
    }
    
    /**
     * 模拟支付（将订单状态从 0-待付款 更新为 1-待发货）
     */
    @PostMapping("/pay/{id}")
    public Result<Void> payOrder(@PathVariable Long id) {
        orderService.payOrder(id);
        return Result.success();
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{id}")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.success();
    }
    
    /**
     * 确认收货
     */
    @PostMapping("/confirm/{id}")
    public Result<Void> confirmOrder(@PathVariable Long id) {
        orderService.confirmOrder(id);
        return Result.success();
    }
    
    /**
     * 删除订单
     */
    @PostMapping("/delete/{id}")
    public Result<Void> deleteOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        Long userId = 1L;
        orderService.deleteOrder(userId, id);
        return Result.success();
    }
    
    /**
     * 获取订单统计
     */
    @GetMapping("/count")
    public Result<java.util.Map<String, Integer>> getOrderCount(
            @RequestHeader("Authorization") String token) {
        Long userId = 1L;
        java.util.Map<String, Integer> count = orderService.getOrderCount(userId);
        return Result.success(count);
    }
    
    /**
     * 订单发货（管理员）
     */
    @PostMapping("/ship/{id}")
    public Result<Void> shipOrder(@PathVariable Long id, 
                                  @RequestBody OrderShipDTO dto) {
        orderService.shipOrder(id, dto.getShipCompany(), dto.getShipNo());
        return Result.success();
    }
}
