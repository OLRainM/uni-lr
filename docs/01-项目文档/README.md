# 藏药小程序

基于 uni-app 开发的藏药电商小程序，提供藏药产品浏览、购买和订单管理服务。

## 项目简介

本项目是一个完整的藏药电商小程序，包含以下核心功能：
- 首页：轮播图、藏药知识科普、品牌故事、热销推荐
- 商品：商品列表、分类筛选、商品详情、规格选择
- 购物车：商品管理、数量调整、结算功能
- 订单：订单创建、订单列表、订单详情、支付流程
- 个人中心：用户信息、收货地址管理、订单管理

## 技术栈

- **框架**: uni-app (Vue 3)
- **状态管理**: Vuex
- **UI 组件**: uni-ui
- **样式**: SCSS
- **构建工具**: Vite

## 项目结构

```
uniapp-lr/
├── api/                    # API 接口
│   └── index.js
├── pages/                  # 页面文件
│   ├── index/             # 首页
│   ├── product/           # 商品相关
│   ├── cart/              # 购物车
│   ├── order/             # 订单相关
│   ├── user/              # 个人中心
│   └── knowledge/         # 藏药知识
├── store/                  # Vuex 状态管理
│   └── index.js
├── utils/                  # 工具函数
│   ├── request.js         # 网络请求封装
│   └── util.js            # 通用工具函数
├── static/                 # 静态资源
├── App.vue                # 应用配置
├── main.js                # 入口文件
├── pages.json             # 页面配置
├── manifest.json          # 应用配置
├── uni.scss               # 全局样式变量
└── package.json           # 项目依赖

```

## 快速开始

### 1. 安装依赖

```bash
npm install
```

### 2. 运行项目

**微信小程序开发**
```bash
npm run dev:mp-weixin
```

然后使用微信开发者工具打开项目的 `dist/dev/mp-weixin` 目录

### 3. 构建生产版本

```bash
npm run build:mp-weixin
```

## 主要页面说明

### 首页 (pages/index/index.vue)
- 轮播图展示
- 藏药材分类导航
- 藏药知识科普
- 品牌故事介绍
- 热销商品推荐

### 商品列表 (pages/product/list.vue)
- 商品分类筛选
- 价格、销量排序
- 商品搜索功能
- 高级筛选（价格区间、产地等）

### 商品详情 (pages/product/detail.vue)
- 商品图片轮播
- 规格选择
- 价格展示
- 商品参数
- 用户评价

### 购物车 (pages/cart/cart.vue)
- 商品列表管理
- 数量调整
- 全选/单选
- 合计金额
- 结算功能

### 结算页 (pages/order/checkout.vue)
- 收货地址选择
- 商品清单展示
- 配送方式
- 优惠信息
- 费用明细
- 提交订单

### 订单列表 (pages/order/list.vue)
- 订单状态分类
- 订单卡片展示
- 订单操作（支付、取消、确认收货等）

### 订单详情 (pages/order/detail.vue)
- 订单状态进度
- 收货信息
- 商品信息
- 费用明细
- 订单信息

### 个人中心 (pages/user/user.vue)
- 用户信息展示
- 订单入口
- 功能菜单
- 设置选项

### 收货地址 (pages/user/address.vue)
- 地址列表
- 添加地址
- 编辑地址
- 删除地址
- 设置默认地址

## 配置说明

### 1. API 地址配置

修改 `utils/request.js` 中的 `BASE_URL`：

```javascript
const BASE_URL = 'https://your-api-domain.com'
```

### 2. 小程序 AppID 配置

修改 `manifest.json` 中的 `mp-weixin.appid`：

```json
{
  "mp-weixin": {
    "appid": "your-appid"
  }
}
```

### 3. 主题色配置

修改 `uni.scss` 中的颜色变量：

```scss
$primary-color: #8B1A1A;      // 主题色
$secondary-color: #D4AF37;     // 辅助色
```

## 数据模拟

当前项目使用模拟数据，实际项目中需要替换为真实的 API 调用。

需要替换的位置：
- 各页面的 `load*` 方法
- API 调用改为真实接口
- 图片资源替换为实际资源

## 注意事项

1. **图片资源**: 项目中使用了占位图片，实际使用时需要替换为真实图片
2. **API 接口**: 需要对接后端 API 接口
3. **微信支付**: 需要配置微信支付相关参数
4. **用户授权**: 需要处理微信登录授权流程
5. **数据持久化**: 建议使用 uni.storage 进行本地数据存储

## 功能扩展建议

- [ ] 实现微信登录
- [ ] 对接真实 API 接口
- [ ] 添加商品收藏功能
- [ ] 实现在线支付
- [ ] 添加物流查询
- [ ] 实现优惠券系统
- [ ] 添加积分系统
- [ ] 实现商品评价功能
- [ ] 添加客服聊天功能
- [ ] 实现分享功能

## 开发文档

- [uni-app 官方文档](https://uniapp.dcloud.net.cn/)
- [Vue 3 官方文档](https://cn.vuejs.org/)
- [微信小程序开发文档](https://developers.weixin.qq.com/miniprogram/dev/framework/)

## 许可证

MIT License

---

**版本**: v1.0.0  
**创建日期**: 2025-01-15  
**开发者**: 藏药小程序团队
