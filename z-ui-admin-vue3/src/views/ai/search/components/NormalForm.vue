<script setup lang="ts">
import { reactive, ref, toRefs } from 'vue'
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
import { onMounted } from 'vue'
import { getEngineSelect } from '@/api/ai/engine'
import { ComponentOptions } from '@/types/components'
import ContentForm from './ContentForm.vue'
import { SearchVO } from '@/api/ai/search/types'

const textarea = ref('')
const delivery = ref('')
const engine = ref(0)

const props = defineProps({
  historyId: {
    type: Number,
    default: 0
  }
})

const { historyId } = toRefs(props)
const hisId: any = ref(historyId.value)
const searchFlag = ref(false)
const searchVO = ref<SearchVO>()

const searchResult = async () => {
  // 1. 将录入内容发送后台
  searchVO.value = {
    content: textarea.value,
    historyId: hisId.value,
    engineId: engine.value
  }
  // 2. 切换到ContentForm上, 并给出historyId每组数据存在一个id
  searchFlag.value = true
}

const options: ComponentOptions[] = reactive([])

onMounted(async () => {
  // 初始化引擎数据
  const res = await getEngineSelect()
  options.push(...res)
})
</script>

<template>
  <div class="main" v-if="searchFlag == false">
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
  <div class="main" v-if="searchFlag == true">
    <!-- 只有保存才应该触发这个标签，显示 -->
    <ContentForm :historyId="hisId" :search-vo="searchVO" />
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
