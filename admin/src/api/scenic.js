import request from '@/utils/request'

export function getScenicList(params) {
  return request.get('/api/scenic/spot/list', { params })
}

export function getScenicById(id) {
  return request.get(`/api/scenic/spot/${id}`)
}

export function addScenic(data) {
  return request.post('/api/scenic/spot', data)
}

export function editScenic(data) {
  return request.put('/api/scenic/spot', data)
}

export function deleteScenic(id) {
  return request.delete(`/api/scenic/spot/${id}`)
}
