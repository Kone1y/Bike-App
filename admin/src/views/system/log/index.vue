<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="操作人">
          <el-input v-model="searchForm.username" placeholder="请输入操作人" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="操作描述">
          <el-input v-model="searchForm.operation" placeholder="请输入操作描述" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="operation" label="操作描述" width="150" />
        <el-table-column prop="method" label="请求方法" min-width="250" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="executeTime" label="耗时(ms)" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.executeTime > 1000 ? 'danger' : row.executeTime > 500 ? 'warning' : 'success'" size="small">
              {{ row.executeTime }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getLogList } from '@/api/log'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({
  username: '',
  operation: ''
})

async function loadData() {
  loading.value = true
  try {
    const res = await getLogList({
      username: searchForm.username || undefined,
      operation: searchForm.operation || undefined
    })
    tableData.value = res.data || []
  } catch (e) {
    // handled by request interceptor
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  loadData()
}

function handleReset() {
  searchForm.username = ''
  searchForm.operation = ''
  loadData()
}

onMounted(() => {
  loadData()
})
</script>
