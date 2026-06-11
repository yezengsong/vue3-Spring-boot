import request from '@/utils/request'

// 收藏小说
export function bookmark(novelId) {
  return request.post(`/novel/${novelId}/bookmark`)
}

// 取消收藏
export function unbookmark(novelId) {
  return request.delete(`/novel/${novelId}/bookmark`)
}

// 获取收藏列表
export function getBookmarks() {
  return request.get('/novel/user/bookmarks')
}
