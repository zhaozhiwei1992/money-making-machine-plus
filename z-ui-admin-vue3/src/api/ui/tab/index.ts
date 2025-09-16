import request from '@/config/axios'
import type { TabVO } from './types'

export const getTabListByMenuApi = (menuId: string | undefined): Promise<TabVO[]> => {
  return request.get({ url: '/ui/tabs/menu/' + menuId })
}

export const saveTableApi = (data: Partial<TabVO>): Promise<TabVO> => {
  return request.post({ url: '/ui/tabs', data })
}

export const getTableDetApi = (id: string): Promise<IResponse<TabVO>> => {
  return request.get({ url: '/ui/tabs/detail', params: { id } })
}

export const delTableListApi = (ids: string[] | number[]): Promise<string> => {
  return request.delete({ url: '/ui/tabs', data: { ids } })
}
