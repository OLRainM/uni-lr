# 藏药商城小程序

> 基于 uni-app + Spring Boot 3 的藏药电商完整解决方案，包含小程序客户端、Spring Boot 后端 API 和纯 HTML 商家管理后台。

**📱 小程序前端** `frontend/` | **⚙️ 后端服务** `backend/` | **🖥️ 商家后台** `adminWeb/`

---

## 📁 项目结构

```
uniapp-lr/
├── frontend/          # 小程序前端（uni-app + Vue 3）
├── backend/           # 后端 API（Spring Boot 3 + MyBatis-Plus）
├── adminWeb/          # 商家管理后台（纯 HTML/CSS/JS，随后端启动）
├── admin/             # ⚠️ 已废弃（旧 Vue+Element Plus 版，勿使用）
├── docs/              # 项目文档
│   └── archive/       # 旧文档归档
├── image/             # 原始图片素材
└── tools/             # 开发工具脚本
```

---

## 🚀 快速启动

### 前置条件

| 工具 | 版本要求 | 用途 |
|---|---|---|
| JDK | 17+ | 后端运行环境 |
| Maven | 3.8+ | 后端构建 |
| Node.js | 16+ | 前端构建 |
| 微信开发者工具 | 最新版 | 小程序调试 |
| MySQL | 8.0（已有远程库） | 数据库（已配置） |

### 第一步：启动后端

```bash
cd backend
mvn spring-boot:run
```

后端启动成功后：
- API 服务：`http://localhost:8080/api`
- 健康检测：`http://localhost:8080/api/health`
- **商家后台自动可访问**：`http://localhost:8080/api/admin-web/index.html`

### 第二步：启动前端小程序

```bash
cd frontend
npm install
npm run dev:mp-weixin
```

用**微信开发者工具**打开 `frontend/dist/dev/mp-weixin` 目录即可。

---

## 🖥️ 商家管理后台

后端启动后，直接访问：

```
http://localhost:8080/api/admin-web/index.html
```

**登录方式**：输入管理秘钥（在 `backend/src/main/resources/application.yml` 中配置的 `admin.secret-key`）

**功能模块**：
- 📊 数据概览：订单量、销售额、用户数、商品数统计
- 🛍️ 商品管理：上架/下架、新增、编辑
- 📋 订单管理：查看订单、发货（填写物流单号）、状态管理
- 👥 用户管理：用户列表、封禁/解封

---

## 🛍️ 小程序功能

| 页面 | 路径 | 功能 |
|---|---|---|
| 首页 | `pages/index/index` | 轮播图、热销推荐、藏药知识 |
| 商品列表 | `pages/product/list` | 分类筛选、搜索、排序 |
| 商品详情 | `pages/product/detail` | 规格选择、加入购物车 |
| 购物车 | `pages/cart/cart` | 商品管理、结算 |
| 结算 | `pages/order/checkout` | 地址选择、提交订单 |
| 订单列表 | `pages/order/list` | 支付、取消、确认收货 |
| 订单详情 | `pages/order/detail` | 物流信息、费用明细 |
| 个人中心 | `pages/user/user` | 用户信息、订单入口 |
| 收货地址 | `pages/user/address` | 地址增删改查 |
| 藏药知识 | `pages/knowledge/detail` | 科普文章详情 |

---

## ⚙️ 技术栈

| 层 | 技术 |
|---|---|
| **小程序前端** | uni-app · Vue 3 Composition API · `<script setup>` · SCSS |
| **后端** | Spring Boot 3.x · MyBatis-Plus · Druid 连接池 · JWT 鉴权 |
| **商家后台** | 原生 HTML · CSS · JavaScript（无构建工具） |
| **数据库** | MySQL 8.0（远程：`115.190.125.177:3306/tibetan_medicine`） |

---

## 📌 关键配置

| 配置项 | 值 |
|---|---|
| 后端端口 | `8080` |
| 接口前缀 | `/api`（完整：`http://localhost:8080/api`） |
| 数据库 | `115.190.125.177:3306/tibetan_medicine` |
| Admin 秘钥 | 见 `backend/src/main/resources/application.yml` → `admin.secret-key` |
| 购物车存储 | localStorage（`cartList` key），不依赖后端 |

---

## 📋 订单/商品状态说明

**订单状态**：`0`=待付款 · `1`=待发货 · `2`=待收货 · `3`=已完成 · `4`=已取消

**商品状态**：`0`=下架 · `1`=上架

**用户状态**：`0`=正常 · `1`=禁用

---

## 📚 文档索引

| 文档 | 说明 |
|---|---|
| [frontend/README.md](./frontend/README.md) | 前端详细说明 |
| [backend/README.md](./backend/README.md) | 后端详细说明 |
| [docs/PRD.md](./docs/PRD.md) | 产品需求文档 |
| [docs/TECH.md](./docs/TECH.md) | 技术架构文档 |
| [docs/archive/旧文档归档.md](./docs/archive/旧文档归档.md) | 历史文档归档 |

---

*版本：v2.0 · 最后更新：2025年*

