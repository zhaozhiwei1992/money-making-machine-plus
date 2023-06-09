import request from '@/config/axios'

export const getTableColListByMenuApi = (menuId: string | undefined): Promise<IResponse> => {
  return request.get({ url: '/ui/tables/menu/' + menuId })
}

// 需要动态传入url, 查询数据
export const getTableDataListApi = (params: any): Promise<IResponse> => {
  return request.get({ url: params.url, params })
}
