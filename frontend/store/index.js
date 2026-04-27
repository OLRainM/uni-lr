import { createStore } from 'vuex'

const store = createStore({
	state: {
		userInfo: uni.getStorageSync('userInfo') || null,
		token: uni.getStorageSync('token') || '',
		cartCount: 0,
		selectedAddress: null
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
		SET_CART_COUNT(state, count) {
			state.cartCount = count
		},
		SET_SELECTED_ADDRESS(state, address) {
			state.selectedAddress = address
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
		},
		updateCartCount({ commit }, count) {
			commit('SET_CART_COUNT', count)
		}
	},
	getters: {
		isLogin: state => !!state.token,
		userInfo: state => state.userInfo,
		cartCount: state => state.cartCount
	}
})

export default store
