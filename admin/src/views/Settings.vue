<template>
  <div class="settings-container">
    <div class="page-header">
      <h2>系统设置</h2>
    </div>
    
    <el-tabs v-model="activeTab">
      <!-- 基本设置 -->
      <el-tab-pane label="基本设置" name="basic">
        <div class="card-container">
          <el-form :model="basicForm" label-width="120px">
            <el-form-item label="商城名称">
              <el-input v-model="basicForm.shopName" placeholder="请输入商城名称" style="width: 400px;" />
            </el-form-item>
            
            <el-form-item label="客服电话">
              <el-input v-model="basicForm.phone" placeholder="请输入客服电话" style="width: 400px;" />
            </el-form-item>
            
            <el-form-item label="客服微信">
              <el-input v-model="basicForm.wechat" placeholder="请输入客服微信" style="width: 400px;" />
            </el-form-item>
            
            <el-form-item label="商城公告">
              <el-input
                v-model="basicForm.notice"
                type="textarea"
                :rows="4"
                placeholder="请输入商城公告"
                style="width: 400px;"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSaveBasic">保存设置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
      
      <!-- 配送设置 -->
      <el-tab-pane label="配送设置" name="shipping">
        <div class="card-container">
          <el-form :model="shippingForm" label-width="120px">
            <el-form-item label="默认运费">
              <el-input-number v-model="shippingForm.defaultFee" :min="0" :precision="2" />
              <span style="margin-left: 10px; color: #909399;">元</span>
            </el-form-item>
            
            <el-form-item label="免运费金额">
              <el-input-number v-model="shippingForm.freeThreshold" :min="0" :precision="2" />
              <span style="margin-left: 10px; color: #909399;">元（订单金额超过此金额免运费）</span>
            </el-form-item>
            
            <el-form-item label="配送时效">
              <el-input-number v-model="shippingForm.deliveryDays" :min="1" :max="30" />
              <span style="margin-left: 10px; color: #909399;">天</span>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSaveShipping">保存设置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
      
      <!-- 支付设置 -->
      <el-tab-pane label="支付设置" name="payment">
        <div class="card-container">
          <el-form :model="paymentForm" label-width="120px">
            <el-form-item label="微信支付">
              <el-switch v-model="paymentForm.wechatPay" />
            </el-form-item>
            
            <el-form-item label="支付宝支付">
              <el-switch v-model="paymentForm.alipay" />
            </el-form-item>
            
            <el-form-item label="自动确认收货">
              <el-input-number v-model="paymentForm.autoConfirmDays" :min="1" :max="30" />
              <span style="margin-left: 10px; color: #909399;">天（发货后自动确认收货天数）</span>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSavePayment">保存设置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
      
      <!-- 账号安全 -->
      <el-tab-pane label="账号安全" name="security">
        <div class="card-container">
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="120px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入原密码"
                style="width: 400px;"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码（6-20位）"
                style="width: 400px;"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                style="width: 400px;"
                show-password
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('basic')
const passwordFormRef = ref(null)

const basicForm = reactive({
  shopName: '藏药小程序商城',
  phone: '400-123-4567',
  wechat: 'tibetan_medicine',
  notice: '欢迎光临藏药商城，正品保证，假一罚十！'
})

const shippingForm = reactive({
  defaultFee: 10.00,
  freeThreshold: 99.00,
  deliveryDays: 3
})

const paymentForm = reactive({
  wechatPay: true,
  alipay: true,
  autoConfirmDays: 7
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleSaveBasic = () => {
  ElMessage.success('基本设置保存成功')
}

const handleSaveShipping = () => {
  ElMessage.success('配送设置保存成功')
}

const handleSavePayment = () => {
  ElMessage.success('支付设置保存成功')
}

const handleChangePassword = async () => {
  await passwordFormRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success('密码修改成功，请重新登录')
      // 清空表单
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    }
  })
}
</script>

<style scoped lang="scss">
.settings-container {
  .card-container {
    padding: 30px;
  }
}
</style>
