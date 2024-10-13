import request from '@/config/axios'
import { SearchVO } from './types'

export const searchApi = (data: Partial<SearchVO>): Promise<SearchVO> => {
  return request.post({ url: '/ai/search', data })
}
