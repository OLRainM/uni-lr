# 后端 API 服务 README

> Spring Boot 3.3.7 · MyBatis-Plus 3.5.9 · MySQL 8.0 · Druid 连接池

---

## 技术栈

| 技术 | 版本 | 说明 |
|---|---|---|
| Spring Boot | 3.3.7 | 主框架（Jakarta 命名空间） |
| MyBatis-Plus | 3.5.9 | ORM 框架，自动填充、逻辑删除 |
| Druid | 1.2.x | 数据库连接池 |
| MySQL | 8.0 | 关系数据库 |
| Java JWT (auth0) | 4.4.0 | 小程序用户鉴权 |
| Lombok | 1.18.x | 代码简化 |
| Hutool | 5.8.x | 工具类库 |

---

## 快速启动

### 环境要求

- JDK 17+
- Maven 3.8+
- 数据库已配置（远程 MySQL，见 `application.yml`）

### 启动命令

```bash
cd backend
mvn spring-boot:run
```

或在 IntelliJ IDEA 中直接运行 `TibetanMedicineApplication.java`。

### 验证启动

- 健康检测：`http://localhost:8080/api/health`
- 商家后台：`http://localhost:8080/api/admin-web/index.html`

---

## 配置说明（application.yml）

| 配置项 | 值 | 说明 |
|---|---|---|
| `server.port` | `8080` | 服务端口 |
| `server.servlet.context-path` | `/api` | 接口前缀 |
| `spring.datasource.url` | `115.190.125.177:3306/tibetan_medicine` | 数据库（远程） |
| `jwt.secret` | `tibetan-medicine-secret-key-2024` | JWT 签名密钥 |
| `jwt.expiration` | `604800000`（7天） | Token 有效期 |
| `admin.secret-key` | 见配置文件 | 商家后台登录秘钥 |
| `admin.web-path` | `../adminWeb` | adminWeb 静态资源路径 |

---

## 目录结构

```
backend/src/main/java/com/tibetan/medicine/
├── TibetanMedicineApplication.java   # 启动类
├── annotation/
│   └── SkipAuth.java                 # 跳过 JWT 鉴权注解
├── common/
│   ├── Result.java                   # 统一响应（code=0成功/-1失败）
│   └── PageResult.java               # 分页响应
├── config/
│   ├── CorsConfig.java               # 跨域配置
│   ├── MyBatisPlusConfig.java        # 分页插件
│   ├── MyMetaObjectHandler.java      # 自动填充（createTime/updateTime）
│   ├── WebMvcConfig.java             # 拦截器注册 + adminWeb 静态资源
│   └── RedisConfig.java              # Redis（当前已禁用）
├── controller/
│   ├── AdminController.java          # 商家后台接口（Admin-Key 鉴权）
│   ├── UserController.java           # 用户接口
│   ├── ProductController.java        # 商品接口
│   ├── OrderController.java          # 订单接口
│   ├── CartController.java           # 购物车接口
│   ├── AddressController.java        # 地址接口
│   ├── BannerController.java         # 轮播图接口
│   ├── KnowledgeController.java      # 藏药知识接口
│   └── CouponController.java         # 优惠券接口
├── interceptor/
│   ├── AuthInterceptor.java          # JWT 鉴权拦截器（小程序用户）
│   └── AdminAuthInterceptor.java     # Admin-Key 鉴权拦截器（商家后台）
├── entity/                           # 实体类（Order/Product/User 等）
├── dto/                              # 请求 DTO
├── vo/                               # 响应 VO
├── mapper/                           # MyBatis-Plus Mapper
├── service/                          # Service 接口
├── service/impl/                     # Service 实现
└── util/JwtUtil.java                 # JWT 工具类
```

---

## API 接口概览

### 小程序用户接口（JWT 鉴权）

完整接口前缀：`http://localhost:8080/api`

| 模块 | 接口示例 | 鉴权方式 |
|---|---|---|
| 用户 | `POST /user/login`、`GET /user/info` | 部分需要 JWT |
| 商品 | `GET /product/list`、`GET /product/detail/{id}` | 无需鉴权 |
| 购物车 | `GET /cart/list`、`POST /cart/add` | 需要 JWT |
| 订单 | `POST /order/create`、`POST /order/pay/{id}` | 需要 JWT |
| 地址 | `GET /address/list`、`POST /address/add` | 需要 JWT |
| 知识库 | `GET /knowledge/list` | 无需鉴权 |
| 轮播图 | `GET /banner/list` | 无需鉴权 |

### 商家后台接口（Admin-Key 鉴权）

所有接口以 `/admin/` 开头，请求头需携带 `Admin-Key: {秘钥}`。

| 接口 | 说明 |
|---|---|
| `POST /admin/login` | 管理员登录验证 |
| `GET /admin/dashboard` | 统计数据（订单数、销售额等） |
| `GET /admin/product/list` | 商品列表（含下架商品） |
| `POST /admin/product/add` | 新增商品 |
| `POST /admin/product/update` | 编辑商品 |
| `POST /admin/product/status/{id}` | 上架/下架 |
| `GET /admin/order/list` | 订单列表 |
| `GET /admin/order/detail/{id}` | 订单详情 |
| `POST /admin/order/ship/{id}` | 发货（填写物流单号） |
| `POST /admin/order/status/{id}` | 修改订单状态 |
| `GET /admin/user/list` | 用户列表 |
| `POST /admin/user/status/{id}` | 封禁/解封用户 |

---

## 统一响应格式

```json
{ "code": 0, "message": "success", "data": { ... } }   // 成功
{ "code": -1, "message": "错误信息" }                    // 失败
```

**分页响应**（`data` 字段为 `PageResult<T>`）：
```json
{
  "code": 0,
  "data": { "total": 100, "pageNum": 1, "pageSize": 10, "list": [...] }
}
```

---

## 鉴权体系

### 小程序用户（JWT）
- 登录后返回 JWT token，有效期 7 天
- 请求头：`Authorization: Bearer {token}`
- `AuthInterceptor` 负责校验，`@SkipAuth` 注解可跳过

### 商家后台（Admin-Key）
- 登录时验证秘钥（配置在 `application.yml` → `admin.secret-key`）
- 请求头：`Admin-Key: {秘钥}`
- `AdminAuthInterceptor` 负责校验，路径 `/admin/**` 均受保护

---

## 数据库说明

数据库连接已配置远程 MySQL（`115.190.125.177:3306/tibetan_medicine`），本地开发直接启动即可。

若需本地部署，执行：
```bash
# 建表
mysql -u root -p < backend/database/schema.sql
# 导入测试数据
mysql -u root -p tibetan_medicine < backend/database/init_data.sql
```

主要数据表：

| 表名 | 说明 |
|---|---|
| `tb_user` | 用户表 |
| `tb_product` | 商品表（status: 0=下架,1=上架） |
| `tb_order` | 订单表（status: 0~4） |
| `tb_order_item` | 订单商品明细 |
| `tb_address` | 收货地址 |
| `tb_banner` | 首页轮播图 |
| `tb_knowledge` | 藏药知识文章 |
| `tb_category` | 商品分类 |

---

## 打包部署

```bash
# 打包
mvn clean package -DskipTests

# 运行（adminWeb 随后端自动可访问）
java -jar target/tibetan-medicine-api-1.0.0.jar
```

