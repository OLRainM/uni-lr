-- 藏药小程序数据库初始化脚本

CREATE DATABASE IF NOT EXISTS `tibetan_medicine` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `tibetan_medicine`;

-- 用户表
CREATE TABLE `tb_user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `open_id` VARCHAR(100) DEFAULT NULL COMMENT '微信openid',
  `union_id` VARCHAR(100) DEFAULT NULL COMMENT '微信unionid',
  `nick_name` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `gender` TINYINT(1) DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
  `vip_level` VARCHAR(20) DEFAULT NULL COMMENT '会员等级',
  `points` INT(11) DEFAULT 0 COMMENT '积分',
  `status` TINYINT(1) DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_open_id` (`open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品分类表
CREATE TABLE `tb_category` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `icon` VARCHAR(255) DEFAULT NULL COMMENT '分类图标',
  `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父级分类ID',
  `sort` INT(11) DEFAULT 0 COMMENT '排序',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
CREATE TABLE `tb_product` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
  `category_id` BIGINT(20) NOT NULL COMMENT '分类ID',
  `main_image` VARCHAR(255) DEFAULT NULL COMMENT '主图',
  `images` TEXT COMMENT '商品图片',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `stock` INT(11) DEFAULT 0 COMMENT '库存',
  `sales` INT(11) DEFAULT 0 COMMENT '销量',
  `desc` VARCHAR(500) DEFAULT NULL COMMENT '商品描述',
  `detail` TEXT COMMENT '商品详情',
  `origin` VARCHAR(50) DEFAULT NULL COMMENT '产地',
  `grade` VARCHAR(50) DEFAULT NULL COMMENT '等级',
  `specification` VARCHAR(50) DEFAULT NULL COMMENT '规格',
  `shelf_life` VARCHAR(50) DEFAULT NULL COMMENT '保质期',
  `storage` VARCHAR(100) DEFAULT NULL COMMENT '储存方式',
  `tags` VARCHAR(200) DEFAULT NULL COMMENT '商品标签',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态 0-下架 1-上架',
  `sort` INT(11) DEFAULT 0 COMMENT '排序',
  `is_recommend` TINYINT(1) DEFAULT 0 COMMENT '是否推荐',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 购物车表
CREATE TABLE `tb_cart` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `product_id` BIGINT(20) NOT NULL COMMENT '商品ID',
  `sku_id` BIGINT(20) DEFAULT NULL COMMENT '规格ID',
  `quantity` INT(11) NOT NULL DEFAULT 1 COMMENT '数量',
  `is_selected` TINYINT(1) DEFAULT 1 COMMENT '是否选中',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 订单表
CREATE TABLE `tb_order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `address_id` BIGINT(20) DEFAULT NULL COMMENT '地址ID',
  `receiver_name` VARCHAR(50) DEFAULT NULL COMMENT '收货人',
  `receiver_phone` VARCHAR(20) DEFAULT NULL COMMENT '收货电话',
  `receiver_address` VARCHAR(255) DEFAULT NULL COMMENT '收货地址',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '商品总额',
  `freight_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
  `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
  `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
  `pay_type` TINYINT(1) DEFAULT NULL COMMENT '支付方式',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `status` TINYINT(1) DEFAULT 0 COMMENT '订单状态 0-待付款 1-待发货 2-待收货 3-已完成 4-已取消',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '订单备注',
  `ship_time` DATETIME DEFAULT NULL COMMENT '发货时间',
  `confirm_time` DATETIME DEFAULT NULL COMMENT '确认收货时间',
  `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE `tb_order_item` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` BIGINT(20) NOT NULL COMMENT '订单ID',
  `product_id` BIGINT(20) NOT NULL COMMENT '商品ID',
  `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
  `product_image` VARCHAR(255) DEFAULT NULL COMMENT '商品图片',
  `specification` VARCHAR(100) DEFAULT NULL COMMENT '规格',
  `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
  `quantity` INT(11) NOT NULL COMMENT '购买数量',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '小计',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 收货地址表
CREATE TABLE `tb_address` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '收货人',
  `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `province` VARCHAR(50) NOT NULL COMMENT '省份',
  `city` VARCHAR(50) NOT NULL COMMENT '城市',
  `district` VARCHAR(50) NOT NULL COMMENT '区县',
  `detail` VARCHAR(255) NOT NULL COMMENT '详细地址',
  `is_default` TINYINT(1) DEFAULT 0 COMMENT '是否默认',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- 藏药知识表
CREATE TABLE `tb_knowledge` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(100) NOT NULL COMMENT '标题',
  `cover` VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '摘要',
  `content` TEXT COMMENT '内容',
  `views` INT(11) DEFAULT 0 COMMENT '阅读量',
  `sort` INT(11) DEFAULT 0 COMMENT '排序',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态 0-下架 1-发布',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='藏药知识表';

-- 插入测试数据
INSERT INTO `tb_category` (`name`, `icon`, `sort`) VALUES
('冬虫夏草', 'icon-cordyceps', 1),
('藏红花', 'icon-saffron', 2),
('雪莲花', 'icon-snow-lotus', 3),
('红景天', 'icon-rhodiola', 4),
('藏灵芝', 'icon-ganoderma', 5),
('天麻', 'icon-gastrodia', 6);
