import request from '@/config/axios'
import { TableData } from './types'
import { ComponentOptions } from '@/types/components'

export const getTableListApi = (params: any): Promise<IResponse> => {
  params = { ...params, page: params.pageIndex, size: params.pageSize }
  return request.get({ url: '/positions', params })
}

export const saveTableApi = (data: Partial<TableData>): Promise<IResponse> => {
  return request.post({ url: '/positions', data })
}

// 获取指定数据详情
export const getTableDetApi = (id: string): Promise<IResponse<TableData>> => {
  return request.get({ url: '/positions/detail', params: { id } })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.delete({ url: '/positions', data: ids })
}

// 查询岗位树
export const getPositionSelect = async (): Promise<ComponentOptions[]> => {
  return await request.get({ url: '/positions/select' })
}
