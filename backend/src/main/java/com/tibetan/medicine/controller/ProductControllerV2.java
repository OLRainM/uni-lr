package com.tibetan.medicine.controller;

import com.tibetan.medicine.annotation.SkipAuth;
import com.tibetan.medicine.common.PageResult;
import com.tibetan.medicine.common.Result;
import com.tibetan.medicine.converter.ProductConverter;
import com.tibetan.medicine.dto.request.ProductQueryRequest;
import com.tibetan.medicine.dto.response.ProductResponse;
import com.tibetan.medicine.entity.Category;
import com.tibetan.medicine.entity.Product;
import com.tibetan.medicine.service.CategoryService;
import com.tibetan.medicine.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器（优化版）
 * 
 * 优化点：
 * 1. 使用Swagger注解生成API文档
 * 2. 使用DTO进行数据传输
 * 3. 使用@Valid进行参数校验
 * 4. 使用@SkipAuth跳过不需要登录的接口
 * 
 * @author Tibetan Medicine Team
 * @date 2025-01-14
 */
@RestController
@RequestMapping("/product-v2")
@Tag(name = "商品管理（优化版）", description = "商品相关接口 - 展示优化后的写法")
public class ProductControllerV2 {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private ProductConverter productConverter;
    
    /**
     * 商品列表
     */
    @SkipAuth  // 不需要登录
    @GetMapping("/list")
    @Operation(summary = "获取商品列表", description = "分页查询商品列表，支持分类筛选、关键词搜索、排序")
    public Result<PageResult<ProductResponse>> getProductList(@Valid ProductQueryRequest request) {
        // 调用Service获取数据
        PageResult<Product> pageResult = productService.getProductList(
                request.getPageNum(),
                request.getPageSize(),
                request.getCategoryId(),
                request.getKeyword(),
                request.getSort()
        );
        
        // 转换为DTO
        List<ProductResponse> responseList = productConverter.toResponseList(pageResult.getList());
        
        // 构建分页响应
        PageResult<ProductResponse> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setPageNum(pageResult.getPageNum());
        result.setPageSize(pageResult.getPageSize());
        result.setList(responseList);
        
        return Result.success(result);
    }
    
    /**
     * 商品详情
     */
    @SkipAuth  // 不需要登录
    @GetMapping("/detail/{id}")
    @Operation(summary = "获取商品详情", description = "根据商品ID获取商品详细信息")
    @Parameter(name = "id", description = "商品ID", required = true, example = "1")
    public Result<ProductResponse> getProductDetail(@PathVariable Long id) {
        Product product = productService.getProductDetail(id);
        ProductResponse response = productConverter.toResponse(product);
        return Result.success(response);
    }
    
    /**
     * 商品分类列表
     */
    @SkipAuth  // 不需要登录
    @GetMapping("/categories")
    @Operation(summary = "获取商品分类列表", description = "获取所有商品分类")
    public Result<List<Category>> getCategories() {
        List<Category> categories = categoryService.getCategoryList();
        return Result.success(categories);
    }
    
    /**
     * 推荐商品列表
     */
    @SkipAuth  // 不需要登录
    @GetMapping("/recommend")
    @Operation(summary = "获取推荐商品", description = "获取热销推荐商品列表")
    @Parameter(name = "limit", description = "数量限制", example = "10")
    public Result<List<ProductResponse>> getRecommendProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Product> products = productService.getRecommendProducts(limit);
        List<ProductResponse> responseList = productConverter.toResponseList(products);
        return Result.success(responseList);
    }
}
