import request from '@/utils/request'

// 用户注册
export function register(data) {
  return request.post('/user/register', data)
}

// 用户登录
export function login(data) {
  return request.post('/user/login', data)
}

// 获取用户信息
export function getUserInfo() {
  return request.get('/user/info')
}

// 更新用户信息
export function updateUserInfo(data) {
  return request.put('/user/info', data)
}

// 获取用户列表（管理员）
export function getUserList(params) {
  return request.get('/admin/users', { params })
}

// 更新用户状态（封禁/解封）
export function updateUserStatus(userId, status) {
  return request.put(`/admin/user/${userId}/status`, null, { params: { status } })
}

// 添加管理员（超级管理员）
export function addAdmin(data) {
  return request.post('/admin/admin', data)
}
