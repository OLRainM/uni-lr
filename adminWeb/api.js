/**
 * api.js — 统一请求层
 * 后端地址：http://localhost:8080/api
 * 鉴权方式：Admin-Key 请求头（值为登录时服务端返回的秘钥 token）
 */
const BASE_URL = 'http://localhost:8080/api'

let _token = ''

function setToken(t) { _token = t; localStorage.setItem('adminToken', t); }
function getToken()  { return _token || localStorage.getItem('adminToken') || ''; }

async function request(method, path, data) {
  const url = BASE_URL + path
  const headers = { 'Content-Type': 'application/json' }

  // 使用 Admin-Key 请求头进行鉴权（而非 Authorization Bearer）
  const tk = getToken()
  if (tk) headers['Admin-Key'] = tk

  const opts = { method, headers }
  if (data && method !== 'GET') opts.body = JSON.stringify(data)

  let fullUrl = url
  if (data && method === 'GET') {
    const qs = new URLSearchParams(data).toString()
    fullUrl = url + (qs ? '?' + qs : '')
  }

  try {
    const res = await fetch(fullUrl, opts)
    const json = await res.json()
    if (json.code === 200 || json.code === 0) return json.data
    throw new Error(json.message || '请求失败')
  } catch (e) {
    console.error('[API]', method, path, e)
    throw e
  }
}

const api = {
  get:    (path, params) => request('GET',    path, params),
  post:   (path, data)   => request('POST',   path, data),
  put:    (path, data)   => request('PUT',     path, data),
  delete: (path)         => request('DELETE',  path),

  /* ---- 登录（无需 Admin-Key）---- */
  adminLogin: (secretKey) => request('POST', '/admin/login', { secretKey }),

  /* ---- 商品（Admin 专用接口）---- */
  getProducts:   (p)      => api.get('/admin/product/list', p),
  getProduct:    (id)     => api.get(`/product/detail/${id}`),
  createProduct: (d)      => api.post('/admin/product/create', d),
  updateProduct: (id, d)  => api.post(`/admin/product/update/${id}`, d),
  onShelf:       (id)     => api.post(`/admin/product/on/${id}`),
  offShelf:      (id)     => api.post(`/admin/product/off/${id}`),

  /* ---- 订单 ---- */
  getAllOrders:       (p)       => api.get('/admin/order/list', p),
  getOrder:          (id)      => api.get(`/admin/order/detail/${id}`),
  shipOrder:         (id, d)   => api.post(`/admin/order/ship/${id}`, d),
  updateOrderStatus: (id, st)  => api.post(`/admin/order/status/${id}`, { status: st }),

  /* ---- 用户 ---- */
  getUsers:  (p)  => api.get('/admin/user/list', p),
  banUser:   (id) => api.post(`/admin/user/ban/${id}`),
  unbanUser: (id) => api.post(`/admin/user/unban/${id}`),

  /* ---- 统计 ---- */
  getDashboard: () => api.get('/admin/dashboard'),
}

window._api = api
window._setToken = setToken
window._getToken = getToken

