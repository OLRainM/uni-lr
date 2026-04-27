-- ==========================================
-- 清空并重新插入测试数据
-- ==========================================

USE tibetan_medicine;

-- 1. 清空所有表（按依赖关系顺序）
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE user_coupons;
TRUNCATE TABLE coupons;
TRUNCATE TABLE cart;
TRUNCATE TABLE products;
TRUNCATE TABLE categories;
TRUNCATE TABLE knowledge;
TRUNCATE TABLE banners;
TRUNCATE TABLE users;
TRUNCATE TABLE addresses;

SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================
-- 2. 插入用户数据
-- ==========================================
INSERT INTO users (id, openid, nickname, avatar, phone, gender, status, create_time, update_time) VALUES
(1, 'wx_openid_001', '张三', 'https://img.yzcdn.cn/vant/cat.jpeg', '13800138000', 1, 1, NOW(), NOW()),
(2, 'wx_openid_002', '李四', 'https://img.yzcdn.cn/vant/cat.jpeg', '13800138001', 2, 1, NOW(), NOW()),
(3, 'wx_openid_003', '王五', 'https://img.yzcdn.cn/vant/cat.jpeg', '13800138002', 0, 1, NOW(), NOW());

-- ==========================================
-- 3. 插入商品分类
-- ==========================================
INSERT INTO categories (id, name, icon, sort, status, create_time, update_time) VALUES
(1, '冬虫夏草', 'icon-cordyceps', 1, 1, NOW(), NOW()),
(2, '藏红花', 'icon-saffron', 2, 1, NOW(), NOW()),
(3, '雪莲花', 'icon-snow-lotus', 3, 1, NOW(), NOW()),
(4, '红景天', 'icon-rhodiola', 4, 1, NOW(), NOW()),
(5, '藏灵芝', 'icon-ganoderma', 5, 1, NOW(), NOW());

-- ==========================================
-- 4. 插入商品数据
-- ==========================================
INSERT INTO products (id, category_id, name, subtitle, price, original_price, stock, sales, image, images, detail, params, specs, origin, shelf_life, is_new, is_hot, status, create_time, update_time) VALUES
(1, 1, '高原野生冬虫夏草', '优质藏药，滋补佳品', 168.00, 200.00, 100, 520, 'https://img.yzcdn.cn/vant/cat.jpeg', '["https://img.yzcdn.cn/vant/cat.jpeg"]', '<p>产品详情</p>', '{}', '[]', '西藏', '24个月', 1, 1, 1, NOW(), NOW()),
(2, 1, '精选红虫草礼盒', '伊妈汗旺 彩蝶飞舞', 299.00, 350.00, 50, 230, 'https://img.yzcdn.cn/vant/cat.jpeg', '["https://img.yzcdn.cn/vant/cat.jpeg"]', '<p>产品详情</p>', '{}', '[]', '西藏', '24个月', 1, 0, 1, NOW(), NOW()),
(3, 2, '顶级藏红花', '100%纯天然 无添加', 1280.00, 1500.00, 30, 180, 'https://img.yzcdn.cn/vant/cat.jpeg', '["https://img.yzcdn.cn/vant/cat.jpeg"]', '<p>产品详情</p>', '{}', '[]', '西藏', '36个月', 0, 1, 1, NOW(), NOW()),
(4, 2, '藏红花礼盒装', '礼盒包装 送礼佳品', 360.00, 400.00, 80, 156, 'https://img.yzcdn.cn/vant/cat.jpeg', '["https://img.yzcdn.cn/vant/cat.jpeg"]', '<p>产品详情</p>', '{}', '[]', '青海', '36个月', 0, 0, 1, NOW(), NOW()),
(5, 3, '天山雪莲花', '天然雪莲 无农药', 580.00, 680.00, 45, 89, 'https://img.yzcdn.cn/vant/cat.jpeg', '["https://img.yzcdn.cn/vant/cat.jpeg"]', '<p>产品详情</p>', '{}', '[]', '四川', '24个月', 0, 1, 1, NOW(), NOW());

-- ==========================================
-- 5. 插入藏药知识
-- ==========================================
INSERT INTO knowledge (id, title, cover, content, views, status, create_time, update_time) VALUES
(1, '冬虫夏草的功效与作用', 'https://img.yzcdn.cn/vant/cat.jpeg', '<h2>冬虫夏草简介</h2><p>冬虫夏草是一种珍贵的中药材...</p>', 1250, 1, NOW(), NOW()),
(2, '藏红花的正确使用方法', 'https://img.yzcdn.cn/vant/cat.jpeg', '<h2>藏红花使用指南</h2><p>藏红花具有活血化瘀...</p>', 890, 1, NOW(), NOW()),
(3, '如何辨别真假虫草', 'https://img.yzcdn.cn/vant/cat.jpeg', '<h2>虫草鉴别方法</h2><p>市场上虫草质量参差不齐...</p>', 2340, 1, NOW(), NOW());

-- ==========================================
-- 6. 插入轮播图
-- ==========================================
INSERT INTO banners (id, title, image, link, type, sort, status, create_time, update_time) VALUES
(1, '冬虫夏草特惠', 'https://img.yzcdn.cn/vant/cat.jpeg', '/pages/product/list?categoryId=1', 1, 1, 1, NOW(), NOW()),
(2, '藏红花新品上市', 'https://img.yzcdn.cn/vant/cat.jpeg', '/pages/product/detail?id=3', 1, 2, 1, NOW(), NOW()),
(3, '限时抢购活动', 'https://img.yzcdn.cn/vant/cat.jpeg', '/pages/product/list', 2, 3, 1, NOW(), NOW());

-- ==========================================
-- 7. 插入优惠券
-- ==========================================
INSERT INTO coupons (id, name, type, discount, min_amount, total, received, start_time, end_time, status, create_time, update_time) VALUES
(1, '新用户专享券', 1, 10.00, 50.00, 1000, 150, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, NOW(), NOW()),
(2, '满100减20', 1, 20.00, 100.00, 500, 80, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, NOW(), NOW()),
(3, '9折优惠券', 2, 0.90, 0.00, 2000, 300, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), 1, NOW(), NOW());

-- ==========================================
-- 8. 插入用户优惠券
-- ==========================================
INSERT INTO user_coupons (id, user_id, coupon_id, status, create_time, update_time) VALUES
(1, 1, 1, 0, NOW(), NOW()),
(2, 1, 2, 0, NOW(), NOW()),
(3, 1, 3, 0, NOW(), NOW());

-- ==========================================
-- 9. 插入订单
-- ==========================================
INSERT INTO orders (id, order_no, user_id, receiver_name, receiver_phone, receiver_province, receiver_city, receiver_district, receiver_address, 
                   total_amount, goods_amount, freight, discount, coupon_id, pay_method, pay_time, status, remark, 
                   ship_time, ship_no, ship_company, complete_time, cancel_time, cancel_reason, create_time, update_time) VALUES
(1, 'ORD202512090001', 1, '张三', '13800138000', '西藏自治区', '拉萨市', '城关区', '北京中路1号',
 168.00, 168.00, 0.00, 0.00, NULL, '微信支付', NOW(), 2, '请尽快发货', NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'ORD202512090002', 1, '张三', '13800138000', '西藏自治区', '拉萨市', '城关区', '北京中路1号',
 1280.00, 1280.00, 0.00, 0.00, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW());

-- ==========================================
-- 验证数据
-- ==========================================
SELECT '✅ 数据插入完成！' AS message;
SELECT COUNT(*) AS user_count FROM users;
SELECT COUNT(*) AS category_count FROM categories;
SELECT COUNT(*) AS product_count FROM products;
SELECT COUNT(*) AS knowledge_count FROM knowledge;
SELECT COUNT(*) AS banner_count FROM banners;
SELECT COUNT(*) AS coupon_count FROM coupons;
SELECT COUNT(*) AS user_coupon_count FROM user_coupons;
SELECT COUNT(*) AS order_count FROM orders;

-- ==========================================
-- 显示插入的数据
-- ==========================================
SELECT '===== 用户数据 =====' AS '';
SELECT id, openid, nickname, phone FROM users;

SELECT '===== 分类数据 =====' AS '';
SELECT id, name, icon, sort FROM categories;

SELECT '===== 商品数据 =====' AS '';
SELECT id, name, subtitle, price, sales FROM products;

SELECT '===== 知识数据 =====' AS '';
SELECT id, title, views FROM knowledge;

SELECT '===== 轮播图数据 =====' AS '';
SELECT id, title, type, sort FROM banners;

SELECT '===== 优惠券数据 =====' AS '';
SELECT id, name, type, discount FROM coupons;

SELECT '===== 订单数据 =====' AS '';
SELECT id, order_no, user_id, status, total_amount FROM orders;
