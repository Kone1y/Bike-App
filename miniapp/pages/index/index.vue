<template>
  <view class="page">
    <view class="map-container">
      <map style="width: 100%; height: 600rpx;" :latitude="latitude" :longitude="longitude" :markers="markers" />
    </view>
    <view class="action-bar">
      <button type="primary" @click="scanCode">扫码租车</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const latitude = ref(30.274)
const longitude = ref(120.155)
const markers = ref([])

const scanCode = () => {
  uni.scanCode({
    success: (res) => {
      uni.navigateTo({ url: `/pages/scan/index?code=${res.result}` })
    }
  })
}
</script>

<style scoped>
.page { padding: 20rpx; }
.map-container { border-radius: 16rpx; overflow: hidden; margin-bottom: 20rpx; }
.action-bar { padding: 20rpx 0; }
</style>
