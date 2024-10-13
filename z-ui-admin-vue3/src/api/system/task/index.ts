import request from '@/config/axios'
import { ComponentOptions } from '@/types/components'
import { TableResponse } from '@/types/table'
import type { TableData } from './types'

export const getTableListApi = (params: any): Promise<TableResponse<TableData>> => {
  // 转换, 适配jpa PageRequest
  params = { ...params, page: params.pageIndex, size: params.pageSize }
  // 这里直接写 params等价与 params: {page:xx, size:xx}
  return request.get({ url: '/task-params', params })
}

export const saveTableApi = (data: Partial<TableData>): Promise<IResponse> => {
  return request.post({ url: '/task-params', data })
}

// 获取指定数据详情
export const getTableDetApi = (id: string): Promise<IResponse<TableData>> => {
  // url?id=xx
  return request.get({ url: '/task-params/detail', params: { id } })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<string> => {
  // 适配后端, 直接使用RequestBody接收数据, 不能使用{ids}, 构建为map对象, key为data
  return request.delete({ url: '/task-params', data: ids })
}

// 启用
export const startApi = (ids: string[] | number[]): Promise<string> => {
  return request.post({ url: '/task-params/start', data: ids })
}

// 停用
export const stopApi = (ids: string[] | number[]): Promise<string> => {
  return request.post({ url: '/task-params/stop', data: ids })
}

// 获取定时任务实现树
export const getJobSelect = async (): Promise<ComponentOptions[]> => {
  return await request.get({ url: '/task-params/select' })
}
