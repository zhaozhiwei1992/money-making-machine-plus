import request from '@/config/axios'
import type { NoticeVO } from './types'

export const getTableListApi = (params: any): Promise<IResponse> => {
  // 转换, 适配jpa PageRequest
  params = { ...params, page: params.pageIndex, size: params.pageSize }
  // 这里直接写 params等价与 params: {page:xx, size:xx}
  return request.get({ url: '/system/notices', params })
}

export const saveTableApi = (data: Partial<NoticeVO>): Promise<IResponse> => {
  return request.post({ url: '/system/notices', data })
}

// 获取指定数据详情
export const getTableDetApi = (id: string): Promise<IResponse<NoticeVO>> => {
  // url?id=xx
  return request.get({ url: '/system/notices/detail', params: { id } })
}

// 批量删除
export const delTableListApi = (ids: string[] | number[]): Promise<IResponse> => {
  // 适配后端, 直接使用RequestBody接收数据, 不能使用{ids}, 这种表示data里的是个map对象,key为ids
  return request.delete({ url: '/system/notices', data: ids })
}

// 获取公告接收类型值集
export const getNoticeRecTypeApi = (): Promise<IResponse> => {
  return request.get({ url: '/system/ele-unions/element-info/select/notice_rec_type' })
}

// 获取通知类型
export const getNoticeTypeApi = (): Promise<IResponse> => {
  return request.get({ url: '/system/ele-unions/element-info/select/notice_type' })
}
