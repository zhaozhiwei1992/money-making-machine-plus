<script setup lang="ts">
import { ref } from 'vue'
import { Check, Delete, Edit, Message, Search, Star } from 'element-plus/icons-vue'
import { ElMenu, ElMenuItem, ElIcon, ElRow, ElCol, ElInput, ElSwitch } from 'element-plus'

const textarea = ref('')
const delivery = ref('')
</script>

<template>
  <ElRow>
    <ElCol :span="6">
      <ElMenu default-active="1" class="ElMenu-vertical-demo" :collapse="true">
        <ElMenuItem index="1">
          <ElIcon><icon-menu /></ElIcon>
          <template #title>回到首页</template>
        </ElMenuItem>
        <ElMenuItem index="2">
          <ElIcon><icon-menu /></ElIcon>
          <template #title>新会话</template>
        </ElMenuItem>
        <ElMenuItem index="3" disabled>
          <ElIcon><document /></ElIcon>
          <template #title>历史会话</template>
        </ElMenuItem>
        <ElMenuItem index="4">
          <ElIcon><setting /></ElIcon>
          <template #title>设置</template>
        </ElMenuItem>
      </ElMenu>
    </ElCol>
    <ElCol :span="18">
      <ElRow>
        <ElInput
          v-model="textarea"
          type="textarea"
          placeholder="想干啥就干啥,只有想不到没有干不了"
        />
      </ElRow>
      <ElRow>
        <ElCol :span="4">
          <ElSwitch v-model="delivery" />
        </ElCol>
        <ElCol :span="20">
          <el-button type="primary" :icon="Edit" circle />
          <el-button type="warning" :icon="Star" circle />
          <el-button :icon="Search" circle />
        </ElCol>
      </ElRow>
    </ElCol>
  </ElRow>
</template>

<style lang="less" scoped></style>
