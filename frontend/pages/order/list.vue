<template>
	<view class="order-list-page">
		<!-- 订单状态标签 -->
		<view class="status-tabs">
			<view class="tab-item" :class="{ active: currentStatus === '' }" @click="changeStatus('')">
				全部
			</view>
			<view class="tab-item" :class="{ active: currentStatus === 'unpaid' }" @click="changeStatus('unpaid')">
				待付款
			</view>
			<view class="tab-item" :class="{ active: currentStatus === 'unshipped' }"
				@click="changeStatus('unshipped')">
				待发货
			</view>
			<view class="tab-item" :class="{ active: currentStatus === 'shipped' }" @click="changeStatus('shipped')">
				待收货
			</view>
			<view class="tab-item" :class="{ active: currentStatus === 'completed' }"
				@click="changeStatus('completed')">
				已完成
			</view>
		</view>

		<!-- 订单列表 -->
		<view class="order-list">
			<view class="order-item" v-for="item in orderList" :key="item.id" @click="goToDetail(item.id)">
				<view class="order-header">
					<text class="order-no">订单号：{{ item.orderNo }}</text>
					<text class="order-status" :class="item.statusClass">{{ item.statusText }}</text>
				</view>

				<view class="goods-list">
					<view class="goods-item" v-for="goods in item.goods" :key="goods.id">
						<image :src="goods.image" class="goods-image"></image>
						<view class="goods-info">
							<text class="goods-name">{{ goods.name }}</text>
							<text class="goods-spec">{{ goods.spec }}</text>
							<view class="goods-footer">
								<text class="goods-price">¥{{ goods.price }}</text>
								<text class="goods-quantity">x{{ goods.quantity }}</text>
							</view>
						</view>
					</view>
				</view>

				<view class="order-footer">
					<text class="order-total">合计：￥{{ item.totalAmount }}</text>
					<view class="order-actions">
						<button class="action-btn" v-if="item.status === 0 || item.status === 'unpaid'"
							@click.stop="handlePay(item)">去支付</button>
						<button class="action-btn secondary" v-if="item.status === 0 || item.status === 'unpaid'"
							@click.stop="handleCancel(item)">取消订单</button>
						<button class="action-btn secondary" v-if="item.status === 1 || item.status === 'unshipped'"
							@click.stop="handleRemind(item)">提醒发货</button>
						<button class="action-btn" v-if="item.status === 2 || item.status === 'shipped'"
							@click.stop="handleConfirm(item)">确认收货</button>
						<button class="action-btn secondary" v-if="item.status === 2 || item.status === 'shipped'"
							@click.stop="handleLogistics(item)">查看物流</button>
						<button class="action-btn secondary" v-if="item.status === 3 || item.status === 'completed'"
							@click.stop="handleBuyAgain(item)">再次购买</button>
					</view>
				</view>
			</view>
		</view>

		<!-- 空状态 -->
		<view class="empty-state" v-if="orderList.length === 0">
			<!-- <image src="../../static/images/empty-order.png" class="empty-image" mode="aspectFit"></image> -->
			<text class="empty-text">暂无订单</text>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue'
	import { onLoad, onPullDownRefresh, onShow } from '@dcloudio/uni-app'
	import { getOrderList, cancelOrder, confirmOrder, payOrder } from '@/api/index.js'

	// 当前订单状态
	const currentStatus = ref('')

	// 订单列表
	const orderList = ref([])
	
	// 加载状态
	const loading = ref(false)
	const page = ref(1)
	const noMore = ref(false)

	// 全部订单数据（已废弃）
	const allOrders = ref([
		{
			id: 1,
			orderNo: '202501150001',
			status: 'unpaid',
			statusText: '待付款',
			statusClass: 'unpaid',
			totalAmount: '1878.00',
			goods: [
				{
					id: 1,
					name: '特级冬虫夏草 西藏那曲产',
					spec: '5g/盒',
					price: '1280.00',
					quantity: 1,
					image: '/static/images/product1.jpg'
				},
				{
					id: 2,
					name: '精选藏红花 伊朗进口',
					spec: '3g/瓶',
					price: '299.00',
					quantity: 2,
					image: '/static/images/product3.jpg'
				}
			]
		},
		{
			id: 2,
			orderNo: '202501140002',
			status: 'shipped',
			statusText: '待收货',
			statusClass: 'shipped',
			totalAmount: '580.00',
			goods: [
				{
					id: 3,
					name: '天山雪莲花 野生采集',
					spec: '10g/袋',
					price: '580.00',
					quantity: 1,
					image: '/static/images/product4.webp'
				}
			]
		},
		{
			id: 3,
			orderNo: '202501130003',
			status: 'completed',
			statusText: '已完成',
			statusClass: 'completed',
			totalAmount: '168.00',
			goods: [
				{
					id: 4,
					name: '高原红景天 增强免疫',
					spec: '50g/瓶',
					price: '168.00',
					quantity: 1,
					image: '/static/images/product2.jpg'
				}
			]
		},
		{
			id: 4,
			orderNo: '202501120004',
			status: 'unshipped',
			statusText: '待发货',
			statusClass: 'unshipped',
			totalAmount: '680.00',
			goods: [
				{
					id: 5,
					name: '野生藏灵芝',
					spec: '100g/盒',
					price: '680.00',
					quantity: 1,
					image: '/static/images/product2.jpg'
				}
			]
		},
		{
			id: 5,
			orderNo: '202501110005',
			status: 'completed',
			statusText: '已完成',
			statusClass: 'completed',
			totalAmount: '380.00',
			goods: [
				{
					id: 6,
					name: '优质天麻',
					spec: '50g/袋',
					price: '380.00',
					quantity: 1,
					image: '/static/images/product1.jpg'
				}
			]
		}
	])

	// 页面加载
	onLoad((options) => {
		if (options.status !== undefined && options.status !== '') {
			currentStatus.value = options.status
		}
		loadOrderList()
	})

	// 页面显示（仅在数据为空时刷新，避免覆盖状态）
	onShow(() => {
		if (orderList.value.length === 0) {
			page.value = 1
			loadOrderList()
		}
	})

	// 下拉刷新
	onPullDownRefresh(() => {
		loadOrderList()
		setTimeout(() => {
			uni.stopPullDownRefresh()
		}, 1000)
	})

	// 字符串状态 → 数字状态的映射表（前端内部用字符串，后端接受 Integer）
	const statusNumMap = {
		'unpaid': 0,      // 待付款
		'unshipped': 1,   // 待发货
		'shipped': 2,     // 待收货
		'completed': 3,   // 已完成
		'cancelled': 4    // 已取消
	}

	// 切换状态
	const changeStatus = (status) => {
		page.value = 1
		orderList.value = []
		currentStatus.value = status
		loadOrderList()
	}

	// 加载订单列表
	const loadOrderList = async () => {
		if (loading.value) return
		loading.value = true

		try {
			const params = {
				pageNum: page.value,
				pageSize: 10
			}

			// 添加状态筛选：将字符串或数字字符串都转换为 Integer
			if (currentStatus.value !== '' && currentStatus.value !== null && currentStatus.value !== undefined) {
				const s = currentStatus.value
				if (statusNumMap[s] !== undefined) {
					// 字符串状态（unpaid / unshipped 等）→ 数字
					params.status = statusNumMap[s]
				} else {
					// 已经是数字或数字字符串（来自 URL 参数）
					const num = parseInt(s)
					if (!isNaN(num)) {
						params.status = num
					}
				}
			}
			
			const res = await getOrderList(params)
			
			if (res && res.list) {
				// 处理订单数据
				const orders = res.list.map(item => ({
					...item,
					// 添加状态文本和样式
					statusText: getStatusText(item.status),
					statusClass: getStatusClass(item.status),
					// 将后端 items(OrderItem[]) 映射为前端 goods 字段
					goods: (item.items && item.items.length > 0)
						? item.items.map(g => ({
							id: g.id,
							name: g.productName || '商品',
							spec: g.specName || '',
							price: g.price,
							quantity: g.quantity,
							image: g.image || '/static/images/product1.jpg'
						}))
						: [{
							id: item.id,
							name: '订单商品',
							spec: '',
							price: item.totalAmount,
							quantity: 1,
							image: '/static/images/product1.jpg'
						}]
				}))
				
				if (page.value === 1) {
					orderList.value = orders
				} else {
					orderList.value = [...orderList.value, ...orders]
				}
				
				noMore.value = orderList.value.length >= res.total
				console.log('订单加载成功，数量:', orders.length)
			} else {
				if (page.value === 1) {
					orderList.value = []
				}
				noMore.value = true
			}
		} catch (e) {
			console.error('加载订单失败:', e)
			if (page.value === 1) {
				orderList.value = []
			}
			uni.showToast({
				title: '加载失败',
				icon: 'none'
			})
		} finally {
			loading.value = false
		}
	}
	
	// 获取状态文本
	const getStatusText = (status) => {
		const statusMap = {
			0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消',
			unpaid: '待付款', unshipped: '待发货', shipped: '待收货',
			completed: '已完成', cancelled: '已取消'
		}
		return statusMap[status] || '未知状态'
	}

	// 获取状态 CSS 类名（数字→字符串类名）
	const getStatusClass = (status) => {
		const classMap = {
			0: 'unpaid', 1: 'unshipped', 2: 'shipped', 3: 'completed', 4: 'cancelled',
			unpaid: 'unpaid', unshipped: 'unshipped', shipped: 'shipped',
			completed: 'completed', cancelled: 'cancelled'
		}
		return classMap[status] || ''
	}

	// 前往订单详情
	const goToDetail = (orderId) => {
		uni.navigateTo({
			url: `/pages/order/detail?id=${orderId}`
		})
	}

	// 去支付
	const handlePay = (order) => {
		uni.showModal({
			title: '提示',
			content: `确认支付¥${order.totalAmount}？`,
			success: async (res) => {
				if (res.confirm) {
					try {
						await payOrder(order.id)
						uni.showToast({
							title: '支付成功',
							icon: 'success'
						})
						// 重新加载订单列表
						page.value = 1
						orderList.value = []
						loadOrderList()
					} catch (e) {
						console.error('支付失败:', e)
						uni.showToast({
							title: '支付失败，请重试',
							icon: 'none'
						})
					}
				}
			}
		})
	}

	// 取消订单
	const handleCancel = (order) => {
		uni.showModal({
			title: '提示',
			content: '确定要取消订单吗？',
			success: async (res) => {
				if (res.confirm) {
					try {
						await cancelOrder(order.id)
						uni.showToast({
							title: '订单已取消',
							icon: 'success'
						})
						// 重新加载
						page.value = 1
						orderList.value = []
						loadOrderList()
					} catch (e) {
						console.error('取消订单失败:', e)
						uni.showToast({
							title: '取消失败',
							icon: 'none'
						})
					}
				}
			}
		})
	}

	// 确认收货
	const handleConfirm = (order) => {
		uni.showModal({
			title: '提示',
			content: '确认已收到货物吗？',
			success: async (res) => {
				if (res.confirm) {
					try {
						await confirmOrder(order.id)
						uni.showToast({
							title: '确认收货成功',
							icon: 'success'
						})
						// 重新加载
						page.value = 1
						orderList.value = []
						loadOrderList()
					} catch (e) {
						console.error('确认收货失败:', e)
						uni.showToast({
							title: '操作失败',
							icon: 'none'
						})
					}
				}
			}
		})
	}

	// 提醒发货
	const handleRemind = (order) => {
		uni.showToast({
			title: '已提醒商家发货',
			icon: 'success'
		})
	}

	// 查看物流
	const handleLogistics = (order) => {
		uni.showToast({
			title: '查看物流功能',
			icon: 'none'
		})
	}

	// 再次购买
	const handleBuyAgain = (order) => {
		uni.showToast({
			title: '已加入购物车',
			icon: 'success'
		})
	}
</script>

<style lang="scss" scoped>
	.order-list-page {
		min-height: 100vh;
		background: #f5f5f5;
	}

	/* 状态标签 */
	.status-tabs {
		display: flex;
		background: #fff;
		border-bottom: 1rpx solid #eee;
		position: sticky;
		top: 0;
		z-index: 10;
	}

	.tab-item {
		flex: 1;
		text-align: center;
		padding: 24rpx 0;
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
			}
		}
	}

	/* 订单列表 */
	.order-list {
		padding: 20rpx;
	}

	.order-item {
		background: #fff;
		border-radius: 12rpx;
		margin-bottom: 20rpx;
		overflow: hidden;
	}

	.order-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 24rpx 30rpx;
		border-bottom: 1rpx solid #eee;
	}

	.order-no {
		font-size: 24rpx;
		color: #999;
	}

	.order-status {
		font-size: 26rpx;
		font-weight: bold;

		&.unpaid {
			color: #ff9800;
		}

		&.unshipped {
			color: #9c27b0;
		}

		&.shipped {
			color: #2196f3;
		}

		&.completed {
			color: #4caf50;
		}
	}

	.goods-list {
		padding: 20rpx 30rpx;
	}

	.goods-item {
		display: flex;
		margin-bottom: 20rpx;

		&:last-child {
			margin-bottom: 0;
		}
	}

	.goods-image {
		width: 160rpx;
		height: 160rpx;
		border-radius: 12rpx;
		margin-right: 20rpx;
		flex-shrink: 0;
	}

	.goods-info {
		flex: 1;
		display: flex;
		flex-direction: column;
	}

	.goods-name {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 8rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.goods-spec {
		font-size: 24rpx;
		color: #999;
		margin-bottom: auto;
	}

	.goods-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.goods-price {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.goods-quantity {
		font-size: 24rpx;
		color: #999;
	}

	.order-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx 30rpx;
		border-top: 1rpx solid #eee;
	}

	.order-total {
		font-size: 28rpx;
		color: #333;

		&::before {
			content: '合计：';
			color: #999;
		}
	}

	.order-actions {
		display: flex;
		gap: 16rpx;
	}

	.action-btn {
		padding: 0 32rpx;
		height: 56rpx;
		line-height: 56rpx;
		background: $primary-color;
		color: #fff;
		border-radius: 28rpx;
		font-size: 24rpx;
		border: none;

		&.secondary {
			background: #fff;
			color: #666;
			border: 1rpx solid #ddd;
		}
	}

	/* 空状态 */
	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding-top: 200rpx;
	}

	.empty-image {
		width: 400rpx;
		height: 400rpx;
		margin-bottom: 40rpx;
	}

	.empty-text {
		font-size: 28rpx;
		color: #999;
	}
</style>
