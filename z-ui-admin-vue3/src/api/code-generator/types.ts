export type TableData = {
  tableName: string
  tableType: 'TABLE' | 'VIEW'
  remarks?: string
}

export type TableListResponse = {
  list: TableData[]
  total: number
}

export type ColData = {
  id: string
  name: string
  order_num: number
}

export type ColListResponse = {
  list: TableData[]
  total: number
}
