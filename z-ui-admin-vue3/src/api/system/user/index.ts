import request from '@/config/axios'
import type { TableResponse } from '@/types/table'
import type { PasswordResetVO, UserVO } from './types'

export const getTableListApi = (params: any): Promise<TableResponse> => {
  // 转换, 适配jpa PageRequest
  params = { ...params, page: params.pageIndex, size: params.pageSize }
  console.log('分页信息', params)
  // 这里直接写 params等价与 params: {page:xx, size:xx}
  return request.get({ url: '/system/users', params })
}

export const saveTableApi = (data: Partial<UserVO>): Promise<UserVO> => {
  return request.post({ url: '/system/users', data })
}

export const resetPasswordApi = (data: Partial<PasswordResetVO>): Promise<UserVO | string> => {
  return request.post({ url: '/system/users/resetpass', data })
}

// 个人信息修改接口
export const personalInfoModApi = (data: Partial<UserVO>): Promise<UserVO> => {
  return request.post({ url: '/system/users/personal/mod', data })
}

// 获取指定数据详情
export const getTableDetApi = (id: string): Promise<IResponse<UserVO>> => {
  // url?id=xx
  return request.get({ url: '/system/users/detail', params: { id } })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<string> => {
  // 适配后端, 直接使用RequestBody接收数据, 不能使用{ids}, 这种表示data里的是个map对象,key为ids
  return request.delete({ url: '/system/users', data: ids })
}

// 获取用户精简信息列表
export const getTableListAllApi = (): Promise<IResponse<UserVO[]>> => {
  return request.get({ url: '/system/users/all' })
}

// 获取头像
export const getAvatarImgApi = (): Promise<any> => {
  // const options = { responseType: 'arraybuffer' }
  const options = { responseType: 'text' }
  return request.get({ url: '/system/users/avatar', ...options })
}
