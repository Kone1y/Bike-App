<template>
  <view class="page">
    <view class="status-banner" :class="'banner-' + (order?.status ?? 0)">
      <text class="status-text">{{ statusText(order?.status) }}</text>
    </view>

    <view class="card">
      <view class="card-title">订单信息</view>
      <view class="info-row">
        <text class="label">订单编号</text>
        <text class="value">{{ order?.orderNo }}</text>
      </view>
      <view class="info-row">
        <text class="label">车辆编号</text>
        <text class="value">{{ order?.bikeCode }}</text>
      </view>
      <view class="info-row">
        <text class="label">开始时间</text>
        <text class="value">{{ formatTime(order?.startTime) }}</text>
      </view>
      <view class="info-row" v-if="order?.endTime">
        <text class="label">结束时间</text>
        <text class="value">{{ formatTime(order?.endTime) }}</text>
      </view>
    </view>

    <view class="card" v-if="order?.status === 1">
      <view class="card-title">费用明细</view>
      <view class="info-row">
        <text class="label">骑行时长</text>
        <text class="value">{{ order?.duration }} 分钟</text>
      </view>
      <view class="info-row">
        <text class="label">订单金额</text>
        <text class="value amount">¥{{ order?.amount }}</text>
      </view>
    </view>

    <view class="card billing-rules">
      <view class="card-title">计费规则</view>
      <text class="rule-text">起步价: ¥2.00</text>
      <text class="rule-text">前10分钟免费</text>
      <text class="rule-text">超出后: ¥0.50/分钟</text>
      <text class="rule-text">每日封顶: ¥30.00</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { getOrderDetail } from '@/api/order'

const order = ref(null)

onLoad(async (options) => {
  if (options.id) {
    try {
      const res = await getOrderDetail(options.id)
      order.value = res.data
    } catch (e) {}
  }
})

const statusText = (status) => {
  if (status === undefined || status === null) return ''
  return ['骑行中', '已完成', '已取消'][status] || '未知'
}

const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  const d = new Date(timeStr)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
</script>

<style scoped>
.page { padding: 20rpx; }
.status-banner { padding: 40rpx 30rpx; border-radius: 16rpx 16rpx 0 0; text-align: center; }
.banner-0 { background: #409eff; }
.banner-1 { background: #67c23a; }
.banner-2 { background: #f56c6c; }
.status-text { color: #fff; font-size: 36rpx; font-weight: bold; }
.card { background: #fff; padding: 30rpx; border-radius: 12rpx; margin-bottom: 20rpx; }
.card-title { font-size: 32rpx; font-weight: bold; margin-bottom: 20rpx; }
.info-row { display: flex; justify-content: space-between; padding: 16rpx 0; }
.label { color: #999; font-size: 28rpx; }
.value { color: #333; font-size: 28rpx; }
.amount { color: #f56c6c; font-weight: bold; font-size: 36rpx; }
.billing-rules .rule-text { display: block; padding: 10rpx 0; color: #666; font-size: 26rpx; }
</style>
