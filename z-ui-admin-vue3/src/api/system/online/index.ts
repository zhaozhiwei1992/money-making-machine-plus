import request from '@/config/axios'

export const getTableListApi = (params: any): Promise<IResponse> => {
  params = { ...params, page: params.pageIndex, size: params.pageSize }
  return request.get({ url: '/system/online/users', params })
}

// 删除
export const delTableListApi = (loginName: string): Promise<IResponse> => {
  return request.delete({ url: '/system/online/users/' + loginName })
}
