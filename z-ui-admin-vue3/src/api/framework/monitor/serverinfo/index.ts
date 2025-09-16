import request from '@/config/axios'
import { ServerInfo } from './types'

export const getServerInfo = (): Promise<ServerInfo> => {
  return request.get({ url: '/server/metrics' })
}
