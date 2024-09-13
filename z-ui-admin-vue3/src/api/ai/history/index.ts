import request from '@/config/axios'
import { HistoryVO } from './types'
import { ComponentOptions } from '@/types/components'

export const getTableListApi = (params: any): Promise<IResponse> => {
  params = { ...params, page: params.pageIndex, size: params.pageSize }
  return request.get({ url: '/histories', params })
}

export const saveTableApi = (data: Partial<HistoryVO>): Promise<IResponse> => {
  return request.post({ url: '/histories', data })
}

// 获取指定数据详情
export const getTableDetApi = (id: string): Promise<IResponse<HistoryVO>> => {
  return request.get({ url: '/histories/detail', params: { id } })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.delete({ url: '/histories', data: ids })
}

// 查询引擎树
export const getHistorieselect = async (): Promise<ComponentOptions[]> => {
  return await request.get({ url: '/histories/select' })
}
