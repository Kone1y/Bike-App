<template>
  <view class="page">
    <map
      class="map"
      :latitude="latitude"
      :longitude="longitude"
      :markers="markers"
      :scale="15"
      :show-location="true"
      @markertap="onMarkerTap"
    />

    <view class="action-bar">
      <view class="bike-count" v-if="nearbyBikes.length > 0">
        附近有 {{ nearbyBikes.length }} 辆可用单车
      </view>
      <view class="bike-count" v-else>
        附近暂无可用单车
      </view>
      <button class="scan-btn" type="primary" @click="scanCode">扫码租车</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getLocation } from '@/utils/location'
import { getNearbyBikes } from '@/api/bike'
import { requireAuth } from '@/utils/auth'
import { getScenicList } from '@/api/scenic'

const latitude = ref(30.274)
const longitude = ref(120.155)
const nearbyBikes = ref([])
const scenicSpots = ref([])

const markers = computed(() => {
  const list = []
  nearbyBikes.value.forEach((bike, i) => {
    list.push({
      id: 10000 + i,
      latitude: parseFloat(bike.lat),
      longitude: parseFloat(bike.lng),
      width: 36,
      height: 36,
      callout: {
        content: bike.bikeCode,
        display: 'ALWAYS',
        fontSize: 12,
        borderRadius: 4,
        padding: 4,
        bgColor: '#67c23a'
      }
    })
  })
  scenicSpots.value.forEach((spot, i) => {
    if (spot.lat && spot.lng) {
      list.push({
        id: 20000 + i,
        latitude: parseFloat(spot.lat),
        longitude: parseFloat(spot.lng),
        width: 32,
        height: 32,
        callout: {
          content: spot.name,
          display: 'ALWAYS',
          fontSize: 12,
          borderRadius: 4,
          padding: 4,
          bgColor: '#409eff',
          color: '#ffffff'
        }
      })
    }
  })
  return list
})

const loadData = async () => {
  try {
    const loc = await getLocation()
    latitude.value = loc.lat
    longitude.value = loc.lng
  } catch (e) {
    console.log('Location failed, using default')
  }

  const [bikes, scenics] = await Promise.all([
    getNearbyBikes(longitude.value, latitude.value).catch(() => ({ data: [] })),
    getScenicList().catch(() => ({ data: [] }))
  ])
  nearbyBikes.value = bikes.data || []
  scenicSpots.value = scenics.data || []
}

const scanCode = () => {
  if (!requireAuth()) return
  uni.scanCode({
    onlyFromCamera: true,
    scanType: ['qrCode'],
    success: (res) => {
      uni.navigateTo({ url: `/pages/scan/index?code=${encodeURIComponent(res.result)}` })
    },
    fail: () => {
      uni.showToast({ title: '扫码取消', icon: 'none' })
    }
  })
}

const onMarkerTap = (e) => {
  const markerId = e.markerId
  if (markerId >= 10000 && markerId < 20000) {
    const bike = nearbyBikes.value[markerId - 10000]
    if (bike) {
      uni.navigateTo({ url: `/pages/scan/index?code=${encodeURIComponent(bike.bikeCode)}` })
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page { position: relative; height: 100vh; }
.map { width: 100%; height: 100%; }
.action-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  padding: 30rpx; background: #fff;
  box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.1);
}
.bike-count { text-align: center; margin-bottom: 20rpx; color: #666; font-size: 28rpx; }
.scan-btn { border-radius: 50rpx; }
</style>
