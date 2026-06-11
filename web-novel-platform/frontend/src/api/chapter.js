import request from '@/utils/request'

// 获取章节列表
export function getChapters(novelId) {
  return request.get(`/novel/${novelId}/chapters`)
}

// 获取章节内容
export function getChapterContent(novelId, chapterId) {
  return request.get(`/novel/${novelId}/chapter/${chapterId}`)
}
