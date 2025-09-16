// 翻译值集
export interface MappingType {
  value: string | number | boolean
  name: string
}

// 可编辑列表定义参数
export interface EditTableColumn {
  code: string
  name: string
  type: string // Input, Select,...
  required: boolean
  source: string
  isEdit: boolean
  width: number
  mapping: MappingType[]
}
