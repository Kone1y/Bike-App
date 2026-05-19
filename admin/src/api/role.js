import request from '@/utils/request'

export function getRoleList(params) {
  return request.get('/api/system/role/list', { params })
}

export function getRoleById(id) {
  return request.get(`/api/system/role/${id}`)
}

export function addRole(data) {
  return request.post('/api/system/role', data)
}

export function editRole(data) {
  return request.put('/api/system/role', data)
}

export function deleteRole(id) {
  return request.delete(`/api/system/role/${id}`)
}

export function getRoleMenuIds(id) {
  return request.get(`/api/system/role/${id}/menus`)
}

export function assignRoleMenus(id, menuIds) {
  return request.post(`/api/system/role/${id}/menus`, menuIds)
}
