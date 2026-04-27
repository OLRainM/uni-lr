// 网络请求封装
// 可通过 storage 中的 API_BASE_URL 覆盖默认地址，便于联调切换环境
const BASE_URL = uni.getStorageSync('API_BASE_URL') || 'http://localhost:8080/api' // 后端API地址

const request = (options) => {
	return new Promise((resolve, reject) => {
		const token = uni.getStorageSync('token') || ''

		uni.request({
			url: BASE_URL + options.url,
			method: options.method || 'GET',
			data: options.data || {},
			timeout: options.timeout || 10000,
			header: {
				'Content-Type': 'application/json',
				'Authorization': token ? `Bearer ${token}` : ''
			},
			success: (res) => {
				if (res.statusCode === 401 || res.data?.code === 401) {
					uni.removeStorageSync('token')
					uni.removeStorageSync('userInfo')
					// 不弹提示，避免多接口同时401时反复弹窗
					reject(res)
					return
				}
				if (res.statusCode === 200) {
					if (res.data.code === 0) {
						resolve(res.data.data)
					} else {
						// 未登录提示只在非静默模式下显示
						if (res.data.message && res.data.message !== '未登录，请先登录') {
							uni.showToast({
								title: res.data.message || '请求失败',
								icon: 'none'
							})
						}
						reject(res.data)
					}
				} else {
					uni.showToast({
						title: '网络请求失败',
						icon: 'none'
					})
					reject(res)
				}
			},
			fail: (err) => {
				uni.showToast({
					title: '网络连接失败',
					icon: 'none'
				})
				reject(err)
			}
		})
	})
}

export default request
