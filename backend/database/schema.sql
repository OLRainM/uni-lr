-- ====================================
-- 藏药小程序数据库结构
-- 版本: 1.0.0
-- 创建日期: 2025-12-02
-- ====================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS tibetan_medicine 
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

USE tibetan_medicine;

-- ====================================
-- 1. 用户表
-- ====================================
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  openid VARCHAR(100) UNIQUE NOT NULL COMMENT '微信openid',
  nickname VARCHAR(100) COMMENT '昵称',
  avatar VARCHAR(500) COMMENT '头像URL',
  phone VARCHAR(20) COMMENT '手机号',
  gender TINYINT DEFAULT 0 COMMENT '性别:0-未知,1-男,2-女',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  status TINYINT DEFAULT 1 COMMENT '状态:1-正常,0-禁用',
  INDEX idx_openid (openid),
  INDEX idx_phone (phone),
  INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ====================================
-- 2. 商品分类表
-- ====================================
CREATE TABLE categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  name VARCHAR(100) NOT NULL COMMENT '分类名称',
  icon VARCHAR(500) COMMENT '分类图标',
  sort INT DEFAULT 0 COMMENT '排序',
  status TINYINT DEFAULT 1 COMMENT '状态:1-显示,0-隐藏',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ====================================
-- 3. 商品表
-- ====================================
CREATE TABLE products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
  category_id BIGINT NOT NULL COMMENT '分类ID',
  name VARCHAR(200) NOT NULL COMMENT '商品名称',
  subtitle VARCHAR(500) COMMENT '副标题',
  price DECIMAL(10,2) NOT NULL COMMENT '价格',
  original_price DECIMAL(10,2) COMMENT '原价',
  stock INT DEFAULT 0 COMMENT '库存',
  sales INT DEFAULT 0 COMMENT '销量',
  image VARCHAR(500) COMMENT '主图URL',
  images TEXT COMMENT '商品图片JSON数组',
  detail TEXT COMMENT '商品详情HTML',
  params TEXT COMMENT '商品参数JSON',
  specs TEXT COMMENT '商品规格JSON',
  origin VARCHAR(100) COMMENT '产地',
  shelf_life VARCHAR(50) COMMENT '保质期',
  is_new TINYINT DEFAULT 0 COMMENT '是否新品:1-是,0-否',
  is_hot TINYINT DEFAULT 0 COMMENT '是否热销:1-是,0-否',
  status TINYINT DEFAULT 1 COMMENT '状态:1-上架,0-下架',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_category (category_id),
  INDEX idx_status (status),
  INDEX idx_is_new (is_new),
  INDEX idx_is_hot (is_hot),
  INDEX idx_sales (sales),
  INDEX idx_price (price)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ====================================
-- 4. 购物车表
-- ====================================
CREATE TABLE cart (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  spec_name VARCHAR(100) COMMENT '规格名称',
  quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_user (user_id),
  INDEX idx_product (product_id),
  UNIQUE KEY uk_user_product_spec (user_id, product_id, spec_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ====================================
-- 5. 订单表
-- ====================================
CREATE TABLE orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
  order_no VARCHAR(50) UNIQUE NOT NULL COMMENT '订单号',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  status VARCHAR(20) NOT NULL COMMENT '订单状态:unpaid-待付款,unshipped-待发货,shipped-待收货,completed-已完成,cancelled-已取消',
  total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  goods_amount DECIMAL(10,2) NOT NULL COMMENT '商品金额',
  freight DECIMAL(10,2) DEFAULT 0 COMMENT '运费',
  discount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
  coupon_id BIGINT COMMENT '优惠券ID',
  receiver_name VARCHAR(50) NOT NULL COMMENT '收货人',
  receiver_phone VARCHAR(20) NOT NULL COMMENT '收货电话',
  receiver_province VARCHAR(50) NOT NULL COMMENT '省',
  receiver_city VARCHAR(50) NOT NULL COMMENT '市',
  receiver_district VARCHAR(50) NOT NULL COMMENT '区',
  receiver_address VARCHAR(500) NOT NULL COMMENT '详细地址',
  remark VARCHAR(500) COMMENT '订单备注',
  pay_method VARCHAR(20) COMMENT '支付方式',
  pay_time DATETIME COMMENT '支付时间',
  ship_time DATETIME COMMENT '发货时间',
  ship_no VARCHAR(100) COMMENT '快递单号',
  ship_company VARCHAR(100) COMMENT '快递公司',
  complete_time DATETIME COMMENT '完成时间',
  cancel_time DATETIME COMMENT '取消时间',
  cancel_reason VARCHAR(500) COMMENT '取消原因',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_user (user_id),
  INDEX idx_order_no (order_no),
  INDEX idx_status (status),
  INDEX idx_create_time (create_time),
  INDEX idx_pay_time (pay_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ====================================
-- 6. 订单商品表
-- ====================================
CREATE TABLE order_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单商品ID',
  order_id BIGINT NOT NULL COMMENT '订单ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
  spec_name VARCHAR(100) COMMENT '规格',
  price DECIMAL(10,2) NOT NULL COMMENT '购买价格',
  quantity INT NOT NULL COMMENT '购买数量',
  image VARCHAR(500) COMMENT '商品图片',
  INDEX idx_order (order_id),
  INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

-- ====================================
-- 7. 收货地址表
-- ====================================
CREATE TABLE addresses (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  name VARCHAR(50) NOT NULL COMMENT '收货人',
  phone VARCHAR(20) NOT NULL COMMENT '手机号',
  province VARCHAR(50) NOT NULL COMMENT '省',
  city VARCHAR(50) NOT NULL COMMENT '市',
  district VARCHAR(50) NOT NULL COMMENT '区',
  detail VARCHAR(500) NOT NULL COMMENT '详细地址',
  is_default TINYINT DEFAULT 0 COMMENT '是否默认:1-是,0-否',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_user (user_id),
  INDEX idx_is_default (is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ====================================
-- 8. 优惠券表
-- ====================================
CREATE TABLE coupons (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '优惠券ID',
  name VARCHAR(100) NOT NULL COMMENT '优惠券名称',
  type VARCHAR(20) NOT NULL COMMENT '类型:discount-满减,percentage-折扣',
  discount DECIMAL(10,2) NOT NULL COMMENT '优惠金额或折扣',
  min_amount DECIMAL(10,2) DEFAULT 0 COMMENT '最低消费金额',
  total INT NOT NULL COMMENT '发放总量',
  used INT DEFAULT 0 COMMENT '已使用数量',
  start_time DATETIME NOT NULL COMMENT '开始时间',
  end_time DATETIME NOT NULL COMMENT '结束时间',
  status TINYINT DEFAULT 1 COMMENT '状态:1-有效,0-失效',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_status (status),
  INDEX idx_time (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- ====================================
-- 9. 用户优惠券表
-- ====================================
CREATE TABLE user_coupons (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户优惠券ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  coupon_id BIGINT NOT NULL COMMENT '优惠券ID',
  order_id BIGINT COMMENT '使用订单ID',
  status VARCHAR(20) NOT NULL COMMENT '状态:unused-未使用,used-已使用,expired-已过期',
  use_time DATETIME COMMENT '使用时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  INDEX idx_user (user_id),
  INDEX idx_coupon (coupon_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- ====================================
-- 10. 藏药知识表
-- ====================================
CREATE TABLE knowledge (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '知识ID',
  title VARCHAR(200) NOT NULL COMMENT '标题',
  subtitle VARCHAR(500) COMMENT '副标题',
  cover VARCHAR(500) COMMENT '封面图',
  content TEXT COMMENT '内容HTML',
  category VARCHAR(50) COMMENT '分类',
  views INT DEFAULT 0 COMMENT '浏览量',
  status TINYINT DEFAULT 1 COMMENT '状态:1-显示,0-隐藏',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_category (category),
  INDEX idx_views (views),
  INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='藏药知识表';

-- ====================================
-- 11. 管理员表
-- ====================================
CREATE TABLE admins (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
  username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
  password VARCHAR(200) NOT NULL COMMENT '密码(加密)',
  nickname VARCHAR(100) COMMENT '昵称',
  avatar VARCHAR(500) COMMENT '头像',
  role VARCHAR(20) DEFAULT 'admin' COMMENT '角色:super-超级管理员,admin-管理员',
  status TINYINT DEFAULT 1 COMMENT '状态:1-正常,0-禁用',
  last_login_time DATETIME COMMENT '最后登录时间',
  last_login_ip VARCHAR(50) COMMENT '最后登录IP',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- ====================================
-- 12. 轮播图表
-- ====================================
CREATE TABLE banners (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '轮播图ID',
  title VARCHAR(100) COMMENT '标题',
  image VARCHAR(500) NOT NULL COMMENT '图片URL',
  link_type VARCHAR(20) COMMENT '链接类型:product-商品,url-外链',
  link_value VARCHAR(500) COMMENT '链接值',
  sort INT DEFAULT 0 COMMENT '排序',
  status TINYINT DEFAULT 1 COMMENT '状态:1-显示,0-隐藏',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_sort (sort),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- ====================================
-- 初始化数据
-- ====================================

-- 插入默认管理员 (用户名: admin, 密码: 123456)
-- 注意: 密码使用BCrypt加密，强度10
INSERT INTO admins (username, password, nickname, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2W4rWqDa6uCWPy6y.YuEqfq', '超级管理员', 'super');

-- 插入商品分类
INSERT INTO categories (name, icon, sort) VALUES 
('冬虫夏草', 'https://example.com/icons/category1.png', 1),
('藏红花', 'https://example.com/icons/category2.png', 2),
('雪莲花', 'https://example.com/icons/category3.png', 3),
('红景天', 'https://example.com/icons/category4.png', 4),
('灵芝', 'https://example.com/icons/category5.png', 5);

-- ====================================
-- 结束
-- ====================================
