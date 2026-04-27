<template>
  <div class="products-container">
    <div class="page-header">
      <h2>商品管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">添加商品</el-button>
    </div>
    
    <!-- 搜索栏 -->
    <div class="card-container search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.name" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="请选择分类" clearable>
            <el-option label="冬虫夏草" value="dongchongxiacao" />
            <el-option label="藏红花" value="zanghonghua" />
            <el-option label="雪莲花" value="xuelianhua" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="上架" value="1" />
            <el-option label="下架" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 商品列表 -->
    <div class="card-container">
      <el-table :data="productList" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品图片" width="100">
          <template #default="scope">
            <el-image
              :src="scope.row.image"
              :preview-src-list="[scope.row.image]"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px;"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="200" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="sales" label="销量" width="100" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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
    
    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="productForm" :rules="rules" ref="productFormRef" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        
        <el-form-item label="商品分类" prop="category">
          <el-select v-model="productForm.category" placeholder="请选择分类" style="width: 100%;">
            <el-option label="冬虫夏草" value="冬虫夏草" />
            <el-option label="藏红花" value="藏红花" />
            <el-option label="雪莲花" value="雪莲花" />
            <el-option label="藏药材" value="藏药材" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="商品价格" prop="price">
          <el-input-number v-model="productForm.price" :min="0" :precision="2" :step="10" />
        </el-form-item>
        
        <el-form-item label="商品库存" prop="stock">
          <el-input-number v-model="productForm.stock" :min="0" :step="1" />
        </el-form-item>
        
        <el-form-item label="商品简介" prop="subtitle">
          <el-input v-model="productForm.subtitle" type="textarea" :rows="3" placeholder="请输入商品简介" />
        </el-form-item>
        
        <el-form-item label="商品详情" prop="detail">
          <el-input v-model="productForm.detail" type="textarea" :rows="5" placeholder="请输入商品详情" />
        </el-form-item>
        
        <el-form-item label="商品图片" prop="image">
          <el-input v-model="productForm.image" placeholder="请输入图片URL">
            <template #append>
              <el-button :icon="Picture">上传</el-button>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="上架状态">
          <el-switch v-model="productForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus, Search, Refresh, Edit, Delete, Picture } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加商品')
const productFormRef = ref(null)

const searchForm = reactive({
  name: '',
  category: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const productList = ref([])

const productForm = reactive({
  id: null,
  name: '',
  category: '',
  price: 0,
  stock: 0,
  subtitle: '',
  detail: '',
  image: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入商品价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入商品库存', trigger: 'blur' }]
}

// 加载商品列表
const loadProductList = () => {
  loading.value = true
  
  // 模拟数据
  setTimeout(() => {
    productList.value = [
      {
        id: 1,
        name: '特级冬虫夏草 西藏那曲产',
        category: '冬虫夏草',
        price: '1280.00',
        stock: 100,
        sales: 999,
        image: 'https://img.yzcdn.cn/vant/cat.jpeg',
        status: 1,
        subtitle: '3.5条/克 特级品质 野生采集',
        detail: '西藏那曲海拔4500米以上高原野生采集...'
      },
      {
        id: 2,
        name: '顶级藏红花',
        category: '藏红花',
        price: '680.00',
        stock: 50,
        sales: 568,
        image: 'https://img.yzcdn.cn/vant/cat.jpeg',
        status: 1,
        subtitle: '伊朗进口 品质保证',
        detail: '伊朗进口顶级藏红花...'
      },
      {
        id: 3,
        name: '天山雪莲花',
        category: '雪莲花',
        price: '980.00',
        stock: 30,
        sales: 234,
        image: 'https://img.yzcdn.cn/vant/cat.jpeg',
        status: 1,
        subtitle: '天山野生 药用价值高',
        detail: '天山海拔3000米以上野生雪莲花...'
      }
    ]
    pagination.total = 3
    loading.value = false
  }, 500)
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadProductList()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.category = ''
  searchForm.status = ''
  handleSearch()
}

// 添加商品
const handleAdd = () => {
  dialogTitle.value = '添加商品'
  resetForm()
  dialogVisible.value = true
}

// 编辑商品
const handleEdit = (row) => {
  dialogTitle.value = '编辑商品'
  Object.assign(productForm, row)
  dialogVisible.value = true
}

// 删除商品
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    loadProductList()
  }).catch(() => {})
}

// 状态切换
const handleStatusChange = (row) => {
  ElMessage.success(row.status === 1 ? '已上架' : '已下架')
}

// 提交表单
const handleSubmit = async () => {
  await productFormRef.value.validate((valid) => {
    if (valid) {
      if (productForm.id) {
        ElMessage.success('编辑成功')
      } else {
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadProductList()
    }
  })
}

// 关闭对话框
const handleDialogClose = () => {
  resetForm()
}

// 重置表单
const resetForm = () => {
  productForm.id = null
  productForm.name = ''
  productForm.category = ''
  productForm.price = 0
  productForm.stock = 0
  productForm.subtitle = ''
  productForm.detail = ''
  productForm.image = ''
  productForm.status = 1
  productFormRef.value?.resetFields()
}

// 分页
const handleSizeChange = () => {
  loadProductList()
}

const handleCurrentChange = () => {
  loadProductList()
}

onMounted(() => {
  loadProductList()
})
</script>

<style scoped lang="scss">
.products-container {
  .search-bar {
    margin-bottom: 20px;
  }
}
</style>
