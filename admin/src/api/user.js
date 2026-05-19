import request from '@/utils/request'

export function getUserList(params) {
  return request.get('/api/system/user/list', { params })
}

export function getUserById(id) {
  return request.get(`/api/system/user/${id}`)
}

export function addUser(data) {
  return request.post('/api/system/user', data)
}

export function editUser(data) {
  return request.put('/api/system/user', data)
}

export function deleteUser(id) {
  return request.delete(`/api/system/user/${id}`)
}
