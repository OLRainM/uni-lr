import request from '@/utils/request'

// ==================== 用户相关 ====================
export const login = (data) => request({ url: '/user/login', method: 'POST', data })
export const testLogin = () => request({ url: '/user/test-login', method: 'POST' })
export const getUserInfo = () => request({ url: '/user/info' })
export const updateUserInfo = (data) => request({ url: '/user/update', method: 'POST', data })

// ==================== 商品相关 ====================
export const getProductList = (params) => request({ url: '/product/list', data: params })
export const getProductDetail = (id) => request({ url: `/product/detail/${id}` })
export const getProductCategories = () => request({ url: '/product/categories' })
export const getRecommendProducts = () => request({ url: '/product/recommend' })

// ==================== 购物车相关 ====================
export const getCartList = () => request({ url: '/cart/list' })
export const addToCart = (data) => request({ url: '/cart/add', method: 'POST', data })
export const updateCart = (data) => request({ url: '/cart/update', method: 'POST', data })
export const deleteCart = (data) => request({ url: '/cart/delete', method: 'POST', data })
export const clearCart = () => request({ url: '/cart/clear', method: 'POST' })

// ==================== 订单相关 ====================
export const createOrder = (data) => request({ url: '/order/create', method: 'POST', data })
export const getOrderList = (params) => request({ url: '/order/list', data: params })
export const getOrderDetail = (id) => request({ url: `/order/detail/${id}` })
export const cancelOrder = (id) => request({ url: `/order/cancel/${id}`, method: 'POST' })
export const confirmOrder = (id) => request({ url: `/order/confirm/${id}`, method: 'POST' })
export const payOrder = (id) => request({ url: `/order/pay/${id}`, method: 'POST' })
export const deleteOrder = (id) => request({ url: `/order/delete/${id}`, method: 'POST' })
export const getOrderCount = () => request({ url: '/order/count' })

// ==================== 地址相关 ====================
export const getAddressList = () => request({ url: '/address/list' })
export const addAddress = (data) => request({ url: '/address/add', method: 'POST', data })
export const updateAddress = (data) => request({ url: '/address/update', method: 'POST', data })
export const deleteAddress = (id) => request({ url: `/address/delete/${id}`, method: 'POST' })
export const setDefaultAddress = (id) => request({ url: `/address/setDefault/${id}`, method: 'POST' })

// ==================== 优惠券相关 ====================
export const getAvailableCoupons = () => request({ url: '/coupon/available' })
export const getMyCoupons = () => request({ url: '/coupon/my' })
export const receiveCoupon = (id) => request({ url: `/coupon/receive/${id}`, method: 'POST' })
export const useCoupon = (id) => request({ url: `/coupon/use/${id}`, method: 'POST' })

// ==================== 支付相关 ====================
export const createPayment = (data) => request({ url: '/payment/create', method: 'POST', data })
export const getPaymentStatus = (orderNo) => request({ url: `/payment/status/${orderNo}` })

// ==================== 知识库相关 ====================
export const getKnowledgeList = (params) => request({ url: '/knowledge/list', data: params })
export const getKnowledgeDetail = (id) => request({ url: `/knowledge/detail/${id}` })

// ==================== 轮播图相关 ====================
export const getBannerList = () => request({ url: '/banner/list' })

// ==================== 文件上传相关 ====================
const BASE_URL = uni.getStorageSync('API_BASE_URL') || 'http://localhost:8080/api'

export const uploadImage = (filePath) => {
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: BASE_URL + '/upload/image',
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': uni.getStorageSync('token') || ''
      },
      success: (res) => {
        const data = JSON.parse(res.data)
        if (data.code === 200) {
          resolve(data.data)
        } else {
          uni.showToast({ title: data.message || '上传失败', icon: 'none' })
          reject(data)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '上传失败', icon: 'none' })
        reject(err)
      }
    })
  })
}
