import request from '@/config/axios'
import { TableData } from './types'

export const getTableListApi = (area: any): Promise<IResponse> => {
  area = { ...area, page: area.pageIndex, size: area.pageSize }
  return request.get({ url: '/system/areas', area: area })
}

export const saveTableApi = (data: Partial<TableData>): Promise<IResponse> => {
  return request.post({ url: '/system/areas', data })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.delete({ url: '/system/areas', data: ids })
}
