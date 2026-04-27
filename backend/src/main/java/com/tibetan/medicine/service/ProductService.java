package com.tibetan.medicine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tibetan.medicine.common.PageResult;
import com.tibetan.medicine.entity.Product;
import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService extends IService<Product> {
    
    /**
     * 分页查询商品列表
     */
    PageResult<Product> getProductList(Integer pageNum, Integer pageSize, 
                                       Long categoryId, String keyword, Integer sort);
    
    /**
     * 获取商品详情
     */
    Product getProductDetail(Long id);
    
    /**
     * 获取推荐商品
     */
    List<Product> getRecommendProducts(Integer limit);
}
