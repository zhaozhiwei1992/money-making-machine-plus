import request from '@/config/axios'
import type { TableData } from './types'

export const getTableListApi = (permissions: any): Promise<IResponse> => {
  permissions = { ...permissions, page: permissions.pageIndex, size: permissions.pageSize }
  return request.get({ url: '/permissions', permissions })
}

export const saveTableApi = (data: Partial<TableData>): Promise<IResponse> => {
  return request.post({ url: '/permissions', data })
}

// 获取指定数据详情
export const getTableDetApi = (id: string): Promise<IResponse<TableData>> => {
  return request.get({ url: '/permissions/detail', permissions: { id } })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.delete({ url: '/permissions', data: ids })
}
