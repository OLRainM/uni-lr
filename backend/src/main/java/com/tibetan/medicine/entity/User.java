package com.tibetan.medicine.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("users")
public class User implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 微信openid（必填）
     */
    private String openid;
    
    /**
     * 微信unionid（数据库中不存在）
     */
    @TableField(exist = false)
    private String unionid;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 性别 0-未知 1-男 2-女
     */
    private Integer gender;
    
    /**
     * 会员等级（数据库中不存在）
     */
    @TableField(exist = false)
    private String vipLevel;
    
    /**
     * 积分（数据库中不存在）
     */
    @TableField(exist = false)
    private Integer points;
    
    /**
     * 账户状态 0-正常 1-禁用
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
