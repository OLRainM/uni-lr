<template>
	<view class="checkout-page">
		<!-- 收货地址 -->
		<view class="address-section" @click="goToAddressList">
			<view class="address-content" v-if="selectedAddress">
				<view class="address-header">
					<text class="receiver-name">{{ selectedAddress.name }}</text>
					<text class="receiver-phone">{{ selectedAddress.phone }}</text>
				</view>
				<text class="address-detail">{{ selectedAddress.fullAddress }}</text>
			</view>
			<view class="no-address" v-else>
				<text class="tip">请选择收货地址</text>
			</view>
			<text class="arrow">›</text>
		</view>

		<!-- 商品列表 -->
		<view class="goods-section">
			<view class="goods-item" v-for="item in orderGoods" :key="item.id">
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

		<!-- 配送方式 -->
		<view class="delivery-section">
			<text class="section-label">配送方式</text>
			<text class="section-value">快递配送</text>
		</view>

		<!-- 订单备注 -->
		<view class="remark-section">
			<text class="section-label">订单备注</text>
			<textarea v-model="remark" placeholder="选填，给商家留言" class="remark-input"></textarea>
		</view>

		<!-- 优惠券选择 -->
		<view class="coupon-section" @click="handleSelectCoupon">
			<view class="coupon-left">
				<text class="coupon-icon">🎫</text>
				<text class="coupon-label">优惠券</text>
			</view>
			<view class="coupon-right">
				<text class="coupon-value" v-if="selectedCoupon">
					已选：{{ selectedCoupon.name }}（-¥{{ selectedCoupon.discount }}）
				</text>
				<text class="coupon-count" v-else-if="availableCoupons.length > 0">
					{{ availableCoupons.length }} 张可用
				</text>
				<text class="coupon-none" v-else>暂无可用优惠券</text>
				<text class="arrow">›</text>
			</view>
		</view>

		<!-- 费用明细 -->
		<view class="cost-section">
			<view class="cost-item">
				<text class="cost-label">商品总额</text>
				<text class="cost-value">¥{{ goodsTotal }}</text>
			</view>
			<view class="cost-item">
				<text class="cost-label">运费</text>
				<text class="cost-value">¥{{ freight }}</text>
			</view>
			<view class="cost-item" v-if="discount > 0">
				<text class="cost-label">优惠金额</text>
				<text class="cost-value discount">-¥{{ discount }}</text>
			</view>
		</view>

		<!-- 底部结算栏 -->
		<view class="bottom-bar">
			<view class="total-section">
				<text class="total-label">应付金额：</text>
				<text class="total-price">¥{{ totalAmount }}</text>
			</view>
			<button class="submit-btn" @click="handleSubmit">提交订单</button>
		</view>
	</view>
</template>

<script setup>
	import { ref, computed } from 'vue'
	import { onLoad, onShow } from '@dcloudio/uni-app'
	import { getAddressList, getMyCoupons, createOrder } from '@/api/index.js'

	// 页面来源（detail-商品详情, cart-购物车）
	const fromPage = ref('')
	
	// 选中的地址
	const selectedAddress = ref(null)

	// 订单商品
	const orderGoods = ref([])

	// 订单备注
	const remark = ref('')

	// 运费
	const freight = ref('10.00')

	// 可用优惠券列表
	const availableCoupons = ref([])

	// 选中的优惠券
	const selectedCoupon = ref(null)
	
	// 加载状态
	const loading = ref(false)

	// 优惠金额（根据选中的优惠券计算）
	const discount = computed(() => {
		return selectedCoupon.value ? selectedCoupon.value.discount : '0.00'
	})

	// 商品总额
	const goodsTotal = computed(() => {
		return orderGoods.value
			.reduce((sum, item) => sum + parseFloat(item.price) * item.quantity, 0)
			.toFixed(2)
	})

	// 应付金额
	const totalAmount = computed(() => {
		return (parseFloat(goodsTotal.value) + parseFloat(freight.value) - parseFloat(discount.value)).toFixed(2)
	})

	// 页面加载
	onLoad((options) => {
		console.log('===== 结算页面加载 ======')
		console.log('接收到的参数:', JSON.stringify(options))
		
		// 记录来源页面
		if (options.from) {
			fromPage.value = options.from
			console.log('✅ 来源页面:', fromPage.value)
		} else {
			console.log('⚠️ 未接收到from参数！')
		}
		
		// 从本地存储加载商品数据
		const checkoutGoods = uni.getStorageSync('checkoutGoods')
		if (checkoutGoods && checkoutGoods.length > 0) {
			orderGoods.value = checkoutGoods
			console.log('加载结算商品数量:', checkoutGoods.length)
			console.log('结算商品详细:', JSON.stringify(orderGoods.value))
		} else {
			console.log('⚠️ 本地存储中没有结算商品，使用默认数据')
		}
		
		console.log('=====================')
		
		// 加载默认地址
		loadDefaultAddress()
	})
	
	// 页面显示时加载地址（从地址列表返回时）
	onShow(() => {
		loadSelectedAddress()
	})
	
	// 加载默认地址
	const loadDefaultAddress = async () => {
		try {
			const addressList = await getAddressList()
			if (addressList && addressList.length > 0) {
				// 查找默认地址
				const defaultAddr = addressList.find(item => item.isDefault)
				const addr = defaultAddr || addressList[0]
				
				selectedAddress.value = {
					id: addr.id,
					name: addr.name,
					phone: addr.phone,
					fullAddress: `${addr.province} ${addr.city} ${addr.district} ${addr.detail}`
				}
				console.log('加载默认地址:', selectedAddress.value)
			}
		} catch (e) {
			console.error('加载默认地址失败:', e)
		}
		
		// 加载优惠券
		await loadCoupons()
	}
	
	// 加载优惠券列表
	const loadCoupons = async () => {
		try {
			const res = await getMyCoupons()
			if (res && res.length > 0) {
				// 只显示未使用的优惠券
				availableCoupons.value = res.filter(item => item.status === 0)
				console.log('可用优惠券数量:', availableCoupons.value.length)
			}
		} catch (e) {
			console.error('加载优惠券失败:', e)
		}
	}
	
	// 加载选中的地址（从本地存储）
	const loadSelectedAddress = async () => {
		try {
			const selectedAddrId = uni.getStorageSync('selectedAddressId')
			if (selectedAddrId) {
				const addressList = await getAddressList()
				const addr = addressList.find(item => item.id === selectedAddrId)
				if (addr) {
					selectedAddress.value = {
						id: addr.id,
						name: addr.name,
						phone: addr.phone,
						fullAddress: `${addr.province} ${addr.city} ${addr.district} ${addr.detail}`
					}
					console.log('加载选中的地址:', selectedAddress.value)
					// 清除缓存
					uni.removeStorageSync('selectedAddressId')
				}
			}
		} catch (e) {
			console.error('加载选中地址失败:', e)
		}
	}

	// 前往地址列表
	const goToAddressList = () => {
		uni.navigateTo({
			url: '/pages/user/address?from=checkout'
		})
	}

	// 选择优惠券
	const handleSelectCoupon = () => {
		if (availableCoupons.value.length === 0) {
			uni.showToast({
				title: '暂无可用优惠券',
				icon: 'none'
			})
			return
		}

		// 筛选可用的优惠券（满足使用条件）
		const usableCoupons = availableCoupons.value.filter(coupon => {
			return parseFloat(goodsTotal.value) >= parseFloat(coupon.minAmount)
		})

		if (usableCoupons.length === 0) {
			uni.showToast({
				title: '商品金额不满足优惠券使用条件',
				icon: 'none'
			})
			return
		}

		// 构建优惠券选项列表
		const couponOptions = usableCoupons.map(coupon => {
			return `${coupon.name} - 减¥${coupon.discount} (${coupon.desc})`
		})
		
		// 添加"不使用优惠券"选项
		couponOptions.push('不使用优惠券')

		uni.showActionSheet({
			itemList: couponOptions,
			success: (res) => {
				if (res.tapIndex < usableCoupons.length) {
					// 选中了某个优惠券
					selectedCoupon.value = usableCoupons[res.tapIndex]
					uni.showToast({
						title: `已选择：${selectedCoupon.value.name}`,
						icon: 'success'
					})
				} else {
					// 选择了"不使用优惠券"
					selectedCoupon.value = null
					uni.showToast({
						title: '已取消使用优惠券',
						icon: 'none'
					})
				}
			}
		})
	}

	// 提交订单
	const handleSubmit = () => {
		if (!selectedAddress.value) {
			uni.showToast({
				title: '请选择收货地址',
				icon: 'none'
			})
			return
		}

		uni.showModal({
			title: '提示',
			content: `确认支付¥${totalAmount.value}？`,
			success: (res) => {
				if (res.confirm) {
					handlePayment()
				}
			}
		})
	}

	// 调起支付
	const handlePayment = async () => {
		uni.showLoading({
			title: '创建订单中...'
		})

		try {
			// 调用后端API创建订单
			await submitOrder()
			
			uni.hideLoading()
			uni.showToast({
				title: '下单成功',
				icon: 'success'
			})

			// 跳转到订单列表
			setTimeout(() => {
				uni.redirectTo({
					url: '/pages/order/list'
				})
			}, 1500)
		} catch (e) {
			uni.hideLoading()
			console.error('创建订单失败:', e)
			uni.showToast({
				title: '下单失败，请重试',
				icon: 'none'
			})
		}
	}
	
	// 提交订单到后端
	const submitOrder = async () => {
		try {
			// 构建订单商品列表（补充 productName、image、specName，防止后端字段无默认值报错）
			const items = orderGoods.value.map(item => ({
				productId: item.id || item.productId,
				productName: item.name || '',
				image: item.image || '',
				specName: item.spec || '',
				quantity: item.quantity,
				price: item.price
			}))

			// 校验商品id是否合法
			const invalidItem = items.find(it => !it.productId)
			if (invalidItem) {
				uni.showToast({ title: '商品数据异常，请返回重新选择', icon: 'none' })
				return
			}
			
			// 构建订单数据
			const orderData = {
				addressId: selectedAddress.value.id,
				items: items,
				couponId: selectedCoupon.value ? selectedCoupon.value.id : null,
				remark: remark.value || '',
				totalAmount: totalAmount.value
			}
			
			console.log('提交订单数据:', orderData)
			
			// 调用API创建订单
			const res = await createOrder(orderData)
			
			if (res) {
				console.log('订单创建成功:', res)
				
				// 清除结算商品缓存
				uni.removeStorageSync('checkoutGoods')
			}
		} catch (e) {
			console.error('提交订单失败:', e)
			throw e
		}
	}
	
	// 旧的本地创建订单逻辑（已废弃）
	const createOrderLocal = () => {
		try {
			// 获取现有订单列表
			const orderList = uni.getStorageSync('orderList') || []
			
			// 生成订单号（时间戳 + 随机数）
			const orderNo = `${Date.now()}${Math.floor(Math.random() * 1000)}`
			
			// 生成订单ID（自增）
			const maxId = orderList.length > 0 
				? Math.max(...orderList.map(item => item.id)) 
				: 0
			const orderId = maxId + 1
			
			// 构建订单数据
			const orderData = {
				id: orderId,
				orderNo: orderNo,
				status: 'unshipped',  // 订单状态：unpaid-待付款, unshipped-待发货, shipped-待收货, completed-已完成
				statusText: '待发货',
				statusClass: 'unshipped',
				createTime: new Date().toISOString(),
				payTime: new Date().toISOString(),  // 支付时间（模拟立即支付）
				
				// 收货地址信息
				address: {
					name: selectedAddress.value.name,
					phone: selectedAddress.value.phone,
					fullAddress: selectedAddress.value.fullAddress
				},
				
				// 商品列表
				goods: orderGoods.value.map(item => ({
					id: item.id,
					name: item.name,
					spec: item.spec,
					price: item.price,
					quantity: item.quantity,
					image: item.image
				})),
				
				// 费用信息
				goodsTotal: goodsTotal.value,
				freight: freight.value,
				discount: discount.value,
				totalAmount: totalAmount.value,
				
				// 优惠券信息
				coupon: selectedCoupon.value ? {
					id: selectedCoupon.value.id,
					name: selectedCoupon.value.name,
					discount: selectedCoupon.value.discount
				} : null,
				
				// 订单备注
				remark: remark.value || ''
			}
			
			// 添加到订单列表
			orderList.unshift(orderData)  // 添加到开头，最新订单在前
			
			// 保存到本地存储
			uni.setStorageSync('orderList', orderList)
			
			console.log('订单创建成功:', orderData)
			
			// 如果是从购物车来的，清理购物车中已购买的商品
			if (fromPage.value === 'cart') {
				clearCartItems()
			}
			
		} catch (e) {
			console.error('创建订单失败:', e)
			uni.showToast({
				title: '订单创建失败',
				icon: 'none'
			})
		}
	}
	
	// 清理购物车中已购买的商品
	const clearCartItems = () => {
		try {
			console.log('===== 开始清理购物车 ======')
			
			// 获取购物车列表（从本地存储）
			const cartList = uni.getStorageSync('cartList') || []
			console.log('当前购物车数量:', cartList.length)
			console.log('购物车完整数据:', JSON.stringify(cartList))
			
			if (cartList.length === 0) {
				console.log('购物车为空，无需清理')
				return
			}
			
			// 获取已购买的商品ID列表（注意：orderGoods中的id实际是productId）
			// 关键修复：确保转换为数字类型，避免字符串和数字比较失败
			const purchasedIds = orderGoods.value.map(item => {
				const id = typeof item.id === 'string' ? parseInt(item.id) : item.id
				console.log(`订单商品 [${item.name}] - 原始id: ${item.id} (类型: ${typeof item.id}), 转换后: ${id} (类型: ${typeof id})`)
				return id
			})
			console.log('已购买的商品ID列表:', purchasedIds)
			console.log('订单商品完整数据:', JSON.stringify(orderGoods.value))
			
			// 过滤掉已购买的商品
			// 比较逻辑：购物车的 productId 不在已购买ID列表中的才保留
			const remainingCart = cartList.filter(item => {
				// 兼容处理：如果没有productId，尝试使用id
				let itemProductId = item.productId || item.id
				
				// 关键修复：确保转换为数字类型进行比较
				if (typeof itemProductId === 'string') {
					itemProductId = parseInt(itemProductId)
					console.log(`⚠️ 商品 [${item.name}] productId是字符串，已转换为数字: ${itemProductId}`)
				}
				
				if (!item.productId) {
					console.log(`⚠️ 商品 [${item.name}] 缺少productId字段，使用id: ${item.id}`)
				}
				
				const shouldKeep = !purchasedIds.includes(itemProductId)
				
				console.log(`商品 [${item.name}]:`)
				console.log(`  - productId: ${item.productId} (类型: ${typeof item.productId})`)
				console.log(`  - id: ${item.id} (类型: ${typeof item.id})`)
				console.log(`  - 匹配用: ${itemProductId} (类型: ${typeof itemProductId})`)
				console.log(`  - 在已购买列表中: ${purchasedIds.includes(itemProductId)}`)
				console.log(`  - 是否保留: ${shouldKeep}`)
				
				return shouldKeep
			})
			
			console.log('清理前购物车数量:', cartList.length)
			console.log('清理后购物车数量:', remainingCart.length)
			console.log('删除了', cartList.length - remainingCart.length, '件商品')
			
			// 保存更新后的购物车
			uni.setStorageSync('cartList', remainingCart)
			console.log('购物车已更新并保存到本地存储')
			
			// 清除结算商品缓存
			uni.removeStorageSync('checkoutGoods')
			console.log('结算商品缓存已清除')
			
			console.log('===== 购物车清理完成 ======')
			
			// 显示提示
			if (cartList.length - remainingCart.length > 0) {
				uni.showToast({
					title: `已从购物车移除${cartList.length - remainingCart.length}件商品`,
					icon: 'success',
					duration: 2000
				})
			}
			
		} catch (e) {
			console.error('清理购物车失败:', e)
			uni.showToast({
				title: '清理购物车失败',
				icon: 'none'
			})
		}
	}
</script>

<style lang="scss" scoped>
	.checkout-page {
		min-height: 100vh;
		background: #f5f5f5;
		padding-bottom: 120rpx;
	}

	/* 收货地址 */
	.address-section {
		display: flex;
		align-items: center;
		padding: 30rpx;
		background: #fff;
		margin-bottom: 20rpx;
		position: relative;
	}

	.address-content {
		flex: 1;
	}

	.address-header {
		margin-bottom: 12rpx;
	}

	.receiver-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-right: 20rpx;
	}

	.receiver-phone {
		font-size: 28rpx;
		color: #666;
	}

	.address-detail {
		display: block;
		font-size: 26rpx;
		color: #666;
		line-height: 1.5;
	}

	.no-address {
		flex: 1;

		.tip {
			font-size: 28rpx;
			color: #999;
		}
	}

	.arrow {
		margin-left: 20rpx;
		font-size: 28rpx;
		color: #999;
	}

	/* 商品列表 */
	.goods-section {
		background: #fff;
		padding: 30rpx;
		margin-bottom: 20rpx;
	}

	.goods-item {
		display: flex;
		margin-bottom: 30rpx;

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
		color: $primary-color;
	}

	.goods-quantity {
		font-size: 24rpx;
		color: #999;
	}

	/* 配送方式 */
	.delivery-section {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 30rpx;
		background: #fff;
		margin-bottom: 20rpx;
	}

	.section-label {
		font-size: 28rpx;
		color: #333;
	}

	.section-value {
		font-size: 28rpx;
		color: #666;
	}

	/* 订单备注 */
	.remark-section {
		background: #fff;
		padding: 30rpx;
		margin-bottom: 20rpx;
	}

	.remark-input {
		width: 100%;
		min-height: 120rpx;
		margin-top: 20rpx;
		padding: 20rpx;
		background: #f5f5f5;
		border-radius: 8rpx;
		font-size: 26rpx;
	}

	/* 优惠券选择 */
	.coupon-section {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 30rpx;
		background: #fff;
		margin-bottom: 20rpx;
	}

	.coupon-left {
		display: flex;
		align-items: center;
	}

	.coupon-icon {
		font-size: 36rpx;
		margin-right: 12rpx;
	}

	.coupon-label {
		font-size: 28rpx;
		color: #333;
	}

	.coupon-right {
		display: flex;
		align-items: center;
	}

	.coupon-value {
		font-size: 26rpx;
		color: $primary-color;
		font-weight: bold;
		margin-right: 8rpx;
	}

	.coupon-count {
		font-size: 26rpx;
		color: $primary-color;
		margin-right: 8rpx;
	}

	.coupon-none {
		font-size: 26rpx;
		color: #999;
		margin-right: 8rpx;
	}

	/* 费用明细 */
	.cost-section {
		background: #fff;
		padding: 30rpx;
	}

	.cost-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;

		&:last-child {
			margin-bottom: 0;
		}
	}

	.cost-label {
		font-size: 28rpx;
		color: #666;
	}

	.cost-value {
		font-size: 28rpx;
		color: #333;

		&.discount {
			color: $primary-color;
		}
	}

	/* 底部结算栏 */
	.bottom-bar {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 20rpx;
		background: #fff;
		border-top: 1rpx solid #eee;
		z-index: 100;
	}

	.total-section {
		flex: 1;
	}

	.total-label {
		font-size: 26rpx;
		color: #666;
	}

	.total-price {
		font-size: 36rpx;
		font-weight: bold;
		color: $primary-color;
	}

	.submit-btn {
		padding: 0 48rpx;
		height: 72rpx;
		line-height: 72rpx;
		background: $primary-color;
		color: #fff;
		border-radius: 36rpx;
		font-size: 28rpx;
		border: none;
	}
</style>
