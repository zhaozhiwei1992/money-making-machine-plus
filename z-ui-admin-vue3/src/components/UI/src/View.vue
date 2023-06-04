<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ref, Ref, onMounted, defineAsyncComponent } from 'vue'
import { getComponentListApi } from '@/api/ui/view'

// 动态组件
interface ComponentType {
  component: Ref
  name: string
  id: number
}
const components = ref<ComponentType[]>([])

// 传递给子组件的属性
const menuid = ref('')

onMounted(() => {
  console.log('挂载组件')
  // 获取menuid
  menuid.value = '1'
  // 获取所有组件信息, 通过下述方式push
  getComponentListApi(menuid.value).then((res) => {
    res.data.forEach((element) => {
      const component: ComponentType = {
        id: element.id,
        name: element.name,
        component: defineAsyncComponent(() => import('./components/' + element.name + '.vue'))
      }
      components.value.push(component)
    })
  })
})
</script>

<template>
  <ContentWrap>
    <!-- 配置的界面都由该view.vue组件动态渲染 -->
    <!-- 动态加载组件 -->
    <component
      v-for="item in components"
      :is="item.name"
      :menuid="menuid"
      :key="item.id"
      :ref="item.name"
    />
  </ContentWrap>
</template>
