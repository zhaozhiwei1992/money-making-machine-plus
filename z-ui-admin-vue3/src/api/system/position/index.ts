import request from '@/config/axios'
import { ComponentOptions } from '@/types/components'
import { TableData } from './types'

export const getTableListApi = (params: any): Promise<IResponse> => {
  params = { ...params, page: params.pageIndex, size: params.pageSize }
  return request.get({ url: '/system/positions', params })
}

export const saveTableApi = (data: Partial<TableData>): Promise<IResponse> => {
  return request.post({ url: '/system/positions', data })
}

// 获取指定数据详情
export const getTableDetApi = (id: string): Promise<IResponse<TableData>> => {
  return request.get({ url: '/system/positions/detail', params: { id } })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.delete({ url: '/system/positions', data: ids })
}

// 查询岗位树
export const getPositionSelect = async (): Promise<ComponentOptions[]> => {
  return await request.get({ url: '/system/positions/select' })
}
