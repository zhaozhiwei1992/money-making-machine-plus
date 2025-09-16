<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ref, onMounted, provide } from 'vue'
import TemplateDefault from '@/components/UI/src/TemplateDefault.vue'
import { useEmitt } from '@/hooks/web/useEmitt'
import * as methods from './demo3'
import { useRouter } from 'vue-router'

const title = ref('动态UI演示3-可编辑列表')

// 监听按钮点击
useEmitt({
  name: 'buttonClick',
  callback: (btnObj) => {
    console.log(btnObj, '点击按钮对象')
    return methods[btnObj.item.action](btnObj.item)
  }
})

// 初始化, 对编辑区简单赋值
onMounted(() => {
  // emitter.emit('editform.setValue', { username: 'zhangsan' })
  // 可以对列表给一些初始值
})

const menuId = ref()

const { currentRoute } = useRouter()
// 路由的name为menuId
menuId.value = currentRoute.value.name
provide('menuId', menuId.value)
</script>

<template>
  <ContentWrap :title="title">
    <!-- 固定模板，实际生产使用, 这里参考demo1, 应是活的 -->
    <TemplateDefault />
  </ContentWrap>
</template>
