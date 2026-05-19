import { get, post } from '@/utils/request'

export const getRidingOrder = () => get('/api/app/order/riding')

export const finishOrder = (orderId, endLng, endLat) =>
  post('/api/app/order/finish', { orderId, endLng, endLat })

export const getOrderList = (status) => get('/api/app/order/list', { status })

export const getOrderDetail = (id) => get(`/api/app/order/${id}`)
