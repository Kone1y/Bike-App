import { store } from '@/store'

export function requireAuth() {
  if (!store.token) {
    uni.navigateTo({ url: '/pages/login/index' })
    return false
  }
  return true
}
