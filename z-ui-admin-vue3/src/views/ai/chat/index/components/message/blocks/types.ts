/** 内容块类型 */
export interface ContentBlock {
  /** 块类型: 'markdown' 或注册的自定义类型如 'echarts', 'video' 等 */
  type: string
  /** 块内容: markdown 块为 markdown 文本, 特殊块为 code fence 内的原始文本 */
  content: string
  /** 块是否完整(闭合)。流式传输中最后一个块可能 incomplete */
  complete: boolean
}
