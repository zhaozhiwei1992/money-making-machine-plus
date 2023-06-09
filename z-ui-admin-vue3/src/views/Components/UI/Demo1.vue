<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { onMounted, ref, defineAsyncComponent, provide, shallowRef } from 'vue'
// import TemplateDefault from '@/components/UI/src/TemplateDefault.vue'
import { useEmitt } from '@/hooks/web/useEmitt'
import * as methods from './demo1'
import { useRouter } from 'vue-router'
import { getMenuDetApi } from '@/api/system/menu'

// 所有的模板
const templates: any = import.meta.glob('@/components/UI/src/*.vue')

const { emitter } = useEmitt()

// 监听页签点击
useEmitt({
  name: 'tabClick',
  callback: (tabObj) => {
    console.log(tabObj.componentId, tabObj.tabCode, '点击页签tabCode')
    // 做一些业务特殊处理, 比如根据code, 重新让列表去查数据
    // 构建查询条件, 查询数据
    emitter.emit('tableLoadData', {})
  }
})

// 监听按钮点击
useEmitt({
  name: 'buttonClick',
  callback: (btnObj) => {
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
    return methods[btnObj.item.action](btnObj.item)
  }
})

// 获取列表数据, 业务必须监听实现, 列表并不知道怎么取数据, 需业务实现
useEmitt({
  name: 'getTableDataList',
  callback: (params: any) => {
    console.log(params, '查询数据')
    const res = methods.getTableListApi(params)
    // 向子组件传输数据, provide不行
    // provide('tableData', res)
    // 取到数据后通过事件将数据发回去
    emitter.emit('getTableDataListEnd', res)
  }
})

const title = ref('动态UI演示1-编辑页面')

const menuId = ref()

const { currentRoute } = useRouter()
// 路由的name为menuId
menuId.value = currentRoute.value.name
provide('menuId', menuId.value)

const componentObj = shallowRef()

// 获取菜单使用的组件, 这逻辑如果是动态模板, 最好放到公共逻辑, 不用每次写
onMounted(() => {
  // 获取componentName, 菜单表扩展配置中增加template: TemplateDefault
  const componentName = ref('')
  getMenuDetApi(menuId.value).then((res) => {
    componentName.value = res.data.template === null ? 'TemplateDefault' : res.data.template
    console.log(templates, 'templates')
    // console.log(templates['/src/components/UI/src/' + componentName.value + '.vue'])
    componentObj.value = defineAsyncComponent(
      templates['/src/components/UI/src/' + componentName.value + '.vue']
    )
    // vite用这个报错
    // defineAsyncComponent(
    //   () => import('@/components/UI/src/' + componentName.value + '.vue')
    // )
  })
})
</script>

<template>
  <ContentWrap :title="title">
    <!-- <TemplateDefault /> -->
    <!-- 界面使用哪个模板页面应该也是可配置的 -->
    <component :is="componentObj" />
  </ContentWrap>
</template>
