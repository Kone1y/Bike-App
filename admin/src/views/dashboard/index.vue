<template>
  <div class="dashboard">
    <h2>欢迎回来，{{ userStore.nickname }}</h2>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="待租车辆" :value="stats.availableBikes">
            <template #prefix><el-icon><Van /></el-icon></template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="今日订单" :value="stats.todayOrders">
            <template #prefix><el-icon><Document /></el-icon></template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="使用中车辆" :value="stats.inUseBikes">
            <template #prefix><el-icon><Loading /></el-icon></template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="待处理报修" :value="stats.pendingRepairs">
            <template #prefix><el-icon><Warning /></el-icon></template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="车辆总数" :value="stats.totalBikes">
            <template #prefix><el-icon><Bicycle /></el-icon></template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="维修中车辆" :value="stats.repairingBikes">
            <template #prefix><el-icon><SetUp /></el-icon></template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getDashboardStats } from '@/api/dashboard'

const userStore = useUserStore()

const stats = reactive({
  totalBikes: 0, availableBikes: 0, inUseBikes: 0,
  repairingBikes: 0, pendingRepairs: 0, todayOrders: 0
})

async function loadStats() {
  try {
    const res = await getDashboardStats()
    if (res.data) {
      Object.assign(stats, res.data)
    }
  } catch (e) {}
}

onMounted(() => { loadStats() })
</script>
