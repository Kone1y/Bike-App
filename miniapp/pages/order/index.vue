<template>
  <view class="page">
    <view class="tabs">
      <view class="tab" :class="{ active: currentTab === '' }" @click="switchTab('')">全部</view>
      <view class="tab" :class="{ active: currentTab === '0' }" @click="switchTab('0')">骑行中</view>
      <view class="tab" :class="{ active: currentTab === '1' }" @click="switchTab('1')">已完成</view>
      <view class="tab" :class="{ active: currentTab === '2' }" @click="switchTab('2')">已取消</view>
    </view>

    <view class="order-list">
      <view class="order-card" v-for="order in orders" :key="order.id"
            @click="goDetail(order.id)">
        <view class="order-header">
          <text class="order-no">{{ order.orderNo }}</text>
          <text class="order-status" :class="'status-' + order.status">
            {{ statusText(order.status) }}
          </text>
        </view>
        <view class="order-body">
          <text class="bike-code">车辆: {{ order.bikeCode }}</text>
          <text class="order-time">{{ formatTime(order.startTime) }}</text>
        </view>
        <view class="order-footer" v-if="order.status === 1">
          <text class="amount">¥{{ order.amount }}</text>
          <text class="duration">{{ order.duration }}分钟</text>
        </view>
      </view>

      <view class="empty" v-if="orders.length === 0 && !loading">
        <text class="empty-text">暂无订单记录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderList } from '@/api/order'
import { requireAuth } from '@/utils/auth'

const orders = ref([])
const currentTab = ref('')
const loading = ref(false)

onMounted(() => {
  if (!requireAuth()) return
  loadOrders()
})

const loadOrders = async () => {
  loading.value = true
  try {
    const status = currentTab.value === '' ? undefined : parseInt(currentTab.value)
    const res = await getOrderList(status)
    orders.value = res.data || []
  } catch (e) {
  } finally {
    loading.value = false
  }
}

const switchTab = (tab) => {
  currentTab.value = tab
  loadOrders()
}

const goDetail = (id) => {
  uni.navigateTo({ url: `/pages/order-detail/index?id=${id}` })
}

const statusText = (status) => {
  return ['骑行中', '已完成', '已取消'][status] || '未知'
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const d = new Date(timeStr)
  const pad = (n) => String(n).padStart(2, '0')
  return `${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
</script>

<style scoped>
.page { padding: 20rpx; }
.tabs { display: flex; background: #fff; border-radius: 12rpx; margin-bottom: 20rpx; padding: 10rpx; }
.tab { flex: 1; text-align: center; padding: 20rpx 0; font-size: 28rpx; color: #666; border-radius: 8rpx; }
.tab.active { background: #409eff; color: #fff; }
.order-list { margin-top: 20rpx; }
.order-card { background: #fff; padding: 30rpx; border-radius: 12rpx; margin-bottom: 20rpx; }
.order-header { display: flex; justify-content: space-between; margin-bottom: 20rpx; }
.order-no { font-size: 28rpx; color: #333; }
.order-status { font-size: 26rpx; font-weight: bold; }
.status-0 { color: #409eff; }
.status-1 { color: #67c23a; }
.status-2 { color: #f56c6c; }
.order-body { display: flex; justify-content: space-between; color: #999; font-size: 26rpx; }
.order-footer { display: flex; justify-content: space-between; margin-top: 20rpx; padding-top: 20rpx; border-top: 1rpx solid #f0f0f0; }
.amount { font-size: 36rpx; font-weight: bold; color: #f56c6c; }
.duration { color: #999; font-size: 26rpx; align-self: flex-end; }
.empty { text-align: center; padding: 200rpx 0; }
.empty-text { color: #999; }
</style>
