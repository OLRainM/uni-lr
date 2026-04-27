# 前端小程序 README

> uni-app + Vue 3 Composition API 实现的藏药商城小程序客户端

---

## 技术栈

| 技术 | 说明 |
|---|---|
| uni-app | 跨端小程序框架 |
| Vue 3 | Composition API + `<script setup>` |
| uni-ui | 组件库 |
| SCSS | 样式预处理 |
| Vite | 构建工具 |

---

## 目录结构

```
frontend/
├── pages/                  # 页面文件
│   ├── index/index.vue     # 首页（TabBar）
│   ├── product/
│   │   ├── list.vue        # 商品列表（TabBar）
│   │   └── detail.vue      # 商品详情
│   ├── cart/cart.vue       # 购物车（TabBar）
│   ├── order/
│   │   ├── checkout.vue    # 结算页
│   │   ├── list.vue        # 订单列表
│   │   └── detail.vue      # 订单详情
│   ├── user/
│   │   ├── user.vue        # 个人中心（TabBar）
│   │   ├── address.vue     # 收货地址列表
│   │   └── address-edit.vue# 编辑/新增地址
│   ├── knowledge/detail.vue# 藏药知识详情
│   └── coupon/list.vue     # 优惠券列表
├── api/
│   └── index.js            # 所有后端接口封装
├── utils/
│   └── request.js          # HTTP 请求封装（自动带 token）
├── static/
│   ├── images/             # 本地商品图片
│   │   ├── product1.jpg    # 冬虫夏草
│   │   ├── product2.jpg    # 红虫草礼盒
│   │   ├── product3.jpg    # 藏红花
│   │   ├── product4.webp   # 雪莲
│   │   └── avatar-default.jpg # 默认头像
│   └── tabbar/             # TabBar 图标
├── App.vue                 # 应用配置
├── main.js                 # 入口文件
├── pages.json              # 页面路由配置
├── manifest.json           # 小程序配置（AppID 等）
├── uni.scss                # 全局样式变量
└── package.json
```

---

## 快速启动

```bash
# 安装依赖
npm install

# 开发模式（微信小程序）
npm run dev:mp-weixin

# 构建生产版本
npm run build:mp-weixin
```

用**微信开发者工具**导入 `frontend/dist/dev/mp-weixin` 目录进行调试。

---

## API 接口列表

所有接口封装在 `api/index.js`，基础 URL 配置在 `utils/request.js`（默认 `http://localhost:8080/api`）。

### 用户相关
| 方法 | 接口 | 说明 |
|---|---|---|
| `login(data)` | `POST /user/login` | 微信登录 |
| `testLogin()` | `POST /user/test-login` | 测试登录（开发用） |
| `getUserInfo()` | `GET /user/info` | 获取用户信息 |
| `updateUserInfo(data)` | `POST /user/update` | 更新用户信息 |

### 商品相关
| 方法 | 接口 | 说明 |
|---|---|---|
| `getProductList(params)` | `GET /product/list` | 商品列表（支持分页/筛选） |
| `getProductDetail(id)` | `GET /product/detail/{id}` | 商品详情 |
| `getProductCategories()` | `GET /product/categories` | 商品分类 |
| `getRecommendProducts()` | `GET /product/recommend` | 推荐商品 |

### 订单相关
| 方法 | 接口 | 说明 |
|---|---|---|
| `createOrder(data)` | `POST /order/create` | 创建订单 |
| `getOrderList(params)` | `GET /order/list` | 订单列表 |
| `getOrderDetail(id)` | `GET /order/detail/{id}` | 订单详情 |
| `payOrder(id)` | `POST /order/pay/{id}` | 支付订单（0→1） |
| `cancelOrder(id)` | `POST /order/cancel/{id}` | 取消订单 |
| `confirmOrder(id)` | `POST /order/confirm/{id}` | 确认收货 |
| `deleteOrder(id)` | `POST /order/delete/{id}` | 删除订单 |

### 地址相关
| 方法 | 接口 | 说明 |
|---|---|---|
| `getAddressList()` | `GET /address/list` | 地址列表 |
| `addAddress(data)` | `POST /address/add` | 新增地址 |
| `updateAddress(data)` | `POST /address/update` | 更新地址 |
| `deleteAddress(id)` | `POST /address/delete/{id}` | 删除地址 |
| `setDefaultAddress(id)` | `POST /address/setDefault/{id}` | 设为默认 |

---

## 购物车说明

购物车数据**完全存储在 localStorage**（key: `cartList`），不依赖后端接口。

数据格式：
```json
[
  {
    "id": 1,
    "name": "冬虫夏草",
    "price": 299,
    "image": "/static/images/product1.jpg",
    "quantity": 2,
    "selected": true
  }
]
```

---

## 图片本地化说明

数据库中部分商品图片为外部链接（已失效），前端通过 `getLocalImage()` 函数映射到本地图片：

| 商品名关键词 | 本地图片 |
|---|---|
| 冬虫夏草 | `static/images/product1.jpg` |
| 红虫草 / 礼盒 | `static/images/product2.jpg` |
| 藏红花 | `static/images/product3.jpg` |
| 雪莲 | `static/images/product4.webp` |
| 其他 | 按序号轮换以上4张 |

该函数在 `list.vue`、`detail.vue`、`cart.vue`、`order/list.vue`、`order/detail.vue`、`index/index.vue` 中均已实现。

---

## 鉴权说明

- 登录后 token 存储在 `uni.storage`（key: `token`）
- `utils/request.js` 自动在每个请求头中附加 `Authorization: Bearer {token}`
- 未登录时访问需要鉴权的页面会弹出提示并跳转到登录页

---

## 主题色

主色调为藏红色系，定义在 `uni.scss`：

```scss
$primary-color: #8B4513;   // 主题色（藏红棕）
$secondary-color: #D4AF37; // 辅助色（金色）
```

