import request from '@/config/axios'
import type { UserType } from './types'

interface RoleParams {
  roleName: string
}

// 请求时得看下是否自动增加了/api
export const loginApi = (data: UserType): Promise<IResponse<UserType>> => {
  return request.post({ url: '/api/login', data })
}

export const loginOutApi = (): Promise<IResponse> => {
  return request.get({ url: '/api/loginOut' })
}

export const getUserListApi = ({ params }: AxiosConfig) => {
  return request.get<{
    code: string
    data: {
      list: UserType[]
      total: number
    }
  }>({ url: '/api/users', params })
}

export const getAdminRoleApi = (
  params: RoleParams
): Promise<IResponse<AppCustomRouteRecordRaw[]>> => {
  return request.get({ url: '/api/roles', params })
}

export const getTestRoleApi = (params: RoleParams): Promise<IResponse<string[]>> => {
  return request.get({ url: '/api/roles', params })
}