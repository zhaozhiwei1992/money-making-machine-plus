import request from '@/config/axios'
import type { EditformVO } from './types'

export const getEditformListApi = (menuId: string | undefined): Promise<IResponse> => {
  return request.get({ url: '/ui/editforms/list', params: { menuId } })
}

export const saveTableApi = (data: Partial<EditformVO>): Promise<IResponse> => {
  return request.post({ url: '/ui/editforms/save', data })
}

export const getTableDetApi = (id: string): Promise<IResponse<EditformVO>> => {
  return request.get({ url: '/ui/editforms/detail', params: { id } })
}

export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.post({ url: '/ui/editforms/delete', data: { ids } })
}
