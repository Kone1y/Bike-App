import request from '@/utils/request'

export function getBikeList(params) {
  return request.get('/api/bike/list', { params })
}

export function getBikeById(id) {
  return request.get(`/api/bike/${id}`)
}

export function addBike(data) {
  return request.post('/api/bike', data)
}

export function editBike(data) {
  return request.put('/api/bike', data)
}

export function deleteBike(id) {
  return request.delete(`/api/bike/${id}`)
}

export function updateBikeStatus(id, status) {
  return request.put(`/api/bike/${id}/status?status=${status}`)
}

export function getBikeQrCode(id) {
  return request.get(`/api/bike/${id}/qrcode`)
}
