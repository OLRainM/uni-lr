-- ==========================================
-- 最小测试数据（用于快速测试）
-- ==========================================

USE tibetan_medicine;

-- 1. 插入1个用户
INSERT INTO users (id, openid, nickname, avatar, phone, gender, status, create_time, update_time) 
VALUES (1, 'wx_openid_001', '张三', 'https://img.yzcdn.cn/vant/cat.jpeg', '13800138000', 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE avatar=VALUES(avatar);

-- 2. 插入1个分类
INSERT INTO categories (id, name, icon, sort, status, create_time, update_time) 
VALUES (1, '冬虫夏草', 'icon-cordyceps', 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 3. 插入1个商品
INSERT INTO products (id, category_id, name, subtitle, price, original_price, stock, sales, 
                     image, images, detail, params, specs, origin, shelf_life, 
                     is_new, is_hot, status, create_time, update_time) 
VALUES (1, 1, '高原野生冬虫夏草', '优质藏药，滋补佳品', 168.00, 200.00, 100, 520, 
       'https://img.yzcdn.cn/vant/cat.jpeg', '["https://img.yzcdn.cn/vant/cat.jpeg"]', 
       '<p>产品详情</p>', '{}', '[]', '西藏', '24个月', 
       1, 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 4. 插入1个知识
INSERT INTO knowledge (id, title, cover, content, views, status, create_time, update_time) 
VALUES (1, '冬虫夏草的功效与作用', 'https://img.yzcdn.cn/vant/cat.jpeg', 
       '<h2>冬虫夏草简介</h2><p>冬虫夏草是一种珍贵的中药材...</p>', 1250, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 5. 插入1个轮播图
INSERT INTO banners (id, title, image, link, type, sort, status, create_time, update_time) 
VALUES (1, '冬虫夏草特惠', 'https://img.yzcdn.cn/vant/cat.jpeg', '/pages/product/list?categoryId=1', 1, 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 验证插入结果
SELECT '=== 插入结果验证 ===' AS '';
SELECT COUNT(*) AS '用户数' FROM users;
SELECT COUNT(*) AS '分类数' FROM categories;
SELECT COUNT(*) AS '商品数' FROM products;
SELECT COUNT(*) AS '知识数' FROM knowledge;
SELECT COUNT(*) AS '轮播图数' FROM banners;

-- 显示插入的数据
SELECT '=== 用户数据 ===' AS '';
SELECT * FROM users LIMIT 5;

SELECT '=== 分类数据 ===' AS '';
SELECT * FROM categories LIMIT 5;

SELECT '=== 商品数据 ===' AS '';
SELECT id, name, price, sales FROM products LIMIT 5;
