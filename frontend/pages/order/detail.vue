<template>
	<view class="order-detail-page" v-if="orderInfo">
		<!-- 订单状态 -->
		<view class="status-section">
			<text class="status-text">{{ orderInfo.statusText }}</text>
			<view class="status-progress">
				<view class="progress-item" :class="{ active: step >= 1 }">
					<view class="progress-dot"></view>
					<text class="progress-label">提交订单</text>
				</view>
				<view class="progress-line" :class="{ active: step >= 2 }"></view>
				<view class="progress-item" :class="{ active: step >= 2 }">
					<view class="progress-dot"></view>
					<text class="progress-label">付款成功</text>
				</view>
				<view class="progress-line" :class="{ active: step >= 3 }"></view>
				<view class="progress-item" :class="{ active: step >= 3 }">
					<view class="progress-dot"></view>
					<text class="progress-label">商家发货</text>
				</view>
				<view class="progress-line" :class="{ active: step >= 4 }"></view>
				<view class="progress-item" :class="{ active: step >= 4 }">
					<view class="progress-dot"></view>
					<text class="progress-label">确认收货</text>
				</view>
			</view>
		</view>

		<!-- 收货信息 -->
		<view class="address-section">
			<view class="section-title">收货信息</view>
			<view class="address-content">
				<view class="address-row">
					<text class="label">收货人：</text>
					<text class="value">{{ orderInfo.receiverName }}</text>
				</view>
				<view class="address-row">
					<text class="label">联系电话：</text>
					<text class="value">{{ orderInfo.receiverPhone }}</text>
				</view>
				<view class="address-row">
					<text class="label">收货地址：</text>
					<text class="value">{{ orderInfo.receiverAddress }}</text>
				</view>
			</view>
		</view>

		<!-- 商品信息 -->
		<view class="goods-section">
			<view class="section-title">商品信息</view>
			<view class="goods-item" v-for="item in orderInfo.goods" :key="item.id">
				<image :src="item.image" class="goods-image"></image>
				<view class="goods-info">
					<text class="goods-name">{{ item.name }}</text>
					<text class="goods-spec">{{ item.spec }}</text>
					<view class="goods-footer">
						<text class="goods-price">¥{{ item.price }}</text>
						<text class="goods-quantity">x{{ item.quantity }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 订单信息 -->
		<view class="order-info-section">
			<view class="section-title">订单信息</view>
			<view class="info-row">
				<text class="label">订单编号</text>
				<text class="value">{{ orderInfo.orderNo }}</text>
			</view>
			<view class="info-row">
				<text class="label">下单时间</text>
				<text class="value">{{ orderInfo.createTime }}</text>
			</view>
			<view class="info-row">
				<text class="label">支付方式</text>
				<text class="value">{{ orderInfo.payMethod }}</text>
			</view>
			<view class="info-row">
				<text class="label">商品总额</text>
				<text class="value">¥{{ orderInfo.goodsAmount }}</text>
			</view>
			<view class="info-row">
				<text class="label">运费</text>
				<text class="value">¥{{ orderInfo.freight }}</text>
			</view>
			<view class="info-row total">
				<text class="label">实付金额</text>
				<text class="value">¥{{ orderInfo.totalAmount }}</text>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue'
	import { onLoad } from '@dcloudio/uni-app'
	import { getOrderDetail } from '@/api/index.js'

	const orderId = ref('')
	const step = ref(2)
	const orderInfo = ref(null)
	const loading = ref(false)

	onLoad((options) => {
		if (options.id) {
			orderId.value = options.id
			loadOrderDetail()
		}
	})
	
	// 时间格式化函数
	const formatTime = (isoString) => {
		if (!isoString) return ''
		
		try {
			const date = new Date(isoString)
			const year = date.getFullYear()
			const month = String(date.getMonth() + 1).padStart(2, '0')
			const day = String(date.getDate()).padStart(2, '0')
			const hours = String(date.getHours()).padStart(2, '0')
			const minutes = String(date.getMinutes()).padStart(2, '0')
			const seconds = String(date.getSeconds()).padStart(2, '0')
			
			return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
		} catch (e) {
			console.error('时间格式化失败:', e)
			return isoString
		}
	}

	const loadOrderDetail = async () => {
		if (loading.value) return
		loading.value = true
		
		try {
			const res = await getOrderDetail(orderId.value)
			
			if (res) {
				// 后端返回 { order: {...}, items: [...] }
				const order = res.order || res
				const items = res.items || []
				
				// 构建完整收货地址
				const fullAddress = [
					order.receiverProvince,
					order.receiverCity,
					order.receiverDistrict,
					order.receiverAddress
				].filter(Boolean).join('')
				
				// 处理商品列表 - 映射字段名
				const goods = items.map(item => ({
					id: item.id,
					name: item.productName,
					spec: item.specName || '默认',
					price: item.price,
					quantity: item.quantity,
					image: item.image || '/static/images/product1.jpg'
				}))
				
				// 构建订单详情数据
				orderInfo.value = {
					orderNo: order.orderNo,
					statusText: getStatusText(order.status),
					createTime: formatTime(order.createTime),
					payMethod: order.payMethod || '微信支付',
					receiverName: order.receiverName,
					receiverPhone: order.receiverPhone,
					receiverAddress: fullAddress,
					goodsAmount: order.goodsAmount || '0.00',
					freight: order.freight || '0.00',
					totalAmount: order.totalAmount || '0.00',
					goods: goods
				}
				
				// 设置进度步骤
				step.value = getStepByStatus(order.status)
				
				console.log('订单详情加载成功:', orderInfo.value)
			}
		} catch (e) {
			console.error('加载订单详情失败:', e)
			uni.showToast({
				title: '加载失败',
				icon: 'none'
			})
			setTimeout(() => {
				uni.navigateBack()
			}, 1500)
		} finally {
			loading.value = false
		}
	}
	
	// 获取状态文本
	const getStatusText = (status) => {
		// 后端返回数字状态：0-待付款 1-待发货 2-待收货 3-已完成 4-已取消
		const statusMap = {
			0: '待付款',
			1: '待发货',
			2: '待收货',
			3: '已完成',
			4: '已取消'
		}
		return statusMap[status] || '未知状态'
	}
	
	// 根据状态获取进度步骤
	const getStepByStatus = (status) => {
		const stepMap = {
			0: 1,  // 待付款
			1: 2,  // 待发货
			2: 3,  // 待收货
			3: 4,  // 已完成
			4: 1   // 已取消
		}
		return stepMap[status] || 1
	}
</script>

<style lang="scss" scoped>
	.order-detail-page {
		min-height: 100vh;
		background: #f5f5f5;
		padding-bottom: 40rpx;
	}

	.status-section {
		background: linear-gradient(135deg, #8B1A1A 0%, #A03030 100%);
		color: #fff;
		padding: 40rpx 30rpx;
	}

	.status-text {
		font-size: 36rpx;
		font-weight: bold;
		display: block;
		margin-bottom: 40rpx;
	}

	.status-progress {
		display: flex;
		align-items: center;
	}

	.progress-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		flex: 1;

		&.active .progress-dot {
			background: #fff;
			border-color: #fff;
		}

		&.active .progress-label {
			color: #fff;
		}
	}

	.progress-dot {
		width: 20rpx;
		height: 20rpx;
		border-radius: 50%;
		background: rgba(255, 255, 255, 0.3);
		border: 2rpx solid rgba(255, 255, 255, 0.3);
		margin-bottom: 12rpx;
	}

	.progress-label {
		font-size: 20rpx;
		color: rgba(255, 255, 255, 0.6);
	}

	.progress-line {
		flex: 1;
		height: 2rpx;
		background: rgba(255, 255, 255, 0.3);
		margin: 0 10rpx 30rpx;

		&.active {
			background: #fff;
		}
	}

	.section-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		padding: 30rpx;
		border-bottom: 1rpx solid #eee;
	}

	.address-section,
	.goods-section,
	.order-info-section {
		background: #fff;
		margin-bottom: 20rpx;
	}

	.address-content {
		padding: 20rpx 30rpx 30rpx;
	}

	.address-row {
		display: flex;
		margin-bottom: 20rpx;

		&:last-child {
			margin-bottom: 0;
		}
	}

	.address-row .label {
		width: 140rpx;
		font-size: 26rpx;
		color: #999;
	}

	.address-row .value {
		flex: 1;
		font-size: 26rpx;
		color: #333;
	}

	.goods-item {
		display: flex;
		padding: 20rpx 30rpx;
		border-bottom: 1rpx solid #f5f5f5;

		&:last-child {
			border-bottom: none;
		}
	}

	.goods-image {
		width: 160rpx;
		height: 160rpx;
		border-radius: 12rpx;
		margin-right: 20rpx;
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
	}

	.goods-spec {
		font-size: 24rpx;
		color: #999;
		margin-bottom: auto;
	}

	.goods-footer {
		display: flex;
		justify-content: space-between;
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

	.info-row {
		display: flex;
		justify-content: space-between;
		padding: 24rpx 30rpx;
		border-bottom: 1rpx solid #f5f5f5;

		&:last-child {
			border-bottom: none;
		}

		&.total {
			background: #f5f5f5;

			.value {
				font-size: 32rpx;
				font-weight: bold;
				color: $primary-color;
			}
		}
	}

	.info-row .label {
		font-size: 26rpx;
		color: #999;
	}

	.info-row .value {
		font-size: 26rpx;
		color: #333;
	}
</style>
