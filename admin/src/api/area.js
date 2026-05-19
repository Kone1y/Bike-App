import request from '@/utils/request'

export function getAreaList(params) {
  return request.get('/api/scenic/area/list', { params })
}

export function getAreaById(id) {
  return request.get(`/api/scenic/area/${id}`)
}

export function addArea(data) {
  return request.post('/api/scenic/area', data)
}

export function editArea(data) {
  return request.put('/api/scenic/area', data)
}

export function deleteArea(id) {
  return request.delete(`/api/scenic/area/${id}`)
}
