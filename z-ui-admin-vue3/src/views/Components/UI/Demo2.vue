<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ref, onMounted } from 'vue'
import TemplateDefault from '@/components/UI/src/TemplateDefault.vue'
import { useEmitt } from '@/hooks/web/useEmitt'
import * as methods from './demo2'

const title = ref('动态UI演示2-录入页面')
const { emitter } = useEmitt()

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
  emitter.emit('editform.setValue', { username: 'zhangsan' })
})
</script>

<template>
  <ContentWrap :title="title">
    <TemplateDefault />
  </ContentWrap>
</template>
