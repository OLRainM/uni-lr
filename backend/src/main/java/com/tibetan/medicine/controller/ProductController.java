package com.tibetan.medicine.controller;

import com.tibetan.medicine.annotation.SkipAuth;
import com.tibetan.medicine.common.PageResult;
import com.tibetan.medicine.common.Result;
import com.tibetan.medicine.entity.Category;
import com.tibetan.medicine.entity.Product;
import com.tibetan.medicine.service.CategoryService;
import com.tibetan.medicine.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 */
@SkipAuth
@RestController
@RequestMapping("/product")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 商品列表
     */
    @GetMapping("/list")
    public Result<PageResult<Product>> getProductList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer sort) {
        
        PageResult<Product> result = productService.getProductList(pageNum, pageSize, categoryId, keyword, sort);
        return Result.success(result);
    }
    
    /**
     * 商品详情
     */
    @GetMapping("/detail/{id}")
    public Result<Product> getProductDetail(@PathVariable Long id) {
        Product product = productService.getProductDetail(id);
        return Result.success(product);
    }
    
    /**
     * 商品分类列表
     */
    @GetMapping("/categories")
    public Result<List<Category>> getCategories() {
        List<Category> categories = categoryService.getCategoryList();
        return Result.success(categories);
    }
    
    /**
     * 推荐商品列表
     */
    @GetMapping("/recommend")
    public Result<List<Product>> getRecommendProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Product> products = productService.getRecommendProducts(limit);
        return Result.success(products);
    }
}
