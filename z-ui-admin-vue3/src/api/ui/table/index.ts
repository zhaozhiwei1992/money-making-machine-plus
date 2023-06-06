import request from '@/config/axios'
import type { TableVO } from './types'

export const getTableConfigListApi = (menuId: string | undefined): Promise<IResponse> => {
  return request.get({ url: '/ui/table/list', params: { menuId } })
}

export const saveTableApi = (data: Partial<TableVO>): Promise<IResponse> => {
  return request.post({ url: '/ui/table/save', data })
}

export const getTableDetApi = (id: string): Promise<IResponse<TableVO>> => {
  return request.get({ url: '/ui/table/detail', params: { id } })
}

export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.post({ url: '/ui/table/delete', data: { ids } })
}
