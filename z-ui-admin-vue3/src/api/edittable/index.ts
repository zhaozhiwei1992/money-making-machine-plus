import request from '@/config/axios'

export const getTableDataListApi = (params: any): Promise<IResponse> => {
  return request.get({ url: params.url, params })
}
