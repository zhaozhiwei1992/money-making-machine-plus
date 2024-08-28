<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ref, onMounted, provide } from 'vue'
import TemplateDefault from '@/components/UI/src/TemplateDefault.vue'
import { useEmitt } from '@/hooks/web/useEmitt'
import * as methods from './demo2'
import { useRouter } from 'vue-router'

const title = ref('动态UI演示2-录入页面')
const { emitter } = useEmitt()

// 监听页签点击
emitter.on('tabClick', (tabObj: any) => {
  console.log(tabObj.componentId, tabObj.tabCode, '点击页签tabCode')
  // 做一些业务特殊处理, 比如根据code, 重新让列表去查数据
  // 构建查询条件, 查询数据
  // emitter.emit('tableLoadData', {})
})

// 监听按钮点击
emitter.on('buttonClick', (btnObj: any) => {
  console.log(btnObj, '点击按钮对象')
  return methods[btnObj.item.action](btnObj.item)
})

// 初始化, 对编辑区简单赋值
onMounted(() => {
  // emitter.emit('editform.setValue', { username: 'zhangsan' })
  setEditform1Data()
})

const setEditform1Data = async () => {
  await getComponent().editform.setValueForForm('editform', { name: '张三' })
}

const defaultTemplateRef = ref(null)

// 获取内部组件
const getComponent = () => {
  return defaultTemplateRef.value?.componentRefs
}

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
