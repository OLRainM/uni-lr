/* pages/users.js — 用户管理页 */
let _userPage = 1, _userKeyword = '', _userStatus = ''
let _userListCache = []  // 缓存当前页用户列表，供详情弹窗使用

function renderUsers() {
  const el = document.getElementById('page-users')
  el.innerHTML = `
    <div class="card">
      <div class="card-header"><h3>用户列表</h3></div>
      <div class="card-body">
        <div class="search-bar">
          <input id="user-keyword" placeholder="搜索昵称 / 手机号" value="${_userKeyword}" onkeydown="if(event.key==='Enter')searchUsers()"/>
          <select id="user-status" onchange="searchUsers()">
            <option value="">全部状态</option>
            <option value="0" ${_userStatus==='0'?'selected':''}>正常</option>
            <option value="1" ${_userStatus==='1'?'selected':''}>已封禁</option>
          </select>
          <button class="btn btn-primary btn-sm" onclick="searchUsers()">搜索</button>
          <button class="btn btn-default btn-sm" onclick="resetUserSearch()">重置</button>
        </div>
        <div class="table-wrap">
          <table>
            <thead><tr>
              <th>ID</th><th>昵称</th><th>手机号</th><th>OpenID</th>
              <th>状态</th><th>注册时间</th><th>操作</th>
            </tr></thead>
            <tbody id="user-tbody">
              <tr><td colspan="7" style="text-align:center;padding:40px;color:#bbb">加载中...</td></tr>
            </tbody>
          </table>
        </div>
        <div class="pagination" id="user-pagination"></div>
      </div>
    </div>`
  loadUsers()
}

async function loadUsers() {
  _userKeyword = document.getElementById('user-keyword')?.value || ''
  _userStatus  = document.getElementById('user-status')?.value  || ''
  const tbody = document.getElementById('user-tbody')
  tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:40px;color:#bbb">加载中...</td></tr>'

  let list = [], total = 0
  try {
    const params = { pageNum: _userPage, pageSize: 10 }
    if (_userStatus !== '') params.status = _userStatus
    if (_userKeyword !== '') params.keyword = _userKeyword
    const res = await _api.getUsers(params)
    list = res?.list || []; total = res?.total || 0
    _userListCache = list  // 缓存供详情使用
  } catch(e) {
    tbody.innerHTML = '<tr><td colspan="7"><div class="empty-state"><div class="empty-icon">❌</div><div>加载失败，请检查后端服务</div></div></td></tr>'
    return
  }

  if (list.length === 0) {
    tbody.innerHTML = '<tr><td colspan="7"><div class="empty-state"><div class="empty-icon">👥</div><div>暂无用户</div></div></td></tr>'
  } else {
    tbody.innerHTML = list.map(u => `
      <tr>
        <td style="color:#888">${u.id}</td>
        <td>
          <div style="display:flex;align-items:center;gap:8px">
            <span style="font-size:20px">${u.avatar ? `<img src="${u.avatar}" style="width:28px;height:28px;border-radius:50%;object-fit:cover"/>` : '👤'}</span>
            <span>${u.nickname || '—'}</span>
          </div>
        </td>
        <td>${u.phone || '—'}</td>
        <td style="font-size:11px;color:#aaa;max-width:120px;overflow:hidden;text-overflow:ellipsis" title="${u.openid||''}">${u.openid ? u.openid.substring(0,16)+'…' : '—'}</td>
        <td>${u.status === 1 ? '<span class="tag tag-danger">已封禁</span>' : '<span class="tag tag-success">正常</span>'}</td>
        <td style="color:#888;font-size:12px">${u.createTime || '—'}</td>
        <td>
          <button class="btn btn-default btn-sm" onclick="viewUserDetail(${u.id})" style="margin-right:4px">详情</button>
          ${u.status === 1
            ? `<button class="btn btn-success btn-sm" onclick="toggleBan(${u.id},0,'${u.nickname||'该用户'}')">解封</button>`
            : `<button class="btn btn-danger  btn-sm" onclick="toggleBan(${u.id},1,'${u.nickname||'该用户'}')">封禁</button>`}
        </td>
      </tr>`).join('')
  }

  renderPagination('user-pagination', _userPage, Math.ceil(total / 10), (p) => { _userPage = p; loadUsers() })
}

function searchUsers()     { _userPage = 1; loadUsers() }
function resetUserSearch() {
  document.getElementById('user-keyword').value = ''
  document.getElementById('user-status').value  = ''
  _userKeyword = ''; _userStatus = ''; _userPage = 1
  loadUsers()
}

async function toggleBan(id, toStatus, name) {
  const action = toStatus === 1 ? '封禁' : '解封'
  if (!confirm(`确认${action}用户"${name}"？`)) return
  try {
    if (toStatus === 1) await _api.banUser(id)
    else                await _api.unbanUser(id)
    showToast(`${action}成功`)
  } catch(e) {
    showToast(`${action}失败：` + (e.message || '未知错误'))
  }
  loadUsers()
}

function viewUserDetail(id) {
  const u = _userListCache.find(x => x.id === id) || { id }
  openModal('用户详情', `
    <div style="font-size:13px;line-height:2.2">
      <div><span style="color:#888;width:70px;display:inline-block">用户ID：</span>${u.id}</div>
      <div><span style="color:#888;width:70px;display:inline-block">昵称：</span>${u.nickname||'—'}</div>
      <div><span style="color:#888;width:70px;display:inline-block">手机号：</span>${u.phone||'未绑定'}</div>
      <div><span style="color:#888;width:70px;display:inline-block">OpenID：</span><span style="font-size:11px;color:#aaa">${u.openid||'—'}</span></div>
      <div><span style="color:#888;width:70px;display:inline-block">状态：</span>${u.status===1?'<span class="tag tag-danger">已封禁</span>':'<span class="tag tag-success">正常</span>'}</div>
      <div><span style="color:#888;width:70px;display:inline-block">注册时间：</span>${u.createTime||'—'}</div>
    </div>`, null, '关闭')
}

