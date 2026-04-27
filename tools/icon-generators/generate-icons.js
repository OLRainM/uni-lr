/**
 * 生成 TabBar 图标
 * 使用 Canvas 生成简单的图标
 */

const fs = require('fs');
const path = require('path');

// 创建 Canvas（Node.js 环境）
let Canvas;
try {
    Canvas = require('canvas');
} catch (e) {
    console.log('Canvas 模块未安装，将生成简化版图标');
    Canvas = null;
}

const SIZE = 81;
const NORMAL_COLOR = '#999999';
const ACTIVE_COLOR = '#8B4513';
const OUTPUT_DIR = path.join(__dirname, 'src', 'static', 'tabbar');

// 确保输出目录存在
if (!fs.existsSync(OUTPUT_DIR)) {
    fs.mkdirSync(OUTPUT_DIR, { recursive: true });
}

/**
 * 生成 SVG 图标
 */
function generateSVGIcon(symbol, color) {
    let svgContent = '';
    
    switch(symbol) {
        case 'home':
            // 房子图标
            svgContent = `
                <svg width="${SIZE}" height="${SIZE}" xmlns="http://www.w3.org/2000/svg">
                    <path d="M40.5 15 L15 35 L20 35 L20 65 L35 65 L35 50 L46 50 L46 65 L61 65 L61 35 L66 35 Z" 
                          fill="${color}"/>
                    <rect x="37" y="53" width="7" height="9" fill="white" opacity="0.8"/>
                </svg>`;
            break;
            
        case 'category':
            // 网格图标
            svgContent = `
                <svg width="${SIZE}" height="${SIZE}" xmlns="http://www.w3.org/2000/svg">
                    <rect x="18" y="18" width="24" height="24" fill="${color}"/>
                    <rect x="48" y="18" width="24" height="24" fill="${color}"/>
                    <rect x="18" y="48" width="24" height="24" fill="${color}"/>
                    <rect x="48" y="48" width="24" height="24" fill="${color}"/>
                </svg>`;
            break;
            
        case 'cart':
            // 购物车图标
            svgContent = `
                <svg width="${SIZE}" height="${SIZE}" xmlns="http://www.w3.org/2000/svg">
                    <path d="M20 30 L25 30 L30 50 L60 50 L65 30 L30 30" 
                          stroke="${color}" stroke-width="3" fill="none"/>
                    <line x1="25" y1="30" x2="20" y2="20" 
                          stroke="${color}" stroke-width="3"/>
                    <circle cx="35" cy="57" r="4" fill="${color}"/>
                    <circle cx="55" cy="57" r="4" fill="${color}"/>
                </svg>`;
            break;
            
        case 'user':
            // 用户图标
            svgContent = `
                <svg width="${SIZE}" height="${SIZE}" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="40.5" cy="28" r="11" fill="${color}"/>
                    <path d="M20 65 Q20 45 40.5 45 T61 65" fill="${color}"/>
                </svg>`;
            break;
    }
    
    return svgContent;
}

/**
 * SVG 转 PNG（如果有 Canvas）
 */
async function svgToPng(svgContent, outputPath) {
    if (!Canvas) {
        // 如果没有 Canvas，直接保存 SVG（但改名为 .png 会有问题）
        // 生成一个最小的 PNG 占位符
        const minPng = Buffer.from(
            'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==',
            'base64'
        );
        fs.writeFileSync(outputPath, minPng);
        return;
    }
    
    const canvas = Canvas.createCanvas(SIZE, SIZE);
    const ctx = canvas.getContext('2d');
    
    // 加载 SVG
    const img = await Canvas.loadImage(Buffer.from(svgContent));
    ctx.drawImage(img, 0, 0, SIZE, SIZE);
    
    // 保存为 PNG
    const buffer = canvas.toBuffer('image/png');
    fs.writeFileSync(outputPath, buffer);
}

/**
 * 生成所有图标
 */
async function generateAllIcons() {
    const icons = ['home', 'category', 'cart', 'user'];
    
    console.log('开始生成 TabBar 图标...\n');
    
    for (const icon of icons) {
        // 生成未选中状态
        const normalSvg = generateSVGIcon(icon, NORMAL_COLOR);
        const normalPath = path.join(OUTPUT_DIR, `${icon}.png`);
        await svgToPng(normalSvg, normalPath);
        console.log(`✓ 生成 ${icon}.png`);
        
        // 生成选中状态
        const activeSvg = generateSVGIcon(icon, ACTIVE_COLOR);
        const activePath = path.join(OUTPUT_DIR, `${icon}-active.png`);
        await svgToPng(activeSvg, activePath);
        console.log(`✓ 生成 ${icon}-active.png`);
    }
    
    console.log(`\n✅ 所有图标已生成到: ${OUTPUT_DIR}`);
    
    if (!Canvas) {
        console.log('\n⚠️  注意：由于未安装 canvas 模块，生成的是占位图标');
        console.log('建议方案：');
        console.log('1. 运行 "npm install canvas" 后重新生成');
        console.log('2. 或从 https://www.iconfont.cn 下载现成图标');
        console.log('3. 或使用下方的在线图标资源');
    }
}

// 运行
generateAllIcons().catch(console.error);
