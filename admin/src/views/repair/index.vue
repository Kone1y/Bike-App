<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="车辆编码">
          <el-input v-model="searchForm.bikeCode" placeholder="请输入车辆编码" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="bikeCode" label="车辆编码" width="120" />
        <el-table-column prop="username" label="上报用户" width="120" />
        <el-table-column prop="description" label="故障描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="repairStatusType(row.status)" size="small">{{ repairStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="adminRemark" label="处理备注" width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="上报时间" width="180" />
        <el-table-column prop="handleTime" label="处理时间" width="180" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status !== 2" type="warning" link size="small" @click="handleRepair(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="报修详情" width="550px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="报修ID">{{ currentRepair.id }}</el-descriptions-item>
        <el-descriptions-item label="车辆编码">{{ currentRepair.bikeCode }}</el-descriptions-item>
        <el-descriptions-item label="上报用户">{{ currentRepair.username }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="repairStatusType(currentRepair.status)" size="small">{{ repairStatusText(currentRepair.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="上报时间" :span="2">{{ currentRepair.createTime }}</el-descriptions-item>
        <el-descriptions-item label="故障描述" :span="2">{{ currentRepair.description }}</el-descriptions-item>
        <el-descriptions-item label="处理备注" :span="2">{{ currentRepair.adminRemark || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间" :span="2">{{ currentRepair.handleTime || '暂未处理' }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="currentRepair.images" style="margin-top: 16px">
        <div style="margin-bottom: 8px; font-weight: bold">故障图片：</div>
        <div style="display: flex; gap: 8px; flex-wrap: wrap">
          <el-image v-for="(img, i) in imageList" :key="i" :src="img" style="width: 100px; height: 100px" fit="cover" :preview-src-list="imageList" :initial-index="i" preview-teleported />
        </div>
      </div>
    </el-dialog>

    <!-- 处理弹窗 -->
    <el-dialog v-model="handleDialogVisible" title="处理报修" width="500px" destroy-on-close>
      <el-form :model="handleForm" ref="handleFormRef" label-width="80px">
        <el-form-item label="处理状态">
          <el-select v-model="handleForm.status" style="width: 100%">
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input v-model="handleForm.adminRemark" type="textarea" :rows="3" placeholder="请输入处理备注" />
        </el-form-item>
        <el-alert v-if="handleForm.status === 2" title="完成后车辆将自动恢复为待租状态" type="info" :closable="false" style="margin-bottom: 12px" />
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRepairSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getRepairList, getRepairById, handleRepair as apiHandleRepair } from '@/api/repair'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const submitLoading = ref(false)

const searchForm = reactive({ status: null, bikeCode: '' })
const currentRepair = reactive({})
const detailDialogVisible = ref(false)
const handleDialogVisible = ref(false)
const handleFormRef = ref(null)
const handleForm = reactive({ id: null, status: 1, adminRemark: '' })

const repairStatusText = (s) => ({ 0: '待处理', 1: '处理中', 2: '已完成' }[s] || '未知')
const repairStatusType = (s) => ({ 0: 'danger', 1: 'warning', 2: 'success' }[s] || 'info')
const imageList = computed(() => {
  if (!currentRepair.images) return []
  return currentRepair.images.split(',').filter(img => img.trim())
})

async function loadData() {
  loading.value = true
  try {
    const res = await getRepairList({
      status: searchForm.status,
      bikeCode: searchForm.bikeCode || undefined
    })
    tableData.value = res.data || []
  } catch (e) {} finally {
    loading.value = false
  }
}

function handleSearch() { loadData() }
function handleReset() { searchForm.status = null; searchForm.bikeCode = ''; loadData() }

async function handleDetail(row) {
  try {
    const res = await getRepairById(row.id)
    Object.assign(currentRepair, res.data || {})
    detailDialogVisible.value = true
  } catch (e) {}
}

function handleRepair(row) {
  handleForm.id = row.id
  handleForm.status = 1
  handleForm.adminRemark = ''
  handleDialogVisible.value = true
}

async function handleRepairSubmit() {
  submitLoading.value = true
  try {
    await apiHandleRepair(handleForm.id, {
      status: handleForm.status,
      adminRemark: handleForm.adminRemark || ''
    })
    ElMessage.success('处理成功')
    handleDialogVisible.value = false
    loadData()
  } catch (e) {} finally {
    submitLoading.value = false
  }
}

onMounted(() => { loadData() })
</script>
