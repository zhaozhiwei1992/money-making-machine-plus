<script setup lang="ts">
import { ref } from 'vue'
import { Edit, Search, Star } from '@element-plus/icons-vue'
import {
  ElRow,
  ElCol,
  ElInput,
  ElSwitch,
  ElButton,
  ElTooltip,
  ElOption,
  ElSelect
} from 'element-plus'

const textarea = ref('')
const delivery = ref('')
const engine = ref('')

const searchResult = () => {
  // 1. 将录入内容发送后台
  // 2. 每组数据存在一个id
}
const options = [
  {
    value: 'Option1',
    label: 'Option1'
  },
  {
    value: 'Option2',
    label: 'Option2'
  },
  {
    value: 'Option3',
    label: 'Option3'
  },
  {
    value: 'Option4',
    label: 'Option4'
  },
  {
    value: 'Option5',
    label: 'Option5'
  }
]
</script>

<template>
  <div class="main">
    <ElRow>
      <ElInput
        class="inputDeep"
        v-model="textarea"
        type="textarea"
        :rows="4"
        placeholder="想干啥就干啥,只有想不到没有干不了"
      />
    </ElRow>
    <ElRow class="buttonRow" justify="end">
      <ElCol :span="10" class="buttonRow_ElCol_1">
        <ElSwitch v-model="delivery" /> <span>联网搜索</span>
        <ElSelect v-model="engine" placeholder="Select" size="small" style="width: 240px">
          <ElOption
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </ElSelect>
      </ElCol>
      <ElCol :span="10" />
      <ElCol :span="3" class="buttonRow_ElCol_2">
        <ElTooltip
          content="<span><strong>输入常用语标题</strong></span>"
          raw-content
          placement="top"
        >
          <ElButton :icon="Edit" circle />
        </ElTooltip>
        <ElTooltip
          content="<span><strong>支持上传文件,每个不超过50M</strong></span>"
          raw-content
          placement="top"
        >
          <ElButton :icon="Star" circle />
        </ElTooltip>
        <ElTooltip
          content="<span><strong>请输入你的问题</strong></span>"
          raw-content
          placement="top"
          v-if="textarea === ''"
        >
          <ElButton :icon="Search" circle />
        </ElTooltip>
        <ElButton v-if="textarea != ''" :icon="Search" @click="searchResult" circle />
      </ElCol>
    </ElRow>
  </div>
</template>

<style lang="less" scoped>
.inputDeep {
  :deep(.el-textarea__inner) {
    box-shadow: 0 0 0 0px var(--el-input-border-color, var(--el-border-color)) inset;
    resize: none;
    cursor: default;
  }
}

.buttonRow {
  background-color: #fff;
}

.buttonRow_ElCol_1 {
  margin-right: 1em;
}

.buttonRow_ElCol_2 {
  margin-right: 1em;
}

.main {
  width: 55vw;
}
</style>
