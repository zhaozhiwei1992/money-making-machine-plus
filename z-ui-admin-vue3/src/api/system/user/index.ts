import request from '@/config/axios'
import type { TableData } from './types'

export interface UserVO {
  id: number
  username: string
  nickname: string
  deptId: number
  postIds: string[]
  email: string
  mobile: string
  sex: number
  avatar: string
  loginIp: string
  status: number
  remark: string
  loginDate: Date
  createTime: Date
}

export const getTableListApi = (params: any): Promise<IResponse> => {
  // 转换, 适配jpa PageRequest
  params = { ...params, page: params.pageIndex, size: params.pageSize }
  console.log('分页信息', params)
  // 这里直接写 params等价与 params: {page:xx, size:xx}
  return request.get({ url: '/users', params })
}

export const saveTableApi = (data: Partial<TableData>): Promise<IResponse> => {
  return request.post({ url: '/users', data })
}

// 获取指定数据详情
export const getTableDetApi = (id: string): Promise<IResponse<TableData>> => {
  // url?id=xx
  return request.get({ url: '/users/detail', params: { id } })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  // 适配后端, 直接使用RequestBody接收数据, 不能使用{ids}, 这种表示data里的是个map对象,key为ids
  return request.delete({ url: '/users', data: ids })
}

// 获取用户精简信息列表
export const getTableListAllApi = (): Promise<IResponse<UserVO[]>> => {
  return request.get({ url: '/users/all' })
}
