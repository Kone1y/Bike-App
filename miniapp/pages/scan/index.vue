<template>
  <view class="page">
    <view class="bike-info">
      <text>车辆编码：{{ bikeCode }}</text>
    </view>
    <button type="primary" @click="rentBike">确认租车</button>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const bikeCode = ref('')

onLoad((options) => {
  bikeCode.value = options.code || ''
})

const rentBike = () => {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}
</script>

<style scoped>
.page { padding: 40rpx; }
.bike-info { padding: 30rpx; background: #fff; border-radius: 12rpx; margin-bottom: 40rpx; }
</style>
