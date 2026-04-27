-- ==========================================
-- 藏药小程序测试数据插入脚本
-- ==========================================

USE tibetan_medicine;

-- 清空现有数据（可选）
-- TRUNCATE TABLE products;
-- TRUNCATE TABLE categories;
-- TRUNCATE TABLE users;
-- TRUNCATE TABLE knowledge;
-- TRUNCATE TABLE banners;
-- TRUNCATE TABLE coupons;

-- ==========================================
-- 1. 插入用户数据
-- ==========================================
INSERT INTO users (id, openid, nickname, avatar, phone, gender, status, create_time, update_time) VALUES
(1, 'wx_openid_001', '张三', 'https://img.yzcdn.cn/vant/cat.jpeg', '13800138000', 1, 1, NOW(), NOW()),
(2, 'wx_openid_002', '李四', 'https://img.yzcdn.cn/vant/cat.jpeg', '13800138001', 2, 1, NOW(), NOW()),
(3, 'wx_openid_003', '王五', 'https://img.yzcdn.cn/vant/cat.jpeg', '13800138002', 0, 1, NOW(), NOW());

-- ==========================================
-- 2. 插入商品分类
-- ==========================================
INSERT INTO categories (id, name, icon, sort, status, create_time, update_time) VALUES
(1, '冬虫夏草', 'icon-cordyceps', 1, 1, NOW(), NOW()),
(2, '藏红花', 'icon-saffron', 2, 1, NOW(), NOW()),
(3, '雪莲花', 'icon-snow-lotus', 3, 1, NOW(), NOW()),
(4, '红景天', 'icon-rhodiola', 4, 1, NOW(), NOW()),
(5, '藏灵芝', 'icon-ganoderma', 5, 1, NOW(), NOW());

-- ==========================================
-- 3. 插入商品数据
-- ==========================================
INSERT INTO products (category_id, name, subtitle, price, original_price, stock, sales, image, images, detail, params, specs, origin, shelf_life, is_new, is_hot, status, create_time, update_time) VALUES
-- 冬虫夏草系列
(1, '高原野生冬虫夏草', '优质藏药，滋补佳品', 168.00, 200.00, 100, 520, 'https://img.yzcdn.cn/vant/cat.jpeg', '["https://img.yzcdn.cn/vant/cat.jpeg"]', '<p>产品详情</p>', '{}', '[]', '西藏', '24个月', 1, 1, 1, NOW(), NOW()),
(1, '精选红虫草礼盒', '伊妈汗旺 彩蝶飞舞', 299.00, 350.00, 50, 230, 'https://img.yzcdn.cn/vant/cat.jpeg', '["https://img.yzcdn.cn/vant/cat.jpeg"]', '<p>产品详情</p>', '{}', '[]', '西藏', '24个月', 1, 0, 1, NOW(), NOW()),

-- 藏红花系列
(2, '顶级藏红花', '100%纯天然 无添加', 1280.00, 1500.00, 30, 180, 'https://img.yzcdn.cn/vant/cat.jpeg', '["https://img.yzcdn.cn/vant/cat.jpeg"]', '<p>产品详情</p>', '{}', '[]', '西藏', '36个月', 0, 1, 1, NOW(), NOW()),
(2, '藏红花礼盒装', '礼盒包装 送礼佳品', 360.00, 400.00, 80, 156, 'https://img.yzcdn.cn/vant/cat.jpeg', '["https://img.yzcdn.cn/vant/cat.jpeg"]', '<p>产品详情</p>', '{}', '[]', '青海', '36个月', 0, 0, 1, NOW(), NOW()),

-- 雪莲花系列
(3, '天山雪莲花', '天然雪莲 无农药', 580.00, 680.00, 45, 89, 'https://img.yzcdn.cn/vant/cat.jpeg', '["https://img.yzcdn.cn/vant/cat.jpeg"]', '<p>产品详情</p>', '{}', '[]', '四川', '24个月', 0, 1, 1, NOW(), NOW());

-- ==========================================
-- 4. 插入藏药知识
-- ==========================================
INSERT INTO knowledge (title, cover, content, views, status, create_time, update_time) VALUES
('冬虫夏草的功效与作用', 'https://img.yzcdn.cn/vant/cat.jpeg', '<h2>冬虫夏草简介</h2><p>冬虫夏草是一种珍贵的中药材...</p>', 1250, 1, NOW(), NOW()),
('藏红花的正确使用方法', 'https://img.yzcdn.cn/vant/cat.jpeg', '<h2>藏红花使用指南</h2><p>藏红花具有活血化瘀...</p>', 890, 1, NOW(), NOW()),
('如何辨别真假虫草', 'https://img.yzcdn.cn/vant/cat.jpeg', '<h2>虫草鉴别方法</h2><p>市场上虫草质量参差不齐...</p>', 2340, 1, NOW(), NOW());

-- ==========================================
-- 5. 插入轮播图
-- ==========================================
INSERT INTO banners (title, image, link, type, sort, status, create_time, update_time) VALUES
('冬虫夏草特惠', 'https://img.yzcdn.cn/vant/cat.jpeg', '/pages/product/list?categoryId=1', 1, 1, 1, NOW(), NOW()),
('藏红花新品上市', 'https://img.yzcdn.cn/vant/cat.jpeg', '/pages/product/detail?id=3', 1, 2, 1, NOW(), NOW()),
('限时抢购活动', 'https://img.yzcdn.cn/vant/cat.jpeg', '/pages/product/list', 2, 3, 1, NOW(), NOW());

-- ==========================================
-- 6. 插入优惠券
-- ==========================================
INSERT INTO coupons (name, type, discount, min_amount, total, received, start_time, end_time, status, create_time, update_time) VALUES
('新用户专享券', 1, 10.00, 50.00, 1000, 150, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, NOW(), NOW()),
('满100减20', 1, 20.00, 100.00, 500, 80, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, NOW(), NOW()),
('9折优惠券', 2, 0.90, 0.00, 2000, 300, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), 1, NOW(), NOW());

-- ==========================================
-- 7. 插入用户优惠券（给用户1发放）
-- ==========================================
INSERT INTO user_coupons (user_id, coupon_id, status, create_time, update_time) VALUES
(1, 1, 0, NOW(), NOW()),
(1, 2, 0, NOW(), NOW()),
(1, 3, 0, NOW(), NOW());

-- ==========================================
-- 8. 插入测试订单
-- ==========================================
INSERT INTO orders (order_no, user_id, receiver_name, receiver_phone, receiver_province, receiver_city, receiver_district, receiver_address, 
                   total_amount, goods_amount, freight, discount, coupon_id, pay_method, pay_time, status, remark, 
                   ship_time, ship_no, ship_company, complete_time, cancel_time, cancel_reason, create_time, update_time) VALUES
('ORD202512090001', 1, '张三', '13800138000', '西藏自治区', '拉萨市', '城关区', '北京中路1号',
 168.00, 168.00, 0.00, 0.00, NULL, '微信支付', NOW(), 2, '请尽快发货', NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
('ORD202512090002', 1, '张三', '13800138000', '西藏自治区', '拉萨市', '城关区', '北京中路1号',
 1280.00, 1280.00, 0.00, 0.00, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW());

-- ==========================================
-- 9. 插入订单商品明细
-- ==========================================
-- 订单1的商品明细（高原野生冬虫夏草）
INSERT INTO order_items (order_id, product_id, product_name, spec_name, price, quantity, image) VALUES
(1, 1, '高原野生冬虫夏草', '500g', 168.00, 1, 'https://img.yzcdn.cn/vant/cat.jpeg');

-- 订单2的商品明细（顶级藏红花）
INSERT INTO order_items (order_id, product_id, product_name, spec_name, price, quantity, image) VALUES
(2, 3, '顶级藏红花', '50g', 1280.00, 1, 'https://img.yzcdn.cn/vant/cat.jpeg');

-- ==========================================
-- 10. 插入收货地址
-- ==========================================
INSERT INTO addresses (user_id, name, phone, province, city, district, detail, is_default, create_time, update_time) VALUES
(1, '张三', '13800138000', '西藏自治区', '拉萨市', '城关区', '北京中路1号', 1, NOW(), NOW()),
(1, '张三', '13900139000', '四川省', '成都市', '武侯区', '天府大道500号', 0, NOW(), NOW());

-- ==========================================
-- 验证数据
-- ==========================================
SELECT '数据插入完成！' AS message;
SELECT COUNT(*) AS user_count FROM users;
SELECT COUNT(*) AS category_count FROM categories;
SELECT COUNT(*) AS product_count FROM products;
SELECT COUNT(*) AS knowledge_count FROM knowledge;
SELECT COUNT(*) AS banner_count FROM banners;
SELECT COUNT(*) AS coupon_count FROM coupons;
SELECT COUNT(*) AS user_coupon_count FROM user_coupons;
SELECT COUNT(*) AS order_count FROM orders;
SELECT COUNT(*) AS order_item_count FROM order_items;
SELECT COUNT(*) AS address_count FROM addresses;
