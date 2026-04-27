package com.tibetan.medicine.converter;

import com.tibetan.medicine.dto.response.ProductResponse;
import com.tibetan.medicine.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品转换器
 * 
 * 功能：Entity <-> DTO 转换
 * 
 * @author Tibetan Medicine Team
 * @date 2025-01-14
 */
@Component
public class ProductConverter {
    
    /**
     * Entity转Response DTO
     */
    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSubtitle(product.getSubtitle());
        response.setPrice(product.getPrice());
        response.setOriginalPrice(product.getOriginalPrice());
        response.setStock(product.getStock());
        response.setSales(product.getSales());
        response.setImage(product.getImage());
        response.setImages(product.getImages());
        response.setCategoryId(product.getCategoryId());
        response.setDescription(product.getDetail());
        response.setIsNew(product.getIsNew());
        response.setIsHot(product.getIsHot());
        response.setCreateTime(product.getCreateTime());
        
        return response;
    }
    
    /**
     * Entity列表转Response DTO列表
     */
    public List<ProductResponse> toResponseList(List<Product> products) {
        if (products == null) {
            return null;
        }
        
        return products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
