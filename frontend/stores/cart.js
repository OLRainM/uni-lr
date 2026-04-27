import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * 购物车状态管理
 */
export const useCartStore = defineStore('cart', () => {
  // 状态
  const cartList = ref([])
  
  // 计算属性
  const totalCount = computed(() => {
    return cartList.value.reduce((sum, item) => sum + item.quantity, 0)
  })
  
  const totalPrice = computed(() => {
    return cartList.value
      .filter(item => item.checked)
      .reduce((sum, item) => sum + item.price * item.quantity, 0)
  })
  
  const checkedCount = computed(() => {
    return cartList.value.filter(item => item.checked).length
  })
  
  // 方法
  function setCartList(list) {
    cartList.value = list
  }
  
  function addToCart(product) {
    const existItem = cartList.value.find(item => item.productId === product.productId)
    if (existItem) {
      existItem.quantity += product.quantity || 1
    } else {
      cartList.value.push({
        ...product,
        checked: true,
        quantity: product.quantity || 1
      })
    }
  }
  
  function updateQuantity(productId, quantity) {
    const item = cartList.value.find(item => item.productId === productId)
    if (item) {
      item.quantity = quantity
    }
  }
  
  function toggleCheck(productId) {
    const item = cartList.value.find(item => item.productId === productId)
    if (item) {
      item.checked = !item.checked
    }
  }
  
  function toggleCheckAll(checked) {
    cartList.value.forEach(item => {
      item.checked = checked
    })
  }
  
  function removeFromCart(productId) {
    const index = cartList.value.findIndex(item => item.productId === productId)
    if (index > -1) {
      cartList.value.splice(index, 1)
    }
  }
  
  function clearCart() {
    cartList.value = []
  }
  
  return {
    // 状态
    cartList,
    // 计算属性
    totalCount,
    totalPrice,
    checkedCount,
    // 方法
    setCartList,
    addToCart,
    updateQuantity,
    toggleCheck,
    toggleCheckAll,
    removeFromCart,
    clearCart
  }
})
