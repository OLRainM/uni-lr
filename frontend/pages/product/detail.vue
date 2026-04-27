<template>
	<view class="product-detail-page">
		<!-- 商品图片轮播 -->
		<swiper class="product-swiper" :indicator-dots="true" :autoplay="false">
			<swiper-item v-for="(item, index) in product.images" :key="index">
				<image :src="item" class="swiper-image" mode="aspectFill" @click="previewImage(index)"></image>
			</swiper-item>
		</swiper>

		<!-- 商品基本信息 -->
		<view class="product-basic-info">
			<view class="price-box">
				<text class="price">¥{{ product.price }}</text>
				<text class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</text>
			</view>
			<view class="product-title">
				<text class="name">{{ product.name }}</text>
				<text class="share-btn" @click="handleShare">分享</text>
			</view>
			<view class="product-subtitle">{{ product.subtitle }}</view>
			<view class="product-tags">
				<text class="tag">已售{{ product.sales }}</text>
				<text class="tag">好评率{{ product.rating }}%</text>
			</view>
		</view>

		<!-- 规格选择 -->
		<view class="spec-selector" @click="openSpecPopup">
			<text class="label">规格</text>
			<text class="value">{{ selectedSpec || '请选择规格' }}</text>
			<text class="arrow">></text>
		</view>

		<!-- 商品详情标签页 -->
		<view class="detail-tabs">
			<view class="tab-header">
				<view class="tab-item" :class="{ active: currentTab === 'detail' }" @click="currentTab = 'detail'">
					商品详情
				</view>
				<view class="tab-item" :class="{ active: currentTab === 'params' }" @click="currentTab = 'params'">
					产品参数
				</view>
				<view class="tab-item" :class="{ active: currentTab === 'reviews' }" @click="currentTab = 'reviews'">
					用户评价({{ product.reviewCount }})
				</view>
			</view>

			<!-- 商品详情 -->
			<view class="tab-content" v-if="currentTab === 'detail'">
				<rich-text :nodes="product.detail"></rich-text>
			</view>

			<!-- 产品参数 -->
			<view class="tab-content params-content" v-if="currentTab === 'params'">
				<view class="param-item" v-for="(item, index) in product.params" :key="index">
					<text class="param-label">{{ item.label }}</text>
					<text class="param-value">{{ item.value }}</text>
				</view>
			</view>

			<!-- 用户评价 -->
			<view class="tab-content reviews-content" v-if="currentTab === 'reviews'">
				<view class="review-item" v-for="(item, index) in reviews" :key="index">
					<view class="review-header">
						<image :src="item.avatar" class="user-avatar"></image>
						<view class="user-info">
							<text class="user-name">{{ item.userName }}</text>
							<view class="rating-stars">
								<text v-for="i in 5" :key="i">{{ i <= item.rating ? '★' : '☆' }}</text>
							</view>
						</view>
						<text class="review-date">{{ item.date }}</text>
					</view>
					<text class="review-content">{{ item.content }}</text>
					<view class="review-images" v-if="item.images && item.images.length">
						<image v-for="(img, idx) in item.images" :key="idx" :src="img" class="review-img"
							@click="previewReviewImage(item.images, idx)"></image>
					</view>
				</view>
			</view>
		</view>

		<!-- 底部操作栏 -->
		<view class="bottom-bar">
			<view class="left-actions">
				<view class="action-btn" @click="goToHome">
					<text class="icon">🏠</text>
					<text class="text">首页</text>
				</view>
				<view class="action-btn" @click="goToCart">
					<text class="icon">🛒</text>
					<text class="text">购物车</text>
					<text class="badge" v-if="cartCount > 0">{{ cartCount }}</text>
				</view>
				<view class="action-btn" @click="contactService">
					<text class="icon">💬</text>
					<text class="text">客服</text>
				</view>
			</view>
			<view class="right-actions">
				<button class="add-cart-btn" @click="handleAddToCart">加入购物车</button>
				<button class="buy-now-btn" @click="handleBuyNow">立即购买</button>
			</view>
		</view>

		<!-- 规格选择弹窗 -->
		<uni-popup ref="specPopup" type="bottom">
			<view class="spec-popup">
				<view class="spec-header">
					<image :src="product.images[0]" class="spec-image"></image>
					<view class="spec-info">
						<text class="spec-price">¥{{ product.price }}</text>
						<text class="spec-stock">库存：{{ product.stock }}</text>
						<text class="spec-selected" v-if="selectedSpec">已选：{{ selectedSpec }}</text>
					</view>
					<text class="close-btn" @click="closeSpecPopup">✕</text>
				</view>
				<view class="spec-content">
					<view class="spec-group">
						<text class="spec-title">规格</text>
						<view class="spec-options">
							<view class="spec-option" :class="{ active: selectedSpec === item, disabled: item.stock <= 0 }"
								v-for="item in specList" :key="item.id" @click="selectSpec(item)">
								{{ item.name }}
							</view>
						</view>
					</view>
					<view class="spec-group">
						<text class="spec-title">数量</text>
						<view class="quantity-selector">
							<text class="quantity-btn" :class="{ disabled: quantity <= 1 }"
								@click="changeQuantity(-1)">-</text>
							<input type="number" v-model="quantity" class="quantity-input" />
							<text class="quantity-btn" :class="{ disabled: quantity >= product.stock }"
								@click="changeQuantity(1)">+</text>
						</view>
					</view>
				</view>
				<view class="spec-footer">
					<button class="add-cart-btn-popup" @click="handleAddToCartFromPopup">加入购物车</button>
					<button class="buy-now-btn-popup" @click="handleBuyNowFromPopup">立即购买</button>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script setup>
	import { ref, computed } from 'vue'
	import { onLoad, onShow } from '@dcloudio/uni-app'
	import { getProductDetail, addToCart, getCartList } from '@/api/index.js'

	// 商品ID
	const productId = ref('')

	// 商品信息
	const product = ref({
		id: '',
		name: '',
		subtitle: '',
		price: '0.00',
		originalPrice: '',
		sales: 0,
		rating: 98,
		reviewCount: 0,
		stock: 0,
		images: [],
		detail: '',
		params: []
	})

	// 规格列表（从商品specs字段解析）
	const specList = ref([])

	// 选中的规格
	const selectedSpec = ref('')

	// 购买数量
	const quantity = ref(1)

	// 当前标签页
	const currentTab = ref('detail')

	// 用户评价列表
	const reviews = ref([
		{
			avatar: '/static/images/product1.jpg',
			userName: '用户***123',
			rating: 5,
			date: '2025-01-15',
			content: '品质很好，正品保证，包装精美，发货速度快。',
			images: ['/static/images/product1.jpg', '/static/images/product3.jpg']
		},
		{
			avatar: '/static/images/product2.jpg',
			userName: '用户***456',
			rating: 4,
			date: '2025-01-10',
			content: '收到货了，看起来不错，准备送给长辈。',
			images: []
		}
	])

	// 规格弹窗的 ref
	const specPopup = ref(null)
	// 规格弹窗显示状态（用于规格选择器点击）
	const showSpecPopup = ref(false)

	// 购物车数量
	const cartCount = ref(0)
	
	// 加载购物车数量
	const loadCartCount = () => {
		try {
			// 从本地存储读取购物车数据
			const cartList = uni.getStorageSync('cartList') || []
			// 计算总数量
			cartCount.value = cartList.reduce((total, item) => total + (item.quantity || 0), 0)
			console.log('购物车加载成功：商品数量：', cartCount.value)
		} catch (e) {
			console.error('加载购物车数量失败:', e)
			cartCount.value = 0
		}
	}

	// 页面加载
	onLoad((options) => {
		if (options.id) {
			productId.value = options.id
			loadProductDetail()
		}
		loadCartCount()
	})
	
	// 页面显示
	onShow(() => {
		loadCartCount()
	})

	// 加载状态
	const loading = ref(false)
	
	// 加载商品详情
	const loadProductDetail = async () => {
		if (!productId.value || loading.value) return
		
		loading.value = true
		try {
			const res = await getProductDetail(productId.value)
			if (res) {
				// 处理图片数组
				let images = []
				if (res.image) images.push(res.image)
				if (res.images) {
					try {
						const imageArray = JSON.parse(res.images)
						if (Array.isArray(imageArray)) {
							images = images.concat(imageArray)
						}
					} catch (e) {
						console.warn('解析images失败', e)
					}
				}
				if (images.length === 0) {
					images = ['/static/images/product1.jpg']
				}
				
				// 处理规格
				console.log('原始specs数据:', res.specs)
				if (res.specs) {
					try {
						const specs = JSON.parse(res.specs)
						console.log('解析后的specs:', specs, '是否为数组:', Array.isArray(specs), '长度:', specs.length)
						if (Array.isArray(specs) && specs.length > 0) {
							specList.value = specs
							console.log('使用商品规格:', specList.value)
						} else {
							// 如果规格为空数组，使用默认规格
							specList.value = [{ name: '默认', stock: res.stock || 999 }]
							selectedSpec.value = '默认'
							console.log('使用默认规格（空数组）:', specList.value)
						}
					} catch (e) {
						console.warn('解析specs失败', e)
						specList.value = [{ name: '默认', stock: res.stock || 999 }]
						selectedSpec.value = '默认'
						console.log('使用默认规格（解析失败）:', specList.value)
					}
				} else {
					// 如果没有specs字段，使用默认规格
					specList.value = [{ name: '默认', stock: res.stock || 999 }]
					selectedSpec.value = '默认'
					console.log('使用默认规格（无specs字段）:', specList.value)
				}
				console.log('最终规格列表:', specList.value, '选中规格:', selectedSpec.value)
				
				// 处理产品参数
				let params = []
				if (res.params) {
					try {
						const parsed = JSON.parse(res.params)
						// 确保params是数组
						params = Array.isArray(parsed) ? parsed : []
					} catch (e) {
						console.warn('解析params失败', e)
						params = []
					}
				}
				// 添加基本参数
				if (res.origin) params.push({ label: '产地', value: res.origin })
				if (res.shelfLife) params.push({ label: '保质期', value: res.shelfLife })
				
				// 更新商品数据
				product.value = {
					id: res.id,
					name: res.name,
					subtitle: res.subtitle || '',
					price: res.price,
					originalPrice: res.originalPrice || '',
					sales: res.sales || 0,
					rating: 98,
					reviewCount: 0,
					stock: res.stock || 0,
					images: images,
					detail: res.detail || '<p>暂无详情</p>',
					params: params
				}
			}
		} catch (e) {
			console.error('加载商品详情失败:', e)
			uni.showToast({
				title: '加载失败，请重试',
				icon: 'none'
			})
		} finally {
			loading.value = false
		}
	}

	// 预览商品图片
	const previewImage = (index) => {
		uni.previewImage({
			urls: product.value.images,
			current: index
		})
	}

	// 预览评价图片
	const previewReviewImage = (images, index) => {
		uni.previewImage({
			urls: images,
			current: index
		})
	}

	// 分享
	const handleShare = () => {
		uni.showShareMenu({
			withShareTicket: true
		})
	}

	// 选择规格
	const selectSpec = (spec) => {
		if (spec.stock > 0) {
			selectedSpec.value = spec.name
			product.value.price = spec.price
			product.value.stock = spec.stock
		}
	}

	// 修改数量
	const changeQuantity = (delta) => {
		const newQuantity = quantity.value + delta
		if (newQuantity >= 1 && newQuantity <= product.value.stock) {
			quantity.value = newQuantity
		}
	}

	// 打开规格弹窗
	const openSpecPopup = () => {
		if (specPopup.value) {
			specPopup.value.open()
		}
	}

	// 关闭规格弹窗
	const closeSpecPopup = () => {
		if (specPopup.value) {
			specPopup.value.close()
		}
	}

	// 从弹窗中加入购物车
	const handleAddToCartFromPopup = () => {
		// 如果只有一个默认规格，自动选中
		if (!selectedSpec.value && specList.value.length === 1) {
			selectedSpec.value = specList.value[0].name
		}
		
		// 如果有多个规格但没选择，提示选择
		if (!selectedSpec.value && specList.value.length > 1) {
			uni.showToast({
				title: '请选择规格',
				icon: 'none'
			})
			return
		}
		
		// 构建商品数据
		const goodsData = {
			id: product.value.id,
			productId: product.value.id,
			name: product.value.name,
			spec: selectedSpec.value,
			price: product.value.price,
			quantity: quantity.value,
			stock: product.value.stock || 99,
			image: product.value.images[0],
			checked: true
		}
		
		try {
			// 从本地存储读取购物车列表
			const cartList = uni.getStorageSync('cartList') || []
			
			// 检查是否已存在相同商品
			const existingItemIndex = cartList.findIndex(
				item => item.productId === goodsData.productId && item.spec === goodsData.spec
			)
			
			if (existingItemIndex > -1) {
				// 如果已存在，增加数量
				cartList[existingItemIndex].quantity += goodsData.quantity
			} else {
				// 如果不存在，添加新商品
				cartList.push(goodsData)
			}
			
			// 保存到本地存储
			uni.setStorageSync('cartList', cartList)
			
			// 更新购物车数量
			loadCartCount()
			
			// 关闭弹窗
			closeSpecPopup()
			
			uni.showToast({
				title: '已加入购物车',
				icon: 'success'
			})
			
			console.log('购物车更新成功:', cartList)
		} catch (e) {
			console.error('添加到购物车失败:', e)
			uni.showToast({
				title: '添加失败，请重试',
				icon: 'none'
			})
		}
	}
	
	// 从弹窗中立即购买
	const handleBuyNowFromPopup = () => {
		// 如果只有一个默认规格，自动选中
		if (!selectedSpec.value && specList.value.length === 1) {
			selectedSpec.value = specList.value[0].name
		}
		
		// 如果有多个规格但没选择，提示选择
		if (!selectedSpec.value && specList.value.length > 1) {
			uni.showToast({
				title: '请选择规格',
				icon: 'none'
			})
			return
		}
		
		const goodsData = {
			id: product.value.id, // 这里的id会被结算页面使用作为productId
			name: product.value.name,
			spec: selectedSpec.value,
			price: product.value.price,
			quantity: quantity.value,
			image: product.value.images[0]
		}
		
		uni.setStorageSync('checkoutGoods', [goodsData])
		
		// 关闭弹窗
		closeSpecPopup()
		
		uni.navigateTo({
			url: '/pages/order/checkout?from=detail'
		})
	}

	// 前往首页
	const goToHome = () => {
		uni.switchTab({
			url: '/pages/index/index'
		})
	}

	// 前往购物车
	const goToCart = () => {
		uni.switchTab({
			url: '/pages/cart/cart'
		})
	}

	// 联系客服
	const contactService = () => {
		uni.showToast({
			title: '联系客服功能',
			icon: 'none'
		})
	}

	// 加入购物车
	const handleAddToCart = () => {
		if (!product.value.id) {
			uni.showToast({ title: '商品信息加载中，请稍后', icon: 'none' })
			return
		}
		// 如果没有选中规格且有多个规格选项，打开规格选择弹窗
		if (!selectedSpec.value && specList.value.length > 1) {
			openSpecPopup()
			return
		}
		
		// 如果没有选中规格但只有一个默认规格，自动选中
		if (!selectedSpec.value && specList.value.length === 1) {
			selectedSpec.value = specList.value[0].name
		}
		
		// 构建商品数据
		const goodsData = {
			id: product.value.id,
			productId: product.value.id, // 商品ID，用于清理购物车
			name: product.value.name,
			spec: selectedSpec.value,
			price: product.value.price,
			quantity: quantity.value,
			stock: product.value.stock || 99, // 库存
			image: product.value.images[0],
			checked: true // 加入购物车默认选中
		}
		
		try {
			// 从本地存储读取购物车列表
			const cartList = uni.getStorageSync('cartList') || []
			
			// 检查购物车中是否已存在相同商品（相同productId和规格）
			const existingItemIndex = cartList.findIndex(
				item => item.productId === goodsData.productId && item.spec === goodsData.spec
			)
			
			if (existingItemIndex > -1) {
				// 如果已存在，增加数量
				cartList[existingItemIndex].quantity += goodsData.quantity
			} else {
				// 如果不存在，添加新商品
				cartList.push(goodsData)
			}
			
			// 保存到本地存储
			uni.setStorageSync('cartList', cartList)
			
			// 更新购物车数量
			loadCartCount()
			
			// 显示成功提示
			uni.showToast({
				title: '已加入购物车',
				icon: 'success'
			})
			
			console.log('购物车更新成功:', cartList)
		} catch (e) {
			console.error('添加到购物车失败:', e)
			uni.showToast({
				title: '添加失败，请重试',
				icon: 'none'
			})
		}
	}

	// 立即购买
	const handleBuyNow = () => {
		if (!product.value.id) {
			uni.showToast({ title: '商品信息加载中，请稍后', icon: 'none' })
			return
		}
		// 如果没有选中规格且有多个规格选项，打开规格选择弹窗
		if (!selectedSpec.value && specList.value.length > 1) {
			openSpecPopup()
			return
		}
		
		// 如果没有选中规格但只有一个默认规格，自动选中
		if (!selectedSpec.value && specList.value.length === 1) {
			selectedSpec.value = specList.value[0].name
		}
		
		// 构建商品数据
		const goodsData = {
			id: product.value.id,
			name: product.value.name,
			spec: selectedSpec.value,
			price: product.value.price,
			quantity: quantity.value,
			image: product.value.images[0]
		}
		
		// 将商品数据存储到本地，供结算页面使用
		uni.setStorageSync('checkoutGoods', [goodsData])
		
		// 跳转到结算页面
		uni.navigateTo({
			url: '/pages/order/checkout?from=detail'
		})
	}
</script>

<style lang="scss" scoped>
	.product-detail-page {
		background: #f5f5f5;
		padding-bottom: 120rpx;
	}

	/* 商品轮播 */
	.product-swiper {
		width: 100%;
		height: 750rpx;
		background: #fff;
	}

	.swiper-image {
		width: 100%;
		height: 100%;
	}

	/* 基本信息 */
	.product-basic-info {
		background: #fff;
		padding: 30rpx;
		margin-bottom: 20rpx;
	}

	.price-box {
		display: flex;
		align-items: baseline;
		margin-bottom: 20rpx;
	}

	.price {
		font-size: 48rpx;
		font-weight: bold;
		color: $primary-color;
	}

	.original-price {
		font-size: 28rpx;
		color: #999;
		text-decoration: line-through;
		margin-left: 16rpx;
	}

	.product-title {
		display: flex;
		justify-content: space-between;
		align-items: flex-start;
		margin-bottom: 12rpx;
	}

	.name {
		flex: 1;
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		line-height: 1.4;
	}

	.share-btn {
		padding: 8rpx 20rpx;
		background: #f5f5f5;
		border-radius: 20rpx;
		font-size: 24rpx;
		color: #666;
		margin-left: 20rpx;
	}

	.product-subtitle {
		font-size: 26rpx;
		color: #666;
		margin-bottom: 20rpx;
	}

	.product-tags {
		display: flex;
		gap: 16rpx;

		.tag {
			padding: 6rpx 16rpx;
			background: #f5f5f5;
			border-radius: 4rpx;
			font-size: 22rpx;
			color: #999;
		}
	}

	/* 规格选择 */
	.spec-selector {
		display: flex;
		align-items: center;
		padding: 30rpx;
		background: #fff;
		margin-bottom: 20rpx;
	}

	.label {
		font-size: 28rpx;
		color: #333;
		margin-right: 40rpx;
	}

	.value {
		flex: 1;
		font-size: 26rpx;
		color: #999;
	}

	.arrow {
		font-size: 24rpx;
		color: #999;
	}

	/* 详情标签页 */
	.detail-tabs {
		background: #fff;
	}

	.tab-header {
		display: flex;
		border-bottom: 1rpx solid #eee;
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

	.tab-content {
		padding: 30rpx;
		min-height: 400rpx;
	}

	/* 产品参数 */
	.params-content {
		.param-item {
			display: flex;
			padding: 24rpx 0;
			border-bottom: 1rpx solid #eee;

			&:last-child {
				border-bottom: none;
			}
		}

		.param-label {
			width: 160rpx;
			font-size: 26rpx;
			color: #999;
		}

		.param-value {
			flex: 1;
			font-size: 26rpx;
			color: #333;
		}
	}

	/* 用户评价 */
	.reviews-content {
		.review-item {
			padding: 30rpx 0;
			border-bottom: 1rpx solid #eee;

			&:last-child {
				border-bottom: none;
			}
		}

		.review-header {
			display: flex;
			align-items: center;
			margin-bottom: 16rpx;
		}

		.user-avatar {
			width: 60rpx;
			height: 60rpx;
			border-radius: 50%;
			margin-right: 16rpx;
		}

		.user-info {
			flex: 1;
		}

		.user-name {
			display: block;
			font-size: 26rpx;
			color: #333;
			margin-bottom: 6rpx;
		}

		.rating-stars {
			font-size: 20rpx;
			color: #FFB400;
		}

		.review-date {
			font-size: 22rpx;
			color: #999;
		}

		.review-content {
			display: block;
			font-size: 26rpx;
			color: #666;
			line-height: 1.6;
			margin-bottom: 16rpx;
		}

		.review-images {
			display: flex;
			gap: 16rpx;
		}

		.review-img {
			width: 160rpx;
			height: 160rpx;
			border-radius: 8rpx;
		}
	}

	/* 底部操作栏 */
	.bottom-bar {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		display: flex;
		align-items: center;
		padding: 16rpx 20rpx;
		background: #fff;
		border-top: 1rpx solid #eee;
		z-index: 100;
	}

	.left-actions {
		display: flex;
		gap: 30rpx;
		margin-right: 20rpx;
	}

	.action-btn {
		display: flex;
		flex-direction: column;
		align-items: center;
		position: relative;

		.icon {
			font-size: 40rpx;
			margin-bottom: 4rpx;
		}

		.text {
			font-size: 20rpx;
			color: #666;
		}

		.badge {
			position: absolute;
			top: -8rpx;
			right: -16rpx;
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
	}

	.right-actions {
		flex: 1;
		display: flex;
		gap: 16rpx;

		button {
			flex: 1;
			height: 72rpx;
			line-height: 72rpx;
			border-radius: 36rpx;
			font-size: 28rpx;
			border: none;
		}

		.add-cart-btn {
			background: #FFB400;
			color: #fff;
		}

		.buy-now-btn {
			background: $primary-color;
			color: #fff;
		}
	}

	/* 规格弹窗 */
	.spec-popup {
		background: #fff;
		border-radius: 24rpx 24rpx 0 0;
		max-height: 80vh;
	}

	.spec-header {
		display: flex;
		align-items: flex-start;
		padding: 30rpx;
		border-bottom: 1rpx solid #eee;
		position: relative;
	}

	.spec-image {
		width: 160rpx;
		height: 160rpx;
		border-radius: 12rpx;
		margin-right: 20rpx;
	}

	.spec-info {
		flex: 1;
		display: flex;
		flex-direction: column;
	}

	.spec-price {
		font-size: 36rpx;
		font-weight: bold;
		color: $primary-color;
		margin-bottom: 12rpx;
	}

	.spec-stock,
	.spec-selected {
		font-size: 24rpx;
		color: #999;
		margin-bottom: 8rpx;
	}

	.close-btn {
		font-size: 48rpx;
		color: #999;
		line-height: 1;
	}

	.spec-content {
		padding: 30rpx;
		max-height: 50vh;
		overflow-y: auto;
	}

	.spec-group {
		margin-bottom: 40rpx;

		&:last-child {
			margin-bottom: 0;
		}
	}

	.spec-title {
		display: block;
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}

	.spec-options {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}

	.spec-option {
		padding: 12rpx 32rpx;
		background: #f5f5f5;
		border-radius: 8rpx;
		font-size: 26rpx;
		color: #666;
		border: 2rpx solid transparent;

		&.active {
			background: #fff;
			color: $primary-color;
			border-color: $primary-color;
		}

		&.disabled {
			opacity: 0.4;
		}
	}

	.quantity-selector {
		display: flex;
		align-items: center;
	}

	.quantity-btn {
		width: 60rpx;
		height: 60rpx;
		line-height: 60rpx;
		text-align: center;
		background: #f5f5f5;
		border-radius: 8rpx;
		font-size: 36rpx;
		color: #333;

		&.disabled {
			opacity: 0.4;
		}
	}

	.quantity-input {
		width: 120rpx;
		height: 60rpx;
		margin: 0 20rpx;
		text-align: center;
		font-size: 28rpx;
		border: 1rpx solid #eee;
		border-radius: 8rpx;
	}

	.spec-footer {
		padding: 20rpx 30rpx;
		border-top: 1rpx solid #eee;
		display: flex;
		gap: 16rpx;

		button {
			flex: 1;
			height: 80rpx;
			line-height: 80rpx;
			border-radius: 40rpx;
			font-size: 28rpx;
			border: none;
		}

		.add-cart-btn-popup {
			background: #FFB400;
			color: #fff;
		}

		.buy-now-btn-popup {
			background: $primary-color;
			color: #fff;
		}
	}
</style>
