import request from '@/config/axios'
import type { QueryformVO } from './types'

export const getQueryformListApi = (menuId: string | undefined): Promise<IResponse> => {
  return request.get({ url: '/ui/queryform/list', params: { menuId } })
}

export const saveTableApi = (data: Partial<QueryformVO>): Promise<IResponse> => {
  return request.post({ url: '/ui/queryform/save', data })
}

export const getTableDetApi = (id: string): Promise<IResponse<QueryformVO>> => {
  return request.get({ url: '/ui/queryform/detail', params: { id } })
}

export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.post({ url: '/ui/queryform/delete', data: { ids } })
}
