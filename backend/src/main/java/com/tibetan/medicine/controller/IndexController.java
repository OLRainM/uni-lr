package com.tibetan.medicine.controller;

import com.tibetan.medicine.annotation.SkipAuth;
import com.tibetan.medicine.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页控制器
 */
@SkipAuth
@RestController
public class IndexController {
    
    /**
     * API 根路径 - 欢迎页面
     */
    @GetMapping("/")
    public Result<Map<String, Object>> index() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "藏药小程序 API");
        data.put("version", "v1.0.0");
        data.put("status", "running");
        data.put("message", "API 服务运行正常");
        
        // API 统计
        Map<String, Integer> apiCount = new HashMap<>();
        apiCount.put("user", 3);
        apiCount.put("product", 4);
        apiCount.put("cart", 5);
        apiCount.put("order", 7);
        apiCount.put("address", 5);
        apiCount.put("coupon", 4);
        apiCount.put("payment", 3);
        apiCount.put("knowledge", 2);
        apiCount.put("banner", 1);
        apiCount.put("upload", 2);
        apiCount.put("total", 36);
        data.put("api_modules", apiCount);
        
        // 文档链接
        Map<String, String> docs = new HashMap<>();
        docs.put("接口文档", "/Apifox接口文档.md");
        docs.put("OpenAPI规范", "/openapi.json");
        docs.put("Postman集合", "/藏药小程序API.postman_collection.json");
        data.put("documentation", docs);
        
        // 快速测试接口
        Map<String, String> quickTest = new HashMap<>();
        quickTest.put("商品列表", "/product/list");
        quickTest.put("商品分类", "/product/categories");
        quickTest.put("轮播图", "/banner/list");
        quickTest.put("知识列表", "/knowledge/list");
        data.put("quick_test", quickTest);
        
        return Result.success(data);
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("service", "tibetan-medicine-api");
        return Result.success(health);
    }
}
