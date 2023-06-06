import request from '@/config/axios'
import type { ToolButtonVO } from './types'

export const getToolButtonListApi = (menuId: string | undefined): Promise<IResponse> => {
  return request.get({ url: '/ui/tool-button/list', params: { menuId } })
}

export const saveTableApi = (data: Partial<ToolButtonVO>): Promise<IResponse> => {
  return request.post({ url: '/ui/tool-button/save', data })
}

export const getTableDetApi = (id: string): Promise<IResponse<ToolButtonVO>> => {
  return request.get({ url: '/ui/tool-button/detail', params: { id } })
}

export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.post({ url: '/ui/tool-button/delete', data: { ids } })
}
