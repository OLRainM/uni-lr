package com.tibetan.medicine.controller;

import com.tibetan.medicine.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/payment")
public class PaymentController {
    
    /**
     * 创建支付订单（Mock版本）
     */
    @PostMapping("/create")
    public Result<Map<String, Object>> createPayment(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> params) {
        
        Long orderId = Long.valueOf(params.get("orderId").toString());
        String paymentMethod = params.get("paymentMethod").toString();  // wechat, alipay
        
        // Mock: 模拟生成支付参数
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("orderId", orderId);
        paymentData.put("paymentId", "MOCK_PAY_" + System.currentTimeMillis());
        paymentData.put("paymentMethod", paymentMethod);
        paymentData.put("status", "pending");
        
        // 微信支付参数（Mock）
        if ("wechat".equals(paymentMethod)) {
            Map<String, String> wxParams = new HashMap<>();
            wxParams.put("appId", "wx1234567890");
            wxParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
            wxParams.put("nonceStr", "mock_nonce_str");
            wxParams.put("package", "prepay_id=mock_prepay_id");
            wxParams.put("signType", "MD5");
            wxParams.put("paySign", "mock_pay_sign");
            paymentData.put("wxParams", wxParams);
        }
        
        return Result.success(paymentData);
    }
    
    /**
     * 查询支付状态（Mock版本）
     */
    @GetMapping("/status/{paymentId}")
    public Result<Map<String, Object>> getPaymentStatus(@PathVariable String paymentId) {
        // Mock: 模拟支付成功
        Map<String, Object> data = new HashMap<>();
        data.put("paymentId", paymentId);
        data.put("status", "success");  // success, pending, failed
        data.put("paidAt", System.currentTimeMillis());
        
        return Result.success(data);
    }
    
    /**
     * 支付回调（Mock版本）
     */
    @PostMapping("/notify")
    public String paymentNotify(@RequestBody Map<String, Object> params) {
        // Mock: 模拟接收支付平台回调
        // 实际应用中需要验证签名、更新订单状态等
        
        System.out.println("收到支付回调：" + params);
        
        // 返回成功响应
        return "SUCCESS";
    }
}
