<template>
	<view class="coupon-page">
		<!-- 标签栏 -->
		<view class="tabs">
			<view 
				class="tab-item" 
				:class="{ active: currentTab === 'available' }" 
				@click="switchTab('available')"
			>
				可用（{{ availableCoupons.length }}）
			</view>
			<view 
				class="tab-item" 
				:class="{ active: currentTab === 'used' }" 
				@click="switchTab('used')"
			>
				已使用（{{ usedCoupons.length }}）
			</view>
			<view 
				class="tab-item" 
				:class="{ active: currentTab === 'expired' }" 
				@click="switchTab('expired')"
			>
				已过期（{{ expiredCoupons.length }}）
			</view>
		</view>

		<!-- 优惠券列表 -->
		<view class="coupon-list">
			<!-- 可用优惠券 -->
			<view v-if="currentTab === 'available' && availableCoupons.length > 0">
				<view 
					class="coupon-item available" 
					v-for="coupon in availableCoupons" 
					:key="coupon.id"
				>
					<view class="coupon-left">
						<view class="coupon-amount">
							<text class="currency">¥</text>
							<text class="price">{{ coupon.discount }}</text>
						</view>
						<text class="coupon-condition">{{ coupon.desc }}</text>
					</view>
					<view class="coupon-right">
						<text class="coupon-name">{{ coupon.name }}</text>
						<text class="coupon-date">有效期至：{{ coupon.expireDate }}</text>
						<button class="use-btn" @click="useCoupon(coupon)">立即使用</button>
					</view>
				</view>
			</view>

			<!-- 已使用优惠券 -->
			<view v-if="currentTab === 'used' && usedCoupons.length > 0">
				<view 
					class="coupon-item used" 
					v-for="coupon in usedCoupons" 
					:key="coupon.id"
				>
					<view class="coupon-left">
						<view class="coupon-amount">
							<text class="currency">¥</text>
							<text class="price">{{ coupon.discount }}</text>
						</view>
						<text class="coupon-condition">{{ coupon.desc }}</text>
					</view>
					<view class="coupon-right">
						<text class="coupon-name">{{ coupon.name }}</text>
						<text class="coupon-date">已使用</text>
					</view>
					<view class="coupon-status">已使用</view>
				</view>
			</view>

			<!-- 已过期优惠券 -->
			<view v-if="currentTab === 'expired' && expiredCoupons.length > 0">
				<view 
					class="coupon-item expired" 
					v-for="coupon in expiredCoupons" 
					:key="coupon.id"
				>
					<view class="coupon-left">
						<view class="coupon-amount">
							<text class="currency">¥</text>
							<text class="price">{{ coupon.discount }}</text>
						</view>
						<text class="coupon-condition">{{ coupon.desc }}</text>
					</view>
					<view class="coupon-right">
						<text class="coupon-name">{{ coupon.name }}</text>
						<text class="coupon-date">已过期</text>
					</view>
					<view class="coupon-status">已过期</view>
				</view>
			</view>

			<!-- 空状态 -->
			<view class="empty-state" v-if="getCurrentList().length === 0">
				<text class="empty-icon">🎫</text>
				<text class="empty-text">暂无优惠券</text>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { ref, onMounted } from 'vue'
	import { getMyCoupons } from '@/api/index.js'
	
	// 当前选中的标签
	const currentTab = ref('available')
	
	// 优惠券列表
	const availableCoupons = ref([])
	const usedCoupons = ref([])
	const expiredCoupons = ref([])
	
	// 加载状态
	const loading = ref(false)
	
	// 初始化
	onMounted(() => {
		loadCoupons()
	})
	
	// 演示优惠券数据（当后端无数据时展示）
	const demoCoupons = [
		{
			id: 'demo-1',
			name: '新人专享券',
			discount: 30,
			minAmount: 199,
			desc: '满199元可用',
			expireDate: '2025-12-31',
			status: 0
		},
		{
			id: 'demo-2',
			name: '节日特惠券',
			discount: 50,
			minAmount: 399,
			desc: '满399元可用',
			expireDate: '2025-06-30',
			status: 0
		},
		{
			id: 'demo-3',
			name: '满减优惠券',
			discount: 100,
			minAmount: 699,
			desc: '满699元可用',
			expireDate: '2025-03-31',
			status: 1
		},
		{
			id: 'demo-4',
			name: '会员专属券',
			discount: 20,
			minAmount: 150,
			desc: '满150元可用',
			expireDate: '2024-12-31',
			status: 2
		}
	]

	// 加载优惠券
	const loadCoupons = async () => {
		if (loading.value) return
		loading.value = true

		try {
			const res = await getMyCoupons()
			if (res && res.length > 0) {
				// 根据status分类：0-未使用 1-已使用 2-已过期
				const processedCoupons = res.map(item => ({
					id: item.id,
					name: item.name || '优惠券',
					discount: item.discount || 0,
					minAmount: item.minAmount || 0,
					desc: `满${item.minAmount || 0}元可用`,
					expireDate: item.endTime ? item.endTime.split(' ')[0] : '',
					status: item.status
				}))

				// 若所有优惠券 discount 都为0，则补充演示数据
				const allZero = processedCoupons.every(c => !c.discount || c.discount === 0)
				const finalCoupons = allZero ? demoCoupons : processedCoupons

				availableCoupons.value = finalCoupons.filter(item => item.status === 0)
				usedCoupons.value = finalCoupons.filter(item => item.status === 1)
				expiredCoupons.value = finalCoupons.filter(item => item.status === 2)
			} else {
				// 无数据时使用演示数据
				availableCoupons.value = demoCoupons.filter(item => item.status === 0)
				usedCoupons.value = demoCoupons.filter(item => item.status === 1)
				expiredCoupons.value = demoCoupons.filter(item => item.status === 2)
			}
		} catch (e) {
			console.error('加载优惠券失败:', e)
			// 失败时也使用演示数据
			availableCoupons.value = demoCoupons.filter(item => item.status === 0)
			usedCoupons.value = demoCoupons.filter(item => item.status === 1)
			expiredCoupons.value = demoCoupons.filter(item => item.status === 2)
		} finally {
			loading.value = false
		}
	}
	
	// 切换标签
	const switchTab = (tab) => {
		currentTab.value = tab
	}
	
	// 获取当前列表
	const getCurrentList = () => {
		switch (currentTab.value) {
			case 'available':
				return availableCoupons.value
			case 'used':
				return usedCoupons.value
			case 'expired':
				return expiredCoupons.value
			default:
				return []
		}
	}
	
	// 使用优惠券
	const useCoupon = (coupon) => {
		uni.showModal({
			title: '使用优惠券',
			content: `前往商品列表选购满${coupon.minAmount}元的商品？`,
			success: (res) => {
				if (res.confirm) {
					// 跳转到商品列表
					uni.switchTab({
						url: '/pages/product/list'
					})
				}
			}
		})
	}
</script>

<style lang="scss" scoped>
	.coupon-page {
		min-height: 100vh;
		background: #f5f5f5;
	}
	
	/* 标签栏 */
	.tabs {
		display: flex;
		background: #fff;
		border-bottom: 1rpx solid #eee;
	}
	
	.tab-item {
		flex: 1;
		text-align: center;
		padding: 30rpx 0;
		font-size: 28rpx;
		color: #666;
		position: relative;
		
		&.active {
			color: $primary-color;
			font-weight: bold;
			
			&::after {
				content: '';
				position: absolute;
				bottom: 0;
				left: 50%;
				transform: translateX(-50%);
				width: 60rpx;
				height: 4rpx;
				background: $primary-color;
				border-radius: 2rpx;
			}
		}
	}
	
	/* 优惠券列表 */
	.coupon-list {
		padding: 20rpx;
	}
	
	.coupon-item {
		display: flex;
		background: #fff;
		border-radius: 12rpx;
		margin-bottom: 20rpx;
		overflow: hidden;
		position: relative;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
		
		&.used,
		&.expired {
			opacity: 0.6;
		}
	}
	
	.coupon-left {
		width: 240rpx;
		background: linear-gradient(135deg, #8B1A1A 0%, #A03030 100%);
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 30rpx 20rpx;
		position: relative;
		
		&::after {
			content: '';
			position: absolute;
			right: -10rpx;
			top: 50%;
			transform: translateY(-50%);
			width: 20rpx;
			height: 20rpx;
			background: #f5f5f5;
			border-radius: 50%;
		}
	}
	
	.coupon-amount {
		display: flex;
		align-items: baseline;
		color: #fff;
		margin-bottom: 8rpx;
	}

	.currency {
		font-size: 36rpx;
		font-weight: bold;
	}

	.price {
		font-size: 80rpx;
		font-weight: bold;
		line-height: 1;
		text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
	}

	.coupon-condition {
		font-size: 24rpx;
		color: rgba(255, 255, 255, 0.95);
		background: rgba(255, 255, 255, 0.15);
		padding: 4rpx 12rpx;
		border-radius: 20rpx;
		margin-top: 4rpx;
	}
	
	.coupon-right {
		flex: 1;
		padding: 30rpx 20rpx;
		display: flex;
		flex-direction: column;
		justify-content: center;
	}
	
	.coupon-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 12rpx;
	}
	
	.coupon-date {
		font-size: 24rpx;
		color: #999;
		margin-bottom: 16rpx;
	}
	
	.use-btn {
		align-self: flex-start;
		padding: 8rpx 32rpx;
		height: auto;
		line-height: 1.5;
		background: $primary-color;
		color: #fff;
		border-radius: 32rpx;
		font-size: 24rpx;
		border: none;
	}
	
	.coupon-status {
		position: absolute;
		top: 50%;
		right: 40rpx;
		transform: translateY(-50%) rotate(-15deg);
		font-size: 48rpx;
		font-weight: bold;
		color: rgba(0, 0, 0, 0.1);
	}
	
	/* 空状态 */
	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 120rpx 0;
	}
	
	.empty-icon {
		font-size: 120rpx;
		margin-bottom: 20rpx;
		opacity: 0.3;
	}
	
	.empty-text {
		font-size: 28rpx;
		color: #999;
	}
</style>
