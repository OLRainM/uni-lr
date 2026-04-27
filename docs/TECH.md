# 技术架构文档（TECH）

> 藏药商城小程序 · 版本 v2.0 · 2025年

---

## 一、整体架构

```
┌─────────────────────────────────────────────────────────┐
│                    客户端层                               │
│  ┌──────────────────┐     ┌──────────────────────────┐  │
│  │  微信小程序        │     │  商家管理后台 (Browser)    │  │
│  │  uni-app + Vue3  │     │  纯 HTML/CSS/JS           │  │
│  └────────┬─────────┘     └────────────┬─────────────┘  │
└───────────┼──────────────────────────── ┼───────────────┘
            │ JWT (Authorization)          │ Admin-Key
            ▼                             ▼
┌─────────────────────────────────────────────────────────┐
│                  Spring Boot 后端                         │
│  ┌──────────────┐  ┌────────────────┐                   │
│  │ AuthInterceptor│  │AdminAuthInterceptor│              │
│  └──────┬───────┘  └───────┬────────┘                   │
│         │                  │                             │
│  ┌──────▼──────────────────▼────────┐                   │
│  │           Controller 层           │                   │
│  │  User / Product / Order / Admin  │                   │
│  └──────────────────┬───────────────┘                   │
│                     │                                    │
│  ┌──────────────────▼───────────────┐                   │
│  │           Service 层              │                   │
│  └──────────────────┬───────────────┘                   │
│                     │                                    │
│  ┌──────────────────▼───────────────┐                   │
│  │      MyBatis-Plus Mapper 层       │                   │
│  └──────────────────┬───────────────┘                   │
└─────────────────────┼───────────────────────────────────┘
                      │
              ┌───────▼────────┐
              │  MySQL 8.0      │
              │  (远程数据库)    │
              └────────────────┘
```

---

## 二、技术选型说明

### 2.1 前端（小程序）

| 技术 | 选型理由 |
|---|---|
| **uni-app** | 跨端框架，支持编译为微信小程序，生态完善 |
| **Vue 3 Composition API** | `<script setup>` 语法，代码组织清晰，响应式系统高效 |
| **localStorage** | 购物车数据存本地，不依赖登录状态，用户体验更流畅 |
| **本地图片映射** | 数据库图片字段存外链（已失效），通过 `getLocalImage()` 映射到本地图片，保证展示效果 |

### 2.2 后端

| 技术 | 选型理由 |
|---|---|
| **Spring Boot 3.x** | 主流 Java 框架，Jakarta 命名空间，社区生态成熟 |
| **MyBatis-Plus** | 简化 CRUD，提供分页插件、逻辑删除、自动填充 |
| **Druid** | 数据库连接池，监控能力强，性能稳定 |
| **JWT (auth0)** | 无状态鉴权，适合小程序 token 场景 |
| **双拦截器方案** | 用户端 JWT + 管理端 Admin-Key，两套鉴权独立互不干扰 |

### 2.3 商家后台

| 技术 | 选型理由 |
|---|---|
| **纯 HTML/CSS/JS** | 无构建步骤，直接作为 Spring Boot 静态资源服务，随后端启动 |
| **Admin-Key 秘钥** | 简单可靠的管理后台鉴权，避免复杂的账号体系 |

---

## 三、鉴权体系详解

### 3.1 小程序用户鉴权（JWT）

```
用户点击登录
    ↓
POST /user/test-login（测试）或 POST /user/login（微信 code）
    ↓
后端生成 JWT token（有效期7天）
    ↓
前端存入 uni.storage（key: token）
    ↓
后续请求：request.js 自动附加 Authorization: Bearer {token}
    ↓
AuthInterceptor 校验 JWT 有效性
    ↓
通过 → 解析出 userId，放入 ThreadLocal 供 Service 使用
```

**白名单接口**（无需 JWT）：
- `POST /user/login`、`POST /user/test-login`
- `GET /product/**`、`GET /banner/**`、`GET /knowledge/**`
- `GET /health`

### 3.2 商家后台鉴权（Admin-Key）

```
管理员输入秘钥
    ↓
POST /admin/login { secretKey: "TIBETAN-ADMINLR-2026-SECRET" }
    ↓
后端对比 application.yml 中的 admin.secret-key
    ↓
匹配成功 → 返回 { token: secretKey }
    ↓
前端存入 localStorage（key: adminToken）
    ↓
后续所有 /admin/** 请求携带 Admin-Key: {秘钥}
    ↓
AdminAuthInterceptor 校验
```

---

## 四、核心数据模型

### 4.1 商品（Product）

```
tb_product
├── id            BIGINT PK
├── name          VARCHAR(100)    商品名称
├── price         DECIMAL(10,2)   价格
├── stock         INT             库存
├── image         VARCHAR(500)    图片URL（前端做本地映射）
├── description   TEXT            描述
├── category_id   BIGINT          分类ID
├── sales         INT             销量
├── status        TINYINT         0=下架, 1=上架
├── create_time   DATETIME        自动填充
├── update_time   DATETIME        自动填充
└── deleted       TINYINT         0=正常, 1=逻辑删除
```

### 4.2 订单（Order）

```
tb_order
├── id            BIGINT PK
├── order_no      VARCHAR(50)     订单编号
├── user_id       BIGINT          用户ID
├── address_id    BIGINT          地址ID
├── total_amount  DECIMAL(10,2)   总金额
├── status        TINYINT         0~4（见PRD）
├── pay_method    VARCHAR(20)     支付方式
├── pay_time      DATETIME        支付时间
├── ship_company  VARCHAR(50)     快递公司
├── ship_no       VARCHAR(100)    快递单号
├── remark        VARCHAR(500)    备注
├── create_time   DATETIME        自动填充（MyMetaObjectHandler）
├── update_time   DATETIME        自动填充
└── deleted       TINYINT
```

### 4.3 订单商品明细（OrderItem）

```
tb_order_item
├── id            BIGINT PK
├── order_id      BIGINT
├── product_id    BIGINT
├── product_name  VARCHAR(100)
├── product_image VARCHAR(500)
├── price         DECIMAL(10,2)
└── quantity      INT
```

---

## 五、关键实现细节

### 5.1 自动时间填充（MyMetaObjectHandler）

`config/MyMetaObjectHandler.java` 实现 `MetaObjectHandler` 接口：
- INSERT 操作：自动填充 `createTime`、`updateTime`
- UPDATE 操作：自动填充 `updateTime`

配合 `Order.java` 实体上的 `@TableField(fill = FieldFill.INSERT)` 注解生效。

### 5.2 LocalDateTime 序列化

`Order.java` 中三个时间字段均加了：
```java
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
```
确保 JSON 响应中时间格式为 `"2025-12-09 20:33:11"` 而非 ISO 格式。

### 5.3 adminWeb 静态资源服务

`WebMvcConfig.java` 将 `../adminWeb` 目录映射为 `/admin-web/**` 静态路径：

```java
registry.addResourceHandler("/admin-web/**")
        .addResourceLocations("file:" + adminWebPath + "/");
```

后端启动后 `http://localhost:8080/api/admin-web/index.html` 直接访问商家后台。

### 5.4 购物车本地化

购物车数据结构：
```js
// localStorage key: "cartList"
[{ id, name, price, image, quantity, selected, spec }]
```

加入购物车时检查是否已存在同商品（合并数量），不调用后端接口。

### 5.5 图片本地映射

`getLocalImage(imageUrl, productName, index)` 函数：
1. 若图片 URL 不含 `yzcdn.cn`（非失效外链），直接返回原 URL
2. 按商品名关键词匹配本地图片
3. 兜底：按商品序号 `index % 4` 轮换 4 张本地图片

---

## 六、接口路径规范

| 类型 | 路径前缀 | 鉴权方式 | 示例 |
|---|---|---|---|
| 公开接口 | `/product/`、`/banner/`、`/knowledge/` | 无 | `GET /api/product/list` |
| 用户接口 | `/user/`、`/order/`、`/cart/`、`/address/` | JWT | `POST /api/order/create` |
| 管理接口 | `/admin/` | Admin-Key | `GET /api/admin/order/list` |
| 静态资源 | `/admin-web/` | 无 | `/api/admin-web/index.html` |

---

## 七、环境配置速查

| 环境变量 / 配置 | 位置 | 说明 |
|---|---|---|
| 数据库连接 | `backend/src/main/resources/application.yml` | MySQL URL、用户名密码 |
| JWT 密钥 | 同上 `jwt.secret` | 生产环境必须修改 |
| Admin 秘钥 | 同上 `admin.secret-key` | 用于商家后台登录 |
| 前端 API 地址 | `frontend/utils/request.js` | `BASE_URL`，开发时为 localhost:8080/api |
| adminWeb API 地址 | `adminWeb/api.js` | `BASE_URL`，同上 |

---

## 八、开发环境要求

| 工具 | 版本 | 用途 |
|---|---|---|
| JDK | 17+ | 后端运行 |
| Maven | 3.8+ | 后端构建 |
| Node.js | 16+ | 前端构建 |
| 微信开发者工具 | 最新版 | 小程序调试 |
| IntelliJ IDEA | 推荐 | 后端开发 |
| VS Code | 推荐 | 前端开发 |

