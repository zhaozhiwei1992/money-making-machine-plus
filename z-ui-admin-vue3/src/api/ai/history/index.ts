import request from '@/config/axios'
import { HistoryVO } from './types'

export const getTableListApi = (params: any): Promise<IResponse> => {
  params = { ...params, page: params.pageIndex, size: params.pageSize }
  return request.get({ url: '/ai/histories', params })
}

export const saveTableApi = (data: Partial<HistoryVO>): Promise<IResponse> => {
  return request.post({ url: '/ai/histories', data })
}

// 获取指定数据详情
export const getTableDetApi = (id: string): Promise<IResponse<HistoryVO>> => {
  return request.get({ url: '/ai/histories/detail', params: { id } })
}

// 批量删除
export const delHistoryApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.delete({ url: '/ai/histories', data: ids })
}

// 查询引擎树
export const getHistories = async (): Promise<HistoryVO[]> => {
  return await request.get({ url: '/ai/histories/list' })
}
