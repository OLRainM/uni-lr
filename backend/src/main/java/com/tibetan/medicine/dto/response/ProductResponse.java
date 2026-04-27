package com.tibetan.medicine.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品响应DTO
 * 
 * @author Tibetan Medicine Team
 * @date 2025-01-14
 */
@Data
@Schema(description = "商品信息")
public class ProductResponse {
    
    @Schema(description = "商品ID", example = "1")
    private Long id;
    
    @Schema(description = "商品名称", example = "特级冬虫夏草 西藏那曲产")
    private String name;
    
    @Schema(description = "副标题", example = "3.5条/克 特级品质 野生采集")
    private String subtitle;
    
    @Schema(description = "价格", example = "1280.00")
    private BigDecimal price;
    
    @Schema(description = "原价", example = "1580.00")
    private BigDecimal originalPrice;
    
    @Schema(description = "库存", example = "50")
    private Integer stock;
    
    @Schema(description = "销量", example = "156")
    private Integer sales;
    
    @Schema(description = "主图URL")
    private String image;
    
    @Schema(description = "轮播图URL列表（逗号分隔）")
    private String images;
    
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;
    
    @Schema(description = "商品详情描述")
    private String description;
    
    @Schema(description = "是否新品：0-否 1-是", example = "1")
    private Integer isNew;
    
    @Schema(description = "是否热销：0-否 1-是", example = "1")
    private Integer isHot;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
