export type TableData = {
  id: string
  code: string
  name: string
}

export interface RoleVO {
  id: string
  name: string
  code: string
  createTime: Date
  menuIdList: string | string[]
}
