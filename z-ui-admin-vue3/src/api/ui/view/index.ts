import request from '@/config/axios'
import type { ComponentVO } from './types'

// 获取指定菜单下的组件信息
export const getComponentListApi = (menuId: string): Promise<IResponse<ComponentVO[]>> => {
  return request.get({ url: '/ui/component/list/', params: { menuId } })
}

export const saveTableApi = (data: Partial<ComponentVO>): Promise<IResponse> => {
  return request.post({ url: '/ui/component/save', data })
}

export const getTableDetApi = (id: string): Promise<IResponse<ComponentVO>> => {
  return request.get({ url: '/ui/component/detail', params: { id } })
}

export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.post({ url: '/ui/component/delete', data: { ids } })
}
