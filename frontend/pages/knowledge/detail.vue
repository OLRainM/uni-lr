<template>
	<view class="knowledge-detail-page">
		<view class="article-header">
			<text class="article-title">{{ article.title }}</text>
			<view class="article-meta">
				<text class="meta-item">{{ article.date }}</text>
				<text class="meta-item">阅读 {{ article.views }}</text>
			</view>
		</view>
		<view class="article-content">
			<rich-text :nodes="article.content"></rich-text>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue'
	import { onLoad } from '@dcloudio/uni-app'

	const articleId = ref('')
	const article = ref({
		title: '冬虫夏草的功效与使用方法',
		date: '2025-01-15',
		views: 1580,
		content: '<p>冬虫夏草是一种珍贵的藏药材...</p>'
	})

	onLoad((options) => {
		if (options.id) {
			articleId.value = options.id
			loadArticleDetail()
		}
	})

	const loadArticleDetail = () => {
		console.log('加载文章详情', articleId.value)
	}
</script>

<style lang="scss" scoped>
	.knowledge-detail-page {
		min-height: 100vh;
		background: #fff;
		padding: 30rpx;
	}

	.article-header {
		margin-bottom: 30rpx;
	}

	.article-title {
		display: block;
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		line-height: 1.5;
		margin-bottom: 20rpx;
	}

	.article-meta {
		display: flex;
		gap: 30rpx;
	}

	.meta-item {
		font-size: 24rpx;
		color: #999;
	}

	.article-content {
		font-size: 28rpx;
		color: #666;
		line-height: 1.8;
	}
</style>
