import type { App } from 'vue'
import { Icon } from './Icon'

/**
 * 可以配置一些全局组件
 * https://cn.vuejs.org/guide/components/registration.html#global-registration
 */
export const setupGlobCom = (app: App<Element>): void => {
  app.component('Icon', Icon)
}
