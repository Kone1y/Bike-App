<template>
  <view class="page">
    <view class="user-info">
      <view class="avatar"></view>
      <text class="name">未登录</text>
    </view>
    <view class="menu-list">
      <view class="menu-item" @click="goRepair">
        <text>故障上报</text>
        <text class="arrow">></text>
      </view>
    </view>
  </view>
</template>

<script setup>
const goRepair = () => { uni.navigateTo({ url: '/pages/repair/index' }) }
</script>

<style scoped>
.page { padding: 20rpx; }
.user-info { background: #409eff; padding: 60rpx 30rpx; border-radius: 12rpx; margin-bottom: 20rpx; display: flex; align-items: center; }
.avatar { width: 100rpx; height: 100rpx; border-radius: 50%; background: #fff; margin-right: 20rpx; }
.name { color: #fff; font-size: 32rpx; }
.menu-list { background: #fff; border-radius: 12rpx; }
.menu-item { display: flex; justify-content: space-between; padding: 30rpx; border-bottom: 1rpx solid #f0f0f0; }
.arrow { color: #999; }
</style>
