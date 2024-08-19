import request from '@/config/axios'
import type { UserVO, PasswordResetVO } from './types'

export const getTableListApi = (params: any): Promise<any> => {
  // 转换, 适配jpa PageRequest
  params = { ...params, page: params.pageIndex, size: params.pageSize }
  console.log('分页信息', params)
  // 这里直接写 params等价与 params: {page:xx, size:xx}
  return request.get({ url: '/users', params })
}

export const saveTableApi = (data: Partial<UserVO>): Promise<UserVO> => {
  return request.post({ url: '/users', data })
}

export const resetPasswordApi = (data: Partial<PasswordResetVO>): Promise<UserVO> => {
  return request.post({ url: '/users/resetpass', data })
}

// 获取指定数据详情
export const getTableDetApi = (id: string): Promise<IResponse<UserVO>> => {
  // url?id=xx
  return request.get({ url: '/users/detail', params: { id } })
}

// 根据登录名获取指定数据详情
export const getUserDetLoginApi = (login: string): Promise<UserVO> => {
  return request.get({ url: '/users/detail/login', params: { login } })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<string> => {
  // 适配后端, 直接使用RequestBody接收数据, 不能使用{ids}, 这种表示data里的是个map对象,key为ids
  return request.delete({ url: '/users', data: ids })
}

// 获取用户精简信息列表
export const getTableListAllApi = (): Promise<UserVO[]> => {
  return request.get({ url: '/users/all' })
}
