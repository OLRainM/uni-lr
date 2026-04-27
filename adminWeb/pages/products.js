/* pages/products.js — 商品管理页 */
let _prodPage = 1, _prodKeyword = '', _prodStatus = ''

function renderProducts() {
  const el = document.getElementById('page-products')
  el.innerHTML = `
    <div class="card">
      <div class="card-header">
        <h3>商品列表</h3>
        <button class="btn btn-primary btn-sm" onclick="openAddProduct()">＋ 新增商品</button>
      </div>
      <div class="card-body">
        <div class="search-bar">
          <input id="prod-keyword" placeholder="搜索商品名称" value="${_prodKeyword}" onkeydown="if(event.key==='Enter')searchProducts()" />
          <select id="prod-status" onchange="searchProducts()">
            <option value="">全部状态</option>
            <option value="1" ${_prodStatus==='1'?'selected':''}>上架中</option>
            <option value="0" ${_prodStatus==='0'?'selected':''}>已下架</option>
          </select>
          <button class="btn btn-primary btn-sm" onclick="searchProducts()">搜索</button>
          <button class="btn btn-default btn-sm" onclick="resetProdSearch()">重置</button>
        </div>
        <div class="table-wrap">
          <table>
            <thead><tr>
              <th>图片</th><th>商品名称</th><th>价格</th><th>库存</th>
              <th>销量</th><th>状态</th><th>操作</th>
            </tr></thead>
            <tbody id="prod-tbody">
              <tr><td colspan="7" style="text-align:center;padding:40px;color:#bbb">加载中...</td></tr>
            </tbody>
          </table>
        </div>
        <div class="pagination" id="prod-pagination"></div>
      </div>
    </div>`
  loadProducts()
}

async function loadProducts() {
  _prodKeyword = document.getElementById('prod-keyword')?.value || ''
  _prodStatus  = document.getElementById('prod-status')?.value  || ''
  const tbody = document.getElementById('prod-tbody')
  tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:40px;color:#bbb">加载中...</td></tr>'

  try {
    const params = { pageNum: _prodPage, pageSize: 10 }
    if (_prodKeyword) params.keyword = _prodKeyword
    if (_prodStatus !== '') params.status = _prodStatus
    const res = await _api.getProducts(params)
    const list = res?.list || []
    const total = res?.total || 0

    if (list.length === 0) {
      tbody.innerHTML = '<tr><td colspan="7"><div class="empty-state"><div class="empty-icon">📦</div><div>暂无商品</div></div></td></tr>'
    } else {
      tbody.innerHTML = list.map(p => `
        <tr>
          <td><img class="product-thumb" src="${p.image || ''}" onerror="this.src='data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 width=%2248%22 height=%2248%22><rect fill=%22%23eee%22 width=%2248%22 height=%2248%22/><text x=%2250%25%22 y=%2255%25%22 font-size=%2218%22 text-anchor=%22middle%22>📦</text></svg>'" /></td>
          <td><div style="font-weight:500">${p.name}</div><div style="color:#999;font-size:12px">${p.subtitle || ''}</div></td>
          <td>¥${p.price}</td>
          <td>${p.stock ?? '—'}</td>
          <td>${p.sales || 0}</td>
          <td>${p.status === 1 ? '<span class="tag tag-success">上架中</span>' : '<span class="tag tag-default">已下架</span>'}</td>
          <td>
            <button class="btn btn-default btn-sm" onclick="openEditProduct(${p.id})" style="margin-right:4px">编辑</button>
            ${p.status === 1
              ? `<button class="btn btn-warning btn-sm" onclick="changeShelf(${p.id},0)">下架</button>`
              : `<button class="btn btn-success btn-sm" onclick="changeShelf(${p.id},1)">上架</button>`}
          </td>
        </tr>`).join('')
    }

    renderPagination('prod-pagination', _prodPage, Math.ceil(total / 10), (p) => { _prodPage = p; loadProducts() })
  } catch(e) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;color:#ff4d4f;padding:40px">加载失败：${e.message}</td></tr>`
  }
}

function searchProducts() { _prodPage = 1; loadProducts() }
function resetProdSearch() {
  document.getElementById('prod-keyword').value = ''
  document.getElementById('prod-status').value  = ''
  _prodKeyword = ''; _prodStatus = ''; _prodPage = 1
  loadProducts()
}

async function changeShelf(id, toStatus) {
  const action = toStatus === 1 ? '上架' : '下架'
  if (!confirm(`确认${action}该商品？`)) return
  try {
    if (toStatus === 1) await _api.onShelf(id)
    else                await _api.offShelf(id)
    showToast(`${action}成功`)
    loadProducts()
  } catch(e) {
    showToast(`${action}失败：` + (e.message || '未知错误'))
  }
}

function openAddProduct() {
  openModal('新增商品', buildProductForm(null), async () => {
    const d = collectProductForm()
    if (!d.name) { showToast('请填写商品名称'); return false }
    try {
      await _api.createProduct(d)
      showToast('新增成功'); loadProducts()
    } catch(e) { showToast('新增失败：' + (e.message || '未知错误')); return false }
  })
}

async function openEditProduct(id) {
  let product = {}
  try { product = await _api.getProduct(id) } catch(e) {}
  openModal('编辑商品', buildProductForm(product), async () => {
    const d = collectProductForm()
    if (!d.name) { showToast('请填写商品名称'); return false }
    try {
      await _api.updateProduct(id, d)
      showToast('保存成功'); loadProducts()
    } catch(e) { showToast('保存失败：' + (e.message || '未知错误')); return false }
  })
}

function buildProductForm(p) {
  return `
    <div class="form-row"><label>商品名称 *</label><input id="f-name" value="${p?.name||''}" placeholder="请输入商品名称"/></div>
    <div class="form-row"><label>副标题</label><input id="f-subtitle" value="${p?.subtitle||''}" placeholder="简短描述"/></div>
    <div class="form-row"><label>价格（元）</label><input id="f-price" type="number" step="0.01" value="${p?.price||''}" placeholder="0.00"/></div>
    <div class="form-row"><label>库存</label><input id="f-stock" type="number" value="${p?.stock||''}" placeholder="0"/></div>
    <div class="form-row"><label>商品图片URL</label><input id="f-image" value="${p?.image||''}" placeholder="https://..."/></div>
    <div class="form-row"><label>状态</label>
      <select id="f-status">
        <option value="1" ${p?.status===1?'selected':''}>上架</option>
        <option value="0" ${p?.status===0?'selected':''}>下架</option>
      </select>
    </div>
    <div class="form-row"><label>商品详情</label><textarea id="f-detail" placeholder="商品详细描述...">${p?.detail||''}</textarea></div>`
}

function collectProductForm() {
  return {
    name:     document.getElementById('f-name').value.trim(),
    subtitle: document.getElementById('f-subtitle').value.trim(),
    price:    parseFloat(document.getElementById('f-price').value) || 0,
    stock:    parseInt(document.getElementById('f-stock').value)   || 0,
    image:    document.getElementById('f-image').value.trim(),
    status:   parseInt(document.getElementById('f-status').value),
    detail:   document.getElementById('f-detail').value.trim(),
  }
}

