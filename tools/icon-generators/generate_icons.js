/**
 * 生成TabBar图标占位说明
 * 由于无法直接生成PNG图标，这里提供获取图标的方法
 */

const fs = require('fs');
const path = require('path');

const OUTPUT_DIR = path.join(__dirname, 'src', 'static', 'tabbar');

// 确保目录存在
if (!fs.existsSync(OUTPUT_DIR)) {
    fs.mkdirSync(OUTPUT_DIR, { recursive: true });
}

console.log('='.repeat(60));
console.log('TabBar 图标配置说明');
console.log('='.repeat(60));
console.log('');
console.log('需要在以下目录添加 8 个图标文件：');
console.log('目录：src/static/tabbar/');
console.log('');
console.log('必需的图标文件：');
console.log('  1. home.png           - 首页（未选中）');
console.log('  2. home-active.png    - 首页（选中）');
console.log('  3. category.png       - 分类（未选中）');
console.log('  4. category-active.png - 分类（选中）');
console.log('  5. cart.png           - 购物车（未选中）');
console.log('  6. cart-active.png    - 购物车（选中）');
console.log('  7. user.png           - 我的（未选中）');
console.log('  8. user-active.png    - 我的（选中）');
console.log('');
console.log('图标要求：');
console.log('  - 尺寸：81px × 81px');
console.log('  - 格式：PNG（支持透明背景）');
console.log('  - 未选中颜色：灰色 #999999');
console.log('  - 选中颜色：棕色 #8B4513');
console.log('');
console.log('推荐图标资源网站：');
console.log('  1. iconfont.cn (阿里巴巴矢量图标库)');
console.log('  2. iconfinder.com');
console.log('  3. flaticon.com');
console.log('  4. iconmonstr.com');
console.log('');
console.log('快速获取方法：');
console.log('  1. 访问 iconfont.cn');
console.log('  2. 搜索关键词：home, category, shopping cart, user');
console.log('  3. 下载PNG格式，尺寸选择 81x81');
console.log('  4. 使用图片编辑器调整颜色（或下载两个不同颜色的）');
console.log('  5. 重命名并保存到 src/static/tabbar/ 目录');
console.log('');
console.log('='.repeat(60));
console.log('');
console.log('临时解决方案：');
console.log('如果暂时没有图标，可以：');
console.log('1. 使用纯色方块作为占位符');
console.log('2. 或者暂时注释掉 pages.json 中的 tabBar 配置');
console.log('3. 使用在线PNG生成工具创建简单图标');
console.log('');
console.log('='.repeat(60));

// 创建一个提示文件
const readmeContent = `# TabBar 图标缺失

当前项目缺少 TabBar 图标文件。

## 需要的文件（共8个）

| 文件名 | 说明 | 尺寸 | 颜色 |
|--------|------|------|------|
| home.png | 首页-未选中 | 81×81 | #999999 |
| home-active.png | 首页-选中 | 81×81 | #8B4513 |
| category.png | 分类-未选中 | 81×81 | #999999 |
| category-active.png | 分类-选中 | 81×81 | #8B4513 |
| cart.png | 购物车-未选中 | 81×81 | #999999 |
| cart-active.png | 购物车-选中 | 81×81 | #8B4513 |
| user.png | 我的-未选中 | 81×81 | #999999 |
| user-active.png | 我的-选中 | 81×81 | #8B4513 |

## 获取图标步骤

### 方法1：从 iconfont.cn 获取
1. 访问 https://www.iconfont.cn/
2. 搜索对应的图标
3. 下载PNG格式，选择81×81尺寸
4. 重命名并放入此目录

### 方法2：使用在线工具
1. 访问 https://www.flaticon.com/
2. 搜索并下载免费图标
3. 使用在线工具调整尺寸和颜色
4. 保存到此目录

### 方法3：使用设计工具
使用 Figma、Sketch 或 Adobe Illustrator 创建简单的图标

## 临时解决方案

如果暂时无法获取图标，可以：
1. 暂时注释掉 pages.json 中的 tabBar 配置
2. 使用纯色PNG作为占位符
3. 运行 Python 脚本 generate_tabbar_icons.py（需要安装Pillow）
`;

fs.writeFileSync(path.join(OUTPUT_DIR, '图标说明.md'), readmeContent);
console.log('已创建图标说明文件：src/static/tabbar/图标说明.md');
console.log('');
