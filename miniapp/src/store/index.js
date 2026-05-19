import { reactive } from 'vue'

export const store = reactive({
  token: uni.getStorageSync('token') || '',
  userInfo: JSON.parse(uni.getStorageSync('userInfo') || '{}'),

  setToken(token) {
    this.token = token
    uni.setStorageSync('token', token)
  },

  setUserInfo(info) {
    this.userInfo = info
    uni.setStorageSync('userInfo', JSON.stringify(info))
  },

  logout() {
    this.token = ''
    this.userInfo = {}
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
  }
})
