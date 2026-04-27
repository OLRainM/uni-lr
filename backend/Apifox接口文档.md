# 藏药小程序API接口文档（Apifox版本）

> 适用于Apifox测试工具的完整API接口文档

**服务地址**：`http://localhost:8080/api`  
**接口总数**：36个  
**文档版本**：v1.0

---

## 📋 目录

- [通用说明](#通用说明)
- [1. 用户模块（3个接口）](#1-用户模块)
- [2. 商品模块（4个接口）](#2-商品模块)
- [3. 购物车模块（5个接口）](#3-购物车模块)
- [4. 订单模块（7个接口）](#4-订单模块)
- [5. 地址模块（5个接口）](#5-地址模块)
- [6. 优惠券模块（4个接口）](#6-优惠券模块)
- [7. 支付模块（3个接口）](#7-支付模块)
- [8. 知识库模块（2个接口）](#8-知识库模块)
- [9. 轮播图模块（1个接口）](#9-轮播图模块)
- [10. 文件上传（2个接口）](#10-文件上传)

---

## 通用说明

### 基础信息

| 项目 | 内容 |
|------|------|
| 协议 | HTTP/HTTPS |
| 域名 | localhost:8080 |
| 基础路径 | /api |
| 数据格式 | JSON |
| 字符编码 | UTF-8 |

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**响应码说明**：
- `200` - 成功
- `400` - 参数错误
- `401` - 未授权
- `403` - 无权限
- `404` - 资源不存在
- `500` - 服务器错误

### 认证说明

部分接口需要携带认证Token：

**Header参数**：
```
Authorization: Bearer {token}
```

**测试Token**（当前版本）：
```
Bearer test_token
```

⚠️ **注意**：当前版本使用固定userId=1进行测试

---

## 1. 用户模块

### 1.1 用户登录

**接口名称**：用户登录  
**接口地址**：`POST /user/login`  
**接口说明**：微信小程序登录（Mock版本）

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| code | String | 是 | 微信登录code |

**请求示例**：
```json
{
  "code": "test_code_123"
}
```

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| token | String | JWT token |
| userInfo | Object | 用户信息 |
| userInfo.id | Long | 用户ID |
| userInfo.openId | String | 微信openId |
| userInfo.nickName | String | 昵称 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "openId": "mock_openid_test_code_123",
      "nickName": "新用户",
      "status": 0
    }
  }
}
```

---

### 1.2 获取用户信息

**接口名称**：获取用户信息  
**接口地址**：`GET /user/info`  
**接口说明**：获取当前登录用户信息  
**需要认证**：是

#### 请求Header

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| Authorization | String | 是 | Bearer {token} |

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 用户ID |
| nickName | String | 昵称 |
| avatar | String | 头像URL |
| phone | String | 手机号 |
| points | Integer | 积分 |

**响应示例**：
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "nickName": "测试用户",
    "avatar": "https://picsum.photos/200/200",
    "phone": "13800138000",
    "points": 100
  }
}
```

---

### 1.3 更新用户信息

**接口名称**：更新用户信息  
**接口地址**：`POST /user/update`  
**接口说明**：更新用户昵称、手机号、头像  
**需要认证**：是

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| nickname | String | 否 | 昵称 |
| phone | String | 否 | 手机号 |
| avatar | String | 否 | 头像URL |

**请求示例**：
```json
{
  "nickname": "新昵称",
  "phone": "13900139000"
}
```

#### 响应参数

**响应示例**：
```json
{
  "code": 200,
  "message": "更新成功"
}
```

---

## 2. 商品模块

### 2.1 商品列表

**接口名称**：商品列表  
**接口地址**：`GET /product/list`  
**接口说明**：获取商品列表，支持分页、筛选、排序

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| categoryId | Long | 否 | 分类ID |
| keyword | String | 否 | 搜索关键词 |
| sort | Integer | 否 | 排序：1-默认 2-价格升序 3-价格降序 4-销量 |

**请求示例**：
```
GET /product/list?pageNum=1&pageSize=10&categoryId=1&sort=4
```

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| total | Long | 总数 |
| pageNum | Integer | 当前页 |
| pageSize | Integer | 每页数量 |
| list | Array | 商品列表 |

**list数组项说明**：

| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 商品ID |
| name | String | 商品名称 |
| subtitle | String | 副标题 |
| price | Decimal | 价格 |
| originalPrice | Decimal | 原价 |
| stock | Integer | 库存 |
| sales | Integer | 销量 |
| image | String | 主图URL |
| images | String | 轮播图（逗号分隔） |
| categoryId | Long | 分类ID |
| description | String | 详情描述 |
| isNew | Integer | 是否新品：0-否 1-是 |
| isHot | Integer | 是否热销：0-否 1-是 |

**响应示例**：
```json
{
  "code": 200,
  "data": {
    "total": 5,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 1,
        "name": "特级冬虫夏草 西藏那曲产",
        "subtitle": "3.5条/克 特级品质 野生采集",
        "price": 1280.00,
        "originalPrice": 1580.00,
        "stock": 50,
        "sales": 156,
        "image": "https://picsum.photos/400/400?random=1",
        "categoryId": 1,
        "isNew": 1,
        "isHot": 1
      }
    ]
  }
}
```

---

### 2.2 商品详情

**接口名称**：商品详情  
**接口地址**：`GET /product/detail/{id}`  
**接口说明**：获取单个商品的详细信息

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 商品ID |

**请求示例**：
```
GET /product/detail/1
```

#### 响应参数

同商品列表中的商品对象，包含完整字段。

**响应示例**：
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "name": "特级冬虫夏草 西藏那曲产",
    "subtitle": "3.5条/克 特级品质 野生采集",
    "price": 1280.00,
    "originalPrice": 1580.00,
    "stock": 50,
    "sales": 156,
    "image": "https://picsum.photos/400/400?random=1",
    "images": "url1,url2,url3",
    "description": "详细介绍...",
    "specifications": "[{\"name\":\"5g/盒\",\"price\":1280}]",
    "categoryId": 1,
    "isNew": 1,
    "isHot": 1
  }
}
```

---

### 2.3 商品分类

**接口名称**：商品分类  
**接口地址**：`GET /product/categories`  
**接口说明**：获取所有商品分类

#### 请求参数

无

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 分类ID |
| name | String | 分类名称 |
| icon | String | 图标URL |
| sort | Integer | 排序 |

**响应示例**：
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "冬虫夏草",
      "icon": "https://picsum.photos/100/100?random=1",
      "sort": 1
    },
    {
      "id": 2,
      "name": "藏红花",
      "icon": "https://picsum.photos/100/100?random=2",
      "sort": 2
    }
  ]
}
```

---

### 2.4 推荐商品

**接口名称**：推荐商品  
**接口地址**：`GET /product/recommend`  
**接口说明**：获取推荐的热销商品

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | Integer | 否 | 数量限制，默认10 |

**请求示例**：
```
GET /product/recommend?limit=5
```

#### 响应参数

商品对象数组，格式同商品列表。

**响应示例**：
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "特级冬虫夏草 西藏那曲产",
      "price": 1280.00,
      "image": "https://picsum.photos/400/400?random=1",
      "isHot": 1
    }
  ]
}
```

---

## 3. 购物车模块

### 3.1 购物车列表

**接口名称**：购物车列表  
**接口地址**：`GET /cart/list`  
**接口说明**：获取用户购物车商品列表  
**需要认证**：是

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 购物车ID |
| userId | Long | 用户ID |
| productId | Long | 商品ID |
| specName | String | 规格名称 |
| quantity | Integer | 数量 |
| createTime | String | 创建时间 |

**响应示例**：
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "productId": 1,
      "specName": "5g/盒",
      "quantity": 2,
      "createTime": "2025-12-03 10:00:00"
    }
  ]
}
```

---

### 3.2 添加到购物车

**接口名称**：添加到购物车  
**接口地址**：`POST /cart/add`  
**接口说明**：添加商品到购物车  
**需要认证**：是

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | Long | 是 | 商品ID |
| spec | String | 否 | 规格 |
| quantity | Integer | 是 | 数量 |

**请求示例**：
```json
{
  "productId": 1,
  "spec": "5g/盒",
  "quantity": 2
}
```

#### 响应参数

**响应示例**：
```json
{
  "code": 200,
  "message": "添加成功"
}
```

---

### 3.3 更新购物车

**接口名称**：更新购物车  
**接口地址**：`POST /cart/update`  
**接口说明**：更新购物车商品数量

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 购物车ID |
| quantity | Integer | 是 | 数量 |

**请求示例**：
```json
{
  "id": 1,
  "quantity": 5
}
```

#### 响应参数

**响应示例**：
```json
{
  "code": 200,
  "message": "更新成功"
}
```

---

### 3.4 删除购物车商品

**接口名称**：删除购物车商品  
**接口地址**：`POST /cart/delete`  
**接口说明**：批量删除购物车商品

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | Array | 是 | 购物车ID数组 |

**请求示例**：
```json
{
  "ids": [1, 2, 3]
}
```

#### 响应参数

**响应示例**：
```json
{
  "code": 200,
  "message": "删除成功"
}
```

---

### 3.5 清空购物车

**接口名称**：清空购物车  
**接口地址**：`POST /cart/clear`  
**接口说明**：清空用户购物车所有商品  
**需要认证**：是

#### 响应参数

**响应示例**：
```json
{
  "code": 200,
  "message": "清空成功"
}
```

---

## 4. 订单模块

### 4.1 创建订单

**接口名称**：创建订单  
**接口地址**：`POST /order/create`  
**接口说明**：创建新订单  
**需要认证**：是

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| goods | Array | 是 | 商品列表 |
| addressId | Long | 是 | 地址ID |
| couponId | Long | 否 | 优惠券ID |
| remark | String | 否 | 备注 |

**goods数组项说明**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | Long | 是 | 商品ID |
| spec | String | 否 | 规格 |
| quantity | Integer | 是 | 数量 |
| price | Decimal | 是 | 单价 |

**请求示例**：
```json
{
  "goods": [
    {
      "productId": 1,
      "spec": "5g/盒",
      "quantity": 2,
      "price": 199.00
    }
  ],
  "addressId": 1,
  "couponId": null,
  "remark": "请尽快发货"
}
```

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 订单ID |
| orderNo | String | 订单号 |
| totalAmount | Decimal | 订单总额 |
| status | Integer | 订单状态 |

**响应示例**：
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "orderNo": "20251203100000001",
    "totalAmount": 398.00,
    "status": 1
  }
}
```

---

### 4.2 订单列表

**接口名称**：订单列表  
**接口地址**：`GET /order/list`  
**接口说明**：获取用户订单列表，支持分页和状态筛选  
**需要认证**：是

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| status | Integer | 否 | 订单状态：1-待付款 2-待发货 3-待收货 4-已完成 5-已取消 |

**请求示例**：
```
GET /order/list?pageNum=1&pageSize=10&status=1
```

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| total | Long | 总数 |
| pageNum | Integer | 当前页 |
| pageSize | Integer | 每页数量 |
| list | Array | 订单列表 |

**响应示例**：
```json
{
  "code": 200,
  "data": {
    "total": 5,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 1,
        "orderNo": "20251203100000001",
        "totalAmount": 398.00,
        "status": 1,
        "createTime": "2025-12-03 10:00:00"
      }
    ]
  }
}
```

---

### 4.3 订单详情

**接口名称**：订单详情  
**接口地址**：`GET /order/detail/{id}`  
**接口说明**：获取订单详细信息

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 订单ID |

**请求示例**：
```
GET /order/detail/1
```

#### 响应参数

包含订单信息、商品列表、地址信息等。

**响应示例**：
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "orderNo": "20251203100000001",
    "totalAmount": 398.00,
    "status": 1,
    "remark": "请尽快发货",
    "items": [
      {
        "productId": 1,
        "productName": "特级冬虫夏草",
        "spec": "5g/盒",
        "quantity": 2,
        "price": 199.00
      }
    ]
  }
}
```

---

### 4.4 取消订单

**接口名称**：取消订单  
**接口地址**：`POST /order/cancel/{id}`  
**接口说明**：取消待付款订单

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 订单ID |

**请求示例**：
```
POST /order/cancel/1
```

#### 响应参数

**响应示例**：
```json
{
  "code": 200,
  "message": "取消成功"
}
```

---

### 4.5 确认收货

**接口名称**：确认收货  
**接口地址**：`POST /order/confirm/{id}`  
**接口说明**：确认收货，订单完成

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 订单ID |

**请求示例**：
```
POST /order/confirm/1
```

#### 响应参数

**响应示例**：
```json
{
  "code": 200,
  "message": "确认收货成功"
}
```

---

### 4.6 删除订单

**接口名称**：删除订单  
**接口地址**：`POST /order/delete/{id}`  
**接口说明**：删除已完成或已取消的订单  
**需要认证**：是

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 订单ID |

**请求示例**：
```
POST /order/delete/1
```

#### 响应参数

**响应示例**：
```json
{
  "code": 200,
  "message": "删除成功"
}
```

---

### 4.7 订单统计

**接口名称**：订单统计  
**接口地址**：`GET /order/count`  
**接口说明**：获取各状态订单数量统计  
**需要认证**：是

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| unpaid | Integer | 待付款数量 |
| unshipped | Integer | 待发货数量 |
| shipped | Integer | 待收货数量 |
| completed | Integer | 已完成数量 |

**响应示例**：
```json
{
  "code": 200,
  "data": {
    "unpaid": 2,
    "unshipped": 3,
    "shipped": 1,
    "completed": 10
  }
}
```

---

## 5. 地址模块

### 5.1 地址列表

**接口名称**：地址列表  
**接口地址**：`GET /address/list`  
**接口说明**：获取用户所有收货地址  
**需要认证**：是

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 地址ID |
| name | String | 收货人姓名 |
| phone | String | 手机号 |
| province | String | 省份 |
| city | String | 城市 |
| district | String | 区县 |
| detail | String | 详细地址 |
| isDefault | Boolean | 是否默认 |

**响应示例**：
```json
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

---

### 5.2 添加地址

**接口名称**：添加地址  
**接口地址**：`POST /address/add`  
**接口说明**：新增收货地址  
**需要认证**：是

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 收货人姓名 |
| phone | String | 是 | 手机号 |
| province | String | 是 | 省份 |
| city | String | 是 | 城市 |
| district | String | 是 | 区县 |
| detail | String | 是 | 详细地址 |
| isDefault | Boolean | 否 | 是否默认 |

**请求示例**：
```json
{
  "name": "张三",
  "phone": "13800138000",
  "province": "北京市",
  "city": "北京市",
  "district": "朝阳区",
  "detail": "xxx街道xxx号",
  "isDefault": false
}
```

#### 响应参数

**响应示例**：
```json
{
  "code": 200,
  "message": "添加成功"
}
```

---

### 5.3 更新地址

**接口名称**：更新地址  
**接口地址**：`POST /address/update`  
**接口说明**：修改收货地址

#### 请求参数

包含id字段和需要更新的字段。

**请求示例**：
```json
{
  "id": 1,
  "name": "张三",
  "phone": "13800138001"
}
```

#### 响应参数

**响应示例**：
```json
{
  "code": 200,
  "message": "更新成功"
}
```

---

### 5.4 删除地址

**接口名称**：删除地址  
**接口地址**：`POST /address/delete/{id}`  
**接口说明**：删除收货地址

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 地址ID |

**请求示例**：
```
POST /address/delete/1
```

#### 响应参数

**响应示例**：
```json
{
  "code": 200,
  "message": "删除成功"
}
```

---

### 5.5 设置默认地址

**接口名称**：设置默认地址  
**接口地址**：`POST /address/setDefault/{id}`  
**接口说明**：设置默认收货地址  
**需要认证**：是

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 地址ID |

**请求示例**：
```
POST /address/setDefault/1
```

#### 响应参数

**响应示例**：
```json
{
  "code": 200,
  "message": "设置成功"
}
```

---

## 6. 优惠券模块

### 6.1 可领取优惠券列表

**接口名称**：可领取优惠券列表  
**接口地址**：`GET /coupon/list`  
**接口说明**：获取当前可领取的优惠券

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 优惠券ID |
| name | String | 优惠券名称 |
| type | String | 类型：discount-满减券 percent-折扣券 |
| discount | Decimal | 优惠金额/折扣 |
| minAmount | Decimal | 最低消费金额 |
| total | Integer | 发行总量 |
| received | Integer | 已领取数量 |
| startTime | String | 开始时间 |
| endTime | String | 结束时间 |

**响应示例**：
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "满500减50优惠券",
      "type": "discount",
      "discount": 50.00,
      "minAmount": 500.00,
      "total": 100,
      "received": 35,
      "startTime": "2025-12-01 00:00:00",
      "endTime": "2025-12-31 23:59:59"
    }
  ]
}
```

---

### 6.2 我的优惠券

**接口名称**：我的优惠券  
**接口地址**：`GET /coupon/my`  
**接口说明**：获取用户已领取的优惠券  
**需要认证**：是

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | String | 否 | 状态：unused-未使用 used-已使用 expired-已过期 |

**请求示例**：
```
GET /coupon/my?status=unused
```

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 用户优惠券ID |
| couponId | Long | 优惠券ID |
| status | String | 状态 |
| createTime | String | 领取时间 |
| usedTime | String | 使用时间 |

**响应示例**：
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "couponId": 1,
      "status": "unused",
      "createTime": "2025-12-03 10:00:00",
      "usedTime": null
    }
  ]
}
```

---

### 6.3 领取优惠券

**接口名称**：领取优惠券  
**接口地址**：`POST /coupon/receive`  
**接口说明**：领取优惠券  
**需要认证**：是

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| couponId | Long | 是 | 优惠券ID |

**请求示例**：
```json
{
  "couponId": 1
}
```

#### 响应参数

**响应示例**：
```json
{
  "code": 200,
  "message": "领取成功"
}
```

---

### 6.4 使用优惠券

**接口名称**：使用优惠券  
**接口地址**：`POST /coupon/use`  
**接口说明**：使用优惠券（内部调用）  
**需要认证**：是

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userCouponId | Long | 是 | 用户优惠券ID |

**请求示例**：
```json
{
  "userCouponId": 1
}
```

#### 响应参数

**响应示例**：
```json
{
  "code": 200,
  "message": "使用成功"
}
```

---

## 7. 支付模块

⚠️ **注意**：当前为Mock版本，返回模拟数据

### 7.1 创建支付

**接口名称**：创建支付  
**接口地址**：`POST /payment/create`  
**接口说明**：创建支付订单（Mock）  
**需要认证**：是

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | Long | 是 | 订单ID |
| paymentMethod | String | 是 | 支付方式：wechat-微信 alipay-支付宝 |

**请求示例**：
```json
{
  "orderId": 1,
  "paymentMethod": "wechat"
}
```

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| paymentId | String | 支付ID |
| status | String | 状态 |
| wxParams | Object | 微信支付参数 |

**响应示例**：
```json
{
  "code": 200,
  "data": {
    "orderId": 1,
    "paymentId": "MOCK_PAY_1733197200000",
    "paymentMethod": "wechat",
    "status": "pending",
    "wxParams": {
      "appId": "wx1234567890",
      "timeStamp": "1733197200",
      "nonceStr": "mock_nonce_str",
      "package": "prepay_id=mock_prepay_id",
      "signType": "MD5",
      "paySign": "mock_pay_sign"
    }
  }
}
```

---

### 7.2 查询支付状态

**接口名称**：查询支付状态  
**接口地址**：`GET /payment/status/{paymentId}`  
**接口说明**：查询支付结果（Mock）

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| paymentId | String | 是 | 支付ID |

**请求示例**：
```
GET /payment/status/MOCK_PAY_1733197200000
```

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| paymentId | String | 支付ID |
| status | String | 状态：success-成功 pending-处理中 failed-失败 |
| paidAt | Long | 支付时间戳 |

**响应示例**：
```json
{
  "code": 200,
  "data": {
    "paymentId": "MOCK_PAY_1733197200000",
    "status": "success",
    "paidAt": 1733197200000
  }
}
```

---

### 7.3 支付回调

**接口名称**：支付回调  
**接口地址**：`POST /payment/notify`  
**接口说明**：接收支付平台回调（Mock）

#### 请求参数

支付平台回调的参数。

**请求示例**：
```json
{
  "paymentId": "MOCK_PAY_1733197200000",
  "status": "success"
}
```

#### 响应参数

**响应示例**：
```
SUCCESS
```

---

## 8. 知识库模块

### 8.1 知识列表

**接口名称**：知识列表  
**接口地址**：`GET /knowledge/list`  
**接口说明**：获取藏药知识列表

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 知识ID |
| title | String | 标题 |
| cover | String | 封面图 |
| summary | String | 摘要 |
| views | Integer | 阅读量 |
| createTime | String | 创建时间 |

**响应示例**：
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "title": "冬虫夏草的功效与作用",
      "cover": "https://picsum.photos/400/300?random=1",
      "summary": "冬虫夏草具有补肺益肾、止血化痰的功效...",
      "views": 256,
      "createTime": "2025-12-01 10:00:00"
    }
  ]
}
```

---

### 8.2 知识详情

**接口名称**：知识详情  
**接口地址**：`GET /knowledge/detail/{id}`  
**接口说明**：获取知识详细内容

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 知识ID |

**请求示例**：
```
GET /knowledge/detail/1
```

#### 响应参数

包含完整的内容字段。

**响应示例**：
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "title": "冬虫夏草的功效与作用",
    "cover": "https://picsum.photos/400/300?random=1",
    "content": "详细内容...",
    "views": 256,
    "createTime": "2025-12-01 10:00:00"
  }
}
```

---

## 9. 轮播图模块

### 9.1 轮播图列表

**接口名称**：轮播图列表  
**接口地址**：`GET /banner/list`  
**接口说明**：获取首页轮播图

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 轮播图ID |
| title | String | 标题 |
| image | String | 图片URL |
| linkType | String | 链接类型：product-商品 category-分类 url-外链 |
| linkValue | String | 链接值 |
| sort | Integer | 排序 |

**响应示例**：
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "title": "冬虫夏草特惠",
      "image": "https://picsum.photos/750/400?random=101",
      "linkType": "product",
      "linkValue": "1",
      "sort": 1
    },
    {
      "id": 2,
      "title": "藏红花新品上市",
      "image": "https://picsum.photos/750/400?random=102",
      "linkType": "category",
      "linkValue": "2",
      "sort": 2
    }
  ]
}
```

---

## 10. 文件上传

⚠️ **注意**：当前为Mock版本，返回占位图URL

### 10.1 上传单图

**接口名称**：上传单图  
**接口地址**：`POST /upload/image`  
**接口说明**：上传单个图片（Mock）  
**需要认证**：是

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | File | 是 | 图片文件 |

**Content-Type**: `multipart/form-data`

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| url | String | 图片URL |
| filename | String | 文件名 |
| size | String | 文件大小（字节） |

**响应示例**：
```json
{
  "code": 200,
  "data": {
    "url": "https://picsum.photos/800/600?random=1733197200000",
    "filename": "avatar.jpg",
    "size": "102400"
  }
}
```

---

### 10.2 批量上传

**接口名称**：批量上传  
**接口地址**：`POST /upload/images`  
**接口说明**：批量上传图片（Mock）  
**需要认证**：是

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| files | File[] | 是 | 图片文件数组 |

**Content-Type**: `multipart/form-data`

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| count | Integer | 上传数量 |
| urls | Array | URL列表 |

**响应示例**：
```json
{
  "code": 200,
  "data": {
    "count": 2,
    "urls": [
      {
        "url": "https://picsum.photos/800/600?random=1",
        "filename": "image1.jpg"
      },
      {
        "url": "https://picsum.photos/800/600?random=2",
        "filename": "image2.jpg"
      }
    ]
  }
}
```

---

## 📝 错误码说明

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 200 | 成功 | - |
| 400 | 参数错误 | 检查请求参数 |
| 401 | 未授权 | 检查token |
| 403 | 无权限 | 检查用户权限 |
| 404 | 资源不存在 | 检查资源ID |
| 500 | 服务器错误 | 联系管理员 |

**错误响应示例**：
```json
{
  "code": 400,
  "message": "参数不能为空",
  "data": null
}
```

---

## 🧪 测试建议

### 使用Apifox导入

1. **创建项目**
   - 在Apifox中新建项目
   - 项目名称：藏药小程序API

2. **导入接口**
   - 方式1：手动创建（根据本文档）
   - 方式2：导入Postman集合后转换

3. **配置环境变量**
   ```
   baseUrl: http://localhost:8080/api
   token: Bearer test_token
   ```

4. **测试顺序**
   - 先测试不需要认证的接口（商品列表、轮播图等）
   - 再测试需要认证的接口（购物车、订单等）

### 快速测试清单

- [ ] 商品列表 - `GET /product/list`
- [ ] 商品详情 - `GET /product/detail/1`
- [ ] 轮播图列表 - `GET /banner/list`
- [ ] 优惠券列表 - `GET /coupon/list`
- [ ] 知识列表 - `GET /knowledge/list`

---

## 📚 补充说明

### Mock接口说明

以下接口为Mock实现：

1. **用户登录** - 返回模拟token
2. **支付模块** - 返回模拟支付参数
3. **文件上传** - 返回占位图URL

生产环境需要对接真实服务。

### 认证说明

当前版本使用固定 `userId = 1` 进行测试，生产环境需要：

- 实现JWT token验证
- 对接微信小程序登录
- 添加权限控制

---

**文档版本**：v1.0  
**最后更新**：2025-12-05  
**接口总数**：36个  
**适用工具**：Apifox、Postman、ApiPost
