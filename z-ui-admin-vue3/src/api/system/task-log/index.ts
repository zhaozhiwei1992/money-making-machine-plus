import request from '@/config/axios'

export const getTableListApi = (params: any): Promise<IResponse> => {
  params = { page: params.pageIndex, size: params.pageSize }
  return request.get({ url: '/api/task/logs', params })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.delete({ url: '/api/task/logs', data: ids })
}
