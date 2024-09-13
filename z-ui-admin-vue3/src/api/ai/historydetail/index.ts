import request from '@/config/axios'
import { HistoryDetailVO } from './types'
import { ComponentOptions } from '@/types/components'

export const getTableListApi = (params: any): Promise<IResponse> => {
  params = { ...params, page: params.pageIndex, size: params.pageSize }
  return request.get({ url: '/history/detail', params })
}

export const saveTableApi = (data: Partial<HistoryDetailVO>): Promise<IResponse> => {
  return request.post({ url: '/history/detail', data })
}

// 获取指定数据详情
export const getTableDetApi = (id: string): Promise<IResponse<HistoryDetailVO>> => {
  return request.get({ url: '/history/detail', params: { id } })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.delete({ url: '/history/detail', data: ids })
}

// 查询引擎树
export const getHistorydetailSelect = async (): Promise<ComponentOptions[]> => {
  return await request.get({ url: '/history/detail/list' })
}
