# 藏药小程序 - Bug检查报告

**生成时间**: 2026-04-08 09:42:17

---

## 📊 检查摘要

| 类型 | 数量 | 状态 |
|------|------|------|
| 🔴 严重问题 (CRITICAL) | 0 | ✅ 通过 |
| 🟠 高危问题 (HIGH) | 16 | ⚠️ 需要尽快修复 |
| 🟡 中危问题 (MEDIUM) | 0 | ✅ 通过 |
| ⚠️ 警告 (WARNING) | 38 | 💡 建议优化 |
| ℹ️ 信息 (INFO) | 8 | 📝 参考信息 |

---

## 🟠 高危问题 (HIGH)

**这些问题可能导致运行时错误或安全问题，建议尽快修复。**

### 1. 从Map获取值后未判空

**文件**: `backend\src\main\java\com\tibetan\medicine\controller\CartController.java`

**行号**: 40

**代码**:
```java
Long productId = Long.valueOf(params.get("productId").toString());
```

---

### 2. 从Map获取值后未判空

**文件**: `backend\src\main\java\com\tibetan\medicine\controller\CartController.java`

**行号**: 41

**代码**:
```java
String spec = params.get("spec") != null ? params.get("spec").toString() : null;
```

---

### 3. 从Map获取值后未判空

**文件**: `backend\src\main\java\com\tibetan\medicine\controller\CartController.java`

**行号**: 42

**代码**:
```java
Integer quantity = Integer.valueOf(params.get("quantity").toString());
```

---

### 4. 从Map获取值后未判空

**文件**: `backend\src\main\java\com\tibetan\medicine\controller\CartController.java`

**行号**: 53

**代码**:
```java
Long id = Long.valueOf(params.get("id").toString());
```

---

### 5. 从Map获取值后未判空

**文件**: `backend\src\main\java\com\tibetan\medicine\controller\CartController.java`

**行号**: 54

**代码**:
```java
Integer quantity = Integer.valueOf(params.get("quantity").toString());
```

---

### 6. 从Map获取值后未判空

**文件**: `backend\src\main\java\com\tibetan\medicine\controller\CartController.java`

**行号**: 66

**代码**:
```java
List<Long> ids = (List<Long>) params.get("ids");
```

---

### 7. 从Map获取值后未判空

**文件**: `backend\src\main\java\com\tibetan\medicine\controller\CouponController.java`

**行号**: 55

**代码**:
```java
Long couponId = Long.valueOf(params.get("couponId").toString());
```

---

### 8. 从Map获取值后未判空

**文件**: `backend\src\main\java\com\tibetan\medicine\controller\CouponController.java`

**行号**: 68

**代码**:
```java
Long userCouponId = Long.valueOf(params.get("userCouponId").toString());
```

---

### 9. 从Map获取值后未判空

**文件**: `backend\src\main\java\com\tibetan\medicine\controller\PaymentController.java`

**行号**: 24

**代码**:
```java
Long orderId = Long.valueOf(params.get("orderId").toString());
```

---

### 10. 从Map获取值后未判空

**文件**: `backend\src\main\java\com\tibetan\medicine\controller\PaymentController.java`

**行号**: 25

**代码**:
```java
String paymentMethod = params.get("paymentMethod").toString();  // wechat, alipay
```

---

### 11. 从Map获取值后未判空

**文件**: `backend\src\main\java\com\tibetan\medicine\controller\UserController.java`

**行号**: 31

**代码**:
```java
String code = params.get("code");
```

---

### 12. 从Map获取值后未判空

**文件**: `backend\src\main\java\com\tibetan\medicine\controller\UserController.java`

**行号**: 94

**代码**:
```java
String nickname = params.get("nickname");
```

---

### 13. 从Map获取值后未判空

**文件**: `backend\src\main\java\com\tibetan\medicine\controller\UserController.java`

**行号**: 95

**代码**:
```java
String phone = params.get("phone");
```

---

### 14. 从Map获取值后未判空

**文件**: `backend\src\main\java\com\tibetan\medicine\controller\UserController.java`

**行号**: 96

**代码**:
```java
String avatar = params.get("avatar");
```

---

### 15. 调用getById后未判空，可能导致NullPointerException

**文件**: `backend\src\main\java\com\tibetan\medicine\service\impl\OrderServiceImpl.java`

**行号**: 42

**代码**:
```java
Address address = addressService.getById(dto.getAddressId());
```

---

### 16. wrapper.last()使用字符串拼接，可能存在SQL注入风险

**文件**: `backend\src\main\java\com\tibetan\medicine\service\impl\ProductServiceImpl.java`

**行号**: 85

**代码**:
```java
wrapper.last("LIMIT " + limit);
```

---

## ⚠️ 警告 (WARNING)

**这些是代码质量问题，建议优化。**

1. 空的catch块，异常被静默吞掉
   - 文件: `backend\src\main\java\com\tibetan\medicine\interceptor\AuthInterceptor.java` (行 76)

2. getUserId返回String类型，调用处需要转换为Long，容易出错
   - 文件: `backend\src\main\java\com\tibetan\medicine\util\JwtUtil.java` (行 54)

3. Service包含数据库写操作但未使用@Transactional注解
   - 文件: `backend\src\main\java\com\tibetan\medicine\service\impl\CartServiceImpl.java`

4. Service包含数据库写操作但未使用@Transactional注解
   - 文件: `backend\src\main\java\com\tibetan\medicine\service\impl\OrderItemServiceImpl.java`

5. 分页参数未校验，可能导致非法参数
   - 文件: `backend\src\main\java\com\tibetan\medicine\service\impl\OrderServiceImpl.java` (行 88)

6. 分页参数未校验，可能导致非法参数
   - 文件: `backend\src\main\java\com\tibetan\medicine\service\impl\ProductServiceImpl.java` (行 27)

7. Service包含数据库写操作但未使用@Transactional注解
   - 文件: `backend\src\main\java\com\tibetan\medicine\service\impl\UserServiceImpl.java`

8. v-for缺少:key属性，可能影响性能
   - 文件: `frontend\pages\coupon\list.vue` (行 34)

9. v-for缺少:key属性，可能影响性能
   - 文件: `frontend\pages\coupon\list.vue` (行 56)

10. v-for缺少:key属性，可能影响性能
   - 文件: `frontend\pages\coupon\list.vue` (行 78)

11. API调用未使用try-catch处理错误
   - 文件: `frontend\pages\order\list.vue` (行 238)

12. API调用未使用try-catch处理错误
   - 文件: `frontend\pages\product\list.vue` (行 252)

13. v-for缺少:key属性，可能影响性能
   - 文件: `frontend\node_modules\@dcloudio\uni-ui\lib\uni-data-pickerview\uni-data-pickerview.vue` (行 7)

14. API调用未使用try-catch处理错误
   - 文件: `frontend\node_modules\@dcloudio\uni-ui\lib\uni-file-picker\uni-file-picker.vue` (行 295)

15. API调用未使用try-catch处理错误
   - 文件: `frontend\node_modules\@dcloudio\uni-ui\lib\uni-file-picker\uni-file-picker.vue` (行 394)

16. API调用未使用try-catch处理错误
   - 文件: `frontend\node_modules\@dcloudio\uni-ui\lib\uni-file-picker\uni-file-picker.vue` (行 466)

17. API调用未使用try-catch处理错误
   - 文件: `frontend\node_modules\@dcloudio\uni-ui\lib\uni-file-picker\uni-file-picker.vue` (行 600)

18. v-for缺少:key属性，可能影响性能
   - 文件: `frontend\node_modules\@dcloudio\uni-ui\lib\uni-segmented-control\uni-segmented-control.vue` (行 4)

19. v-for缺少:key属性，可能影响性能
   - 文件: `frontend\node_modules\@dcloudio\uni-ui\lib\uni-swiper-dot\uni-swiper-dot.vue` (行 5)

20. v-for缺少:key属性，可能影响性能
   - 文件: `frontend\node_modules\@dcloudio\uni-ui\lib\uni-swiper-dot\uni-swiper-dot.vue` (行 10)

*...还有 18 个警告未显示*

## ℹ️ 信息提示 (INFO)

1. 建议在onMounted生命周期中加载初始数据
   - 文件: `frontend\node_modules\@dcloudio\uni-ui\lib\uni-data-checkbox\uni-data-checkbox.vue`

2. 建议在onMounted生命周期中加载初始数据
   - 文件: `frontend\node_modules\@dcloudio\uni-ui\lib\uni-data-picker\uni-data-picker.vue`

3. 建议在onMounted生命周期中加载初始数据
   - 文件: `frontend\node_modules\@dcloudio\uni-ui\lib\uni-data-pickerview\uni-data-pickerview.vue`

4. 建议在onMounted生命周期中加载初始数据
   - 文件: `frontend\node_modules\@dcloudio\uni-ui\lib\uni-forms\uni-forms.vue`

5. 建议在onMounted生命周期中加载初始数据
   - 文件: `frontend\node_modules\@dcloudio\uni-ui\lib\uni-forms-item\uni-forms-item.vue`

6. 建议在onMounted生命周期中加载初始数据
   - 文件: `frontend\node_modules\@dcloudio\uni-components\lib-x\unicloud-db\unicloud-db.vue`

7. 数据库配置使用localhost，部署时需要修改
   - 文件: `backend\src\main\resources\application.yml`

8. Redis密码为空，生产环境建议设置密码
   - 文件: `backend\src\main\resources\application.yml`


## 🔧 修复建议

### 优先级

1. **立即修复**: 🔴 严重问题 (CRITICAL) - 阻塞性问题
2. **尽快修复**: 🟠 高危问题 (HIGH) - 可能导致运行时错误
3. **计划修复**: 🟡 中危问题 (MEDIUM) - 影响稳定性
4. **建议优化**: ⚠️ 警告 (WARNING) - 代码质量问题

### 常见问题修复方案

#### 1. 空指针异常 (NullPointerException)

**问题**: 调用方法返回null后直接使用

```java
// ❌ 错误写法
User user = userService.getById(id);
String name = user.getName();  // 如果user为null会抛异常

// ✅ 正确写法
User user = userService.getById(id);
if (user != null) {
    String name = user.getName();
} else {
    throw new BusinessException("用户不存在");
}
```

#### 2. JWT工具类注入问题

**问题**: JwtUtil使用静态方法但在拦截器中使用@Autowired注入

```java
// ❌ 错误：JwtUtil是工具类，不应该注入
@Component
public class AuthInterceptor {
    @Autowired
    private JwtUtil jwtUtil;  // 错误！
}

// ✅ 方案1：直接使用静态方法
public class AuthInterceptor {
    public boolean preHandle(...) {
        String userId = JwtUtil.getUserId(token);
    }
}

// ✅ 方案2：将JwtUtil改为Spring Bean
@Component
public class JwtUtil {
    public Long getUserId(String token) { ... }
}
```

#### 3. 登录接口被拦截问题

**问题**: 登录接口没有跳过认证，导致无法登录

```java
// ❌ 错误：登录接口会被拦截器拦截
@PostMapping("/login")
public Result login(@RequestBody LoginDTO dto) {
    return Result.success(userService.login(dto));
}

// ✅ 正确：添加@SkipAuth注解
@SkipAuth
@PostMapping("/login")
public Result login(@RequestBody LoginDTO dto) {
    return Result.success(userService.login(dto));
}
```

#### 4. 前端API调用错误处理

**问题**: API调用没有错误处理

```javascript
// ❌ 错误：没有错误处理
const loadData = async () => {
  const res = await getProductList()
  productList.value = res.data
}

// ✅ 正确：使用try-catch
const loadData = async () => {
  try {
    const res = await getProductList()
    productList.value = res.data
  } catch (error) {
    console.error('加载失败:', error)
    uni.showToast({
      title: '加载失败，请重试',
      icon: 'none'
    })
  }
}
```

#### 5. SQL注入防护

**问题**: 使用字符串拼接构建SQL

```java
// ❌ 错误：SQL注入风险
String sql = "SELECT * FROM user WHERE name = '" + name + "'";

// ✅ 正确：使用MyBatis Plus的Wrapper
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(User::getName, name);
List<User> users = userMapper.selectList(wrapper);
```

### 下一步行动

1. 优先修复所有🔴严重问题
2. 修复🟠高危问题中的空指针和认证问题
3. 运行单元测试验证修复
4. 重新运行本检查工具确认问题已解决

---

*本报告由自动化Bug检查工具生成*
