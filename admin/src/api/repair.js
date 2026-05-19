import request from '@/utils/request'

export function getRepairList(params) {
  return request.get('/api/repair/list', { params })
}

export function getRepairById(id) {
  return request.get(`/api/repair/${id}`)
}

export function handleRepair(id, data) {
  return request.put(`/api/repair/${id}/handle`, data)
}
