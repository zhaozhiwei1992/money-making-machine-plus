import request from '@/config/axios'
import type { UploadVO } from './types'

// 头像上传
export const uploadAvatarApi = (data: FormData): Promise<UploadVO> => {
  return request.post({ url: '/system/uploads', data: data, headersType: 'multipart/form-data' })
}
