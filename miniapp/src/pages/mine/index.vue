<template>
  <view class="page">
    <view class="user-header">
      <view class="avatar-wrapper">
        <view class="avatar avatar-default">{{ initial }}</view>
      </view>
      <view class="user-info">
        <text class="nickname">{{ store.userInfo?.nickname || '未登录' }}</text>
        <text class="desc" v-if="store.token">已登录</text>
        <text class="desc login-link" v-else @click="goLogin">点击登录</text>
      </view>
    </view>

    <view class="menu-list">
      <view class="menu-item" @click="goPage('/pages/order/index', true)">
        <text class="menu-icon">📋</text>
        <text class="menu-text">我的订单</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="goRepair">
        <text class="menu-icon">🔧</text>
        <text class="menu-text">故障上报</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="showRules">
        <text class="menu-icon">💰</text>
        <text class="menu-text">计费规则</text>
        <text class="menu-arrow">></text>
      </view>
    </view>

    <button class="logout-btn" v-if="store.token" @click="logout">退出登录</button>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { store } from '@/store'
import { requireAuth } from '@/utils/auth'

const initial = computed(() => {
  const name = store.userInfo?.nickname || ''
  return name.charAt(0) || '?'
})

const goLogin = () => {
  uni.navigateTo({ url: '/pages/login/index' })
}

const goPage = (url, needAuth = false) => {
  if (needAuth && !requireAuth()) return
  uni.navigateTo({ url })
}

const goRepair = () => {
  if (!requireAuth()) return
  uni.navigateTo({ url: '/pages/repair/index' })
}

const showRules = () => {
  uni.showModal({
    title: '计费规则',
    content: '起步价: ¥2.00\n前10分钟免费\n超出后: ¥0.50/分钟\n每日封顶: ¥30.00',
    showCancel: false
  })
}

const logout = () => {
  uni.showModal({
    title: '提示',
    content: '确定退出登录？',
    success: (res) => {
      if (res.confirm) {
        store.logout()
        uni.showToast({ title: '已退出登录', icon: 'success' })
      }
    }
  })
}
</script>

<style scoped>
.page { padding: 20rpx; }
.user-header { background: linear-gradient(135deg, #409eff, #66b1ff); padding: 60rpx 30rpx; border-radius: 16rpx; margin-bottom: 20rpx; display: flex; align-items: center; }
.avatar-wrapper { margin-right: 24rpx; }
.avatar { width: 120rpx; height: 120rpx; border-radius: 50%; }
.avatar-default { background: rgba(255,255,255,0.3); display: flex; align-items: center; justify-content: center; color: #fff; font-size: 48rpx; font-weight: bold; }
.user-info { flex: 1; }
.nickname { color: #fff; font-size: 36rpx; font-weight: bold; display: block; }
.desc { color: rgba(255,255,255,0.8); font-size: 26rpx; margin-top: 8rpx; display: block; }
.login-link { text-decoration: underline; }
.menu-list { background: #fff; border-radius: 12rpx; }
.menu-item { display: flex; align-items: center; padding: 30rpx; border-bottom: 1rpx solid #f5f5f5; }
.menu-item:last-child { border-bottom: none; }
.menu-icon { font-size: 36rpx; margin-right: 20rpx; }
.menu-text { flex: 1; font-size: 30rpx; color: #333; }
.menu-arrow { color: #ccc; font-size: 28rpx; }
.logout-btn { margin-top: 60rpx; border-radius: 50rpx; background: #fff; color: #f56c6c; border: 1rpx solid #f56c6c; font-size: 30rpx; }
</style>
