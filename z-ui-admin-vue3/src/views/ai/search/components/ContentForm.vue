<script setup lang="ts">
import { reactive, ref, toRefs } from 'vue'
import { DocumentCopy } from '@element-plus/icons-vue'
import { ElCard, ElRow, ElCol, ElButton } from 'element-plus'
import { ElInput, ElSwitch, ElTooltip, ElOption, ElSelect, ElMessage } from 'element-plus'
import { Edit, Search, Star } from '@element-plus/icons-vue'
import { onMounted } from 'vue'
import { getHistoryDetail } from '@/api/ai/historydetail'
import { HistoryDetailVO } from '@/api/ai/historydetail/types'
import { searchApi } from '@/api/ai/search'
import { SearchVO } from '@/api/ai/search/types'
import { dataBox } from 'js-tool-big-box'

const textarea = ref('')
const delivery = ref('')
const engine = ref(0)

// 获取历史记录
const historyContentList: HistoryDetailVO[] = reactive([])

const props = defineProps({
  historyId: {
    type: Number,
    default: 0
  }
})

const { historyId } = toRefs(props)

onMounted(async () => {
  // 查询所有历史信息
  const res = await getHistoryDetail(historyId.value)
  historyContentList.push(...res)
})

const copy = (copyText) => {
  // 复制
  dataBox.copyText(
    copyText,
    () => {
      ElMessage({
        type: 'success',
        message: '复制成功'
      })
    },
    () => {
      ElMessage({
        type: 'error',
        message: '复制异常，请尝试其他方式复制内容'
      })
    }
  )
}

const searchResult = async () => {
  // 1. 将录入内容发送后台
  const data: SearchVO = {
    content: textarea.value,
    historyId: historyId.value,
    engineId: engine.value
  }
  await searchApi(data)
  const res = await getHistoryDetail(historyId.value)
  historyContentList.length = 0
  historyContentList.push(...res)
}
</script>

<template>
  <div>
    <ElRow>
      <ul>
        <li v-for="historyContent in historyContentList" :key="historyContent.id">
          <!-- direct: 0:请求(靠右) 1:回复(靠左) -->
          <ElRow v-if="historyContent.direct == '0'" class="content-row">
            <ElCol :span="21">
              <ElCard class="content-card-0">
                <p>{{ historyContent.content }}</p>
              </ElCard>
            </ElCol>
            <ElCol :span="3" />
          </ElRow>
          <ElRow v-if="historyContent.direct == '1'" class="content-row">
            <ElCol :span="3" />
            <ElCol :span="21">
              <ElCard class="content-card-1">
                <p>{{ historyContent.content }}</p>
                <template #footer>
                  <ElButton :icon="DocumentCopy" @click="copy(historyContent.content)">
                    复制
                  </ElButton>
                </template>
              </ElCard>
            </ElCol>
          </ElRow>
        </li>
      </ul>
    </ElRow>
    <ElRow>
      <!-- <NormalForm :historyId="historyId" /> -->
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
    </ElRow>
  </div>
</template>

<style lang="less" scoped>
.content-row {
  margin-bottom: 10px;
}

.content-card-0 {
  background-color: #f5f7fa;
  width: 45vw;
}

.content-card-1 {
  // 用蓝色背景填充
  background-color: deepskyblue;
  width: 45vw;
}

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
