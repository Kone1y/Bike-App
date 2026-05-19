<template>
  <view class="page">
    <view class="form">
      <view class="form-item">
        <text>故障类型</text>
        <picker :range="types" @change="onTypeChange">
          <text>{{ types[selectedIndex] || '请选择' }}</text>
        </picker>
      </view>
      <view class="form-item">
        <text>故障描述</text>
        <textarea placeholder="请描述故障情况" />
      </view>
      <button type="primary" @click="submitRepair">提交报修</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const types = ['刹车失灵', '轮胎漏气', '车锁损坏', '其他']
const selectedIndex = ref(-1)

const onTypeChange = (e) => { selectedIndex.value = e.detail.value }
const submitRepair = () => { uni.showToast({ title: '功能开发中', icon: 'none' }) }
</script>

<style scoped>
.page { padding: 20rpx; }
.form { background: #fff; padding: 30rpx; border-radius: 12rpx; }
.form-item { margin-bottom: 30rpx; }
textarea { width: 100%; height: 200rpx; border: 1rpx solid #eee; margin-top: 10rpx; padding: 10rpx; }
</style>
