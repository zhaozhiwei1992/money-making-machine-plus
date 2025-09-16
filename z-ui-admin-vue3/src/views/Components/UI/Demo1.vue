<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { onMounted, ref, defineAsyncComponent, provide, shallowRef } from 'vue'
import TemplateDefault from '@/components/UI/src/TemplateDefault.vue'
import { useEmitt } from '@/hooks/web/useEmitt'
import * as methods from './demo1'
import { useRouter } from 'vue-router'
import { getMenuDetApi } from '@/api/system/menu'

// 所有的模板
const templates: any = import.meta.glob('@/components/UI/src/*.vue')

const { emitter } = useEmitt()

const defaultTemplateRef = ref()

// 获取内部组件
const getComponent = () => {
  return defaultTemplateRef.value?.componentRefs
}

const getEditform1Data = async () => {
  const data = await getComponent().editform.getFormData()
  console.log('获取表单数据editform1', data.name)
  return data
}

// 监听按钮点击
emitter.on('buttonClick', async (btnObj: any) => {
  console.log(btnObj, '点击按钮对象')
  // 根据按钮编码, 或者config中特殊参数, 触发各种事件
  // vue2 写法
  // this.$bus.$off("buttonClick").$on("buttonClick", (item) => {
  //   return this[item.click](item)
  // });
  //https://segmentfault.com/q/1010000042472899
  if (btnObj.item.action === 'save') {
    // 获取表单数据
    const data = await getEditform1Data()
    methods.save(data)
  } else {
    methods[btnObj.item.action](btnObj.item)
  }
})

// 获取列表数据, 业务必须监听实现, 列表并不知道怎么取数据, 需业务实现
emitter.on('getTableDataList', (params: any) => {
  console.log(params, '查询数据')
  const res = methods.getTableListApi(params)
  // 向子组件传输数据, provide不行
  // provide('tableData', res)
  // 取到数据后通过事件将数据发回去
  emitter.emit('getTableDataListEnd', res)
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
    componentName.value = res.template === null ? 'TemplateDefault' : res.template
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
    <!-- <component :is="componentObj" /> -->
    <TemplateDefault ref="defaultTemplateRef" />
  </ContentWrap>
</template>
