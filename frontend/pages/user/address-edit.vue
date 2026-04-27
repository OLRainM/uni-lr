<template>
	<view class="address-edit-page">
		<view class="form-section">
			<view class="form-item">
				<text class="form-label">收货人</text>
				<input class="form-input" v-model="formData.name" placeholder="请输入收货人姓名" />
			</view>
			<view class="form-item">
				<text class="form-label">联系电话</text>
				<input class="form-input" type="number" v-model="formData.phone" placeholder="请输入联系电话" />
			</view>
			<view class="form-item">
				<text class="form-label">所在地区</text>
				<picker mode="multiSelector" :value="pickerValue" :range="pickerRange" @change="handlePickerChange" @columnchange="handleColumnChange">
					<view class="picker-value">
						<text class="form-value" v-if="formData.region">{{ formData.region }}</text>
						<text class="form-placeholder" v-else>请选择省市区</text>
						<text class="form-arrow">></text>
					</view>
				</picker>
			</view>
			<view class="form-item">
				<text class="form-label">详细地址</text>
				<textarea class="form-textarea" v-model="formData.detail" placeholder="请输入详细地址"></textarea>
			</view>
			<view class="form-item checkbox">
				<text class="form-label">设为默认地址</text>
				<checkbox :checked="formData.isDefault" @click="formData.isDefault = !formData.isDefault" />
			</view>
		</view>

		<view class="btn-wrapper">
			<button class="save-btn" @click="handleSave">保存</button>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue'
	import { onLoad } from '@dcloudio/uni-app'

	const addressId = ref('')

	const formData = ref({
		name: '',
		phone: '',
		region: '',
		detail: '',
		isDefault: false
	})

	onLoad((options) => {
		if (options.id) {
			addressId.value = options.id
			loadAddressDetail()
		}
		// 初始化 picker 数据
		initPickerData()
	})

	const loadAddressDetail = () => {
		// 从本地存储加载地址详情用于编辑
		try {
			const addressList = uni.getStorageSync('addressList') || []
			const address = addressList.find(item => item.id === parseInt(addressId.value))
			
			if (address) {
				// 组合省市区
				const region = `${address.province} ${address.city} ${address.district}`
				
				formData.value = {
					name: address.name,
					phone: address.phone,
					region: region.trim(),
					detail: address.detail,
					isDefault: address.isDefault
				}
			}
		} catch (e) {
			console.error('加载地址失败', e)
		}
	}

	// 省市区数据（扩充版，包含全国常用省份）
	const provinces = ref([
		{
			name: '北京市',
			cities: [
				{
					name: '北京市',
					districts: ['东城区', '西城区', '朝阳区', '丰台区', '石景山区', '海淀区', '门头沟区', '房山区', '通州区', '顺义区', '昌平区', '大兴区', '怀柔区', '平谷区', '密云区', '延庆区']
				}
			]
		},
		{
			name: '上海市',
			cities: [
				{
					name: '上海市',
					districts: ['黄浦区', '徐汇区', '长宁区', '静安区', '普陀区', '虹口区', '杨浦区', '闵行区', '宝山区', '嘉定区', '浦东新区', '金山区', '松江区', '青浦区', '奉贤区', '崇明区']
				}
			]
		},
		{
			name: '天津市',
			cities: [
				{
					name: '天津市',
					districts: ['和平区', '河东区', '河西区', '南开区', '河北区', '红桥区', '东丽区', '西青区', '津南区', '北辰区', '武清区', '宝坻区', '滨海新区', '宁河区', '静海区', '蓟州区']
				}
			]
		},
		{
			name: '重庆市',
			cities: [
				{
					name: '重庆市',
					districts: ['渝中区', '大渡口区', '江北区', '沙坪坝区', '九龙坡区', '南岸区', '北碚区', '渝北区', '巴南区', '涪陵区', '綦江区', '大足区', '长寿区', '江津区', '合川区', '永川区', '南川区', '璧山区', '铜梁区', '潼南区', '荣昌区', '开州区', '梁平区', '武隆区']
				}
			]
		},
		{
			name: '广东省',
			cities: [
				{
					name: '广州市',
					districts: ['荔湾区', '越秀区', '海珠区', '天河区', '白云区', '黄埔区', '番禺区', '花都区', '南沙区', '从化区', '增城区']
				},
				{
					name: '深圳市',
					districts: ['罗湖区', '福田区', '南山区', '宝安区', '龙岗区', '盐田区', '龙华区', '坪山区', '光明区']
				},
				{
					name: '东莞市',
					districts: ['莞城区', '南城区', '东城区', '万江区', '石龙镇', '虎门镇', '长安镇', '厚街镇', '塘厦镇', '凤岗镇', '大朗镇', '黄江镇', '清溪镇', '常平镇', '寮步镇', '樟木头镇', '大岭山镇', '石排镇', '茶山镇', '石碣镇', '横沥镇', '企石镇', '桥头镇', '谢岗镇', '东坑镇', '沙田镇', '道滘镇', '洪梅镇', '麻涌镇', '望牛墩镇', '中堂镇', '高埗镇']
				},
				{
					name: '佛山市',
					districts: ['禅城区', '南海区', '顺德区', '三水区', '高明区']
				}
			]
		},
		{
			name: '浙江省',
			cities: [
				{
					name: '杭州市',
					districts: ['上城区', '拱墅区', '西湖区', '滨江区', '萧山区', '余杭区', '临平区', '钱塘区', '富阳区', '临安区', '桐庐县', '淳安县', '建德市']
				},
				{
					name: '宁波市',
					districts: ['海曙区', '江北区', '北仑区', '镇海区', '鄞州区', '奉化区', '余姚市', '慈溪市', '宁海县', '象山县']
				},
				{
					name: '温州市',
					districts: ['鹿城区', '龙湾区', '瓯海区', '洞头区', '瑞安市', '乐清市', '龙港市', '永嘉县', '平阳县', '苍南县', '文成县', '泰顺县']
				}
			]
		},
		{
			name: '江苏省',
			cities: [
				{
					name: '南京市',
					districts: ['玄武区', '秦淮区', '建邺区', '鼓楼区', '浦口区', '栖霞区', '雨花台区', '江宁区', '六合区', '溧水区', '高淳区']
				},
				{
					name: '苏州市',
					districts: ['姑苏区', '虎丘区', '吴中区', '相城区', '吴江区', '昆山市', '常熟市', '张家港市', '太仓市']
				},
				{
					name: '无锡市',
					districts: ['梁溪区', '锡山区', '惠山区', '滨湖区', '新吴区', '江阴市', '宜兴市']
				}
			]
		},
		{
			name: '四川省',
			cities: [
				{
					name: '成都市',
					districts: ['锦江区', '青羊区', '金牛区', '武侯区', '成华区', '龙泉驿区', '青白江区', '新都区', '温江区', '双流区', '郫都区', '新津区', '简阳市', '都江堰市', '彭州市', '邛崃市', '崇州市', '金堂县', '大邑县', '蒲江县']
				},
				{
					name: '绵阳市',
					districts: ['涪城区', '游仙区', '安州区', '江油市', '三台县', '盐亭县', '梓潼县', '北川羌族自治县', '平武县']
				},
				{
					name: '德阳市',
					districts: ['旌阳区', '罗江区', '广汉市', '什邡市', '绵竹市', '中江县']
				}
			]
		},
		{
			name: '云南省',
			cities: [
				{
					name: '昆明市',
					districts: ['五华区', '盘龙区', '官渡区', '西山区', '东川区', '呈贡区', '晋宁区', '安宁市', '富民县', '宜良县', '石林彝族自治县', '嵩明县', '禄劝彝族苗族自治县', '寻甸回族彝族自治县']
				},
				{
					name: '大理白族自治州',
					districts: ['大理市', '漾濞彝族自治县', '祥云县', '宾川县', '弥渡县', '南涧彝族自治县', '巍山彝族回族自治县', '永平县', '云龙县', '洱源县', '剑川县', '鹤庆县']
				},
				{
					name: '丽江市',
					districts: ['古城区', '玉龙纳西族自治县', '永胜县', '华坪县', '宁蒗彝族自治县']
				}
			]
		},
		{
			name: '西藏自治区',
			cities: [
				{
					name: '拉萨市',
					districts: ['城关区', '堆龙德庆区', '达孜区', '林周县', '当雄县', '尼木县', '曲水县', '墨竹工卡县']
				},
				{
					name: '日喀则市',
					districts: ['桑珠孜区', '南木林县', '江孜县', '定日县', '萨迦县', '拉孜县', '昂仁县', '谢通门县', '白朗县', '仁布县', '康马县', '定结县', '仲巴县', '亚东县', '吉隆县', '聂拉木县', '萨嘎县', '岗巴县']
				},
				{
					name: '昌都市',
					districts: ['卡若区', '江达县', '贡觉县', '类乌齐县', '丁青县', '察雅县', '八宿县', '左贡县', '芒康县', '洛隆县', '边坝县']
				},
				{
					name: '林芝市',
					districts: ['巴宜区', '工布江达县', '米林县', '墨脱县', '波密县', '察隅县', '朗县']
				}
			]
		},
		{
			name: '青海省',
			cities: [
				{
					name: '西宁市',
					districts: ['城东区', '城中区', '城西区', '城北区', '湟中区', '湟源县', '大通回族土族自治县']
				},
				{
					name: '海东市',
					districts: ['乐都区', '平安区', '民和回族土族自治县', '互助土族自治县', '化隆回族自治县', '循化撒拉族自治县']
				}
			]
		},
		{
			name: '山东省',
			cities: [
				{
					name: '济南市',
					districts: ['历下区', '市中区', '槐荫区', '天桥区', '历城区', '长清区', '章丘区', '济阳区', '莱芜区', '钢城区', '平阴县', '商河县']
				},
				{
					name: '青岛市',
					districts: ['市南区', '市北区', '黄岛区', '崂山区', '李沧区', '城阳区', '即墨区', '胶州市', '平度市', '莱西市']
				}
			]
		},
		{
			name: '河南省',
			cities: [
				{
					name: '郑州市',
					districts: ['中原区', '二七区', '管城回族区', '金水区', '上街区', '惠济区', '中牟县', '巩义市', '荥阳市', '新密市', '新郑市', '登封市']
				},
				{
					name: '洛阳市',
					districts: ['老城区', '西工区', '瀍河回族区', '涧西区', '吉利区', '洛龙区', '偃师区', '孟津区', '新安县', '栾川县', '嵩县', '汝阳县', '宜阳县', '洛宁县', '伊川县']
				}
			]
		},
		{
			name: '湖北省',
			cities: [
				{
					name: '武汉市',
					districts: ['江岸区', '江汉区', '硚口区', '汉阳区', '武昌区', '青山区', '洪山区', '东西湖区', '汉南区', '蔡甸区', '江夏区', '黄陂区', '新洲区']
				},
				{
					name: '宜昌市',
					districts: ['西陵区', '伍家岗区', '点军区', '猇亭区', '夷陵区', '宜都市', '当阳市', '枝江市', '远安县', '兴山县', '秭归县', '长阳土家族自治县', '五峰土家族自治县']
				}
			]
		},
		{
			name: '湖南省',
			cities: [
				{
					name: '长沙市',
					districts: ['芙蓉区', '天心区', '岳麓区', '开福区', '雨花区', '望城区', '长沙县', '浏阳市', '宁乡市']
				},
				{
					name: '株洲市',
					districts: ['荷塘区', '芦淞区', '石峰区', '天元区', '渌口区', '醴陵市', '攸县', '茶陵县', '炎陵县']
				}
			]
		},
		{
			name: '福建省',
			cities: [
				{
					name: '福州市',
					districts: ['鼓楼区', '台江区', '仓山区', '马尾区', '晋安区', '长乐区', '闽侯县', '连江县', '罗源县', '闽清县', '永泰县', '平潭县', '福清市']
				},
				{
					name: '厦门市',
					districts: ['思明区', '海沧区', '湖里区', '集美区', '同安区', '翔安区']
				}
			]
		},
		{
			name: '陕西省',
			cities: [
				{
					name: '西安市',
					districts: ['新城区', '碑林区', '莲湖区', '灞桥区', '未央区', '雁塔区', '阎良区', '临潼区', '长安区', '高陵区', '鄠邑区', '蓝田县', '周至县']
				},
				{
					name: '宝鸡市',
					districts: ['渭滨区', '金台区', '陈仓区', '凤翔区', '岐山县', '扶风县', '眉县', '陇县', '千阳县', '麟游县', '凤县', '太白县']
				}
			]
		}
	])

	// picker 组件的数据
	const pickerValue = ref([0, 0, 0]) // 当前选中的索引 [省索引, 市索引, 区索引]
	const pickerRange = ref([[], [], []]) // picker 的三列数据

	// 初始化 picker 数据
	const initPickerData = () => {
		// 第一列：所有省份
		pickerRange.value[0] = provinces.value.map(p => p.name)
		
		// 第二列：第一个省份的所有城市
		if (provinces.value.length > 0) {
			pickerRange.value[1] = provinces.value[0].cities.map(c => c.name)
			
			// 第三列：第一个城市的所有区县
			if (provinces.value[0].cities.length > 0) {
				pickerRange.value[2] = provinces.value[0].cities[0].districts
			}
		}
	}

	// 当某一列的值改变时触发
	const handleColumnChange = (e) => {
		const column = e.detail.column // 改变的列
		const value = e.detail.value // 改变后的值
		
		const newPickerValue = [...pickerValue.value]
		newPickerValue[column] = value
		
		if (column === 0) {
			// 省份改变，更新城市和区县列表
			const newCities = provinces.value[value].cities
			pickerRange.value[1] = newCities.map(c => c.name)
			
			// 重置城市和区县索引
			newPickerValue[1] = 0
			newPickerValue[2] = 0
			
			// 更新区县列表
			if (newCities.length > 0) {
				pickerRange.value[2] = newCities[0].districts
			}
		} else if (column === 1) {
			// 城市改变，更新区县列表
			const provinceIndex = newPickerValue[0]
			const cityIndex = value
			const newDistricts = provinces.value[provinceIndex].cities[cityIndex].districts
			pickerRange.value[2] = newDistricts
			
			// 重置区县索引
			newPickerValue[2] = 0
		}
		
		pickerValue.value = newPickerValue
	}

	// picker 确认选择
	const handlePickerChange = (e) => {
		const [provinceIndex, cityIndex, districtIndex] = e.detail.value
		
		const province = provinces.value[provinceIndex].name
		const city = provinces.value[provinceIndex].cities[cityIndex].name
		const district = provinces.value[provinceIndex].cities[cityIndex].districts[districtIndex]
		
		// 更新表单数据
		formData.value.region = `${province} ${city} ${district}`
		
		// 保存选中的索引
		pickerValue.value = e.detail.value
	}

	const handleSave = () => {
		// 验证表单
		if (!formData.value.name) {
			uni.showToast({
				title: '请输入收货人姓名',
				icon: 'none'
			})
			return
		}
		if (!formData.value.phone) {
			uni.showToast({
				title: '请输入联系电话',
				icon: 'none'
			})
			return
		}
		if (!/^1[3-9]\d{9}$/.test(formData.value.phone)) {
			uni.showToast({
				title: '请输入正确的手机号',
				icon: 'none'
			})
			return
		}
		if (!formData.value.region) {
			uni.showToast({
				title: '请选择所在地区',
				icon: 'none'
			})
			return
		}
		if (!formData.value.detail) {
			uni.showToast({
				title: '请输入详细地址',
				icon: 'none'
			})
			return
		}

		// 保存地址到本地存储
		try {
			// 获取现有地址列表
			let addressList = uni.getStorageSync('addressList') || []
			
			// 解析省市区
			const regionParts = formData.value.region.split(' ')
			const addressData = {
				name: formData.value.name,
				phone: formData.value.phone,
				province: regionParts[0] || '',
				city: regionParts[1] || '',
				district: regionParts[2] || '',
				detail: formData.value.detail,
				isDefault: formData.value.isDefault
			}
			
			if (addressId.value) {
				// 编辑模式：更新现有地址
				const index = addressList.findIndex(item => item.id === parseInt(addressId.value))
				if (index !== -1) {
					addressData.id = parseInt(addressId.value)
					addressList[index] = addressData
				}
			} else {
				// 新增模式：生成新ID并添加
				const maxId = addressList.length > 0 
					? Math.max(...addressList.map(item => item.id)) 
					: 0
				addressData.id = maxId + 1
				
				// 如果设为默认地址，取消其他地址的默认状态
				if (addressData.isDefault) {
					addressList.forEach(item => {
						item.isDefault = false
					})
				}
				
				addressList.push(addressData)
			}
			
			// 保存到本地存储
			uni.setStorageSync('addressList', addressList)
			
			uni.showToast({
				title: '保存成功',
				icon: 'success'
			})

			setTimeout(() => {
				uni.navigateBack()
			}, 1500)
		} catch (e) {
			console.error('保存地址失败', e)
			uni.showToast({
				title: '保存失败，请重试',
				icon: 'none'
			})
		}
	}
</script>

<style lang="scss" scoped>
	.address-edit-page {
		min-height: 100vh;
		background: #f5f5f5;
		padding-bottom: 120rpx;
	}

	.form-section {
		background: #fff;
		margin-top: 20rpx;
	}

	.form-item {
		display: flex;
		align-items: center;
		padding: 30rpx;
		border-bottom: 1rpx solid #f5f5f5;

		&.checkbox {
			justify-content: space-between;
		}

		&:last-child {
			border-bottom: none;
		}
		
		picker {
			flex: 1;
		}
	}
	
	.picker-value {
		display: flex;
		align-items: center;
		flex: 1;
	}

	.form-label {
		width: 160rpx;
		font-size: 28rpx;
		color: #333;
	}

	.form-input {
		flex: 1;
		font-size: 28rpx;
		color: #333;
	}

	.form-value {
		flex: 1;
		font-size: 28rpx;
		color: #333;
	}

	.form-placeholder {
		flex: 1;
		font-size: 28rpx;
		color: #999;
	}

	.form-arrow {
		font-size: 24rpx;
		color: #999;
		margin-left: 12rpx;
	}

	.form-textarea {
		flex: 1;
		min-height: 120rpx;
		font-size: 28rpx;
		color: #333;
	}

	.btn-wrapper {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		padding: 20rpx;
		background: #fff;
		border-top: 1rpx solid #eee;
	}

	.save-btn {
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
