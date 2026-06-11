import request from '@/utils/request'

// 获取评论列表
export function getComments(chapterId) {
  return request.get(`/chapter/${chapterId}/comments`)
}

// 发表评论
export function createComment(chapterId, data) {
  return request.post(`/chapter/${chapterId}/comments`, data)
}

// 回复评论
export function replyComment(chapterId, commentId, data) {
  return request.post(`/chapter/${chapterId}/comments/${commentId}/reply`, data)
}

// 点赞评论
export function likeComment(commentId) {
  return request.post(`/chapter/comments/${commentId}/like`)
}
