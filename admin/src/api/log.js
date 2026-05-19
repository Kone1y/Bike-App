import request from '@/utils/request'

export function getLogList(params) {
  return request.get('/api/system/log/list', { params })
}
