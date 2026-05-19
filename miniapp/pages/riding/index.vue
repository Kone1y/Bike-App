<template>
  <view class="page">
    <view class="timer-section">
      <text class="timer">{{ timerDisplay }}</text>
      <text class="status-text">骑行中</text>
    </view>

    <map class="ride-map" :latitude="currentLat" :longitude="currentLng"
         :scale="16" :show-location="true" />

    <view class="info-section">
      <view class="info-item">
        <text class="info-label">订单编号</text>
        <text class="info-value">{{ order?.orderNo || '' }}</text>
      </view>
      <view class="info-item">
        <text class="info-label">开始时间</text>
        <text class="info-value">{{ formatTime(order?.startTime) }}</text>
      </view>
    </view>

    <button class="finish-btn" type="warn" :loading="finishing" @click="confirmFinish">
      结束行程
    </button>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { getRidingOrder, finishOrder } from '@/api/order'
import { getLocation } from '@/utils/location'

const orderId = ref(null)
const order = ref(null)
const timerDisplay = ref('00:00')
const currentLat = ref(30.274)
const currentLng = ref(120.155)
const finishing = ref(false)
let timerInterval = null

onLoad(async (options) => {
  if (options.orderId) {
    orderId.value = options.orderId
  }
  await loadRidingOrder()
  if (order.value) {
    orderId.value = order.value.id
    startTimer()
  }
})

onUnload(() => {
  if (timerInterval) clearInterval(timerInterval)
})

const loadRidingOrder = async () => {
  try {
    const res = await getRidingOrder()
    if (res.data) order.value = res.data
  } catch (e) {}
}

const startTimer = () => {
  const startTime = order.value?.startTime ? new Date(order.value.startTime).getTime() : Date.now()
  timerInterval = setInterval(() => {
    const now = Date.now()
    const diff = Math.floor((now - startTime) / 1000)
    const minutes = Math.floor(diff / 60)
    const seconds = diff % 60
    timerDisplay.value = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
    getLocation().then(loc => {
      currentLat.value = loc.lat
      currentLng.value = loc.lng
    }).catch(() => {})
  }, 1000)
}

const confirmFinish = () => {
  uni.showModal({
    title: '确认结束行程',
    content: '请确认您已将车辆停放在指定区域',
    success: async (res) => {
      if (res.confirm) {
        await doFinish()
      }
    }
  })
}

const doFinish = async () => {
  finishing.value = true
  try {
    const loc = await getLocation()
    const result = await finishOrder(orderId.value, loc.lng, loc.lat)
    const finishedOrder = result.data
    uni.redirectTo({ url: `/pages/order-detail/index?id=${finishedOrder.id}` })
  } catch (e) {
  } finally {
    finishing.value = false
  }
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const d = new Date(timeStr)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
</script>

<style scoped>
.page { padding: 30rpx; }
.timer-section { text-align: center; padding: 60rpx 0; }
.timer { font-size: 100rpx; font-weight: bold; color: #333; display: block; font-variant-numeric: tabular-nums; }
.status-text { font-size: 32rpx; color: #409eff; margin-top: 10rpx; display: block; }
.ride-map { width: 100%; height: 400rpx; border-radius: 16rpx; margin: 30rpx 0; }
.info-section { background: #fff; border-radius: 16rpx; padding: 30rpx; margin-bottom: 40rpx; }
.info-item { display: flex; justify-content: space-between; padding: 20rpx 0; }
.info-label { color: #999; }
.info-value { color: #333; }
.finish-btn { border-radius: 50rpx; }
</style>
