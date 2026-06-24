import request from '@/utils/request'

// 获取阅读历史列表
export function getReadHistory() {
  return request.get('/user/history')
}
