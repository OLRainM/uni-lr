<template>
  <div class="orders-container">
    <div class="page-header">
      <h2>订单管理</h2>
    </div>
    
    <!-- 搜索栏 -->
    <div class="card-container search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="客户姓名">
          <el-input v-model="searchForm.userName" placeholder="请输入客户姓名" clearable />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待付款" value="unpaid" />
            <el-option label="待发货" value="unshipped" />
            <el-option label="已发货" value="shipped" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 订单列表 -->
    <div class="card-container">
      <el-table :data="orderList" stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column label="商品信息" min-width="250">
          <template #default="scope">
            <div class="goods-info" v-for="item in scope.row.goods" :key="item.id">
              <el-image :src="item.image" style="width: 50px; height: 50px; border-radius: 4px;" />
              <div class="goods-detail">
                <div class="goods-name">{{ item.name }}</div>
                <div class="goods-spec">{{ item.spec }} × {{ item.quantity }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="收货人" width="150">
          <template #default="scope">
            <div>{{ scope.row.address.name }}</div>
            <div style="color: #909399; font-size: 12px;">{{ scope.row.address.phone }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" :icon="View" @click="handleView(scope.row)">查看</el-button>
            <el-button
              v-if="scope.row.status === 'unshipped'"
              type="success"
              size="small"
              @click="handleShip(scope.row)"
            >
              发货
            </el-button>
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
    
    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">
            {{ currentOrder.statusText }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ formatTime(currentOrder.payTime) }}</el-descriptions-item>
        <el-descriptions-item label="收货人" :span="2">
          {{ currentOrder.address.name }} {{ currentOrder.address.phone }}
        </el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">
          {{ currentOrder.address.fullAddress }}
        </el-descriptions-item>
      </el-descriptions>
      
      <el-divider content-position="left">商品信息</el-divider>
      <el-table :data="currentOrder?.goods" border>
        <el-table-column label="商品图片" width="100">
          <template #default="scope">
            <el-image :src="scope.row.image" style="width: 60px; height: 60px;" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" />
        <el-table-column prop="spec" label="规格" width="100" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="小计" width="100">
          <template #default="scope">
            ¥{{ (parseFloat(scope.row.price) * scope.row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
      </el-table>
      
      <div style="margin-top: 20px; text-align: right;">
        <div>商品总额：¥{{ currentOrder?.goodsTotal }}</div>
        <div>运费：¥{{ currentOrder?.freight }}</div>
        <div style="font-size: 18px; font-weight: bold; color: #f56c6c; margin-top: 10px;">
          订单总额：¥{{ currentOrder?.totalAmount }}
        </div>
      </div>
    </el-dialog>
    
    <!-- 发货对话框 -->
    <el-dialog v-model="shipVisible" title="订单发货" width="500px">
      <el-form :model="shipForm" :rules="shipRules" ref="shipFormRef" label-width="100px">
        <el-form-item label="物流公司" prop="company">
          <el-select v-model="shipForm.company" placeholder="请选择物流公司" style="width: 100%;">
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="圆通速递" value="圆通速递" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="韵达快递" value="韵达快递" />
            <el-option label="申通快递" value="申通快递" />
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号" prop="trackingNo">
          <el-input v-model="shipForm.trackingNo" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button type="primary" @click="handleShipSubmit">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh, View } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getOrderList, shipOrder } from '@/api/order'

const loading = ref(false)
const detailVisible = ref(false)
const shipVisible = ref(false)
const currentOrder = ref(null)
const shipFormRef = ref(null)

const searchForm = reactive({
  orderNo: '',
  userName: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const shipForm = reactive({
  company: '',
  trackingNo: ''
})

const shipRules = {
  company: [{ required: true, message: '请选择物流公司', trigger: 'change' }],
  trackingNo: [{ required: true, message: '请输入物流单号', trigger: 'blur' }]
}

const orderList = ref([])

// 加载订单列表
const loadOrderList = async () => {
  loading.value = true
  
  try {
    // 准备请求参数
    const params = {
      pageNum: pagination.page,
      pageSize: pagination.pageSize,
      status: searchForm.status ? getStatusNumber(searchForm.status) : null
    }
    
    // 调用API
    const result = await getOrderList(params)
    
    // 处理订单数据
    orderList.value = (result.records || []).map(order => ({
      id: order.id,
      orderNo: order.orderNo,
      status: getStatusString(order.status),
      statusText: getStatusText(order.status),
      createTime: order.createTime,
      payTime: order.payTime,
      totalAmount: order.totalAmount,
      goodsTotal: order.goodsAmount,
      freight: order.freight,
      shipCompany: order.shipCompany,
      shipNo: order.shipNo,
      shipTime: order.shipTime,
      address: {
        name: order.receiverName,
        phone: order.receiverPhone,
        fullAddress: `${order.receiverProvince} ${order.receiverCity} ${order.receiverDistrict} ${order.receiverAddress}`
      },
      goods: [] // 商品信息需要从订单详情获取
    }))
    
    pagination.total = result.total || 0
  } catch (error) {
    console.error('加载订单列表失败:', error)
    ElMessage.error('加载订单列表失败，请稍后重试')
    orderList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', { hour12: false })
}

// 状态映射函数：数字 -> 字符串
const getStatusString = (statusNumber) => {
  const map = {
    0: 'unpaid',
    1: 'unshipped',
    2: 'shipped',
    3: 'completed',
    4: 'cancelled'
  }
  return map[statusNumber] || 'unknown'
}

// 状态映射函数：字符串 -> 数字
const getStatusNumber = (statusString) => {
  const map = {
    'unpaid': 0,
    'unshipped': 1,
    'shipped': 2,
    'completed': 3,
    'cancelled': 4
  }
  return map[statusString]
}

// 状态映射函数：数字 -> 中文文本
const getStatusText = (statusNumber) => {
  const map = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    3: '已完成',
    4: '已取消'
  }
  return map[statusNumber] || '未知状态'
}

const getStatusType = (status) => {
  const types = {
    unpaid: 'warning',
    unshipped: 'primary',
    shipped: 'info',
    completed: 'success',
    cancelled: 'info'
  }
  return types[status] || 'info'
}

const handleSearch = () => {
  pagination.page = 1
  loadOrderList()
}

const handleReset = () => {
  searchForm.orderNo = ''
  searchForm.userName = ''
  searchForm.status = ''
  handleSearch()
}

const handleView = (row) => {
  currentOrder.value = row
  detailVisible.value = true
}

const handleShip = (row) => {
  currentOrder.value = row
  shipVisible.value = true
}

const handleShipSubmit = async () => {
  await shipFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 调用发货API
        await shipOrder(currentOrder.value.id, {
          shipCompany: shipForm.company,
          shipNo: shipForm.trackingNo
        })
        
        ElMessage.success('发货成功')
        shipVisible.value = false
        
        // 重置表单
        shipForm.company = ''
        shipForm.trackingNo = ''
        
        // 刷新订单列表
        loadOrderList()
      } catch (error) {
        console.error('发货失败:', error)
        ElMessage.error('发货失败，请稍后重试')
      }
    }
  })
}

const handleSizeChange = () => {
  loadOrderList()
}

const handleCurrentChange = () => {
  loadOrderList()
}

onMounted(() => {
  loadOrderList()
})
</script>

<style scoped lang="scss">
.orders-container {
  .search-bar {
    margin-bottom: 20px;
  }
  
  .goods-info {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 10px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .goods-detail {
      flex: 1;
      
      .goods-name {
        font-size: 14px;
        margin-bottom: 4px;
      }
      
      .goods-spec {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}
</style>
