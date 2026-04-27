/* app.js — 主控制器：登录、路由、弹窗、分页、Toast */

/* ===== Toast ===== */
;(function() {
  const el = document.createElement('div')
  el.id = 'toast'
  document.body.appendChild(el)
})()

function showToast(msg, duration = 2000) {
  const el = document.getElementById('toast')
  el.textContent = msg
  el.classList.add('show')
  clearTimeout(el._t)
  el._t = setTimeout(() => el.classList.remove('show'), duration)
}

/* ===== 登录 ===== */
async function doLogin() {
  const key = document.getElementById('loginKey').value.trim()
  if (!key) { showToast('请输入管理秘钥'); return }

  const btn = document.querySelector('#loginMask .btn-primary')
  btn.disabled = true
  btn.textContent = '验证中...'

  try {
    const data = await _api.adminLogin(key)
    // 后端返回 { token: '秘钥值', role: 'admin' }
    _setToken(data.token || key)
    document.getElementById('loginMask').classList.add('hidden')
    document.getElementById('app').classList.remove('hidden')
    switchPage('dashboard', document.querySelector('.nav-item[data-page="dashboard"]'))
  } catch(e) {
    showToast(e.message || '秘钥错误，请重试')
    btn.disabled = false
    btn.textContent = '登 录'
  }
}

function doLogout() {
  if (!confirm('确认退出登录？')) return
  localStorage.removeItem('adminToken')
  _setToken('')  // 同步清除 api.js 内存中的 _token
  document.getElementById('app').classList.add('hidden')
  document.getElementById('loginMask').classList.remove('hidden')
}

// 回车登录
document.addEventListener('keydown', (e) => {
  if (e.key === 'Enter' && !document.getElementById('loginMask').classList.contains('hidden')) {
    doLogin()
  }
})

/* ===== 路由 ===== */
const PAGE_TITLES = {
  dashboard: '数据概览',
  products:  '商品管理',
  orders:    '订单管理',
  users:     '用户管理',
}

const PAGE_RENDERERS = {
  dashboard: renderDashboard,
  products:  renderProducts,
  orders:    renderOrders,
  users:     renderUsers,
}

function switchPage(name, navEl) {
  // 高亮导航
  document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'))
  if (navEl) navEl.classList.add('active')

  // 切换内容区
  document.querySelectorAll('.page').forEach(el => el.classList.remove('active'))
  const target = document.getElementById('page-' + name)
  if (target) target.classList.add('active')

  // 更新标题
  document.getElementById('pageTitle').textContent = PAGE_TITLES[name] || name

  // 渲染页面
  if (PAGE_RENDERERS[name]) PAGE_RENDERERS[name]()
}

/* ===== 弹窗 ===== */
let _modalConfirmCb = null

function openModal(title, bodyHtml, onConfirm, confirmText = '确认') {
  document.getElementById('modalTitle').textContent   = title
  document.getElementById('modalBody').innerHTML       = bodyHtml
  document.getElementById('modalConfirm').textContent = confirmText
  document.getElementById('modalConfirm').style.display = onConfirm ? '' : 'none'
  _modalConfirmCb = onConfirm
  document.getElementById('modal').classList.remove('hidden')
}

function closeModal() {
  document.getElementById('modal').classList.add('hidden')
  _modalConfirmCb = null
}

async function onModalConfirm() {
  if (!_modalConfirmCb) { closeModal(); return }
  const result = await _modalConfirmCb()
  if (result !== false) closeModal()
}

// 点击遮罩关闭
document.getElementById('modal').addEventListener('click', (e) => {
  if (e.target === document.getElementById('modal')) closeModal()
})

/* ===== 分页工具 ===== */
function renderPagination(containerId, currentPage, totalPages, onChange) {
  const el = document.getElementById(containerId)
  if (!el) return
  if (totalPages <= 1) { el.innerHTML = ''; return }

  const pages = []
  // 最多显示 5 个页码
  let start = Math.max(1, currentPage - 2)
  let end   = Math.min(totalPages, start + 4)
  if (end - start < 4) start = Math.max(1, end - 4)

  for (let i = start; i <= end; i++) pages.push(i)

  el.innerHTML = `
    <button ${currentPage===1?'disabled':''} onclick="(${onChange.toString()})(${currentPage-1})">‹</button>
    ${pages.map(p => `<button class="${p===currentPage?'active':''}" onclick="(${onChange.toString()})(${p})">${p}</button>`).join('')}
    <button ${currentPage===totalPages?'disabled':''} onclick="(${onChange.toString()})(${currentPage+1})">›</button>
    <span class="page-info">第 ${currentPage}/${totalPages} 页</span>`
}

// 已有 token 则尝试自动进入，先验证 token 是否仍有效
;(async function autoLogin() {
  const savedToken = _getToken()
  if (!savedToken) return
  try {
    // 用 dashboard 接口探测 token 是否有效
    await _api.getDashboard()
    document.getElementById('loginMask').classList.add('hidden')
    document.getElementById('app').classList.remove('hidden')
    switchPage('dashboard', document.querySelector('.nav-item[data-page="dashboard"]'))
  } catch(e) {
    // token 无效（秘钥已更改），清除后回到登录页
    _setToken('')
    localStorage.removeItem('adminToken')
  }
})()

