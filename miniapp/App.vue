<script>
import { store } from '@/store'
import { getRidingOrder } from '@/api/order'

export default {
  onLaunch() {
    console.log('App Launch')
  },
  onShow() {
    if (store.token) {
      getRidingOrder().then(res => {
        if (res.data) {
          uni.showModal({
            title: '骑行提醒',
            content: '您有一个正在进行的行程',
            confirmText: '查看',
            success: (modal) => {
              if (modal.confirm) {
                uni.navigateTo({ url: '/pages/riding/index?orderId=' + res.data.id })
              }
            }
          })
        }
      }).catch(() => {})
    }
  }
}
</script>

<style>
page {
  background-color: #f8f8f8;
  font-size: 28rpx;
}
</style>
