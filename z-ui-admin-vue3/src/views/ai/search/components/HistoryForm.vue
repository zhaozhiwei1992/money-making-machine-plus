<script setup lang="ts">
import { onMounted, reactive, ref, unref } from 'vue'
import { ElTimeline, ElTimelineItem, ElCard, ElRow, ElCol, ElButton } from 'element-plus'
import { Delete, Edit } from '@element-plus/icons-vue'
import { getHistories } from '@/api/ai/history'
import { HistoryVO } from '@/api/ai/history/types'

// 获取历史记录
const historyList: HistoryVO[] = reactive([])

onMounted(async () => {
  // 查询所有历史信息
  const res = await getHistories()
  historyList.push(...res)
})

// 子传父
const emit = defineEmits(['historyClick'])
const historyClick = (historyId: number) => {
  emit('historyClick', historyId)
}
</script>

<template>
  <div>
    <ElTimeline style="max-width: 600px">
      <ElTimelineItem
        v-for="history in historyList"
        :timestamp="history.lastModifiedDate"
        :key="history.id"
        placement="top"
      >
        <ElCard>
          <ElRow>
            <ElCol :span="21">
              <p @click="historyClick(history.id)">{{ history.remark }}</p>
            </ElCol>
            <ElCol :span="3">
              <ElButton :icon="Edit" text circle size="small" />
              <ElButton :icon="Delete" text circle size="small" />
            </ElCol>
          </ElRow>
        </ElCard>
      </ElTimelineItem>
    </ElTimeline>
  </div>
</template>

<style lang="less" scoped></style>
