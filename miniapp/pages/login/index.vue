<template>
  <view class="login-page">
    <view class="logo-area">
      <text class="logo-icon">🚲</text>
      <text class="logo-text">景区共享单车</text>
      <text class="logo-desc">便捷骑行，畅游景区</text>
    </view>
    <button class="login-btn" type="primary" @click="handleLogin">微信一键登录</button>
    <button class="dev-btn" @click="doMockLogin">开发模式登录</button>
  </view>
</template>

<script setup>
import { login } from '@/api/auth'
import { store } from '@/store'

const doLogin = async (code) => {
  try {
    const result = await login(code)
    store.setToken(result.data.token)
    store.setUserInfo({
      userId: result.data.userId,
      username: result.data.username,
      nickname: result.data.nickname
    })
    uni.switchTab({ url: '/pages/index/index' })
  } catch (e) {
    uni.showToast({ title: '登录失败', icon: 'none' })
  }
}

const handleLogin = () => {
  uni.login({
    provider: 'weixin',
    success: (res) => {
      doLogin(res.code)
    },
    fail: () => {
      uni.showToast({ title: '请在微信环境中使用', icon: 'none' })
    }
  })
}

const doMockLogin = () => {
  const mockCode = 'test_' + Date.now()
  doLogin(mockCode)
}
</script>

<style scoped>
.login-page { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 80vh; }
.logo-area { margin-bottom: 100rpx; text-align: center; }
.logo-icon { font-size: 120rpx; display: block; margin-bottom: 20rpx; }
.logo-text { font-size: 48rpx; font-weight: bold; color: #333; display: block; }
.logo-desc { font-size: 28rpx; color: #999; margin-top: 10rpx; display: block; }
.login-btn { width: 500rpx; border-radius: 50rpx; }
.dev-btn { width: 500rpx; border-radius: 50rpx; margin-top: 20rpx; background: #f5f5f5; color: #666; border: none; font-size: 26rpx; }
</style>
