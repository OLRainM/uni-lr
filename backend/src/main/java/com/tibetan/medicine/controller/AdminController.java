package com.tibetan.medicine.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tibetan.medicine.common.PageResult;
import com.tibetan.medicine.common.Result;
import com.tibetan.medicine.entity.Order;
import com.tibetan.medicine.entity.Product;
import com.tibetan.medicine.entity.User;
import com.tibetan.medicine.exception.BusinessException;
import com.tibetan.medicine.service.OrderItemService;
import com.tibetan.medicine.service.OrderService;
import com.tibetan.medicine.service.ProductService;
import com.tibetan.medicine.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.tibetan.medicine.dto.OrderShipDTO;
import com.tibetan.medicine.vo.OrderDetailVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商家后台 API 控制器
 * 所有接口均需在请求头携带 Admin-Key（由 AdminAuthInterceptor 统一校验）
 * 登录接口 /admin/login 已在拦截器中排除，无需 Admin-Key
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Value("${admin.secret-key}")
    private String adminSecretKey;

    @Autowired private ProductService productService;
    @Autowired private OrderService   orderService;
    @Autowired private UserService    userService;
    @Autowired private OrderItemService orderItemService;

    // ===================== 登录 =====================

    /**
     * 商家后台登录
     * 请求体：{ "secretKey": "TIBETAN-ADMIN-2024-SECRET" }
     * 返回：  { "token": "TIBETAN-ADMIN-2024-SECRET" }
     * 说明：Admin-Key 即秘钥本身，前端登录后存储并在后续请求头中携带
     */
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String key = body.get("secretKey");
        if (!StringUtils.hasText(key) || !adminSecretKey.equals(key)) {
            throw new BusinessException("秘钥错误，请联系管理员");
        }
        Map<String, String> data = new HashMap<>();
        data.put("token", adminSecretKey);   // token 即秘钥，后续请求放入 Admin-Key 请求头
        data.put("role", "admin");
        log.info("[Admin] 登录成功");
        return Result.success(data);
    }

    // ===================== 数据概览 =====================

    /**
     * 概览统计
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();

        // 商品统计
        long totalProducts = productService.count(
                new LambdaQueryWrapper<Product>().eq(Product::getStatus, 1));
        data.put("products", totalProducts);

        // 订单统计（全量）
        long totalOrders = orderService.count();
        data.put("orders", totalOrders);

        // 用户统计
        long totalUsers = userService.count(
                new LambdaQueryWrapper<User>().eq(User::getStatus, 0));
        data.put("users", totalUsers);

        // 总销售额（所有已完成订单）
        List<Order> completedOrders = orderService.list(
                new LambdaQueryWrapper<Order>().eq(Order::getStatus, 3));
        BigDecimal revenue = completedOrders.stream()
                .map(Order::getTotalAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("revenue", revenue);

        return Result.success(data);
    }

    // ===================== 商品管理 =====================

    /**
     * 商品列表（管理员版，可查所有状态）
     */
    @GetMapping("/product/list")
    public Result<PageResult<Product>> productList(
            @RequestParam(defaultValue = "1")  Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false)    String  keyword,
            @RequestParam(required = false)    Integer status) {

        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) wrapper.like(Product::getName, keyword);
        if (status != null)              wrapper.eq(Product::getStatus, status);
        wrapper.orderByDesc(Product::getCreateTime);

        Page<Product> result = productService.page(page, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), pageNum, pageSize, result.getRecords()));
    }

    /**
     * 新增商品
     */
    @PostMapping("/product/create")
    public Result<Product> productCreate(@RequestBody Product product) {
        product.setId(null);
        if (product.getStatus() == null) product.setStatus(1);
        productService.save(product);
        return Result.success(product);
    }

    /**
     * 编辑商品
     */
    @PostMapping("/product/update/{id}")
    public Result<Void> productUpdate(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        productService.updateById(product);
        return Result.success();
    }

    /**
     * 上架商品
     */
    @PostMapping("/product/on/{id}")
    public Result<Void> productOn(@PathVariable Long id) {
        Product p = new Product();
        p.setId(id); p.setStatus(1);
        productService.updateById(p);
        return Result.success();
    }

    /**
     * 下架商品
     */
    @PostMapping("/product/off/{id}")
    public Result<Void> productOff(@PathVariable Long id) {
        Product p = new Product();
        p.setId(id); p.setStatus(0);
        productService.updateById(p);
        return Result.success();
    }

    // ===================== 订单管理 =====================

    /**
     * 订单列表（全量，不限用户）
     */
    @GetMapping("/order/list")
    public Result<PageResult<Order>> orderList(
            @RequestParam(defaultValue = "1")  Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false)    Integer status,
            @RequestParam(required = false)    String  keyword) {

        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null)              wrapper.eq(Order::getStatus, status);
        if (StringUtils.hasText(keyword)) wrapper.like(Order::getOrderNo, keyword)
                                                  .or().like(Order::getReceiverName, keyword);
        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> result = orderService.page(page, wrapper);

        // 填充订单明细
        result.getRecords().forEach(order ->
                order.setItems(orderItemService.getOrderItems(order.getId())));

        return Result.success(new PageResult<>(result.getTotal(), pageNum, pageSize, result.getRecords()));
    }

    /**
     * 订单详情（管理员版，无需 JWT）
     */
    @GetMapping("/order/detail/{id}")
    public Result<OrderDetailVO> orderDetail(@PathVariable Long id) {
        OrderDetailVO detail = orderService.getOrderDetail(id);
        return Result.success(detail);
    }

    /**
     * 订单发货（管理员版，无需 JWT）
     */
    @PostMapping("/order/ship/{id}")
    public Result<Void> orderShip(@PathVariable Long id, @RequestBody OrderShipDTO dto) {
        orderService.shipOrder(id, dto.getShipCompany(), dto.getShipNo());
        return Result.success();
    }

    /**
     * 更新订单状态
     */
    @PostMapping("/order/status/{id}")
    public Result<Void> orderStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        if (status == null) throw new BusinessException("状态不能为空");
        Order order = orderService.getById(id);
        if (order == null) throw new BusinessException("订单不存在");
        order.setStatus(status);
        orderService.updateById(order);
        return Result.success();
    }

    // ===================== 用户管理 =====================

    /**
     * 用户列表
     */
    @GetMapping("/user/list")
    public Result<PageResult<User>> userList(
            @RequestParam(defaultValue = "1")  Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false)    Integer status,
            @RequestParam(required = false)    String  keyword) {

        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (status != null)              wrapper.eq(User::getStatus, status);
        if (StringUtils.hasText(keyword)) wrapper.like(User::getNickname, keyword)
                                                   .or().like(User::getPhone, keyword);
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> result = userService.page(page, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), pageNum, pageSize, result.getRecords()));
    }

    /**
     * 封禁用户
     */
    @PostMapping("/user/ban/{id}")
    public Result<Void> banUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) throw new BusinessException("用户不存在");
        user.setStatus(1); // 1=禁用
        userService.updateById(user);
        log.info("[Admin] 封禁用户 id={}", id);
        return Result.success();
    }

    /**
     * 解封用户
     */
    @PostMapping("/user/unban/{id}")
    public Result<Void> unbanUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) throw new BusinessException("用户不存在");
        user.setStatus(0); // 0=正常
        userService.updateById(user);
        log.info("[Admin] 解封用户 id={}", id);
        return Result.success();
    }
}

