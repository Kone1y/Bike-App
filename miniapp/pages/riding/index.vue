<template>
  <view class="page">
    <view class="riding-info">
      <text class="timer">{{ timer }}</text>
      <text class="tips">骑行中...</text>
    </view>
    <button type="warn" @click="finishRiding">结束行程</button>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const timer = ref('00:00')
const finishRiding = () => {
  uni.showModal({
    title: '确认结束行程',
    success: () => uni.navigateTo({ url: '/pages/order-detail/index' })
  })
}
</script>

<style scoped>
.page { padding: 40rpx; display: flex; flex-direction: column; align-items: center; }
.riding-info { text-align: center; margin: 60rpx 0; }
.timer { font-size: 80rpx; font-weight: bold; display: block; }
.tips { font-size: 32rpx; color: #999; margin-top: 20rpx; display: block; }
</style>
