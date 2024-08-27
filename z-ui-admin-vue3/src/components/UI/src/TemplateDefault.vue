<script setup lang="ts">
import { ref, onMounted, inject, defineAsyncComponent, shallowRef } from 'vue'
import { getComponentListApi } from '@/api/ui/view'
import { useEmitt } from '@/hooks/web/useEmitt'

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

onMounted(async () => {
  console.log(uiComponents, '挂载组件')
  // 获取menuid
  const menuId: string | undefined = inject('menuId')
  // 获取所有组件信息, 通过下述方式push
  const res = await getComponentListApi(menuId)
  res.forEach((element) => {
    const component: ComponentType = {
      id: element.id,
      name: element.name,
      // component: defineAsyncComponent(() => import('./components/' + element.name + '.vue'))
      // 上述方式会被vite报异常
      component: shallowRef(
        defineAsyncComponent(
          uiComponents['/src/components/UI/src/components/' + element.component + '.vue']
        )
      )
      // component: shallowRef(
      //   uiComponents['/src/components/UI/src/components/' + element.component + '.vue']
      // )
    }
    components.value.push(component)
  })
  console.log('加载的组件: ', components)
})

// 定义所有组件引用, 方便在业务代码中获取组件
const componentRefs = ref({})

const setItemRef = (name) => {
  return (el) => {
    componentRefs.value[name] = el
  }
}
defineExpose({
  componentRefs
})
</script>

<template>
  <!-- 配置的界面都由该vue组件动态渲染 -->
  <!-- 动态加载组件 特殊的页面布局需单独增加文件, 进行特殊处理, 菜单template使用新组件-->
  <component
    v-for="item in components"
    :is="item.component"
    :key="item.id"
    :ref="setItemRef(item.name)"
    :componentId="item.name"
  />
</template>
