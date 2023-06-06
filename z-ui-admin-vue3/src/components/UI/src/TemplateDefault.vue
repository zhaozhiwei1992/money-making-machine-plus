<script setup lang="ts">
import { ref, onMounted, inject, defineAsyncComponent } from 'vue'
import { getComponentListApi } from '@/api/ui/view'

const uiComponents: any = import.meta.glob('@/components/UI/src/components/*.vue')

// 动态组件
interface ComponentType {
  component: any
  name: string
  id: number
}
const components = ref<ComponentType[]>([])

// 模拟后端返回的components信息
// const componentSchema = reactive<ComponentType[]>([
//   {
//     id: 1,
//     name: 'toolbutton',
//     component: defineAsyncComponent(() => import('./components/toolbutton.vue'))
//   },
//   {
//     id: 2,
//     name: 'queryform',
//     component: defineAsyncComponent(() => import('./components/queryform.vue'))
//   },
//   {
//     id: 3,
//     name: 'tab',
//     component: defineAsyncComponent(() => import('./components/tab.vue'))
//   },
//   {
//     id: 4,
//     name: 'table',
//     component: defineAsyncComponent(() => import('./components/table.vue'))
//   }
// ])

onMounted(() => {
  console.log(uiComponents, '挂载组件')
  // 获取menuid
  const menuId: string | undefined = inject('menuId')
  // 获取所有组件信息, 通过下述方式push
  getComponentListApi(menuId).then((res) => {
    res.data.forEach((element) => {
      const component: ComponentType = {
        id: element.id,
        name: element.name,
        // component: defineAsyncComponent(() => import('./components/' + element.name + '.vue'))
        // 上述方式会被vite报异常
        component: defineAsyncComponent(
          uiComponents['/src/components/UI/src/components/' + element.component + '.vue']
        )
      }
      components.value.push(component)
    })
  })
  // components.value.push(...componentSchema)
  console.log(components)
})
</script>

<template>
  <!-- 配置的界面都由该vue组件动态渲染 -->
  <!-- 动态加载组件 特殊的页面布局需单独增加文件, 进行特殊处理, 菜单template使用新组件-->
  <component
    v-for="item in components"
    :is="item.component"
    :key="item.id"
    :ref="item.name"
    :componentId="item.name"
  />
</template>
