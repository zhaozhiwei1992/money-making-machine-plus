import request from '@/config/axios'
import { ComponentOptions } from '@/types/components'
import type { TableData } from './types'

export interface RoleVO {
  id: number
  name: string
  code: string
  sort: number
  status: number
  type: number
  dataScope: number
  dataScopeDeptIds: number[]
  createTime: Date
}

export interface UpdateStatusReqVO {
  id: number
  status: number
}

export const getTableListApi = (params: any): Promise<IResponse> => {
  // 转换, 适配jpa PageRequest
  params = { ...params, page: params.pageIndex, size: params.pageSize }
  console.log('分页信息', params)
  // 这里直接写 params等价与 params: {page:xx, size:xx}
  return request.get({ url: '/system/roles', params })
}

export const saveTableApi = (data: Partial<TableData>): Promise<IResponse> => {
  return request.post({ url: '/system/roles', data })
}

// 获取指定数据详情
export const getTableDetApi = (id: string): Promise<IResponse<TableData>> => {
  // url?id=xx
  return request.get({ url: '/system/roles/detail', params: { id } })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  // 适配后端, 直接使用RequestBody接收数据, 不能使用{ids}, 这种表示data里的是个map对象,key为ids
  return request.delete({ url: '/system/roles', data: ids })
}

// 查询角色（精简)列表
export const getSimpleRoleList = async (): Promise<RoleVO[]> => {
  return await request.get({ url: '/system/roles/list' })
}

// 查询角色树
export const getRoleSelect = async (): Promise<ComponentOptions[]> => {
  return await request.get({ url: '/system/roles/select' })
}
