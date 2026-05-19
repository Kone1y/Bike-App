<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="车辆编码">
          <el-input v-model="searchForm.bikeCode" placeholder="请输入车辆编码" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待租" :value="0" />
            <el-option label="使用中" :value="1" />
            <el-option label="报修" :value="2" />
            <el-option label="报废" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="停车区域">
          <el-select v-model="searchForm.areaId" placeholder="全部" clearable style="width: 150px">
            <el-option v-for="item in areaList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>

      <div style="margin-bottom: 16px">
        <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增车辆</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="bikeCode" label="车辆编码" width="130" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lng" label="经度" width="130" />
        <el-table-column prop="lat" label="纬度" width="130" />
        <el-table-column prop="areaName" label="所属区域" width="130" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-dropdown @command="(cmd) => handleStatusChange(row, cmd)" style="margin-left: 8px">
              <el-button type="warning" link size="small">状态变更</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="0" :disabled="row.status === 0">待租</el-dropdown-item>
                  <el-dropdown-item :command="2" :disabled="row.status === 2">报修</el-dropdown-item>
                  <el-dropdown-item :command="3" :disabled="row.status === 3">报废</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button type="success" link size="small" @click="handleQrCode(row)">二维码</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="车辆编码" prop="bikeCode">
          <el-input v-model="form.bikeCode" placeholder="请输入车辆编码">
            <template #append>
              <el-button @click="generateBikeCode">自动生成</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="停车区域">
          <el-select v-model="form.areaId" placeholder="请选择区域" clearable style="width: 100%">
            <el-option v-for="item in areaList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="经度">
          <el-input-number v-model="form.lng" :precision="6" :step="0.001" :controls="false" placeholder="经度" style="width: 100%" />
        </el-form-item>
        <el-form-item label="纬度">
          <el-input-number v-model="form.lat" :precision="6" :step="0.001" :controls="false" placeholder="纬度" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 二维码弹窗 -->
    <el-dialog v-model="qrDialogVisible" title="车辆二维码" width="400px" align-center>
      <div style="text-align: center">
        <div style="margin-bottom: 12px; font-size: 16px; font-weight: bold">{{ qrBikeCode }}</div>
        <el-image v-if="qrImage" :src="qrImage" style="width: 250px; height: 250px" fit="contain" />
        <div style="margin-top: 12px; color: #909399">扫描此二维码可租用该车辆</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getBikeList, addBike, editBike, deleteBike, updateBikeStatus, getBikeQrCode } from '@/api/bike'
import { getAreaList } from '@/api/area'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const areaList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const dialogTitle = ref('')

const qrDialogVisible = ref(false)
const qrImage = ref('')
const qrBikeCode = ref('')

const searchForm = reactive({ bikeCode: '', status: null, areaId: null })
const form = reactive({ id: null, bikeCode: '', lng: null, lat: null, areaId: null })
const rules = { bikeCode: [{ required: true, message: '请输入车辆编码', trigger: 'blur' }] }

const statusText = (s) => ({ 0: '待租', 1: '使用中', 2: '报修', 3: '报废' }[s] || '未知')
const statusTagType = (s) => ({ 0: 'success', 1: 'primary', 2: 'warning', 3: 'info' }[s] || 'info')

async function loadData() {
  loading.value = true
  try {
    const res = await getBikeList({
      bikeCode: searchForm.bikeCode || undefined,
      status: searchForm.status,
      areaId: searchForm.areaId
    })
    tableData.value = res.data || []
  } catch (e) {} finally {
    loading.value = false
  }
}

async function loadAreas() {
  try {
    const res = await getAreaList()
    areaList.value = res.data || []
  } catch (e) {}
}

function handleSearch() { loadData() }
function handleReset() {
  searchForm.bikeCode = ''
  searchForm.status = null
  searchForm.areaId = null
  loadData()
}

function generateBikeCode() {
  const now = new Date()
  const ts = now.getFullYear().toString() + String(now.getMonth() + 1).padStart(2, '0') + String(now.getDate()).padStart(2, '0')
  const rand = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
  form.bikeCode = 'BK' + ts + rand
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增车辆'
  Object.assign(form, { id: null, bikeCode: '', lng: null, lat: null, areaId: null })
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  dialogTitle.value = '编辑车辆'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function handleSubmit() {
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await editBike(form)
      ElMessage.success('编辑成功')
    } else {
      await addBike(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {} finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除车辆"${row.bikeCode}"吗？`, '提示', { type: 'warning' })
    await deleteBike(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

async function handleStatusChange(row, status) {
  const text = statusText(status)
  try {
    await ElMessageBox.confirm(`确定将车辆"${row.bikeCode}"变更为"${text}"状态吗？`, '提示', { type: 'warning' })
    await updateBikeStatus(row.id, status)
    ElMessage.success('状态变更成功')
    loadData()
  } catch (e) {}
}

async function handleQrCode(row) {
  try {
    qrBikeCode.value = row.bikeCode
    const res = await getBikeQrCode(row.id)
    qrImage.value = res.data?.image || ''
    qrDialogVisible.value = true
  } catch (e) {}
}

onMounted(() => { loadData(); loadAreas() })
</script>
