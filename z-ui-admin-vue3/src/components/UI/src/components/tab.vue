<script lang="ts" setup>
import { ref, reactive, onMounted, inject } from 'vue'
import type { TabsPaneContext } from 'element-plus'
import { useEmitt } from '@/hooks/web/useEmitt'
import { ElTabs, ElTabPane } from 'element-plus'
import { ContentWrap } from '@/components/ContentWrap'
import { getTabListApi } from '@/api/ui/tab'

const { emitter } = useEmitt()

const props = defineProps({
  componentId: String,
  comRef: ref<any>
})

const menuid: string | undefined = inject('menuid')

const tabValue = ref('')

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

const tabs = ref<TabType[]>([])

// 初始化页签信息
onMounted(() => {
  // 这里通过异步接口后端获取返回
  // 获取页签信息, 填充
  console.log(menuid, '页签区菜单id')
  getTabListApi(menuid).then((res) => {
    tabs.value.push(...res.data)
  })
  // 默认选中第一个
  tabValue.value = tabs.value[0].code
  // tabs.value.push(...tabsSchema)
})
</script>
<template>
  <!-- <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
    <el-tab-pane label="User" name="first">User</el-tab-pane>
    <el-tab-pane label="Config" name="second">Config</el-tab-pane>
    <el-tab-pane label="Role" name="third">Role</el-tab-pane>
    <el-tab-pane label="Task" name="fourth">Task</el-tab-pane>
  </el-tabs> -->
  <ContentWrap>
    <el-tabs v-model="tabValue" type="card" @tab-click="handleClick" ref="comRef">
      <el-tab-pane :key="item.code" v-for="item in tabs" :label="item.name" :name="item.code" />
    </el-tabs>
  </ContentWrap>
</template>
