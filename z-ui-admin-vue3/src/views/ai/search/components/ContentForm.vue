<script setup lang="ts">
import { reactive, ref, toRefs } from 'vue'
import { DocumentCopy } from '@element-plus/icons-vue'
import { ElCard, ElRow, ElCol, ElButton } from 'element-plus'
import NormalForm from './NormalForm.vue'
import { onMounted } from 'vue'
import { getHistoryDetail } from '@/api/ai/historydetail'
import { HistoryDetailVO } from '@/api/ai/historydetail/types'

// 获取历史记录
const historyContentList: HistoryDetailVO[] = reactive([])

const props = defineProps({
  historyId: {
    type: Number,
    default: 0
  }
})

const { historyId } = toRefs(props)

const hisId = ref(historyId.value)

onMounted(async () => {
  // 查询所有历史信息
  const res = await getHistoryDetail(historyId.value)
  historyContentList.push(...res)
})

const copy = () => {
  // 复制
}
</script>

<template>
  <div>
    <ElRow>
      <ul>
        <li v-for="historyContent in historyContentList" :key="historyContent.id">
          <!-- direct: 0:请求(靠右) 1:回复(靠坐) -->
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
                  <ElButton :icon="DocumentCopy" @click="copy"> 复制 </ElButton>
                </template>
              </ElCard>
            </ElCol>
          </ElRow>
        </li>
      </ul>
    </ElRow>
    <ElRow>
      <NormalForm :historyId="hisId" :fromContent="true" />
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
</style>
