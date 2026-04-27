import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 地址状态管理
 */
export const useAddressStore = defineStore('address', () => {
  // 状态
  const selectedAddress = ref(null)
  
  // 方法
  function setSelectedAddress(address) {
    selectedAddress.value = address
  }
  
  function clearSelectedAddress() {
    selectedAddress.value = null
  }
  
  return {
    // 状态
    selectedAddress,
    // 方法
    setSelectedAddress,
    clearSelectedAddress
  }
})
