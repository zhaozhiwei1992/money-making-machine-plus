import request from '@/config/axios'
import { SearchVO } from './types'

export const saveTableApi = (data: Partial<SearchVO>): Promise<IResponse> => {
  return request.post({ url: '/search', data })
}
