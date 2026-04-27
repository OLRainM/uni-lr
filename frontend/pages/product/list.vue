<template>
	<view class="product-list-page">
		<!-- 搜索栏 -->
		<view class="search-bar">
			<view class="search-input">
				<text class="iconfont icon-search">🔍</text>
				<input type="text" placeholder="搜索商品" v-model="keyword" @confirm="handleSearch" />
			</view>
			<view class="filter-btn" @click="showFilterPopup = true">
				<text>筛选</text>
			</view>
		</view>

		<!-- 分类标签 -->
		<scroll-view class="category-tabs" scroll-x>
			<view class="tab-item" :class="{ active: currentCategory === '' }" @click="selectCategory('')">
				全部
			</view>
			<view class="tab-item" :class="{ active: currentCategory === item.id }"
				v-for="item in categories" :key="item.id" @click="selectCategory(item.id)">
				{{ item.name }}
			</view>
		</scroll-view>

		<!-- 排序栏 -->
		<view class="sort-bar">
			<view class="sort-item" :class="{ active: sortType === 'default' }" @click="changeSort('default')">
				综合
			</view>
			<view class="sort-item" :class="{ active: sortType === 'sales' }" @click="changeSort('sales')">
				销量
			</view>
			<view class="sort-item" :class="{ active: sortType === 'price' }" @click="changeSort('price')">
				价格
				<text class="arrow" :class="{ up: priceOrder === 'asc', down: priceOrder === 'desc' }">↑</text>
			</view>
			<view class="sort-item" :class="{ active: sortType === 'new' }" @click="changeSort('new')">
				新品
			</view>
		</view>

		<!-- 商品列表 -->
		<view class="product-list">
			<view class="product-item" v-for="item in productList" :key="item.id"
				@click="goToDetail(item.id)">
				<image :src="item.image" class="product-image" mode="aspectFill"></image>
				<view class="product-info">
					<text class="product-name">{{ item.name }}</text>
					<text class="product-desc">{{ item.subtitle }}</text>
					<view class="product-tags">
						<text class="tag" v-if="item.isNew">新品</text>
						<text class="tag hot" v-if="item.isHot">热销</text>
					</view>
					<view class="product-footer">
						<view class="price-box">
							<text class="price">¥{{ item.price }}</text>
							<text class="original-price" v-if="item.originalPrice">¥{{ item.originalPrice }}</text>
						</view>
						<text class="sales">已售{{ item.sales }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 加载提示 -->
		<view class="loading-tip" v-if="loading">
			<text>加载中...</text>
		</view>
		<view class="no-more" v-if="!loading && noMore">
			<text>没有更多了</text>
		</view>

		<!-- 筛选弹窗 -->
		<uni-popup ref="filterPopup" type="bottom">
			<view class="filter-popup">
				<view class="filter-header">
					<text class="filter-title">筛选</text>
					<text class="filter-reset" @click="resetFilter">重置</text>
				</view>
				<view class="filter-content">
					<!-- 价格区间 -->
					<view class="filter-group">
						<text class="group-title">价格区间</text>
						<view class="price-range">
							<input type="number" placeholder="最低价" v-model="filterData.minPrice" />
							<text class="separator">-</text>
							<input type="number" placeholder="最高价" v-model="filterData.maxPrice" />
						</view>
					</view>

					<!-- 产地 -->
					<view class="filter-group">
						<text class="group-title">产地</text>
						<view class="filter-options">
							<view class="option-item" :class="{ active: filterData.origin === item }"
								v-for="item in originList" :key="item" @click="filterData.origin = item">
								{{ item }}
							</view>
						</view>
					</view>
				</view>
				<view class="filter-footer">
					<button class="cancel-btn" @click="showFilterPopup = false">取消</button>
					<button class="confirm-btn" @click="confirmFilter">确定</button>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script setup>
	import { ref, onMounted } from 'vue'
	import { onShow, onReachBottom, onPullDownRefresh } from '@dcloudio/uni-app'
	import { getProductList, getProductCategories } from '@/api/index.js'

	// 搜索关键词
	const keyword = ref('')
	
	// 当前分类
	const currentCategory = ref('')
	
	// 分类列表
	const categories = ref([])

	// 排序类型
	const sortType = ref('default')
	const priceOrder = ref('desc') // asc: 升序, desc: 降序

	// 商品列表
	const productList = ref([])

	// 加载状态
	const loading = ref(false)
	const noMore = ref(false)
	const page = ref(1)

	// 筛选弹窗
	const showFilterPopup = ref(false)
	const filterData = ref({
		minPrice: '',
		maxPrice: '',
		origin: ''
	})
	const originList = ref(['西藏', '青海', '四川', '云南'])

	// 初始化
	onMounted(() => {
		loadCategories()
		loadProductList()
	})
	
	// 下拉刷新
	onPullDownRefresh(() => {
		page.value = 1
		productList.value = []
		loadProductList().then(() => {
			uni.stopPullDownRefresh()
		})
	})

	// TabBar 页面显示时触发（用于接收从首页传来的参数）
	onShow(() => {
		// 从本地存储读取参数
		const categoryId = uni.getStorageSync('categoryId')
		const productType = uni.getStorageSync('productType')
		
		// 处理分类参数
		if (categoryId) {
			currentCategory.value = categoryId
			// 清除本地存储，避免重复使用
			uni.removeStorageSync('categoryId')
			// 重新加载数据
			page.value = 1
			productList.value = []
			loadProductList()
		}
		
		// 处理类型参数（新品、促销）
		if (productType) {
			// 清除本地存储
			uni.removeStorageSync('productType')
			
			// 根据类型设置排序
			if (productType === 'new') {
				sortType.value = 'new'
			} else if (productType === 'sale') {
				// 可以添加促销标记的处理逻辑
				sortType.value = 'default'
			}
			
			// 重新加载并排序
			page.value = 1
			productList.value = []
			loadProductList()
		}
	})
	
	// 加载分类列表
	const loadCategories = async () => {
		try {
			const res = await getProductCategories()
			if (res && res.length > 0) {
				categories.value = res
			}
		} catch (e) {
			console.error('加载分类失败:', e)
		}
	}

	// 加载商品列表
	const loadProductList = async () => {
		if (loading.value) return
		loading.value = true
		
		try {
			// 构建请求参数
			const params = {
				pageNum: page.value,
				pageSize: 10
			}
			
			// 分类筛选
			if (currentCategory.value) {
				params.categoryId = currentCategory.value
			}
			
			// 搜索关键词
			if (keyword.value) {
				params.keyword = keyword.value
			}
			
			// 价格区间
			if (filterData.value.minPrice) {
				params.minPrice = filterData.value.minPrice
			}
			if (filterData.value.maxPrice) {
				params.maxPrice = filterData.value.maxPrice
			}
			
			// 排序
			if (sortType.value === 'sales') {
				params.orderBy = 'sales'
				params.orderDirection = 'desc'
			} else if (sortType.value === 'price') {
				params.orderBy = 'price'
				params.orderDirection = priceOrder.value
			} else if (sortType.value === 'new') {
				params.isNew = 1
			}
			
			// 调用API
			const res = await getProductList(params)
			
			if (res && res.list) {
				// 对图片做兜底处理，将猫咪占位图替换为本地图片
				const localImages = [
					'/static/images/product1.jpg',
					'/static/images/product3.jpg',
					'/static/images/product4.webp',
					'/static/images/product2.jpg'
				]
				const getLocalImage = (item, index) => {
					// 如果已有有效的非猫咪图片，直接使用
					if (item.image && !item.image.includes('yzcdn.cn')) return item.image
					// 按商品名关键词匹配
					const name = item.name || ''
					if (name.includes('冬虫夏草')) return '/static/images/product1.jpg'
					if (name.includes('藏红花')) return '/static/images/product3.jpg'
					if (name.includes('雪莲')) return '/static/images/product4.webp'
					if (name.includes('红虫草') || name.includes('礼盒')) return '/static/images/product2.jpg'
					if (name.includes('红景天')) return '/static/images/product5.jpg'
					// 其余按序号轮换
					return localImages[index % localImages.length]
				}
				const mappedList = res.list.map((item, index) => ({
					...item,
					image: getLocalImage(item, index)
				}))
				// 第一页重置列表，其他页追加数据
				if (page.value === 1) {
					productList.value = mappedList
				} else {
					productList.value = [...productList.value, ...mappedList]
				}
				
				// 判断是否还有更多数据
				noMore.value = productList.value.length >= res.total
			} else {
				// 没有数据
				if (page.value === 1) {
					productList.value = []
				}
				noMore.value = true
			}
		} catch (e) {
			console.error('加载商品列表失败:', e)
			uni.showToast({
				title: '加载失败，请重试',
				icon: 'none'
			})
			// 加载失败时，如果是首页则清空列表
			if (page.value === 1) {
				productList.value = []
			}
		} finally {
			loading.value = false
		}
	}

	// 搜索
	const handleSearch = () => {
		page.value = 1
		productList.value = []
		loadProductList()
	}

	// 选择分类
	const selectCategory = (categoryId) => {
		currentCategory.value = categoryId
		page.value = 1
		productList.value = []
		noMore.value = false
		loadProductList()
	}

	// 切换排序
	const changeSort = (type) => {
		// 如果点击价格排序，且当前已是价格排序，则切换升序/降序
		if (type === 'price' && sortType.value === 'price') {
			priceOrder.value = priceOrder.value === 'asc' ? 'desc' : 'asc'
		}
		sortType.value = type
		// 重新加载数据（使用API排序）
		page.value = 1
		productList.value = []
		noMore.value = false
		loadProductList()
	}

	// 重置筛选
	const resetFilter = () => {
		filterData.value = {
			minPrice: '',
			maxPrice: '',
			origin: ''
		}
	}

	// 确认筛选
	const confirmFilter = () => {
		showFilterPopup.value = false
		page.value = 1
		productList.value = []
		noMore.value = false
		loadProductList()
	}

	// 前往详情
	const goToDetail = (id) => {
		uni.navigateTo({
			url: `/pages/product/detail?id=${id}`
		})
	}

	// 触底加载更多
	onReachBottom(() => {
		if (!loading.value && !noMore.value) {
			page.value++
			loadProductList()
		}
	})
</script>

<style lang="scss" scoped>
	.product-list-page {
		background: #f5f5f5;
		min-height: 100vh;
	}

	/* 搜索栏 */
	.search-bar {
		display: flex;
		align-items: center;
		padding: 20rpx;
		background: #fff;
	}

	.search-input {
		flex: 1;
		display: flex;
		align-items: center;
		height: 64rpx;
		background: #f5f5f5;
		border-radius: 32rpx;
		padding: 0 24rpx;

		input {
			flex: 1;
			margin-left: 10rpx;
			font-size: 28rpx;
		}
	}

	.filter-btn {
		margin-left: 20rpx;
		padding: 0 24rpx;
		height: 64rpx;
		line-height: 64rpx;
		background: $primary-color;
		color: #fff;
		border-radius: 32rpx;
		font-size: 28rpx;
	}

	/* 分类标签 */
	.category-tabs {
		white-space: nowrap;
		padding: 20rpx;
		background: #fff;
		border-bottom: 1rpx solid #eee;
	}

	.tab-item {
		display: inline-block;
		padding: 8rpx 24rpx;
		margin-right: 20rpx;
		background: #f5f5f5;
		border-radius: 32rpx;
		font-size: 26rpx;
		color: #666;

		&.active {
			background: $primary-color;
			color: #fff;
		}
	}

	/* 排序栏 */
	.sort-bar {
		display: flex;
		align-items: center;
		padding: 0 20rpx;
		height: 88rpx;
		background: #fff;
		border-bottom: 1rpx solid #eee;
	}

	.sort-item {
		flex: 1;
		text-align: center;
		font-size: 28rpx;
		color: #666;
		position: relative;

		&.active {
			color: $primary-color;
			font-weight: bold;
		}

		.arrow {
			margin-left: 4rpx;
			font-size: 20rpx;
			
			&.up {
				transform: rotate(0deg);
			}
			
			&.down {
				transform: rotate(180deg);
			}
		}
	}

	/* 商品列表 */
	.product-list {
		padding: 20rpx;
	}

	.product-item {
		display: flex;
		background: #fff;
		border-radius: 12rpx;
		margin-bottom: 20rpx;
		overflow: hidden;
	}

	.product-image {
		width: 240rpx;
		height: 240rpx;
		flex-shrink: 0;
	}

	.product-info {
		flex: 1;
		padding: 20rpx;
		display: flex;
		flex-direction: column;
	}

	.product-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 8rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.product-desc {
		font-size: 24rpx;
		color: #999;
		margin-bottom: 12rpx;
	}

	.product-tags {
		margin-bottom: 12rpx;

		.tag {
			display: inline-block;
			padding: 4rpx 12rpx;
			background: #fff3e0;
			color: #ff9800;
			border-radius: 4rpx;
			font-size: 20rpx;
			margin-right: 8rpx;

			&.hot {
				background: #ffebee;
				color: #f44336;
			}
		}
	}

	.product-footer {
		display: flex;
		justify-content: space-between;
		align-items: flex-end;
		margin-top: auto;
	}

	.price-box {
		display: flex;
		align-items: baseline;
	}

	.price {
		font-size: 32rpx;
		font-weight: bold;
		color: $primary-color;
	}

	.original-price {
		font-size: 24rpx;
		color: #999;
		text-decoration: line-through;
		margin-left: 8rpx;
	}

	.sales {
		font-size: 22rpx;
		color: #999;
	}

	/* 加载提示 */
	.loading-tip,
	.no-more {
		text-align: center;
		padding: 40rpx 0;
		font-size: 24rpx;
		color: #999;
	}

	/* 筛选弹窗 */
	.filter-popup {
		background: #fff;
		border-radius: 24rpx 24rpx 0 0;
		max-height: 80vh;
	}

	.filter-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 30rpx;
		border-bottom: 1rpx solid #eee;
	}

	.filter-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.filter-reset {
		font-size: 28rpx;
		color: $primary-color;
	}

	.filter-content {
		padding: 30rpx;
		max-height: 60vh;
		overflow-y: auto;
	}

	.filter-group {
		margin-bottom: 40rpx;
	}

	.group-title {
		display: block;
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}

	.price-range {
		display: flex;
		align-items: center;

		input {
			flex: 1;
			height: 64rpx;
			padding: 0 20rpx;
			background: #f5f5f5;
			border-radius: 8rpx;
			font-size: 28rpx;
		}

		.separator {
			margin: 0 20rpx;
			font-size: 28rpx;
			color: #999;
		}
	}

	.filter-options {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}

	.option-item {
		padding: 12rpx 32rpx;
		background: #f5f5f5;
		border-radius: 8rpx;
		font-size: 26rpx;
		color: #666;

		&.active {
			background: $primary-color;
			color: #fff;
		}
	}

	.filter-footer {
		display: flex;
		padding: 20rpx 30rpx;
		border-top: 1rpx solid #eee;
		gap: 20rpx;

		button {
			flex: 1;
			height: 80rpx;
			line-height: 80rpx;
			border-radius: 40rpx;
			font-size: 28rpx;
		}

		.cancel-btn {
			background: #f5f5f5;
			color: #666;
		}

		.confirm-btn {
			background: $primary-color;
			color: #fff;
		}
	}
</style>
