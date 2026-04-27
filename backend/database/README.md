# 数据库文件说明

> 藏药小程序数据库结构和初始化脚本

---

## 📂 文件列表

| 文件 | 说明 | 阅读时间 |
|------|------|----------|
| `schema.sql` | 数据库结构定义 | - |
| `init_data.sql` | 初始化测试数据 | - |
| `服务器数据库部署指南.md` | **服务器部署完整步骤** ⭐ | 15分钟 |
| `数据库部署检查清单.md` | 部署验证脚本和检查项 | 5分钟 |
| `快速部署命令.txt` | 可复制的快速命令 | 3分钟 |
| `README.md` | 本文档（本地开发参考） | 10分钟 |

---

## 🗄️ 数据库信息

### 基本信息

```
数据库名：tibetan_medicine
字符集：utf8mb4
排序规则：utf8mb4_unicode_ci
```

### 数据表（12张）

| 序号 | 表名 | 说明 | 记录数 |
|------|------|------|--------|
| 1 | `users` | 用户表 | - |
| 2 | `categories` | 商品分类表 | 5 |
| 3 | `products` | 商品表 | 5 |
| 4 | `cart` | 购物车表 | - |
| 5 | `orders` | 订单表 | - |
| 6 | `order_items` | 订单商品表 | - |
| 7 | `addresses` | 收货地址表 | - |
| 8 | `coupons` | 优惠券表 | 3 |
| 9 | `user_coupons` | 用户优惠券表 | - |
| 10 | `knowledge` | 藏药知识表 | 3 |
| 11 | `admins` | 管理员表 | 1 |
| 12 | `banners` | 轮播图表 | 3 |

---

## 🚀 使用方法

### 方法1：命令行执行

```bash
# 1. 登录MySQL
mysql -u root -p

# 2. 执行schema.sql（创建表）
SOURCE /path/to/schema.sql;

# 3. 执行init_data.sql（插入数据）
SOURCE /path/to/init_data.sql;

# 4. 验证
USE tibetan_medicine;
SHOW TABLES;
SELECT COUNT(*) FROM products;
```

### 方法2：导入文件

```bash
# Windows PowerShell
mysql -u root -p < E:\project\uniapp-lr\backend\database\schema.sql
mysql -u root -p < E:\project\uniapp-lr\backend\database\init_data.sql

# macOS/Linux
mysql -u root -p < /path/to/schema.sql
mysql -u root -p < /path/to/init_data.sql
```

### 方法3：使用GUI工具

#### MySQL Workbench

```
1. 打开MySQL Workbench
2. 连接到数据库
3. File -> Run SQL Script
4. 选择 schema.sql 执行
5. 选择 init_data.sql 执行
```

#### DBeaver

```
1. 打开DBeaver
2. 连接到MySQL
3. SQL Editor -> Open SQL Script
4. 执行 schema.sql
5. 执行 init_data.sql
```

---

## 📋 表结构详解

### 核心业务表

#### 1. users - 用户表

```sql
字段：
- id: 用户ID (主键)
- openid: 微信openid (唯一)
- nickname: 昵称
- avatar: 头像URL
- phone: 手机号
- create_time: 创建时间
- status: 状态 (1-正常, 0-禁用)
```

#### 2. products - 商品表

```sql
字段：
- id: 商品ID (主键)
- category_id: 分类ID
- name: 商品名称
- price: 价格
- stock: 库存
- sales: 销量
- image: 主图
- images: 商品图片JSON数组
- detail: 商品详情
- specs: 规格JSON
- status: 状态 (1-上架, 0-下架)
```

#### 3. orders - 订单表

```sql
字段：
- id: 订单ID (主键)
- order_no: 订单号 (唯一)
- user_id: 用户ID
- status: 订单状态
- total_amount: 订单总金额
- receiver_name: 收货人
- receiver_address: 收货地址
- pay_time: 支付时间
- create_time: 创建时间
```

---

## 🔧 维护操作

### 重置数据库

```sql
-- 删除数据库（谨慎操作）
DROP DATABASE IF EXISTS tibetan_medicine;

-- 重新创建
SOURCE schema.sql;
SOURCE init_data.sql;
```

### 备份数据库

```bash
# 备份整个数据库
mysqldump -u root -p tibetan_medicine > backup_$(date +%Y%m%d).sql

# 只备份结构
mysqldump -u root -p --no-data tibetan_medicine > structure.sql

# 只备份数据
mysqldump -u root -p --no-create-info tibetan_medicine > data.sql
```

### 恢复数据库

```bash
# 恢复数据库
mysql -u root -p tibetan_medicine < backup_20251202.sql
```

---

## 📊 数据统计

### 查询表记录数

```sql
-- 查看所有表的记录数
SELECT 
  TABLE_NAME,
  TABLE_ROWS
FROM 
  information_schema.TABLES
WHERE 
  TABLE_SCHEMA = 'tibetan_medicine'
ORDER BY 
  TABLE_ROWS DESC;
```

### 查看表大小

```sql
-- 查看表占用空间
SELECT 
  TABLE_NAME,
  CONCAT(ROUND(DATA_LENGTH/1024/1024,2),'MB') AS data_size,
  CONCAT(ROUND(INDEX_LENGTH/1024/1024,2),'MB') AS index_size
FROM 
  information_schema.TABLES
WHERE 
  TABLE_SCHEMA = 'tibetan_medicine';
```

---

## ⚠️ 注意事项

### 字符编码

- 数据库使用 `utf8mb4` 编码
- 支持emoji表情和特殊字符
- 连接字符串需要指定编码：`?characterEncoding=utf8`

### 时区设置

- 时间字段存储使用服务器时区
- 连接字符串指定时区：`?serverTimezone=Asia/Shanghai`

### 外键约束

- 当前版本未使用外键
- 采用应用层控制数据一致性
- 便于分库分表扩展

### 索引优化

- 已为常用查询字段创建索引
- 包括：用户ID、商品ID、订单号、状态、时间等
- 定期执行 `ANALYZE TABLE` 更新统计信息

---

## 🔍 常用查询

### 查看商品列表

```sql
SELECT id, name, price, stock, sales 
FROM products 
WHERE status = 1 
ORDER BY sales DESC 
LIMIT 10;
```

### 查看用户订单

```sql
SELECT 
  o.order_no,
  o.status,
  o.total_amount,
  o.create_time,
  u.nickname
FROM orders o
LEFT JOIN users u ON o.user_id = u.id
ORDER BY o.create_time DESC
LIMIT 20;
```

### 查看商品销量排行

```sql
SELECT 
  p.name,
  p.sales,
  p.price,
  c.name as category_name
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
WHERE p.status = 1
ORDER BY p.sales DESC
LIMIT 10;
```

---

## 📚 相关文档

- [快速开始-后端开发](../../docs/快速开始-后端开发.md)
- [后端实施方案](../../docs/后端实施方案.md)
- [API对接实施指南](../../docs/API对接实施指南.md)

---

**最后更新**：2025-12-02  
**版本**：v1.0
