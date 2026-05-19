<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="景点名称">
          <el-input v-model="searchForm.name" placeholder="请输入景点名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>

      <div style="margin-bottom: 16px">
        <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增景点</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="景点名称" width="150" />
        <el-table-column prop="lng" label="经度" width="120" />
        <el-table-column prop="lat" label="纬度" width="120" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="coverImage" label="封面图" width="100">
          <template #default="{ row }">
            <el-image v-if="row.coverImage" :src="row.coverImage" style="width: 60px; height: 60px" fit="cover" :preview-src-list="[row.coverImage]" preview-teleported />
            <span v-else style="color: #c0c4cc">无</span>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="景点名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入景点名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入景点描述" />
        </el-form-item>
        <el-form-item label="经度">
          <el-input-number v-model="form.lng" :precision="6" :step="0.001" :controls="false" placeholder="经度" style="width: 100%" />
        </el-form-item>
        <el-form-item label="纬度">
          <el-input-number v-model="form.lat" :precision="6" :step="0.001" :controls="false" placeholder="纬度" style="width: 100%" />
        </el-form-item>
        <el-form-item label="封面图">
          <el-input v-model="form.coverImage" placeholder="请输入封面图片URL" />
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
import { getScenicList, addScenic, editScenic, deleteScenic } from '@/api/scenic'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const dialogTitle = ref('')

const searchForm = reactive({ name: '' })
const form = reactive({ id: null, name: '', description: '', lng: null, lat: null, coverImage: '' })
const rules = { name: [{ required: true, message: '请输入景点名称', trigger: 'blur' }] }

async function loadData() {
  loading.value = true
  try {
    const res = await getScenicList({ name: searchForm.name || undefined })
    tableData.value = res.data || []
  } catch (e) {} finally {
    loading.value = false
  }
}

function handleSearch() { loadData() }
function handleReset() { searchForm.name = ''; loadData() }

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增景点'
  Object.assign(form, { id: null, name: '', description: '', lng: null, lat: null, coverImage: '' })
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  dialogTitle.value = '编辑景点'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function handleSubmit() {
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    if (isEdit.value) { await editScenic(form); ElMessage.success('编辑成功') }
    else { await addScenic(form); ElMessage.success('新增成功') }
    dialogVisible.value = false
    loadData()
  } catch (e) {} finally { submitLoading.value = false }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除景点"${row.name}"吗？`, '提示', { type: 'warning' })
    await deleteScenic(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

onMounted(() => { loadData() })
</script>
