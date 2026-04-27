<template>
  <div class="users-container">
    <div class="page-header">
      <h2>用户管理</h2>
    </div>
    
    <!-- 搜索栏 -->
    <div class="card-container search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 用户列表 -->
    <div class="card-container">
      <el-table :data="userList" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="100">
          <template #default="scope">
            <el-avatar :src="scope.row.avatar" />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="orderCount" label="订单数" width="100" />
        <el-table-column prop="totalAmount" label="消费金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" :icon="View" @click="handleView(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </div>
    
    <!-- 用户详情对话框 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="600px">
      <el-descriptions :column="2" border v-if="currentUser">
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="订单数量">{{ currentUser.orderCount }}</el-descriptions-item>
        <el-descriptions-item label="消费金额">¥{{ currentUser.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ currentUser.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh, View } from '@element-plus/icons-vue'

const loading = ref(false)
const detailVisible = ref(false)
const currentUser = ref(null)

const searchForm = reactive({
  username: '',
  phone: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const userList = ref([])

// 加载用户列表
const loadUserList = () => {
  loading.value = true
  
  setTimeout(() => {
    userList.value = [
      {
        id: 1,
        username: '张三',
        phone: '138****8888',
        avatar: 'https://img.yzcdn.cn/vant/cat.jpeg',
        orderCount: 5,
        totalAmount: '6,400.00',
        createTime: '2024-01-15 10:30:00',
        status: 1
      },
      {
        id: 2,
        username: '李四',
        phone: '139****9999',
        avatar: 'https://img.yzcdn.cn/vant/cat.jpeg',
        orderCount: 3,
        totalAmount: '3,840.00',
        createTime: '2024-02-20 14:20:00',
        status: 1
      }
    ]
    pagination.total = 2
    loading.value = false
  }, 500)
}

const handleSearch = () => {
  pagination.page = 1
  loadUserList()
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.phone = ''
  handleSearch()
}

const handleView = (row) => {
  currentUser.value = row
  detailVisible.value = true
}

const handleSizeChange = () => {
  loadUserList()
}

const handleCurrentChange = () => {
  loadUserList()
}

onMounted(() => {
  loadUserList()
})
</script>

<style scoped lang="scss">
.users-container {
  .search-bar {
    margin-bottom: 20px;
  }
}
</style>
