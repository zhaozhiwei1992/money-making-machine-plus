import request from '@/config/axios'

export type DictDataVO = {
  id: number | undefined
  sort: number | undefined
  label: string
  value: string
  dictType: string
  status: number
  colorType: string
  cssClass: string
  remark: string
  createTime: Date
}

// 查询字典数据（精简)列表
export const listSimpleDictData = () => {
  return request.get({ url: '/system/dict/list' })
}
