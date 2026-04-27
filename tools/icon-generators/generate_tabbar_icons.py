"""
生成TabBar图标的Python脚本
使用PIL库创建简单的图标占位符
"""

try:
    from PIL import Image, ImageDraw, ImageFont
    import os
    
    # 配置
    SIZE = 81  # 图标尺寸
    NORMAL_COLOR = '#999999'  # 未选中颜色
    ACTIVE_COLOR = '#8B4513'  # 选中颜色（藏药主题色）
    
    # 输出目录
    OUTPUT_DIR = 'src/static/tabbar'
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    
    def create_icon(name, color, symbol):
        """创建图标"""
        # 创建透明背景图像
        img = Image.new('RGBA', (SIZE, SIZE), (255, 255, 255, 0))
        draw = ImageDraw.Draw(img)
        
        # 解析颜色
        if color.startswith('#'):
            color = tuple(int(color[i:i+2], 16) for i in (1, 3, 5)) + (255,)
        
        # 根据不同类型绘制不同图标
        center = SIZE // 2
        
        if symbol == 'home':
            # 绘制房子图标
            points = [
                (center, 15),  # 顶点
                (15, center),  # 左下
                (25, center),  # 左墙
                (25, SIZE - 15),  # 左下角
                (SIZE - 25, SIZE - 15),  # 右下角
                (SIZE - 25, center),  # 右墙
                (SIZE - 15, center)  # 右下
            ]
            draw.polygon(points, fill=color)
            # 门
            draw.rectangle([32, 50, 49, 66], fill=(255, 255, 255, 200))
            
        elif symbol == 'category':
            # 绘制网格图标
            gap = 8
            block_size = 22
            for i in range(2):
                for j in range(2):
                    x = 18 + i * (block_size + gap)
                    y = 18 + j * (block_size + gap)
                    draw.rectangle([x, y, x + block_size, y + block_size], fill=color)
        
        elif symbol == 'cart':
            # 绘制购物车图标
            # 车身
            draw.rectangle([20, 25, 61, 50], outline=color, width=3)
            draw.line([20, 25, 15, 15], fill=color, width=3)
            # 轮子
            draw.ellipse([25, 52, 35, 62], fill=color)
            draw.ellipse([48, 52, 58, 62], fill=color)
            
        elif symbol == 'user':
            # 绘制用户图标
            # 头
            draw.ellipse([28, 18, 53, 43], fill=color)
            # 身体
            draw.ellipse([18, 43, 63, 70], fill=color)
        
        return img
    
    # 生成所有图标
    icons = [
        ('home', 'home'),
        ('category', 'category'),
        ('cart', 'cart'),
        ('user', 'user')
    ]
    
    for filename, symbol in icons:
        # 未选中状态
        normal_img = create_icon(f'{filename}', NORMAL_COLOR, symbol)
        normal_img.save(os.path.join(OUTPUT_DIR, f'{filename}.png'))
        print(f'✓ 生成 {filename}.png')
        
        # 选中状态
        active_img = create_icon(f'{filename}-active', ACTIVE_COLOR, symbol)
        active_img.save(os.path.join(OUTPUT_DIR, f'{filename}-active.png'))
        print(f'✓ 生成 {filename}-active.png')
    
    print(f'\n所有图标已生成到: {OUTPUT_DIR}/')
    print('图标尺寸: 81x81 像素')
    
except ImportError:
    print('错误: 未安装PIL库')
    print('请运行: pip install Pillow')
    print('\n或者手动下载图标并放到 src/static/tabbar/ 目录下')
    print('需要的文件:')
    print('  - home.png / home-active.png')
    print('  - category.png / category-active.png')
    print('  - cart.png / cart-active.png')
    print('  - user.png / user-active.png')
