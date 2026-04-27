#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
藏药小程序 - 自动化Bug检查脚本
功能：全面检查后端和前端代码中的潜在bug
作者：Tibetan Medicine Team
日期：2026-03-09
"""

import os
import re
import json
from pathlib import Path
from typing import List, Dict, Tuple

class BugChecker:
    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.backend_path = self.project_root / "backend" / "src" / "main" / "java"
        self.frontend_path = self.project_root / "frontend"
        self.bugs = []
        self.warnings = []
        self.info = []
        
    def add_bug(self, file: str, line: int, severity: str, message: str, code: str = ""):
        """添加bug记录"""
        self.bugs.append({
            'file': file,
            'line': line,
            'severity': severity,
            'message': message,
            'code': code
        })
    
    def add_warning(self, file: str, line: int, message: str):
        """添加警告"""
        self.warnings.append({
            'file': file,
            'line': line,
            'message': message
        })
    
    def add_info(self, file: str, message: str):
        """添加信息"""
        self.info.append({
            'file': file,
            'message': message
        })
    
    def check_java_files(self):
        """检查Java文件"""
        print("🔍 检查Java后端代码...")
        
        if not self.backend_path.exists():
            print(f"❌ 后端路径不存在: {self.backend_path}")
            return
        
        java_files = list(self.backend_path.rglob("*.java"))
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
            
            # 1. 检查空指针风险
            self.check_null_pointer(rel_path, lines, content)
            
            # 2. 检查SQL注入风险
            self.check_sql_injection(rel_path, lines, content)
            
            # 3. 检查资源泄漏
            self.check_resource_leak(rel_path, lines, content)
            
            # 4. 检查异常处理
            self.check_exception_handling(rel_path, lines, content)
            
            # 5. 检查并发问题
            self.check_concurrency_issues(rel_path, lines, content)
            
            # 6. 检查JWT工具类问题
            if 'JwtUtil' in file_path.name:
                self.check_jwt_util(rel_path, lines, content)
            
            # 7. 检查Controller问题
            if 'Controller' in file_path.name:
                self.check_controller(rel_path, lines, content)
            
            # 8. 检查Service实现问题
            if 'ServiceImpl' in file_path.name:
                self.check_service_impl(rel_path, lines, content)
                
        except Exception as e:
            print(f"   ⚠️  检查文件失败: {file_path.name} - {str(e)}")
    
    def check_null_pointer(self, file: str, lines: List[str], content: str):
        """检查空指针风险"""
        # 检查未判空的get操作
        patterns = [
            (r'\.getById\([^)]+\)(?!\s*[;{]|\s*!=\s*null|\s*==\s*null)', 
             '调用getById后未判空，可能导致NullPointerException'),
            (r'\.getOne\([^)]+\)(?!\s*[;{]|\s*!=\s*null|\s*==\s*null)', 
             '调用getOne后未判空，可能导致NullPointerException'),
            (r'params\.get\([^)]+\)(?!\s*!=\s*null|\s*==\s*null)', 
             '从Map获取值后未判空'),
        ]
        
        for i, line in enumerate(lines, 1):
            for pattern, message in patterns:
                if re.search(pattern, line):
                    # 检查后续几行是否有判空
                    next_lines = ''.join(lines[i:min(i+3, len(lines))])
                    if 'if' not in next_lines or 'null' not in next_lines:
                        self.add_bug(file, i, 'HIGH', message, line.strip())
    
    def check_sql_injection(self, file: str, lines: List[str], content: str):
        """检查SQL注入风险"""
        # 检查字符串拼接SQL
        for i, line in enumerate(lines, 1):
            if re.search(r'(SELECT|INSERT|UPDATE|DELETE).*\+.*["\']', line, re.IGNORECASE):
                self.add_bug(file, i, 'CRITICAL', 
                           'SQL语句使用字符串拼接，存在SQL注入风险', line.strip())
            
            # 检查wrapper.last使用
            if '.last(' in line and '+' in line:
                self.add_bug(file, i, 'HIGH', 
                           'wrapper.last()使用字符串拼接，可能存在SQL注入风险', line.strip())
    
    def check_resource_leak(self, file: str, lines: List[str], content: str):
        """检查资源泄漏"""
        # 检查未关闭的资源
        if 'new FileInputStream' in content or 'new FileOutputStream' in content:
            if 'try-with-resources' not in content and '.close()' not in content:
                self.add_bug(file, 0, 'MEDIUM', 
                           '文件流未使用try-with-resources或未显式关闭，可能导致资源泄漏')
    
    def check_exception_handling(self, file: str, lines: List[str], content: str):
        """检查异常处理"""
        for i, line in enumerate(lines, 1):
            # 检查空catch块
            if 'catch' in line:
                # 查找catch块内容
                brace_count = 0
                catch_content = []
                for j in range(i, min(i+10, len(lines))):
                    catch_content.append(lines[j])
                    brace_count += lines[j].count('{') - lines[j].count('}')
                    if brace_count == 0 and '{' in ''.join(catch_content):
                        break
                
                catch_text = ''.join(catch_content)
                # 如果catch块只有注释或空行
                if re.sub(r'//.*|/\*.*?\*/|\s', '', catch_text.split('{', 1)[-1].split('}')[0]) == '':
                    self.add_warning(file, i, '空的catch块，异常被静默吞掉')
            
            # 检查printStackTrace
            if 'printStackTrace()' in line:
                self.add_warning(file, i, '使用printStackTrace()，应该使用日志框架')
    
    def check_concurrency_issues(self, file: str, lines: List[str], content: str):
        """检查并发问题"""
        # 检查SimpleDateFormat（非线程安全）
        if 'SimpleDateFormat' in content and 'static' in content:
            self.add_bug(file, 0, 'HIGH', 
                       'SimpleDateFormat是非线程安全的，不应该作为静态变量')
        
        # 检查非线程安全的集合
        if '@Service' in content or '@Component' in content:
            for i, line in enumerate(lines, 1):
                if re.search(r'(private|public)\s+(static\s+)?HashMap|ArrayList', line):
                    if 'static' in line:
                        self.add_warning(file, i, 
                                       '静态集合在多线程环境下可能不安全，考虑使用ConcurrentHashMap')
    
    def check_jwt_util(self, file: str, lines: List[str], content: str):
        """检查JWT工具类特定问题"""
        # 检查JwtUtil是否被注入为Bean
        if 'public class JwtUtil' in content:
            if '@Component' not in content and '@Service' not in content:
                # 检查是否有非静态方法
                if re.search(r'public\s+(?!static)\w+\s+\w+\s*\(', content):
                    self.add_bug(file, 0, 'HIGH', 
                               'JwtUtil包含非静态方法但未标注为Spring Bean，在AuthInterceptor中注入会失败')
        
        # 检查getUserId返回类型
        for i, line in enumerate(lines, 1):
            if 'getUserId' in line and 'String' in line:
                # 查找调用处是否有类型转换
                self.add_warning(file, i,
                               'getUserId返回String类型，调用处需要转换为Long，容易出错')

    def check_controller(self, file: str, lines: List[str], content: str):
        """检查Controller特定问题"""
        # 检查登录接口是否有@SkipAuth
        if '/login' in content:
            login_method_found = False
            skip_auth_found = False

            for i, line in enumerate(lines):
                if '@PostMapping' in line and 'login' in line:
                    login_method_found = True
                    # 检查前几行是否有@SkipAuth
                    prev_lines = ''.join(lines[max(0, i-5):i])
                    if '@SkipAuth' in prev_lines:
                        skip_auth_found = True
                    break

            if login_method_found and not skip_auth_found:
                self.add_bug(file, 0, 'CRITICAL',
                           '登录接口未添加@SkipAuth注解，会被拦截器拦截导致无法登录')

        # 检查是否正确获取userId
        for i, line in enumerate(lines, 1):
            if 'request.getAttribute("userId")' in line:
                # 检查是否有类型转换
                if '(Long)' not in line and 'Long userId' not in line:
                    self.add_warning(file, i,
                                   'getAttribute返回Object类型，需要强制转换为Long')

    def check_service_impl(self, file: str, lines: List[str], content: str):
        """检查Service实现类问题"""
        # 检查事务注解
        if 'save(' in content or 'update(' in content or 'remove(' in content:
            if '@Transactional' not in content:
                self.add_warning(file, 0,
                               'Service包含数据库写操作但未使用@Transactional注解')

        # 检查分页查询
        for i, line in enumerate(lines, 1):
            if 'new Page<>' in line:
                # 检查是否有参数校验
                prev_lines = ''.join(lines[max(0, i-10):i])
                if 'pageNum' in prev_lines and 'if' not in prev_lines:
                    self.add_warning(file, i,
                                   '分页参数未校验，可能导致非法参数')

    def check_vue_files(self):
        """检查Vue文件"""
        print("🔍 检查Vue前端代码...")

        if not self.frontend_path.exists():
            print(f"❌ 前端路径不存在: {self.frontend_path}")
            return

        vue_files = list(self.frontend_path.rglob("*.vue"))
        js_files = list(self.frontend_path.rglob("*.js"))

        print(f"   找到 {len(vue_files)} 个Vue文件")
        print(f"   找到 {len(js_files)} 个JS文件")

        for vue_file in vue_files:
            self.check_vue_file(vue_file)

        for js_file in js_files:
            self.check_js_file(js_file)

    def check_vue_file(self, file_path: Path):
        """检查单个Vue文件"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
                lines = content.split('\n')

            rel_path = str(file_path.relative_to(self.project_root))

            # 1. 检查API调用错误处理
            self.check_vue_error_handling(rel_path, lines, content)

            # 2. 检查数据绑定问题
            self.check_vue_data_binding(rel_path, lines, content)

            # 3. 检查生命周期问题
            self.check_vue_lifecycle(rel_path, lines, content)

        except Exception as e:
            print(f"   ⚠️  检查文件失败: {file_path.name} - {str(e)}")

    def check_vue_error_handling(self, file: str, lines: List[str], content: str):
        """检查Vue错误处理"""
        # 检查API调用是否有错误处理
        for i, line in enumerate(lines, 1):
            if 'await' in line and ('get' in line or 'post' in line):
                # 检查是否在try-catch中
                prev_lines = ''.join(lines[max(0, i-10):i])
                next_lines = ''.join(lines[i:min(i+10, len(lines))])

                if 'try' not in prev_lines and 'catch' not in next_lines:
                    self.add_warning(file, i,
                                   'API调用未使用try-catch处理错误')

    def check_vue_data_binding(self, file: str, lines: List[str], content: str):
        """检查Vue数据绑定问题"""
        # 检查v-for是否有key
        for i, line in enumerate(lines, 1):
            if 'v-for' in line and ':key' not in line and 'key=' not in line:
                self.add_warning(file, i, 'v-for缺少:key属性，可能影响性能')

    def check_vue_lifecycle(self, file: str, lines: List[str], content: str):
        """检查Vue生命周期问题"""
        # 检查是否在onMounted中加载数据
        if 'onMounted' not in content and ('loadData' in content or 'getData' in content):
            self.add_info(file, '建议在onMounted生命周期中加载初始数据')

    def check_js_file(self, file_path: Path):
        """检查JS文件"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
                lines = content.split('\n')

            rel_path = str(file_path.relative_to(self.project_root))

            # 检查请求封装
            if 'request' in file_path.name:
                self.check_request_wrapper(rel_path, lines, content)

            # 检查API定义
            if 'api' in str(file_path):
                self.check_api_definition(rel_path, lines, content)

        except Exception as e:
            print(f"   ⚠️  检查文件失败: {file_path.name} - {str(e)}")

    def check_request_wrapper(self, file: str, lines: List[str], content: str):
        """检查请求封装"""
        # 检查是否处理401错误
        if '401' not in content:
            self.add_warning(file, 0, '请求封装未处理401未授权错误')

        # 检查是否添加Token
        if 'Authorization' not in content and 'token' not in content:
            self.add_warning(file, 0, '请求封装未自动添加Token到请求头')

    def check_api_definition(self, file: str, lines: List[str], content: str):
        """检查API定义"""
        # 检查API路径是否正确
        for i, line in enumerate(lines, 1):
            if 'url:' in line:
                if not line.strip().startswith('//'):
                    # 检查是否以/开头
                    if "url: '" in line or 'url: "' in line:
                        url_match = re.search(r'url:\s*["\']([^"\']+)["\']', line)
                        if url_match:
                            url = url_match.group(1)
                            if not url.startswith('/'):
                                self.add_warning(file, i,
                                               f'API路径应该以/开头: {url}')

    def check_config_files(self):
        """检查配置文件"""
        print("🔍 检查配置文件...")

        # 检查application.yml
        app_yml = self.project_root / "backend" / "src" / "main" / "resources" / "application.yml"
        if app_yml.exists():
            self.check_application_yml(app_yml)

        # 检查pom.xml
        pom_xml = self.project_root / "backend" / "pom.xml"
        if pom_xml.exists():
            self.check_pom_xml(pom_xml)

    def check_application_yml(self, file_path: Path):
        """检查application.yml配置"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()

            rel_path = str(file_path.relative_to(self.project_root))

            # 检查数据库配置
            if 'url:' in content and 'localhost' in content:
                self.add_info(rel_path, '数据库配置使用localhost，部署时需要修改')

            # 检查Redis配置
            if 'redis' in content:
                if 'password:' in content:
                    # 检查密码是否为空
                    if re.search(r'password:\s*$', content, re.MULTILINE):
                        self.add_info(rel_path, 'Redis密码为空，生产环境建议设置密码')

            # 检查JWT密钥
            if 'secret:' in content:
                secret_match = re.search(r'secret:\s*(.+)', content)
                if secret_match:
                    secret = secret_match.group(1).strip()
                    if len(secret) < 32:
                        self.add_warning(rel_path, 0,
                                       'JWT密钥长度过短，建议至少32字符')

        except Exception as e:
            print(f"   ⚠️  检查配置文件失败: {str(e)}")

    def check_pom_xml(self, file_path: Path):
        """检查pom.xml依赖"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()

            rel_path = str(file_path.relative_to(self.project_root))

            # 检查是否有安全漏洞的依赖版本
            vulnerable_deps = {
                'log4j': '2.14',  # Log4j漏洞
                'fastjson': '1.2.80',  # Fastjson漏洞
            }

            for dep, vuln_version in vulnerable_deps.items():
                if dep in content and vuln_version in content:
                    self.add_bug(rel_path, 0, 'CRITICAL',
                               f'检测到存在安全漏洞的依赖: {dep} {vuln_version}')

        except Exception as e:
            print(f"   ⚠️  检查pom.xml失败: {str(e)}")

    def generate_report(self):
        """生成检查报告"""
        print("\n" + "="*80)
        print("📊 生成检查报告...")
        print("="*80)

        # 统计
        critical_bugs = [b for b in self.bugs if b['severity'] == 'CRITICAL']
        high_bugs = [b for b in self.bugs if b['severity'] == 'HIGH']
        medium_bugs = [b for b in self.bugs if b['severity'] == 'MEDIUM']

        total_bugs = len(self.bugs)
        total_warnings = len(self.warnings)
        total_info = len(self.info)

        # 控制台输出
        print(f"\n🔴 严重问题: {len(critical_bugs)}")
        print(f"🟠 高危问题: {len(high_bugs)}")
        print(f"🟡 中危问题: {len(medium_bugs)}")
        print(f"⚠️  警告: {total_warnings}")
        print(f"ℹ️  信息: {total_info}")
        print(f"\n总计: {total_bugs} 个bug, {total_warnings} 个警告, {total_info} 条信息")

        # 生成Markdown报告
        report_path = self.project_root / "Bug检查报告.md"

        with open(report_path, 'w', encoding='utf-8') as f:
            f.write("# 藏药小程序 - Bug检查报告\n\n")
            f.write(f"**生成时间**: {self.get_current_time()}\n\n")
            f.write("---\n\n")

            # 摘要
            f.write("## 📊 检查摘要\n\n")
            f.write("| 类型 | 数量 | 状态 |\n")
            f.write("|------|------|------|\n")
            f.write(f"| 🔴 严重问题 (CRITICAL) | {len(critical_bugs)} | {'⚠️ 需要立即修复' if critical_bugs else '✅ 通过'} |\n")
            f.write(f"| 🟠 高危问题 (HIGH) | {len(high_bugs)} | {'⚠️ 需要尽快修复' if high_bugs else '✅ 通过'} |\n")
            f.write(f"| 🟡 中危问题 (MEDIUM) | {len(medium_bugs)} | {'⚠️ 建议修复' if medium_bugs else '✅ 通过'} |\n")
            f.write(f"| ⚠️ 警告 (WARNING) | {total_warnings} | {'💡 建议优化' if total_warnings else '✅ 通过'} |\n")
            f.write(f"| ℹ️ 信息 (INFO) | {total_info} | 📝 参考信息 |\n")
            f.write("\n---\n\n")

            # 严重问题
            if critical_bugs:
                f.write("## 🔴 严重问题 (CRITICAL)\n\n")
                f.write("**这些问题会导致系统无法正常运行，必须立即修复！**\n\n")
                for i, bug in enumerate(critical_bugs, 1):
                    f.write(f"### {i}. {bug['message']}\n\n")
                    f.write(f"**文件**: `{bug['file']}`\n\n")
                    if bug.get('line'):
                        f.write(f"**行号**: {bug['line']}\n\n")
                    if bug.get('code'):
                        f.write(f"**代码**:\n```java\n{bug['code']}\n```\n\n")
                    f.write("---\n\n")

            # 高危问题
            if high_bugs:
                f.write("## 🟠 高危问题 (HIGH)\n\n")
                f.write("**这些问题可能导致运行时错误或安全问题，建议尽快修复。**\n\n")
                for i, bug in enumerate(high_bugs, 1):
                    f.write(f"### {i}. {bug['message']}\n\n")
                    f.write(f"**文件**: `{bug['file']}`\n\n")
                    if bug.get('line'):
                        f.write(f"**行号**: {bug['line']}\n\n")
                    if bug.get('code'):
                        f.write(f"**代码**:\n```java\n{bug['code']}\n```\n\n")
                    f.write("---\n\n")

            # 中危问题
            if medium_bugs:
                f.write("## 🟡 中危问题 (MEDIUM)\n\n")
                f.write("**这些问题可能影响系统稳定性，建议计划修复。**\n\n")
                for i, bug in enumerate(medium_bugs, 1):
                    f.write(f"### {i}. {bug['message']}\n\n")
                    f.write(f"**文件**: `{bug['file']}`\n\n")
                    if bug.get('line'):
                        f.write(f"**行号**: {bug['line']}\n\n")
                    f.write("---\n\n")

            # 警告
            if self.warnings:
                f.write("## ⚠️ 警告 (WARNING)\n\n")
                f.write("**这些是代码质量问题，建议优化。**\n\n")
                for i, warning in enumerate(self.warnings[:20], 1):  # 只显示前20个
                    f.write(f"{i}. {warning['message']}\n")
                    f.write(f"   - 文件: `{warning['file']}`")
                    if warning.get('line'):
                        f.write(f" (行 {warning['line']})")
                    f.write("\n\n")

                if len(self.warnings) > 20:
                    f.write(f"*...还有 {len(self.warnings) - 20} 个警告未显示*\n\n")

            # 信息
            if self.info:
                f.write("## ℹ️ 信息提示 (INFO)\n\n")
                for i, info in enumerate(self.info[:10], 1):  # 只显示前10个
                    f.write(f"{i}. {info['message']}\n")
                    f.write(f"   - 文件: `{info['file']}`\n\n")

                if len(self.info) > 10:
                    f.write(f"*...还有 {len(self.info) - 10} 条信息未显示*\n\n")

        print(f"\n✅ 报告已生成: {report_path}")
        print("\n" + "="*80)

        # 添加修复建议部分
        self.add_fix_suggestions(report_path)

    def add_fix_suggestions(self, report_path: Path):
        """添加修复建议到报告"""
        with open(report_path, 'a', encoding='utf-8') as f:
            f.write("\n## 🔧 修复建议\n\n")

            f.write("### 优先级\n\n")
            f.write("1. **立即修复**: 🔴 严重问题 (CRITICAL) - 阻塞性问题\n")
            f.write("2. **尽快修复**: 🟠 高危问题 (HIGH) - 可能导致运行时错误\n")
            f.write("3. **计划修复**: 🟡 中危问题 (MEDIUM) - 影响稳定性\n")
            f.write("4. **建议优化**: ⚠️ 警告 (WARNING) - 代码质量问题\n\n")

            f.write("### 常见问题修复方案\n\n")

            # 1. 空指针异常
            f.write("#### 1. 空指针异常 (NullPointerException)\n\n")
            f.write("**问题**: 调用方法返回null后直接使用\n\n")
            f.write("```java\n")
            f.write("// ❌ 错误写法\n")
            f.write("User user = userService.getById(id);\n")
            f.write("String name = user.getName();  // 如果user为null会抛异常\n\n")
            f.write("// ✅ 正确写法\n")
            f.write("User user = userService.getById(id);\n")
            f.write("if (user != null) {\n")
            f.write("    String name = user.getName();\n")
            f.write("} else {\n")
            f.write("    throw new BusinessException(\"用户不存在\");\n")
            f.write("}\n")
            f.write("```\n\n")

            # 2. JWT工具类注入问题
            f.write("#### 2. JWT工具类注入问题\n\n")
            f.write("**问题**: JwtUtil使用静态方法但在拦截器中使用@Autowired注入\n\n")
            f.write("```java\n")
            f.write("// ❌ 错误：JwtUtil是工具类，不应该注入\n")
            f.write("@Component\n")
            f.write("public class AuthInterceptor {\n")
            f.write("    @Autowired\n")
            f.write("    private JwtUtil jwtUtil;  // 错误！\n")
            f.write("}\n\n")
            f.write("// ✅ 方案1：直接使用静态方法\n")
            f.write("public class AuthInterceptor {\n")
            f.write("    public boolean preHandle(...) {\n")
            f.write("        String userId = JwtUtil.getUserId(token);\n")
            f.write("    }\n")
            f.write("}\n\n")
            f.write("// ✅ 方案2：将JwtUtil改为Spring Bean\n")
            f.write("@Component\n")
            f.write("public class JwtUtil {\n")
            f.write("    public Long getUserId(String token) { ... }\n")
            f.write("}\n")
            f.write("```\n\n")

            # 3. 登录接口认证问题
            f.write("#### 3. 登录接口被拦截问题\n\n")
            f.write("**问题**: 登录接口没有跳过认证，导致无法登录\n\n")
            f.write("```java\n")
            f.write("// ❌ 错误：登录接口会被拦截器拦截\n")
            f.write("@PostMapping(\"/login\")\n")
            f.write("public Result login(@RequestBody LoginDTO dto) {\n")
            f.write("    return Result.success(userService.login(dto));\n")
            f.write("}\n\n")
            f.write("// ✅ 正确：添加@SkipAuth注解\n")
            f.write("@SkipAuth\n")
            f.write("@PostMapping(\"/login\")\n")
            f.write("public Result login(@RequestBody LoginDTO dto) {\n")
            f.write("    return Result.success(userService.login(dto));\n")
            f.write("}\n")
            f.write("```\n\n")

            # 4. 前端错误处理
            f.write("#### 4. 前端API调用错误处理\n\n")
            f.write("**问题**: API调用没有错误处理\n\n")
            f.write("```javascript\n")
            f.write("// ❌ 错误：没有错误处理\n")
            f.write("const loadData = async () => {\n")
            f.write("  const res = await getProductList()\n")
            f.write("  productList.value = res.data\n")
            f.write("}\n\n")
            f.write("// ✅ 正确：使用try-catch\n")
            f.write("const loadData = async () => {\n")
            f.write("  try {\n")
            f.write("    const res = await getProductList()\n")
            f.write("    productList.value = res.data\n")
            f.write("  } catch (error) {\n")
            f.write("    console.error('加载失败:', error)\n")
            f.write("    uni.showToast({\n")
            f.write("      title: '加载失败，请重试',\n")
            f.write("      icon: 'none'\n")
            f.write("    })\n")
            f.write("  }\n")
            f.write("}\n")
            f.write("```\n\n")

            # 5. SQL注入防护
            f.write("#### 5. SQL注入防护\n\n")
            f.write("**问题**: 使用字符串拼接构建SQL\n\n")
            f.write("```java\n")
            f.write("// ❌ 错误：SQL注入风险\n")
            f.write("String sql = \"SELECT * FROM user WHERE name = '\" + name + \"'\";\n\n")
            f.write("// ✅ 正确：使用MyBatis Plus的Wrapper\n")
            f.write("LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();\n")
            f.write("wrapper.eq(User::getName, name);\n")
            f.write("List<User> users = userMapper.selectList(wrapper);\n")
            f.write("```\n\n")

            f.write("### 下一步行动\n\n")
            f.write("1. 优先修复所有🔴严重问题\n")
            f.write("2. 修复🟠高危问题中的空指针和认证问题\n")
            f.write("3. 运行单元测试验证修复\n")
            f.write("4. 重新运行本检查工具确认问题已解决\n\n")

            f.write("---\n\n")
            f.write("*本报告由自动化Bug检查工具生成*\n")

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

        # 检查后端
        self.check_java_files()

        # 检查前端
        self.check_vue_files()

        # 检查配置文件
        self.check_config_files()

        # 生成报告
        self.generate_report()


def main():
    """主函数"""
    import sys

    # 获取项目根目录
    if len(sys.argv) > 1:
        project_root = sys.argv[1]
    else:
        # 默认使用当前目录的上两级（假设脚本在tools/scripts目录下）
        project_root = Path(__file__).parent.parent.parent

    print(f"📁 项目根目录: {project_root}")

    # 创建检查器并运行
    checker = BugChecker(str(project_root))
    checker.run()


if __name__ == "__main__":
    main()


