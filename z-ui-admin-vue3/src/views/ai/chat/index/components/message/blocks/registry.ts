import { defineAsyncComponent, type Component } from 'vue'

const registry = new Map<string, Component>()

/** 注册一个块类型及其渲染组件 */
export function registerBlock(type: string, component: Component) {
  registry.set(type, component)
}

/** 获取块类型对应的渲染组件，未注册返回 null */
export function getBlockComponent(type: string): Component | null {
  return registry.get(type) ?? null
}

/** 获取所有已注册的块类型名称集合（供 parser 使用） */
export function getRegisteredTypes(): Set<string> {
  return new Set(registry.keys())
}

// ========== 注册内置块类型（异步加载） ==========
registerBlock(
  'echarts',
  defineAsyncComponent(() => import('./BlockEcharts.vue'))
)
registerBlock(
  'video',
  defineAsyncComponent(() => import('./BlockVideo.vue'))
)
registerBlock(
  'table',
  defineAsyncComponent(() => import('./BlockTable.vue'))
)
registerBlock(
  'html',
  defineAsyncComponent(() => import('./BlockHtml.vue'))
)
registerBlock(
  'image',
  defineAsyncComponent(() => import('./BlockImage.vue'))
)
registerBlock(
  'href',
  defineAsyncComponent(() => import('./BlockHref.vue'))
)
