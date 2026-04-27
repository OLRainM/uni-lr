package com.tibetan.medicine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tibetan.medicine.common.CacheConstants;
import com.tibetan.medicine.common.PageResult;
import com.tibetan.medicine.entity.Product;
import com.tibetan.medicine.mapper.ProductMapper;
import com.tibetan.medicine.service.ProductService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 商品服务实现类
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    
    @Override
    public PageResult<Product> getProductList(Integer pageNum, Integer pageSize, 
                                             Long categoryId, String keyword, Integer sort) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询上架商品
        wrapper.eq(Product::getStatus, 1);
        
        // 分类筛选
        if (categoryId != null && categoryId > 0) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword);
        }
        
        // 排序：1-综合 2-销量 3-价格升序 4-价格降序
        if (sort != null) {
            switch (sort) {
                case 2:
                    wrapper.orderByDesc(Product::getSales);
                    break;
                case 3:
                    wrapper.orderByAsc(Product::getPrice);
                    break;
                case 4:
                    wrapper.orderByDesc(Product::getPrice);
                    break;
                default:
                    // 综合排序：优先热销，然后按销量和创建时间
                    wrapper.orderByDesc(Product::getIsHot);
                    wrapper.orderByDesc(Product::getSales);
                    wrapper.orderByDesc(Product::getCreateTime);
            }
        } else {
            // 默认排序
            wrapper.orderByDesc(Product::getIsHot);
            wrapper.orderByDesc(Product::getSales);
            wrapper.orderByDesc(Product::getCreateTime);
        }
        
        Page<Product> result = this.page(page, wrapper);
        return new PageResult<>(result.getTotal(), pageNum, pageSize, result.getRecords());
    }
    
    @Override
    @Cacheable(value = CacheConstants.PRODUCT_CACHE, key = "#id")
    public Product getProductDetail(Long id) {
        return this.getById(id);
    }
    
    @Override
    @Cacheable(value = CacheConstants.PRODUCT_LIST_CACHE, key = "'recommend:' + #limit")
    public List<Product> getRecommendProducts(Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        wrapper.eq(Product::getIsHot, 1);  // 使用热销商品作为推荐
        wrapper.orderByDesc(Product::getSales);  // 按销量排序
        wrapper.last("LIMIT " + limit);
        return this.list(wrapper);
    }
    
    /**
     * 清空商品缓存（管理员更新商品时调用）
     */
    @CacheEvict(value = {CacheConstants.PRODUCT_CACHE, CacheConstants.PRODUCT_LIST_CACHE}, allEntries = true)
    public void clearProductCache() {
        // 清空所有商品相关缓存
    }
}
