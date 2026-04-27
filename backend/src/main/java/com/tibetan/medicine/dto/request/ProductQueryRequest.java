package com.tibetan.medicine.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 商品查询请求DTO
 * 
 * @author Tibetan Medicine Team
 * @date 2025-01-14
 */
@Data
@Schema(description = "商品查询请求")
public class ProductQueryRequest {
    
    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;
    
    @Schema(description = "每页数量", example = "10")
    @Min(value = 1, message = "每页数量最小为1")
    @Max(value = 100, message = "每页数量最大为100")
    private Integer pageSize = 10;
    
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;
    
    @Schema(description = "搜索关键词", example = "冬虫夏草")
    private String keyword;
    
    @Schema(description = "排序方式：1-默认 2-价格升序 3-价格降序 4-销量", example = "1")
    @Min(value = 1, message = "排序方式错误")
    @Max(value = 4, message = "排序方式错误")
    private Integer sort = 1;
}
