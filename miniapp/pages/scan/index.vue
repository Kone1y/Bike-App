<template>
  <view class="page">
    <view class="bike-card">
      <view class="card-title">车辆信息</view>
      <view class="info-row">
        <text class="label">车辆编号</text>
        <text class="value">{{ bikeCode }}</text>
      </view>
      <view class="info-row" v-if="bike">
        <text class="label">所属区域</text>
        <text class="value">{{ bike.areaName || '未分配' }}</text>
      </view>
    </view>

    <view class="tips" v-if="bike && bike.status === 0">
      <text>确认后将开始计费，请仔细检查车辆状况</text>
    </view>

    <button class="rent-btn" type="primary" :loading="loading" :disabled="loading"
            @click="confirmRent">确认租车</button>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { rentBike, getNearbyBikes } from '@/api/bike'
import { getLocation } from '@/utils/location'

const bikeCode = ref('')
const bike = ref(null)
const loading = ref(false)

onLoad(async (options) => {
  bikeCode.value = decodeURIComponent(options.code || '')
  if (bikeCode.value) {
    try {
      const res = await getNearbyBikes(0, 0, 99999)
      const found = (res.data || []).find(b => b.bikeCode === bikeCode.value)
      if (found) bike.value = found
    } catch (e) {}
  }
})

const confirmRent = async () => {
  if (!bikeCode.value) {
    uni.showToast({ title: '无效的车辆编码', icon: 'none' })
    return
  }
  loading.value = true
  try {
    const loc = await getLocation()
    const result = await rentBike(bikeCode.value, loc.lng, loc.lat)
    const orderId = result.data.id
    uni.redirectTo({ url: `/pages/riding/index?orderId=${orderId}` })
  } catch (e) {
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page { padding: 40rpx; }
.bike-card { background: #fff; padding: 40rpx; border-radius: 16rpx; margin-bottom: 30rpx; }
.card-title { font-size: 36rpx; font-weight: bold; margin-bottom: 30rpx; }
.info-row { display: flex; justify-content: space-between; padding: 20rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.label { color: #999; }
.value { color: #333; }
.tips { background: #fff3cd; padding: 20rpx 30rpx; border-radius: 12rpx; margin-bottom: 60rpx; color: #856404; font-size: 26rpx; }
.rent-btn { border-radius: 50rpx; }
</style>
