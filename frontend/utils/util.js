// 工具函数

// 格式化时间
export const formatTime = (date) => {
	const year = date.getFullYear()
	const month = date.getMonth() + 1
	const day = date.getDate()
	const hour = date.getHours()
	const minute = date.getMinutes()
	const second = date.getSeconds()

	return `${[year, month, day].map(formatNumber).join('-')} ${[hour, minute, second].map(formatNumber).join(':')}`
}

// 格式化数字
const formatNumber = n => {
	n = n.toString()
	return n[1] ? n : `0${n}`
}

// 格式化价格
export const formatPrice = (price) => {
	return (price / 100).toFixed(2)
}

// 防抖函数
export const debounce = (func, wait) => {
	let timeout
	return function () {
		const context = this
		const args = arguments
		clearTimeout(timeout)
		timeout = setTimeout(() => {
			func.apply(context, args)
		}, wait)
	}
}

// 节流函数
export const throttle = (func, wait) => {
	let previous = 0
	return function () {
		const now = Date.now()
		const context = this
		const args = arguments
		if (now - previous > wait) {
			func.apply(context, args)
			previous = now
		}
	}
}

// 深拷贝
export const deepClone = (obj) => {
	if (obj === null) return null
	if (typeof obj !== 'object') return obj
	if (obj.constructor === Date) return new Date(obj)
	if (obj.constructor === RegExp) return new RegExp(obj)
	const newObj = new obj.constructor()
	for (let key in obj) {
		if (obj.hasOwnProperty(key)) {
			newObj[key] = deepClone(obj[key])
		}
	}
	return newObj
}
