<script setup>
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'

onLaunch(() => {
	console.log('App Launch')
	
	// Vue 3 推荐使用 Pinia 或本地存储管理全局数据
	// 不再使用 globalData
})

onShow(() => {
	console.log('App Show')
})

onHide(() => {
	console.log('App Hide')
})
</script>

<style lang="scss">
	/* 全局样式 */
	@import '@/uni.scss';
	
	page {
		background-color: #f5f5f5;
		font-size: 14px;
		color: #333;
	}
	
	/* 主题色定义 */
	:root {
		--primary-color: #8B1A1A;
		--secondary-color: #D4AF37;
		--text-color: #333333;
		--text-light: #666666;
		--border-color: #E5E5E5;
		--bg-color: #F5F5F5;
	}
</style>
