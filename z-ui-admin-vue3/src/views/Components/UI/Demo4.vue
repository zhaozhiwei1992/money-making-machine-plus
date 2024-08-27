<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ref, onMounted, provide } from 'vue'
import TemplateDefault from '@/components/UI/src/TemplateDefault.vue'
import { useEmitt } from '@/hooks/web/useEmitt'
import * as methods from './demo2'
import { useRouter } from 'vue-router'

const title = ref('动态UI演示4-组件引用')
const { emitter } = useEmitt()

// 初始化, 对编辑区简单赋值
onMounted(() => {
  emitter.emit('editform.setValue', { username: 'zhangsan' })
})

// 获取内部组件
const getComponent = (name) => {
  return emitter.emit('component.get', { name })
}

const getEditform1Data = () => {
  const editformRef: any = getComponent('editform1')
  const data = editformRef.getFormData()
  console.log('获取表单数据editform1', data)
}

// 传递菜单id
const menuId = ref()
const { currentRoute } = useRouter()
// 路由的name为menuId
menuId.value = currentRoute.value.name
provide('menuId', menuId.value)
</script>

<template>
  <ContentWrap :title="title">
    <TemplateDefault ref="defaultTemplateRef" />
  </ContentWrap>
</template>
