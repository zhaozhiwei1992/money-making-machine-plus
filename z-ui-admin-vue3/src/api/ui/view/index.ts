import request from '@/config/axios'
import type { ComponentVO } from './types'

// 获取指定菜单下的组件信息
export const getComponentListApi = (menuId: string | undefined): Promise<ComponentVO[]> => {
  return request.get({ url: '/ui/components/menu/' + menuId })
}

export const saveTableApi = (data: Partial<ComponentVO>): Promise<ComponentVO> => {
  return request.post({ url: '/ui/components', data })
}

export const getTableDetApi = (id: string): Promise<ComponentVO> => {
  return request.get({ url: '/ui/components/' + id })
}

export const delTableListApi = (ids: string[] | number[]): Promise<string> => {
  return request.delete({ url: '/ui/components', data: { ids } })
}
