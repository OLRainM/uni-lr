package com.tibetan.medicine.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 藏药知识实体类
 */
@Data
@TableName("knowledge")
public class Knowledge implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 文章标题
     */
    private String title;
    
    /**
     * 封面图片
     */
    private String cover;
    
    /**
     * 文章摘要（数据库中不存在）
     */
    @TableField(exist = false)
    private String summary;
    
    /**
     * 文章内容（富文本）
     */
    private String content;
    
    /**
     * 阅读量
     */
    private Integer views;
    
    /**
     * 排序（数据库中不存在）
     */
    @TableField(exist = false)
    private Integer sort;
    
    /**
     * 状态 0-下架 1-发布
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
