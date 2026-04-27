# Redis缓存与Pinia快速参考

> 快速查阅常用代码和配置

---

## 🔥 Redis缓存注解

### @Cacheable - 查询缓存
```java
import org.springframework.cache.annotation.Cacheable;
import static com.tibetan.medicine.common.CacheConstants.*;

// 单个参数
@Cacheable(value = PRODUCT_CACHE, key = "#id")
public Product getById(Long id) {
    return mapper.selectById(id);
}

// 多个参数
@Cacheable(value = PRODUCT_LIST_CACHE, key = "'list:' + #categoryId + ':' + #pageNum")
public List<Product> getList(Long categoryId, Integer pageNum) {
    return mapper.selectList(categoryId, pageNum);
}

// 条件缓存
@Cacheable(value = PRODUCT_CACHE, key = "#id", condition = "#id > 0")
public Product getById(Long id) {
    return mapper.selectById(id);
}
```

### @CachePut - 更新缓存
```java
import org.springframework.cache.annotation.CachePut;

// 更新后刷新缓存
@CachePut(value = PRODUCT_CACHE, key = "#product.id")
public Product update(Product product) {
    mapper.updateById(product);
    return product;
}
```

### @CacheEvict - 删除缓存
```java
import org.springframework.cache.annotation.CacheEvict;

// 删除单个缓存
@CacheEvict(value = PRODUCT_CACHE, key = "#id")
public void delete(Long id) {
    mapper.deleteById(id);
}

// 删除所有缓存
@CacheEvict(value = PRODUCT_CACHE, allEntries = true)
public void clearAll() {
    // 清空所有商品缓存
}

// 删除多个缓存
@CacheEvict(value = {PRODUCT_CACHE, PRODUCT_LIST_CACHE}, allEntries = true)
public void clearProductCache() {
    // 清空商品相关的所有缓存
}
```

---

## 🎯 Pinia Store使用

### 创建Store
```javascript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useMyStore = defineStore('myStore', () => {
  // 状态（相当于Vuex的state）
  const count = ref(0)
  const name = ref('张三')
  
  // 计算属性（相当于Vuex的getters）
  const doubleCount = computed(() => count.value * 2)
  
  // 方法（相当于Vuex的actions）
  function increment() {
    count.value++
  }
  
  function setName(newName) {
    name.value = newName
  }
  
  // 返回需要暴露的内容
  return {
    count,
    name,
    doubleCount,
    increment,
    setName
  }
})
```

### 在组件中使用
```vue
<template>
  <view>
    <view>计数：{{ myStore.count }}</view>
    <view>双倍：{{ myStore.doubleCount }}</view>
    <button @click="myStore.increment()">增加</button>
  </view>
</template>

<script setup>
import { useMyStore } from '@/stores/myStore'

const myStore = useMyStore()

// 直接访问状态
console.log(myStore.count)

// 直接调用方法
myStore.increment()
myStore.setName('李四')
</script>
```

### 持久化到本地存储
```javascript
export const useUserStore = defineStore('user', () => {
  // 从本地存储初始化
  const token = ref(uni.getStorageSync('token') || '')
  
  function setToken(newToken) {
    token.value = newToken
    // 保存到本地存储
    uni.setStorageSync('token', newToken)
  }
  
  function logout() {
    token.value = ''
    // 清除本地存储
    uni.removeStorageSync('token')
  }
  
  return { token, setToken, logout }
})
```

---

## 📦 常用Store模式

### 用户Store
```javascript
export const useUserStore = defineStore('user', () => {
  const token = ref(uni.getStorageSync('token') || '')
  const userInfo = ref(uni.getStorageSync('userInfo') || null)
  const isLogin = computed(() => !!token.value)
  
  function login(data) {
    token.value = data.token
    userInfo.value = data.userInfo
    uni.setStorageSync('token', data.token)
    uni.setStorageSync('userInfo', data.userInfo)
  }
  
  function logout() {
    token.value = ''
    userInfo.value = null
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
  }
  
  return { token, userInfo, isLogin, login, logout }
})
```

### 购物车Store
```javascript
export const useCartStore = defineStore('cart', () => {
  const cartList = ref([])
  
  const totalCount = computed(() => 
    cartList.value.reduce((sum, item) => sum + item.quantity, 0)
  )
  
  const totalPrice = computed(() => 
    cartList.value
      .filter(item => item.checked)
      .reduce((sum, item) => sum + item.price * item.quantity, 0)
  )
  
  function addToCart(product) {
    const exist = cartList.value.find(item => item.id === product.id)
    if (exist) {
      exist.quantity += product.quantity
    } else {
      cartList.value.push({ ...product, checked: true })
    }
  }
  
  function removeFromCart(productId) {
    const index = cartList.value.findIndex(item => item.id === productId)
    if (index > -1) {
      cartList.value.splice(index, 1)
    }
  }
  
  return { cartList, totalCount, totalPrice, addToCart, removeFromCart }
})
```

---

## 🔧 Redis命令速查

### 连接Redis
```bash
redis-cli
```

### 查看所有key
```bash
keys *
```

### 查看具体缓存
```bash
# 查看字符串值
get product::1

# 查看hash值
hgetall user:1

# 查看列表
lrange product:list 0 -1
```

### 删除缓存
```bash
# 删除单个key
del product::1

# 删除匹配的key
keys product::* | xargs redis-cli del

# 清空所有缓存（慎用！）
flushall
```

### 查看缓存过期时间
```bash
# 查看剩余时间（秒）
ttl product::1

# 设置过期时间
expire product::1 3600
```

---

## 🎨 缓存Key命名规范

### 推荐格式
```
模块:功能:参数
```

### 示例
```java
// 商品详情
"product::1"
"product::2"

// 商品列表
"product:list:category:1:page:1"
"product:list:recommend:10"

// 用户信息
"user::1"
"user::2"

// 分类列表
"category:list"
"category:tree"
```

---

## 📊 性能监控

### 查看Redis状态
```bash
redis-cli info

# 查看内存使用
redis-cli info memory

# 查看连接数
redis-cli info clients

# 查看命中率
redis-cli info stats
```

### 监控缓存命中
```bash
# 实时监控Redis命令
redis-cli monitor
```

---

## ⚠️ 注意事项

### Redis缓存

1. **缓存穿透**：查询不存在的数据
   - 解决：缓存空值，设置较短过期时间

2. **缓存雪崩**：大量缓存同时过期
   - 解决：设置随机过期时间

3. **缓存击穿**：热点数据过期
   - 解决：热点数据永不过期，或使用互斥锁

4. **序列化问题**：实体类需要可序列化
   - 解决：使用Jackson序列化配置

### Pinia状态管理

1. **持久化**：重要数据需要持久化
   - 使用 `uni.setStorageSync` 保存

2. **命名规范**：Store名称使用use开头
   - 如 `useUserStore`、`useCartStore`

3. **响应式**：使用ref和computed
   - 不要直接修改state，使用方法修改

4. **组合式API**：返回需要暴露的内容
   - 只返回需要在组件中使用的状态和方法

---

## 🚀 快速测试

### 测试Redis缓存
```bash
# 1. 启动Redis
redis-server

# 2. 测试接口（第一次）
curl http://localhost:8080/api/product/detail/1

# 3. 测试接口（第二次，应该更快）
curl http://localhost:8080/api/product/detail/1

# 4. 查看缓存
redis-cli
keys *
get product::1
```

### 测试Pinia
```vue
<script setup>
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 测试登录
userStore.login({
  token: 'test-token',
  userInfo: { id: 1, nickname: '测试' }
})

console.log('登录状态:', userStore.isLogin)
console.log('用户信息:', userStore.userInfo)

// 测试登出
userStore.logout()
console.log('登录状态:', userStore.isLogin)
</script>
```

---

**文档版本**：v1.0  
**创建日期**：2025-01-14
