<template>
	<view class="cart-page">
		<view v-if="cartList.length > 0">
			<!-- 购物车列表 -->
			<view class="cart-list">
				<view class="cart-item" v-for="item in cartList" :key="item.id">
					<view class="checkbox-wrapper">
						<checkbox :checked="item.checked" @click="toggleCheck(item)" />
					</view>
					<image :src="item.image" class="product-image" @click="goToDetail(item.productId)"></image>
					<view class="product-info">
						<text class="product-name">{{ item.name }}</text>
						<text class="product-spec">{{ item.spec }}</text>
						<view class="product-footer">
							<text class="product-price">¥{{ item.price }}</text>
							<view class="quantity-control">
								<text class="quantity-btn" :class="{ disabled: item.quantity <= 1 }"
									@click="changeQuantity(item, -1)">-</text>
								<text class="quantity-value">{{ item.quantity }}</text>
								<text class="quantity-btn" :class="{ disabled: item.quantity >= item.stock }"
									@click="changeQuantity(item, 1)">+</text>
							</view>
						</view>
					</view>
					<view class="delete-btn" @click="handleDelete(item)">
						<text class="icon">🗑️</text>
					</view>
				</view>
			</view>

			<!-- 推荐商品 -->
			<view class="recommend-section">
				<view class="section-title">为您推荐</view>
				<view class="recommend-list">
					<view class="recommend-item" v-for="item in recommendList" :key="item.id"
						@click="goToDetail(item.id)">
						<image :src="item.image" class="recommend-image"></image>
						<text class="recommend-name">{{ item.name }}</text>
						<text class="recommend-price">¥{{ item.price }}</text>
					</view>
				</view>
			</view>

			<!-- 底部结算栏 -->
			<view class="bottom-bar">
				<view class="left-section">
					<checkbox :checked="allChecked" @click="toggleAllCheck" />
					<text class="all-check-text">全选</text>
				</view>
				<view class="right-section">
					<view class="total-info">
						<text class="total-label">合计：</text>
						<text class="total-price">¥{{ totalPrice }}</text>
					</view>
					<button class="checkout-btn" :disabled="checkedCount === 0" @click="handleCheckout">
						结算({{ checkedCount }})
					</button>
				</view>
			</view>
		</view>

		<!-- 空购物车 -->
		<view class="empty-cart" v-else>
			<!-- <image src="../../static/images/empty-cart.png" class="empty-image" mode="aspectFit"></image> -->
			<text class="empty-text">购物车空空如也</text>
			<button class="go-shopping-btn" @click="goShopping">去逛逛</button>
		</view>
	</view>
</template>

<script setup>
	import { ref, computed } from 'vue'
	import { onShow } from '@dcloudio/uni-app'
	import { getRecommendProducts } from '@/api/index.js'

	// 购物车列表
	const cartList = ref([])

	// 推荐商品
	const recommendList = ref([])

	// 本地商品图片列表（顺序对应轮换）
	const LOCAL_IMAGES = [
		'/static/images/product1.jpg',
		'/static/images/product2.jpg',
		'/static/images/product3.jpg',
		'/static/images/product4.webp'
	]

	/**
	 * 将外链猫咪图替换为本地商品图
	 * @param {string} imageUrl 原始图片地址
	 * @param {string} name     商品名称（用于关键词匹配）
	 * @param {number} idx      序号（用于兜底轮换）
	 */
	const getLocalImage = (imageUrl, name = '', idx = 0) => {
		// 已有有效本地图片，直接使用
		if (imageUrl && !imageUrl.includes('yzcdn.cn') && !imageUrl.includes('placeholder')) {
			return imageUrl
		}
		const n = name || ''
		if (n.includes('冬虫夏草')) return LOCAL_IMAGES[0]
		if (n.includes('藏红花'))  return LOCAL_IMAGES[2]
		if (n.includes('雪莲'))    return LOCAL_IMAGES[3]
		if (n.includes('红虫草') || n.includes('礼盒')) return LOCAL_IMAGES[1]
		if (n.includes('红景天'))  return '/static/images/product5.jpg'
		// 其余按序号轮换
		return LOCAL_IMAGES[idx % LOCAL_IMAGES.length]
	}
	
	// 加载状态
	const loading = ref(false)

	// 全选状态
	const allChecked = computed(() => {
		return cartList.value.length > 0 && cartList.value.every(item => item.checked)
	})

	// 已选数量
	const checkedCount = computed(() => {
		return cartList.value.filter(item => item.checked).length
	})

	// 总价
	const totalPrice = computed(() => {
		return cartList.value
			.filter(item => item.checked)
			.reduce((sum, item) => sum + parseFloat(item.price) * item.quantity, 0)
			.toFixed(2)
	})

	// 页面显示时加载数据
	onShow(() => {
		loadCartList()
		loadRecommendProducts()
	})

	// 同步购物车到 localStorage
	const saveCartToStorage = () => {
		uni.setStorageSync('cartList', cartList.value)
	}

	// 加载购物车列表（优先读取 localStorage，与商品详情页保持一致）
	const loadCartList = () => {
		if (loading.value) return
		loading.value = true
		try {
			const localCart = uni.getStorageSync('cartList') || []
			cartList.value = localCart.map((item, idx) => ({
				...item,
				image: getLocalImage(item.image, item.name, idx),
				checked: item.checked !== undefined ? item.checked : true,
				stock: item.stock || 99
			}))
			console.log('购物车加载成功，商品数量:', cartList.value.length)
		} catch (e) {
			console.error('加载购物车失败:', e)
			cartList.value = []
		} finally {
			loading.value = false
		}
	}
	
	// 加载推荐商品
	const loadRecommendProducts = async () => {
		try {
			const res = await getRecommendProducts()
			if (res && res.length > 0) {
				// 只取前4个，并对 image 字段做本地图片映射
				recommendList.value = res.slice(0, 4).map((item, idx) => ({
					id: item.id,
					name: item.name,
					price: item.price,
					image: getLocalImage(item.image, item.name, idx)
				}))
			}
		} catch (e) {
			console.error('加载推荐商品失败:', e)
		}
	}

	// 切换单个商品选中状态
	const toggleCheck = (item) => {
		item.checked = !item.checked
		saveCartToStorage()
	}

	// 全选/取消全选
	const toggleAllCheck = () => {
		const checked = !allChecked.value
		cartList.value.forEach(item => {
			item.checked = checked
		})
		saveCartToStorage()
	}

	// 修改数量（直接操作 localStorage，不调 API）
	const changeQuantity = (item, delta) => {
		const newQuantity = item.quantity + delta

		// 检查数量范围
		if (newQuantity < 1 || newQuantity > item.stock) {
			if (newQuantity > item.stock) {
				uni.showToast({
					title: '库存不足',
					icon: 'none'
				})
			}
			return
		}

		// 更新内存数据
		item.quantity = newQuantity
		// 同步到 localStorage
		saveCartToStorage()
		console.log('数量更新成功')
	}

	// 删除商品（直接操作 localStorage，不调 API）
	const handleDelete = (item) => {
		uni.showModal({
			title: '提示',
			content: '确定要删除该商品吗？',
			success: (res) => {
				if (res.confirm) {
					// 通过 productId 或 id 匹配（兼容两种格式）
					const index = cartList.value.findIndex(
						i => i.id === item.id && i.spec === item.spec
					)
					if (index !== -1) {
						cartList.value.splice(index, 1)
						saveCartToStorage()
					}

					uni.showToast({
						title: '删除成功',
						icon: 'success'
					})
					console.log('商品删除成功')
				}
			}
		})
	}

	// 前往商品详情
	const goToDetail = (productId) => {
		uni.navigateTo({
			url: `/pages/product/detail?id=${productId}`
		})
	}

	// 结算
	const handleCheckout = () => {
		if (checkedCount.value === 0) {
			uni.showToast({
				title: '请选择商品',
				icon: 'none'
			})
			return
		}
		
		// 获取所有选中的商品
		const selectedGoods = cartList.value.filter(item => item.checked)
		
		// 转换为结算页面需要的格式
		const checkoutGoods = selectedGoods.map(item => ({
			id: item.productId || item.id,
			cartId: item.id, // 购物车记录ID，用于结算后清空
			name: item.name,
			spec: item.spec || '',
			price: item.price,
			quantity: item.quantity,
			image: item.image
		}))
		
		// 保存到本地存储，供结算页使用
		uni.setStorageSync('checkoutGoods', checkoutGoods)
		
		console.log('准备结算', checkoutGoods.length, '件商品')
		
		// 跳转到结算页面
		uni.navigateTo({
			url: '/pages/order/checkout?from=cart'
		})
	}

	// 去逛逛
	const goShopping = () => {
		uni.switchTab({
			url: '/pages/product/list'
		})
	}
</script>

<style lang="scss" scoped>
	.cart-page {
		min-height: 100vh;
		background: #f5f5f5;
		padding-bottom: 120rpx;
	}

	/* 购物车列表 */
	.cart-list {
		padding: 20rpx 0;
	}

	.cart-item {
		display: flex;
		align-items: center;
		padding: 20rpx;
		background: #fff;
		margin-bottom: 20rpx;
		position: relative;
	}

	.checkbox-wrapper {
		margin-right: 20rpx;
	}

	.product-image {
		width: 160rpx;
		height: 160rpx;
		border-radius: 12rpx;
		margin-right: 20rpx;
		flex-shrink: 0;
	}

	.product-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		min-width: 0;
	}

	.product-name {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 8rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.product-spec {
		font-size: 24rpx;
		color: #999;
		margin-bottom: 20rpx;
	}

	.product-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.product-price {
		font-size: 32rpx;
		font-weight: bold;
		color: $primary-color;
	}

	.quantity-control {
		display: flex;
		align-items: center;
		border: 1rpx solid #eee;
		border-radius: 8rpx;
		overflow: hidden;
	}

	.quantity-btn {
		width: 48rpx;
		height: 48rpx;
		line-height: 48rpx;
		text-align: center;
		background: #f5f5f5;
		font-size: 32rpx;
		color: #333;

		&.disabled {
			opacity: 0.4;
		}
	}

	.quantity-value {
		min-width: 60rpx;
		height: 48rpx;
		line-height: 48rpx;
		text-align: center;
		font-size: 26rpx;
		color: #333;
	}

	.delete-btn {
		position: absolute;
		top: 20rpx;
		right: 20rpx;

		.icon {
			font-size: 40rpx;
		}
	}

	/* 推荐商品 */
	.recommend-section {
		background: #fff;
		padding: 30rpx 20rpx;
		margin-top: 20rpx;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}

	.recommend-list {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 20rpx;
	}

	.recommend-item {
		display: flex;
		flex-direction: column;
	}

	.recommend-image {
		width: 100%;
		height: 280rpx;
		border-radius: 12rpx;
		margin-bottom: 12rpx;
	}

	.recommend-name {
		font-size: 26rpx;
		color: #333;
		margin-bottom: 8rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.recommend-price {
		font-size: 28rpx;
		font-weight: bold;
		color: $primary-color;
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

	.left-section {
		display: flex;
		align-items: center;
	}

	.all-check-text {
		font-size: 28rpx;
		color: #333;
		margin-left: 12rpx;
	}

	.right-section {
		display: flex;
		align-items: center;
	}

	.total-info {
		margin-right: 20rpx;
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

	.checkout-btn {
		padding: 0 48rpx;
		height: 72rpx;
		line-height: 72rpx;
		background: $primary-color;
		color: #fff;
		border-radius: 36rpx;
		font-size: 28rpx;
		border: none;

		&[disabled] {
			opacity: 0.6;
		}
	}

	/* 空购物车 */
	.empty-cart {
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
		margin-bottom: 60rpx;
	}

	.go-shopping-btn {
		width: 300rpx;
		height: 80rpx;
		line-height: 80rpx;
		background: $primary-color;
		color: #fff;
		border-radius: 40rpx;
		font-size: 28rpx;
		border: none;
	}
</style>
