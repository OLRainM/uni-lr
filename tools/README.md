# 工具文件夹

> 项目开发过程中使用的各种工具和脚本

---

## 📁 目录结构

```
tools/
├── icon-generators/        # 图标生成工具
│   ├── create_placeholder_icons.js
│   ├── generate-icons.js
│   ├── generate_icons.js
│   ├── generate_tabbar_icons.py
│   └── 生成TabBar图标.html
├── scripts/               # 实用脚本
│   ├── fix-simulator-error.bat
│   └── fix-weixin-error.bat
├── config.example.js      # 配置文件示例
└── README.md             # 本文件
```

---

## 🎨 图标生成工具

### 使用场景

为 TabBar 生成占位符图标或自定义图标。

### 可用工具

#### 1. **生成TabBar图标.html** (推荐)
- **类型**: 浏览器工具
- **使用**: 直接在浏览器中打开，可视化生成图标
- **优点**: 无需安装依赖，操作简单
- **适合**: 快速生成测试图标

#### 2. **generate_tabbar_icons.py**
- **类型**: Python 脚本
- **依赖**: Python 3 + Pillow
- **使用**: `python generate_tabbar_icons.py`
- **优点**: 自动化批量生成
- **适合**: 需要批量生成统一风格的图标

#### 3. **generate-icons.js** / **generate_icons.js**
- **类型**: Node.js 脚本
- **依赖**: Node.js + Canvas
- **使用**: `node generate-icons.js`
- **优点**: 集成到 npm 脚本中
- **适合**: 项目构建流程

#### 4. **create_placeholder_icons.js**
- **类型**: Node.js 脚本
- **功能**: 创建简单的占位符图标
- **适合**: 开发阶段快速占位

---

## 🔧 实用脚本

### fix-simulator-error.bat

**功能**: 修复微信开发者工具模拟器错误

**使用**:
```bash
tools\scripts\fix-simulator-error.bat
```

**解决的问题**:
- 编译错误
- 模拟器无法启动
- 依赖问题

### fix-weixin-error.bat

**功能**: 修复微信小程序相关错误

**使用**:
```bash
tools\scripts\fix-weixin-error.bat
```

**解决的问题**:
- 微信开发者工具报错
- 项目配置问题

---

## ⚙️ 配置文件

### config.example.js

**功能**: 项目配置文件模板

**使用**:
1. 复制为 `config.js`
2. 根据实际情况修改配置
3. 不要提交 `config.js` 到 Git（已在 .gitignore 中）

**包含配置**:
- API 接口地址
- 小程序 AppID
- 其他环境变量

---

## 📝 使用建议

### 开发阶段

1. 使用 **生成TabBar图标.html** 快速生成测试图标
2. 遇到错误时运行对应的修复脚本
3. 根据 config.example.js 创建本地配置

### 生产环境

1. 使用专业设计的图标资源
2. 不要使用这些占位符图标
3. 配置真实的 API 地址和 AppID

---

## 🗑️ 清理说明

**这些工具文件已从项目根目录移至此处，原因**:

1. **保持根目录整洁**: 项目根目录只保留核心配置文件
2. **便于管理**: 工具文件统一管理，方便查找
3. **清晰分类**: 按功能分类，提高可维护性

**原位置**: 项目根目录  
**新位置**: `tools/` 文件夹  
**移动日期**: 2025-12-02

---

## 📚 相关文档

- [TabBar图标解决方案](../docs/03-Bug修复/TabBar相关/TabBar图标解决方案.md)
- [快速获取图标](../docs/03-Bug修复/TabBar相关/快速获取图标.md)

---

**最后更新**: 2025-12-02
