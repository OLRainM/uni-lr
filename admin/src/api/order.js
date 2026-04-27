import request from '../utils/request'

/**
 * 获取订单列表
 */
export function getOrderList(params) {
  return request({
    url: '/order/list',
    method: 'get',
    params: {
      pageNum: params.pageNum,
      pageSize: params.pageSize,
      status: params.status
    }
  })
}

/**
 * 获取订单详情
 */
export function getOrderDetail(id) {
  return request({
    url: `/order/detail/${id}`,
    method: 'get'
  })
}

/**
 * 订单发货
 * @param {number} id - 订单ID
 * @param {object} data - 发货数据 { shipCompany, shipNo }
 */
export function shipOrder(id, data) {
  return request({
    url: `/order/ship/${id}`,
    method: 'post',
    data: {
      shipCompany: data.shipCompany,
      shipNo: data.shipNo
    }
  })
}

/**
 * 取消订单
 */
export function cancelOrder(id) {
  return request({
    url: `/order/cancel/${id}`,
    method: 'post'
  })
}

/**
 * 确认收货
 */
export function confirmOrder(id) {
  return request({
    url: `/order/confirm/${id}`,
    method: 'post'
  })
}

/**
 * 删除订单
 */
export function deleteOrder(id) {
  return request({
    url: `/order/delete/${id}`,
    method: 'post'
  })
}
