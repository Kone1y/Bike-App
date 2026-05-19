<template>
  <view class="login-page">
    <view class="logo">共享单车</view>
    <button type="primary" open-type="getUserInfo" @getuserinfo="handleLogin">微信一键登录</button>
  </view>
</template>

<script setup>
import { post } from '@/utils/request'
import { store } from '@/store'

const handleLogin = async (e) => {
  // 微信登录对接将在阶段三实现
  uni.showToast({ title: '登录功能开发中', icon: 'none' })
}
</script>

<style scoped>
.login-page { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 80vh; }
.logo { font-size: 60rpx; font-weight: bold; margin-bottom: 80rpx; }
</style>
