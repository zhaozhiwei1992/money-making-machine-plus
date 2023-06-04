<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ref, getCurrentInstance } from 'vue'
import View from '@/components/UI/src/View.vue'
import { useEmitt } from '@/hooks/web/useEmitt'

// 监听页签点击
useEmitt({
  name: 'tabClick',
  callback: (tabCode: string) => {
    console.log(tabCode, '点击页签tabCode')
    alert(tabCode)
    // 做一些业务特殊处理, 比如根据code, 重新让列表去查数据
  }
})

const instance: any = getCurrentInstance()

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
    // vue3 怎么写
    // this.$bus.$off("buttonClick").$on("buttonClick", (item) => {
    //   return this[item.click](item)
    // });
    instance.ctx.$refs[btnObj.action](btnObj)
    // btnObj[click](btnObj)
  }
})

const add = (btnObj: ButtonType) => {
  alert(btnObj.name)
}

const title = ref('动态UI演示1')
</script>

<template>
  <ContentWrap :title="title">
    <view />
  </ContentWrap>
</template>
