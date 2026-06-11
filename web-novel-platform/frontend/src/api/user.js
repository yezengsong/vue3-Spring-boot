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
