<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ref } from 'vue'
import TemplateDefault from '@/components/UI/src/TemplateDefault.vue'
import { useEmitt } from '@/hooks/web/useEmitt'
import * as methods from './demo1'

// 监听页签点击
useEmitt({
  name: 'tabClick',
  callback: (tabCode: string) => {
    console.log(tabCode, '点击页签tabCode')
    alert(tabCode)
    // 做一些业务特殊处理, 比如根据code, 重新让列表去查数据
  }
})

interface ButtonType {
  id: number
  name: string
  action: string
  type: any
  size: any
}

// 监听按钮点击
useEmitt({
  name: 'buttonClick',
  callback: (btnObj: ButtonType) => {
    console.log(btnObj, '点击按钮对象')
    // 根据按钮编码, 或者config中特殊参数, 触发各种事件
    // vue2 写法
    // this.$bus.$off("buttonClick").$on("buttonClick", (item) => {
    //   return this[item.click](item)
    // });
    //https://segmentfault.com/q/1010000042472899
    console.log(methods)
    // vue3 这么写, instance方式用不了，找不到methods
    // add(btnObj)
    // instance.ctx.$method[btnObj.action](btnObj)
    return methods[btnObj.action](btnObj)
  }
})

const title = ref('动态UI演示1')
</script>

<template>
  <ContentWrap :title="title">
    <TemplateDefault />
  </ContentWrap>
</template>
