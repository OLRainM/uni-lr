# API接口清单

> 小程序端完整的API接口需求列表

---

## 📋 目录

- [1. 用户模块](#1-用户模块)
- [2. 商品模块](#2-商品模块)
- [3. 购物车模块](#3-购物车模块)
- [4. 订单模块](#4-订单模块)
- [5. 地址模块](#5-地址模块)
- [6. 优惠券模块](#6-优惠券模块)
- [7. 支付模块](#7-支付模块)
- [8. 知识库模块](#8-知识库模块)
- [9. 其他模块](#9-其他模块)

---

## 1. 用户模块

### 1.1 用户登录

```
接口：POST /api/user/login
说明：微信小程序登录
请求：
{
  "code": "微信登录code",
  "userInfo": {
    "nickName": "用户昵称",
    "avatar": "头像URL"
  }
}
响应：
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "jwt_token",
    "userInfo": {
      "id": 1,
      "openid": "xxx",
      "nickname": "用户昵称",
      "avatar": "头像URL",
      "phone": "手机号"
    }
  }
}
```

### 1.2 获取用户信息

```
接口：GET /api/user/info
说明：获取当前登录用户信息
请求：无（需要token）
响应：
{
  "code": 200,
  "data": {
    "id": 1,
    "nickname": "用户昵称",
    "avatar": "头像URL",
    "phone": "13800138000",
    "vipLevel": "普通会员"
  }
}
```

### 1.3 更新用户信息

```
接口：POST /api/user/update
说明：更新用户信息
请求：
{
  "nickname": "新昵称",
  "phone": "新手机号"
}
响应：
{
  "code": 200,
  "message": "更新成功"
}
```

---

## 2. 商品模块

### 2.1 获取商品列表

```
接口：GET /api/product/list
说明：获取商品列表（支持筛选）
请求参数：
{
  "page": 1,           # 页码
  "pageSize": 10,      # 每页数量
  "categoryId": 1,     # 分类ID（可选）
  "keyword": "搜索词", # 搜索关键词（可选）
  "sortBy": "sales",   # 排序方式：sales-销量，price-价格
  "sortOrder": "desc", # 排序方向：asc-升序，desc-降序
  "isNew": true,       # 是否新品（可选）
  "isHot": true        # 是否热销（可选）
}
响应：
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "name": "商品名称",
        "subtitle": "副标题",
        "price": 199.00,
        "originalPrice": 299.00,
        "image": "主图URL",
        "sales": 100,
        "stock": 50,
        "isNew": false,
        "isHot": true
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 10
  }
}
```

### 2.2 获取商品详情

```
接口：GET /api/product/detail/:id
说明：获取商品详情
请求：商品ID作为路径参数
响应：
{
  "code": 200,
  "data": {
    "id": 1,
    "name": "商品名称",
    "subtitle": "副标题",
    "price": 199.00,
    "originalPrice": 299.00,
    "stock": 50,
    "sales": 100,
    "image": "主图URL",
    "images": ["图片1", "图片2", "图片3"],
    "detail": "商品详情HTML",
    "specs": [
      {
        "id": 1,
        "name": "规格1",
        "stock": 20
      }
    ],
    "params": [
      {
        "label": "产地",
        "value": "西藏"
      },
      {
        "label": "保质期",
        "value": "24个月"
      }
    ],
    "origin": "西藏那曲",
    "shelfLife": "24个月"
  }
}
```

### 2.3 获取商品分类

```
接口：GET /api/product/categories
说明：获取所有商品分类
请求：无
响应：
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "冬虫夏草",
      "icon": "图标URL",
      "sort": 1
    },
    {
      "id": 2,
      "name": "藏红花",
      "icon": "图标URL",
      "sort": 2
    }
  ]
}
```

### 2.4 获取推荐商品

```
接口：GET /api/product/recommend
说明：获取推荐商品（首页热销推荐）
请求参数：
{
  "limit": 10  # 数量限制，默认10
}
响应：
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "商品名称",
      "desc": "商品描述",
      "price": 199.00,
      "image": "图片URL",
      "sales": 100
    }
  ]
}
```

---

## 3. 购物车模块

### 3.1 获取购物车列表

```
接口：GET /api/cart/list
说明：获取当前用户的购物车
请求：无（需要token）
响应：
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "productId": 1,
      "name": "商品名称",
      "spec": "规格名称",
      "price": 199.00,
      "quantity": 2,
      "stock": 50,
      "image": "图片URL",
      "checked": true
    }
  ]
}
```

### 3.2 添加到购物车

```
接口：POST /api/cart/add
说明：添加商品到购物车
请求：
{
  "productId": 1,
  "spec": "规格名称",
  "quantity": 1
}
响应：
{
  "code": 200,
  "message": "添加成功"
}
```

### 3.3 更新购物车

```
接口：POST /api/cart/update
说明：更新购物车商品（数量、选中状态）
请求：
{
  "id": 1,          # 购物车项ID
  "quantity": 3,    # 新数量（可选）
  "checked": true   # 选中状态（可选）
}
响应：
{
  "code": 200,
  "message": "更新成功"
}
```

### 3.4 删除购物车商品

```
接口：POST /api/cart/delete
说明：删除购物车商品（支持批量）
请求：
{
  "ids": [1, 2, 3]  # 购物车项ID数组
}
响应：
{
  "code": 200,
  "message": "删除成功"
}
```

### 3.5 清空购物车

```
接口：POST /api/cart/clear
说明：清空购物车
请求：无
响应：
{
  "code": 200,
  "message": "清空成功"
}
```

---

## 4. 订单模块

### 4.1 创建订单

```
接口：POST /api/order/create
说明：创建订单
请求：
{
  "goods": [
    {
      "productId": 1,
      "spec": "规格名称",
      "quantity": 2,
      "price": 199.00
    }
  ],
  "addressId": 1,           # 收货地址ID
  "couponId": 1,            # 优惠券ID（可选）
  "remark": "订单备注"      # 备注（可选）
}
响应：
{
  "code": 200,
  "data": {
    "orderId": 1,
    "orderNo": "202512020001",
    "totalAmount": 398.00,
    "payAmount": 388.00      # 实付金额
  }
}
```

### 4.2 获取订单列表

```
接口：GET /api/order/list
说明：获取用户订单列表
请求参数：
{
  "page": 1,
  "pageSize": 10,
  "status": "unpaid"  # 订单状态（可选）：
                      # unpaid-待付款
                      # unshipped-待发货
                      # shipped-待收货
                      # completed-已完成
                      # cancelled-已取消
}
响应：
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "orderNo": "202512020001",
        "status": "unpaid",
        "statusText": "待付款",
        "totalAmount": 398.00,
        "goods": [
          {
            "productId": 1,
            "name": "商品名称",
            "spec": "规格",
            "price": 199.00,
            "quantity": 2,
            "image": "图片URL"
          }
        ],
        "createTime": "2025-12-02 20:00:00"
      }
    ],
    "total": 50
  }
}
```

### 4.3 获取订单详情

```
接口：GET /api/order/detail/:id
说明：获取订单详情
请求：订单ID作为路径参数
响应：
{
  "code": 200,
  "data": {
    "id": 1,
    "orderNo": "202512020001",
    "status": "unpaid",
    "statusText": "待付款",
    "totalAmount": 398.00,
    "goodsAmount": 398.00,
    "freight": 10.00,
    "discount": 20.00,
    "payAmount": 388.00,
    "goods": [
      {
        "productId": 1,
        "name": "商品名称",
        "spec": "规格",
        "price": 199.00,
        "quantity": 2,
        "image": "图片URL"
      }
    ],
    "address": {
      "name": "收货人",
      "phone": "13800138000",
      "province": "北京市",
      "city": "北京市",
      "district": "朝阳区",
      "detail": "详细地址"
    },
    "remark": "订单备注",
    "createTime": "2025-12-02 20:00:00",
    "payTime": null,
    "shipTime": null,
    "shipNo": null,
    "shipCompany": null,
    "completeTime": null
  }
}
```

### 4.4 取消订单

```
接口：POST /api/order/cancel/:id
说明：取消订单
请求：
{
  "reason": "取消原因"  # 可选
}
响应：
{
  "code": 200,
  "message": "取消成功"
}
```

### 4.5 确认收货

```
接口：POST /api/order/confirm/:id
说明：确认收货
请求：订单ID作为路径参数
响应：
{
  "code": 200,
  "message": "确认成功"
}
```

### 4.6 删除订单

```
接口：POST /api/order/delete/:id
说明：删除订单（仅已完成或已取消的订单）
请求：订单ID作为路径参数
响应：
{
  "code": 200,
  "message": "删除成功"
}
```

### 4.7 获取订单统计

```
接口：GET /api/order/count
说明：获取各状态订单数量
请求：无
响应：
{
  "code": 200,
  "data": {
    "unpaid": 2,      # 待付款
    "unshipped": 3,   # 待发货
    "shipped": 1,     # 待收货
    "completed": 10   # 已完成
  }
}
```

---

## 5. 地址模块

### 5.1 获取地址列表

```
接口：GET /api/address/list
说明：获取用户收货地址列表
请求：无（需要token）
响应：
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "张三",
      "phone": "13800138000",
      "province": "北京市",
      "city": "北京市",
      "district": "朝阳区",
      "detail": "xxx街道xxx号",
      "isDefault": true
    }
  ]
}
```

### 5.2 添加地址

```
接口：POST /api/address/add
说明：添加收货地址
请求：
{
  "name": "张三",
  "phone": "13800138000",
  "province": "北京市",
  "city": "北京市",
  "district": "朝阳区",
  "detail": "xxx街道xxx号",
  "isDefault": false
}
响应：
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": 1
  }
}
```

### 5.3 更新地址

```
接口：POST /api/address/update
说明：更新收货地址
请求：
{
  "id": 1,
  "name": "张三",
  "phone": "13800138000",
  "province": "北京市",
  "city": "北京市",
  "district": "朝阳区",
  "detail": "xxx街道xxx号",
  "isDefault": false
}
响应：
{
  "code": 200,
  "message": "更新成功"
}
```

### 5.4 删除地址

```
接口：POST /api/address/delete/:id
说明：删除收货地址
请求：地址ID作为路径参数
响应：
{
  "code": 200,
  "message": "删除成功"
}
```

### 5.5 设置默认地址

```
接口：POST /api/address/setDefault/:id
说明：设置默认收货地址
请求：地址ID作为路径参数
响应：
{
  "code": 200,
  "message": "设置成功"
}
```

---

## 6. 优惠券模块

### 6.1 获取用户优惠券列表

```
接口：GET /api/coupon/myList
说明：获取用户的优惠券
请求参数：
{
  "status": "unused"  # 状态：unused-未使用，used-已使用，expired-已过期
}
响应：
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "couponId": 1,
      "name": "满100减10",
      "type": "discount",
      "discount": 10.00,
      "minAmount": 100.00,
      "desc": "满100元可用",
      "status": "unused",
      "expireDate": "2025-12-31"
    }
  ]
}
```

### 6.2 领取优惠券

```
接口：POST /api/coupon/receive
说明：领取优惠券
请求：
{
  "couponId": 1
}
响应：
{
  "code": 200,
  "message": "领取成功"
}
```

### 6.3 获取可用优惠券

```
接口：GET /api/coupon/available
说明：获取可用的优惠券（未领取的）
请求：无
响应：
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "新人专享券",
      "type": "discount",
      "discount": 50.00,
      "minAmount": 500.00,
      "desc": "满500元可用",
      "total": 1000,
      "used": 500,
      "startTime": "2025-01-01",
      "endTime": "2025-12-31"
    }
  ]
}
```

### 6.4 获取订单可用优惠券

```
接口：POST /api/coupon/orderAvailable
说明：获取订单可用的优惠券
请求：
{
  "totalAmount": 500.00
}
响应：
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "满100减10",
      "discount": 10.00,
      "minAmount": 100.00,
      "desc": "满100元可用"
    }
  ]
}
```

---

## 7. 支付模块

### 7.1 发起支付

```
接口：POST /api/payment/request
说明：发起微信支付
请求：
{
  "orderId": 1,
  "payMethod": "wxpay"
}
响应：
{
  "code": 200,
  "data": {
    "timeStamp": "时间戳",
    "nonceStr": "随机字符串",
    "package": "prepay_id=xxx",
    "signType": "RSA",
    "paySign": "签名"
  }
}
```

### 7.2 支付回调通知

```
接口：POST /api/payment/notify
说明：微信支付回调（微信服务器调用）
请求：微信支付回调数据
响应：成功或失败
```

### 7.3 查询支付状态

```
接口：GET /api/payment/status/:orderNo
说明：查询订单支付状态
请求：订单号作为路径参数
响应：
{
  "code": 200,
  "data": {
    "orderNo": "202512020001",
    "status": "paid",      # paid-已支付，unpaid-未支付
    "payTime": "2025-12-02 20:05:00"
  }
}
```

---

## 8. 知识库模块

### 8.1 获取知识库列表

```
接口：GET /api/knowledge/list
说明：获取藏药知识列表
请求参数：
{
  "page": 1,
  "pageSize": 10,
  "category": "养生保健"  # 分类（可选）
}
响应：
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "title": "冬虫夏草的功效与作用",
        "subtitle": "了解冬虫夏草的药用价值",
        "cover": "封面图URL",
        "category": "药材知识",
        "views": 1000,
        "createTime": "2025-12-01"
      }
    ],
    "total": 50
  }
}
```

### 8.2 获取知识库详情

```
接口：GET /api/knowledge/detail/:id
说明：获取知识库文章详情
请求：文章ID作为路径参数
响应：
{
  "code": 200,
  "data": {
    "id": 1,
    "title": "冬虫夏草的功效与作用",
    "subtitle": "了解冬虫夏草的药用价值",
    "cover": "封面图URL",
    "content": "文章内容HTML",
    "category": "药材知识",
    "views": 1001,
    "createTime": "2025-12-01"
  }
}
```

---

## 9. 其他模块

### 9.1 获取轮播图

```
接口：GET /api/banner/list
说明：获取首页轮播图
请求：无
响应：
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "title": "轮播图标题",
      "image": "图片URL",
      "linkType": "product",  # product-商品，url-外链
      "linkValue": "1"
    }
  ]
}
```

### 9.2 文件上传

```
接口：POST /api/upload
说明：上传文件（图片）
请求：multipart/form-data
{
  "file": 文件
}
响应：
{
  "code": 200,
  "data": {
    "url": "https://xxx.com/xxx.jpg"
  }
}
```

### 9.3 健康检查

```
接口：GET /api/health
说明：服务健康检查
请求：无
响应：
{
  "code": 200,
  "data": {
    "status": "UP",
    "time": "2025-12-02T20:00:00"
  }
}
```

---

## 📊 接口统计

| 模块 | 接口数量 |
|------|----------|
| 用户模块 | 3个 |
| 商品模块 | 4个 |
| 购物车模块 | 5个 |
| 订单模块 | 7个 |
| 地址模块 | 5个 |
| 优惠券模块 | 4个 |
| 支付模块 | 3个 |
| 知识库模块 | 2个 |
| 其他模块 | 3个 |
| **总计** | **36个** |

---

## 🔐 认证说明

### JWT Token

除以下接口外，其他接口都需要在Header中携带Token：

```
无需认证的接口：
- POST /api/user/login
- GET /api/product/list
- GET /api/product/detail/:id
- GET /api/product/categories
- GET /api/knowledge/list
- GET /api/knowledge/detail/:id
- GET /api/banner/list
- GET /api/health
```

**Token格式**：

```
Authorization: Bearer {token}
```

---

## 📝 通用响应格式

### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": {}  // 业务数据
}
```

### 错误响应

```json
{
  "code": 400/401/403/404/500,
  "message": "错误信息",
  "data": null
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录或token过期 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

---

## 🚀 开发优先级

### 第一阶段（核心功能）

**优先级：⭐⭐⭐⭐⭐**

1. 用户登录接口
2. 商品列表和详情
3. 购物车CRUD
4. 订单创建和列表
5. 地址管理

### 第二阶段（完善功能）

**优先级：⭐⭐⭐⭐**

6. 优惠券功能
7. 订单状态操作
8. 支付接口（mock）
9. 知识库

### 第三阶段（扩展功能）

**优先级：⭐⭐⭐**

10. 轮播图管理
11. 文件上传
12. 数据统计

---

## 📚 相关文档

- [后端实施方案](./后端实施方案.md) - 完整的开发部署计划
- [API对接实施指南](./API对接实施指南.md) - 前端改造步骤
- [快速开始-后端开发](./快速开始-后端开发.md) - 快速启动指南

---

**文档创建**：2025-12-02  
**最后更新**：2025-12-02  
**版本**：v1.0  
**接口总数**：36个
