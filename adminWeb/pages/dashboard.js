/* pages/dashboard.js — 数据概览页 */
function renderDashboard() {
  const el = document.getElementById('page-dashboard')

  const stats = [
    { icon: '📦', label: '在售商品', value: '—', color: '#fff7e6', key: 'products' },
    { icon: '🧾', label: '总订单数', value: '—', color: '#f0f9ff', key: 'orders' },
    { icon: '👥', label: '注册用户', value: '—', color: '#f6ffed', key: 'users' },
    { icon: '💰', label: '累计销售额', value: '—', color: '#fff2f0', key: 'revenue' },
  ]

  el.innerHTML = `
    <div class="stat-grid">
      ${stats.map(s => `
        <div class="stat-card">
          <div class="stat-icon" style="background:${s.color}">${s.icon}</div>
          <div class="stat-info">
            <div class="stat-value" id="stat-${s.key}">${s.value}</div>
            <div class="stat-label">${s.label}</div>
          </div>
        </div>`).join('')}
    </div>

    <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px">
      <div class="card">
        <div class="card-header"><h3>🕒 最近订单</h3></div>
        <div class="card-body" id="dash-recent-orders">
          <div class="empty-state"><div class="empty-icon">⏳</div><div>加载中...</div></div>
        </div>
      </div>
      <div class="card">
        <div class="card-header"><h3>🔥 热销商品 Top5</h3></div>
        <div class="card-body" id="dash-top-products">
          <div class="empty-state"><div class="empty-icon">⏳</div><div>加载中...</div></div>
        </div>
      </div>
    </div>
  `

  // 加载真实数据
  loadDashboardData()
}

async function loadDashboardData() {
  // 1. 通过 /admin/dashboard 一次性获取统计数据
  try {
    const data = await _api.getDashboard()
    if (data) {
      document.getElementById('stat-products').textContent = data.products ?? '—'
      document.getElementById('stat-orders').textContent   = data.orders   ?? '—'
      document.getElementById('stat-users').textContent    = data.users    ?? '—'
      const rev = data.revenue != null ? '¥' + Number(data.revenue).toFixed(2) : '¥—'
      document.getElementById('stat-revenue').textContent  = rev
    }
  } catch(e) {
    console.warn('[Dashboard] 统计接口失败', e)
    document.getElementById('stat-products').textContent = 'N/A'
    document.getElementById('stat-orders').textContent   = 'N/A'
    document.getElementById('stat-users').textContent    = 'N/A'
    document.getElementById('stat-revenue').textContent  = 'N/A'
  }

  // 2. 最近订单（取最新 5 条）
  try {
    const res = await _api.getAllOrders({ pageNum: 1, pageSize: 5 })
    renderRecentOrders(res?.list || [])
  } catch(e) {
    document.getElementById('dash-recent-orders').innerHTML =
      '<div class="empty-state"><div class="empty-icon">📭</div><div>加载失败</div></div>'
  }

  // 3. 热销商品 Top5（按销量排序）
  try {
    const res = await _api.getProducts({ pageNum: 1, pageSize: 5, sort: 2 })
    renderTopProducts(res?.list || [])
  } catch(e) {
    document.getElementById('dash-top-products').innerHTML =
      '<div class="empty-state"><div class="empty-icon">📦</div><div>加载失败</div></div>'
  }
}

function renderRecentOrders(list) {
  const el = document.getElementById('dash-recent-orders')
  if (!list || list.length === 0) {
    el.innerHTML = '<div class="empty-state"><div class="empty-icon">📭</div><div>暂无订单</div></div>'
    return
  }
  el.innerHTML = `<table><thead><tr><th>订单号</th><th>金额</th><th>状态</th></tr></thead><tbody>
    ${list.map(o => `<tr>
      <td style="font-size:12px;color:#888">${o.orderNo || o.id}</td>
      <td>¥${o.totalAmount || '0.00'}</td>
      <td>${getOrderStatusTag(o.status)}</td>
    </tr>`).join('')}
  </tbody></table>`
}

function renderTopProducts(list) {
  const el = document.getElementById('dash-top-products')
  if (!list || list.length === 0) {
    el.innerHTML = '<div class="empty-state"><div class="empty-icon">📦</div><div>暂无数据</div></div>'
    return
  }
  el.innerHTML = `<table><thead><tr><th>商品名称</th><th>销量</th><th>库存</th></tr></thead><tbody>
    ${list.map((p, i) => `<tr>
      <td><span style="color:#cd853f;margin-right:6px">${i + 1}</span>${p.name}</td>
      <td>${p.sales || 0}</td>
      <td>${p.stock ?? '—'}</td>
    </tr>`).join('')}
  </tbody></table>`
}

function getOrderStatusTag(status) {
  const map = {
    0: ['tag-warning', '待付款'],
    1: ['tag-info',    '待发货'],
    2: ['tag-info',    '待收货'],
    3: ['tag-success', '已完成'],
    4: ['tag-default', '已取消'],
  }
  const [cls, txt] = map[status] || ['tag-default', '未知']
  return `<span class="tag ${cls}">${txt}</span>`
}
