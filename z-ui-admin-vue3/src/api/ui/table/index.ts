import request from '@/config/axios'
import { TableVO } from './types'

export const getTableColListByMenuApi = (menuId: string | undefined): Promise<TableVO[]> => {
  return request.get({ url: '/ui/tables/menu/' + menuId })
}

// 需要动态传入url, 查询数据
export const getTableDataListApi = (params: any): Promise<IResponse<TableVO[]>> => {
  return request.get({ url: params.url, params })
}
