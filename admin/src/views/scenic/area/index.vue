<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="区域名称">
          <el-input v-model="searchForm.name" placeholder="请输入区域名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>

      <div style="margin-bottom: 16px">
        <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增停车区域</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="区域名称" width="150" />
        <el-table-column prop="centerLng" label="中心经度" width="130" />
        <el-table-column prop="centerLat" label="中心纬度" width="130" />
        <el-table-column label="允许半径" width="120">
          <template #default="{ row }">
            {{ row.radius }} 米
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="区域名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入区域名称" />
        </el-form-item>
        <el-form-item label="中心经度">
          <el-input-number v-model="form.centerLng" :precision="6" :step="0.001" :controls="false" placeholder="经度" style="width: 100%" />
        </el-form-item>
        <el-form-item label="中心纬度">
          <el-input-number v-model="form.centerLat" :precision="6" :step="0.001" :controls="false" placeholder="纬度" style="width: 100%" />
        </el-form-item>
        <el-form-item label="允许半径" prop="radius">
          <el-input-number v-model="form.radius" :min="10" :max="10000" :step="50" style="width: 100%" />
          <span style="margin-left: 8px; color: #909399">米</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAreaList, addArea, editArea, deleteArea } from '@/api/area'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const dialogTitle = ref('')

const searchForm = reactive({ name: '' })
const form = reactive({ id: null, name: '', centerLng: null, centerLat: null, radius: 200 })
const rules = { name: [{ required: true, message: '请输入区域名称', trigger: 'blur' }] }

async function loadData() {
  loading.value = true
  try {
    const res = await getAreaList({ name: searchForm.name || undefined })
    tableData.value = res.data || []
  } catch (e) {} finally {
    loading.value = false
  }
}

function handleSearch() { loadData() }
function handleReset() { searchForm.name = ''; loadData() }

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增停车区域'
  Object.assign(form, { id: null, name: '', centerLng: null, centerLat: null, radius: 200 })
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  dialogTitle.value = '编辑停车区域'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function handleSubmit() {
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    if (isEdit.value) { await editArea(form); ElMessage.success('编辑成功') }
    else { await addArea(form); ElMessage.success('新增成功') }
    dialogVisible.value = false
    loadData()
  } catch (e) {} finally { submitLoading.value = false }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除停车区域"${row.name}"吗？`, '提示', { type: 'warning' })
    await deleteArea(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

onMounted(() => { loadData() })
</script>
