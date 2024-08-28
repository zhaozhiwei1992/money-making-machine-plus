import request from '@/config/axios'
import { CacheContentVO, CacheKeyVO, CacheVO } from './types'

export const getCacheInfo = (): Promise<CacheVO[]> => {
  return request.get({ url: '/cache/metrics' })
}

export const getCacheKeysInfo = (cacheName: string): Promise<CacheKeyVO[]> => {
  const params = { cacheName: cacheName }
  return request.get({ url: '/cache/metrics/keys', params })
}

export const getCacheContentInfo = (cacheName: string, key: string): Promise<CacheContentVO> => {
  const params = { cacheName: cacheName, key: key }
  return request.get({ url: '/cache/metrics/content', params })
}
