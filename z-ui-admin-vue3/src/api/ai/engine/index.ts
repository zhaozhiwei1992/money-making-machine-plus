import request from '@/config/axios'
import { ComponentOptions } from '@/types/components'
import { EngineVO } from './types'

export const getTableListApi = (params: any): Promise<IResponse> => {
  params = { ...params, page: params.pageIndex, size: params.pageSize }
  return request.get({ url: '/ai/engines', params })
}

export const saveTableApi = (data: Partial<EngineVO>): Promise<IResponse> => {
  return request.post({ url: '/ai/engines', data })
}

// 获取指定数据详情
export const getTableDetApi = (id: string): Promise<IResponse<EngineVO>> => {
  return request.get({ url: '/ai/engines/detail', params: { id } })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.delete({ url: '/ai/engines', data: ids })
}

// 查询引擎树
export const getEngineSelect = async (): Promise<ComponentOptions[]> => {
  return await request.get({ url: '/ai/engines/select' })
}
