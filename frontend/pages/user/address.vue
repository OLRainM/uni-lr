<template>
	<view class="address-page">
		<!-- 地址列表 -->
		<view class="address-list" v-if="addressList.length > 0">
			<view class="address-item" v-for="item in addressList" :key="item.id" @click="handleSelect(item)">
				<view class="address-content">
					<view class="address-header">
						<text class="receiver-name">{{ item.name }}</text>
						<text class="receiver-phone">{{ item.phone }}</text>
						<text class="default-badge" v-if="item.isDefault">默认</text>
					</view>
					<text class="address-detail">{{ item.province }} {{ item.city }} {{ item.district }} {{ item.detail
						}}</text>
				</view>
				<view class="address-actions">
					<text class="action-btn" @click.stop="handleEdit(item)">编辑</text>
					<text class="action-btn delete" @click.stop="handleDelete(item)">删除</text>
				</view>
			</view>
		</view>

		<!-- 空状态 -->
		<view class="empty-state" v-else>
			<!-- <image src="../../static/images/empty-address.png" class="empty-image" mode="aspectFit"></image> -->
			<text class="empty-text">还没有收货地址</text>
		</view>

		<!-- 添加地址按钮 -->
		<view class="add-btn-wrapper">
			<button class="add-btn" @click="handleAdd">+ 新增收货地址</button>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue'
	import { onLoad, onShow } from '@dcloudio/uni-app'

	const fromPage = ref('')
	
	// 初始化默认地址数据（仅第一次访问时使用）
	const defaultAddresses = [
		{
			id: 1,
			name: '张三',
			phone: '13800138000',
			province: '西藏自治区',
			city: '拉萨市',
			district: '城关区',
			detail: 'XX路XX号',
			isDefault: true
		},
		{
			id: 2,
			name: '李四',
			phone: '13900139000',
			province: '青海省',
			city: '西宁市',
			district: '城东区',
			detail: 'YY街YY号',
			isDefault: false
		}
	]
	
	const addressList = ref([])

	onLoad((options) => {
		if (options.from) {
			fromPage.value = options.from
		}
	})

	onShow(() => {
		loadAddressList()
	})

	const loadAddressList = () => {
		// 从本地存储加载地址列表
		try {
			const savedAddresses = uni.getStorageSync('addressList')
			if (savedAddresses && savedAddresses.length > 0) {
				addressList.value = savedAddresses
				console.log('加载地址列表，数量:', savedAddresses.length)
			} else {
				// 第一次访问，使用默认数据
				addressList.value = defaultAddresses
				// 保存到本地
				uni.setStorageSync('addressList', defaultAddresses)
				console.log('初始化默认地址数据')
			}
		} catch (e) {
			console.error('加载地址失败', e)
			addressList.value = defaultAddresses
		}
	}

	const handleSelect = (address) => {
		if (fromPage.value === 'checkout') {
			// 从结算页进入，选择地址后返回
			// 将选中的地址ID保存到本地存储
			uni.setStorageSync('selectedAddressId', address.id)
			console.log('选中地址:', address.name, address.id)
			uni.navigateBack()
		}
	}

	const handleAdd = () => {
		uni.navigateTo({
			url: '/pages/user/address-edit'
		})
	}

	const handleEdit = (address) => {
		uni.navigateTo({
			url: `/pages/user/address-edit?id=${address.id}`
		})
	}

	const handleDelete = (address) => {
		uni.showModal({
			title: '提示',
			content: '确定要删除该地址吗？',
			success: (res) => {
				if (res.confirm) {
					const index = addressList.value.findIndex(item => item.id === address.id)
					if (index !== -1) {
						addressList.value.splice(index, 1)
						// 保存到本地存储
						uni.setStorageSync('addressList', addressList.value)
						uni.showToast({
							title: '删除成功',
							icon: 'success'
						})
					}
				}
			}
		})
	}
</script>

<style lang="scss" scoped>
	.address-page {
		min-height: 100vh;
		background: #f5f5f5;
		padding-bottom: 120rpx;
	}

	.address-list {
		padding: 20rpx;
	}

	.address-item {
		background: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
	}

	.address-content {
		margin-bottom: 24rpx;
	}

	.address-header {
		display: flex;
		align-items: center;
		margin-bottom: 16rpx;
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

	.default-badge {
		margin-left: auto;
		padding: 4rpx 12rpx;
		background: $primary-color;
		color: #fff;
		font-size: 20rpx;
		border-radius: 4rpx;
	}

	.address-detail {
		display: block;
		font-size: 26rpx;
		color: #666;
		line-height: 1.6;
	}

	.address-actions {
		display: flex;
		gap: 40rpx;
		padding-top: 24rpx;
		border-top: 1rpx solid #f5f5f5;
	}

	.action-btn {
		font-size: 26rpx;
		color: #666;

		&.delete {
			color: #f44336;
		}
	}

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

	.add-btn-wrapper {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		padding: 20rpx;
		background: #fff;
		border-top: 1rpx solid #eee;
	}

	.add-btn {
		width: 100%;
		height: 80rpx;
		line-height: 80rpx;
		background: $primary-color;
		color: #fff;
		border-radius: 40rpx;
		font-size: 28rpx;
		border: none;
	}
</style>
