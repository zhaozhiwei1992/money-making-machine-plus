export type MenuVO = {
  id: string
  name: string
  url: string
  icon_cls: string
  order_num: number
  parent_id: string
  component: string
  template: string
  config: string
  children: MenuVO[]
}
