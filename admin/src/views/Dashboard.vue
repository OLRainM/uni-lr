<template>
  <div class="dashboard-container">
    <div class="page-header">
      <h2>数据概览</h2>
      <el-button type="primary" :icon="Refresh" @click="loadData">刷新数据</el-button>
    </div>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <div class="stat-card" style="border-left: 4px solid #409eff;">
          <div class="stat-icon" style="color: #409eff;">
            <el-icon><ShoppingCart /></el-icon>
          </div>
          <div class="stat-value">{{ stats.totalOrders }}</div>
          <div class="stat-label">总订单数</div>
        </div>
      </el-col>
      
      <el-col :span="6">
        <div class="stat-card" style="border-left: 4px solid #67c23a;">
          <div class="stat-icon" style="color: #67c23a;">
            <el-icon><Money /></el-icon>
          </div>
          <div class="stat-value">¥{{ stats.totalRevenue }}</div>
          <div class="stat-label">总收入</div>
        </div>
      </el-col>
      
      <el-col :span="6">
        <div class="stat-card" style="border-left: 4px solid #e6a23c;">
          <div class="stat-icon" style="color: #e6a23c;">
            <el-icon><Goods /></el-icon>
          </div>
          <div class="stat-value">{{ stats.totalProducts }}</div>
          <div class="stat-label">商品总数</div>
        </div>
      </el-col>
      
      <el-col :span="6">
        <div class="stat-card" style="border-left: 4px solid #f56c6c;">
          <div class="stat-icon" style="color: #f56c6c;">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-value">{{ stats.totalUsers }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <div class="card-container">
          <h3 class="card-title">订单趋势</h3>
          <div ref="orderChartRef" style="height: 300px;"></div>
        </div>
      </el-col>
      
      <el-col :span="12">
        <div class="card-container">
          <h3 class="card-title">收入统计</h3>
          <div ref="revenueChartRef" style="height: 300px;"></div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 最近订单 -->
    <div class="card-container recent-orders">
      <h3 class="card-title">最近订单</h3>
      <el-table :data="recentOrders" stripe>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userName" label="客户" width="120" />
        <el-table-column prop="totalAmount" label="金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="statusText" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const orderChartRef = ref(null)
const revenueChartRef = ref(null)

const stats = ref({
  totalOrders: 0,
  totalRevenue: '0.00',
  totalProducts: 0,
  totalUsers: 0
})

const recentOrders = ref([])

// 加载数据
const loadData = () => {
  // 模拟数据
  stats.value = {
    totalOrders: 156,
    totalRevenue: '125,680.00',
    totalProducts: 48,
    totalUsers: 89
  }
  
  recentOrders.value = [
    {
      orderNo: '202412020001',
      userName: '张三',
      totalAmount: '1,280.00',
      status: 'unshipped',
      statusText: '待发货',
      createTime: '2024-12-02 10:30:00'
    },
    {
      orderNo: '202412020002',
      userName: '李四',
      totalAmount: '2,560.00',
      status: 'shipped',
      statusText: '已发货',
      createTime: '2024-12-02 09:15:00'
    },
    {
      orderNo: '202412010003',
      userName: '王五',
      totalAmount: '768.00',
      status: 'completed',
      statusText: '已完成',
      createTime: '2024-12-01 16:45:00'
    }
  ]
  
  // 绘制图表
  drawCharts()
}

// 绘制图表
const drawCharts = () => {
  // 订单趋势图
  if (orderChartRef.value) {
    const orderChart = echarts.init(orderChartRef.value)
    orderChart.setOption({
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '订单数',
          type: 'line',
          data: [12, 18, 15, 23, 20, 28, 25],
          smooth: true,
          itemStyle: {
            color: '#409eff'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
            ])
          }
        }
      ]
    })
  }
  
  // 收入统计图
  if (revenueChartRef.value) {
    const revenueChart = echarts.init(revenueChartRef.value)
    revenueChart.setOption({
      tooltip: {
        trigger: 'axis',
        formatter: '¥{c}'
      },
      xAxis: {
        type: 'category',
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '收入',
          type: 'bar',
          data: [15360, 23040, 19200, 29440, 25600, 35840, 32000],
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#67c23a' },
              { offset: 1, color: '#85ce61' }
            ])
          }
        }
      ]
    })
  }
}

const getStatusType = (status) => {
  const types = {
    unpaid: 'warning',
    unshipped: 'primary',
    shipped: 'info',
    completed: 'success'
  }
  return types[status] || 'info'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.dashboard-container {
  .stat-row {
    margin-bottom: 20px;
  }
  
  .chart-row {
    margin-bottom: 20px;
  }
  
  .card-title {
    font-size: 16px;
    font-weight: 500;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #ebeef5;
  }
  
  .recent-orders {
    .el-table {
      margin-top: 10px;
    }
  }
}
</style>
