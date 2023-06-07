import request from '@/config/axios'
import type { EditformVO } from './types'

export const getEditformListByMenuApi = (menuId: string | undefined): Promise<IResponse> => {
  return request.get({ url: '/ui/editforms/menu/' + menuId })
}

export const saveTableApi = (data: Partial<EditformVO>): Promise<IResponse> => {
  return request.post({ url: '/ui/editforms', data })
}

export const getTableDetApi = (id: string): Promise<IResponse<EditformVO>> => {
  return request.get({ url: '/ui/editforms', params: { id } })
}

export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.delete({ url: '/ui/editforms', data: { ids } })
}
