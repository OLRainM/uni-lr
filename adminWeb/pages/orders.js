/* pages/orders.js — 订单管理页 */
let _orderPage = 1, _orderStatus = '', _orderKeyword = ''

/**
 * 格式化时间字符串：将 ISO 格式（含 T）统一为 yyyy-MM-dd HH:mm:ss
 * 后端加 @JsonFormat 后会直接返回格式化值，此函数作为兜底
 */
function formatDateTime(val) {
  if (!val) return '—'
  // 把 T 替换为空格，去掉可能存在的毫秒和时区后缀
  return String(val).replace('T', ' ').replace(/\.\d+.*$/, '').replace(/Z$/, '')
}

function renderOrders() {
  const el = document.getElementById('page-orders')
  el.innerHTML = `
    <div class="card">
      <div class="card-header"><h3>订单列表</h3></div>
      <div class="card-body">
        <div class="search-bar">
          <input id="order-keyword" placeholder="搜索订单号 / 收货人" value="${_orderKeyword}" onkeydown="if(event.key==='Enter')searchOrders()"/>
          <select id="order-status" onchange="searchOrders()">
            <option value="">全部状态</option>
            <option value="0" ${_orderStatus==='0'?'selected':''}>待付款</option>
            <option value="1" ${_orderStatus==='1'?'selected':''}>待发货</option>
            <option value="2" ${_orderStatus==='2'?'selected':''}>待收货</option>
            <option value="3" ${_orderStatus==='3'?'selected':''}>已完成</option>
            <option value="4" ${_orderStatus==='4'?'selected':''}>已取消</option>
          </select>
          <button class="btn btn-primary btn-sm" onclick="searchOrders()">搜索</button>
          <button class="btn btn-default btn-sm" onclick="resetOrderSearch()">重置</button>
        </div>
        <div class="table-wrap">
          <table>
            <thead><tr>
              <th>订单号</th><th>收货人</th><th>联系电话</th>
              <th>金额</th><th>状态</th><th>下单时间</th><th>操作</th>
            </tr></thead>
            <tbody id="order-tbody">
              <tr><td colspan="7" style="text-align:center;padding:40px;color:#bbb">加载中...</td></tr>
            </tbody>
          </table>
        </div>
        <div class="pagination" id="order-pagination"></div>
      </div>
    </div>`
  loadOrders()
}

async function loadOrders() {
  _orderKeyword = document.getElementById('order-keyword')?.value || ''
  _orderStatus  = document.getElementById('order-status')?.value  || ''
  const tbody = document.getElementById('order-tbody')
  tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:40px;color:#bbb">加载中...</td></tr>'

  let list = [], total = 0
  try {
    const params = { pageNum: _orderPage, pageSize: 10 }
    if (_orderStatus !== '') params.status = _orderStatus
    if (_orderKeyword !== '') params.keyword = _orderKeyword
    const res = await _api.getAllOrders(params)
    list = res?.list || []; total = res?.total || 0
  } catch(e) {
    tbody.innerHTML = '<tr><td colspan="7"><div class="empty-state"><div class="empty-icon">❌</div><div>加载失败，请检查后端服务</div></div></td></tr>'
    return
  }

  if (list.length === 0) {
    tbody.innerHTML = '<tr><td colspan="7"><div class="empty-state"><div class="empty-icon">📭</div><div>暂无订单</div></div></td></tr>'
  } else {
    tbody.innerHTML = list.map(o => `
      <tr>
        <td style="font-size:12px;color:#888;font-family:monospace">${o.orderNo || o.id}</td>
        <td>${o.receiverName || '—'}</td>
        <td>${o.receiverPhone || '—'}</td>
        <td style="font-weight:600;color:#cd853f">¥${o.totalAmount || '0.00'}</td>
        <td>${getOrderStatusTag(o.status)}</td>
        <td style="color:#888;font-size:12px">${formatDateTime(o.createTime)}</td>
        <td>
          <button class="btn btn-default btn-sm" onclick="viewOrder(${o.id})" style="margin-right:4px">详情</button>
          ${o.status === 1 ? `<button class="btn btn-primary btn-sm" onclick="openShipOrder(${o.id})">发货</button>` : ''}
          ${o.status === 0 ? `<button class="btn btn-warning btn-sm" onclick="updateOrderStatus(${o.id},4)">取消</button>` : ''}
        </td>
      </tr>`).join('')
  }

  renderPagination('order-pagination', _orderPage, Math.ceil(total / 10), (p) => { _orderPage = p; loadOrders() })
}

function searchOrders()     { _orderPage = 1; loadOrders() }
function resetOrderSearch() {
  document.getElementById('order-keyword').value = ''
  document.getElementById('order-status').value  = ''
  _orderKeyword = ''; _orderStatus = ''; _orderPage = 1
  loadOrders()
}

async function viewOrder(id) {
  let detail = null
  try { detail = await _api.getOrder(id) } catch(e) {}

  const order = detail?.order || {}
  const items  = detail?.items || []
  openModal('订单详情', `
    <div style="margin-bottom:12px">
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:8px;font-size:13px">
        <div><span style="color:#888">订单号：</span>${order.orderNo || id}</div>
        <div><span style="color:#888">状态：</span>${getOrderStatusTag(order.status)}</div>
        <div><span style="color:#888">收货人：</span>${order.receiverName||'—'}</div>
        <div><span style="color:#888">电话：</span>${order.receiverPhone||'—'}</div>
        <div style="grid-column:1/-1"><span style="color:#888">地址：</span>${[order.receiverProvince,order.receiverCity,order.receiverDistrict,order.receiverAddress].filter(Boolean).join('')||'—'}</div>
      </div>
    </div>
    <table style="width:100%">
      <thead><tr><th>商品</th><th>规格</th><th>单价</th><th>数量</th></tr></thead>
      <tbody>${items.length ? items.map(i=>`<tr>
        <td>${i.productName||'—'}</td><td>${i.specName||'—'}</td>
        <td>¥${i.price}</td><td>×${i.quantity}</td>
      </tr>`).join('') : `<tr><td colspan="4" style="text-align:center;color:#bbb;padding:20px">暂无商品信息</td></tr>`}
      </tbody>
    </table>
    <div style="text-align:right;margin-top:12px;font-size:15px;font-weight:600;color:#cd853f">
      合计：¥${order.totalAmount||'—'}
    </div>`, null, '关闭')
}

function openShipOrder(id) {
  openModal('订单发货', `
    <div class="form-row"><label>物流公司</label>
      <select id="ship-company">
        <option>顺丰速运</option><option>中通快递</option>
        <option>圆通速递</option><option>韵达快递</option><option>邮政EMS</option>
      </select>
    </div>
    <div class="form-row"><label>快递单号 *</label>
      <input id="ship-no" placeholder="请输入快递单号"/>
    </div>`, async () => {
    const shipNo = document.getElementById('ship-no').value.trim()
    if (!shipNo) { showToast('请填写快递单号'); return false }
    const company = document.getElementById('ship-company').value
    try {
      await _api.shipOrder(id, { shipCompany: company, shipNo })
      showToast('发货成功'); loadOrders()
    } catch(e) { showToast('发货失败：' + (e.message || '未知错误')); return false }
  })
}

async function updateOrderStatus(id, status) {
  const labels = { 4:'取消' }
  if (!confirm(`确认${labels[status]||'更新'}该订单？`)) return
  try {
    await _api.updateOrderStatus(id, status)
    showToast('操作成功')
  } catch(e) { showToast('操作失败：' + (e.message || '未知错误')) }
  loadOrders()
}

