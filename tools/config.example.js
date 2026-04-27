// 配置示例文件
// 复制此文件为 config.js 并修改相关配置

export default {
	// API 基础地址
	apiBaseUrl: 'https://api.example.com',
	
	// 微信小程序配置
	wechat: {
		appId: 'your-wechat-appid',
		// 微信支付商户号
		mchId: 'your-merchant-id'
	},
	
	// 图片服务器地址
	imageBaseUrl: 'https://cdn.example.com',
	
	// 第三方服务配置
	services: {
		// 快递查询
		logistics: {
			apiKey: 'your-logistics-api-key'
		},
		// 短信服务
		sms: {
			apiKey: 'your-sms-api-key'
		}
	},
	
	// 应用配置
	app: {
		// 应用名称
		name: '藏药小程序',
		// 版本号
		version: '1.0.0',
		// 客服电话
		servicePhone: '400-xxx-xxxx',
		// 客服微信
		serviceWechat: 'service-wechat-id'
	},
	
	// 分页配置
	pagination: {
		pageSize: 10,
		defaultPage: 1
	},
	
	// 购物车配置
	cart: {
		// 最大商品种类数
		maxItems: 50,
		// 单个商品最大数量
		maxQuantity: 99
	},
	
	// 订单配置
	order: {
		// 未支付订单自动取消时间（分钟）
		autoCancelTime: 30,
		// 自动确认收货时间（天）
		autoConfirmTime: 7
	}
}
