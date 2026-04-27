-- ==========================================
-- 藏药小程序数据库修复脚本
-- 用途：修复表名不匹配和缺失的表
-- 执行时间：2025-12-09
-- ==========================================

USE tibetan_medicine;

-- ==========================================
-- 第一步：重命名表（去掉tb_前缀）
-- ==========================================

-- 重命名表（去掉tb_前缀，每条单独执行）
ALTER TABLE tb_user RENAME TO users;
ALTER TABLE tb_category RENAME TO categories;
ALTER TABLE tb_product RENAME TO products;
ALTER TABLE tb_cart RENAME TO cart;
ALTER TABLE tb_order RENAME TO orders;
ALTER TABLE tb_order_item RENAME TO order_items;
ALTER TABLE tb_address RENAME TO addresses;
ALTER TABLE tb_knowledge RENAME TO knowledge;

SELECT '第一步完成：表重命名成功' AS message;

-- ==========================================
-- 第二步：删除deleted字段（不使用逻辑删除）
-- ==========================================

-- 删除deleted字段（忽略错误）
ALTER TABLE users DROP COLUMN deleted;
ALTER TABLE products DROP COLUMN deleted;
ALTER TABLE orders DROP COLUMN deleted;
ALTER TABLE addresses DROP COLUMN deleted;
ALTER TABLE knowledge DROP COLUMN deleted;

SELECT '第二步完成：deleted字段删除成功' AS message;

-- ==========================================
-- 第三步：创建缺失的表
-- ==========================================

-- 先删除可能存在的旧表（避免字段冲突）
DROP TABLE IF EXISTS `coupons`;
DROP TABLE IF EXISTS `user_coupons`;
DROP TABLE IF EXISTS `banners`;

SELECT '旧表已删除' AS message;

-- 3.1 创建优惠券表
CREATE TABLE `coupons` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '优惠券名称',
  `type` TINYINT(1) NOT NULL COMMENT '类型 1-满减 2-折扣',
  `discount` DECIMAL(10,2) NOT NULL COMMENT '优惠金额/折扣',
  `min_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '最低使用金额',
  `total` INT(11) NOT NULL COMMENT '发行总量',
  `received` INT(11) DEFAULT 0 COMMENT '已领取数量',
  `start_time` DATETIME NOT NULL COMMENT '有效期开始',
  `end_time` DATETIME NOT NULL COMMENT '有效期结束',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态 0-下架 1-上架',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_time` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

SELECT '优惠券表创建成功' AS message;

-- 3.2 创建用户优惠券表
CREATE TABLE `user_coupons` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `coupon_id` BIGINT(20) NOT NULL COMMENT '优惠券ID',
  `status` TINYINT(1) DEFAULT 0 COMMENT '状态 0-未使用 1-已使用 2-已过期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_coupon_id` (`coupon_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

SELECT '用户优惠券表创建成功' AS message;

-- 3.3 创建轮播图表
CREATE TABLE `banners` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(100) DEFAULT NULL COMMENT '标题',
  `image` VARCHAR(255) NOT NULL COMMENT '图片URL',
  `link` VARCHAR(255) DEFAULT NULL COMMENT '跳转链接',
  `type` TINYINT(1) DEFAULT 1 COMMENT '类型 1-商品 2-活动 3-外链',
  `sort` INT(11) DEFAULT 0 COMMENT '排序',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sort` (`sort`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

SELECT '轮播图表创建成功' AS message;

SELECT '第三步完成：缺失的表创建成功' AS message;

-- ==========================================
-- 第四步：插入测试数据
-- ==========================================

-- 4.1 插入优惠券测试数据
INSERT INTO `coupons` (`name`, `type`, `discount`, `min_amount`, `total`, `received`, `start_time`, `end_time`, `status`) 
VALUES 
('新用户专享券', 1, 10.00, 50.00, 1000, 0, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1),
('满100减20', 1, 20.00, 100.00, 500, 0, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1),
('9折优惠券', 2, 0.90, 0.00, 2000, 0, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), 1);

SELECT '优惠券测试数据插入成功' AS message;

-- 4.2 插入轮播图测试数据
INSERT INTO `banners` (`title`, `image`, `link`, `type`, `sort`, `status`) 
VALUES 
('冬虫夏草特惠', 'https://img.yzcdn.cn/vant/cat.jpeg', '/pages/product/list?categoryId=1', 1, 1, 1),
('藏红花新品', 'https://img.yzcdn.cn/vant/cat.jpeg', '/pages/product/detail?id=2', 1, 2, 1),
('限时抢购', 'https://img.yzcdn.cn/vant/cat.jpeg', '/pages/product/list', 2, 3, 1);

SELECT '轮播图测试数据插入成功' AS message;

-- ==========================================
-- 验证：检查所有表
-- ==========================================

SELECT '==========================================' AS '';
SELECT '数据库修复完成！' AS message;
SELECT '==========================================' AS '';

-- 显示所有表
SHOW TABLES;

SELECT '==========================================' AS '';
SELECT '请重启后端服务以使更改生效！' AS message;
SELECT '==========================================' AS '';
