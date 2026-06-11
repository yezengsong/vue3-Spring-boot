import request from '@/utils/request'

// 获取小说列表
export function getNovelList(params) {
  return request.get('/novel/list', { params })
}

// 获取小说详情
export function getNovelDetail(novelId) {
  return request.get(`/novel/${novelId}`)
}

// 获取推荐小说
export function getRecommendNovels(novelId, limit = 5) {
  return request.get(`/novel/${novelId}/recommend`, { params: { limit } })
}

// 获取分类列表
export function getCategoryList() {
  return request.get('/category/list')
}
