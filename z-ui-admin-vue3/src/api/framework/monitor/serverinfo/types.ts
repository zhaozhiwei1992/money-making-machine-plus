export type TableData = {
  type: string
  value: string
}

export type ServerInfo = {
  cpu: [
    {
      type: string
      value: string
    }
  ]
  disk: [
    {
      type: string
      value: string
    }
  ]
  memory: [
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
