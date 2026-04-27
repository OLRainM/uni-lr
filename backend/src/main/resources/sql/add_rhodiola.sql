-- ===================================================
-- 新增商品：红景天含片（金诃牌）
-- 执行数据库：tibetan_medicine
-- 执行时间：2025年
-- ===================================================

USE tibetan_medicine;

-- 插入红景天含片商品
-- category_id = 4 对应 '红景天' 分类（categories 表第4条）
INSERT INTO `products` (
    `name`,
    `category_id`,
    `main_image`,
    `images`,
    `price`,
    `original_price`,
    `stock`,
    `sales`,
    `desc`,
    `detail`,
    `origin`,
    `grade`,
    `specification`,
    `shelf_life`,
    `storage`,
    `tags`,
    `status`,
    `sort`,
    `is_recommend`,
    `create_time`,
    `update_time`
) VALUES (
    '红景天含片',                                      -- name
    4,                                                  -- category_id（红景天分类）
    'https://example.com/rhodiola.jpg',                -- main_image（前端会用本地图片映射覆盖）
    'https://example.com/rhodiola.jpg',                -- images
    68.00,                                              -- price
    98.00,                                              -- original_price
    500,                                                -- stock
    128,                                                -- sales
    '金诃牌红景天含片，每100g含红景天苷623mg，缓解体力疲劳，适合易疲劳人群。',   -- desc
    '【品牌】金诃藏药\n【原料】红景天\n【辅料】D-甘露糖醇、柠檬酸钠、阿斯巴甜(含苯丙氨酸)、硬脂酸镁、薄荷脑\n【功效成分/标志性成分及含量】每100g含：红景天苷623mg\n【保健功能】本品经动物实验评价，具有缓解体力疲劳的保健功能\n【适宜人群】易疲劳者\n【不适宜人群】少年儿童、孕妇、乳母\n【贮藏方法】密闭，置于阴凉干燥处\n【注意事项】本品不能代替药物；适宜人群外的人群不推荐食用本产品；苯丙酮尿症患者慎用\n【食用量及食用方法】每次1片，每日3次，含食',   -- detail
    '青藏高原',                                        -- origin
    '保健食品',                                        -- grade
    '60片/盒',                                         -- specification
    '24个月',                                          -- shelf_life
    '密闭，置于阴凉干燥处',                           -- storage
    '缓解疲劳,红景天,保健食品,金诃藏药',             -- tags
    1,                                                  -- status（上架）
    5,                                                  -- sort
    1,                                                  -- is_recommend（推荐）
    NOW(),                                              -- create_time
    NOW()                                               -- update_time
);

SELECT LAST_INSERT_ID() AS '新增商品ID';
SELECT '红景天含片商品插入成功！' AS message;

-- 验证：查看插入的商品
SELECT id, name, price, original_price, stock, status, is_recommend 
FROM products 
WHERE name = '红景天含片';

