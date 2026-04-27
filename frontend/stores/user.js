import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * 用户状态管理
 */
export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(uni.getStorageSync('token') || '')
  const userInfo = ref(uni.getStorageSync('userInfo') || null)
  
  // 计算属性
  const isLogin = computed(() => !!token.value)
  
  // 方法
  function setToken(newToken) {
    token.value = newToken
    uni.setStorageSync('token', newToken)
  }
  
  function setUserInfo(info) {
    userInfo.value = info
    uni.setStorageSync('userInfo', info)
  }
  
  function login(loginData) {
    setToken(loginData.token)
    setUserInfo(loginData.userInfo)
  }
  
  function logout() {
    token.value = ''
    userInfo.value = null
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
  }
  
  return {
    // 状态
    token,
    userInfo,
    // 计算属性
    isLogin,
    // 方法
    setToken,
    setUserInfo,
    login,
    logout
  }
})
