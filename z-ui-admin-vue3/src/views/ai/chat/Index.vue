<script setup lang="ts">
import { ChatDotRound, Collection, Setting } from '@element-plus/icons-vue'
import { ElCol, ElContainer, ElIcon, ElMain, ElMenu, ElMenuItem, ElRow } from 'element-plus'
import { onMounted, ref } from 'vue'
import ContentForm from './components/ContentForm.vue'
import HistoryForm from './components/HistoryForm.vue'
import NormalForm from './components/NormalForm.vue'

const showIndex = ref(1)

const alignItems = ref('center')

onMounted(() => {
  // console.log(router)
  // 动态添加路由
  // dynamicRoute.forEach((item) => {
  // router.removeRoute(item.name)
  // router.addRoute('501', item)
  // })
})

const hisId: any = ref(0)

const showContent = (historyId: number) => {
  console.log(historyId)
  hisId.value = historyId
  console.log('hisID:', hisId.value)
  showIndex.value = 3
}
</script>

<template>
  <ElContainer class="main">
    <ElMain>
      <ElRow>
        <ElCol :span="4" class="menu">
          <ElMenu default-active="1" class="ElMenu-vertical-demo" :collapse="true">
            <ElMenuItem index="1" @click=";(showIndex = 1), (alignItems = 'center')">
              <!-- <router-link to="/ai/search/normal"> -->
              <ElIcon><ChatDotRound /></ElIcon>
              <template #title>新会话</template>
              <!-- </router-link> -->
            </ElMenuItem>
            <ElMenuItem index="2" @click=";(showIndex = 2), (alignItems = 'start')">
              <!-- <router-link to="/ai/search/history">
              </router-link> -->
              <ElIcon><Collection /></ElIcon>
              <template #title>历史会话</template>
            </ElMenuItem>
            <ElMenuItem index="3">
              <ElIcon><Setting /></ElIcon>
              <template #title>设置</template>
            </ElMenuItem>
          </ElMenu>
        </ElCol>
        <ElCol :span="16" class="content">
          <NormalForm v-if="showIndex == 1" :historyId="0" />
          <HistoryForm v-if="showIndex == 2" @history-click="showContent" />
          <ContentForm v-if="showIndex == 3" :historyId="hisId" />
        </ElCol>
        <ElCol :span="4" />
      </ElRow>
    </ElMain>
  </ElContainer>
</template>

<style lang="less" scoped>
.menu {
  height: 80vh;
  // direction: vertical;
  display: flex;
  align-items: center;
}
// .content {
//   height: 80vh;
//   display: flex;
//   align-items: v-bind(alignItems);
// }
</style>
