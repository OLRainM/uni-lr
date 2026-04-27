#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
藏药小程序 - 自动化Bug检查脚本（简化版）
功能：快速检查后端和前端代码中的关键bug
"""

import os
import re
from pathlib import Path

class SimpleBugChecker:
    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.bugs = []
        
    def add_bug(self, severity: str, file: str, line: int, message: str, code: str = ""):
        """添加bug记录"""
        self.bugs.append({
            'severity': severity,
            'file': file,
            'line': line,
            'message': message,
            'code': code
        })
    
    def check_java_files(self):
        """检查Java文件"""
        print("🔍 检查Java后端代码...")
        backend_path = self.project_root / "backend" / "src" / "main" / "java"
        
        if not backend_path.exists():
            print(f"   ⚠️  后端路径不存在: {backend_path}")
            return
        
        java_files = list(backend_path.rglob("*.java"))
        print(f"   找到 {len(java_files)} 个Java文件")
        
        for java_file in java_files:
            self.check_java_file(java_file)
    
    def check_java_file(self, file_path: Path):
        """检查单个Java文件"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                lines = f.readlines()
                content = ''.join(lines)
            
            rel_path = str(file_path.relative_to(self.project_root))
            
            # 1. 检查JwtUtil问题
            if 'JwtUtil' in file_path.name:
                self.check_jwt_util(rel_path, lines, content)
            
            # 2. 检查AuthInterceptor问题
            if 'AuthInterceptor' in file_path.name:
                self.check_auth_interceptor(rel_path, lines, content)
            
            # 3. 检查Controller问题
            if 'Controller' in file_path.name:
                self.check_controller(rel_path, lines, content)
            
            # 4. 检查空指针风险
            self.check_null_pointer(rel_path, lines)
            
        except Exception as e:
            print(f"   ⚠️  检查文件失败: {file_path.name} - {str(e)}")
    
    def check_jwt_util(self, file: str, lines: list, content: str):
        """检查JWT工具类"""
        # 检查getUserId返回类型
        for i, line in enumerate(lines, 1):
            if 'public static String getUserId' in line:
                self.add_bug('HIGH', file, i, 
                           'JwtUtil.getUserId()返回String类型，但AuthInterceptor中需要Long类型，会导致类型转换错误',
                           line.strip())
    
    def check_auth_interceptor(self, file: str, lines: list, content: str):
        """检查认证拦截器"""
        # 检查是否注入了JwtUtil
        has_autowired_jwt = False
        uses_static_jwt = False
        
        for i, line in enumerate(lines, 1):
            if '@Autowired' in line:
                # 检查下一行是否是JwtUtil
                if i < len(lines) and 'JwtUtil' in lines[i]:
                    has_autowired_jwt = True
                    self.add_bug('CRITICAL', file, i,
                               'AuthInterceptor使用@Autowired注入JwtUtil，但JwtUtil不是Spring Bean，会导致NullPointerException',
                               line.strip() + '\n' + lines[i].strip())
            
            if 'JwtUtil.' in line and '@Autowired' not in lines[max(0, i-5):i]:
                uses_static_jwt = True
        
        # 检查getUserId的类型转换
        for i, line in enumerate(lines, 1):
            if 'getUserId(token)' in line:
                # 检查是否有类型转换
                if 'Long userId' in line and '(Long)' not in line and 'Long.parseLong' not in line:
                    self.add_bug('HIGH', file, i,
                               'getUserId返回String，赋值给Long类型会导致编译错误或类型转换异常',
                               line.strip())
    
    def check_controller(self, file: str, lines: list, content: str):
        """检查Controller"""
        # 检查登录接口是否有@SkipAuth
        for i, line in enumerate(lines, 1):
            if '@PostMapping' in line and 'login' in line.lower():
                # 检查前5行是否有@SkipAuth
                prev_lines = ''.join(lines[max(0, i-5):i])
                if '@SkipAuth' not in prev_lines:
                    self.add_bug('CRITICAL', file, i,
                               '登录接口未添加@SkipAuth注解，会被认证拦截器拦截，导致无法登录',
                               line.strip())
    
    def check_null_pointer(self, file: str, lines: list):
        """检查空指针风险"""
        for i, line in enumerate(lines, 1):
            # 检查getById后是否判空
            if '.getById(' in line and 'if' not in line:
                # 检查后续3行是否有判空
                next_lines = ''.join(lines[i:min(i+3, len(lines))])
                if 'if' not in next_lines or 'null' not in next_lines:
                    self.add_bug('HIGH', file, i,
                               '调用getById后未判空，可能导致NullPointerException',
                               line.strip())
    
    def check_vue_files(self):
        """检查Vue文件"""
        print("🔍 检查Vue前端代码...")
        frontend_path = self.project_root / "src"
        
        if not frontend_path.exists():
            print(f"   ⚠️  前端路径不存在: {frontend_path}")
            return
        
        vue_files = list(frontend_path.rglob("*.vue"))
        print(f"   找到 {len(vue_files)} 个Vue文件")
        
        for vue_file in vue_files:
            self.check_vue_file(vue_file)
    
    def check_vue_file(self, file_path: Path):
        """检查Vue文件"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
                lines = content.split('\n')
            
            rel_path = str(file_path.relative_to(self.project_root))
            
            # 检查API调用错误处理
            for i, line in enumerate(lines, 1):
                if 'await' in line and ('get' in line or 'post' in line):
                    prev_lines = ''.join(lines[max(0, i-10):i])
                    if 'try' not in prev_lines:
                        self.add_bug('MEDIUM', rel_path, i,
                                   'API调用未使用try-catch处理错误',
                                   line.strip())
                        
        except Exception as e:
            print(f"   ⚠️  检查文件失败: {file_path.name} - {str(e)}")
    
    def generate_report(self):
        """生成报告"""
        print("\n" + "="*80)
        print("📊 生成检查报告...")
        print("="*80)
        
        # 统计
        critical = [b for b in self.bugs if b['severity'] == 'CRITICAL']
        high = [b for b in self.bugs if b['severity'] == 'HIGH']
        medium = [b for b in self.bugs if b['severity'] == 'MEDIUM']
        
        print(f"\n🔴 严重问题: {len(critical)}")
        print(f"🟠 高危问题: {len(high)}")
        print(f"🟡 中危问题: {len(medium)}")
        print(f"\n总计: {len(self.bugs)} 个问题")
        
        # 生成Markdown报告
        report_path = self.project_root / "Bug检查报告.md"
        
        with open(report_path, 'w', encoding='utf-8') as f:
            f.write("# 藏药小程序 - Bug检查报告\n\n")
            f.write(f"**检查时间**: {self.get_current_time()}\n\n")
            f.write("---\n\n")
            
            # 摘要
            f.write("## 📊 检查摘要\n\n")
            f.write("| 类型 | 数量 | 状态 |\n")
            f.write("|------|------|------|\n")
            f.write(f"| 🔴 严重问题 | {len(critical)} | {'⚠️ 需要立即修复' if critical else '✅ 通过'} |\n")
            f.write(f"| 🟠 高危问题 | {len(high)} | {'⚠️ 需要尽快修复' if high else '✅ 通过'} |\n")
            f.write(f"| 🟡 中危问题 | {len(medium)} | {'⚠️ 建议修复' if medium else '✅ 通过'} |\n")
            f.write("\n---\n\n")
            
            # 严重问题
            if critical:
                f.write("## 🔴 严重问题 (CRITICAL)\n\n")
                f.write("**这些问题会导致系统无法正常运行，必须立即修复！**\n\n")
                for i, bug in enumerate(critical, 1):
                    f.write(f"### {i}. {bug['message']}\n\n")
                    f.write(f"**文件**: `{bug['file']}`\n\n")
                    f.write(f"**行号**: {bug['line']}\n\n")
                    if bug.get('code'):
                        f.write(f"**代码**:\n```java\n{bug['code']}\n```\n\n")
                    f.write("---\n\n")
            
            # 高危问题
            if high:
                f.write("## 🟠 高危问题 (HIGH)\n\n")
                for i, bug in enumerate(high, 1):
                    f.write(f"### {i}. {bug['message']}\n\n")
                    f.write(f"**文件**: `{bug['file']}`\n\n")
                    f.write(f"**行号**: {bug['line']}\n\n")
                    if bug.get('code'):
                        f.write(f"**代码**:\n```java\n{bug['code']}\n```\n\n")
                    f.write("---\n\n")
            
            # 中危问题
            if medium:
                f.write("## 🟡 中危问题 (MEDIUM)\n\n")
                for i, bug in enumerate(medium[:10], 1):
                    f.write(f"{i}. {bug['message']}\n")
                    f.write(f"   - 文件: `{bug['file']}` (行 {bug['line']})\n\n")
        
        print(f"\n✅ 报告已生成: {report_path}")
    
    def get_current_time(self):
        """获取当前时间"""
        from datetime import datetime
        return datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    
    def run(self):
        """运行检查"""
        print("\n" + "="*80)
        print("🚀 藏药小程序 - 自动化Bug检查工具")
        print("="*80)
        print()
        
        self.check_java_files()
        self.check_vue_files()
        self.generate_report()


if __name__ == "__main__":
    import sys
    from pathlib import Path
    
    # 获取项目根目录
    if len(sys.argv) > 1:
        project_root = sys.argv[1]
    else:
        project_root = Path(__file__).parent.parent.parent
    
    print(f"📁 项目根目录: {project_root}")
    
    checker = SimpleBugChecker(str(project_root))
    checker.run()

