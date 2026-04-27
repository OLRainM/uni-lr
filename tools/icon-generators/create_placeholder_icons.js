/**
 * 创建简单的PNG占位符图标
 * 使用base64编码的最小PNG数据
 */

const fs = require('fs');
const path = require('path');

const OUTPUT_DIR = path.join(__dirname, 'src', 'static', 'tabbar');

// 确保目录存在
if (!fs.existsSync(OUTPUT_DIR)) {
    fs.mkdirSync(OUTPUT_DIR, { recursive: true });
}

// 一个81x81的简单灰色PNG图片（base64编码）
const grayIconBase64 = 'iVBORw0KGgoAAAANSUhEUgAAAFEAAABRCAYAAACqj0o2AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEwAACxMBAJqcGAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAD/SURBVHic7dMxDQAgAMDAv+fhYQkEIcnqrMyeY/7W7QJ/MRIYiQxGIoORyGAkMhiJDEYig5HIYCQyGIkMRiKDkchgJDIYiQxGIoORyGAkMhiJDEYig5HIYCQyGIkMRiKDkchgJDIYiQxGIoORyGAkMhiJDEYig5HIYCQyGIkMRiKDkchgJDIYiQxGIoORyGAkMhiJDEYig5HIYCQyGIkMRiKDkchgJDIYiQxGIoORyGAkMhiJDEYig5HIYCQyGIkMRiKDkchgJDIYiQxGIoORyGAkMhiJDEYig5HIYCQyGIkMRiKDkchgJDIYiQxGIoORyGAkMhiJDEYig5HIYCQyXDvqA/PP5fK/AAAAAElFTkSuQmCC';

// 一个81x81的简单棕色PNG图片（base64编码）
const brownIconBase64 = 'iVBORw0KGgoAAAANSUhEUgAAAFEAAABRCAYAAACqj0o2AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEwAACxMBAJqcGAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAD/SURBVHic7dMxDQAgAMDAv+fhHxaAJKuzMnuO+Vu3C/zFSGAkMhiJDEYig5HIYCQyGIkMRiKDkchgJDIYiQxGIoORyGAkMhiJDEYig5HIYCQyGIkMRiKDkchgJDIYiQxGIoORyGAkMhiJDEYig5HIYCQyGIkMRiKDkchgJDIYiQxGIoORyGAkMhiJDEYig5HIYCQyGIkMRiKDkchgJDIYiQxGIoORyGAkMhiJDEYig5HIYCQyGIkMRiKDkchgJDIYiQxGIoORyGAkMhiJDEYig5HIYCQyGIkMRiKDkchgJDIYiQxGIoORyGAkMhiJDEYig5HIYCQyGIkMRiKDkchgJDLcO0oDdAfz0M0AAAAASUVORK5CYII=';

// 图标列表
const icons = [
    { name: 'home', active: false },
    { name: 'home-active', active: true },
    { name: 'category', active: false },
    { name: 'category-active', active: true },
    { name: 'cart', active: false },
    { name: 'cart-active', active: true },
    { name: 'user', active: false },
    { name: 'user-active', active: true }
];

console.log('正在创建占位符图标...\n');

icons.forEach(icon => {
    const base64Data = icon.active ? brownIconBase64 : grayIconBase64;
    const buffer = Buffer.from(base64Data, 'base64');
    const filePath = path.join(OUTPUT_DIR, `${icon.name}.png`);
    
    fs.writeFileSync(filePath, buffer);
    console.log(`✓ 已创建: ${icon.name}.png`);
});

console.log('\n所有占位符图标已创建完成！');
console.log(`位置: ${OUTPUT_DIR}`);
console.log('\n注意: 这些是简单的占位符图标，建议后续替换为专业设计的图标。');
console.log('请参考 src/static/tabbar/图标说明.md 获取更多信息。\n');
