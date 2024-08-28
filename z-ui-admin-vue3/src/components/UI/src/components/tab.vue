<script lang="ts" setup>
import { ref, onMounted, inject, reactive } from 'vue'
import type { TabsPaneContext } from 'element-plus'
import { ElTabs, ElTabPane } from 'element-plus'
import { useEmitt } from '@/hooks/web/useEmitt'
import { ContentWrap } from '@/components/ContentWrap'
import { getTabListByMenuApi } from '@/api/ui/tab'

const { emitter } = useEmitt()

const props = defineProps({
  componentId: String
})

const handleClick = (tab: TabsPaneContext, event: Event) => {
  console.log(tab, event)
  // 一个页面页签只有一个, 如果多个需要特殊实现
  // 触发发页签点击事件, 传递点击的页签编码
  emitter.emit('tabClick', { componentId: props.componentId, tabCode: tab.paneName })
}

interface TabType {
  code: string
  name: string
}

// 模拟后端返回的tab信息
// const tabsSchema = reactive<TabType[]>([
//   {
//     code: 'first',
//     name: '待审核'
//   },
//   {
//     code: 'second',
//     name: '已审核'
//   }
// ])

const tabValue = ref('')

const tabs = ref<TabType[]>([])

// 初始化页签信息
onMounted(async () => {
  // 这里通过异步接口后端获取返回
  // 获取页签信息, 填充
  const menuId: string | undefined = inject('menuId')
  console.log(menuId, '页签区菜单id')
  const res = await getTabListByMenuApi(menuId)
  tabs.value.push(...res)
  // 默认选中第一个
  tabValue.value = tabs.value[0].code
  // tabs.value.push(...tabsSchema)
})
</script>
<template>
  <ContentWrap>
    <ElTabs v-model="tabValue" type="card" @tab-click="handleClick">
      <ElTabPane :key="item.code" v-for="item in tabs" :label="item.name" :name="item.code" />
    </ElTabs>
  </ContentWrap>
</template>
