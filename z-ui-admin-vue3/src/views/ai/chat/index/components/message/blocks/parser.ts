import type { ContentBlock } from './types'

/**
 * 将消息内容解析为结构化块列表。
 *
 * 识别标准 markdown code fence 语法，当语言标识符匹配已注册类型时，
 * 提取为特殊块；未注册的语言标识符（如 javascript）保留为普通 markdown。
 *
 * 设计为纯函数、O(n) 单次遍历，适合在 computed 中高频调用。
 *
 * @param raw 消息原始内容字符串
 * @param knownTypes 已注册的块类型集合
 * @returns 有序的内容块数组
 */
export function parseContent(raw: string, knownTypes: Set<string>): ContentBlock[] {
  if (!raw) return []

  const blocks: ContentBlock[] = []
  const lines = raw.split('\n')
  let i = 0
  let currentMarkdown: string[] = []

  const flushMarkdown = () => {
    const text = currentMarkdown.join('\n')
    if (text) {
      blocks.push({ type: 'markdown', content: text, complete: true })
    }
    currentMarkdown = []
  }

  while (i < lines.length) {
    const line = lines[i]
    // 匹配 code fence 开始行: ```lang
    const fenceMatch = line.match(/^```(\w[\w-]*)\s*$/)

    if (fenceMatch && knownTypes.has(fenceMatch[1])) {
      // 先刷出已累积的 markdown
      flushMarkdown()

      const blockType = fenceMatch[1]
      const contentLines: string[] = []
      i++ // 跳过开始 fence 行
      let closed = false

      while (i < lines.length) {
        // 匹配闭合 fence: 只有 ``` 的行
        if (/^```\s*$/.test(lines[i])) {
          closed = true
          i++ // 跳过闭合 fence 行
          break
        }
        contentLines.push(lines[i])
        i++
      }

      blocks.push({
        type: blockType,
        content: contentLines.join('\n'),
        complete: closed
      })
    } else {
      currentMarkdown.push(line)
      i++
    }
  }

  // 刷出剩余 markdown
  flushMarkdown()

  return blocks
}
