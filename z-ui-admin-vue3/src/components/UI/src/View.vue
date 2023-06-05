<script setup lang="ts">
import { ref, reactive, onMounted, defineAsyncComponent } from 'vue'
import { getComponentListApi } from '@/api/ui/view'

// 动态组件
interface ComponentType {
  component: any
  name: string
  id: number
}
const components = ref<ComponentType[]>([])

// 模拟后端返回的components信息
const componentSchema = reactive<ComponentType[]>([
  {
    id: 1,
    name: 'toolbutton',
    component: defineAsyncComponent(() => import('./components/toolbutton.vue'))
  },
  {
    id: 2,
    name: 'queryform',
    component: defineAsyncComponent(() => import('./components/queryform.vue'))
  },
  {
    id: 3,
    name: 'tab',
    component: defineAsyncComponent(() => import('./components/tab.vue'))
  },
  {
    id: 4,
    name: 'table',
    component: defineAsyncComponent(() => import('./components/table.vue'))
  }
])

// 传递给子组件的属性
const menuid = ref('')

onMounted(() => {
  console.log('挂载组件')
  // 获取menuid
  menuid.value = '1'
  // 获取所有组件信息, 通过下述方式push
  // getComponentListApi(menuid.value).then((res) => {
  //   res.data.forEach((element) => {
  //     const component: ComponentType = {
  //       id: element.id,
  //       name: element.name,
  //       component: defineAsyncComponent(() => import('./components/' + element.name + '.vue'))
  //     }
  //     components.value.push(component)
  //   })
  // })
  components.value.push(...componentSchema)
  console.log(components)
})
</script>

<template>
  <!-- 配置的界面都由该view.vue组件动态渲染 -->
  <!-- 动态加载组件 -->
  <component
    v-for="item in components"
    :is="item.component"
    :menuid="menuid"
    :key="item.id"
    :ref="item.name"
  />
</template>
