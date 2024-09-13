<script setup lang="ts">
import { reactive, ref, unref } from 'vue'
import { ElTimeline, ElTimelineItem, ElCard, ElRow, ElCol, ElButton } from 'element-plus'
import { Delete, Edit } from '@element-plus/icons-vue'

// 获取历史记录
const historyList = ref([
  { id: 1, timestamp: '2018-04-12 20:46:23', content: 'Tom committed 2018/4/12 20:46' },
  { id: 2, timestamp: '2018-04-12 20:46:23', content: 'Tom committed 2018/4/12 20:46' },
  { id: 3, timestamp: '2018-04-12 20:46:23', content: 'Tom committed 2018/4/12 20:46' },
  { id: 4, timestamp: '2018-04-12 20:46:23', content: 'Tom committed 2018/4/12 20:46' },
  { id: 5, timestamp: '2018-04-12 20:46:23', content: 'Tom committed 2018/4/12 20:46' },
  { id: 6, timestamp: '2018-04-12 20:46:23', content: 'Tom committed 2018/4/12 20:46' }
])

// 子传父
const emit = defineEmits(['historyClick'])
const historyClick = (row: any) => {
  emit('historyClick', row)
}
</script>

<template>
  <div>
    <ElTimeline style="max-width: 600px">
      <ElTimelineItem
        v-for="history in historyList"
        :timestamp="history.timestamp"
        :key="history.id"
        placement="top"
      >
        <ElCard>
          <ElRow>
            <ElCol :span="21">
              <p @click="historyClick(history.id)">{{ history.content }}</p>
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
