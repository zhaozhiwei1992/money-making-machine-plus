import request from '@/config/axios'
import type { TableData } from './types'

export const getTableListApi = (params: any): Promise<IResponse> => {
  params = { page: params.pageIndex, size: params.pageSize }
  // 这里直接写 params等价与 params: {page:xx, size:xx}
  return request.get({ url: '/menus', params })
}

export const saveTableApi = (data: Partial<TableData>): Promise<IResponse> => {
  return request.post({ url: '/menus', data })
}

// 获取指定数据详情
export const getTableDetApi = (id: string): Promise<IResponse<TableData>> => {
  // url?id=xx
  return request.get({ url: '/menus/detail', params: { id } })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  // 适配后端, 直接使用RequestBody接收数据, 不能使用{ids}, 这种表示data里的是个map对象,key为ids
  return request.delete({ url: '/menus', data: ids })
}
