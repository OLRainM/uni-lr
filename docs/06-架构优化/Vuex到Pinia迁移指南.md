# Vuex到Pinia迁移指南

> 从Vuex平滑迁移到Pinia

---

## 📋 为什么要迁移到Pinia？

### Pinia的优势

| 特性 | Vuex | Pinia |
|------|------|-------|
| **代码量** | 多 | 少30% |
| **类型支持** | 需要额外配置 | 原生TypeScript支持 |
| **组合式API** | 不支持 | 完美支持 |
| **模块化** | 需要手动配置 | 自动模块化 |
| **DevTools** | 支持 | 更好的支持 |
| **学习曲线** | 陡峭 | 平缓 |
| **包大小** | 较大 | 更小 |

---

## 🔄 核心概念对比

### Vuex vs Pinia

| Vuex | Pinia |
|------|-------|
| state | state (ref) |
| getters | computed |
| mutations | 直接修改state |
| actions | actions (函数) |
| modules | 多个store |

---

## 📝 代码对比

### 1. Store定义

#### Vuex（旧）
```javascript
// store/index.js
import { createStore } from 'vuex'

const store = createStore({
  state: {
    count: 0,
    userInfo: null
  },
  
  getters: {
    doubleCount(state) {
      return state.count * 2
    },
    isLogin(state) {
      return !!state.userInfo
    }
  },
  
  mutations: {
    SET_COUNT(state, count) {
      state.count = count
    },
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
    }
  },
  
  actions: {
    increment({ commit, state }) {
      commit('SET_COUNT', state.count + 1)
    },
    async login({ commit }, credentials) {
      const res = await api.login(credentials)
      commit('SET_USER_INFO', res.data)
    }
  }
})

export default store
```

#### Pinia（新）
```javascript
// stores/user.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // state
  const count = ref(0)
  const userInfo = ref(null)
  
  // getters
  const doubleCount = computed(() => count.value * 2)
  const isLogin = computed(() => !!userInfo.value)
  
  // actions
  function increment() {
    count.value++
  }
  
  async function login(credentials) {
    const res = await api.login(credentials)
    userInfo.value = res.data
  }
  
  return {
    count,
    userInfo,
    doubleCount,
    isLogin,
    increment,
    login
  }
})
```

---

### 2. 在组件中使用

#### Vuex（旧）
```vue
<template>
  <view>
    <view>计数：{{ count }}</view>
    <view>双倍：{{ doubleCount }}</view>
    <view v-if="isLogin">已登录</view>
    <button @click="increment">增加</button>
  </view>
</template>

<script>
import { mapState, mapGetters, mapActions } from 'vuex'

export default {
  computed: {
    ...mapState(['count', 'userInfo']),
    ...mapGetters(['doubleCount', 'isLogin'])
  },
  
  methods: {
    ...mapActions(['increment', 'login'])
  }
}
</script>
```

#### Pinia（新）
```vue
<template>
  <view>
    <view>计数：{{ userStore.count }}</view>
    <view>双倍：{{ userStore.doubleCount }}</view>
    <view v-if="userStore.isLogin">已登录</view>
    <button @click="userStore.increment()">增加</button>
  </view>
</template>

<script setup>
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
</script>
```

---

### 3. 模块化

#### Vuex（旧）
```javascript
// store/modules/user.js
export default {
  namespaced: true,
  state: { ... },
  getters: { ... },
  mutations: { ... },
  actions: { ... }
}

// store/index.js
import user from './modules/user'
import cart from './modules/cart'

const store = createStore({
  modules: {
    user,
    cart
  }
})
```

#### Pinia（新）
```javascript
// stores/user.js
export const useUserStore = defineStore('user', () => {
  // ...
})

// stores/cart.js
export const useCartStore = defineStore('cart', () => {
  // ...
})

// 在组件中使用
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const userStore = useUserStore()
const cartStore = useCartStore()
```

---

## 🚀 迁移步骤

### 步骤1：安装Pinia
```bash
npm install pinia
```

### 步骤2：配置Pinia
```javascript
// src/main.js
import { createPinia } from 'pinia'

const app = createSSRApp(App)
const pinia = createPinia()
app.use(pinia)
```

### 步骤3：创建新的Store

将Vuex的每个模块转换为独立的Pinia Store：

```javascript
// stores/user.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 1. 将state转换为ref
  const token = ref(uni.getStorageSync('token') || '')
  const userInfo = ref(uni.getStorageSync('userInfo') || null)
  
  // 2. 将getters转换为computed
  const isLogin = computed(() => !!token.value)
  
  // 3. 将mutations和actions合并为函数
  function setToken(newToken) {
    token.value = newToken
    uni.setStorageSync('token', newToken)
  }
  
  function setUserInfo(info) {
    userInfo.value = info
    uni.setStorageSync('userInfo', info)
  }
  
  function login(data) {
    setToken(data.token)
    setUserInfo(data.userInfo)
  }
  
  function logout() {
    token.value = ''
    userInfo.value = null
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
  }
  
  // 4. 返回需要暴露的内容
  return {
    token,
    userInfo,
    isLogin,
    setToken,
    setUserInfo,
    login,
    logout
  }
})
```

### 步骤4：更新组件

#### 选项式API组件
```vue
<script>
// 旧的Vuex方式
import { mapState, mapActions } from 'vuex'

export default {
  computed: {
    ...mapState('user', ['userInfo'])
  },
  methods: {
    ...mapActions('user', ['login'])
  }
}
</script>
```

改为：

```vue
<script>
// 新的Pinia方式
import { useUserStore } from '@/stores/user'

export default {
  setup() {
    const userStore = useUserStore()
    
    return {
      userStore
    }
  }
}
</script>

<template>
  <view>{{ userStore.userInfo }}</view>
  <button @click="userStore.login()">登录</button>
</template>
```

#### 组合式API组件
```vue
<script setup>
// 直接使用Pinia
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
</script>

<template>
  <view>{{ userStore.userInfo }}</view>
  <button @click="userStore.login()">登录</button>
</template>
```

### 步骤5：移除Vuex

完成迁移后，可以移除Vuex：

```bash
npm uninstall vuex
```

删除 `src/store` 目录。

---

## 📦 实际迁移示例

### 示例1：用户模块

#### Vuex版本
```javascript
// store/modules/user.js
export default {
  namespaced: true,
  
  state: {
    userInfo: uni.getStorageSync('userInfo') || null,
    token: uni.getStorageSync('token') || ''
  },
  
  mutations: {
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
      uni.setStorageSync('userInfo', userInfo)
    },
    SET_TOKEN(state, token) {
      state.token = token
      uni.setStorageSync('token', token)
    },
    LOGOUT(state) {
      state.userInfo = null
      state.token = ''
      uni.removeStorageSync('userInfo')
      uni.removeStorageSync('token')
    }
  },
  
  actions: {
    login({ commit }, userInfo) {
      commit('SET_USER_INFO', userInfo)
    },
    logout({ commit }) {
      commit('LOGOUT')
    }
  },
  
  getters: {
    isLogin: state => !!state.token
  }
}
```

#### Pinia版本
```javascript
// stores/user.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(uni.getStorageSync('userInfo') || null)
  const token = ref(uni.getStorageSync('token') || '')
  
  const isLogin = computed(() => !!token.value)
  
  function login(data) {
    userInfo.value = data
    uni.setStorageSync('userInfo', data)
  }
  
  function setToken(newToken) {
    token.value = newToken
    uni.setStorageSync('token', newToken)
  }
  
  function logout() {
    userInfo.value = null
    token.value = ''
    uni.removeStorageSync('userInfo')
    uni.removeStorageSync('token')
  }
  
  return {
    userInfo,
    token,
    isLogin,
    login,
    setToken,
    logout
  }
})
```

---

### 示例2：购物车模块

#### Vuex版本
```javascript
// store/modules/cart.js
export default {
  namespaced: true,
  
  state: {
    cartCount: 0,
    selectedAddress: null
  },
  
  mutations: {
    SET_CART_COUNT(state, count) {
      state.cartCount = count
    },
    SET_SELECTED_ADDRESS(state, address) {
      state.selectedAddress = address
    }
  },
  
  actions: {
    updateCartCount({ commit }, count) {
      commit('SET_CART_COUNT', count)
    }
  },
  
  getters: {
    cartCount: state => state.cartCount
  }
}
```

#### Pinia版本
```javascript
// stores/cart.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  const cartList = ref([])
  const selectedAddress = ref(null)
  
  const cartCount = computed(() => 
    cartList.value.reduce((sum, item) => sum + item.quantity, 0)
  )
  
  function updateCartCount(list) {
    cartList.value = list
  }
  
  function setSelectedAddress(address) {
    selectedAddress.value = address
  }
  
  return {
    cartList,
    selectedAddress,
    cartCount,
    updateCartCount,
    setSelectedAddress
  }
})
```

---

## ✅ 迁移检查清单

- [ ] 安装Pinia依赖
- [ ] 在main.js中配置Pinia
- [ ] 创建所有需要的Store
- [ ] 更新所有使用Vuex的组件
- [ ] 测试所有功能正常
- [ ] 移除Vuex依赖
- [ ] 删除旧的store目录
- [ ] 更新文档

---

## 🎯 常见问题

### Q1: 如何在Pinia中使用其他Store？

```javascript
export const useCartStore = defineStore('cart', () => {
  // 在一个Store中使用另一个Store
  const userStore = useUserStore()
  
  function checkout() {
    if (!userStore.isLogin) {
      uni.showToast({ title: '请先登录' })
      return
    }
    // 继续结算逻辑
  }
  
  return { checkout }
})
```

### Q2: 如何在Pinia中使用插件？

```javascript
// main.js
import { createPinia } from 'pinia'

const pinia = createPinia()

// 添加插件
pinia.use(({ store }) => {
  // 为所有store添加属性
  store.$router = router
})

app.use(pinia)
```

### Q3: 如何重置Store状态？

```javascript
export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const userInfo = ref(null)
  
  function $reset() {
    token.value = ''
    userInfo.value = null
  }
  
  return { token, userInfo, $reset }
})

// 使用
userStore.$reset()
```

---

## 📚 参考资源

- [Pinia官方文档](https://pinia.vuejs.org/)
- [从Vuex迁移](https://pinia.vuejs.org/cookbook/migration-vuex.html)
- [组合式API](https://vuejs.org/guide/extras/composition-api-faq.html)

---

**文档版本**：v1.0  
**创建日期**：2025-01-14
