export type MenuVO = {
  id: number
  createdBy: string
  createdDate: Date
  url: string
  name: string
  iconCls: string
  orderNum: number
  keepAlive: boolean
  requireAuth: boolean
  parentId: number
  enabled: boolean
  config: string
  component: string
  template: string
  menuType: number
  permissionCode: string
  children: MenuVO[]
}
