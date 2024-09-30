import request from '@/config/axios'

// 头像上传
export const uploadAvatarApi = (data: FormData): Promise<string> => {
  return request.post({ url: '/uploads', data: data, headersType: 'multipart/form-data' })
}
