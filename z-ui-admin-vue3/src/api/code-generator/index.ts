import request from '@/config/axios'
import { ColData, TableData } from './types'

export const generateCodeApi = (data: any): Promise<any> => {
  const options = { responseType: 'blob', data: data }
  return request.post({ url: '/generator/code', ...options })
}

export const getTableListApi = (params: any): Promise<TableData[]> => {
  params = { ...params, page: params.pageIndex, size: params.pageSize }
  return request.get({ url: '/generator/tables', params })
}

export const getColListApi = (params: any): Promise<ColData[]> => {
  params = { ...params, page: params.pageIndex, size: params.pageSize }
  return request.get({ url: '/generator/cols', params })
}

// 预览代码生成
export const getViewCode = (tableName: any): Promise<any> => {
  const params = { tableName: tableName }
  return request.get({ url: '/generator/code/view', params })
}
