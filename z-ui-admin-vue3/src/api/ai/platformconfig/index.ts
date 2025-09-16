import request from '@/config/axios'
import type { TableData } from './types'

export const getTableListApi = (params: any): Promise<IResponse> => {
  // 转换, 适配jpa PageRequest
  params = { page: params.pageIndex, size: params.pageSize }
  console.log('分页信息', params)
  return request.get({ url: '/api/ai/platformconfig', params })
}

export const saveTableApi = (data: Partial<TableData>): Promise<IResponse> => {
  return request.post({ url: '/api/ai/platformconfig', data })
}

// 获取指定数据详情
export const getTableDetApi = (id: string): Promise<IResponse<TableData>> => {
  // url?id=xx
  return request.get({ url: '/api/ai/platformconfig/detail', params: { id } })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.delete({ url: '/api/ai/platformconfig', data: ids })
}
