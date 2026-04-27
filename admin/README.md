# 藏药小程序 - 商家管理后台

> 基于 Vue 3 + Element Plus 开发的现代化商家管理系统

---

## 📚 项目简介

这是藏药小程序的商家管理后台，提供商品管理、订单管理、用户管理、数据统计等功能。

### 主要功能

- 📊 **数据概览** - 实时查看订单、收入、商品、用户等核心数据
- 🛍️ **商品管理** - 商品的增删改查、上下架管理
- 📋 **订单管理** - 订单查看、发货操作、状态管理
- 👥 **用户管理** - 用户信息查看、订单统计
- ⚙️ **系统设置** - 基本配置、配送设置、支付设置、账号安全

---

## 🛠️ 技术栈

- **框架**: Vue 3 (Composition API)
- **UI 组件库**: Element Plus
- **路由**: Vue Router 4
- **图表**: ECharts 5
- **HTTP 客户端**: Axios
- **构建工具**: Vite 4
- **样式**: SCSS

---

## 📦 安装运行

### 环境要求

- Node.js >= 16.0.0
- npm >= 8.0.0

### 安装依赖

```bash
cd admin
npm install
```

### 开发模式

```bash
npm run dev
```

浏览器自动打开 `http://localhost:3000`

### 生产构建

```bash
npm run build
```

构建产物在 `dist/` 目录

### 预览构建产物

```bash
npm run preview
```

---

## 🔐 登录信息

**测试账号**：
- 用户名：`admin`
- 密码：`123456`

---

## 📁 项目结构

```
admin/
├── public/                 # 静态资源
├── src/
│   ├── views/             # 页面组件
│   │   ├── Login.vue      # 登录页
│   │   ├── Layout.vue     # 布局组件
│   │   ├── Dashboard.vue  # 数据概览
│   │   ├── Products.vue   # 商品管理
│   │   ├── Orders.vue     # 订单管理
│   │   ├── Users.vue      # 用户管理
│   │   └── Settings.vue   # 系统设置
│   ├── router/            # 路由配置
│   │   └── index.js
│   ├── styles/            # 全局样式
│   │   └── global.scss
│   ├── utils/             # 工具函数
│   ├── App.vue            # 根组件
│   └── main.js            # 入口文件
├── index.html             # HTML 模板
├── vite.config.js         # Vite 配置
├── package.json           # 项目配置
└── README.md              # 项目说明
```

---

## 🎨 功能页面

### 1. 登录页面
- 用户登录认证
- 表单验证
- 记住登录状态

### 2. 数据概览
- 核心数据统计卡片
- 订单趋势图表
- 收入统计图表
- 最近订单列表

### 3. 商品管理
- 商品列表展示
- 商品搜索筛选
- 商品添加/编辑
- 商品删除
- 商品上下架
- 分页功能

### 4. 订单管理
- 订单列表展示
- 订单搜索筛选
- 订单详情查看
- 订单发货操作
- 物流信息填写
- 分页功能

### 5. 用户管理
- 用户列表展示
- 用户搜索筛选
- 用户详情查看
- 消费统计
- 分页功能

### 6. 系统设置
- 基本设置（商城名称、客服信息）
- 配送设置（运费、免运费金额）
- 支付设置（支付方式、自动确认）
- 账号安全（密码修改）

---

## 🔧 配置说明

### 路由配置

路由采用路由守卫机制，未登录用户自动跳转到登录页。

修改 `src/router/index.js` 可以调整路由配置。

### API 配置

目前使用模拟数据，实际使用时需要：

1. 在 `src/utils/` 目录创建 `request.js` 封装 axios
2. 配置 API 基础路径
3. 添加请求拦截器和响应拦截器
4. 处理 token 认证

示例代码：

```javascript
// src/utils/request.js
import axios from 'axios'

const request = axios.create({
  baseURL: 'http://your-api-url',
  timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(config => {
  const token = localStorage.getItem('admin_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器
request.interceptors.response.use(
  response => response.data,
  error => {
    // 错误处理
    return Promise.reject(error)
  }
)

export default request
```

### Vite 代理配置

开发环境中，如需配置 API 代理，修改 `vite.config.js`：

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://your-backend-url',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

---

## 🚀 部署指南

### 1. 构建项目

```bash
npm run build
```

### 2. 部署到服务器

将 `dist/` 目录下的所有文件上传到服务器。

### 3. Nginx 配置示例

```nginx
server {
  listen 80;
  server_name your-domain.com;
  
  location / {
    root /path/to/dist;
    index index.html;
    try_files $uri $uri/ /index.html;
  }
  
  # API 代理（如果需要）
  location /api/ {
    proxy_pass http://your-backend-url/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
  }
}
```

### 4. 使用 Docker 部署

创建 `Dockerfile`：

```dockerfile
FROM nginx:alpine

COPY dist/ /usr/share/nginx/html/

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

构建和运行：

```bash
docker build -t tibetan-medicine-admin .
docker run -d -p 80:80 tibetan-medicine-admin
```

---

## 📝 开发说明

### 添加新页面

1. 在 `src/views/` 创建新的 Vue 组件
2. 在 `src/router/index.js` 添加路由配置
3. 在 `Layout.vue` 的菜单中添加导航项

### 主题定制

修改 `src/styles/global.scss` 中的 CSS 变量：

```scss
:root {
  --primary-color: #8B4513;  // 主题色
  --success-color: #67c23a;
  --warning-color: #e6a23c;
  --danger-color: #f56c6c;
}
```

### 图标使用

项目已注册所有 Element Plus Icons，可以直接使用：

```vue
<el-icon><Edit /></el-icon>
```

---

## 🐛 常见问题

### 1. 端口被占用

修改 `vite.config.js` 中的端口号：

```javascript
server: {
  port: 3001  // 改为其他端口
}
```

### 2. 安装依赖失败

尝试清除缓存后重新安装：

```bash
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

### 3. 图表不显示

确保 ECharts 正确导入和初始化：

```javascript
import * as echarts from 'echarts'
const chart = echarts.init(dom)
```

---

## 🎯 后续优化

- [ ] 接入真实后端 API
- [ ] 添加数据导出功能
- [ ] 增加批量操作功能
- [ ] 添加数据报表页面
- [ ] 实现消息通知功能
- [ ] 添加权限管理系统
- [ ] 优化移动端适配
- [ ] 添加暗黑模式

---

## 📄 许可证

MIT License

---

## 👥 贡献

欢迎提交 Issue 和 Pull Request！

---

**开发日期**：2025-12-02  
**版本**：v1.0.0
