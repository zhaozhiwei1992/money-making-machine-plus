import { ComponentName } from '@/types/components'

export type EditformVO = {
  id: string
  code: string
  name: string
  action: string
  size: string
  type: ComponentName
  menuId: string
  componentId: string
  requirement: boolean
  // 数据源ele_code
  source: string
}
