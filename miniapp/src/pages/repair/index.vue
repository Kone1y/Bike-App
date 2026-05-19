<template>
  <view class="page">
    <view class="form">
      <view class="form-item">
        <text class="label">车辆编号</text>
        <view class="bike-code-row">
          <input class="input" v-model="bikeCode" placeholder="请输入或扫描车辆编号" />
          <button class="scan-small-btn" size="mini" @click="scanBike">扫码</button>
        </view>
      </view>

      <view class="form-item">
        <text class="label">故障类型</text>
        <picker :range="types" @change="onTypeChange">
          <view class="picker-value">
            {{ selectedIndex >= 0 ? types[selectedIndex] : '请选择故障类型' }}
          </view>
        </picker>
      </view>

      <view class="form-item">
        <text class="label">故障描述</text>
        <textarea class="textarea" v-model="description" placeholder="请描述故障情况"
                  maxlength="500" />
      </view>

      <view class="form-item">
        <text class="label">拍照上传（最多3张）</text>
        <view class="image-list">
          <view class="image-item" v-for="(img, i) in imageUrls" :key="i">
            <image :src="BASE_URL + img" mode="aspectFill" class="upload-img" />
            <view class="remove-btn" @click="removeImage(i)">x</view>
          </view>
          <view class="image-item add-btn" @click="chooseImage" v-if="imageUrls.length < 3">
            <text class="plus">+</text>
          </view>
        </view>
      </view>

      <button class="submit-btn" type="primary" :loading="submitting" @click="submit">
        提交报修
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { submitRepair } from '@/api/repair'
import { uploadImage } from '@/api/file'
import { requireAuth } from '@/utils/auth'

const BASE_URL = 'http://localhost:8080'
const types = ['刹车失灵', '轮胎漏气', '车锁损坏', '车座损坏', '链条脱落', '其他']
const selectedIndex = ref(-1)
const bikeCode = ref('')
const description = ref('')
const imageUrls = ref([])
const submitting = ref(false)

onLoad((options) => {
  if (options.code) {
    bikeCode.value = decodeURIComponent(options.code)
  }
})

const scanBike = () => {
  uni.scanCode({
    onlyFromCamera: true,
    scanType: ['qrCode'],
    success: (res) => {
      bikeCode.value = res.result
    }
  })
}

const onTypeChange = (e) => {
  selectedIndex.value = parseInt(e.detail.value)
}

const chooseImage = () => {
  uni.chooseImage({
    count: 3 - imageUrls.value.length,
    sizeType: ['compressed'],
    sourceType: ['camera', 'album'],
    success: async (res) => {
      for (const path of res.tempFilePaths) {
        try {
          const url = await uploadImage(path)
          imageUrls.value.push(url)
        } catch (e) {
          uni.showToast({ title: '图片上传失败', icon: 'none' })
        }
      }
    }
  })
}

const removeImage = (index) => {
  imageUrls.value.splice(index, 1)
}

const submit = async () => {
  if (!requireAuth()) return
  if (!bikeCode.value) {
    uni.showToast({ title: '请输入车辆编号', icon: 'none' })
    return
  }
  if (selectedIndex.value < 0) {
    uni.showToast({ title: '请选择故障类型', icon: 'none' })
    return
  }
  if (!description.value.trim()) {
    uni.showToast({ title: '请填写故障描述', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    const fullDescription = `[${types[selectedIndex.value]}] ${description.value}`
    await submitRepair(bikeCode.value, fullDescription, imageUrls.value.join(','))
    uni.showToast({ title: '提交成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1500)
  } catch (e) {
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.page { padding: 20rpx; }
.form { background: #fff; padding: 30rpx; border-radius: 12rpx; }
.form-item { margin-bottom: 30rpx; }
.label { font-size: 28rpx; color: #333; font-weight: bold; margin-bottom: 10rpx; display: block; }
.bike-code-row { display: flex; gap: 16rpx; align-items: center; }
.input { flex: 1; border: 1rpx solid #ddd; padding: 20rpx; border-radius: 8rpx; font-size: 28rpx; }
.scan-small-btn { background: #409eff; color: #fff; border: none; }
.picker-value { border: 1rpx solid #ddd; padding: 20rpx; border-radius: 8rpx; color: #333; }
.textarea { width: 100%; height: 200rpx; border: 1rpx solid #ddd; padding: 20rpx; border-radius: 8rpx; font-size: 28rpx; box-sizing: border-box; }
.image-list { display: flex; flex-wrap: wrap; gap: 16rpx; margin-top: 10rpx; }
.image-item { width: 200rpx; height: 200rpx; border-radius: 12rpx; position: relative; }
.upload-img { width: 100%; height: 100%; border-radius: 12rpx; }
.remove-btn { position: absolute; top: -10rpx; right: -10rpx; width: 40rpx; height: 40rpx; background: #f56c6c; color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 24rpx; }
.add-btn { border: 2rpx dashed #ddd; display: flex; align-items: center; justify-content: center; }
.plus { font-size: 60rpx; color: #ccc; }
.submit-btn { border-radius: 50rpx; margin-top: 40rpx; }
</style>
