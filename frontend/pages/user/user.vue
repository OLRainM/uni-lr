<template>
	<view class="user-page">
		<!-- 用户信息卡片 -->
		<view class="user-card" @click="handleAvatarClick">
			<view class="user-info">
				<image :src="userInfo.avatar || defaultAvatar" class="user-avatar"></image>
				<view class="user-detail">
					<text class="user-name">{{ userInfo.nickname || '点击头像登录' }}</text>
					<text class="user-phone" v-if="userInfo.phone">{{ userInfo.phone }}</text>
					<text class="user-phone" v-if="!isLoggedIn" style="font-size: 22rpx; opacity: 0.8;">点击头像使用测试账号登录</text>
				</view>
			</view>
			<view class="login-status">
				<text v-if="isLoggedIn" class="logout-btn" @click.stop="handleLogout">退出</text>
			</view>
		</view>

		<!-- 订单入口 -->
		<view class="order-section">
			<view class="section-header">
				<text class="section-title">我的订单</text>
				<text class="section-more" @click="goToOrderList('')">全部订单 ›</text>
			</view>
			<view class="order-grid">
				<view class="order-item" @click="goToOrderList('unpaid')">
					<text class="order-icon">💰</text>
					<text class="order-text">待付款</text>
					<text class="order-badge" v-if="orderCount.unpaid > 0">{{ orderCount.unpaid }}</text>
				</view>
				<view class="order-item" @click="goToOrderList('unshipped')">
					<text class="order-icon">📦</text>
					<text class="order-text">待发货</text>
					<text class="order-badge" v-if="orderCount.unshipped > 0">{{ orderCount.unshipped }}</text>
				</view>
				<view class="order-item" @click="goToOrderList('shipped')">
					<text class="order-icon">🚚</text>
					<text class="order-text">待收货</text>
					<text class="order-badge" v-if="orderCount.shipped > 0">{{ orderCount.shipped }}</text>
				</view>
				<view class="order-item" @click="goToOrderList('completed')">
					<text class="order-icon">✅</text>
					<text class="order-text">已完成</text>
				</view>
			</view>
		</view>

		<!-- 功能菜单 -->
		<view class="menu-section">
			<view class="menu-item" @click="goToAddress">
				<text class="menu-icon">📍</text>
				<text class="menu-text">收货地址</text>
				<text class="menu-arrow">›</text>
			</view>
			<view class="menu-item" @click="handleCoupon">
				<text class="menu-icon">🎫</text>
				<text class="menu-text">优惠券</text>
				<text class="menu-badge" v-if="couponCount > 0">{{ couponCount }}</text>
				<text class="menu-arrow">›</text>
			</view>
			<view class="menu-item" @click="handleCustomerService">
				<text class="menu-icon">💬</text>
				<text class="menu-text">客服咨询</text>
				<text class="menu-arrow">›</text>
			</view>
			<view class="menu-item" @click="handleAbout">
				<text class="menu-icon">ℹ️</text>
				<text class="menu-text">关于我们</text>
				<text class="menu-arrow">›</text>
			</view>
		</view>

		<!-- 设置 -->
		<view class="settings-section">
			<view class="menu-item" @click="handleShare">
				<text class="menu-icon">📤</text>
				<text class="menu-text">分享小程序</text>
				<text class="menu-arrow">›</text>
			</view>
			<view class="menu-item" @click="handleSettings">
				<text class="menu-icon">⚙️</text>
				<text class="menu-text">设置</text>
				<text class="menu-arrow">›</text>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { ref, computed } from 'vue'
	import { onShow } from '@dcloudio/uni-app'
	import { testLogin, getUserInfo, getOrderCount, getMyCoupons } from '@/api/index.js'

	const defaultAvatar = '/static/images/avatar-default.jpg'

	const userInfo = ref({
		avatar: '',
		nickname: '',
		phone: '',
	})

	const orderCount = ref({
		unpaid: 0,
		unshipped: 0,
		shipped: 0,
		completed: 0
	})

	const couponCount = ref(0)

	// 是否已登录
	const isLoggedIn = computed(() => !!uni.getStorageSync('token'))

	onShow(() => {
		if (isLoggedIn.value) {
			loadUserData()
		}
	})

	// 点击头像：未登录则测试登录，已登录则不处理
	const handleAvatarClick = async () => {
		if (isLoggedIn.value) {
			return // 已登录，不做处理
		}

		try {
			uni.showLoading({ title: '登录中...' })
			const res = await testLogin()

			if (res && res.token) {
				// 存储 token
				uni.setStorageSync('token', res.token)

				// 存储并立即显示用户信息
				if (res.userInfo) {
					uni.setStorageSync('userInfo', res.userInfo)
					userInfo.value = {
						avatar: res.userInfo.avatar || defaultAvatar,
						nickname: res.userInfo.nickname || '测试用户',
						phone: res.userInfo.phone || ''
					}
				}

				uni.hideLoading()
				uni.showToast({ title: '登录成功', icon: 'success' })

				// 再加载订单/优惠券等数据
				await loadOrderCount()
				await loadCouponCount()
			} else {
				uni.hideLoading()
				uni.showToast({ title: '登录失败，响应数据异常', icon: 'none' })
			}
		} catch (e) {
			uni.hideLoading()
			console.error('测试登录失败:', e)
			uni.showToast({ title: '登录失败，请确认后端已启动', icon: 'none' })
		}
	}

	// 退出登录
	const handleLogout = () => {
		uni.showModal({
			title: '提示',
			content: '确定退出登录吗？',
			success: (res) => {
				if (res.confirm) {
					uni.removeStorageSync('token')
					uni.removeStorageSync('userInfo')
					userInfo.value = { avatar: '', nickname: '', phone: '' }
					orderCount.value = { unpaid: 0, unshipped: 0, shipped: 0, completed: 0 }
					couponCount.value = 0
					uni.showToast({ title: '已退出登录', icon: 'none' })
				}
			}
		})
	}

	const loadUserData = async () => {
		await loadUserInfo()
		await loadOrderCount()
		await loadCouponCount()
	}
	
	// 加载用户信息
	const loadUserInfo = async () => {
		if (!isLoggedIn.value) return
		try {
			const res = await getUserInfo()
			if (res) {
				userInfo.value = {
					avatar: res.avatar || defaultAvatar,
					nickname: res.nickname || res.username || '测试用户',
					phone: res.phone || ''
				}
			}
		} catch (e) {
			console.error('加载用户信息失败:', e)
		}
	}

	// 加载订单统计
	const loadOrderCount = async () => {
		if (!isLoggedIn.value) return
		try {
			const res = await getOrderCount()
			if (res) {
				orderCount.value = {
					unpaid: res.unpaid || 0,
					unshipped: res.unshipped || 0,
					shipped: res.shipped || 0,
					completed: res.completed || 0
				}
			}
		} catch (e) {
			console.error('加载订单统计失败:', e)
		}
	}

	// 加载优惠券数量
	const loadCouponCount = async () => {
		if (!isLoggedIn.value) return
		try {
			const res = await getMyCoupons()
			if (res && Array.isArray(res)) {
				couponCount.value = res.filter(item => item.status === 0).length
			}
		} catch (e) {
			console.error('加载优惠券数量失败:', e)
		}
	}

	const goToOrderList = (status) => {
		// 将字符串状态转换为数字状态（后端需要数字类型）
		const statusMap = {
			'unpaid': 0,      // 待付款
			'unshipped': 1,   // 待发货
			'shipped': 2,     // 待收货
			'completed': 3    // 已完成
		}
		const numStatus = statusMap[status] !== undefined ? statusMap[status] : ''
		uni.navigateTo({
			url: `/pages/order/list?status=${numStatus}`
		})
	}

	const goToAddress = () => {
		uni.navigateTo({
			url: '/pages/user/address'
		})
	}

	const handleCoupon = () => {
		uni.navigateTo({
			url: '/pages/coupon/list'
		})
	}

	const handleCustomerService = () => {
		uni.showModal({
			title: '联系客服',
			content: '客服热线：400-888-8888\n服务时间：周一至周日 9:00-18:00\n微信客服：jinhe_tibetan\n\n如有疑问，欢迎随时联系我们！',
			showCancel: false,
			confirmText: '知道了'
		})
	}

	const handleAbout = () => {
		uni.showModal({
			title: '关于金诃藏药',
			content: '金诃藏药股份有限公司起源于1992年，注册地为青海西宁，总部及生产基地位于西宁生物科技产业园，营销中心位于上海。\n\n公司以"弘扬藏医药，造福全人类"为企业使命，以"生命痊力，金诃藏药"为口号，传承藏医药千年智慧，贡献高原极度生命力，致力成为中国健康事业的标杆企业。',
			showCancel: false,
			confirmText: '知道了'
		})
	}

	const handleShare = () => {
		// #ifdef MP-WEIXIN
		uni.showShareMenu({
			withShareTicket: true,
			menus: ['shareAppMessage', 'shareTimeline']
		})
		// #endif
		// #ifndef MP-WEIXIN
		uni.showModal({
			title: '分享小程序',
			content: '请点击右上角"…"菜单，选择"转发给朋友"或"分享到朋友圈"分享本小程序。',
			showCancel: false,
			confirmText: '知道了'
		})
		// #endif
	}

	const handleSettings = () => {
		uni.showActionSheet({
			itemList: ['清除缓存', '关于版本', '隐私政策'],
			success: (res) => {
				if (res.tapIndex === 0) {
					// 清除缓存
					uni.showModal({
						title: '清除缓存',
						content: '确定清除本地缓存吗？购物车数据也将一并清除。',
						success: (r) => {
							if (r.confirm) {
								uni.clearStorageSync()
								uni.showToast({ title: '缓存已清除', icon: 'success' })
							}
						}
					})
				} else if (res.tapIndex === 1) {
					uni.showModal({
						title: '版本信息',
						content: '当前版本：v1.0.0\n发布日期：2025年\n\n金诃藏药商城小程序',
						showCancel: false,
						confirmText: '知道了'
					})
				} else if (res.tapIndex === 2) {
					uni.showModal({
						title: '隐私政策',
						content: '本小程序严格遵守相关法律法规，仅收集必要的用户信息用于订单服务。我们承诺不向第三方出售您的个人信息。',
						showCancel: false,
						confirmText: '知道了'
					})
				}
			}
		})
	}
</script>

<style lang="scss" scoped>
	.user-page {
		min-height: 100vh;
		background: #f5f5f5;
		padding-bottom: 40rpx;
	}

	.user-card {
		background: linear-gradient(135deg, #8B1A1A 0%, #A03030 100%);
		padding: 60rpx 30rpx 40rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.user-info {
		display: flex;
		align-items: center;
	}

	.user-avatar {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
		margin-right: 24rpx;
		border: 4rpx solid rgba(255, 255, 255, 0.3);
	}

	.user-detail {
		display: flex;
		flex-direction: column;
	}

	.user-name {
		font-size: 36rpx;
		font-weight: bold;
		color: #fff;
		margin-bottom: 8rpx;
	}

	.user-phone {
		font-size: 24rpx;
		color: rgba(255, 255, 255, 0.8);
	}

	.vip-badge {
		padding: 8rpx 20rpx;
		background: rgba(212, 175, 55, 0.9);
		color: #fff;
		border-radius: 20rpx;
		font-size: 22rpx;
	}

	.login-status {
		display: flex;
		align-items: center;
	}

	.logout-btn {
		padding: 10rpx 24rpx;
		background: rgba(255, 255, 255, 0.2);
		color: #fff;
		border-radius: 24rpx;
		font-size: 24rpx;
	}

	.order-section,
	.menu-section,
	.settings-section {
		background: #fff;
		margin-top: 20rpx;
	}

	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 30rpx;
		border-bottom: 1rpx solid #f5f5f5;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.section-more {
		font-size: 24rpx;
		color: #999;
	}

	.order-grid {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		padding: 30rpx 0;
	}

	.order-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		position: relative;
	}

	.order-icon {
		font-size: 48rpx;
		margin-bottom: 12rpx;
	}

	.order-text {
		font-size: 24rpx;
		color: #666;
	}

	.order-badge {
		position: absolute;
		top: -8rpx;
		right: 60rpx;
		min-width: 32rpx;
		height: 32rpx;
		line-height: 32rpx;
		padding: 0 8rpx;
		background: #f44336;
		color: #fff;
		font-size: 20rpx;
		text-align: center;
		border-radius: 16rpx;
	}

	.menu-item {
		display: flex;
		align-items: center;
		padding: 32rpx 30rpx;
		border-bottom: 1rpx solid #f5f5f5;
		position: relative;

		&:last-child {
			border-bottom: none;
		}
	}

	.menu-icon {
		font-size: 44rpx;
		margin-right: 20rpx;
	}

	.menu-text {
		flex: 1;
		font-size: 28rpx;
		color: #333;
	}

	.menu-badge {
		padding: 4rpx 12rpx;
		background: #f44336;
		color: #fff;
		font-size: 20rpx;
		border-radius: 12rpx;
		margin-right: 12rpx;
	}

	.menu-arrow {
		font-size: 24rpx;
		color: #999;
	}
</style>
