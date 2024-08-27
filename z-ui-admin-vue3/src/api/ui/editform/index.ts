import request from '@/config/axios'
import type { EditformVO } from './types'

// 根据菜单id, 加载所有编辑区信息
export const getEditformListByMenuApi = (menuId: string | undefined): Promise<EditformVO[]> => {
  return request.get({ url: '/ui/editforms/menu/' + menuId })
}

export const saveTableApi = (data: Partial<EditformVO>): Promise<EditformVO> => {
  return request.post({ url: '/ui/editforms', data })
}

export const getTableDetApi = (id: string): Promise<IResponse<EditformVO>> => {
  return request.get({ url: '/ui/editforms', params: { id } })
}

export const delTableListApi = (ids: string[] | number[]): Promise<string> => {
  return request.delete({ url: '/ui/editforms', data: { ids } })
}
