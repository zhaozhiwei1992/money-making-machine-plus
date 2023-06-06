import request from '@/config/axios'
import type { TableVO } from './types'

export const getTableColListByMenuApi = (menuId: string | undefined): Promise<IResponse> => {
  return request.get({ url: '/ui/tables/menu/' + menuId })
}

export const saveTableApi = (data: Partial<TableVO>): Promise<IResponse> => {
  return request.post({ url: '/ui/tables', data })
}

export const getTableDetApi = (id: string): Promise<IResponse<TableVO>> => {
  return request.get({ url: '/ui/tables/detail', params: { id } })
}

export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.delete({ url: '/ui/tables', data: { ids } })
}
