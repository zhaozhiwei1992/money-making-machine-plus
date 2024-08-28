import request from '@/config/axios'
import type { ToolButtonVO } from './types'

export const getToolButtonListApi = (menuId: string | undefined): Promise<ToolButtonVO[]> => {
  return request.get({ url: '/ui/tool-buttons/menu/' + menuId })
}

export const saveTableApi = (data: Partial<ToolButtonVO>): Promise<ToolButtonVO> => {
  return request.post({ url: '/ui/tool-buttons', data })
}

export const getTableDetApi = (id: string): Promise<IResponse<ToolButtonVO>> => {
  return request.get({ url: '/ui/tool-buttons/detail', params: { id } })
}

export const delTableListApi = (ids: string[] | number[]): Promise<string> => {
  return request.delete({ url: '/ui/tool-buttons', data: { ids } })
}
