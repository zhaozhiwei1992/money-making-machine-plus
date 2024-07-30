export type TableData = {
  type: string
  value: string
}

export type SysFiles = {
  /**
   * 盘符路径
   */
  dirName: string

  /**
   * 盘符类型
   */
  sysTypeName: string

  /**
   * 文件类型
   */
  typeName: string

  /**
   * 总大小
   */
  total: string

  /**
   * 剩余大小
   */
  free: string

  /**
   * 已经使用量
   */
  used: string

  /**
   * 资源的使用率
   */
  usage: string
}

export type ServerInfo = {
  cpu: [
    {
      type: string
      value: string
    }
  ]
  sysFiles: SysFiles[]
  mem: [
    {
      type: string
      value: string
    }
  ]
  sys: [
    {
      type: string
      value: string
    }
  ]
  jvm: [
    {
      type: string
      value: string
    }
  ]
}
