import request from '@/config/axios'

export const generateCodeApi = (data: any): Promise<any> => {
  const options = { responseType: 'blob', data: data }
  return request.post({ url: '/generator/code', ...options })
}
