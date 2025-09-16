import request from '@/config/axios'
import { TableData } from './types'
import { ComponentOptions } from '@/types/components'

export const getTableListApi = (departments: any): Promise<IResponse> => {
  departments = { ...departments, page: departments.pageIndex, size: departments.pageSize }
  return request.get({ url: '/departments', departments })
}

export const saveTableApi = (data: Partial<TableData>): Promise<IResponse> => {
  return request.post({ url: '/departments', data })
}

// 获取指定数据详情
export const getTableDetApi = (id: string): Promise<IResponse<TableData>> => {
  return request.get({ url: '/departments/detail', departments: { id } })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  return request.delete({ url: '/departments', data: ids })
}

// 查询部门树
export const getDeptSelect = async (): Promise<ComponentOptions[]> => {
  return await request.get({ url: '/departments/select' })
}
