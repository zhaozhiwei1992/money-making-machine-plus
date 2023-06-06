import request from '@/config/axios'
import type { TabVO } from './types'

export const getTabListApi = (menuId: string | undefined): Promise<IResponse> => {
  return request.get({ url: '/ui/tab/list', params: { menuId } })
}

export const saveTableApi = (data: Partial<TabVO>): Promise<IResponse> => {
  return request.post({ url: '/ui/tab/save', data })
}

export const getTableDetApi = (id: string): Promise<IResponse<TabVO>> => {
  return request.get({ url: '/ui/tab/detail', params: { id } })
}

export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.post({ url: '/ui/tab/delete', data: { ids } })
}
