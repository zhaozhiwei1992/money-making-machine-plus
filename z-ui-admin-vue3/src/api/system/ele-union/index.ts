import request from '@/config/axios'
import { EleUnionVO } from './types'

// 查询基础信息左侧树
export const getTree = async (): Promise<any> => {
  return request.get({ url: '/system/ele-unions/left-tree' })
}

// 根据分类查询基础信息明细
export const getDetail = async (id: string) => {
  return request.get({ url: '/system/ele-unions/element-info/' + id })
}

// 新增基础信息
export const create = async (data: EleUnionVO[]) => {
  return request.post({ url: '/system/ele-unions/save/update', data: data })
}
