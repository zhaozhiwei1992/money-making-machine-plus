import request from '@/config/axios'
import type { RegisterVO, UserType } from './types'

interface RoleParams {
  roleName: string
}

// 请求时得看下是否自动增加了
export const loginApi = (data: UserType): Promise<UserType> => {
  return request.post({ url: '/system/login', data })
  // 等价
  // const options = { data: data }
  // return request.post({ url: '/system/login', ...options })
}

export const loginOutApi = (): Promise<string> => {
  // return request.post({ url: '/system/loginOut' })
  return request.post({ url: '/system/loginOut' })
}

export const getUserListApi = ({ params }: AxiosConfig) => {
  return request.get<{
    code: string
    data: {
      list: UserType[]
      total: number
    }
  }>({ url: '/system/users', params })
}

export const getAdminRoleApi = (params: RoleParams): Promise<AppCustomRouteRecordRaw[]> => {
  return request.get({ url: '/system/role/list', params })
}

export const getTestRoleApi = (params: RoleParams): Promise<string[]> => {
  return request.get({ url: '/system/role/list', params })
}

export const getMenuRouteListApi = (params: RoleParams): Promise<AppCustomRouteRecordRaw[]> => {
  return request.get({ url: '/system/menus/route', params })
}

// 获取验证码
export const getImgCodeApi = (): Promise<any> => {
  // const options = { responseType: 'arraybuffer' }
  const options = { responseType: 'text' }
  return request.get({ url: '/captcha/numCode', ...options })
}

export const registerApi = (data: RegisterVO): Promise<RegisterVO> => {
  return request.post({ url: '/system/register', data })
}
