import request from '@/utils/request'

export function getMenuTree() {
  return request.get('/api/system/menu/tree')
}

export function getUserMenus() {
  return request.get('/api/system/menu/user-menus')
}

export function addMenu(data) {
  return request.post('/api/system/menu', data)
}

export function editMenu(data) {
  return request.put('/api/system/menu', data)
}

export function deleteMenu(id) {
  return request.delete(`/api/system/menu/${id}`)
}
