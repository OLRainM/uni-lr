<template>
	<view class="index-page">
		<!-- 轮播图 -->
		<view class="swiper-container">
			<swiper class="swiper" :indicator-dots="true" :autoplay="true" :interval="3000" :duration="500"
				circular>
				<swiper-item v-for="(item, index) in bannerList" :key="index" @click="handleBannerClick(item)">
					<image :src="item.image" class="swiper-image" mode="aspectFill"></image>
				</swiper-item>
			</swiper>
		</view>

		<!-- 快捷导航 -->
		<view class="nav-grid">
			<view class="nav-item" v-for="(item, index) in navList" :key="index" @click="handleNavClick(item)">
				<image :src="item.icon" class="nav-icon"></image>
				<text class="nav-text">{{ item.name }}</text>
			</view>
		</view>

		<!-- 藏药知识科普 -->
		<view class="knowledge-section">
			<view class="section-header">
				<text class="section-title">藏药知识</text>
				<text class="section-more" @click="goToKnowledge">更多 ›</text>
			</view>
			<scroll-view class="knowledge-scroll" scroll-x>
				<view class="knowledge-item" v-for="(item, index) in knowledgeList" :key="index"
					@click="goToKnowledgeDetail(item)">
					<image :src="item.image" class="knowledge-image"></image>
					<text class="knowledge-title">{{ item.title }}</text>
				</view>
			</scroll-view>
		</view>

		<!-- 品牌故事 -->
		<view class="brand-section">
			<view class="section-header">
				<text class="section-title">品牌故事</text>
			</view>
			<view class="brand-content">
				<view class="brand-logo-row">
					<text class="brand-logo-text">生命痊力·金珂</text>
					<text class="brand-founded">创立于 1992 年</text>
				</view>
				<text class="brand-desc">金珂股份有限公司起源于1992年成立的青海省藏药制剂中心，注册地为青海西宁。青海总部、生产基地位于西宁生物科技产业园，管理总部、营销中心位于上海，研发中心位于山东济南，互联网医院位于四川成都。</text>
				<text class="brand-desc brand-desc-second">公司以"弘扬藏医药，造福全人类"为企业使命，传承藏医药千年智慧，贡献高原极度生命力，力争成为中国健康事业的标杆企业！</text>
				<view class="brand-tags">
					<text class="brand-tag">千年传承</text>
					<text class="brand-tag">高原原料</text>
					<text class="brand-tag">正宗品质</text>
					<text class="brand-tag">科学炮制</text>
				</view>
			</view>
		</view>

		<!-- 藏药知识 -->
		<view class="tibetan-knowledge-section">
			<view class="section-header">
				<text class="section-title">藏药文化</text>
			</view>
			<view class="tibetan-knowledge-content">
				<view class="tk-card">
					<text class="tk-icon">📜</text>
					<text class="tk-title">千年历史</text>
					<text class="tk-desc">藏药是在广泛吸收、融合了中医药学、印度医药学和大食医药学等理论的基础上，通过长期实践所形成的独特医药体系，迄今已有上千年历史。</text>
				</view>
				<view class="tk-card">
					<text class="tk-icon">🏔️</text>
					<text class="tk-title">高原精华</text>
					<text class="tk-desc">藏药源于青藏高原的传统医学体系，融合藏族本土医疗经验，吸收中医、印度医学与阿拉伯医学精华，在独特自然环境和文化背景下发展而成。</text>
				</view>
				<view class="tk-card">
					<text class="tk-icon">🌿</text>
					<text class="tk-title">非遗传承</text>
					<text class="tk-desc">藏医是具有悠久历史、独具特色、疗效显著的一门科学，是中国医学宝库的重要组成部分，国家高度重视其非物质文化遗产的保护与传承。</text>
				</view>
			</view>
		</view>

		<!-- 热销推荐 -->
		<view class="recommend-section">
			<view class="section-header">
				<text class="section-title">热销推荐</text>
				<text class="section-more" @click="goToProductList">更多 ›</text>
			</view>
			<view class="product-grid">
				<view class="product-item" v-for="(item, index) in recommendList" :key="index"
					@click="goToProductDetail(item)">
					<image :src="item.image" class="product-image" mode="aspectFill"></image>
					<view class="product-info">
						<text class="product-name">{{ item.name }}</text>
						<text class="product-desc">{{ item.subtitle }}</text>
						<view class="product-footer">
							<text class="product-price">¥{{ item.price }}</text>
							<text class="product-sales">已售{{ item.sales }}</text>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { ref, onMounted } from 'vue'
	import { onPullDownRefresh } from '@dcloudio/uni-app'
	import { getBannerList, getKnowledgeList, getRecommendProducts, getProductCategories } from '@/api/index.js'

	// 轮播图数据
	const bannerList = ref([])

	// 快捷导航
	const navList = ref([])

	// 藏药知识
	const knowledgeList = ref([])

	// 热销推荐
	const recommendList = ref([])
	
	// 加载状态
	const loading = ref(false)

	// 初始化
	onMounted(() => {
		loadData()
	})

	// 下拉刷新
	onPullDownRefresh(() => {
		loadData()
		setTimeout(() => {
			uni.stopPullDownRefresh()
		}, 1000)
	})

	// 加载数据
	const loadData = async () => {
		if (loading.value) return
		loading.value = true
		
		try {
			// 并行加载所有数据
			await Promise.all([
				loadBanners(),
				loadCategories(),
				loadKnowledge(),
				loadRecommendProducts()
			])
		} catch (e) {
			console.error('加载数据失败:', e)
		} finally {
			loading.value = false
		}
	}
	
	// 本地兜底轮播图（品牌宣传横幅）
	const fallbackBanners = [
		{ image: '/static/images/banner1.png', link: '' },
		{ image: '/static/images/banner2.png', link: '' },
		{ image: '/static/images/banner3.png', link: '' }
	]

	// 判断图片是否有效（非外链占位图）
	const isValidImage = (url) => {
		if (!url) return false
		if (url.includes('yzcdn.cn') || url.includes('placeholder') || url.includes('via.placeholder')) return false
		return true
	}

	// 加载轮播图
	const loadBanners = async () => {
		try {
			const res = await getBannerList()
			if (res && res.length > 0) {
				const mapped = res.map((item, idx) => ({
					id: item.id,
					// 后端图片无效时，用本地兜底图片
					image: isValidImage(item.image) ? item.image : fallbackBanners[idx % fallbackBanners.length].image,
					link: item.link || ''
				}))
				bannerList.value = mapped
			} else {
				// 后端无数据，直接用本地图
				bannerList.value = fallbackBanners
			}
		} catch (e) {
			console.error('加载轮播图失败:', e)
			bannerList.value = fallbackBanners
		}
	}
	
	// 加载分类（构建快捷导航）
	// 按分类名称关键词匹配本地图标
	const getCategoryIcon = (name) => {
		const n = name || ''
		if (n.includes('冬虫夏草') || n.includes('虫草')) return '/static/images/product1.jpg'
		if (n.includes('藏红花'))                          return '/static/images/product3.jpg'
		if (n.includes('雪莲'))                            return '/static/images/product4.webp'
		if (n.includes('红景天'))                          return '/static/images/product5.png'
		if (n.includes('红虫草') || n.includes('礼盒'))    return '/static/images/product2.jpg'
		// 其余分类按名称首字母轮换
		return '/static/images/product2.jpg'
	}

	const loadCategories = async () => {
		try {
			const categories = await getProductCategories()
			if (categories && categories.length > 0) {
				// 只取前5个分类（为新品、优惠留出位置）
				const topCategories = categories.slice(0, 5).map(item => {
					return {
						name: item.name,
						// 优先使用后端有效图片，否则按名称关键词匹配本地图标
						icon: (item.icon && (item.icon.startsWith('http://') || item.icon.startsWith('https://')))
							? item.icon
							: getCategoryIcon(item.name),
						type: 'category',
						value: item.id.toString()
					}
				})

				// 添加新品和优惠
				navList.value = [
					...topCategories,
					{
						name: '新品上架',
						icon: '/static/images/new.png',
						type: 'new'
					},
					{
						name: '限时特惠',
						icon: '/static/images/timelimit.png',
						type: 'sale'
					}
				]
			}
		} catch (e) {
			console.error('加载分类失败:', e)
		}
	}
	
	// 加载知识库
	const loadKnowledge = async () => {
		try {
			const res = await getKnowledgeList({ pageNum: 1, pageSize: 4 })
			if (res && res.list && res.list.length > 0) {
				knowledgeList.value = res.list.map(item => ({
					id: item.id,
					title: item.title,
					image: item.cover || '/static/images/product3.jpg'
				}))
			} else {
				// 接口无数据时使用本地兜底内容
				knowledgeList.value = [
					{ id: 1, title: '藏药的历史与传承', image: '/static/images/product3.jpg' },
					{ id: 2, title: '藏医三因学说解读', image: '/static/images/product1.jpg' },
					{ id: 3, title: '高原藏药材炮制工艺', image: '/static/images/product4.webp' },
					{ id: 4, title: '藏红花的功效与使用', image: '/static/images/product2.jpg' }
				]
			}
		} catch (e) {
			console.error('加载知识库失败:', e)
			// 失败时使用本地兜底内容
			knowledgeList.value = [
				{ id: 1, title: '藏药的历史与传承', image: '/static/images/product3.jpg' },
				{ id: 2, title: '藏医三因学说解读', image: '/static/images/product1.jpg' },
				{ id: 3, title: '高原藏药材炮制工艺', image: '/static/images/product4.webp' },
				{ id: 4, title: '藏红花的功效与使用', image: '/static/images/product2.jpg' }
			]
		}
	}
	
	// 加载推荐商品
	const loadRecommendProducts = async () => {
		try {
			const products = await getRecommendProducts()
			if (products && products.length > 0) {
				recommendList.value = products.map(item => ({
					id: item.id,
					name: item.name,
					subtitle: item.subtitle || '',
					price: item.price,
					sales: item.sales || 0,
					image: item.image || '/static/images/product1.jpg'
				}))
			}
		} catch (e) {
			console.error('加载推荐商品失败:', e)
		}
	}

	// 轮播图点击
	const handleBannerClick = (item) => {
		if (!item || !item.link) return

		const link = String(item.link).trim()
		if (!link) return

		// 统一处理为站内页面跳转，兼容带/不带前缀
		const normalized = link.startsWith('/') ? link : `/${link}`

		// tabBar 页面必须用 switchTab
		const tabBarPages = ['/pages/index/index', '/pages/product/list', '/pages/cart/cart', '/pages/user/user']
		if (tabBarPages.includes(normalized.split('?')[0])) {
			uni.switchTab({ url: normalized.split('?')[0] })
			return
		}

		uni.navigateTo({
			url: normalized
		})
	}

	// 导航点击
	const handleNavClick = (item) => {
		// 由于 product/list 是 TabBar 页面，不能用 navigateTo，需要用 switchTab
		// switchTab 不支持传参，所以使用本地存储传递参数
		if (item.type === 'category') {
			// 存储分类参数到本地存储
			uni.setStorageSync('categoryId', item.value)
			uni.removeStorageSync('productType')
			
			uni.switchTab({
				url: '/pages/product/list'
			})
		} else if (item.type === 'new') {
			// 存储类型参数到本地存储
			uni.setStorageSync('productType', 'new')
			uni.removeStorageSync('categoryId')
			
			uni.switchTab({
				url: '/pages/product/list'
			})
		} else if (item.type === 'sale') {
			// 存储类型参数到本地存储
			uni.setStorageSync('productType', 'sale')
			uni.removeStorageSync('categoryId')
			
			uni.switchTab({
				url: '/pages/product/list'
			})
		}
	}

	// 前往知识列表
	const goToKnowledge = () => {
		uni.showToast({
			title: '知识列表页面',
			icon: 'none'
		})
	}

	// 前往知识详情
	const goToKnowledgeDetail = (item) => {
		uni.navigateTo({
			url: `/pages/knowledge/detail?id=${item.id}`
		})
	}

	// 前往商品列表
	const goToProductList = () => {
		// 商品列表是 TabBar 页面，使用 switchTab
		uni.switchTab({
			url: '/pages/product/list'
		})
	}

	// 前往商品详情
	const goToProductDetail = (item) => {
		uni.navigateTo({
			url: `/pages/product/detail?id=${item.id}`
		})
	}
</script>

<style lang="scss" scoped>
	.index-page {
		padding-bottom: 20rpx;
	}

	/* 轮播图 */
	.swiper-container {
		width: 100%;
		height: 400rpx;
		background: #fff;
	}

	.swiper {
		height: 100%;
	}

	.swiper-image {
		width: 100%;
		height: 100%;
	}

	/* 快捷导航 */
	.nav-grid {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 20rpx;
		padding: 30rpx 20rpx;
		background: #fff;
		margin-top: 20rpx;
	}

	.nav-item {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.nav-icon {
		width: 100rpx;
		height: 100rpx;
		border-radius: 50%;
		margin-bottom: 10rpx;
		object-fit: cover;
		border: 2rpx solid #f0e6d0;
		box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.10);
	}

	.nav-text {
		font-size: 24rpx;
		color: #333;
	}

	/* 区块标题 */
	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 30rpx 20rpx 20rpx;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		position: relative;
		padding-left: 20rpx;

		&::before {
			content: '';
			position: absolute;
			left: 0;
			top: 50%;
			transform: translateY(-50%);
			width: 6rpx;
			height: 28rpx;
			background: $primary-color;
			border-radius: 3rpx;
		}
	}

	.section-more {
		font-size: 24rpx;
		color: #999;
	}

	/* 藏药知识 */
	.knowledge-section {
		background: #fff;
		margin-top: 20rpx;
	}

	.knowledge-scroll {
		white-space: nowrap;
		padding: 0 20rpx 30rpx;
	}

	.knowledge-item {
		display: inline-block;
		width: 280rpx;
		margin-right: 20rpx;
	}

	.knowledge-image {
		width: 280rpx;
		height: 200rpx;
		border-radius: 12rpx;
		margin-bottom: 10rpx;
	}

	.knowledge-title {
		font-size: 26rpx;
		color: #333;
		display: block;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	/* 品牌故事 */
	.brand-section {
		background: #fff;
		margin-top: 20rpx;
	}

	.brand-content {
		padding: 0 20rpx 30rpx;
	}

	.brand-image {
		width: 100%;
		height: 300rpx;
		border-radius: 12rpx;
		margin-bottom: 20rpx;
	}

	.brand-text {
		display: flex;
		flex-direction: column;
	}

	.brand-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
	}

	.brand-desc {
		font-size: 24rpx;
		color: #666;
		line-height: 1.6;
	}

	/* 品牌故事 */
	.brand-section {
		background: #fff;
		margin-top: 20rpx;
	}

	.brand-content {
		padding: 0 30rpx 30rpx;
	}

	.brand-logo-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-bottom: 20rpx;
		padding: 20rpx 0 16rpx;
		border-bottom: 2rpx solid #f0ece8;
	}

	.brand-logo-text {
		font-size: 34rpx;
		font-weight: bold;
		color: #8B1A1A;
		letter-spacing: 2rpx;
	}

	.brand-founded {
		font-size: 22rpx;
		color: #999;
		background: #f8f4f0;
		padding: 6rpx 16rpx;
		border-radius: 20rpx;
	}

	.brand-desc {
		font-size: 26rpx;
		color: #555;
		line-height: 1.8;
		display: block;
		margin-bottom: 12rpx;
	}

	.brand-desc-second {
		color: #333;
		font-weight: 500;
	}

	.brand-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 16rpx;
		margin-top: 20rpx;
	}

	.brand-tag {
		padding: 8rpx 24rpx;
		background: linear-gradient(135deg, #8B1A1A 0%, #A03030 100%);
		color: #fff;
		border-radius: 30rpx;
		font-size: 24rpx;
	}

	/* 藏药文化 */
	.tibetan-knowledge-section {
		background: #fff;
		margin-top: 20rpx;
	}

	.tibetan-knowledge-content {
		padding: 0 20rpx 30rpx;
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.tk-card {
		background: linear-gradient(135deg, #fdf8f0 0%, #f8f0e8 100%);
		border-radius: 16rpx;
		padding: 30rpx;
		display: flex;
		flex-direction: column;
		border-left: 6rpx solid #8B1A1A;
	}

	.tk-icon {
		font-size: 48rpx;
		margin-bottom: 12rpx;
	}

	.tk-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #8B1A1A;
		margin-bottom: 12rpx;
	}

	.tk-desc {
		font-size: 26rpx;
		color: #555;
		line-height: 1.7;
	}

	/* 热销推荐 */
	.recommend-section {
		background: #fff;
		margin-top: 20rpx;
	}

	.product-grid {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 20rpx;
		padding: 0 20rpx 30rpx;
	}

	.product-item {
		background: #fff;
		border-radius: 12rpx;
		overflow: hidden;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
	}

	.product-image {
		width: 100%;
		height: 340rpx;
	}

	.product-info {
		padding: 20rpx;
	}

	.product-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 6rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.product-desc {
		font-size: 22rpx;
		color: #999;
		display: block;
		margin-bottom: 12rpx;
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

	.product-sales {
		font-size: 22rpx;
		color: #999;
	}
</style>
